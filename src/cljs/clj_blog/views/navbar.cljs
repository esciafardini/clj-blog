(ns clj-blog.views.navbar
  (:require
   [reagent.core :as r]))

(defn nav-item [on-click href link-text]
  [:a.navbar-item
   {:on-click on-click
    :href href}
   link-text])

(defn navbar []
  (let [burger-active (r/atom false)
        on-click #(swap! burger-active not)]
    (fn []
      [:nav.navbar.is-dark
       [:div.container
        [:div.navbar-brand
         [:a.navbar-item
          {:href "/"
           :style {:font-family "VT323" :font-size "28px" :font-weight "bold"}}
          "FP BLOGG"]
         [:span.navbar-burger.burger
          {:data-target "nav-menu"
           :on-click on-click
           :class (when @burger-active "is-active")}
          [:span]
          [:span]
          [:span]]]
        [:div#nav-menu.navbar-menu
         {:class (when @burger-active "is-active")}
         [:div.navbar-start
          [nav-item on-click "/" "Home"]
          [nav-item on-click "/about" "About"]
          [nav-item on-click "/resources" "Resources"]]]]])))
