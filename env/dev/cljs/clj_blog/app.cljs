(ns ^:dev/once clj-blog.app
  (:require
    [devtools.core :as devtools]
    [clj-blog.core :as core]))

(enable-console-print!)

(println "loading env/dev/cljs/clj_blog/app.cljs")

(devtools/install!)

(core/init!)
