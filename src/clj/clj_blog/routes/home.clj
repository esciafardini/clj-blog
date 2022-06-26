(ns clj-blog.routes.home
  (:require
   [clj-blog.layout :as layout]
   [clj-blog.messages :as msg]
   [clj-blog.middleware :as middleware]
   [ring.util.http-response :as response]))

;; Application routes represent URIs that a client can call to perform an action or retrieve some data
;;  specific workflows will group all routes into singular namespaces (like home)

;; When adding a new workflow, we will create a corresponding routes namespace
;; and then we will add these routes to our router in the handler namespace
(defn home-page [{:keys [] :as request}]
  ;;;;;;;;;;;;;;;request, template, params 
  (layout/render request "home.html"))

(defn about-page [request]
  (layout/render request "about.html"))

;;Pages are defined by creating routes
;; One route can respond to GET requests and return HTML to be rendered by the browser
;; Another route can respond to POST requests and make submissions to the DB

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/about" {:get about-page}]])

