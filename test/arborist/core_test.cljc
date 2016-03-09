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
             (-> (a/zipper data) z/down z/right z/down z/next))))
    (let [sel '(defn app-view)
          data '[(ns foo)
                 (def foo {:x 1 :bar ["foo" "bar"]})
                 (defn app-view [stuff] stuff)]]
      (is (= (a/follow-selector data sel)
             (-> (a/zipper data) z/down z/right z/right z/down z/next))))
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
      (is (= (z/node (z/up (a/follow-selector data sel)))
             '[:li
               [:div.name (r :name)]
               [:div.address (r :address)]])))))

(deftest modifying-tree
  (testing "can insert things after"
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
      (is (= (a/insert-after data sel '[:div.rating (r :rating)])
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
