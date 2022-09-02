(ns clj-blog.core
  (:require
   [clj-blog.views.blog-post :as blog-post]
   [clj-blog.views.home :as home]
   [clj-blog.views.about :as about]
   [mount.core :as mount]
   [re-frame.core :as rf]
   [reagent.core :as r]
   [reagent.dom :as dom]
   [reitit.coercion.spec :as rss]
   [reitit.core :as reitit]
   [reitit.frontend :as reitit-fe]
   [reitit.frontend.controllers :as reitit-c]
   [reitit.frontend.easy :as reitit-fee]))


(rf/reg-event-db
 :app/initialize
 (fn [_ _]
   {:current-route nil
    :blog-posts/current-blog-post nil
    :dispatch [:blog-posts/load]}))

(rf/reg-event-db
 :blog-posts/select-blog-post
 (fn [db [_ blog-id]]
   (assoc db :blog-posts/current-blog-post blog-id)))

(rf/reg-event-db
 :front-end-routes/navigated
 (fn [db [_ new-match]]
   (assoc db :current-route new-match)))

(rf/reg-sub
 :front-end-routes/current-route
 (fn [db]
   (:current-route db)))

(rf/reg-sub
 :blog-posts/current-blog-post
 (fn [db]
   (:blog-posts/current-blog-post db)))

;;; Views ;;;

(defn navbar []
  (let [burger-active (r/atom false)]
    (fn []
      [:nav.navbar.is-info
       [:div.container
        [:div.navbar-brand
         [:a.navbar-item
          {:href "/"
           :style {:font-weight "bold"}}
          "FP BLOGG"]
         [:span.navbar-burger.burger
          {:data-target "nav-menu"
           :on-click #(swap! burger-active not)
           :class (when @burger-active "is-active")}
          [:span]
          [:span]
          [:span]]]
        [:div#nav-menu.navbar-menu
         {:class (when @burger-active "is-active")}
         [:div.navbar-start
          [:a.navbar-item
           {:href "/#"}
           "Home"]
          [:a.navbar-item
           {:href "/#/about"}
           "About"]
          [:a.navbar-item
           {:href "/#/blog-list"}
           "Posts"]]]]])))

(def routes
  ["/"
   [""
    {:name      ::home
     :view      home/home
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
     :view about/about
     :controllers
     [{:start (fn [] (js/console.log "Entering about page"))
       :stop  (fn [] (js/console.log "Leaving about page"))}]}]
   ["blog-list"
    {:name ::blog-list
     :view home/blog-list
     :link-text "Blog List"
     :controllers
     [{:start (fn [] (js/console.log "Entering bloglist"))
       :stop  (fn [] (js/console.log "Leaving bloglist"))}]}]
   ["blog-posts/:id"
    {:name ::blog-post
     :view blog-post/blog-post
     :link-text "Blog Post"
     :controllers
     [{:parameters {:path [:id]}
       :start (fn [params] (do
                             (rf/dispatch [:blog-posts/load])
                             (rf/dispatch [:blog-posts/select-blog-post (-> params :path :id)])))
       :stop (fn [_params] (rf/dispatch [:blog-posts/select-blog-post nil]))}]}]])

(defn on-navigate [new-match]
  (let [old-match (rf/subscribe [:front-end-routes/current-route])]
    (when new-match
      (let [cs (reitit-c/apply-controllers (:controllers @old-match) new-match)
            m  (assoc new-match :controllers cs)]
        (rf/dispatch [:front-end-routes/navigated m])))))

(def router
  (reitit-fe/router
   routes
   {:data {:coercion rss/coercion}}))

(defn init-routes! []
  (js/console.log "initializing routes")
  (reitit-fee/start!
   router
   on-navigate
   {:use-fragment true}))

(defn main-page []
  (let [current-route @(rf/subscribe [:front-end-routes/current-route])]
    [:<>
     [navbar]
     (when current-route
       [:section.section
        [:div.container
         [:div.content>div.columns.is-centered>div.column.is-two-thirds
          [(-> current-route :data :view)]]]])]))

(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (.log js/console "Mounting Components...")
  (init-routes!)
  (dom/render [main-page #_{:router router}]
              (.getElementById js/document "content"))
  (.log js/console "Components Mounted!"))

(defn init!
  "Actions involving data are moved to a Re-Frame event [:app/initialize-db]"
  []
  (.log js/console "Here she comes...")
  (mount/start)
  (rf/dispatch-sync [:app/initialize])
  (mount-components))
