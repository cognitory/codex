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

(defn right-colls
  "Find all subtrees right of the current zipper location that are collections"
  [z]
  (loop [z z
         r []]
    (cond
      (nil? z) r
      (coll? (z/node z)) (recur (z/right z) (conj r (z/down z)))
      true (recur (z/right z) r))))

(defn matches-at?
  [z sz]
  (cond
    (or (nil? sz) (z/end? sz)) (z/prev z)

    (or (nil? z) (z/end? z)) nil

    (not-any? (comp coll? z/node) [z sz])
    (and (= (z/node z) (z/node sz))
      (recur (z/right z) (z/right sz)))

    (coll? (z/node sz))
    (some #(matches-at? % (z/down sz)) (right-colls z))))

(defn follow-selector
  [data sel]
  (let [initial-sel (-> (zipper sel) z/leftmost z/down)]
    (loop [zp (zipper data)
           s initial-sel]
      (cond
        (z/end? zp) nil
        (z/end? s) zp

        true
        (if-let [match (matches-at? zp s)]
          match
          (recur (z/next zp) initial-sel))))))

(defn append-at
  [data sel to-insert]
  (-> (follow-selector data sel)
      z/rightmost
      (z/insert-right to-insert)
      z/root))

(defn prepend-at
  [data sel to-insert]
  (-> (follow-selector data sel)
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
