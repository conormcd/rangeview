(ns rangeview.frontend.ajax
  (:require [ajax.core :refer [GET]]
            [rangeview.frontend.cron :as cron]))

(defn log
  "Log an AJAX error"
  [& args]
  (.log js/console (str args)))

(defn fetch
  "Fetch a URL"
  [url handler]
  (GET url {:handler handler
            :error-handler log
            :response-format :json
            :keywords? true}))

(defn poll
  "Fetch a URL repeatedly"
  [url interval handler]
  (do
    (fetch url handler)
    (cron/start (str "poll-" url) interval #(fetch url handler))))

(defn stop-polling
  "Stop repeatedly fetching a URL"
  [url]
  (cron/stop (str "poll-" url)))

(defn stop-all-polling
  "Stop all the AJAX polling we're doing. Optionally restrict it to a pattern
   by passing the string form of a regex for the URLs to match."
  [& args]
  (let [url-pattern-str (or (first args) ".*")]
   (cron/stop-all (re-pattern (str "^poll-" url-pattern-str)))))
