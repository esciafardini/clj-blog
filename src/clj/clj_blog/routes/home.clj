(ns clj-blog.routes.home
  (:require
   [clj-blog.db.core :as db]
   [clj-blog.layout :as layout]
   [clj-blog.middleware :as middleware]
   [clj-blog.validation :refer [validate-message]]
   [ring.util.http-response :as response]
   [ring.util.response]))

;; Application routes represent URIs that a client can call to perform an action or retrieve some data
;;  specific workflows will group all routes into singular namespaces (like home)

;; When adding a new workflow, we will create a corresponding routes namespace
;; and then we will add these routes to our router in the handler namespace
(defn home-page [{:keys [] :as request}]
  ;;;;;;;;;;;;;;;request, template, params 
  (layout/render request "home.html"))

(defn about-page [request]
  (layout/render request "about.html"))

(defn save-message! [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (response/bad-request {:errors errors})
    (try
      (db/save-message! params)
      (response/ok {:status :ok})
      (catch Exception _e
        (response/internal-server-error
         {:errors {:server-error ["Failed to save message!"]}})))))

(defn message-list [_]
  (response/ok {:messages (vec (db/get-messages))}))

;;Pages are defined by creating routes
;; One route can respond to GET requests and return HTML to be rendered by the browser
;; Another route can respond to POST requests and make submissions to the DB

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]                 ;displaying a list of messages and a form
   ["/about" {:get about-page}]           ;displays an images and text
   ["/message" {:post save-message!}]     ;responsible for handling the creation of new messages
   ["/messages" {:get message-list}]])    ;get the messages

