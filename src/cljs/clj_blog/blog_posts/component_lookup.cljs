(ns clj-blog.blog-posts.component-lookup
  (:require
   [clj-blog.blog-posts.blog-components-01 :as blog_components_01]
   [clj-blog.blog-posts.blog-components-02 :as blog_components_02]))

(def component-lookup
  {"blog_components_01/first-entry" blog_components_01/first-entry
   "blog_components_01/higher-order-functions" blog_components_01/higher-order-functions
   "blog_components_01/css-quest" blog_components_01/css-quest
   "blog_components_02/idioms" blog_components_02/idioms
   "blog_components_02/little-schemer-chapter8" blog_components_02/little-schemer-chapter8})
