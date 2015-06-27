(ns rangeview.shots-test
  (:require [clojure.test :refer :all]
            [rangeview.shots :refer :all]))

(def sample-data [{:sighter true :inner false :score 9.0}
                  {:sighter true :inner false :score 9.1}
                  {:sighter true :inner false :score 9.2}
                  {:sighter true :inner false :score 9.3}
                  {:sighter true :inner false :score 9.4}
                  {:sighter true :inner false :score 9.5}
                  {:sighter true :inner false :score 9.6}
                  {:sighter true :inner false :score 9.7}
                  {:sighter true :inner false :score 9.8}
                  {:sighter true :inner false :score 9.9}
                  {:sighter true :inner false :score 10.0}
                  {:sighter true :inner false :score 10.1}
                  {:sighter true :inner false :score 10.2}
                  {:sighter true :inner true :score 10.3}
                  {:sighter true :inner true :score 10.4}
                  {:sighter true :inner true :score 10.5}
                  {:sighter true :inner true :score 10.6}
                  {:sighter true :inner true :score 10.7}
                  {:sighter true :inner true :score 10.8}
                  {:sighter true :inner true :score 10.9}
                  {:sighter false :inner false :score 9.0}
                  {:sighter false :inner false :score 9.1}
                  {:sighter false :inner false :score 9.2}
                  {:sighter false :inner false :score 9.3}
                  {:sighter false :inner false :score 9.4}
                  {:sighter false :inner false :score 9.5}
                  {:sighter false :inner false :score 9.6}
                  {:sighter false :inner false :score 9.7}
                  {:sighter false :inner false :score 9.8}
                  {:sighter false :inner false :score 9.9}
                  {:sighter false :inner false :score 10.0}
                  {:sighter false :inner false :score 10.1}
                  {:sighter false :inner false :score 10.2}
                  {:sighter false :inner true :score 10.3}
                  {:sighter false :inner true :score 10.4}
                  {:sighter false :inner true :score 10.5}
                  {:sighter false :inner true :score 10.6}
                  {:sighter false :inner true :score 10.7}
                  {:sighter false :inner true :score 10.8}
                  {:sighter false :inner true :score 10.9}])

(deftest last-series-nil
  (testing "(last-series nil) -> []"
    (is (= [] (last-series nil)))))

(deftest last-series-empty
  (testing "(last-series []) -> []"
    (is (= [] (last-series [])))))

(deftest last-series-simple
  (testing "last-series returns the last 10 shots"
    (is (= (take-last 10 sample-data) (last-series sample-data)))))

(deftest last-series-sighters-only
  (testing "last-series with only sighters returns sighters"
    (is (= (take-last 10 (sighters sample-data))
           (last-series (sighters sample-data))))))

(deftest last-series-match-shots-only
  (testing "last-series with only match shots returns record shots"
    (is (= (take-last 10 (match-shots sample-data))
           (last-series (match-shots sample-data))))))

(deftest last-series-cuts-on-boundaries-of-ten
  (testing "last-series deals correctly with partial series"
    (let [data (take 13 (match-shots sample-data))]
      (is (= (take-last 3 data) (last-series data))))))

(deftest match-shots-only-contains-match-shots
  (testing "match-shots returns only record shots"
    (is (= #{false} (set (map :sighter (match-shots sample-data)))))))

(deftest sighters-only-contains-sighting-shots
  (testing "sighters returns only sighter shots"
    (is (= #{true} (set (map :sighter (sighters sample-data)))))))

(deftest round-decimal-int
  (testing "(round-decimal 0) -> 0.0"
    (is (= "0.0" (round-decimal 0)))))

(deftest round-decimal-float
  (testing "(round-decimal 0.123) -> 0.1"
    (is (= "0.1" (round-decimal 0.123)))))

(deftest round-decimal-float-exact
  (testing "(round-decimal 0.1) -> 0.1"
    (is (= "0.1" (round-decimal 0.1)))))

(deftest score-sample-data
  (testing "Basic exercise of score with sample data."
    (is (= {:decimal "398.0" :integer 380 :inners 14} (score sample-data)))))

(deftest score->str-sample-data
  (testing "Basic exercise of score->str with sample data."
    (is (= "398.0 (380 14X)" (score->str (score sample-data))))))

(deftest shot->str-simple
  (testing "Simple test of shot->str"
    (is (= "10.1" (shot->str {:score 10.123 :inner false})))
    (is (= "10.3" (shot->str {:score 10.3 :inner false})))
    (is (= "10.9*" (shot->str {:score 10.9 :inner true})))))

(deftest shots->str-test
  (testing "Simple test of shots->str"
    (is (= "199.0 (190 7X)" (shots->str sample-data)))))
