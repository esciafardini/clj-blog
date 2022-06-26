(ns clj-blog.messages
  (:require [clj-blog.db.core :as db]
            [clj-blog.validation :refer [validate-message]]))

;;Notice: nothing related to HTTP
;; why?
;; now these can be utilized by other services
;;this is the messages `model`

(defn message-list []
  {:messages (vec (db/get-messages))})

(defn save-message! [message]
  (if-let [errors (validate-message message)]
    (throw (ex-info "Message is invalid"
                    {:clj-blog/error-id :validation
                     :errors errors}))
    (db/save-message! message)))
