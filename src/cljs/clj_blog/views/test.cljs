(ns clj-blog.views.test
  (:require
   [clj-blog.blog-posts.blog-components-01 :refer [css-quest-chapter-2]]))

(defn test-view []
  [:div.column.is-two-thirds
   ;put yr draft blog component here
   [css-quest-chapter-2]])