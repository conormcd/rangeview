(ns rangeview.mlq
  (:require [clojure.java.jdbc :refer :all]
            [rangeview.target :as target]))

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

(defn discipline
  "Figure out which type of target was used in the MLQ"
  [mlq]
  (case (Integer/parseInt (param mlq "TargetID"))
    4 :fr60pr
    21 :ar60))

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
  [discipline shot]
  (merge shot (target/score discipline (:x shot) (:y shot))))

(defn shot-series
  "Convert the shot series number to a boolean for sighters"
  [shot]
  (let [series (:series shot)]
    (assoc (dissoc shot :series) :sighter (<= series 1))))

(defn shots
  "Pull the shots from an MLQ"
  [mlq]
  (let [scale (target-distance-scale mlq)
        discipline (discipline mlq)]
    (->> (query (db mlq) "SELECT id, series,
                            PRINTF(\"%d\", x) AS x,
                            PRINTF(\"%d\", y) AS y
                          FROM Shots")
         (map shot-convert)
         (map shot-series)
         (map (partial shot-scale scale))
         (map (partial shot-score discipline))
         )))

(defn target-state
  "Get the current state of a target as seen in an MLQ"
  [mlq]
  {:name (param mlq "CardName")
   :discipline (discipline mlq)
   :shots (shots mlq)})
