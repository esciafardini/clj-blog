(ns clj-blog.blog-posts.blog-components-01
  (:require
   ["react-syntax-highlighter/dist/esm/styles/hljs" :as hljs]
   [clj-blog.blog-posts.utils :refer [codeblock]]
   [clojure.string :as string]
   [clojure.walk :as walk]
   [goog.string.linkify :refer [EMAIL_RE_]]
   [re-frame.core :as rf])
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

(defn css-quest-1 []
  [:<>
   [:h2 "CSS Quest - Chapter 1"]
   [:h3 "The Box Model"]
   [:p "When I want a holistic and therapeutic `learning to code` experience, I turn to " [:a {:href "https://www.theodinproject.com"} "The Odin Project"]
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
   [:p "This will be the first of many CSS posts with example codes and revelations peppered in.  I intend to begin at the Box Model and finish
       with Bulma CSS - as this blog utilizes Bulma CSS and that's ultimately what I want to understand."]
   [:p "God this is going to be dull.....LET'S GO!"]
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
   [:p "The concept of margins being `shared` also applies to negative margins.  If one shared margin is negative and one is postive, the resulting margin will be [positive margin] - [negative margin]"]
   [:p "If there are two negative margins (i.e. -40 and -50) - the resulting margin will be -50 since that is the greater scalar quantity."]
   [:p "This is weird & confusing...it's called `Margin Collapsing`.  It only happens to the top & bottom margins - NOT left & right."]
   [:p "Why margin collapsing is good:"]
   [:p "This prevents empty elements from adding extra, usually undesirable, vertical margin space. It keeps spacing consistent between header elements and paragraphs.
        It applies to nested elements which prevents unreasonably large and unexpected margins between nested elements."]
   [:p "Main takeaways:"]
   [:p {:style {:margin "18px" :border "2px solid red"}} "Use margin to separate distinct elements from one another."]
   [:p {:style {:padding "18px" :border "2px solid red"}} "Use padding to create space within an element."]
   [:p "Setting box-sizing to `border-box` is common practice because it allows you to be explicit about height and width of elements."]
   [:p {:style {:box-sizing "content-box" :height "300px" :width "300px" :padding "40px" :border "20px solid red"}}
    "Default CSS box-sizing is set to \"content-box\" Observe this in Chrome dev tools. Even though height is set to 200px and width is set to 500px,
    the actual height and width are higher because `content-box` adds padding and border to the height and width."]
   [:p {:style {:box-sizing "border-box" :height "300px" :width "300px" :padding "40px" :border "20px solid red"}}
    "This div has box-sizing set to \"border-box\".  This way, user defined height and width is not appended to with padding and margin."]
   [:p "How do we do the mental math to find the actual height & width of an element with box-sizing set to `content-box`?
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
   [:p "An inline element is one that does not break onto a new line - like `span` or `em`."]
   [:p "A block element is one that does break onto a new line - like `div` or `p`."]
   [:p "Using `display: inline-block` on an inline element will do something special.  The element will not break onto a new line - but height and width will be respected."]
   [:h3 "Aligning Elements"]
   [:p "In vanilla CSS, the following code will align items to the center:"]
   [codeblock
    ".box {\nmargin: 0 auto;\nwidth: 50%;\n}"
    false "css" hljs/androidstudio]
   [:p "It is very important to set a width when aligning this way, otherwise it will not work. This is because the default width of a block element is 100% of the page."]
   [:p {:style {:margin "0 auto" :width "50%" :border "2px solid red" :text-align "center"}} "am I middling?"]
   [:h3 "CSS Bulma"]
   [:p {:style {:margin-top "1rem"}} "Since Bulma is available here, we can use Bulma columns to right align:"]
   [codeblock
    "[:div.columns
      [:div.column.is-one-third]
      [:div.column \"Am I middling?\"]
      [:div.column.is-one-third]]"]
   [:div.columns {:style {:margin-top "5px"}}
    [:div.column.is-one-third]
    [:div.column
     {:style {:border "2px solid red" :text-align "center"}} "Am I middling?"]
    [:div.column.is-one-third]]])

(defn css-quest-2 []
  [:<>
   [:h2 "CSS Quest - Chapter 2"]
   [:h3 "Flexbox"]
   [:p "Flexbox is the modern way to move things around on a webpage."]])

