(ns clj-blog.blog-posts
  (:require [clj-blog.db.core :as db]))

;;Notice: nothing related to HTTP
;; why?
;; now these can be utilized by other services
;;this is the `model`

(defn get-blog-posts []
  {:blog-posts (vec (db/get-blog-posts))})
