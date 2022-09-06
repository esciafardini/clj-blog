(ns clj-blog.views.about)

(defn about []
  [:div.column.is-two-thirds
   [:h1 "About The Blogg"]
   [:p "This Blogg was built with Clojure(Script) using the Luminus framework. The frontend is Re-Frame and Reagent and the backend is a PostgreSQL instance.
       This is my first fully realized & deployed full stack project.  It is meant to be a learning resource for myself and other people who are new to
       Web Development and Functional Programming (like me). I relied heavily on the book Web Development In Clojure (3rd Edition), YouTube (Kelvin Mai & On The Code Again), my co-workers,
       and the Clojurians Slack.  Much gratitude to all."]
   [:p "This Blogg is made up of:"]
   [:ul
    [:li "Re-Frame"]
    [:li "Reagent"]
    [:li "Reitit"]
    [:li "Malli"]
    [:li "Shadow-CLJS"]
    [:li "Bulma CSS"]
    [:li "Selmer"]
    [:li "Migratus"]
    [:li "Mount"]]
   [:img {:src "/img/bite.png" :style {:object-fit "cover" :width "200px" :height "100px"}}]
   [:div "This is the Blog about itself."]
   [:p "It's my playground."]
   [:marquee "No rules, just right"]])
