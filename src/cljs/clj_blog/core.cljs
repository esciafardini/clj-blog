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
           {:href "/"}
           "Home"]
          [:a.navbar-item
           {:href "/about"}
           "About"]
          [:a.navbar-item
           {:href "/blog-list"}
           "Posts"]]]]])))

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

(defn page [{{:keys [view name]} :data
             path                :path}]
  [:section.section>div.container
   (if view
     [:div.content>div.columns.is-centered>div.column.is-two-thirds
      [view]]
     [:div (str "No view specified for route: " name " on " path)])])

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
