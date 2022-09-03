(ns clj-blog.blog-posts.blog-components-templates
  (:require
   [clj-blog.blog-posts.utils :as utils]
   [clj-blog.blog-posts.component-lookup :refer [component-lookup]]))

(defn blog-post-container []
  (fn [{:keys [title date_created component_function home-page?]}]
    (let [component (get component-lookup component_function)]
      [:div.blogpost
       [:h1 title]
       (if home-page?
         [:p {:style {:margin-top "2rem"}}]
         [:p.date (utils/inst->date-str date_created)])
       [component]])))
