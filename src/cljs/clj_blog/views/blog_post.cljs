(ns clj-blog.views.blog-post
  (:require
   [clj-blog.ajax :as ajax]
   [clj-blog.blog-posts.component-lookup :refer [component-lookup]]
   [clj-blog.blog-posts.utils :as utils :refer [loading-indicator]]
   [re-frame.core :as rf]))

(rf/reg-event-db
 :blog-post/set
 (fn [db [_ blog-post]]
   (-> db
       (assoc :blog-post/loading? :done
              :blog-post/current-blog-post blog-post))))

(rf/reg-event-db
 :blog-post/loading-with-indicator
 (fn [db _]
   (when (= :loading (:blog-post/loading? db))
     (assoc db :blog-post/loading? :loading-with-indicator))))

(rf/reg-event-fx
 :blog-post/select-blog-post
 (fn [{:keys [db]} [_ id]]
   {:db (assoc db :blog-post/loading? :loading)
    ; Wait half a second to display a loading indicator for better user experience
    :dispatch-later {:ms 500 :dispatch [:blog-post/loading-with-indicator]}
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

(defn blog-post-container []
  (fn [{:keys [title date_created component_function home-page?]}]
    (let [component (get component-lookup component_function)]
      [:div.blogpost
       [:h1 title]
       (if home-page?
         [:p {:style {:margin-top "2rem"}}]
         [:p.date (utils/inst->date-str date_created)])
       [component]])))

(defn blog-post []
  (let [loading? (rf/subscribe [:blog-post/loading?])
        blog-post (rf/subscribe [:blog-post/current-blog-post])]
    (fn []
      (case @loading?
        :loading
        [:<>]
        :loading-with-indicator
        [loading-indicator]
        :done
        [:div.column.is-two-thirds
         [blog-post-container @blog-post]
         [:hr]
         [:div {:style {:text-align "center"}}
          [:a {:href "/blog-list"} "Go To Blog Posts"]]]))))
