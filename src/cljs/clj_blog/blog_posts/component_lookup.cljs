(ns clj-blog.blog-posts.component-lookup
  (:require
   [clj-blog.blog-posts.blog-components-01 :as blog_components_01]))

(def component-lookup
  {"blog_components_01/first-entry" blog_components_01/first-entry
   "blog_components_01/higher-order-functions" blog_components_01/higher-order-functions
   "blog_components_01/css-quest-chapter-1" blog_components_01/css-quest-chapter-1
   "blog_components_01/css-quest-chapter-2" blog_components_01/css-quest-chapter-2})
