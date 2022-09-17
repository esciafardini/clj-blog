(ns clj-blog.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [clj-blog.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[clj-blog started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[clj-blog has shut down successfully]=-"))
   :middleware wrap-dev
   :hsts false
   :ssl-redirect false
   :proxy false})
