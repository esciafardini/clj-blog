(ns clj-blog.views.test
  (:require
   [clj-blog.blog-posts.blog-components-01 :refer [css-quest]]
   [clj-blog.blog-posts.blog-components-02 :refer [little-schemer-chapter8 idioms]]
   ))

(defn test-view []
  [:div.column.is-two-thirds
   ;put yr draft blog component here
   [little-schemer-chapter8]])
