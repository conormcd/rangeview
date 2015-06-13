(ns rangeview.peers
  (:require [rangeview.utils :refer (filter-map)]))

(def peers (atom {}))

(defn peer-name
  "Given a hash describing a target, derive a name for it."
  [peer]
  (str (:host peer) ":" (:port peer)))

(defn all
  "All the peers of this instance of rangeview that are still alive within a
   given timeout window."
  [heartbeat-timeout]
  (let [deadline (- (System/currentTimeMillis) (* heartbeat-timeout 1000))]
    (filter-map #(> (:last-heartbeat %) deadline) @peers)))

(defn add
  "Add a peer"
  [peer]
  (swap! peers assoc (peer-name peer) (assoc peer
                                             :last-heartbeat
                                             (System/currentTimeMillis))))

(defn delete
  "Remove a peer"
  [peer]
  (swap! peers dissoc (peer-name peer)))
