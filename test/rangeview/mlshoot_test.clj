(ns rangeview.mlshoot-test
  (:require [clojure.test :refer :all]
            [rangeview.mlshoot :refer :all]))

(def test-mlq-dir "test/data")
(def latest-mlq-file
  (.getAbsolutePath (clojure.java.io/file "test/data/Card061.mlq")))

(deftest all-mlq-files-returns-some-files
  (testing "all-mlq-files returns some files"
    (is (> (count (all-mlq-files test-mlq-dir)) 0))))

(deftest latest-mlq-path-returns-expected-value
  (testing "We know which test MLQ is 'latest'; ensure it's returned"
    (.setLastModified (clojure.java.io/file latest-mlq-file) (System/currentTimeMillis))
    (is (= (latest-mlq-path test-mlq-dir) latest-mlq-file))))
