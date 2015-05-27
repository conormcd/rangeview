(ns rangeview.frontend
  (:require [ajax.core :refer [GET]]
            [clojure.string :as str]
            [rangeview.frontend.ajax :as ajax]
            [rangeview.frontend.draw :as draw]
            [rangeview.frontend.html :as html]
            [rangeview.shots :as shots]
            [rangeview.target :as target]))

(defn refresh
  "Re-draw the target and all the score details"
  [target-id ajax-payload]
  (let [card-name (or (:name ajax-payload))
        discipline (keyword (:discipline ajax-payload))
        shots (:shots ajax-payload)
        series (shots/last-series shots)]
    (html/update-name target-id (if (str/blank? card-name) "&nbsp;" card-name))
    (html/update-scores target-id (map shots/shot->str series))
    (html/update-series-score target-id (shots/shots->str series))
    (html/update-match-score target-id (shots/shots->str shots))
    (html/update-canvas-height target-id)
    (draw/repaint
      target-id
      (target/rings discipline)
      (target/calibre discipline)
      series)))

(defn target-addresses
  "Extract all of the target addresses from the URL"
  []
  (-> js/window .-location .-hash (subs 1) (str/split #",")))

(let [num-targets (count (target-addresses))
      target-width (/ (.-innerWidth js/window) num-targets)]
  (doseq [source (map-indexed vector (target-addresses))]
    (let [id (first source)
          url (str "http://" (second source) "/target-state")]
      (html/append-contents "targets" (html/target id target-width))
      (ajax/poll url 5 (partial refresh id)))))
