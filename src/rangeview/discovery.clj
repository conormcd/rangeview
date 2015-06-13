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
  (let [sock (socket adv-address adv-port)
        pack (packet adv-address adv-port (pr-str {:port port}))]
    (async/go
      (while true
        (.send sock pack)
        (Thread/sleep (* interval 1000))))))

(defn listen
  "Listen for advertisements from other instances of rangeview."
  [interval]
  (let [sock (socket adv-address adv-port)
        packet (DatagramPacket. (byte-array 1024) 1024)]
    (async/go
      (while true
        (.receive sock packet)
        (let [source (-> packet .getAddress .getHostAddress)
              payload (-> packet .getData (String.) read-string)]
          (peers/add {:host source :port (:port payload)}))
        (Thread/sleep (* interval 1000))))))
