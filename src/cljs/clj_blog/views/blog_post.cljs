(ns clj-blog.views.blog-post
  (:require
   [re-frame.core :as rf]))

(defn blog-post []
  (let [post-id (rf/subscribe [:blog-posts/current-blog-post])
        f-blog (rf/subscribe [:blog-posts/blog-post 1])]
    (fn []
      [:div
       [:div "BLOGPOST VIEW"]
       [:div (str @f-blog)]
       [:div (str @post-id)]
       [:a {:href "/"} "Return home"]])))
