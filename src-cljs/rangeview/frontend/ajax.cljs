(ns rangeview.frontend.ajax
  (:require [ajax.core :refer [GET]]))

(defn log
  "Log an AJAX error"
  [& args]
  (.log js/console (str args)))

(defn fetch
  "Fetch a URL"
  [url handler]
  (GET url {:handler handler
            :error-handler log
            :response-format :json
            :keywords? true}))

(defn poll
  "Fetch a URL repeatedly"
  [url interval handler]
  (do
    (fetch url handler)
    (js/setInterval (fn [] (fetch url handler)) (* interval 1000))))
