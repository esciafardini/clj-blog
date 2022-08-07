(ns clj-blog.blog-posts.blog-components-01

  (:require
   [clj-blog.blog-posts.utils :refer [codeblock]]))

(defn first-entry []
  [:div.blogpost
   [:h1 "Why did I make this?"]
   [:p.date "Apr 16th, 2022"]
   [:p "As a relatively new developer, I am very interested in becoming
       a relatively good developer.  When asking the experienced and 
       wizard-streaked developers in my circle, there are a two responses 
       that are given almost every time, one hundred percent of the time(?)"]
   [:ul
    [:li [:b "Build Projects"]]
    [:li [:b "Read Documentation & Books"]]]
   [:p "And so it is - this blogg is bullet point 1 in action. The intention is to 
       track my progress working on various projects (bullet point 1) and document 
       some documentation in a way that is readable to myself and other beginners 
       (bullet point 2)."]
   [:p "I am attempting to feed two birds with one scone."]
   [:<>
    [codeblock
     (pr-str
      '(defn codeblock
         "Adds syntax highlighting and formatting to code snippets for rendering"
         ([code] (codeblock code "clojure"))
         ([code language]
          [:div.code
           [:> SyntaxHighlighter
            {:language language
             :showLineNumbers true
             :style docco}
            (zprint-code code)]])))]]
   [:p "Hell Yea Fckn RITE"]
   [:<>
    [codeblock "(defn neverhood-blogg \"I think I know why I exist?\" [concepts] (for [concept concepts] (blogg-post concept)))"]]])

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
