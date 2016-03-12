(ns orbit.core
  (:refer-clojure :exclude [replace])
  (:require [arborist.core :as a]))

(defn init []
  {:history [{:resources {}
              :step "init"}]})

(comment
  "a state is:"
  {:resources {"resource-name" '()}
   :step "step name"})

(defn- advance
  "helper function to modify last state by func and store in history"
  [orbit func]
  (update orbit :history conj (func (last (orbit :history)))))

(defn step
  "applies collection of txs "
  [orbit step-name & txs]
  (advance orbit
    (fn [state]
      (-> state
          (assoc :step step-name)
          (update :resources
                  #(reduce (fn [st tx] (tx st)) % txs))))))

; transactions

(defn resource
  "creates a new resource"
  [resource-name]
  (fn [state]
    (assoc state resource-name [])))

; each tx returns a function that modifies state

(defn- tx
  "helper fn for creating txs; returns a function that modifies state"
  [r func]
  (fn [state]
    (update state r func)))

(defn add [r form]
  (tx r (fn [forms]
          (conj forms form))))

(defn before [r pattern form]
  (tx r (fn [forms]
          (a/insert-before forms pattern form))))

(defn after [r pattern form]
  (tx r (fn [forms]
          (a/insert-after forms pattern form))))

(defn append [r pattern form]
  (tx r (fn [forms]
          (a/append-at forms pattern form))))

(defn prepend [r pattern form]
  (tx r (fn [forms]
          (a/prepend-at forms pattern form))))

(defn wrap [r pattern wrap-form]
  (tx r (fn [forms]
          (a/wrap-with forms pattern wrap-form))))

(defn replace [r pattern form]
  (tx r (fn [forms]
          (a/replace-with forms pattern form))))

(comment

(defn remove [r pattern]
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
 )

