(ns rangeview.core
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.tools.cli :refer (parse-opts)]
            [clojure.tools.nrepl.server :as nrepl]
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
    (when (.exists file)
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

(defn enable-repl
  [port]
  (nrepl/start-server :port port))

(defn parse-args
  [args]
  (parse-opts args [["-d" "--directory DIR" "The directory containing the MLQ files."
                     :validate [#(.exists (io/as-file %)) "Directory must exist."]]
                    ["-p" "--port PORT" "HTTP API port."
                     :default 8080
                     :parse-fn #(Integer/parseInt %)
                     :validate [#(< 0 % 65536) "Must be a valid port number"]]
                    [nil "--heartbeat-frequency SECONDS" "Seconds between advertising to other rangeview instances"
                     :default 5
                     :parse-fn #(Integer/parseInt %)
                     :validate [#(< 0 %) "Must be a positive number of seconds."]]
                    [nil "--heartbeat-timeout SECONDS" "The longest a peer can go without heartbeating before being considered gone away."
                     :default 30
                     :parse-fn #(Integer/parseInt %)
                     :validate [#(< 0 %) "Must be a positive number of seconds."]]
                    [nil "--help" "Show how to run this program."]
                    [nil "--nrepl-port PORT" "Run a nREPL on this port."
                     :parse-fn #(Integer/parseInt %)
                     :validate [#(< 0 % 65536) "Must be a valid port number"]]]))

(defn print-usage-message [opts]
  (println "Usage: rangeview [options]\n")
  (println (-> opts :summary)))

(defn -main
  [& args]
  (let [opts (parse-args args)
        heartbeat-frequency (-> opts :options :heartbeat-frequency)
        heartbeat-timeout (-> opts :options :heartbeat-timeout)
        mlq-dir (-> opts :options :directory)
        port (-> opts :options :port)
        repl-port (-> opts :options :nrepl-port)]
    (when (-> opts :options :help)
      (print-usage-message opts)
      (System/exit 0))
    (when (-> opts :errors)
      (doseq [error (-> opts :errors)]
        (println error))
      (println "")
      (print-usage-message opts)
      (System/exit 1))
    (when repl-port
      (enable-repl repl-port))
    (when mlq-dir
      (discovery/advertise port heartbeat-frequency))
    (discovery/listen heartbeat-frequency)
    (jetty/run-jetty (partial handler mlq-dir heartbeat-timeout) {:port port})))
