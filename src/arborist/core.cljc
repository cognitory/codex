(ns arborist.core
  (:require [clojure.zip :as z]))

(comment (defn map-zipper [m]
  (z/zipper
    (fn [x] (or (map? x) (map? (nth x 1))))
    (fn [x] (seq (if (map? x) x (nth x 1))))
    (fn [x children]
      (if (map? x)
        (into {} children)
        (assoc x 1 (into {} children))))
    m)))

(defn zipper
  [data]
  (z/zipper coll? seq (fn [_ c] c) data))

(defn follow-selector
  [data sel]
  (loop [zp (zipper data)
         s sel]
    (if (empty? s)
      zp

      )))
