(ns clj-blog.ajax
  (:require
   [ajax.core :refer [GET]]
   [re-frame.core :as rf]))

; :ajax/get
; {:url "/api/blog-posts"
;  :success-path [:blog-list]
;  :success-event [:blog-list/set]}

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
