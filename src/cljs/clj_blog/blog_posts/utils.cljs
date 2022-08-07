(ns clj-blog.blog-posts.utils
  (:require
   [clojure.string :as string]
   ["react-syntax-highlighter" :as rsh :default SyntaxHighlighter]
   ["react-syntax-highlighter/dist/esm/styles/hljs" :as hljs]
   #_["react-syntax-highlighter/dist/esm/styles/prism" :as przm]
   [zprint.core :as zp]))

(defn zprint-code [string]
  (->> (zp/zprint string
                  {:parse-string? true})
       with-out-str
       (drop-last 2)
       string/join))

(defn codeblock
  "Adds syntax highlighting and formatting to code snippets for rendering"
  ([code] (codeblock code "clojure"))
  ([code language]
   [:> SyntaxHighlighter
    {:language language
     :showLineNumbers true
     :style hljs/androidstudio}
    (zprint-code code)]))

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

