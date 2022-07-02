(ns clj-blog.validation
  (:require
   [malli.core :as m]
   [malli.error :as me]))

(def message-schema
  [:map
   [:name [:fn
           {:error/fn (fn [{:keys [value] :as wut} _] (str "You need to enter a name"))}
           (fn [x] (and (string? x) (> (count x) 0)))]]
   [:message [:fn
              {:error/fn (fn [{:keys [value] :as wut} _] (str "Message `" value "` should be longer than 10 characters"))}
              (fn [x] (and (string? x) (> (count x) 10)))]]])

(defn validate-message 
  "Receives a map `params` keys :name & :message => {:name \"Johnny\" :message \"how are you today?\"}
   Returns nil if no errors
   Returns a map otherwise"
  [params]
  (-> message-schema
      (m/explain params)
      (me/humanize)))

(comment
  (validate-message {:name "ted" :message "ookaok"}))


