(ns clj-blog.core
  (:require
   [clj-blog.handler :as handler]
   [clj-blog.nrepl :as nrepl]
   [luminus.http-server :as http]
   [luminus-migrations.core :as migrations]
   [clj-blog.config :refer [env]]
   [clojure.tools.cli :refer [parse-opts]]
   [clojure.tools.logging :as log]
   [mount.tools.graph :as graph]
   [mount.core :as mount])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (log/error {:what :uncaught-exception
                 :exception ex
                 :where (str "Uncaught exception on" (.getName thread))}))))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

;mount/defstate allows us to declare something
;; which can be started and stopped
;; 
; INFO  luminus.http-server - starting HTTP server on port 3010
; INFO  clj-blog.nrepl - starting nREPL server on port NUMBER 7777
; INFO  clj-blog.core - #'clj-blog.db.core/*db* started
; INFO  clj-blog.core - #'clj-blog.handler/init-app started
; INFO  clj-blog.core - #'clj-blog.handler/app-routes started
; INFO  clj-blog.core - #'clj-blog.core/http-server started
; INFO  clj-blog.core - #'clj-blog.core/repl-server started
;
;; these are fired up on run

;; :on-reload :noop means this will not be reloaded when namespaces are refreshed

(comment
  (graph/states-with-deps) 
  
  )


(mount/defstate ^{:on-reload :noop} http-server
  ;mount/start is tight bc it automatically determines the order in which
  ;; resources need to be initialized
  :start
  (http/start
   (-> env
       (assoc  :handler (handler/app)) ;handler/app handles incoming requests
       (update :port #(or (-> env :options :port) %))
       (select-keys [:handler :host :port])))
  :stop
  (http/stop http-server))

(mount/defstate ^{:on-reload :noop} repl-server
  :start
  (when (env :nrepl-port)
    (nrepl/start {:bind (env :nrepl-bind)
                  :port (env :nrepl-port)}))
  :stop
  (when repl-server
    (nrepl/stop repl-server)))

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (println args)
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (mount/start #'clj-blog.config/env)
  ;env has a TON of keys....a giant map
  (def ricky args)
  ;args is what is passed in on calling lein run
  (cond
    (nil? (:database-url env))
    (do
      (log/error "Database configuration not found, :database-url environment variable must be set before running")
      (System/exit 1))
    (some #{"init"} args)
    (do
      (migrations/init (select-keys env [:database-url :init-script]))
      (System/exit 0))
    (migrations/migration? args)
    (do
      (migrations/migrate args (select-keys env [:database-url]))
      (System/exit 0))
    :else
    ;I think it usually lands here for real
    (doall
     (def x "lol!") ;CONFIRMED
     (start-app args))))
