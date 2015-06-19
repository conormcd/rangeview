(ns rangeview.discovery
  (:require [clojure.core.async :as async]
            [rangeview.peers :as peers])
  (:import [java.net DatagramPacket InetAddress MulticastSocket]))

(def adv-address (InetAddress/getByName "224.0.0.42"))
(def adv-port 12345)

(defn socket [address port]
  "Create a multicast socket."
  (let [s (MulticastSocket. port)]
    (.joinGroup s address)
    s))

(defn packet
  "Create a UDP packet."
  [address port payload]
  (DatagramPacket. (.getBytes payload) (.length payload) address port))

(defn advertise
  "Advertise this host as listening on a specific port."
  [port interval]
  (async/go
    (while true
      (try
        (let [sock (socket adv-address adv-port)
              pack (packet adv-address adv-port (pr-str {:port port}))]
          (while true
            (try
              (.send sock pack)
              (catch java.io.IOException e))
            (Thread/sleep (* interval 1000))))
        (catch java.io.IOException e))
      (Thread/sleep 1000))))

(defn listen
  "Listen for advertisements from other instances of rangeview."
  [interval]
  (async/go
    (while true
      (let [sock (socket adv-address adv-port)
            packet (DatagramPacket. (byte-array 1024) 1024)]
        (while true
          (try
            (.receive sock packet)
            (let [source (-> packet .getAddress .getHostAddress)
                  payload (-> packet .getData (String.) read-string)]
              (peers/add {:host source :port (:port payload)}))
            (catch java.io.IOException e))
            (Thread/sleep (* interval 1000))))
      (Thread/sleep 1000))))
