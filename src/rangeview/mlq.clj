(ns rangeview.mlq
  (:require [clojure.java.jdbc :refer :all]))

(defn db
  "Get JDBC database config details for an MLQ"
  [mlq]
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     mlq})

(defn param
  "Fetch a parameter from an MLQ"
  [mlq param_name]
  (-> (query (db mlq) ["SELECT Data FROM Param WHERE Id = ?" param_name])
      first
      :data))

(defn target-distance-scale
  "Find out the distance scaling factor for an MLQ"
  [mlq]
  (/
    (Integer/parseInt (param mlq "DistanceSimulated"))
    (Integer/parseInt (param mlq "DistanceActual"))))

(defn target-params
  "Get the characteristics of a target"
  [mlq]
  (case (Integer/parseInt (param mlq "TargetID"))
    4 {:ring-size 8 :inner-ten 5}
    21 {:ring-size 2.5 :inner-ten 0.5}))

(defn shot-convert
  "Re-intepret the integer values from the MLQ as the floats they really are."
  [shot]
  (assoc shot
         :x (Float/intBitsToFloat (Integer/parseInt (:x shot)))
         :y (Float/intBitsToFloat (Integer/parseInt (:y shot)))))

(defn shot-scale
  "Scale the coordinates of the shot."
  [scale shot]
  (assoc shot
         :x (* scale (:x shot))
         :y (* scale (:y shot))))

(defn shot-score
  "Score the shot according to the target it was shot at."
  [target shot]
  (let [x (:x shot)
        y (:y shot)
        error (Math/sqrt (+ (* x x) (* y y)))
        ring-size (:ring-size target)
        inner-ten (:inner-ten target)]
    (assoc shot
           :score (max 0.0 (min 10.9 (- 11 (* 0.1 (Math/ceil (/ error (* ring-size 0.1)))))))
           :inner (< error inner-ten))))

(defn shot-series
  "Convert the shot series number to a boolean for sighters"
  [shot]
  (let [series (:series shot)]
    (assoc (dissoc shot :series) :sighter (<= series 1))))

(defn shots
  "Pull the shots from an MLQ"
  [mlq]
  (let [scale (target-distance-scale mlq)
        target (target-params mlq)]
    (map
      (fn [row]
        (shot-score target
          (shot-scale scale 
            (shot-series
              (shot-convert row)))))
      (query (db mlq) "SELECT PRINTF(\"%d\", x) AS x, PRINTF(\"%d\", y) AS y, series, id FROM Shots"))))

(defn discipline
  "Figure out which type of target was used in the MLQ"
  [mlq]
  (case (Integer/parseInt (param mlq "TargetID"))
    4 :fr60pr
    21 :ar60))

(defn target-state
  "Get the current state of a target as seen in an MLQ"
  [mlq]
  {:name (param mlq "CardName")
   :discipline (discipline mlq)
   :shots (shots mlq)})
