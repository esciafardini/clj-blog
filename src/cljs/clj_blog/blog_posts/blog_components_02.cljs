(ns clj-blog.blog-posts.blog-components-02
  (:require
   [clj-blog.blog-posts.utils :refer [codeblock codeblocks]]
   [clj-blog.log :as log]))

(def users
  [{:first-name "Alan"
    :last-name "Perlis"
    :online? false}
   {:first-name "Jan"
    :last-name "Rickles"
    :online? true}
   {:first-name "Dork"
    :last-name "Fish"
    :online? false}
   {:first-name "Daniel"
    :last-name "Daniels"
    :online? true}
   {:first-name "Pemmy"
    :last-name "Scud"
    :online? false}
   {:first-name "Dick"
    :last-name "Taargus"
    :online? true}
   {:first-name "Daniel"
    :last-name "Taargus"
    :online? true}
   {:first-name "Rich"
    :last-name "Taargus"
    :online? false}])

(filter :online? users)

(filter (comp #{"Scud" "Taargus"} :last-name) users)

(defn idioms []
  [:<>
   [:p [:em "In which the fledgling Clojure developer recounts Clojure idioms that he's picked up on the job..."]]
   [:h4 "Filtering Vectors of Maps"]
   [:p "In my experience, it's very common to be dealing with a vector of maps (i.e. honeySQL data)."]
   [:p "Here is an example of what we might see:"]
   [codeblock
    (pr-str
     '(def users
        [{:first-name "Alan"
          :last-name "Perlis"
          :online? false}
         {:first-name "Jan"
          :last-name "Rickles"
          :online? true}
         {:first-name "Dork"
          :last-name "Fish"
          :online? false}
         {:first-name "Daniel"
          :last-name "Daniels"
          :online? true}
         {:first-name "Pemmy"
          :last-name "Scud"
          :online? false}
         {:first-name "Dick"
          :last-name "Taargus"
          :online? true}
         {:first-name "Daniel"
          :last-name "Taargus"
          :online? true}
         {:first-name "Rich"
          :last-name "Taargus"
          :online? false}]))]

   [:p "Clojure makes it really easy to filter these users using predicate functions.  When I first tried to filter a vector of maps like this I did something goofy like this:"]
   [codeblock
    (pr-str '(filter (fn [user-map] (= (:online? user-map) true)) users))]
   [:p "This works fine for filtering down to users that are online..."]
   [codeblock
    (pr-str
     '({:first-name "Jan", :last-name "Rickles", :online? true}
       {:first-name "Daniel", :last-name "Daniels", :online? true}
       {:first-name "Dick", :last-name "Taargus", :online? true}
       {:first-name "Daniel", :last-name "Taargus", :online? true}))]
   [:p "But there is an idiomatic and terse way to do so:"]
   [codeblock
    (pr-str '(filter :online? users))]
   [:p "Wow, so clean and fresh."]
   [codeblock
    (pr-str
     '(:online? {:first-name "Jeff"
                 :last-name "Dankiel"
                 :online? false}))]
   [:p "This is the function called on each 'user-map' when calling filter.  The above codeblock will return
       false and therefore be filtered out of the collection that is returned.  This is great for booleans / exists
       filtering, but what if we want something a little more complicated?  Something like 'Only users with last name Taargus OR Rickles'?"]
   [:p "I will begin with a slightly gnarly example."]
   [codeblock
    (pr-str
     '(filter (fn [user] (or (= (:last-name user) "Rickles") (= (:last-name user) "Taargus"))) users))]
   [:p "Something about this....feels...off. Let's take a quick detour into 'Sets As Functions In Clojure' because it will help us in a second."]

   [:p "It may look strange at first, but sets can be called as functions. Whenever I see this in code, I read it as: "
    [:em "Does set  #{1, 2, 3} include 1?"]]
   [codeblock "(#{1 2 3} 1) ;=> 1" false]
   [:p "When it is included, a truthy value is returned. When it isn't included, a nil value is returned."]
   [codeblock "(#{1 2 3} 4) ;=> nil" false]
   [:p "Let's combine comp, set functions, and keywords as functions to write a fresh, clean, and terse way to accomplish our goal in Clojure."]
   [codeblock
    (pr-str
     '(filter (comp #{"Rickles" "Taargus"} :last-name) users))]
   [:p "This broke my junior developer brain when I first saw it, but I think it's pretty clean & fresh now that I understand it.
       The clojure fn 'comp' returns a function that is 'composed' of the functions you pass to it.  What happens here?"]
   [:div "For each user map...."]
   [:ol
    [:li "Call :last-name on the user-map"]
    [:li "Call the function #{\"Rickles\" \"Taargus\"} on the last name"]]

   [:p "Recall that set functions are effectively asking the question: " [:em "Does the set #{\"Rickles\" \"Taargus\"} include the value \"Wilson\"?"]]])

(defn little-schemer-chapter8 []
  [:<>
   [:img {:src "/img/schemer.jpg" :style {:object-fit "cover" :width "120px" :height "auto"}}]
   [:div [:span.warning "WARNING: "]]
   [:p "This Blogg Entry assumes the reader understands recursion and higher order functions.  Also, it is probably only useful if you have read or are reading The Little Schemer...."]

   [:p "What is The Little Schemer?  It's a book of recursion exercises in LISP.  I have been coding along with it in Clojure & it's been an illuminating experience.
        Recursion never really clicked with me but after working through these exercises, I am beginning to have an intuitive understanding of it.
        Chapter 8 took me a while to work through and it was by far the most challenging. I will be traversing this chapter again in this blogg entry while stripping away
        the fat and focusing on the true essence of what is covered."]

   [:p "Let us begin at the beginning, my child - because it's all uphill from here.  The last function in particular was a real doozy: " [:em "evens-only*&co."]]

   [:p "The chapter begins with some nonsense about the difference between eq? and equal? that we will disregard because Clojure has a perfectly fine equality check in the
        form of (= x y). The first new function we come across is rember-f.  I am skipping ahead a few pages and observing the function in it's final form. Essence extracted:"]

   [codeblock
    (pr-str
     '(defn rember-f
        [test-f]
        (fn [a l]
          (cond
            (not (seq l)) '()

            (test-f (first l) a)
            (rest l)

            :else
            (cons (first l) ((rember-f test-f) a (rest l)))))))]

   [:p "It's important to notice that this function returns a function.  The function takes a function & uses it in the cond statement.  How rember-f recurses
        ultimately depends on the result of calling the passed in function on (first l) and a.  In this case, l stands for list or collection & a stands for atom or item in the list.
        The else statement is building a new list from an old list recursively.  If the test-f predicate returns true, (rest l) is returned and the recursion ends."]

   [:p "So there are two base cases in this function: "
    [:ol
     [:li "The entire collection is traversed & (rest l) is passed in as an empty collection"]
     [:li "The first item in the collection is equal to atom a"]]]

   [:p "Another thing to consider is that this function returns a function and so must be called AS a function - like so:"]
   [codeblock
    (pr-str
     '((rember-f (fn [x y] (= x y))) "ok" ["wow" "um" "ok" "well" "ugh" "jesus" "ok"]))]

   [:p "This is how it's done in the else block. This will return a collection with the first instance of \"ok\" removed from it.  Okay....so maybe this chapter is teaching us how to build functions
        that we can call on collections to do things recursively.  As usual, the chapter title " [:b "Lambda The Ultimate"]
       " doesn't reveal much. It seems the ultimate goal is to use this function as a base for removal functions.  Here is an example of what I mean:"]

   [codeblocks "clojure"
    (pr-str
     '(def rember-eq? (rember-f =)))
    (pr-str
     '(rember-eq? "ok" ["wow" "um" "ok" "well" "ugh" "jesus" "ok"]))]

   [:p "This is a more readable re-factor of the last codeblock.  So it is kind of cool to think of having a base function that recursively acts on a collection
        based on a predicate passed into it.  Now we have options to remove an item from a collection in any way our mind can conceive of.  There are subtle hints
        in the book that say this is related to " [:em "currying"] ".  It describes currying as a function that when passed an argument a, returns a function that uses argument a."]

   [:h2 "My Fav Curry Recipes:"]
   [:ul
    [:li
     [:a {:target "_blank"
          :href "https://andrewberls.com/blog/post/partial-function-application-for-humans"} "Currying Made Simpler"]]
    [:li
     [:a {:target "_blank"
          :href "https://practical.li/clojure/thinking-functionally/partial-functions.html"} "Currying in Clojure"]]]

   [:p "Currying takes a multi-arity function & converts it into a series/chain of single-arity functions.
        Something similar (but not exactly Currying) is achieved in Clojure via the function partial.
        Partial functions fix some number of arguments to a function - the result is a function of smaller arity. See this example:"]

   [codeblocks "clojure"
    "(def add-2 (partial + 2))"
    "\n"
    "(add-2 10 10)"
    ";=> 22"]

   [:p "Currying generally deals with fixed argument lengths. The limitation of partial vs classic currying is that partial only enables you to fix the first arguments to the function.
        Partial allows you to provide a number of (the first x or xs) arguments to a function, and get a new function that takes the rest of the arguments to return a value.
        The benefit of partial is that it works with multi-arity functions."]

   [:p "Just because - let's look at a 4clojure exercise that deals with currying:"]

   [codeblocks "clojure"

    (pr-str '(= 10 ((__ (fn [a]
                          (fn [b]
                            (fn [c]
                              (fn [d]
                                (+ a b c d))))))
                    1 2 3 4)))
    "\n"
    (pr-str '(def curried-fn (fn [a]
                               (fn [b]
                                 (fn [c]
                                   (fn [d]
                                     (+ a b c d)))))))
    "\n"
    (pr-str '(def f1 (curried-fn 1)))
    ";=> equivalent to:"
    (pr-str '(fn [b]
               (fn [c]
                 (fn [d]
                   (+ 1 b c d)))))
    "\n"

    (pr-str '(def f2 (f1 2)))
    ";=> equivalent to:"
    (pr-str '(fn [c]
               (fn [d]
                 (+ 1 2 c d))))
    "\n"
    (pr-str '(def f3 (f2 3)))
    ";=> equivalent to:"
    (pr-str '(fn [d]
               (+ 1 2 3 d)))
    "\n"
    (pr-str '(f3 4))

    ";=> equivalent to:"
    (pr-str '((fn [d] (+ 1 2 3 d)) 4))
    ";=> 10"
    "\n"
    ";what about this?"
    (pr-str '(reduce (fn [f item] (f item)) curried-fn [1 2 3 4]))]
   [:p "Reducing over the collection (1 2 3 4) and calling the curried function each time will result in calling line 19 above. This ONLY works because there are 4 nested functions
        and 4 items in the collection.  If the collection was [1 2 3] instead of [1 2 3 4], the result of reducing would be a function equivalent to line 15 above."]

   [:p "I'm pretty new to the concept of currying, but as far as I can tell - it's not explicitly supported in Clojure.  We have something similar though, in the form of partial.  ANYWAY...
        back to Chapter 8 of the Little Schemer"]

   [:p "Next is another exercise in returning a function based on a predicate in the form of insertL-f.  This is really the same exact idea as rember-f, but with a
        different modification of the collection.  With this fn, we are adding a new item to the left of the first occurance of another item."]

   [codeblock
    (pr-str
     '(defn insertL-f
        [f]
        (fn [nu old lat]

          (cond
            (not (seq lat))
            '()

            (f old (first lat))
            (cons nu lat)

            :else
            (cons (first lat) ((insertL-f f) nu old (rest lat)))))))]

   [:p "Sure - makes enough sense, it's the same as insertL (not -f) but you pass in a predicate function and it returns a new function that you can use.  This operates the same way that
        rember-f does."]

   [:p "The book goes on to describe another way to pass functions around with a more generic insert-g function.  This function will allow you to build your list differently depending on which function is passed in.
        This way you can build insertL-f and insertR-f from insert-g and it's composable if you think of some other insertX-f you want to build:"]

   [codeblocks "clojure"
    (pr-str '(defn seqL
               [nu old l]
               (cons nu (cons old l))))
    "\n"
    (pr-str '(defn seqR
               [nu old l]
               (cons old (cons nu l))))
    "\n"
    (pr-str '(defn insert-g
               [seq-fn]
               (fn [nu old l]
                 (cond
                   (not (seq l)) '()

                   (= old (first l)) (seq-fn nu old (rest l))

                   :else
                   (cons (first l) ((insert-g seq-fn) nu old (rest l)))))))
    "\n"
    (pr-str '(def insertR-f (insert-g seqR)))

    "\n"
    (pr-str '(insertR-f "chicken" "garlic" ["with" "veggies" "and" "garlic" "tasty"]))

    ";=> (\"with\" \"veggies\" \"and\" \"garlic\" \"chicken\" \"tasty\")"
    "\n"
    ";this can be extended to create a substitute fn:"
    (pr-str '(defn seqSubstitute [nu _old l]
               (cons nu l)))
    ";add the new item in the place of the old item"
    "\n"
    ";or a Remove Member function:"
    (pr-str '(defn seqRemove [_nu _old l]
               l))
    ";when the nu is equal to the old, return the rest of the list"]

   [:p "This is all tracking.  So far, so good.  Recursive higher order functions that offer us more composability and flexibility than their predecessors.
        It looks like insert-g can be used to build any of our old functions that modified a single item in a collection.
        Hmmm....maybe I will understand evens-only*&co when we get there?  I'm not so sure, but let's carry on."]

   [:p "Alright, up next is an atom-to-function function that tales an atom like + and returns the function for + and I'm not really interested.  The same thing can be achieved in a lookup map:"]

   [codeblock
    "{:plus '+
      :minus '-
      :times '*
      :divide '/}"]

   [:p "Now we will define multi-rember-f as a higher order function.  This is VERY similar to the above insertL-f"]

   [codeblock
    (pr-str
     '(defn multi-rember-f
        [test-f]
        (fn [a lat]
          (cond
            (not (seq lat)) '()

            (test-f a (first lat))
            ((multi-rember-f test-f) a (rest lat))

            :else
            (cons (first lat) ((multi-rember-f test-f) a (rest lat)))))))]

   [:p "Sure! So let's make a partial function to make this easier on ourselves. The book shows a partial function called eq?-c that we will rewrite here for Clojure:"]
   [codeblocks "clojure"
    (pr-str
     '(defn eq?-c [a]
        (partial = a)))
    "\n"
    (pr-str '((eq?-c "chungus") "chungus"))
    ";=> true"
    "\n"
    (pr-str '((eq?-c "chawngus") "chungus"))
    ";=> false"]
   [:p "The next exercise is turning multi-rember-f into a function that returns the desired new collection when passed a predicate function and a collection.  This way, we can define our predicate function elsewhere
        and just call this new function (multi-rember-T ...) to do the work on a collection."]

   [codeblocks "clojure"
    "; we want to check if the value is equal to \"tuna\""
    (pr-str
     '(def equals-tuna
        (eq?-c "tuna")))
    "\n"
    (pr-str '(equals-tuna "tuna"))
    ";=> true"
    ";nice!"
    "\n"
    (pr-str '(defn multi-rember-T
               [test-f lat]
               (cond
                 (not (seq lat))
                 '()

                 (test-f (first lat))
                 (multi-rember-T test-f (rest lat))

                 :else
                 (cons (first lat) (multi-rember-T test-f (rest lat))))))
    "\n"
    (pr-str '(multi-rember-T equals-tuna ["oh" "i" "do" "eat" "tuna" "fish" "becuz" "tuna" "tuna" "tuna" "fish" "is" "good"]))
    ";=> (\"oh\" \"i\" \"do\" \"eat\" \"fish\" \"becuz\" \"fish\" \"is\" \"good\")"]

   [:p "It seems to me that we are doing the same a ton of different ways - and if I had to guess at the authors' objective behind this it would be that they want to reinforce the importance and utility of passing
        functions into functions and returning functions - AKA the importance and utility of higher order functions.  Ok, so this is still making sense - much more than my first time around.  Let us proceed."]

   [:p "Up next is the gnarly function your "
    [:a {:target "_blank"
         :href "http://www.michaelharrison.ws/weblog/2007/08/unpacking-multiremberco-from-tls/"} "friends have been warning you about"]
    ":"]

   [codeblock
    (pr-str '(defn multi-rember&co
               [a lat f]
               (cond
                 (not (seq lat))
                 (f '() '())

                 (= (first lat) a)
                 (multi-rember&co a
                                  (rest lat)
                                  (fn [newlat removed-items]
                                    (f
                                     newlat
                                     (cons (first lat) removed-items))))

                 :else
                 (multi-rember&co a
                                  (rest lat)
                                  (fn [newlat removed-items]
                                    (f
                                     (cons (first lat) newlat)
                                     removed-items))))))]

   [:p "This requires a cognitive leap because it's not exactly like anything we've seen before in the book. I have renamed a lot of the variables for my own sanity.
        Calling the collector function col is too similar to collection, so I named it f because it's really just a function.  It took a lot of REPLing and head scratching but
        I think I understand this well enough to explain it now."]

   [:p "This is still a multi-rember function so it is going to remove all instances of \"a\" from the lat you pass in.  What this ALSO does is collect all the removed items in a
        second collection that contains all the discarded or \"removed\" elements. The function you pass in has access to both the new-lat and the collection of discarded elements.
       I renamed the variables in the collector functions to make this more clear as well."]

   [codeblocks "clojure"
    (pr-str
     '(multirember&co "chicken" ["no" "more" "chicken" "meat" "in" "this" "chicken" "collection"] (fn [newlat removed-items] newlat)))
    ";=> (\"no\" \"more\" \"meat\" \"in\" \"this\" \"collection\")"
    ";this will return the same value as the old multi-rember, because newlat is the return value of the old multi-rember"]

   [:p "The logic is still the same - remove the items when they are equal to the specified atom.  The big difference is that we are now building arguments for function f as opposed
        to just building a collection to be returned.  This gives you the ability to track what's removed from the collection and operated on it if desired."]

   [codeblocks "clojure"
    (pr-str
     '(multi-rember&co "chicken" ["no" "more" "chicken" "meat" "in" "this" "chicken" "collection"]
                       (fn [newlat removed-items] (let [cnt (count removed-items)]
                                                    (println (str cnt " items have been removed from this collection."))
                                                    newlat))))
    ";(out) 2 items have been removed from this collection."
    ";=> (\"no\" \"more\" \"meat\" \"in\" \"this\" \"collection\")"]

   [:p "Admittedly, this stuff took me a long time to grok & it still trips me up a bit.  I strongly recommend you work with these in a live REPL and try to implement your own versions
        of these functions."]

   [:p "There are a few more &co implementations that the book has you work through to seal the deal.  I will be skipping these and moving on to my arch-nemesis " [:em "evens-only*&co"] "."]

   [codeblock
    (pr-str
     '(defn evens-only*&co
        [lat f]
        (let [idx0 (first lat)
              remaining (rest lat)]
          (cond
            (not (seq lat))
            (f lat 1 0)

            (not (coll? idx0))
            (if (even? idx0)
              (evens-only*&co remaining
                              (fn [new-lat evens-product odds-sum]
                                (f (cons idx0 new-lat)
                                   (* idx0 evens-product)
                                   odds-sum)))

              (evens-only*&co remaining
                              (fn [new-lat evens-product odds-sum]
                                (f new-lat
                                   evens-product
                                   (+ idx0 odds-sum)))))

            :else
            (evens-only*&co idx0
                            (fn [result-lat-from-idx0 evens-product-from-idx0 odds-sum-from-idx0]
                              (evens-only*&co remaining
                                              (fn [new-lat evens-product odds-sum]
                                                (f (cons result-lat-from-idx0 new-lat)
                                                   (* evens-product evens-product)
                                                   (+ odds-sum-from-idx0 odds-sum))))))))))]

   [:p "Alright...as far as I can tell everything makes sense up until the else block. Let's go step by step. For readability, the first element and the remaining elements
        are moved to let bindings.  When the base case occurs - meaning the sequence has been traversed entirely, we call the function on [lat 1 0] which makes sense:"]
   [:ol
    [:li [:b "lat"] " is the new collection with all odd numbers removed"]
    [:li [:b "1"] " is ideal for multiplying n amount of numbers by a base i.e. (* 8 4 2 6 14 " [:b " 1"] ")"]
    [:li [:b "0"] " is ideal for adding n amount of numberrs to a base i.e. (+ 13 5 3 9 7 5 " [:b " 0"] ")"]]

   [:p "These are the arguments we can act upon with whatever f we pass into evens-only&co. It's the same idea as multirember&co where we are building arguments for the
        function f to act upon."]

   [:p "When we reach " [:b "(not (coll? idx0)) "] "we are now in the context of a collection element that is not a collection or what the book refers to as an 'atom'.
        So we check if it's odd or even, and build the arguments for our function accordingly.  If it's an even number:"]

   [:ol
    [:li  "cons it onto new-lat"]
    [:li  "multiply it by the evens-product accumulator"]
    [:li  "leave the odds-sum accumulator alone"]]

   [:p "When it's an odd number:"]

   [:ol
    [:li  "do NOT cons it onto new-lat - notice that new-lat is always 'remaining' in the context of where it is called"]
    [:li  "leave the evens-product accumulator alone"]
    [:li  "add it to the odds-sum accumulator"]]

   [:p "Which brings us to the else block...and oh what an else block it is...."]
   [:p "I swear to God I am going to understand this"]
   [:p "We are cons'ing the result-lat-from-idx0 to the new-lat which makes me think whatever result-lat-from-idx0 is is the result of calling evens-oresult-lat-from-idx0nly&co on the collection in idx0."]
   [:p "Let's look at this thing again"]
   [codeblock (pr-str '(evens-only*&co idx0
                                       (fn [result-lat-from-idx0 evens-product-from-idx0 odds-sum-from-idx0]
                                         (evens-only*&co remaining
                                                         (fn [new-lat evens-product odds-sum]
                                                           (f (cons result-lat-from-idx0 new-lat)
                                                              (* evens-product-from-idx0 evens-product)
                                                              (+ odds-sum-from-idx0 odds-sum)))))))]
   [:blockquote
    "Imagine you have the results from removing, summing and adding the numbers in idx0 and call these result-lat-from-idx0, evens-product-from-idx0, and odds-sum-from-idx0; then imagine you have the results from doing the same
        thing to remaining, and call them new-lat, evens-product and odds-sum. To your waiting continuation, give the values produced by consing result-lat-from-idx0 and new-lat (since we're producing
        a list of lists); multiplying together evens-product-from-idx0 and evens-product; and adding odds-sum-from-idx0 and odds-sum. Notice: each time we make a recursive call, we construct a new continuation for the recursive call, which closes over the current values of the argument, lat, and the return continuation -
        f, in other words, you can think of the chain of continuations which we build up during the recursion as modelling the call stack of a more conventionally written function!"]
   [:p {:style {:margin-left "2rem"}} "-Someone On StackOverflow"]
   [codeblock
    (pr-str
     '(defn evens-only*&co
        [lat f]
        (let [idx0 (first lat)
              remaining (rest lat)]
          (cond
            (not (seq lat))
            (f lat 1 0)

            (not (coll? idx0))
            (if (even? idx0)
              (evens-only*&co remaining
                              (fn [new-lat evens-product odds-sum]
                                (f (cons idx0 new-lat)
                                   (* idx0 evens-product)
                                   odds-sum)))

              (evens-only*&co remaining
                              (fn [new-lat evens-product odds-sum]
                                (f new-lat
                                   evens-product
                                   (+ idx0 odds-sum)))))

            :else
            (evens-only*&co idx0
                            (fn [result-lat-from-idx0 evens-product-from-idx0 odds-sum-from-idx0]
                              (evens-only*&co (r-spy remaining)
                                              (fn [new-lat evens-product odds-sum]
                                                (f (cons result-lat-from-idx0 new-lat)
                                                   (* evens-product-from-idx0 evens-product)
                                                   (+ odds-sum-from-idx0 odds-sum))))))))))]
   [:p "Let's go step by step."]

   [codeblocks "clojure"
    (pr-str '(evens-only*&co [1 [2 3] 4] (fn [nu-lat sum product] (str nu-lat "  sum is " sum " product is " product))))
    "; 1 is not a coll, and 1 is odd SO"
    "; this is what is called:"
    (pr-str
     '(evens-only*&co [[2 3] 4] (fn [nu-lat sum product] (f [[2 3] 4] 1 (+ 1 0)))))
    "\n"
    "; Next is a collection so we move into else block...uh oh"
    (pr-str
     '(evens-only*&co [2 3] (fn [result-lat-from-idx0 evens-product-from-idx0 odds-sum-from-idx-0]
                              (evens-only*&co '(4) (fn [new-lat evens-product odds-sum]
                                                     (f (cons result-lat-from-idx0 new-lat)
                                                        (* evens-product-from-idx0 evens-product)
                                                        (+ odds-sum-from-idx0 odds-sum)))))))
    ";We have a function wrapping the results from the inner call to evens-only*&co that ties the values back to the outer function"]

   [:p "Pattern recognition tells me that the else block is the only call to evens-only*&co that calls evens-only*&co within the function passed into it.
        It looks as though that inner call is building it's own arguments - turtles all the way down style.  Then we cons the result into newlat and
        make sure all evens and odds are accounted for the ultimate return value."]
   [:p "But I don't really understand it.  I will surely be re-visiting this and revising if it starts to make more sense.  For now, continuation remains a semi-mystery."]])
