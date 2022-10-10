(ns ^:dev/once clj-blog.app
  (:require
    [clj-blog.core :as core]))

(enable-console-print!)

(println "loading env/dev/cljs/clj_blog/app.cljs")

(core/init!)
