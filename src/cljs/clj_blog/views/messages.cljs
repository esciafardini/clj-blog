(ns clj-blog.views.messages
  (:require
   [ajax.core :refer [GET POST]]
   [clj-blog.validation :refer [validate-message]]
   [clojure.string :as string]
   [clj-blog.ajax :as ajax]
   [re-frame.core :as rf]
   [reagent.core :as r]))

#_(rf/reg-event-fx
 :message/send!-called-back
 (fn [_ [_ {:keys [success errors]}]]
   (if success
     {:dispatch [:form/clear-fields]}
     {:dispatch [:form/set-server-errors errors]})))

(rf/reg-event-fx
 :message/send!
 (fn [{:keys [db]} [_ fields]]
   (POST "/api/message"
         {:format :json
          :headers
          {"Accept" "application/transit+json"
           "x-csrf-token" (.-value (.getElementById js/document "token"))}
          :params fields
          :handler #(rf/dispatch
                     [:message/add
                      (-> fields
                          (assoc :timestamp (js/Date.)))])
          :error-handler #(rf/dispatch
                           [:form/set-server-errors
                            (get-in % [:response :errors])])})
   {:db (dissoc db :form/server-errors)}))

(rf/reg-event-fx
 :messages/load
 (fn [{:keys [db]} _]
   {:db (assoc db :messages/loading? true)
    :ajax/get {:url "/api/messages"
               :success-path [:messages]
               :success-event [:messages/set]}}))

(rf/reg-event-db
 :messages/set
 (fn [db [_ messages]]
   (-> db
       (assoc :messages/loading? false
              :messages/list messages))))

(rf/reg-sub
 :messages/loading?
 (fn [db _]
   (:messages/loading? db)))

(rf/reg-sub
 :messages/list
 (fn [db _]
   (:messages/list db [])))

(defn message-list [messages]
  [:ul.messages
   (for [{:keys [timestamp message name]} @messages]
     ^{:key timestamp}
     [:li
      [:time (.toLocaleString timestamp)]
      [:p message]
      [:p " - " name]])])

(rf/reg-event-db
 :message/add
 (fn [db [_ message]]
   (update db :messages/list conj message)))

(rf/reg-event-db
 :form/set-field
 [(rf/path :form/fields)]
 (fn [fields [_ id value]]
   (assoc fields id value)))

(rf/reg-event-db
 :form/clear-fields
 [(rf/path :form/fields)]
 (fn [_ _]
   {}))

;;this needs to be cleared on successful comment - not working rn when I try
(rf/reg-sub
 :form/validation-errors
 :<- [:form/fields]
 (fn [fields _]
   (validate-message fields)))

(rf/reg-sub
 :form/fields
 (fn [db _]
   (:form/fields db)))

(rf/reg-sub
 :form/field
 :<- [:form/fields]
 (fn [fields [_ id]]
   (get fields id)))

(rf/reg-event-db
 :form/set-server-errors
 [(rf/path :form/server-errors)]
 (fn [_ [_ errors]]
   errors))

(rf/reg-sub
 :form/server-errors
 (fn [db _]
   (:form/server-errors db)))

;;Validation errors are reactively computed
(rf/reg-sub
 :form/validation-errors
 :<- [:form/fields]
 (fn [fields _]
   (validate-message fields)))

(rf/reg-sub
 :form/validation-errors?
 :<- [:form/validation-errors]
 (fn [errors _]
   (seq errors)))

(rf/reg-sub
 :form/errors
 :<- [:form/validation-errors]
 :<- [:form/server-errors]
 (fn [[validation server] _]
   (merge validation server)))

(rf/reg-sub
 :form/error
 :<- [:form/errors]
 (fn [errors [_ id]]
   (get errors id)))

(defn errors-component [id]
  (when-let [error @(rf/subscribe [:form/error id])]
    [:div.notification.is-danger (string/join error)]))

(defn text-input
  "now with track - prevents immediate updates to app-db on keypress"
  [{value :value
    attrs :attrs
    :keys [on-save]}]
  (let [draft (r/atom nil)
        value (r/track #(or @draft @value ""))]
    (fn []
      [:input.input
       (merge attrs
              {:type :text
               :on-focus #(reset! draft (or @value ""))
               :on-blur (fn []
                          (on-save (or @draft "")))
               :on-change #(reset! draft (.. % -target -value))
               :value @value})])))

(defn text-area-input
  "now with track - prevents immediate updates to app-db on keypress"
  [{value :value
    attrs :attrs
    :keys [on-save]}]
  (let [draft (r/atom nil)
        value (r/track #(or @draft @value ""))]
    (fn []
      [:textarea.textarea
       (merge attrs
              {:on-focus #(reset! draft (or @value ""))
               :on-blur (fn []
                          (on-save (or @draft "")))
               :on-change #(reset! draft (.. % -target -value))
               :value @value})])))

(defn message-form []
  [:div
   [errors-component :server-error]
   [:div.field
    [:label.label {:for :name} "Your name:"]
    [errors-component :name]
    [text-input
     {:attrs {:name :name}
      :value (rf/subscribe [:form/field :name])
      :on-save #(rf/dispatch [:form/set-field :name %])}]]
   [:label.label {:for :name} "Leave a message:"]
   [text-area-input
    {:attrs {:name :message}
     :value (rf/subscribe [:form/field :message])
     :on-save #(rf/dispatch [:form/set-field :message %])}]
   [:input.button.is-primary
    {:type :submit
     :on-click #(rf/dispatch [:message/send!
                              @(rf/subscribe [:form/fields])])
     :value "comment"}]])

(defn reload-messages-button []
  (let [loading? (rf/subscribe [:messages/loading?])]
    [:button.button.is-info.is-fullwidth
     {:on-click #(rf/dispatch [:messages/load])
      :disabled @loading?}
     (if @loading?
       "Loading messages..."
       "Refresh Messages")]))

(defn messages []
  (let [messages (rf/subscribe [:messages/list])]
    (fn []
      [:div.column.is-two-thirds
       [:div
        [:div.columns>div.column
         [message-list messages]]
        [:div.columns>div.column
         [message-form]]]])))
