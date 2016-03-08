(ns orbit.core
  (:require [orbit.spath :as spath]))

(defn init []
  {:history [{:resources {}
              :step "init"}]})

(comment
  "a state is:"
  {:resources {"resource-name" '()}
   :step "step name"})

(defn- swap
  "helper function to modify last state by func and store in history"
  [orbit func]
  (conj orbit :history (func (last (orbit :history)))))

(defn resource
  "creates a new resource"
  [orbit resource-name]
  (swap orbit
        (fn [state]
          (-> state
              (assoc :step (str "create " resource-name))
              (assoc-in [:resources resource-name] '())))))

(defn step
  "applies collection of txs "
  [orbit step-name &txs]
  (swap orbit
        (fn [state]
          (->> (assoc state :step step-name)
               ; TODO apply each tx function in txs
               ; apply comp txs
               ))))





; transactions

; each tx returns a function that modifies state

(defn- tx
  "helper fn for creating txs; returns a function that modifies state"
  [r func]
  (fn [state]
    (update-in state [:resources r] func)))

(defn add [r form]
  (tx r (fn [forms]
          (conj forms form))))

(defn remove [r pattern]
  (tx r (fn [forms]
          forms ;TODO
          )))

(defn before [r pattern form]
  (tx r (fn [forms]
          forms ;TODO
          )))

(defn after [r pattern form]
  (tx r (fn [forms]
          forms ;TODO
          )))

(defn append [r pattern form]
  (tx r (fn [forms]
          forms ;TODO
          )))

(defn prepend [r pattern form]
  (tx r (fn [forms]
          forms ;TODO
          )))

(defn replace [r pattern form]
  (tx r (fn [forms]
          forms ;TODO
          )))

(defn wrap [r pattern form]
  (tx r (fn [forms]
          forms ;TODO
          )))

(defn assoc [r pattern k v]
  (tx r (fn [forms]
          forms ;TODO
          )))

(defn dissoc [r pattern k]
  (tx r (fn [forms]
          forms ;TODO
          )))

