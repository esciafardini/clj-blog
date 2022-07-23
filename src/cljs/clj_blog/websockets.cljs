(ns clj-blog.websockets
  (:require-macros [mount.core :refer [defstate]])
  (:require [re-frame.core :as rf]
            [taoensso.sente :as sente]
            mount.core))

;;Sente is primarily a WebSockets library
;;but allows best of both Ajax & WebSockets workflows

;;WebSocket
;; server push results of an action to all concerned parties
;;AJAX
;; when we want information ABOUT our actions

(defstate socket ;;one of to defstates for socket
  :start (sente/make-channel-socket!
          "/ws"                                           ;;first arg is a URL
          (.-value (.getElementById js/document "token")) ;;CRSF token
          {:type :auto                                    ;;type determines Websocket vs. AJAX
           :wrap-recv-evs? false}))

(defn send! [& args]
  (if-let [send-fn (:send-fn @socket)] ;;mounts must be de-ref'd in clojurescript
    (apply send-fn args)
    (throw (ex-info "Couldn't send message, channel isn't open!!!"
                    {:message (first args)}))))

;;EFFECTS for impure thingz (actions)
(rf/reg-fx
 :ws/send!
 (fn [{:keys [message timeout callback-event]
       :or {timeout 30000}}]
   (if callback-event
     (send! message timeout #(rf/dispatch (conj callback-event %)))
     (send! message))))

;; structured similarly to the one on server
;; instead of interacting with DB, dispatches re-frame events based on type of message
(defmulti handle-message
  (fn [{:keys [id]} _]
    id))

(defmethod handle-message :message/add
  [_ msg-add-event]
  (rf/dispatch msg-add-event))

(defmethod handle-message :message/creation-errors
  [_ [_ response]]
  (rf/dispatch
   [:form/set-server-errors (:errors response)]))

;;----------------
;;Default Handlers (deal with Sente specific events - related to connection status)

(defmethod handle-message :chsk/handshake
  [{:keys [event]} _]
  (.log js/console "Connection Established: " (pr-str event)))

(defmethod handle-message :chsk/state
  [{:keys [event]} _]
  (.log js/console "State Changed: " (pr-str event)))

(defmethod handle-message :default
  [{:keys [event]} _]
  (.warn js/console "Unknown websocket message: " (pr-str event)))

;;----------------
;;Router

(defn receive-message!
  [{:keys [_id event] :as ws-message}]
  (do
    (.log js/console "Event Received: " (pr-str event))
    (handle-message ws-message event)))

(defstate channel-router
  :start (sente/start-chsk-router!
          (:ch-recv @socket)
          #'receive-message!)
  :stop (when-let [stop-fn @channel-router]
          (stop-fn)))
