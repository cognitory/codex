(ns arborist.core-test
  (:require #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros [is deftest testing]])
            [clojure.zip :as z]
            [arborist.core :as a]))

(deftest matching-test
  (testing "can see if selector matches at location"
    (let [zp (z/next (a/zipper '(defn foo [x] (inc x))))
          s (z/next (a/zipper '(defn foo (inc))))]
     (is (a/matches-at? zp s)))))

(deftest selector-finding
  (testing "failing query"
    (is (nil? (a/follow-selector '[(ns foo) (defn app-view [x] x)]
                                 '(defn foobar))))
    (is (nil? (a/follow-selector '[(ns foo) (defn app-view [x] (inc x))]
                                 '(defn app-view (dec))))))
  (testing "simple search"
    (let [sel '(defn app-view)
          data '[(ns foo)
                 (defn app-view [stuff] stuff)]]
      (is (= (a/follow-selector data sel)
             (-> (a/zipper data) z/down z/right z/down z/next))))
    (let [sel '(defn app-view)
          data '[(ns foo)
                 (def foo {:x 1 :bar ["foo" "bar"]})
                 (defn app-view [stuff] stuff)]]
      (is (= (a/follow-selector data sel)
             (-> (a/zipper data) z/down z/right z/right z/down z/next)))))
  (testing "nested query"
    (let [sel '(defn app-view [:div [:ul (for [:li])]])
          data '[(ns foo)
                 (def foo {:x 1 :bar ["foo" "bar"]})
                 (defn app-view []
                   [:div
                    [:ul
                     (for [r restaurants]
                       [:li
                        [:div.name (r :name)]
                        [:div.address (r :address)]])]])]]
      (is (= (z/node (z/up (a/follow-selector data sel)))
             '[:li
               [:div.name (r :name)]
               [:div.address (r :address)]]))))
  (testing "multi-branch query"
    (let [data '[(foo [bar 1] [baz 2] [quux 3])]]
      (is (some? (a/follow-selector data '(foo [baz])))))))

(deftest modifying-tree
  (testing "can append things"
    (let [sel '(defn app-view [:div [:ul (for (:li))]])
          data '[(ns foo)
                 (def foo {:x 1 :bar ["foo" "bar"]})
                 (defn app-view []
                   [:div
                    [:ul
                     (for [r restaurants]
                       [:li
                        [:div.name (r :name)]
                        [:div.address (r :address)]])]])]]
      (is (= (a/append-at data sel '[:div.rating (r :rating)])
             '[(ns foo)
               (def foo {:x 1 :bar ["foo" "bar"]})
               (defn app-view []
                 [:div
                  [:ul
                   (for [r restaurants]
                     [:li
                      [:div.name (r :name)]
                      [:div.address (r :address)]
                      [:div.rating (r :rating)]])]])]))))
  (testing "can insert after"
    (let [sel '(defn app-view)
          data '[(ns foo) (defn app-view [x] (inc x))]]
      (is (= (a/insert-after data sel '(println "okay!"))
             '[(ns foo)
               (defn app-view [x] (inc x))
               (println "okay!")]))))
  (testing "can insert before"
    (let [sel '(defn app-view)
          data '[(ns foo)
                 (enable-console-print!)
                 (defn app-view [] [:div "hello"])]]
      (is (= (a/insert-before data sel '(def stuff [{:a 1 :b 2} {:a 7 :b 12}]))
             '[(ns foo)
              (enable-console-print!)
              (def stuff [{:a 1 :b 2} {:a 7 :b 12}])
              (defn app-view [] [:div "hello"])])))))
