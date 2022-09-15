(ns clj-blog.blog-posts.blog-components-02
  (:require
   [clj-blog.blog-posts.utils :refer [codeblock]]
   [clojure.string :as string]))

(def users
  [{:first-name "Alan"
    :last-name "Perlis"
    :online? false}
   {:first-name "Jan"
    :last-name "Rickles"
    :online? true}
   {:first-name "Dork"
    :last-name "Fish"
    :online? false}
   {:first-name "Daniel"
    :last-name "Daniels"
    :online? true}
   {:first-name "Pemmy"
    :last-name "Scud"
    :online? false}
   {:first-name "Dick"
    :last-name "Taargus"
    :online? true}
   {:first-name "Daniel"
    :last-name "Taargus"
    :online? true}
   {:first-name "Rich"
    :last-name "Taargus"
    :online? false}])

(filter :online? users)

(filter (comp #{"Scud" "Taargus"} :last-name) users)

(defn idioms []
  [:<>
   [:p [:em "In which the fledgling Clojure developer recounts Clojure idioms that he's picked up on the job..."]]
   [:h4 "Filtering Vectors of Maps"]
   [:p "In my experience, it's very common to be dealing with a vector of maps (i.e. honeySQL data)."]
   [:p "Here is an example of what we might see:"]
   [codeblock
    (pr-str
     '(def users
        [{:first-name "Alan"
          :last-name "Perlis"
          :online? false}
         {:first-name "Jan"
          :last-name "Rickles"
          :online? true}
         {:first-name "Dork"
          :last-name "Fish"
          :online? false}
         {:first-name "Daniel"
          :last-name "Daniels"
          :online? true}
         {:first-name "Pemmy"
          :last-name "Scud"
          :online? false}
         {:first-name "Dick"
          :last-name "Taargus"
          :online? true}
         {:first-name "Daniel"
          :last-name "Taargus"
          :online? true}
         {:first-name "Rich"
          :last-name "Taargus"
          :online? false}]))]

   [:p "Clojure makes it really easy to filter these users using predicate functions.  When I first tried to filter a vector of maps like this I did something goofy like this:"]
   [codeblock
    (pr-str '(filter (fn [user-map] (= (:online? user-map) true)) users))]
   [:p "This works fine for filtering down to users that are online..."]
   [codeblock
    (pr-str
     '({:first-name "Jan", :last-name "Rickles", :online? true}
       {:first-name "Daniel", :last-name "Daniels", :online? true}
       {:first-name "Dick", :last-name "Taargus", :online? true}
       {:first-name "Daniel", :last-name "Taargus", :online? true}))]
   [:p "But there is an idiomatic and terse way to do so:"]
   [codeblock
    (pr-str '(filter :online? users))]
   [:p "Wow, so clean and fresh."]
   [codeblock
    (pr-str
     '(:online? {:first-name "Jeff"
                 :last-name "Dankiel"
                 :online? false}))]
   [:p "This is the function called on each 'user-map' when calling filter.  The above codeblock will return
       false and therefore be filtered out of the collection that is returned.  This is great for booleans / exists
       filtering, but what if we want something a little more complicated?  Something like 'Only users with last name Taargus OR Rickles'?"]
   [:p "Good news: Clojure also makes this easy and terse to write."]
   [codeblock
    (pr-str
     '(filter (comp #{"Rickles" "Taargus"} :last-name) users))]
   [:p "This broke my junior developer brain when I first saw it, but I think it's pretty clean & fresh now that I understand it.
       The clojure fn 'comp' returns a function that is 'composed' of the functions you pass to it.  What happens here?"]
   [:div "For each user map...."]
   [:ol
    [:li "Call :last-name on the user-map"]
    [:li "Call the function #{\"Rickles\" \"Taargus\"} on the return value of #1"]]
   [:p "EFFECTIVELY:"]
   [codeblock
    "(#{\"Rickles\" \"Taargus\"}\n  (:last-name \n    {:first-name \"Jocko\"\n     :last-name \"Taargus\"\n     :online? false}))\n;=> \"Taargus\""
    false]
   [:p "Using set as a function was difficult for me to wrap my head around.  The way I think about it is like asking 'is equal to one of?' with the argument being any value.
       So when calling this function:"]
   [codeblock
    (pr-str
     '(#{"Taargus" "Rickles"} "Rickles"))]
   [:p "It reads like so:"]
   [:p "Is the value \"Rickles\" one of the values in set: #{\"Taargus\" \"Rickles\"}?"]
   [:p "Coming soon: (seq, not empty?) and other idioms."]
   #_[:h4 "Use seq, not empty?"]])

;;;;;Learning Clojurescript

(def alan-p
  {:first-name "Alan"
   :last-name "Perlis"
   :online? false})

(defn nickname [entity]
  (or (:nickname entity)
      (->> entity
           ((juxt :first-name :last-name))
           (string/join " The Snake "))))

(defn bold [child]
  [:strong child])

(bold (nickname alan-p))

(defn concat-strings [s1 s2]
  (string/trim (str s1 " " s2)))

(conj {:jim "dale"} [:a 1])

(defn with-class [dom class-name]
  (if (map? (second dom))
    (update-in dom [1 :class] concat-strings class-name)
    (let [[tag & children] dom]
      (vec
       (concat [tag {:class class-name}] children)))))

(with-class [:div "Why, hello there."] "bold-ctx")
