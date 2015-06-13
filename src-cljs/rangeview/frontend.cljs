(ns rangeview.frontend
  (:require [ajax.core :refer [GET]]
            [clojure.string :as str]
            [rangeview.frontend.ajax :as ajax]
            [rangeview.frontend.draw :as draw]
            [rangeview.frontend.html :as html]
            [rangeview.shots :as shots]
            [rangeview.target :as target]))

(def current-targets (atom {}))

(defn peers-url [] "/targets")
(defn peers-refresh-interval [] 5)
(defn state-url
  [peer]
  (str "http://" (:host peer) ":" (:port peer) "/target-state"))
(defn state-refresh-interval [] 5)

(defn refresh
  "Re-draw a target and all the score details"
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

(defn reset-targets
  "Redraw all the target diagrams and info from scratch."
  [targets]
  (let [target-urls (->> targets (map second) (map state-url))
        num-targets (count target-urls)
        target-width (/ (.-innerWidth js/window) num-targets)]
    (ajax/stop-all-polling (state-url {:host ".*" :port ".*"}))
    (if (empty? target-urls)
      (html/set-contents "targets" "No targets")
      (html/set-contents "targets" ""))
    (doseq [source (map-indexed vector target-urls)]
      (let [id (first source)
            url (second source)]
        (html/append-contents "targets" (html/target id target-width))
        (ajax/poll url (state-refresh-interval) (partial refresh id))))))

; Main UI loop. Query for changes in the list of peers and force a reload when
; they happen.
(ajax/poll (peers-url)
           (peers-refresh-interval)
           (fn [ajax-payload]
             (if (not= (keys ajax-payload) (keys @current-targets))
               (do
                 (reset! current-targets ajax-payload)
                 (reset-targets @current-targets)))))
