(ns rangeview.frontend.shots)

(defn last-series
  "Given a sequence of shots, extract the last series of 10 shots."
  [shots]
  (let [sighter? (:sighter (last shots))
        series-shots (filter #(= (:sighter %) sighter?) shots)
        n (mod (count series-shots) 10)]
    (take-last (if (> n 0) n 10) series-shots)))

(defn match-shots
  "Extract the match or record shots from a series of shots."
  [shots]
  (filter (complement :sighter) shots))

(defn sighters
  "Extract the sighting or practice shots from a series of shots."
  [shots]
  (filter :sighter shots))

(defn round-decimal
  "Round a decimal value to one decimal place."
  [value]
  (.toFixed value 1))

(defn score
  "Take a series of shots and score them in both decimal and integer"
  [shots]
  {:decimal (->> shots (map :score) (reduce +) (round-decimal))
   :integer (->> shots (map :score) (map int) (reduce +))
   :inners (->> shots (map :inner) (filter true?) (count))})

(defn score->str
  "Convert the result of score below to a string describing the score."
  [scores]
  (str (:decimal scores) " (" (:integer scores) " " (:inners scores) "X)"))

(defn shot->str
  "Convert a shot structure into a string describing that shot's score."
  [shot]
  (str (round-decimal (:score shot)) (if (:inner shot) "*" "")))

(defn shots->str
  "Convert a selection of shots into a total score with score->str."
  [shots]
  (->> shots (match-shots) (score) (score->str)))
