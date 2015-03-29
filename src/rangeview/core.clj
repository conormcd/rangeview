(ns rangeview.core
  (:require [clojure.data.json :as json]
            [rangeview.mlq :as mlq]
            [rangeview.mlshoot :as mlshoot]
            [ring.adapter.jetty :as jetty])
  (:gen-class))

(defn handler
  [mlq-dir req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (json/write-str (mlq/shots (mlshoot/latest-mlq-path mlq-dir)))})

(defn -main
  [& args]
  (let [mlq-dir (first args)
        port (first (filter #(not (nil? %)) [(second args), 8080]))]
    (when (nil? mlq-dir) (println "Usage: rangeview dir [port]") (System/exit 1))
    (jetty/run-jetty (partial handler mlq-dir) {:port port})))
