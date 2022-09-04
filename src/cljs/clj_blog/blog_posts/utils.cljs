(ns clj-blog.blog-posts.utils
  (:require
   [clojure.string :as string]
   ["react-syntax-highlighter" :as rsh :default SyntaxHighlighter]
   ["react-syntax-highlighter/dist/esm/styles/hljs" :as hljs]
   [zprint.core :as zp]))

(defn inst->date-str
  "Takes an inst and returns a human-readable string after
   converting it into a js/Date object."
  [inst-ob]
  (.toLocaleDateString (js/Date. inst-ob) "en-US" #js {:dateStyle "long"}))

(defn format-code [string]
  (->> (zp/zprint string
                  {:parse-string? true
                   :map {:comma? false :sort? false :force-nl? true}
                   :vector {:respect-nl? true}
                   :list {:respect-nl? true}})
       with-out-str
       (drop-last 1) ;remove added \n
       string/join))

(format-code "(def x 4) (def v 7)")

(defn codeblock
  "Adds syntax highlighting and formatting to code snippets for rendering"
  ([code] (codeblock code true "clojure" hljs/nightOwl))
  ([code format?] (codeblock code format? "clojure" hljs/nightOwl))
  ([code format? language style]
   [:> SyntaxHighlighter
    {:language language
     :showLineNumbers true
     :style style}
    (if format?
      (format-code code)
      code)]))

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

