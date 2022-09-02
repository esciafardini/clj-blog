(ns clj-blog.views.blog-post
  (:require
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
      [:div
       [:div "BLOGPOST VIEW"]
       [:div "ERR:"]
       [:div (str (:id @blog-post))]
       [:a {:href "/#/"} "Return home"]])))
