(ns clj-blog.routes.app
  (:require
   #?@(:clj [[clj-blog.layout :as layout]
             [clj-blog.middleware :as middleware]]
       :cljs [[re-frame.core :as rf]
              [clj-blog.views.blog-post :as blog-post]
              [clj-blog.views.blog-list :as blog-list]
              [clj-blog.views.about :as about]
              [clj-blog.views.home :as home]])))

#?(:cljs
   (rf/reg-event-db
    :router/navigated
    (fn [db [_ new-match]]
      (.log js/console new-match)
      (assoc db :router/current-route new-match))))

#?(:cljs
   (rf/reg-sub
    :router/current-route
    (fn [db]
      (:router/current-route db))))

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
        {:controllers
         [{:start (fn []
                    (rf/dispatch [:blog-posts/load])
                    (js/console.log "Entering home page"))
           :stop  (fn [] (js/console.log "Leaving home page"))}]
         :view #'home/home}))]
   ["/about"
    (merge
     {:name ::about}
     #?(:cljs {:controllers
               [{:start (fn [] (js/console.log "Entering about page"))
                 :stop  (fn [] (js/console.log "Leaving about page"))}]
               :view #'about/about}))]
   ["/blog-list"
    (merge
     {:name ::blog-list}
     #?(:cljs {:controllers
               [{:start (fn [] (do
                                 (rf/dispatch [:blog-posts/load])
                                 (js/console.log "Entering bloglist")))
                 :stop  (fn [] (js/console.log "Leaving bloglist"))}]
               :view #'blog-list/blog-list}))]
   ["blog-posts/:id"
    (merge
     {:name ::blog-post}
     #?(:cljs {:controllers
               [{:parameters {:path [:id]}
                 :start (fn [params] (do
                                       (rf/dispatch [:blog-posts/load])
                                       (.log js/console (str "Going to Blog Post: " (-> params :path :id)))))
                 :stop (fn [_params] (.log js/console "Leaving Blog Post"))}]
               :view #'blog-post/blog-post}))]])
