(ns clj-blog.views.resources)

(defn resources []
  [:div.column.is-two-thirds
   [:h1 "Useful Resources"]

   [:p
    "I have found these resources to be extremely helpful. These are mostly Clojure-oriented and beginner/intermediate friendly. I am sure this list will expand as time goes on..."]

   [:h3 "Books"]
   [:ul
    [:li
     [:a {:target "_blank"
          :href "https://www.braveclojure.com/clojure-for-the-brave-and-true/"} "Clojure For The Brave And True"]
     " - I might consider this required reading for Clojure beginners."]
    [:li
     [:a {:target "_blank"
          :href "https://pragprog.com/titles/dswdcloj3/web-development-with-clojure-third-edition/"} "Web Development In Clojure (3rd Edition)"]
     " - Web Dev with the Luminus framework."]
    [:li
     [:a {:target "_blank"
          :href "https://vpb.smallyu.net/[Type]%20books/The%20Little%20Schemer.pdf"} "The Little Schemer"]
     " - A great book for learning recursion in LISP."]]

   [:h3 "Tutorials"]
   [:ul
    [:li
     [:a {:target "_blank"
          :href "https://www.theodinproject.com/"} "The Odin Project"]
     " - The best resource for learning Web Development."]
    [:li
     [:a {:target "_blank"
          :href "https://ericnormand.me/clojure"} "Eric Normand"]
     " - Fountain of knowledge, great courses for beginners (not free, but payment plans and discounts offered)."]
    [:li
     [:a {:target "_blank"
          :href "https://lambdaisland.com/"} "Lambda Island"]
     " - Recently made available for free!"]
    [:li
     [:a {:target "_blank"
          :href "https://www.youtube.com/channel/UCKlYSDBb1KBcZyCRbniW1ig"} "On The Code Again"]
     " - Fun & Informative."]
    [:li
     [:a {:target "_blank"
          :href "https://www.youtube.com/c/KelvinMai"} "Kelvin Mai"]
     " - Good courses, but fast-paced."]]

   [:h3 "Miscellaneous"]
   [:ul
    [:li
     [:a {:target "_blank"
          :href "https://clojurians.slack.com/?redir=%2Fmessages"} "Clojurians Slack"]
     " - Ask them anything."]
    [:li
     [:a {:target "_blank"
          :href "https://theclouncil.com/"} "The Clouncil"]
     " - Meetups for Beginners to ask Clojure questions to experts."]
    [:li
     [:a {:target "_blank"
          :href "https://podcasts.apple.com/us/podcast/lost-in-lambduhhs/id1566620738"} "Lost In Lambduhhs Podcast"]
     " - Great Clojure talks."]
    [:li
     [:a {:target "_blank"
          :href "https://github.com/functional-koans/clojure-koans"} "Clojure Koans"]
     " - Zen themed clojure practice."]
    [:li
     [:a {:target "_blank"
          :href "https://exercism.org/"} "Exercism"]
     " - Coding challenges with Clojure track and free mentorship."]
    [:li
     [:a {:target "_blank"
          :href "https://4clojure.oxal.org/"} "4Clojure"]
     " - Coding challenges in Clojure."]
    [:li
     [:a {:target "_blank"
          :href "https://www.evalapply.org/"} "Eval/Apply Blog"]
     " - Huge inspiration for this Blogg."]]])
