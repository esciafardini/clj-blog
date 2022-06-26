(ns clj-blog.routes.services
  (:require
   [clj-blog.layout :as layout]
   [clj-blog.messages :as msg]
   [clj-blog.middleware :as middleware]
   [ring.util.http-response :as response]))

;;the save-message! and message-list fns in this namespace are HTTP specific
(defn message-list 
  "HTTP means of fetching messages from the psql database" 
  [_]
  (response/ok (msg/message-list)))

(defn save-message! 
  "HTTP means of saving a message to the psql database" 
  [{:keys [params]}]
  (try
    (msg/save-message! params)
    (response/ok {:status :ok})
    (catch Exception e
      (let [{id :clj-blog/error-id
             errors :errors} (ex-data e)] ;;;interesting destructure here - let bindings created via keywords
        (case id
          :validation
          (response/bad-request {:errors errors})
          ;;else
          (response/internal-server-error
            {:errors {:server-error ["Failed to save message!"]}}))))))

(defn service-routes []
  ["/api"
   {:middleware [middleware/wrap-formats]}
   ["/messages"
    {:get message-list}]
   ["/message"
    {:post save-message!}]])
