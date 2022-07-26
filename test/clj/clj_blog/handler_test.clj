(ns clj-blog.handler-test
  (:require
    [clojure.test :refer :all]
    [ring.mock.request :refer :all]
    [clj-blog.handler :refer :all]
    [clj-blog.middleware.formats :as formats]
    [muuntaja.core :as m]
    [mount.core :as mount]))

#_(defn parse-json [body]
  (m/decode formats/instance "application/json" body))

#_(use-fixtures
  :once
  (fn [f]
    (mount/start #'clj-blog.config/env
                 #'clj-blog.handler/app-routes)
    (f)))

#_(deftest test-app
  (testing "main route"
    (let [response ((app) (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response ((app) (request :get "/invalid"))]
      (is (= 404 (:status response))))))
