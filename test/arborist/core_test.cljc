(ns arborist.core-test
  (:require #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros [is deftest testing]])
            [arborist.core :as a]))

(deftest selector-test
  (let [sel '(defn app-view)
        data '[(ns foo) (defn app-view [stuff] stuff)]]
    (println (a/follow-selector data sel))
    ))
