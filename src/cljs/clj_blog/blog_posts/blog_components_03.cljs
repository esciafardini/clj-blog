(ns clj-blog.blog-posts.blog-components-03
  (:require
   [clj-blog.blog-posts.utils :refer [codeblock]]))

(def sql-examples
  {:generate-calendar
   "\\set start '2022-02-01';
   select generate_series(date :'start',
   date :'start' + interval '1 month'
   - interval '1 day',
   interval '1 day');"})

(defn art-of-sql []
  [:<>
   [:div "How to make a calendar view to query against:"]
   [codeblock (get sql-examples :generate-calendar) false "sql"]])
