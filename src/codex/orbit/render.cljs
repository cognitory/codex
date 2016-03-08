(ns orbit.render
  (:require [reagent.core :as r]))

(defonce app-state (r/atom {}))

(defn- app-view [])

(defn- code-view [])

(defn- steps-view [])

(defn- orbit-view []
  [:div
   (app-view)
   (steps-view)
   (code-view)])

(defn render [orbit dom-target]
  (swap! app-state assoc
         :orbit orbit
         :step (- (count (orbit :history)) 1))
  (r/render-component [orbit-view] dom-target))
