(ns rangeview.mlshoot)

(defn all-mlq-files
  [dir]
  (filter #(re-find #"\.mlq$" (.getPath %)) (file-seq (clojure.java.io/file dir))))

(defn latest-mlq-path
  [dir]
  (let [all-files (all-mlq-files dir)
        all-times (map #(.lastModified %) all-files)]
    (.getAbsolutePath (key (apply max-key val (zipmap all-files all-times))))))
