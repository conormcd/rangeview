(ns rangeview.frontend.cron)

(def jobs (atom {}))

(defn start
  "Start a repeating job."
  [job-name interval job]
  (swap! jobs assoc job-name (js/setInterval job (* interval 1000))))

(defn stop
  "Stop a repeating job."
  [job-name]
  (do
    (js/clearInterval (get @jobs job-name))
    (swap! jobs dissoc job-name)))

(defn stop-all
  "Stop all jobs, optionally limiting to jobs with the supplied prefix."
  [& args]
  (let [name-regex (or (first args) #".*")]
    (doseq [job-name (filter #(re-matches name-regex %) (keys @jobs))]
      (stop job-name))))
