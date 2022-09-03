(ns clj-blog.views.blog-list
  (:require
   [clj-blog.blog-posts.utils :refer [inst->date-str]]
   [re-frame.core :as rf]))


(defn blog-list []
  (let [blog-posts (rf/subscribe [:blog-posts/list])]
    (fn []
      [:<>
       [:h1 "Blog Posts"]
       [:ul
        (for [blog-post @blog-posts]
          ^{:key (:title blog-post)}
          [:li
           {:on-click #(rf/dispatch [:blog-post/select-blog-post blog-post])}
           [:a {:href (str "/#/blog-posts/" (:id blog-post))}
           (str (inst->date-str (:date_created blog-post)) " - " (:title blog-post))]])]])))
