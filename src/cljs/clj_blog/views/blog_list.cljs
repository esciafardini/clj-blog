(ns clj-blog.views.blog-list
  (:require
   [clj-blog.blog-posts.utils :refer [inst->date-str]]
   [clj-blog.ajax :as ajax] ;GOD DAMN IT
   [re-frame.core :as rf]))

(rf/reg-event-fx
 :blog-list/load
 (fn [{:keys [db]} _]
   {:db (assoc db :blog-list/loading? true)
    :ajax/get {:url "/api/blog-posts"
               :success-path [:blog-list]
               :success-event [:blog-list/set]}}))

(rf/reg-event-db
 :blog-list/set
 (fn [db [_ blog-posts]]
   (do
     (.log js/console "hit")
     (-> db
         (assoc :blog-list/loading? false
                :blog-list/list blog-posts)))))

(rf/reg-sub
 :blog-list/loading?
 (fn [db _]
   (:blog-list/loading? db)))

(rf/reg-sub
 :blog-list/list
 (fn [db _]
   (:blog-list/list db [])))

(defn blog-list []
  (let [blog-posts (rf/subscribe [:blog-list/list])
        loading? (rf/subscribe [:blog-list/loading?])]
    (fn []
      (if @loading?
        [:h1 "WAIT"]
        [:div.column.is-two-thirds
         [:h1 "Blogg Posts"]
         [:ul
          (for [blog-post @blog-posts]
            ^{:key (:title blog-post)}
            [:li
             [:a {:href (str "/blog-posts/" (:id blog-post))}
              (str (inst->date-str (:date_created blog-post)) " - " (:title blog-post))]])]]))))
