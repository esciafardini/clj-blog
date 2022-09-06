(ns clj-blog.views.blog-post
  (:require
   [clj-blog.blog-posts.blog-components-templates :refer [blog-post-container]]
   [re-frame.core :as rf]))

(rf/reg-event-db
 :blog-post/set
 (fn [db [_ blog-post]]
   (-> db
       (assoc :blog-post/loading? false
              :blog-post/current-blog-post blog-post))))

(rf/reg-event-fx
 :blog-post/select-blog-post
 (fn [{:keys [db]} [_ id]]
   {:db (assoc db :blog-post/loading? true)
    :ajax/get {:url (str "/api/blog-posts/by/" id)
               :success-path [:blog-post]
               :success-event [:blog-post/set]}}))

(rf/reg-sub
 :blog-post/current-blog-post
 (fn [db]
   (:blog-post/current-blog-post db)))

(rf/reg-sub
 :blog-post/loading?
 (fn [db]
   (:blog-post/loading? db)))

(defn blog-post []
  (let [loading? (rf/subscribe [:blog-post/loading?])
        blog-post (rf/subscribe [:blog-post/current-blog-post])]
    (fn []
      [:div.column.is-two-thirds
       (cond
         @loading?
         [:<>]
         (nil? @blog-post)
         [:div "No blog post"]
         :else
         [:<>
          [blog-post-container @blog-post]
          [:hr]
          [:div {:style {:text-align "center"}}
           [:a {:href "/blog-list"} "Go To Blog Posts"]]])])))
