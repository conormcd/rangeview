(ns rangeview.mlq-test
  (:require [clojure.test :refer :all]
            [rangeview.mlq :refer :all]
            [rangeview.mlshoot :as mlshoot]
            [rangeview.target :as target]))

(def test-mlq-dir "test/data")
(def test-mlq-files
  (map #(.getAbsolutePath %) (mlshoot/all-mlq-files test-mlq-dir)))
(def sample-mlq (first test-mlq-files))

(deftest param-fetches-params
  (testing "Ensure that param fetches from the Param table"
    (is (= "4" (param sample-mlq "TargetID")))))

(deftest param-returns-nil-for-missing-params
  (testing "Get nil when a parameter does not exist."
    (is (nil? (param sample-mlq "NotAThing")))))

(deftest shot-scale-scales-x-value
  (testing "shot-scale scales the X value of a shot"
    (is (= 8.0 (:x (shot-scale 2/1 {:x 4.0 :y 4.0}))))))

(deftest shot-scale-scales-y-value
  (testing "shot-scale scales the Y value of a shot"
    (is (= 8.0 (:y (shot-scale 2/1 {:x 4.0 :y 4.0}))))))

(defn validate-score
  [discipline x y expected-score inner]
  (let [score (shot-score discipline {:x x :y y})]
    (and
      (= (:score score) expected-score)
      (= (:inner score) inner))))
(deftest validate-scores
  (doall
    (for [test-data [[:fr60pr 0.0 0.0 10.9 true]
                     [:fr60pr 300.0 300.0 0.0 false]
                     [:fr60pr 14.85 7.15 8.9 false]
                     [:fr60pr 2.72 4.05 10.3 true]
                     [:fr60pr -5.28 0.84 10.3 false]]]
      (testing (str "(validate-scores " test-data ")")
        (is (apply validate-score test-data))))))

(deftest shot-series-1-is-sighter
  (testing "Series 1 is a sighting series"
    (is (:sighter (shot-series {:series 1})))))

(deftest shot-series-2-is-match
  (testing "Series 1 is a sighting series"
    (is (not (:sighter (shot-series {:series 2}))))))

(deftest shot-ids-are-a-contiguous-series
  (testing "Shot IDs are a contiguous series"
    (is (let [shot-ids (map :id (shots sample-mlq))]
          (= shot-ids (range 1 (+ 1 (reduce max shot-ids))))))))

(deftest no-sighters-after-match-shots
  (testing "There are no sighting shots after match shots"
    (is (= [true false]
           (->> (shots sample-mlq)
                (map :sighter)
                (partition-by identity)
                (map first))))))

(deftest no-inner-tens-less-than-10-3
  (testing "All inner tens are at least 10.3"
    (is (>= 10.3
            (->> (shots sample-mlq)
                 (filter :inner)
                 (map :score)
                 (reduce min))))))

;
; Integrity checks for the test MLQs. These repeat some of the tests above but
; are designed not to test the code but the MLQs.
;

(defn true-for-all-mlq-files
  [f]
  (empty? (filter false? (map f test-mlq-files))))
(defn false-for-all-mlq-files
  [f]
  (empty? (filter true? (map f test-mlq-files))))

(deftest mlq-files-contain-distance-actual
  (testing "All MLQ files have a param 'DistanceActual'"
    (is (false-for-all-mlq-files #(nil? (param % "DistanceActual"))))))

(deftest mlq-files-contain-distance-simulated
  (testing "All MLQ files have a param 'DistanceSimulated'"
    (is (false-for-all-mlq-files #(nil? (param % "DistanceSimulated"))))))

(deftest mlq-files-contain-target-id
  (testing "All MLQ files have a param 'TargetId'"
    (is (false-for-all-mlq-files #(nil? (param % "TargetID"))))))

(deftest mlq-files-have-ring-size
  (testing "All MLQ files have a valid, defined ring size"
    (is (true-for-all-mlq-files
          #(> (:ring-size (target/spec (discipline %))) 0)))))

(deftest mlq-files-have-inner-ten
  (testing "All MLQ files have a valid, defined inner ten size"
    (is (true-for-all-mlq-files
          #(> (:inner-ten (target/spec (discipline %))) 0)))))
