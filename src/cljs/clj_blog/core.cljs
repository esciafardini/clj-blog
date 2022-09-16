(ns clj-blog.core
  (:require
   [clj-blog.ajax :as ajax]
   [clj-blog.routes.app :refer [app-routes]]
   [clj-blog.views.navbar :as navbar]
   [mount.core :as mount]
   [re-frame.core :as rf]
   [reagent.dom :as dom]
   [reitit.coercion.spec :as reitit-spec]
   [reitit.frontend :as reitit-fe]
   [reitit.frontend.controllers :as reitit-controllers]
   [reitit.frontend.easy :as reitit-fee]))

; Events
(rf/reg-event-db
 :app/initialize
 (fn [_ _]
   {:current-route nil
    :blog-post/current-blog-post nil
    :dispatch [:blog-post/load]}))

(rf/reg-event-fx
 :blog-post/load
 (fn [{:keys [db]} _]
   {:db (assoc db :blog-post/loading? true)
    :ajax/get {:url "/api/blog-posts"
               :success-path [:blog-list]
               :success-event [:blog-posts/list]}}))

; Views
(defn page [{{:keys [view]} :data}]
  [:section.section>div.container
   (if view
     [:div.content>div.columns.is-centered
      [view]]
     [:<>])])

(defn main-page []
  (let [current-route @(rf/subscribe [:router/current-route])]
    [:<>
     [navbar/navbar]
     [page current-route]]))

; Frontend Routes
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

; Initialize App
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
