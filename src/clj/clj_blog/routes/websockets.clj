(ns clj-blog.routes.websockets
  (:require [clojure.tools.logging :as log]
            [clj-blog.messages :as msg]
            [clj-blog.middleware :as middleware]
            [mount.core :refer [defstate]]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]))

;;;WEB SOCKETS
;;A Novel Concept for YOURZ TRULEY
;We will open a web socket connection when the page loads
;server will notify active client anytime a new message is created
;We shall utilize http-kit

;;a websocket connection remains open while HTTP requests are closed after the req/res

;; We initialize Sente with this fn...
;; Accepts server adapter and a map of initialization options
;; 
(defstate socket ;;one of to defstates for socket
  :start (sente/make-channel-socket! ;;returns a map that contans a number of variables that were initialized
          (get-sch-adapter)
          {:user-id-fn (fn [ring-req]
                         (get-in ring-req [:params :client-id]))}))

(defn send!
  "Accessing send-fn from our socket map w/ helper function `send!`"
  [uid message]
  (println "Sending message: " message)
  ((:send-fn socket) uid message))

;;multi-method to accept mutlitple types of messages
(defmulti handle-message (fn [{:keys [id]}]
                           id))

;;handle-message multimethods return MAPs
(defmethod handle-message :default
  [{:keys [id]}]
  (log/debug "Received unrecognized websocket event type: " id)
  {:error  (str "Unrecognized web socket event type: " (pr-str id))
   :id     id})

(defmethod handle-message :message/create!
  [{:keys [?data uid] :as _message}] ;;event messages have many useful keys (see pg 147)
  (let [response (try
                   (msg/save-message! ?data)
                   (assoc ?data :timestamp (java.util.Date.))
                   (catch Exception e
                     (let [{id      :guestbook/error-id
                            errors  :errors} (ex-data e)]
                       (case id
                         :validation
                         {:errors errors}
                         ;;else
                         {:errors
                          {:server-error ["Failed to save message!!!!"]}}))))]
    (if (:errors response)
      (do
        (log/debug "Failed to save message: " ?data)
        response)
      (do
        (doseq [uid (:any @(:connected-uids socket))]
          (send! uid [:message/add response]))
        {:success true}))))

(defn receive-message!
  "Any logic that applies to ALL events go here"
  [{:keys [id ?reply-fn] ;;?reply-fn handles client-specific concerns
    :as message}]
  (log/debug "Got message with id: " id)
  (let [reply-fn (or ?reply-fn (fn [_]))]
    (when-some [response (handle-message message)]
      (reply-fn response))))

(defstate channel-router
  :start (sente/start-chsk-router!
          (:ch-recv socket)
          #'receive-message!)
  :stop (when-let [stop-fn channel-router]
          (stop-fn)))

(defn websocket-routes
  "Our router depends on the initialization of our socket
   defstate via Mount knows to start our socket before starting our router"
  []
  ["/ws"
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]
    :get (:ajax-get-or-ws-handshake-fn socket)
    :post (:ajax-post-fn socket)}])
