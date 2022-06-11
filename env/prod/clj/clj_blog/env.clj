(ns clj-blog.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[clj-blog started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[clj-blog has shut down successfully]=-"))
   :middleware identity})
