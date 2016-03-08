(ns arborist.core-test
  (:require #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros [is deftest testing]])
            [clojure.zip :as z]
            [arborist.core :as a]))

(deftest selector-finding
  (testing "can search for things with zippers"
    (let [sel '(defn app-view)
          data '[(ns foo)
                 (defn app-view [stuff] stuff)]]
      (is (= (a/follow-selector data sel)
             (-> (a/zipper data) z/down z/right z/down z/next))))))
