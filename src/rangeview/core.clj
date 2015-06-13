(ns rangeview.core
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [rangeview.discovery :as discovery]
            [rangeview.mlq :as mlq]
            [rangeview.mlshoot :as mlshoot]
            [rangeview.peers :as peers]
            [ring.adapter.jetty :as jetty])
  (:gen-class))

(defn json-response
  [body & {:keys [status] :or {status 200}}]
  {:status status
   :headers {"Access-Control-Allow-Origin" "*"
             "Content-Type" "application/json"}
   :body (json/write-str body)})

(defn static-file
  [path content-type]
  (let [file (io/file path)]
    (if (.exists file)
      {:status 200
       :headers {"Access-Control-Allow-Origin" "*"
                 "Content-Type" content-type}
       :body (slurp file)}
      (json-response {"No such file " file} :status 404))))

(defn handler
  [mlq-dir heartbeat-timeout req]
  (case (:uri req)
    "/" (static-file "web/index.html" "text/html")
    "/css/rangeview.css" (static-file "web/css/rangeview.css" "text/css")
    "/js/rangeview.js" (static-file "web/js/rangeview.js" "application/javascript")
    "/target-state" (let [mlq-file (mlshoot/latest-mlq-path mlq-dir)]
                      (if (some? mlq-file)
                        (json-response (mlq/target-state mlq-file))
                        (json-response {:error "No target" :status 404})))
    "/targets" (json-response (peers/all heartbeat-timeout))
    (json-response {:error (str "No such path " (:uri req))} :status 404)))

(defn -main
  [& args]
  (let [mlq-dir (first args)
        port (or (some-> args second Integer/parseInt) 8080)
        heartbeat-frequency 5
        heartbeat-timeout 30]
    (when (some? mlq-dir)
      (discovery/advertise port heartbeat-frequency))
    (discovery/listen heartbeat-frequency)
    (jetty/run-jetty (partial handler mlq-dir heartbeat-timeout) {:port port})))
