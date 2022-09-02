(ns clj-blog.routes.app
  (:require
   #?@(:clj [[clj-blog.layout :as layout]
             [clj-blog.middleware :as middleware]]
       :cljs [[clj-blog.views.home :as home]])))

#?(:clj
   (defn home-page [request]
     (layout/render
      request
      "home.html")))

(defn app-routes []
  [""
   #?(:clj {:middleware [middleware/wrap-csrf]
            :get home-page})
   ["/"
    (merge
     {:name ::home}
     #?(:cljs
        {:view #'home/home}))]])
