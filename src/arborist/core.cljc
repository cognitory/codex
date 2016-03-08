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
  (z/zipper coll? seq (fn [n c] (with-meta c n)) data))

(defn follow-selector
  [data sel]
  (let [initial-sel (-> (zipper sel) z/leftmost z/down)]
    (loop [zp (zipper data)
           s initial-sel]
      (cond
        (z/end? zp) nil
        (z/end? s) zp

        (not= (z/node zp) (z/node s))
        (recur (z/next zp) initial-sel)

        (z/end? (z/next s)) zp
        (coll? (z/node (z/next s))) (recur (-> zp z/rightmost z/down)
                                           (-> s z/next z/down))
        true (recur (z/next zp) (z/next s))))))
