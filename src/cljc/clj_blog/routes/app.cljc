(ns clj-blog.routes.app
  (:require
   #?@(:clj [[clj-blog.layout :as layout]
             [clj-blog.middleware :as middleware]]
       :cljs [[clj-blog.views.about :as about]
              [clj-blog.views.blog-post :as blog-post]
              [clj-blog.views.blog-list :as blog-list]
              [clj-blog.views.blog-themes :as themes]
              [clj-blog.views.messages :as messages]
              [clj-blog.views.resources :as resources]
              [clj-blog.views.test :as test-view]
              [re-frame.core :as rf]])))

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
                    (rf/dispatch [:blog-post/select-blog-post 1])
                    (js/console.log "Entering home page"))
           :stop  (fn [] (js/console.log "Leaving home page"))}]
         :view #'blog-post/blog-post}))]
   ["/blog-list"
    (merge
     {:name ::blog-list}
     #?(:cljs
        {:controllers
         [{:start (fn []
                    (rf/dispatch [:blog-list/load])
                    (js/console.log "Entering Blog List"))
           :stop  (fn [] (js/console.log "Leaving Blog List"))}]
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
   ["/resources"
    (merge
     {:name ::resources}
     #?(:cljs {:controllers
               [{:start (fn [] (js/console.log "Entering resources"))
                 :stop  (fn [] (js/console.log "Leaving resources"))}]
               :view #'resources/resources}))]
   ["/test"
    (merge
     {:name ::testing}
     #?(:cljs {:controllers
               [{:start (fn [] (js/console.log "Entering test page"))
                 :stop  (fn [] (js/console.log "Leaving test page"))}]
               :view #'test-view/test-view}))]
   ["/about"
    (merge
     {:name ::about}
     #?(:cljs {:controllers
               [{:start (fn [] (js/console.log "Entering about page"))
                 :stop  (fn [] (js/console.log "Leaving about page"))}]
               :view #'about/about}))]
   ["/the-chat"
    (merge
     {:name ::the-chat}
     #?(:cljs {:controllers
               [{:start (fn [] (do
                                 (rf/dispatch [:messages/load])
                                 (js/console.log "Entering messages page")))
                 :stop  (fn [] (js/console.log "Leaving messages page"))}]
               :view #'messages/messages}))]
   ["/themes"
    (merge
     {:name ::themes}
     #?(:cljs {:controllers
               [{:start (fn [] (js/console.log "Entering themes page"))
                 :stop  (fn [] (js/console.log "Leaving themes page"))}]
               :view #'themes/color-palette}))]])
