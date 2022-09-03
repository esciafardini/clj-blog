(ns clj-blog.views.blog-post
  (:require
   [clj-blog.blog-posts.blog-components-templates :refer [blog-post-container]]
   [re-frame.core :as rf]))

(rf/reg-event-db
 :blog-post/select-blog-post
 (fn [db [_ blog]]
   (assoc db :blog-post/current-blog-post blog)))

(rf/reg-sub
 :blog-post/current-blog-post
 (fn [db]
   (:blog-post/current-blog-post db)))

(defn blog-post []
  (let [blog-post (rf/subscribe [:blog-post/current-blog-post])]
    (fn []
      [:<>
       (when blog-post
         [blog-post-container @blog-post])
       [:hr]
       [:div {:style {:text-align "center"}}
        [:a {:href "/#/"} "Return Home"]]])))
