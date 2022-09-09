(ns clj-blog.views.blog-post
  (:require
   [clj-blog.blog-posts.utils :as utils]
   [clj-blog.blog-posts.component-lookup :refer [component-lookup]]
   [clj-blog.ajax :as ajax] ;;CAREFUL - this needs to be here even tho LSP thinks it doesn't
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
