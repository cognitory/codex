(ns codex.router
  (:require [secretary.core :as secretary]
            [goog.history.EventType :as EventType]
            [goog.events :as events])
  (:import [goog.history Html5History]
           [goog Uri]))

(defn init! []
  (secretary/set-config! :prefix "#")

  (let [h (Html5History.)]
    (doto h
      (.setUseFragment true)
      (.setPathPrefix "")
      (.setEnabled true))

    (events/listen h EventType/NAVIGATE
      (fn [e]
        (secretary/dispatch! (.-token e)))))

  (secretary/dispatch! (.getFragment (.parse Uri js/window.location))))

