(ns rangeview.frontend
  (:require [ajax.core :refer [GET]]
            [clojure.string :as str]
            [rangeview.frontend.draw :as draw]
            [rangeview.frontend.html :as html]
            [rangeview.frontend.shots :as shots]
            [rangeview.frontend.target :as target]))

(defn ajax-error
  "Log an AJAX error"
  [& args]
  (.log js/console (str args)))

(defn refresh
  "Re-draw the target and all the score details"
  [target-id ajax-payload]
  (let [discipline (keyword (:discipline ajax-payload))
        shots (:shots ajax-payload)
        series (shots/last-series shots)]
    (html/update-scores target-id (map shots/shot->str series))
    (html/update-series-score target-id (shots/shots->str series))
    (html/update-match-score target-id (shots/shots->str shots))
    (draw/repaint
      target-id
      5 ; TODO: Calculate this correctly.
      (target/rings discipline)
      (target/calibre discipline)
      series)))

(defn fetch
  "Do a single refresh of a URL and update a target with the result."
  [id url]
  (GET url {:handler (partial refresh id)
            :error-handler ajax-error
            :response-format :json
            :keywords? true}))

(defn poll
  "Poll a URL and update a target."
  [id url interval]
  (do
    (fetch id url)
    (js/setInterval (fn [] (fetch id url)) (* interval 1000))))

(defn target-addresses
  "Extract all of the target addresses from the URL"
  []
  (-> js/window .-location .-hash (subs 1) (str/split #",")))

(doseq [source (map-indexed vector (target-addresses))]
  (let [id (first source)
        url (str "http://" (second source) "/")]
    (html/append-contents "targets" (html/target id))
    (poll id url 5)))
