(ns clj-blog.core
  (:require
   [ajax.core :refer [GET]]
   [clj-blog.blog-posts.blog-components-01 :as blog_components_01]
   [mount.core :as mount]
   [re-frame.core :as rf]
   [reagent.dom :as dom]))

(comment
  (.log js/console "Hello From The Shadows")
  (js/alert "ALERT From The Shadows"))

(defn inst->date-str
  "Takes an inst and returns a human-readable string after
   converting it into a js/Date object."
  [inst-ob]
  (.toLocaleDateString (js/Date. inst-ob) "en-US" #js {:dateStyle "long"}))

;;FOR REAL -- TALKIN ABOUT EFFECTS vs. EVENTS
;; a strict distinction between events & effects is crucial...
;; utilize `reg-fx` to create effects within effects map

;;EFFECTS (actions) - separate from application logic (events)
;;EVENTS - should only transform data

(rf/reg-event-fx
 ;;actions involving data should happen here rather than the init! fn
 :app/initialize
 (fn [_ _]
   {:db {:blog-posts/loading? true}
    :dispatch [:blog-posts/load]}))

(rf/reg-fx
 :ajax/get
 (fn [{:keys [url success-event error-event success-path]}] ;;success-path allows us to specify a path in the response we'd like to pas to our success-event
   (GET url
     (cond-> {:headers {"Accept" "application/transit+json"}}
       ;;building a map, conditionally adds handler & error handler
       success-event (assoc :handler
                            #(rf/dispatch
                              (conj success-event
                                    (if success-path
                                      (get-in % success-path)
                                      %))))
       error-event (assoc :error-handler
                          #(rf/dispatch
                            (conj error-event %)))))))

(rf/reg-event-fx
 :blog-posts/load
 (fn [{:keys [db]} _]
   {:db (assoc db :blog-posts/loading? true)
    :ajax/get {:url "/api/blog-posts"
               :success-path [:blog-posts]
               :success-event [:blog-posts/set]}}))

(rf/reg-event-db
 :blog-posts/set
 (fn [db [_ blog-posts]]
   (-> db
       (assoc :blog-posts/loading? false
              :blog-posts/list blog-posts))))

(rf/reg-sub
 :blog-posts/loading?
 (fn [db _]
   (:blog-posts/loading? db)))

(rf/reg-sub
 :blog-posts/list
 (fn [db _]
   (:blog-posts/list db [])))

(def component-lookup
  {"blog_components_01/first-entry" blog_components_01/first-entry})

(defn blog-post-container []
  (fn [{:keys [title date_created component_function]}]
    (let [component (get component-lookup component_function)]
      [:div.blogpost
       [:h1 title]
       [:p.date (inst->date-str date_created)]
       [component]])))

(defn app []
  (let [blog-posts (rf/subscribe [:blog-posts/list])]
    (fn []
      [:div.content>div.columns.is-centered>div.column.is-two-thirds
       [blog_components_01/css-part-1]
       [:hr]
       [:hr]
       [blog_components_01/practical-google-closure]
       (do
         (for [blog-post @blog-posts]
           ^{:key (:id blog-post)}
           [blog-post-container blog-post]))])))

(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (.log js/console "Mounting Components...")
  (dom/render [#'app] (.getElementById js/document "content"))
  (.log js/console "Components Mounted!"))

(defn init!
  "Actions involving data are moved to a Re-Frame event [:app/initialize]"
  []
  (.log js/console "Here she comes...")
  (mount/start)
  (rf/dispatch [:app/initialize])
  (mount-components))
