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
  (z/zipper
    coll?
    seq
    (fn [n c]
      (-> (cond
            (vector? n) (vec c)
            (map? n) (into {} c)
            true c)
        (with-meta (meta n))))
    data))

(defn matches-at?
  [z sz]
  (println "z" z "sz" sz)
  (cond
    (nil? z) true
    (z/end? z) true

    (nil? sz) nil
    (z/end? sz) nil

    (not-any? (comp coll? z/node) [z sz])
    (and (= (z/node z) (z/node sz))
      (recur (z/right z) (z/right sz)))

    (every? (comp coll? z/node) [z sz])
    (recur (z/down z) (z/down sz)) ))

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

(defn append-at
  [data sel to-insert]
  (-> (follow-selector data sel)
      z/rightmost
      (z/insert-right to-insert)
      z/root))

(defn insert-after
  [data sel to-insert]
  (-> (follow-selector data sel)
      z/up
      (z/insert-right to-insert)
      z/root))

(defn insert-before
  [data sel to-insert]
  (-> (follow-selector data sel)
      z/up
      (z/insert-left to-insert)
      z/root))
