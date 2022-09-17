(ns clj-blog.env
  "Dev defaults"
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
   ;; do not re-direct http -> https in development environment
   :hsts false
   :ssl-redirect false
   :proxy false})
