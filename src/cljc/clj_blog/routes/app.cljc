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
    :front-end-routes/navigated
    (fn [db [_ new-match]]
      (.log js/console new-match)
      (assoc db :current-route new-match))))

#?(:cljs
   (rf/reg-sub
    :front-end-routes/current-route
    (fn [db]
      (:current-route db))))

#?(:clj
   (defn home-page [request]
     (layout/render
      request
      "home.html")))

(defn app-routes []
  [""
   #?(:clj {:middleware [middleware/wrap-csrf]
            :get home-page})
   ;
   ;this is required for the initial http request to web server
   ["/"
    (merge
     {:name ::home}
     #?(:cljs
        {:view #'home/home}))]])

#?(:cljs
   (def front-end-routes
     ["/"
      [""
       {:name      ::home
        :view      #'home/home
        :link-text "Home"
        :controllers
        [{;; Do whatever initialization needed for home page
       ;; I.e (re-frame/dispatch [::events/load-something-with-ajax])
          :start (fn []
                   (rf/dispatch [:blog-posts/load])
                   (js/console.log "Entering home page"))
       ;; teardown can be done here.
          :stop  (fn [] (js/console.log "Leaving home page"))}]}]
      ["about"
       {:name ::about
        :view #'about/about
        :controllers
        [{:start (fn [] (js/console.log "Entering about page"))
          :stop  (fn [] (js/console.log "Leaving about page"))}]}]
      ["blog-list"
       {:name ::blog-list
        :view #'blog-list/blog-list
        :link-text "Blog List"
        :controllers
        [{:start (fn [] (js/console.log "Entering bloglist"))
          :stop  (fn [] (js/console.log "Leaving bloglist"))}]}]
      ["blog-posts/:id"
       {:name ::blog-post
        :view #'blog-post/blog-post
        :link-text "Blog Post"
        :controllers
        [{:parameters {:path [:id]}
          :start (fn [params] (do
                                (rf/dispatch [:blog-post/load])
                                (rf/dispatch [:blog-post/select-blog-post (-> params :path :id)])))
          :stop (fn [_params] (rf/dispatch [:blog-post/select-blog-post nil]))}]}]]))
