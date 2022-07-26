(ns clj-blog.blog-posts.blog-components-01
  (:require
   ["react-syntax-highlighter/dist/esm/styles/hljs" :as hljs]
   [clj-blog.blog-posts.utils :refer [codeblock]]
   [clojure.string :as string]
   [clojure.walk :as walk]
   [goog.string.linkify :refer [EMAIL_RE_]])
  (:import
   [goog.math Integer]))

(defn first-entry []
  [:div
   [:p "As a relatively new developer, I am very interested in becoming
       a relatively good developer."]
   [:p "The question keeps coming up: \"How can I get better at software development?\""]
   [:p "Many nights spent searching googul and reddet for answers."]
   [:p "All of the knowledge I've attained from these late night excursions can be condensed into two <li>s:"]
   [:ol
    [:li [:b "Build Projects"]]
    [:li [:b "Read Documentation & Books"]]]
   [:p "So here I am, building a project (this blog) where I will document and recount my experiences building projects, reading books, and reading documentation."]
   [:<>
    [codeblock "(defn blogg \n \"I think I know why I exist?\" \n [concepts] \n (for [concept concepts]\n  (blogg-post concept)))\n\n;; Exotic gradient theme used in\n;; celebration of my first blog entry"
     false "clojure" hljs/gradientLight]]])

(defn higher-order-functions []
  [:<>
   [:p "This one taught me a valuable lesson."]
   [codeblock
    ";Write a higher-order function which flips the order \n;of the arguments of an input function.\n\n(= 3 ((____ nth) 2 [1 2 3 4 5]))\n\n(= true ((____ >) 7 8))\n\n(= 4 ((____ quot) 2 8))\n\n(= [1 2 3] ((____ take) [1 2 3 4 5] 3))"
    false]
   [:p "Being that functions are first class citizens in Clojure, we can pass functions into functions and we can return new functions.  This has always felt a little tricky to me, so I want to slow down."]
   [:p "If we look at the ____ space where our function will live...we see it only takes one parameter."]
   [:p "And what type of parameter will the function take?  We have nth, >, quot, and take.  These are all Clojure functions.  We are writing a function that takes a function as an argument."]
   [:p "It's important to note the arity of these functions.  Here is how they would normally be called:"]
   [codeblock
    "(take 3 [1 2 3 4 5])\n;=>\n'(1 2 3)\n\n(nth [1 2 3 4 5] 2)\n;=>\n3\n\n(quot 8 2)\n;=>\n4\n\n(> 8 7)\n;=>\ntrue"
    false]
   [:p "If we look at the 4Clojure problem, we see that the arguments are being passed into the function in reverse order.  So what do we want to do to our function?"]
   [:p "Something like turning the function (f [a b] ...) into a function (f [b a] ...) where the result is the same, but the arguments are swapped."]
   [:p "This is a good exercise in functional thinking:"]
   [codeblock
    "((fn [f] (fn [arg1 arg2] (f arg2 arg1)) take) [1 2 3 4 5] 3)\n;=>\n'(1 2 3)"
    false]
   [:p "What happened? The outer function takes the function - and that makes sense because in each example listed in 4Clojure, the fn we are writing takes a single function.
        What is returned ultimately is a" [:em " new "] "function comprised of the" [:em " old "] "function passed into it.  It's always a little clearer to me to see fns in a defn:"]
   [codeblock
    "(defn reverse-args [f]
    (fn [a b] (f b a)))"]
   [:p "A function comes in & a function goes out."]
   [:p "This solution works fine for two args, but let's take it a step further and see about reversing multiple args."]
   [:p "It's common convention to use [& args] for variadic functions (functions that take a variable number of parameters).  This converts the arguments coming into the function into a sequence.
       The cool thing is that this sequence is like any other Clojure sequence & can be manipulated as such."]
   [:p "Try this out in your REPL if that doesn't make sense:"]
   [codeblock
    "(fn [& args] (println args) (println (reverse args))) 1 2 3 4)]\n;=>\n;(out): '(1 2 3 4)\n;(out): '(4 3 2 1)"
    false]
   [:p "The function took in four arguments, and the & symbol turned them into a sequence.  The sequence was reversed in the example above, but any functions that can be called on sequences can be
       called on args - because args is a sequence.  With that in mind, we can deal with multi-arity argument flipping:"]
   [codeblock
    "(defn reverse-args [f]\n  (fn [& args] (apply f (reverse args))))\n\n(=\n (range 0 20 2)\n ((reverse-args range) 2 20 0))\n;=> true"
    false]
   [:p "I can feel the power of functions. I understand the need for a higher order."]])

(defn ^:private string->hiccup-with-links
  [s]
  (let [emails (->> (re-seq EMAIL_RE_ s)
                    (mapv first)
                    (mapv (fn [email]
                            {:start (string/index-of s email)
                             :size (count email)
                             :text email
                             :href (str "mailto:" email)})))]
    (if (seq emails)
      ;; This loop is a bit tricky.
      ;; Basically, we need to "break" the link out of the string s.
      ;; We want actual hiccup, like [:<> "Hello, " [:a {:href "mailto:bob@email.com"} "bob@email.com"] " is my email address!"]
      ;; So this loop does that, by looping over the emails we found.
      ;; For each email:
      ;; * find any "pre-text" (text preceding curr email, but post-ceding prev email, if any)
      ;; * creating the link
      ;; * add both the pre-text and link to result
      ;; After we've done that for all the emails, the only thing left to do is to add any remaining
      ;; text after the last email to result, and return it.
      (loop [prev nil
             curr (first emails)
             rem (rest emails)
             result [:<>]]
        (if (seq curr)
          (let [{:keys [start href text]} curr
                {prev-start :start prev-size :size
                 :or {prev-start 0 prev-size 0}} prev
                pre-text (subs s (+ prev-start prev-size) start)]
            (recur curr (first rem) (rest rem) (into result [pre-text [:a {:href href} text]])))
          (if (seq prev)
            (let [{:keys [start size]} prev]
              (conj result (subs s (+ start size))))
            result)))
      ;; No emails to linkify, just return s as-is
      s)))

(defn ^:private linkify-hiccup
  [hiccup]
  #_hiccup
  (walk/postwalk (fn [node]
                   (if (string? node)
                     (string->hiccup-with-links node)
                     node))
                 hiccup))

(defn practical-google-closure []
  [:<>
   [:h2 "Google Closure With Clojurescript"]
   [:p "The Google Closure library is available within any clojurescript project."]
   [:p "I haven't really found a good resource that shows practical application & usage of Closure classes within ClojureScript - so I'm writing this post as documentation for myself."]
   [:p "Google Closure provides 'modules' with classes, functions, and variables that we can leverage in our clojurescript projects."]
   [:p "To use a Closure " [:em "class: "] "use the 'import' keyword:"]
   [codeblock
    "(ns closure-example.core
      (:import [goog.net XhrIo]
               [goog.math Integer Long Vec2 Vec3]))"]
   [:p "The import form is specific to the use case of importing classes from the host libraries (google closure modules)."]
   [:p "To use a specific Closure " [:em "var "] "or" [:em " function "] "- you can use the 'require' keyword.  The 'as' keyword allows us to use other vars and functions within the linkify module:"]
   [codeblock
    "(ns closure-example.core
    (:require
       [goog.string.linkify :refer [EMAIL_RE_] :as linkify]))"]

   [:p "Calling functions from imported Closure classes: "]
   [codeblock
    "(Long. 4 6)\n;=> #<25769803780> \n(Integer.fromString \"10\") \n;=> #<10> "
    false]

   [:p "It is also possible to import in-line with js prefix.  This bypasses the need to require/import the class."]
   [:p "I don't know if this good practice or idiomatic - but here is an example:"]
   [codeblock
    "(new js/goog.math.Long 4 6)\n;=> #<25769803780>\n\n(js/goog.math.Integer.fromString \"12\")\n;=> #<12>"
    false]

   [:p "TODO: turn these into code snippets and implement the linkify emails"]
   [:p (str (Integer.fromString "12"))]
   [:p
    (str (re-seq EMAIL_RE_ "Well what  haha annie@gmus.net is this utur@aol.com booter?"))]
   [:hr]])

(defn flex-box-navbar-example []
  [:div.fb-menu-container
   [:div.fb-menu
    [:div.fb-title "FLEXBOX BLOGG"]
    #_[:div]
    [:div.fb-links
     [:div.fb-signup "Sign Up"]
     [:div.fb-login "Login"]]]])

(defn flex-box-header-example []
  [:div {:class "fb-header-container"}
   [:div {:class "fb-header"}
    [:div {:class "fb-subscribe"} "Subscribe ▾"]
    [:div {:class "fb-logo"}
     [:div "( ͡°( ͡° ͜ʖ( ͡° ͜ʖ ͡°)ʖ ͡°) ͡°)"]]
    [:div {:class "fb-social"}
     [:img {:src "/img/social-icons.svg"}]]]])

(defn flex-box-wrapping-example []
  [:div {:class "fb-photo-grid-container"}
   [:div {:class "fb-photo-grid"}
    [:div {:class "fb-photo-grid-item first-item"}
     [:img {:src "/img/one.svg"}]]
    [:div {:class "fb-photo-grid-item"}
     [:img {:src "/img/two.svg"}]]
    [:div {:class "fb-photo-grid-item"}
     [:img {:src "/img/three.svg"}]]
    [:div {:class "fb-photo-grid-item"}
     [:img {:src "/img/four.svg"}]]
    [:div {:class "fb-photo-grid-item last-item"}
     [:img {:src "/img/five.svg"}]]]])

(defn flex-box-footer []
  [:div {:class "fb-footer"}
   [:div {:class "fb-footer-item fb-footer-one"} "Flex: initial"]
   [:div {:class "fb-footer-item fb-footer-two"} "Flex: 1"]
   [:div {:class "fb-footer-item fb-footer-three"} "Flex: initial"]])

(defn css-quest []
  [:<>
   [:p "Here are some Flexbox resources:"]
   [:ul
    [:li
     [:a {:href "https://css-tricks.com/snippets/css/a-guide-to-flexbox/"
          :target "_blank"} "Flexbox Illustrated Guide"]]
    [:li
     [:a {:href "https://mastery.games/post/flexboxzombies2/"
          :target "_blank"} "Flexbox Zombies Game"]]
    [:li
     [:a {:href "https://flexbox.malven.co/"
          :target "_blank"} "More Flexbox Illustrations"]]]

   [:div [:span.warning "WARNING: "]]
   [:p "This Blogg Entry is pretty sloppy and entirely self serving.  Proceed and your own risk."]

   [:p "When I want a holistic and therapeutic 'learning to code' experience, I turn to " [:a {:href "https://www.theodinproject.com"} "The Odin Project"]
    " because it's a warm and friendly place with fantastic reading material and a great community of people ready to answer any questions that may arise."]
   [:p "I am turning to Odin for help with CSS because I hate it and I need a friend to tell me it's going to be okay as I embark on this treacherous quest."]
   [:p "Odin told me: "
    [:em "\"Unfortunately, many learners race through learning HTML and CSS to get to JavaScript and end up missing these fundamental concepts.
         This leads to frustration and pain because all the JavaScript skills in the world are meaningless if you can’t stick
         your elements on the page where you need them to be.\""]]
   [:p "This describes my situation pretty well."]
   [:p "I avoid CSS because whenever I deal with CSS -  I end up behaving this way:"]
   [:img {:src "/img/belial.gif"}]
   [:p "I don't want to BEHAVE this way anymore."]
   [:p "This will be the first of two CSS blogg posts with example codes and revelations peppered in.  I intend to begin with more generic CSS (part 1) and move on to
       Bulma CSS (part 2) as this blog utilizes Bulma CSS and that's ultimately what I want to understand."]
   [:p "God this is going to be dull.....LET'S GO!"]
   [:h1 "Chapter 1: The Box Model"]
   [:div {:style {:border "2px solid red"}} "Every HTML element is a box.  This is a div....EVERY element is a BOX.  Ok?"]
   [:div {:style {:padding "8px" :border "2px solid red"}} "PADDING example - Padding occurs inside the border."]
   [:div {:style {:margin "8px" :border "2px solid red"}} "MARGIN example - Margin occurs outside of the border."]
   [:div {:style {:width "100px" :border "2px solid red"}} "Width? Specifying Height & Width will specify height & width inside of padding"]
   [:p "Here's a weird and annoying CSS-ism:  Margin is shared between elements."]
   [:div {:style {:height "40px" :border "2px solid red"}} "This is an element with height 40px"]
   [:div {:style {:margin "40px" :border "2px solid red"}} "A box element with 40px margin.."]
   [:div {:style {:margin "40px" :border "2px solid red"}} "A box element with 40px margin..."]
   [:p "As you can see - despite the two elements above having 40px margin - there is only 40px of space between them.  This is because margin collapses between two elements that are next to eachother."]
   [:p "If one element has a larger margin, this larger margin will take precedence.  Pretty annoying, huh?"]
   [:p "The concept of margins being 'shared' also applies to negative margins.  If one shared margin is negative and one is postive, the resulting margin will be [positive margin] - [negative margin]"]
   [:p "If there are two negative margins (i.e. -40 and -50) - the resulting margin will be -50 since that is the greater scalar quantity."]
   [:p "This is weird & confusing...it's called 'Margin Collapsing'.  It only happens to the top & bottom margins - NOT left & right."]
   [:p "Why margin collapsing is good:"]
   [:p "This prevents empty elements from adding extra, usually undesirable, vertical margin space. It keeps spacing consistent between header elements and paragraphs.
        It applies to nested elements which prevents unreasonably large and unexpected margins between nested elements."]
   [:p "Main takeaways:"]
   [:p {:style {:margin "18px" :border "2px solid red"}} "Use margin to separate distinct elements from one another."]
   [:p {:style {:padding "18px" :border "2px solid red"}} "Use padding to create space within an element."]
   [:p "Setting box-sizing to 'border-box' is common practice because it allows you to be explicit about height and width of elements."]
   [:p {:style {:box-sizing "content-box" :height "300px" :width "300px" :padding "40px" :border "20px solid red"}}
    "Default CSS box-sizing is set to \"content-box\" Observe this in Chrome dev tools. Even though height is set to 200px and width is set to 500px,
    the actual height and width are higher because 'content-box' adds padding and border to the height and width."]
   [:p {:style {:box-sizing "border-box" :height "300px" :width "300px" :padding "40px" :border "20px solid red"}}
    "This div has box-sizing set to \"border-box\".  This way, user defined height and width is not appended to with padding and margin."]
   [:p "How do we do the mental math to find the actual height & width of an element with box-sizing set to 'content-box'?
       We disregard margin, as that spacing exists outside of height & width.  Padding must be added to height and width twice as it is added to all 4 sides of the element.
       Border must also be added to height and width twice for the same reason."]
   [:p "The following css codeblocks are identical.  Margin will fill in top & bottom or left & right if only two values are provided."]
   [codeblock
    ".box {margin: 0 1.5em;}"
    false "css" hljs/androidstudio]
   [:p "Notice that the code above is a condensed version of the code below."]
   [codeblock
    ".box {margin: 0 1.5em 0 1.5em;}"
    false "css" hljs/androidstudio]
   [:h3 "Inline vs. Block Elements"]
   [:p "An inline element is one that does not break onto a new line - like 'span' or 'em'."]
   [:p "A block element is one that does break onto a new line - like 'div' or 'p'."]
   [:p "Using 'display: inline-block' on an inline element will do something special.  The element will not break onto a new line - but height and width will be respected."]
   [:h3 "Aligning Elements"]
   [:p "In vanilla CSS, the following code will align items to the center:"]
   [codeblock
    ".box {\nmargin: 0 auto;\nwidth: 50%;\n}"
    false "css" hljs/androidstudio]
   [:p "It is very important to set a width when aligning this way, otherwise it will not work. This is because the default width of a block element is 100% of the page."]
   [:p {:style {:margin "0 auto" :width "50%" :border "2px solid red" :text-align "center"}} "am I middling?"]
   [:h3 "CSS Bulma"]
   [:p {:style {:margin-top "1rem"}} "Since Bulma is available here, we can use Bulma columns to center align:"]
   [codeblock
    "[:div.columns
      [:div.column.is-one-third]
      [:div.column \"Am I middling?\"]
      [:div.column.is-one-third]]"]
   [:div.columns {:style {:margin-top "5px"}}
    [:div.column.is-one-third]
    [:div.column
     {:style {:border "2px solid red" :text-align "center"}} "Am I middling?"]
    [:div.column.is-one-third]]
   [:h1 "Chapter 2: Flexbox"]
   [:p "Flexbox is the modern way to move things around on a webpage. There is a key distinction between containers and items within said containers."]
   [:p "The first step to flexboxing is to set the container to have " [:em "display: flex;"] ". Flex Items can be Flex Containers - which is a little weird but very utilitarian.
       Also worth noting: The display property is the same one we used to set to " [:em "block"] " and " [:em "inline"] ". This is a new use case for the display property."]
   [:p "Flexbox containers are made by setting display to flex.  Flexbox items are made by nesting elements within a container."]
   [:p "Here is a block of divs nested within a flex container:"]
   [codeblock
    (pr-str
     [:div.fb-flex-container
      [:div]
      [:div]
      [:div]
      [:div]])]
   [:div.fb-flex-container
    [:div]
    [:div]
    [:div]
    [:div]]
   [:p]
   [:p "The navbar at the top of this Blogg was made using Bulma CSS...but let's look at how to build a navbar with Flexbox"]
   [:p "I will be following this tutorial: " [:a {:href "https://www.internetingishard.com/html-and-css/flexbox/"}]]
   [:p "Flexbox containers hold flexbox items and manage how they are laid out on the page. These decisions are made by the CONTAINER."]
   [flex-box-navbar-example]
   [flex-box-header-example]
   [:p "I'd advise going through this process yourself.  Here is what I learned from the process (gotchas included): "]
   [:ul
    [:li "Changing the width of an element will undo all flexbox rules (why?)"]
    [:li "Justify Content <- justifies horizontally ->"]
    [:li "Align Items \\/ aligns vertically /\\"]
    [:li "Flex containers only define rules for their children - not their grandchildren.  The rules only apply to elements one level deep."]

    [:li "Setting align-self will override the alignment in the flex container for individual elements."]]

   [:p "I will now go thru the god damn wrapping example.  This post really needs some polish lol."]
   [flex-box-wrapping-example]

   [:p "Here is what I learned from the process: "]
   [:ul
    [:li "Wrapping flex-box items allows us to easily handle over-flowing elements."]
    [:li "Justify Content & Align Items mean different things depending on what flex-direction is set to (column || row)."]
    [:li "It's easy to reverse order of elements with :flex-direction (row-reverse || column-reverse)."]
    [:li "The default value for 'order' is set to 0 on all flexbox items.  This can be modified on an individual basis to change order of elements."]
    [:li "Giving a flexbox item a value of 1 for 'order' will move it to the end."]
    [:li "Giving a flexbox item a value of -1 for 'order' will move it to the front."]]

   [:p "The order values in flexbox items aren't very well explained in the tutorial linked above, so here is a little more depth: " [:a {:href "https://mastery.games/post/flexbox-order/" :target "_blank"} "How Flexbox Order Works"] "."]
   [:h3 "Flexible Items"]
   [:div "Here we can see what happens when we set the 'flex' value.  Initial will fall back to default. Since width is set to 100px, the outer-most elements will remain fixed and the middle item
         will be flexible in size when the page width is changed."]
   [flex-box-footer]
   [:h3 "The Flex Shorthand"]
   [:p "When flex is set, there are three properties being set. Setting 'flex: 1' is shorthand for 'flex: 1 1 0'."]
   [:h4 "flex-grow AKA growth rate"]
   [:p "This is called the flex item's 'growth factor' and determines how quickly it will grow. Just setting flex to 2 will set flex-grow to 2."]

   [:h4 "flex-shrink AKA shrink rate"]
   [:p "This is the same idea as flex-grow, but determines the rate at which the element shrinks when the page width is decreasing.  This also defaults to whatever you set 'flex' to, like flex-grow."]
   [:p "Setting flex-shrink to 0 will keep the element from decreasing below it's set width. Note: set widths are a minimum value.  If the element can, it will grow larger than the set width."]

   [:h4 "flex-basis AKA ideal size"]
   [:p "Flex-basis sets the initial size of the flexbox item. Setting this to auto will make the element adhere to a width."]
   [:p "According to Odin, generally flex is set to integers (shorthand notation) and sometimes flex-shrink is set to 0 to prevent items from shrinking."]
   [:p "Complete the assignments in the last thing"]
   [:p "For landing page project, add a sidebar to blog that splits to the bottom on mobile, and maybe an image as background instead of solid color of navbar"]])
