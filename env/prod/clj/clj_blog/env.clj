(ns clj-blog.env
  "Production defaults"
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[clj-blog started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[clj-blog has shut down successfully]=-"))
   :middleware identity
   ;https redirect enable for production
   :hsts true
   :ssl-redirect true
   :proxy true})
