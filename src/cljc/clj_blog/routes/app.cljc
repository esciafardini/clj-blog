(ns clj-blog.routes.app
  (:require
   #?@(:clj [[clj-blog.layout :as layout]
             [clj-blog.middleware :as middleware]]
       :cljs [[re-frame.core :as rf]
              [clj-blog.views.blog-post :as blog-post]
              [clj-blog.views.blog-list :as blog-list]
              [clj-blog.blog-posts.blog-themes :as themes] ;TODO move to a view component
              [clj-blog.views.about :as about]
              [clj-blog.views.resources :as resources]
              [clj-blog.views.home :as home]])))

#?(:cljs
   (rf/reg-event-db
    :router/navigated
    (fn [db [_ new-match]]
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
   ["/resources"
    (merge
     {:name ::resources}
     #?(:cljs {:controllers
               [{:start (fn [] (js/console.log "Entering resources"))
                 :stop  (fn [] (js/console.log "Leaving resources"))}]
               :view #'resources/resources}))]
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
   ["/blog-posts/:id"
    (merge
     {:name ::blog-posts}
     #?(:cljs {:controllers
               [{:parameters {:path [:id]}
                 :start (fn [{{:keys [id]} :path}]
                          (do
                            (rf/dispatch [:blog-post/select-blog-post id])
                            (.log js/console (str "Going to Blog Post: " id))))
                 :stop (fn [_params]
                         (do
                           (rf/dispatch [:blog-post/set nil])
                           (.log js/console "Leaving Blog Post")))}]
               :view #'blog-post/blog-post}))]
   ["/themes"
    (merge
     {:name ::themes}
     #?(:cljs {:controllers
               [{:start (fn [] (js/console.log "Entering themes page"))
                 :stop  (fn [] (js/console.log "Leaving themes page"))}]
               :view #'themes/color-palette}))]])
