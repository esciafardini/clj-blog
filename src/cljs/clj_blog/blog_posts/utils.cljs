(ns clj-blog.blog-posts.utils
  (:require
   [clojure.string :as string]
   ["react-syntax-highlighter" :as rsh :default SyntaxHighlighter]
   ["react-syntax-highlighter/dist/esm/styles/hljs" :as hljs]
   [zprint.core :as zp]))

(defn sleep
  "Was going to use this to delay rendering of Loading indicator"
  [f ms]
  (js/setTimeout f ms))

(comment
  (sleep (fn [] (js/alert "5 seconds")) 5000))

(defn loading-indicator
  "Cute Sonic The Hedgehog loading indicator"
  []
  [:div {:style {:display "flex" :flex-direction "column" :align-items "center"}}
   [:div
    [:span.warning "~ PLEASE HOLD ~"]]
   [:img {:src "/img/loading.gif" :style {:margin-right "2rem" :width "10rem"}}]
   [:div
    [:span.warning  "~ PAGE LOADING ~"]]])

(defn inst->date-str
  "Takes an inst and returns a human-readable string after
   converting it into a js/Date object."
  [inst-ob]
  (.toLocaleDateString (js/Date. inst-ob) "en-US" #js {:dateStyle "long"}))

(defn format-code [string]
  (if (not= string "\n")
    (->> (zp/zprint string
                    {:parse-string? true
                     :map {:comma? false :sort? false :force-nl? true}
                     :vector {:respect-nl? true}
                     :list {:respect-nl? true}
                     :fn-map {"cond" :flow-body}})
         with-out-str
         (drop-last 1) ;remove added \n
         string/join)
    " "))

(comment
  (format-code "(def x 4) (def v 7)"))

(defn codeblock
  "Adds syntax highlighting and formatting to code snippets for rendering"
  ([code] (codeblock code true "clojure" hljs/nightOwl))
  ([code format?] (codeblock code format? "clojure" hljs/nightOwl))
  ([code format? language] (codeblock code format? language hljs/nightOwl))
  ([code format? language style]
   [:> SyntaxHighlighter
    {:language language
     :showLineNumbers true
     :style style}
    (if format?
      (format-code code)
      code)]))

(apply str (interpose "\n" ["the" "what" "fuck"]))

(defn codeblocks
  "yeah"
  [language & code-strs]
  [:> SyntaxHighlighter
   {:language language
    :showLineNumbers true
    :style hljs/nightOwl}
   (apply str (interpose "\n" (map #(format-code %) code-strs)))])

;; JS code
;;
; import SyntaxHighlighter from 'react-syntax-highlighter';
; import { docco } from 'react-syntax-highlighter/dist/esm/styles/hljs';
; const Component = () => {
;   const codeString = '(num) => num + 1';
;   return (
;     <SyntaxHighlighter language="javascript" style={docco}>
;       {codeString}
;     </SyntaxHighlighter>
;   );
; };

