(ns clj-blog.handler
  (:require
   [clj-blog.env :refer [defaults]]
   [clj-blog.layout :refer [error-page]]
   [clj-blog.middleware :as middleware]
   [clj-blog.routes.home :refer [home-routes]]
   [clj-blog.routes.services :refer [service-routes]]
   [clj-blog.routes.websockets :refer [websocket-routes]]
   [mount.core :as mount]
   [reitit.ring :as ring]
   [reitit.ring.middleware.dev :as dev]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.webjars :refer [wrap-webjars]]))

;This namespace is responsible for transforming all route declarations into a single ring ring-handler
;; wraps them with shared middleware

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

;;any additional routes needed within app will be added here
(mount/defstate app-routes
  :start
  (middleware/wrap-base
   (ring/ring-handler
    ;`ring/router` aggregates routes for handling all requests to our app into a reitit router
    (ring/router
     [(home-routes)
      (service-routes)
      (websocket-routes)]
     #_{:reitit.middleware/transform dev/print-request-diffs}) ;un-comment to log reitit middleware
    (ring/routes
      ;default ring handler:
     (ring/create-resource-handler
      {:path "/"})
     (wrap-content-type
      (wrap-webjars (constantly nil)))
     (ring/create-default-handler
      {:not-found
       (constantly (error-page {:status 404, :title "404 - Page not found"}))
       :method-not-allowed
       (constantly (error-page {:status 405, :title "405 - Not allowed"}))
       :not-acceptable
       (constantly (error-page {:status 406, :title "406 - Not acceptable"}))})))))

(defn app []
  (middleware/wrap-base #'app-routes))
