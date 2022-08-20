(ns clj-blog.app
  (:require
    [clj-blog.core :as core]))

(set! *print-fn* (fn [& _]))

(core/init!)
