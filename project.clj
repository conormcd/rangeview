(defproject rangeview "0.0.1"
  :description "A daemon to expose the data from a target controlled by MLShoot."
  :url "http://github.com/conormcd/rangeview"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3126"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [org.xerial/sqlite-jdbc "3.8.7"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [rm-hull/monet "0.2.1"]
                 [cljs-ajax "0.3.10"]]
  :plugins [[lein-cljsbuild "1.0.5"]]
  :aliases {"repl" ["do" "cljx," "repl"]
            "run" ["do" "cljx," ["cljsbuild" "once"] "run"]
            "test" ["do" "cljx," "test"]}
  :cljx {:builds [{:source-paths ["src-cljx"]
                   :output-path "target/generated/clj"
                   :rules :clj}
                  {:source-paths ["src-cljx"]
                   :output-path "target/generated/cljs"
                   :rules :cljs}]}
  :cljsbuild {:builds [{:source-paths ["src-cljs" "target/generated/cljs"]
                        :compiler {:output-to "web/js/rangeview.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}
  :source-paths ["src" "target/generated/clj"]
  :main ^:skip-aot rangeview.core
  :target-path "target/%s"
  :profiles {:dev {:plugins [[com.keminglabs/cljx "0.6.0"]]}
             :uberjar {:aot :all}})
