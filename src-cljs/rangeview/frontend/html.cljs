(ns rangeview.frontend.html
  (:require [clojure.string :refer (join)]))

(defn element
  "Get a DOM element by ID"
  [id]
  (.getElementById js/document id))

(defn get-contents
  "Get the innerHTML of a DOM element."
  [id]
  (.-innerHTML (element id)))

(defn set-contents
  "Set the innerHTML of a DOM element."
  [id contents]
  (set! (.-innerHTML (element id)) contents))

(defn append-contents
  "Append to the innerHTML of a DOM element"
  [id contents]
  (set-contents id (str (get-contents id) contents)))

(defn target
  "The HTML for a single target's display."
  [id width]
  (str
    "<div class=\"target\" id=\"target" id "\" width=\"" width "\">"
      "<div id=\"target" id "-name\" class=\"name\"></div>"
      "<canvas id=\"target" id "-canvas\" width=\"" width "\"></canvas>"
      "<table id=\"target" id "-infobox\" class=\"infobox\" width=\"" width "\">"
      (join
        ""
        (for [shot (range 5)]
          (str
            "<tr>"
              "<td id=\"target" id "-shot" shot "\"></td>"
              "<td id=\"target" id "-shot" (+ shot 5) "\"></td>"
              (case shot
                0 "<td>Series</td>"
                1 (str "<td id=\"target" id "-series-total\"></td>")
                2 ""
                3 "<td>Match</td>"
                4 (str "<td id=\"target" id "-match-total\"></td>"))
            "</tr>"
            )))
      "</table>"
    "</div>"))

(defn update-name
  "Update the name of the card."
  [target-id card-name]
  (set-contents (str "target" target-id "-name") card-name))

(defn update-canvas-height
  "Reset the canvas height to window.innerHeight minus the name & infobox"
  [id]
  (let [total-height (.-innerHeight js/window)
        name-height (.-offsetHeight (element (str "target" id "-name")))
        info-height (.-offsetHeight (element (str "target" id "-infobox")))
        canvas-height (- total-height (+ 5 name-height info-height))]
    (set! (.-height (element (str "target" id "-canvas"))) canvas-height)))

(defn update-scores
  "Update the individual shot scores for a target."
  [target-id scores]
  (doseq [score (concat
                  (map #(seq [% ""]) (range 10))
                  (map-indexed vector scores))]
    (set-contents (str "target" target-id "-shot" (first score)) (second score))))

(defn update-series-score
  "Update the series score for a target."
  [target-id score]
  (set-contents (str "target" target-id "-series-total") score))

(defn update-match-score
  "Update the overall score for a target."
  [target-id score]
  (set-contents (str "target" target-id "-match-total") score))
