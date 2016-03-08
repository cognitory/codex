(ns orbit.core-test
  (:require #?(:cljs [cljs.test :refer-macros [is deftest testing]]
               :clj [clojure.test :refer :all])
            [orbit.core :as o]))

#?(:cljs (enable-console-print!))

(deftest steps-test
  (testing "can add resources as a step"
    (let [orb (-> (o/init)
                  (o/step "add-files"
                          (o/resource "core.cljs")))]
      (is (= (keys orb) [:history]))
      (is (= 2 (count (orb :history))))
      (is (= {:step "init" :resources {}}
             (first (orb :history))))
      (is (= {:step "add-files" :resources {"core.cljs" []}}
             (second (orb :history))))
      (testing "can add things to files"
        (let [orb (-> orb
                      (o/step "hello world"
                              (o/add "core.cljs"
                                '(ns foo.core
                                   (:require [foo.core :as f])))
                              (o/add "core.cljs"
                                '(enable-console-print!))
                              (o/add "core.cljs"
                                '(defn sq [x] (* x x)))))])))
    ))
