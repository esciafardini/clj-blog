(ns clj-blog.core
  (:require
   [ajax.core :refer [GET]]
   [clj-blog.blog-posts.blog-components-01 :refer [first-entry hljs-themes]]
   [clj-blog.validation :refer [validate-message]]
   [clojure.string :as string]
   [mount.core :as mount]
   [re-frame.core :as rf]
   [reagent.core :as r]
   [reagent.dom :as dom])
  (:import [goog.date DateTime]))

(defn inst->date-str [inst-ob]
  (.toLocaleDateString (js/Date. inst-ob) "en-US" #js {:dateStyle "long"}))


(comment

; new Date().toLocaleDateString('en-US', {dateStyle: 'long'})
; => 'August 10, 2022'


(inst->date-str #inst "2015-10-13T05:00:00.000-00:00")

;EUREEEKAH
(.toLocaleDateString (js/Date. #inst "2015-10-13T05:00:00.000-00:00") "en-US" #js {:dateStyle "long"})

(DateTime.  (js/Date. #inst "2015-10-13T05:00:00.000-00:00"))
(DateTime. (js/Date. "October 13, 2015"))

(extend-type DateTime
  IPrintWithWriter
  (-pr-writer [obj writer _opts]
    (let [normalize (fn [n len]
                      (loop [ns (str n)]
                        (if (< (count ns) len)
                          (recur (str "0" ns))
                          ns)))]
      (write-all writer
;                 "#inst \""
                 \"
                 (str (.getUTCFullYear obj))             "-"
                 (normalize (inc (.getUTCMonth obj)) 2)  "-"
                 (normalize (.getUTCDate obj) 2)         "T"
                 (normalize (.getUTCHours obj) 2)        ":"
                 (normalize (.getUTCMinutes obj) 2)      ":"
                 (normalize (.getUTCSeconds obj) 2)      "."
                 (normalize (.getUTCMilliseconds obj) 3) "-"
                 \"
;                 "00:00\""
                 ))))
  
  )


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
 :app/initialize
 (fn [_ _]
   {:db {:blog-posts/loading? true}
    :dispatch [:blog-posts/load]}))

(rf/reg-fx
 :ajax/get
 (fn [{:keys [url success-event error-event success-path]}] ;;success-path allows us to specify a path in the response we'd like to pas to our success-event
   (GET url
     (cond-> {:headers {"Accept" "application/transit+json"}}
       ;;building a map, conditionally adds handler & error handler
       success-event (assoc :handler
                            #(rf/dispatch
                              (conj success-event
                                    (if success-path
                                      (get-in % success-path)
                                      %))))
       error-event (assoc :error-handler
                          #(rf/dispatch
                            (conj error-event %)))))))

(rf/reg-event-fx
 :blog-posts/load
 (fn [{:keys [db]} _]
   {:db (assoc db :blog-posts/loading? true)
    :ajax/get {:url "/api/blog-posts"
               :success-path [:blog-posts]
               :success-event [:blog-posts/set]}}))

(rf/reg-event-db
 :blog-posts/set
 (fn [db [_ blog-posts]]
   (-> db
       (assoc :blog-posts/loading? false
              :blog-posts/list blog-posts))))

(rf/reg-sub
 :blog-posts/loading?
 (fn [db _]
   (:blog-posts/loading? db)))

(rf/reg-sub
 :blog-posts/list
 (fn [db _]
   (:blog-posts/list db [])))

(defn home []
  (let [blog-posts (rf/subscribe [:blog-posts/list])]
    (fn []
      (let [_ (.log js/console @blog-posts)]
        [:div.content>div.columns.is-centered>div.column.is-two-thirds
         (for [blog-post @blog-posts]
           [:<>
            [:div (:title blog-post)]
            [:div (str (:tags blog-post))]
            [:div (inst->date-str (:date_created blog-post))]])
         [first-entry]]))))

(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (.log js/console "Mounting Components...")
  (dom/render [#'home] (.getElementById js/document "content"))
  (.log js/console "Components Mounted!"))

(defn init!
  "Actions involving data are moved to a Re-Frame event [:app/initialize]"
  []
  (.log js/console "Here she comes...")
  (mount/start)
  (rf/dispatch [:app/initialize])
  (mount-components))
