(ns clj-blog.routes.home
  (:require
   [clj-blog.layout :as layout]
   [clj-blog.db.core :as db]
   [clojure.java.io :as io]
   [clj-blog.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [struct.core :as st]))

(defn home-page [{:keys [flash] :as request}]
  ;;;;;;;;;;;;;;;request, template, params 
  (layout/render request "home.html"  (merge 
                                        {:messages (db/get-messages)}
                                        (select-keys flash [:name :message :errors]))))
;;;;; leaving chunka key here to show that any key can be utilized by Bulma
;TODO : delete when this doesn't feel relevant anymore
(defn about-page [request]
  (layout/render request "about.html"))

(def message-schema
  [[:name
    st/required
    st/string]
   [:message
    st/required
    st/string
    {:message "message must contain at least 10 characters"
     :validate (fn [msg] (>= (count msg) 10))}]])

(defn validate-message [params]
  (first (st/validate params message-schema)))

(comment
;pretty cool
  (validate-message {:message " hahahah ahahah ha hahahaha hahahahwhowwww" :name "Pickel"}) ; nil
  (validate-message {:name "Pickel"})  ; {:message "this field is mandatory"}
  (validate-message {:message "oh" :name "Pickel"})  ; {:message "message must contain at least 10 characters"}
  )

(defn save-message! [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (-> (response/found "/")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/save-message! params)
      (response/found "/"))))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/about" {:get about-page}]
   ["/message" {:post save-message!}]])

