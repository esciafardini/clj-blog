(ns clj-blog.blog-posts.blog-components-01
  (:require
   ["react-syntax-highlighter/dist/esm/styles/hljs" :as hljs]
   [clj-blog.blog-posts.utils :refer [codeblock format-code]]))

(def theme-maps
  [{:symbol hljs/a11yDark
    :name "a11yDark"}
   {:symbol hljs/a11yLight
    :name "a11yLight"}
   {:symbol hljs/agate
    :name "agate"}
   {:symbol hljs/anOldHope
    :name "anOldHope"}
   {:symbol hljs/androidstudio
    :name "androidstudio"}
   {:symbol hljs/arduinoLight
    :name "arduinoLight"}
   {:symbol hljs/arta
    :name "arta"}
   {:symbol hljs/ascetic
    :name "ascetic"}
   {:symbol hljs/atelierCaveDark
    :name "atelierCaveDark"}
   {:symbol hljs/atelierCaveLight
    :name "atelierCaveLight"}
   {:symbol hljs/atelierDuneDark
    :name "atelierDuneDark"}
   {:symbol hljs/atelierDuneLight
    :name "atelierDuneLight"}
   {:symbol hljs/atelierEstuaryDark
    :name "atelierEstuaryDark"}
   {:symbol hljs/atelierEstuaryLight
    :name "atelierEstuaryLight"}
   {:symbol hljs/atelierForestDark
    :name "atelierForestDark"}
   {:symbol hljs/atelierForestLight
    :name "atelierForestLight"}
   {:symbol hljs/atelierHeathDark
    :name "atelierHeathDark"}
   {:symbol hljs/atelierHeathLight
    :name "atelierHeathLight"}
   {:symbol hljs/atelierLakesideDark
    :name "atelierLakesideDark"}
   {:symbol hljs/atelierLakesideLight
    :name "atelierLakesideLight"}
   {:symbol hljs/atelierPlateauDark
    :name "atelierPlateauDark"}
   {:symbol hljs/atelierPlateauLight
    :name "atelierPlateauLight"}
   {:symbol hljs/atelierSavannaDark
    :name "atelierSavannaDark"}
   {:symbol hljs/atelierSavannaLight
    :name "atelierSavannaLight"}
   {:symbol hljs/atelierSeasideDark
    :name "atelierSeasideDark"}
   {:symbol hljs/atelierSeasideLight
    :name "atelierSeasideLight"}
   {:symbol hljs/atelierSulphurpoolDark
    :name "atelierSulphurpoolDark"}
   {:symbol hljs/atelierSulphurpoolLight
    :name "atelierSulphurpoolLight"}
   {:symbol hljs/atomOneDarkReasonable
    :name "atomOneDarkReasonable"}
   {:symbol hljs/atomOneDark
    :name "atomOneDark"}
   {:symbol hljs/atomOneLight
    :name "atomOneLight"}
   {:symbol hljs/codepenEmbed
    :name "codepenEmbed"}
   {:symbol hljs/colorBrewer
    :name "colorBrewer"}
   {:symbol hljs/darcula
    :name "darcula"}
   {:symbol hljs/dark
    :name "dark"}
   {:symbol hljs/defaultStyle
    :name "defaultStyle"}
   {:symbol hljs/docco
    :name "docco"}
   {:symbol hljs/dracula
    :name "dracula"}
   {:symbol hljs/far
    :name "far"}
   {:symbol hljs/foundation
    :name "foundation"}
   {:symbol hljs/githubGist
    :name "githubGist"}
   {:symbol hljs/github
    :name "github"}
   {:symbol hljs/gml
    :name "gml"}
   {:symbol hljs/googlecode
    :name "googlecode"}
   {:symbol hljs/gradientDark
    :name "gradientDark"}
   {:symbol hljs/gradientLight
    :name "gradientLight"}
   {:symbol hljs/grayscale
    :name "grayscale"}
   {:symbol hljs/gruvboxDark
    :name "gruvboxDark"}
   {:symbol hljs/gruvboxLight
    :name "gruvboxLight"}
   {:symbol hljs/hopscotch
    :name "hopscotch"}
   {:symbol hljs/hybrid
    :name "hybrid"}
   {:symbol hljs/idea
    :name "idea"}
   {:symbol hljs/irBlack
    :name "irBlack"}
   {:symbol hljs/isblEditorDark
    :name "isblEditorDark"}
   {:symbol hljs/isblEditorLight
    :name "isblEditorLight"}
   {:symbol hljs/kimbieDark
    :name "kimbieDark"}
   {:symbol hljs/kimbieLight
    :name "kimbieLight"}
   {:symbol hljs/lightfair
    :name "lightfair"}
   {:symbol hljs/lioshi
    :name "lioshi"}
   {:symbol hljs/magula
    :name "magula"}
   {:symbol hljs/monoBlue
    :name "monoBlue"}
   {:symbol hljs/monokaiSublime
    :name "monokaiSublime"}
   {:symbol hljs/monokai
    :name "monokai"}
   {:symbol hljs/nightOwl
    :name "nightOwl"}
   {:symbol hljs/nnfxDark
    :name "nnfxDark"}
   {:symbol hljs/nnfx
    :name "nnfx"}
   {:symbol hljs/nord
    :name "nord"}
   {:symbol hljs/obsidian
    :name "obsidian"}
   {:symbol hljs/ocean
    :name "ocean"}
   {:symbol hljs/paraisoDark
    :name "paraisoDark"}
   {:symbol hljs/paraisoLight
    :name "paraisoLight"}
   {:symbol hljs/purebasic
    :name "purebasic"}
   {:symbol hljs/qtcreatorDark
    :name "qtcreatorDark"}
   {:symbol hljs/qtcreatorLight
    :name "qtcreatorLight"}
   {:symbol hljs/railscasts
    :name "railscasts"}
   {:symbol hljs/rainbow
    :name "rainbow"}
   {:symbol hljs/routeros
    :name "routeros"}
   {:symbol hljs/shadesOfPurple
    :name "shadesOfPurple"}
   {:symbol hljs/solarizedDark
    :name "solarizedDark"}
   {:symbol hljs/solarizedLight
    :name "solarizedLight"}
   {:symbol hljs/srcery
    :name "srcery"}
   {:symbol hljs/stackoverflowDark
    :name "stackoverflowDark"}
   {:symbol hljs/stackoverflowLight
    :name "stackoverflowLight"}
   {:symbol hljs/sunburst
    :name "sunburst"}
   {:symbol hljs/tomorrowNightBlue
    :name "tomorrowNightBlue"}
   {:symbol hljs/tomorrowNightBright
    :name "tomorrowNightBright"}
   {:symbol hljs/tomorrowNightEighties
    :name "tomorrowNightEighties"}
   {:symbol hljs/tomorrowNight
    :name "tomorrowNight"}
   {:symbol hljs/tomorrow
    :name "tomorrow"}
   {:symbol hljs/vs
    :name "vs"}
   {:symbol hljs/vs2015
    :name "vs2015"}
   {:symbol hljs/xcode
    :name "xcode"}
   {:symbol hljs/xt256
    :name "xt256"}
   {:symbol hljs/zenburn
    :name "zenburn"}])

(defn color-palette []
  [:<>
   (for [theme theme-maps]
     ^{:key theme}
     [:div
      [:h3 (:name theme)]
      [codeblock
       (pr-str
        '(defn render
           "renders the HTML template located relative to resources/html"
           [request template & [params]]
           (content-type
            (ok
             (parser/render-file
              template
              (assoc params
                     :page template
                     :csrf-token *anti-forgery-token*)))
            "text/html; charset=utf-8")))
       true "clojure" (:symbol theme)]])])

(defn hljs-themes []
  [:div.blogpost
   [color-palette]])

(defn first-entry []
  [:<>
   [:p "As a relatively new developer, I am very interested in becoming
       a relatively good developer."]
   [:p "The question keeps coming up: \"How can I get better at software development?\""]
   [:p "Many nights spent searching googul and reddet for answers."]
   [:p "All the responses I've come across can be condensed into two <li>s:"]
   [:ol
    [:li [:b "Build Projects"]]
    [:li [:b "Read Documentation & Books"]]]
   [:p "So here I am, building a project (this blog) where I will document and recount my experiences building projects, reading books, and reading documentation."]
   [:<>
    [codeblock "(defn blogg \n \"I think I know why I exist?\" \n [concepts] \n (for [concept concepts]\n  (blogg-post concept)))\n\n; Exotic gradient theme used in celebration of my first blog entry"
     false "clojure" hljs/gradientLight]]])

(defn higher-order-functions []
  [:div.blogpost
   [:p.date "April 17th, 2022"]
   [:h1 "4Clojure No. 46 Solution"]
   [:p "This one I found to be tricky and somewhat revealing...."]
   [:div.code
    ";Write a higher-order function which 
      ;flips the order of the arguments of an input function.
      \n
      \t(= 3 ((_ nth) 2 [1 2 3 4 5]))\n\n
      \t(= true ((_ >) 7 8))\n\n
      \t(= 4 ((__ quot) 2 8))\n\n
      \t(= [1 2 3] ((_ take) [1 2 3 4 5] 3))
      \n"]
   [:p "Being that functions are first class citizens in Clojure, we can pass in functions to functions
        and return new functions......uhhhh"]
   [:p "Let's do a visualization exercise to explain...envision a computer monitor....visualize yourself reading a blog post....."]
   [:p "If we look at where the function we are writing is...it should only take one parameter."]
   [:p "So what is the parameter?  We have nth, >, quot, and take."]
   [:div.code
    ";so it takes a function f.....\n
      (fn [f] (.......)) \n
      ;But then what?

      ((fn [f] (...something....) take) [1 2 3 4 5] 3)
      "]

   [:p "It looks like....we want to take that function into our black box....and turn it into a slightly janked version of itself."]
   [:p "What does janked mean?"]
   [:p "In this context the terminology indicates a directional acyclical graph shift via parameterization of schematic index order traversal......"]
   [:p "Just kidding it means the args get reversed."]
   [:p "Return the function so that when it is called, it takes the second argument first & the first argument second."]
   [:p "This is a good exercise in functional thinking OKAY:"]
   [:div.code
    ";if I give you the func, are you gonna take it?
      ((fn [f] (fn [arg1 arg2] (f arg2 arg1)) take) [1 2 3 4 5] 3) ;=> (1 2 3)"]

   [:p "WHOA"]
   [:p "What happened? The outer function takes the function - and that makes sense because in each example listed in 4clojure, the fn we are writing takes a single function"]
   [:p "The next part is a little strange to think about - but it will begin to make sense if you keep pulling clumps of your hair out and staring at the screen."]
   [:p "What is returned ultimately is a NEW function comprised of the OLD function passed into it."]
   [:div.code
    ";Let me show you what I mean:\n
     
      ((fn [n coll] (nth coll n)) 2 [1 2 3 4 5])\n
      ((fn [x y] (> y x)) 7 8)\n
      ((fn [coll n] (take n coll)) [1 2 3 4 5] 3)\n
      ((fn [x y] (quot y x)) 2 8)
      \n"]

   [:p "CAN YOU FEEL THE POWER OF FUNCTIONS? OH MY GOD"]])
