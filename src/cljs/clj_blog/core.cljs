(ns clj-blog.core
  (:require
   [clj-blog.routes.app :as app-routes]
   [mount.core :as mount]
   [re-frame.core :as rf]
   [reagent.core :as r]
   [reagent.dom :as dom]
   [reitit.coercion.spec :as rss]
   [reitit.frontend :as reitit-fe]
   [reitit.frontend.controllers :as reitit-c]
   [reitit.frontend.easy :as reitit-fee]))

(rf/reg-event-db
 :app/initialize
 (fn [_ _]
   {:current-route nil
    :blog-post/current-blog-post nil
    :dispatch [:blog-post/load]}))

(rf/reg-event-fx
 :blog-post/load
 (fn [{:keys [db]} _]
   {:db (assoc db :blog-posts/loading? true)
    :ajax/get {:url "/api/blog-posts"
               :success-path [:blog-posts]
               :success-event [:blog-posts/set]}}))

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

(defn on-navigate [new-match]
  (let [old-match (rf/subscribe [:front-end-routes/current-route])]
    (.log js/console new-match)
    (when new-match
      (let [cs (reitit-c/apply-controllers (:controllers @old-match) new-match)
            m  (assoc new-match :controllers cs)]
        (rf/dispatch [:front-end-routes/navigated m])))))

(def front-end-router
  (reitit-fe/router
   app-routes/front-end-routes
   {:data {:coercion rss/coercion}}))

(defn init-routes! []
  (js/console.log "initializing routes")
  (reitit-fee/start!
   front-end-router
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
  (dom/render [main-page]
              (.getElementById js/document "content"))
  (.log js/console "Components Mounted!"))

(defn init!
  "Actions involving data are moved to a Re-Frame event [:app/initialize-db]"
  []
  (.log js/console "Here she comes...")
  (mount/start)
  (rf/dispatch-sync [:app/initialize])
  (mount-components))
