; (ns clj-blog.core
;   (:require
;    [ajax.core :refer [GET]]
;    [clj-blog.blog-posts.blog-components-01 :as blog_components_01]
;    [clj-blog.routes.app :refer [app-routes]]
;    [mount.core :as mount]
;    [re-frame.core :as rf]
;    [reagent.core :as r]
;    [reagent.dom :as dom]
;    [reitit.coercion.spec :as reitit-spec]
;    [reitit.frontend :as rtf]
;    [reitit.frontend.easy :as rtfe]))


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

;; Triggering navigation from events.
(rf/reg-fx
 ::navigate!
 (fn [k params query]
   (reitit-fee/push-state k params query)))

;;; Events ;;;

(rf/reg-event-db
 :app/initialize-db
 (fn [_ _]
   {:current-route nil
    :blog-posts/current-blog-post nil
    :dispatch [:blog-posts/load]}))

(rf/reg-event-fx
 ::navigate
 (fn [db [_ route]]
   ;; See `navigate` effect in routes.cljs
   {::navigate! route}))

(rf/reg-event-db
 ::select-blog-post
 (fn [db [_ blog-id]]
   (assoc db :blog-posts/current-blog-post blog-id)))

(rf/reg-event-db
 ::navigated
 (fn [db [_ new-match]]
   (assoc db :current-route new-match)))

;;; Subscriptions ;;;

(rf/reg-sub
 ::current-route
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

(defn home-page []
  [:div
   [:h1 "This is home page"]
   [:button
    ;; Dispatch navigate event that triggers a (side)effect.
    {:on-click #(rf/dispatch [::navigate ::sub-page2])}
    "Go to sub-page 2"]])

(defn sub-page1 []
  [:div
   [:h1 "This is sub-page 1"]])

(defn sub-page2 []
  [:div
   [:h1 "This is sub-page 2"]])

;;; Effects ;;;

;;; Routes ;;;

(defn href
  "Return relative url for given route. Url can be used in HTML links."
  ([k]
   (href k nil nil))
  ([k params]
   (href k params nil))
  ([k params query]
   (reitit-fee/href k params query)))

(def routes
  ["/"
   [""
    {:name      ::home
     :view      home/home
     :link-text "Home"
     :controllers
     [{;; Do whatever initialization needed for home page
       ;; I.e (re-frame/dispatch [::events/load-something-with-ajax])
       :start (fn [& params] (do
                               (rf/dispatch [:blog-posts/load])
                               (js/console.log "Entering home page")))
       ;; teardown can be done here.
       :stop  (fn [& params] (js/console.log "Leaving home page"))}]}]
   ["about"
    {:name ::about
     :view about/about}]
   ["blog-list"
    {:name ::blog-list
     :view home/blog-list
     :link-text "Blog List"
     :controllers
     [{:start (fn [& params] (js/console.log "Entering bloglist"))
       :stop  (fn [& params] (js/console.log "Leaving bloglist"))}]}]
   ["blog-posts/:id"
    {:name ::blog-post
     :view blog-post/blog-post
     :link-text "Blog Post"
     :controllers
     [{:parameters {:path [:id]}
       :start (fn [params] (rf/dispatch [::select-blog-post (-> params :path :id)]))
       :stop (fn [_params] (rf/dispatch [::select-blog-post nil]))}]}]

   ["sub-page1"
    {:name      ::sub-page1
     :view      sub-page1
     :link-text "Sub page 1"
     :controllers
     [{:start (fn [& params] (js/console.log "Entering sub-page 1"))
       :stop  (fn [& params] (js/console.log "Leaving sub-page 1"))}]}]
   ["sub-page2"
    {:name      ::sub-page2
     :view      sub-page2
     :link-text "Sub-page 2"
     :controllers
     [{:start (fn [& params] (js/console.log "Entering sub-page 2"))
       :stop  (fn [& params] (js/console.log "Leaving sub-page 2"))}]}]])

(defn on-navigate [new-match]
  (let [old-match (rf/subscribe [::current-route])]
    (when new-match
      (let [cs (reitit-c/apply-controllers (:controllers @old-match) new-match)
            m  (assoc new-match :controllers cs)]
        (rf/dispatch [::navigated m])))))

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

#_(defn nav [{:keys [router current-route]}]
    [:<>
     [navbar]
     (into
      [:ul]
      (for [route-name (reitit/route-names router)
            :let       [route (reitit/match-by-name router route-name)
                        text (-> route :data :link-text)]]
        [:li
         (when (= route-name (-> current-route :data :name))
           "> ")
      ;; Create a normal links that user can click
         [:a {:href (href route-name)} text]]))])

(defn main-page []
  (let [current-route @(rf/subscribe [::current-route])]
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
  (rf/dispatch-sync [:app/initialize-db])
  (mount-components))
