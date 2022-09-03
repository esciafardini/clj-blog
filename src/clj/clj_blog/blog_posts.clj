(ns clj-blog.blog-posts
  (:require [clj-blog.db.core :as db]))

;;Notice: nothing related to HTTP
;; why?
;; now these can be utilized by other services
;;this is the `model`

(defn blog-post-list []
  {:blog-posts (vec (db/get-blog-posts))})

(defn blog-post-by-id [id]
  {:blog-post (first (db/get-blog-post-by-id {:id id}))})
