(ns clj-blog.config
  (:require
    [cprop.core :refer [load-config]]
    [cprop.source :as source]
    [mount.core :refer [args defstate]]))

;cprop massages environment variables into a clojure friendly format

;this variable `env` contains the configuration aggregated by config.edn in resources path
;; + the optional EDN config file pointed to by the conf java parameter
;; + java parameters and environment variables
(defstate env
  :start
  (load-config
    :merge
    [(args)
     (source/from-system-props)
     (source/from-env)]))
;; merge allows a base config that can be overwritten
