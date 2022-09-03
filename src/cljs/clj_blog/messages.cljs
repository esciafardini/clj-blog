(ns clj-blog.messages
  (:require
   [ajax.core :refer [GET]]
   [clj-blog.blog-posts.blog-components-templates :refer [blog-post]]
   [clj-blog.validation :refer [validate-message]]
   [clojure.string :as string]
   [mount.core :as mount]
   [re-frame.core :as rf]
   [reagent.core :as r]
   [reagent.dom :as dom]))

;https://github.com/react-syntax-highlighter/react-syntax-highlighter
;https://github.com/kkinnear/zprint
;;
;;; use these for code blocks
;;; zprint it, then wrap that in syntax highlighter

(comment
  (.log js/console "Hello From The Shadows")
  (js/alert "ALERT From The Shadows"))

;;FOR REAL -- TALKIN ABOUT EFFECTS vs. EVENTS
;; a strict distinction between events & effects is crucial...
;; utilize `reg-fx` to create effects within effects map

;;EFFECTS (actions) - separate from application logic (events)
;;EVENTS - should only transform data

(rf/reg-event-fx
 ;;actions involving data should happen here rather than the init! fn
 :messages/initialize
 (fn [_ _]
   {:db {:messages/loading? true}
    :dispatch [:messages/load]}))

(rf/reg-event-fx
 :messages/load
 (fn [{:keys [db]} _]
   (GET "/api/messages"
     {:headers {"Accept" "application/transit+json"}
      :handler #(rf/dispatch [:messages/set (:messages %)])})
   {:db (assoc db :messages/loading? true)}))

;;RF handler function takes
;; 1. coeffects map (includes db)
;; 2. event-vector (ID is first argument, then args passed in)
(rf/reg-event-fx
 :message/send!-called-back
 (fn [_ [_ {:keys [success errors]}]]
   (if success
     {:dispatch [:form/clear-fields]}
     {:dispatch [:form/set-server-errors errors]})))

(rf/reg-event-fx
 :message/send! ;;ID
 ;; handler fn....
 (fn [{:keys [db]} [_ fields]]
   {:db (dissoc db :form/server-errors)
    :ws/send! {:message [:message/create! fields]
               :timeout 10000
               :callback-event [:message/send!-called-back]}}))

(rf/reg-fx
 :ajax/get
 (fn [{:keys [url success-event error-event success-path]}] ;;success-path allows us to specify a path in the response we'd like to pas to our success-event
   (GET url
     (cond-> {:headers {"Accept" "application/transit+json"}}
       success-event (assoc :handler
                            #(rf/dispatch
                              (conj success-event
                                    (if success-path
                                      ;the success path is key-path to data in response
                                      (get-in % success-path)
                                      %))))
       error-event (assoc :error-handler
                          #(rf/dispatch
                            (conj error-event %)))))))

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
  (println messages)
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
               :value @value})]))
  )

(defn message-form []
  [:div
   [errors-component :server-error]
   [:div.field
    [:label.label {:for :name} "Name"]
    [errors-component :name]
    [text-input
     {:attrs {:name :name}
      :value (rf/subscribe [:form/field :name])
      :on-save #(rf/dispatch [:form/set-field :name %])}]]
   [text-area-input
    {:attrs {:name :message}
     :value (rf/subscribe [:form/field :message])
     :on-save #(rf/dispatch [:form/set-field :message %])}]
   [:input.button.is-primary
    {:type :submit
     :disabled @(rf/subscribe [:form/validation-errors?])
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

(defn home []
  (let [messages (rf/subscribe [:messages/list])]
    (fn []
      [:div.content>div.columns.is-centered>div.column.is-two-thirds
       [:div
        [:div.columns>div.column
         [:h3 "Messages"]
         [message-list messages]]
        [:div.columns>div.column
         [reload-messages-button]]
        [:div.columns>div.column
         [message-form]]]])))

#_(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (.log js/console "Mounting Components...")
  (dom/render [#'home] (.getElementById js/document "messages"))
  (.log js/console "Components Mounted!"))

#_(defn init!
  "Actions involving data are moved to a Re-Frame event [:app/initialize]"
  []
  (.log js/console "Here she comes...")
  (mount/start)
  (rf/dispatch [:messages/initialize])
  (mount-components))
