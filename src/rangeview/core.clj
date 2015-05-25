(ns rangeview.core
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [rangeview.mlq :as mlq]
            [rangeview.mlshoot :as mlshoot]
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
  [mlq-dir req]
  (case (:uri req)
    "/" (static-file "web/index.html" "text/html")
    "/css/rangeview.css" (static-file "web/css/rangeview.css" "text/css")
    "/js/rangeview.js" (static-file "web/js/rangeview.js" "application/javascript")
    "/target-state" (json-response (mlq/target-state (mlshoot/latest-mlq-path mlq-dir)))
    (json-response {:error (str "No such path " (:uri req))} :status 404)))

(defn -main
  [& args]
  (let [mlq-dir (first args)
        port (or (some-> args second Integer/parseInt) 8080)]
    (when (nil? mlq-dir) (println "Usage: rangeview dir [port]") (System/exit 1))
    (jetty/run-jetty (partial handler mlq-dir) {:port port})))
