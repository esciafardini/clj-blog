(ns clj-blog.routes.home
  (:require
   [clj-blog.layout :as layout]
   [clj-blog.middleware :as middleware]))

;; Application routes represent URIs that a client can call to perform an action or retrieve some data
;;  specific workflows will group all routes into singular namespaces (like home)

;; When adding a new workflow, we will create a corresponding routes namespace
;; and then we will add these routes to our router in the handler namespace
(defn home-page [{:keys [] :as request}]
  ;;;;;;;;;;;;;;;request, template, params 
  (layout/render request "home.html"))

(defn about-page [request]
  (layout/render request "about.html"))

(defn messages-page [request]
  (layout/render request "messages.html"))

;;Pages are defined by creating routes
;; One route can respond to GET requests and return HTML to be rendered by the browser
;; Another route can respond to POST requests and make submissions to the DB

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/about" {:get about-page}]
   ["/messages"] {:get messages-page}])

