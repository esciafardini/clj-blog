(ns clj-blog.blog-posts.blog-themes
  (:require
   ["react-syntax-highlighter/dist/esm/styles/hljs" :as hljs]
   [clj-blog.blog-posts.utils :refer [codeblock]]))

;TODO move this ish to its own ns
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
