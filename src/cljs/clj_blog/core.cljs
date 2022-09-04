(ns clj-blog.core
  (:require
   [clj-blog.routes.app :refer [app-routes]]
   [mount.core :as mount]
   [re-frame.core :as rf]
   [reagent.core :as r]
   [reagent.dom :as dom]
   [reitit.coercion.spec :as reitit-spec]
   [reitit.frontend :as reitit-fe]
   [reitit.frontend.controllers :as reitit-controllers]
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

(def front-end-router
  ;;looks good
  (reitit-fe/router
   (app-routes)
   {:data {:coercion reitit-spec/coercion}}))

(defn on-navigate [new-match]
  (when new-match
    (let [{controllers :controllers} @(rf/subscribe [:router/current-route])
          new-match-with-controllers (assoc new-match :controllers (reitit-controllers/apply-controllers controllers new-match))]
      (rf/dispatch [:router/navigated new-match-with-controllers]))))

(defn init-routes! []
  (js/console.log "initializing routes")
  (reitit-fee/start!
   front-end-router
   on-navigate
   {:use-fragment false}))

;;; Views ;;;

(defn nav-item [on-click href link-text]
  [:a.navbar-item
   {:on-click on-click
    :href href}
   link-text])

(defn navbar []
  (let [burger-active (r/atom false)
        on-click #(swap! burger-active not)]
    (fn []
      [:nav.navbar.is-dark
       [:div.container
        [:div.navbar-brand
         [:a.navbar-item
          {:href "/"
           :style {:font-family "VT323" :font-size "28px" :font-weight "bold"}}
          "FP BLOGG"]
         [:span.navbar-burger.burger
          {:data-target "nav-menu"
           :on-click on-click
           :class (when @burger-active "is-active")}
          [:span]
          [:span]
          [:span]]]
        [:div#nav-menu.navbar-menu
         {:class (when @burger-active "is-active")}
         [:div.navbar-start
          [nav-item on-click "/" "Home"]
          [nav-item on-click "/about" "About"]
          [nav-item on-click "/blog-list" "Posts"]]]]])))

(defn page [{{:keys [view]} :data}]
  [:section.section>div.container
   (if view
     [:div.content>div.columns.is-centered
      [view]]
     [:<>])])

(defn main-page []
  (let [current-route @(rf/subscribe [:router/current-route])]
    [:<>
     [navbar]
     [page current-route]]))

(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (.log js/console "Mounting Components...")
  (init-routes!)
  (dom/render [#'main-page] (.getElementById js/document "content"))
  (.log js/console "Components Mounted!"))

(defn init!
  "Actions involving data are moved to a Re-Frame event [:app/initialize-db]"
  []
  (.log js/console "Here she comes...")
  (mount/start)
  (rf/dispatch-sync [:app/initialize])
  (mount-components))
