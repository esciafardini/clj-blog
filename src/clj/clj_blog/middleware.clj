(ns clj-blog.middleware
  (:require
   [clj-blog.env :refer [defaults]]
   [clojure.tools.logging :as log]
   [clj-blog.layout :refer [error-page]]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [clj-blog.middleware.formats :as formats]
   [muuntaja.middleware :refer [wrap-format wrap-params]]
   [ring-ttl-session.core :refer [ttl-memory-store]]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

;This namespace is reserved for any wrapper functions that are used to modify the requests and responses
;a central place for handling common tasks such as CSRF protection

(defn wrap-internal-error
  "Middleware Fns in Clojure are higher order Functions.
  wrap-internal-error receives a handler function
  handler functions take requests
  wrap-internal-error returns a function that takes a request (like the handler) ->
  THEN calls the handler on the request
  augmented with a try-catch.
  This is the common higher-order function form of middleware functions.
  NOTICE: if no error is thrown, the handler is called on the request as normal"
  [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t (.getMessage t))
        (error-page {:status 500
                     :title "Something very bad has happened!"
                     :message "We've dispatched a team of highly trained gnomes to take care of the problem."})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
   handler
   {:error-response
    (error-page
     {:status 403
      :title "Invalid anti-forgery token"})}))

(defn wrap-formats
  "The pattern is visible here as well.
  The original handler is called on the request, but differently under certain conditions"
  [handler]
  (let [wrapped (-> handler wrap-params (wrap-format formats/instance))]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

;wrap-base ties all the common middleware together in the order of dependency
;also adds ring defaults
(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      (wrap-defaults
       (-> site-defaults
           (assoc-in [:security :anti-forgery] false)
           (assoc-in [:session :store] (ttl-memory-store (* 60 30)))
           ;Enable HTTPS redirect...
           ;TODO - look into these settings and what they mean:
           (assoc-in [:security :hsts] true)
           (assoc-in [:security :ssl-redirect] true)))
      wrap-internal-error))
