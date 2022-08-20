(ns clj-blog.db.core-test
  (:require
   [clj-blog.db.core :refer [*db*] :as db]
   [java-time.pre-java8]
   [luminus-migrations.core :as migrations]
   [clojure.test :refer :all]
   [next.jdbc :as jdbc]
   [clj-blog.config :refer [env]]
   [mount.core :as mount]))

#_(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'clj-blog.config/env
     #'clj-blog.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

#_(deftest test-users
  (jdbc/with-transaction [t-conn *db* {:rollback-only true}]
    (is (= 1 (db/create-blog-post!
              t-conn
              {:title               "Blog Entry"
               :component_function  "blog_posts/blog1"}
              {})))))
