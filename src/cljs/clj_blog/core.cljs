(ns clj-blog.core
  (:require
   [ajax.core :refer [GET POST]]
   [clj-blog.validation :refer [validate-message]]
   [reagent.core :as r]
   [reagent.dom :as dom]
   [re-frame.core :as rf]))

;;; one of the main goals of rf is to remove state and logic from components
;;  separation of logic & components makes for easier testing, more composable components

(rf/reg-event-fx
  ;;a handler fn and a keyword that identifies it
 :app/initialize
 (fn [_ _]
   {:db {:messages/loading? true}}))

(rf/reg-event-db
 ;;a helper fn that only uses the db effect
 ;;unwraps the db from the co-effects map (the first argument)
 ;;wraps your return with {:db ...}
 :messages/set
 (fn [db [_ messages]]
   (-> db
       (assoc :messages/loading? false
              :messages/list messages))))

(rf/reg-event-db
 :messages/add
 (fn [db [_ message]]
   (update db :messages/list conj message)))

(rf/reg-sub
 :messages/list
 (fn [db _]
   (:messages/list db [])))

(rf/reg-sub
 :messages/loading?
 (fn [db _]
   (:messages/loading? db)))

;an event handler takes two args and returns an effects map
;this one sets the db to the returned value

(defn get-messages
  "Recieves a reagent atom that stores messages
  Notice: this doesn't require csrf protection"
  []
  (GET "/messages"
    {:headers {"Accept" "application/transit+json"}
    ;:handler (fn [response] (reset! messages (:messages response)))  ;;Reagent Remnant
     :handler (fn [response]
                (rf/dispatch [:messages/set (:messages response)]))}))

(defn send-message! [fields errors]
  (if-let [validation-errors (validate-message @fields)]
    (reset! errors validation-errors)
    (POST "/message"
      {:format :json
       :headers
       {"Accept" "application/transit+json" ;transit tags clojure data structures when they're encoded
        "x-csrf-token" (.-value (.getElementById js/document "token"))}
       :params @fields
       :handler (fn [_]
                  (rf/dispatch [:messages/add (assoc @fields :timestamp (js/Date.))])
                  (reset! errors nil)
                  (reset! fields nil))
     ; :handler (fn [res]
     ;            (reset! errors nil)
     ;            (reset! fields nil)
     ;            (swap! messages conj (assoc @fields :timestamp (js/Date.))) ;conj a new map onto the vector
     ;            (.log js/console res))
       :error-handler
       (fn [res]
         (let [error-map (-> res
                             :response
                             :errors)]
           (.log js/console error-map)
           (reset! errors error-map)))})))

(defn errors-component [errors error-type]
  (when-let [error (get @errors error-type)]
    [:div
     [:div.notification.is-danger (apply str error)]]))

;;A form 2 component - the atom is defined in a let block before the component fn
(defn message-form
  "Reagent will call this outermost function first, then whenever the fields atom changes,
  it will call the inner function."
  []
  (let [fields (r/atom {}) ;reagent atoms utilized this way == an example of closure
        errors (r/atom nil)]
    (fn []
      [:div
       [:div.field
        [:label.label {:for :name} "Name"]
        [:input.input
         {:type :text
          :name :name
          :value (:name @fields)
          :on-change (fn [e] (swap! fields
                                    assoc :name (.-value (.-target e))))}]]
       [errors-component errors :name]
       [:div.field
        [:label.label {:for :message} "Message"]
        [:textarea.textarea
         {:name :message
          :value (:message @fields)
          :on-change (fn [e] (swap! fields
                                    assoc :message (-> e .-target .-value)))}]]
       [errors-component errors :message]
       [:input.buttom.is-primary
        {:type :submit
         :value "Comment"
         :on-click (fn [_e] (send-message! fields errors))}]])))

(defn message-list [messages]
  [:ul.messages
   (for [{:keys [timestamp message name]} @messages]
     ^{:key timestamp}
     [:li
      [:time (.toLocaleString timestamp)]
      [:p message]
      [:p " - " name]])])

(defn home
  "Reagent provides a powerful mechanism for connecting producers and consumers of data.
  The get-messages fn and the message-list fn have no coupling between them.
  Reagent atoms can be used to observe the current value of the model by components with no awareness as to how it was populated."
  []
  (let [messages (rf/subscribe [:messages/list])]
    (rf/dispatch [:app/initialize])
    (get-messages)
    (fn []
      (if @(rf/subscribe [:messages/loading?])
        [:div>div.row>div.span12>h3 "Loading messages"]
        [:div.content>div.columns.is-centered>div.column.is-two-thirds
         [:div.columns>div.column
          [:h3 "Messages"]
          [message-list messages]]
         [:div.columns>div.column
          [message-form messages]]]))))

;reagent.dom/render takes two arguments
;; 1. A component to render
;; 2. The target DOM node
(dom/render
 [home]
 (.getElementById js/document "content"))

#_[:div#hello.content>h1 "Another Way To Say It"]
