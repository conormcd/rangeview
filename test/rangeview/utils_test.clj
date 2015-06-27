(ns rangeview.utils-test
  (:require [clojure.test :refer :all]
            [rangeview.utils :refer :all]))

(deftest filter-map-on-nil-produces-an-empty-hash
  (testing "(filter-map identity nil) -> {}"
    (is (= {} (filter-map identity nil)))))

(deftest filter-map-on-empty-hash-produces-an-empty-hash
  (testing "(filter-map identity {}) -> {}"
    (is (= {} (filter-map identity {})))))

(deftest filter-map-filters-on-value
  (testing "(filter-map #(= 2 %) {:foo 1 :bar 2}) -> {:bar 2}"
    (is (= {:bar 2} (filter-map #(= 2 %) {:foo 1 :bar 2})))))
