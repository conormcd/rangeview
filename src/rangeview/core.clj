(ns rangeview.core
  (:require [clojure.data.json :as json]
            [rangeview.mlq :as mlq]
            [rangeview.mlshoot :as mlshoot]
            [ring.adapter.jetty :as jetty])
  (:gen-class))

(defn handler
  [mlq-dir req]
  (let [latest-mlq (mlshoot/latest-mlq-path mlq-dir)]
    {:status  200
     :headers {"Content-Type" "application/json"}
     :body    (json/write-str (mlq/current-series latest-mlq))})
  )

(defn -main
  [& args]
  (jetty/run-jetty (partial handler "test/data") {:port 8080}))
