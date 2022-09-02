(ns clj-blog.views.home
  (:require
   [clj-blog.blog-posts.blog-components-templates :refer [blog-post-container]]
   [clj-blog.blog-posts.utils :refer [inst->date-str]]
   [clj-blog.ajax :as ajax]
   [re-frame.core :as rf]))

;;EFFECTS vs. EVENTS
;; a strict distinction between events & effects is crucial...
;; utilize `reg-fx` to create effects within effects map

;;EFFECTS (actions) - separate from application logic (events)
;;EVENTS - should only transform data

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

(rf/reg-sub
 :blog-posts/blog-post
 :<- [:blog-posts/list]
 (fn [blog-posts [_ id]]
   (first (filter (fn [{blog-id :id}] (= id blog-id)) blog-posts))))

(defn blog-list []
  (let [blog-posts (rf/subscribe [:blog-posts/list])]
    (fn []
      [:<>
       [:h1 "Blog Posts"]
       [:ul
        (for [blog-post @blog-posts]
          ^{:key (:title blog-post)}
          [:li (str (inst->date-str (:date_created blog-post)) " - " (:title blog-post))])]])))

(defn home []
  (let [blog-posts (rf/subscribe [:blog-posts/list])]
    (fn []

      #_[blog_components_01/css-part-1]
      #_[blog_components_01/practical-google-closure]
       ; [:hr]
       ; [blog_components_01/higher-order-functions]
       ; [:hr]

       ;TODO: Implement blog-posts/3 page
       ;TODO: Tie on-click handlers to render them
       ;TODO: Clean up these namespaces
      [:<>
       (for [blog-post @blog-posts
             :when (= 1 (:id blog-post))]
         ^{:key (:id blog-post)}
         [blog-post-container blog-post])])))
