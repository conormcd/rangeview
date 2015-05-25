(ns rangeview.target-test
  (:require [clojure.test :refer :all]
            [clojure.string :refer (join)]
            [rangeview.target :refer :all]))

(def discipline-keywords (keys disciplines))

(deftest all-disciplines-have-a-calibre
  (doseq [discipline discipline-keywords]
    (testing (str "(calibre " discipline ")")
      (let [calibre (calibre discipline)]
        (is (and (not (nil? calibre)) (> calibre 0)))))))

(deftest all-disciplines-have-a-spec
  (doseq [discipline discipline-keywords]
    (testing (str "(spec " discipline ")")
      (let [spec (spec discipline)]
        (is (not (nil? spec)))
        (is (contains? spec :ring-size))
        (is (contains? spec :inner-ten))
        (is (contains? spec :rings))))))

(deftest all-disciplines-have-rings
  (doseq [discipline discipline-keywords]
    (testing (str "(rings " discipline ")")
      (let [rings (rings discipline)]
        (is (seq? rings))
        (is (= 3 (first (set (map count rings)))))))))

(deftest validate-scores
  (doseq [test-data [[:fr60pr 0.0 0.0 10.9 true]
                     [:fr60pr 300.0 300.0 0.0 false]
                     [:fr60pr 14.85 7.15 8.9 false]
                     [:fr60pr 2.72 4.05 10.3 true]
                     [:fr60pr -5.28 0.84 10.3 false]]]
    (testing (str "(score " (join " " (subvec test-data 0 3)) ")")
      (let [s (apply score (subvec test-data 0 3))
            expected-score (nth test-data 3)
            should-be-inner? (nth test-data 4)]
        (is (= expected-score (:score s)))
        (is (= should-be-inner? (:inner s)))))))
