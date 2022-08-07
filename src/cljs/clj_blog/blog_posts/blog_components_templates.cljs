(ns clj-blog.blog-posts.blog-components-templates 
  (:require
   [clojure.string :as string]))


(defn blog-post [{:keys [title date content-component tags]}]
  ;title, date, content (a component), tags
  [:div
   [:h1 title]
   [:div date]
   (content-component)
   [:p (string/join ", " tags)]])

