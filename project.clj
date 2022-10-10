(defproject clj-blog "0.1.0-SNAPSHOT"

  :description "A Blogg about Functional Programming"
  :url "http://www.fpblogg.com"

  :dependencies [[ch.qos.logback/logback-classic "1.2.3"]
                 [cheshire "5.10.0"]
                 [clojure.java-time "0.3.2"]
                 [cljs-ajax "0.8.1"]
                 [com.google.javascript/closure-compiler-unshaded "v20200830" :scope "provided"]
                 [com.taoensso/sente "1.16.0"]
                 [conman "0.9.1"]
                 [cprop "0.1.17"]
                 [expound "0.8.7"]
                 [luminus-http-kit "0.1.9"]
                 [luminus-migrations "0.7.1"]
                 [luminus-transit "0.1.2"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [markdown-clj "1.10.5"]
                 [markdown-to-hiccup "0.6.2"]
                 [metosin/malli "0.8.9"]
                 [metosin/muuntaja "0.6.7"]
                 [metosin/reitit "0.5.10"]
                 [metosin/ring-http-response "0.9.1"]
                 [mount "0.1.16"]
                 [nrepl "0.8.3"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.773" :scope "provided"]
                 [org.clojure/google-closure-library "0.0-20191016-6ae1f72f" :scope "provided"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.postgresql/postgresql "42.2.18"]
                 [org.webjars.npm/bulma "0.9.1"]
                 [org.webjars.npm/material-icons "0.3.1"]
                 [org.webjars/webjars-locator "0.40"]
                 [reagent "1.1.1"]
                 [re-frame "1.1.2"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.8.2"]
                 [ring/ring-defaults "0.3.2"]
                 [selmer "1.12.31"]
                 [thheller/shadow-cljs "2.11.14" :scope "provided"]
                 [zprint "1.2.3"]]

  :min-lein-version "2.0.0"

; I’ve figured this one out. The previous project.clj with the :cljsbuild config had its own :source-paths [“src/cljs”] entry.
; When switching over to shadow-cljs, we also have to add “src/cljs” to our lein :source-paths.
; This isn’t called out in the current version of the book and might be worth including.
; Thanks

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot clj-blog.core

;profiles identify different build scenarios
  :profiles
  {:uberjar {:omit-source true
             :aot :all
             :uberjar-name "clj-blog.jar"
             ;source paths and resource paths are very lightweight & specific
             ;prod/clj does not include support for starting and stopping the server within REPL for example
             :source-paths ["env/prod/clj" "env/prod/cljc" "env/prod/cljs"]
             :resource-paths ["env/prod/resources"]
             :prep-tasks ["compile" ["run" "-m" "shadow.cljs.devtools.cli" "release" "app"]]}
   ;uberjar packages application for deployment

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[day8.re-frame/re-frame-10x "1.4.1"]
                                 [day8.re-frame/tracing "0.6.2"]
                                 [pjstadig/humane-test-output "0.10.0"]
                                 [prone "2020-01-17"]
                                 [ring/ring-devel "1.8.2"]
                                 [ring/ring-mock "0.4.0"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                 [jonase/eastwood "0.3.5"]]

                  :source-paths ["env/dev/clj" "env/dev/cljs" "env/dev/cljc"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user
                                 :timeout 120000}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}})
