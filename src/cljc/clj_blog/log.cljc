(ns clj-blog.log)

(defmacro spy
  [& body]
  `(let [x# ~@body]
     (printf "%s:\n%s\n" (first '~body) x#)
     x#))
