(ns clj-blog.views.home
  (:require
   [clj-blog.blog-posts.blog-components-01 :refer [css-quest-2]]
   [clj-blog.blog-posts.utils :refer [inst->date-str]]
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

(defn home []
  (let [blog-posts (rf/subscribe [:blog-posts/list])]
    (fn []
      [:div.column.is-two-thirds
       [:h1 "Blogg Posts"]
       [:ul
        (for [blog-post @blog-posts]
          ^{:key (:title blog-post)}
          [:li
           {:on-click #(rf/dispatch [:blog-post/select-blog-post (:id blog-post)])}
           [:a {:href (str "/blog-posts/" (:id blog-post))}
            (str (inst->date-str (:date_created blog-post)) " - " (:title blog-post))]])]]
      #_[:div.column.is-two-thirds
       [css-quest-2]])))
