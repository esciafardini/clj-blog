(ns clj-blog.views.blog-post
  (:require
   [re-frame.core :as rf]))

(rf/reg-event-db
 :blog-post/select-blog-post
 (fn [db [_ blog-id]]
   (assoc db :blog-post/current-blog-post blog-id)))

(rf/reg-sub
 :blog-post/current-blog-post
 (fn [db]
   (:blog-post/current-blog-post db)))

(defn blog-post []
  (let [post-id (rf/subscribe [:blog-post/current-blog-post])
        blog-post (rf/subscribe [:blog-posts/blog-post post-id])]
    (fn []
      [:div
       [:div "BLOGPOST VIEW"]
       [:div (str (:id @blog-post))]
       [:div (str @post-id)]
       [:a {:href "/#/"} "Return home"]])))
