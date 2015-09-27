(ns rangeview.utils)

(defn filter-map
  "Filter a hash-map by value."
  [predicate hashmap]
  (into {} (filter #(predicate (second %)) hashmap)))
