(ns rangeview.frontend.html)

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
  [id]
  (str
    "<div class=\"target\" id=\"target" id "\">"
      "<div id=\"target" id "-name\"></div>"
      "<canvas id=\"target" id "-canvas\"></canvas>"
      "<table>"
      (clojure.string/join 
        ""
        (for [shot (range 5)]
          (str
            "<tr>"
              "<td id=\"target" id "-shot" shot "\"></td>"
              "<td id=\"target" id "-shot" (+ shot 5) "\"></td>"
            "</tr>"
            )))
      "<tr>"
        "<td>Series:</td>"
        (str "<td id=\"target" id "-series-total\"></td>")
      "</tr>"
      "<tr>"
        "<td>Match:</td>"
        (str "<td id=\"target" id "-match-total\"></td>")
      "</tr>"
      "</table>"
    "</div>"))

(defn update-scores
  "Update the individual shot scores for a target."
  [target-id scores]
  (doseq [score (map-indexed vector scores)]
    (set-contents (str "target" target-id "-shot" (first score)) (second score))))

(defn update-series-score
  "Update the series score for a target."
  [target-id score]
  (set-contents (str "target" target-id "-series-total") score))

(defn update-match-score
  "Update the overall score for a target."
  [target-id score]
  (set-contents (str "target" target-id "-match-total") score))
