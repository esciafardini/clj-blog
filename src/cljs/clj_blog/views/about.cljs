(ns clj-blog.views.about)

(defn about []
  [:<>
   [:img {:src "/img/bite.png" :style {:object-fit "cover" :width "200px" :height "100px"}}]
   [:p "This is the Blog about itself."]
   [:p "It's my playground."]
   [:marquee "No rules, just right"]])
