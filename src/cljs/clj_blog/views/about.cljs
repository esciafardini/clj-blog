(ns clj-blog.views.about)

(defn about []
  [:div.column.is-two-thirds
   [:h1 "About The Blogg"]
   [:p "This Blogg was built using the Luminus framework. The frontend is Re-Frame and Reagent and the backend is a PostgreSQL instance.
       This is my first fully realized & deployed full stack project.  It is meant to be a learning resource for myself and other people who are new to
       Web Development and Functional Programming (like me). I relied heavily on the book Web Development In Clojure (3rd Edition), YouTube (Kelvin Mai & On The Code Again), my co-workers,
       and the Clojurians Slack.  Much gratitude to all."]
   [:p "I will be BLOGGing about the languages and frameworks that were used to build this BLOGG:"]
   [:ul
    [:li "Clojure(Script)"]
    [:li "Re-Frame"]
    [:li "Reagent"]
    [:li "Reitit"]
    [:li "Shadow-CLJS"]
    [:li "PostgreSQL"]
    [:li "Bulma CSS"]
    [:li "Selmer"]]
   [:img {:src "/img/bite.png" :style {:object-fit "cover" :width "200px" :height "100px"}}]
   [:div "This is the Blog about itself."]
   [:p "It's my playground."]
   [:marquee "No rules, just right"]])
