(defproject rangeview "0.0.1"
  :description "A daemon to expose the data from a target controlled by MLShoot."
  :url "http://github.com/conormcd/rangeview"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.122"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [org.clojure/tools.cli "0.3.3"]
                 [org.clojure/tools.nrepl "0.2.11"]
                 [org.xerial/sqlite-jdbc "3.8.11.1"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [rm-hull/monet "0.2.2"]
                 [cljsbuild "1.1.0"]
                 [cljs-ajax "0.3.14"]]
  :pedantic? :abort
  :plugins [[lein-cljsbuild "1.1.0"]]
  :hooks  [leiningen.cljsbuild]
  :cljsbuild {:builds [{:source-paths ["src" "src-cljs"]
                        :compiler {:output-to "web/js/rangeview.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}
  :main rangeview.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :resource-paths ["web/"]}}
  :jvm-opts ["-Djava.net.preferIPv4Stack=true"])
