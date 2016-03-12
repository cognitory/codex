(ns rustyspoon.orbit
  (:require [orbit.core :as o]
            [orbit.render :refer [render]]))

(defonce orbit
  (-> (o/init)

      (o/step "hello world"
              (o/resource "core.cljs")

              (o/add "core.cljs"
                '(ns rustyspoon.core
                   (:require [reagent.core :as r])))

              (o/add "core.cljs"
                '(enable-console-print!))

              (o/add "core.cljs"
                '(println "Hello Console!"))

              (o/add "core.cljs"
                (quote
                  ^{:id "app-view"}
                  (defn app-view []
                    [:div "Hello World!"])))

              (o/add "core.cljs"
                '(r/render-component [app-view] (.-body js/document))))

      (o/step "define restaurants array"
              (o/before "core.cljs"
                        '(defn app-view)
                        (quote
                          ^{:id "restaurants-data"}
                          (def restaurants
                            [{:name "Byblos"
                              :address "11 Duncan Street"
                              :image "kgXfBW9csGml_ZicwCB5Xg/ls.jpg"
                              :rating 4.5
                              :price-range 3 }
                             {:name "George"
                              :address "111 Queen St. E"
                              :image "gH783lm_UYR8b78s3Ul5Rg/ls.jpg"
                              :rating 4.4
                              :price-range 4 }
                             {:name "Kaiju"
                              :address "384 Yonge St."
                              :image "WQvsAGnWJcjUQQH3DMw8gA/ls.jpg"
                              :rating 4.3
                              :price-range 1 }
                             {:name "Richmond Station"
                              :address "1 Richmond St West"
                              :image "AGtyni4gZtoWSRz_U0Axwg/ls.jpg"
                              :rating 4.2
                              :price-range 3 }
                             {:name "Banh Mi Boys"
                              :address "392 Queen St. West"
                              :image "S1JS93tjQLqSwXMeWz0z7g/ls.jpg"
                              :rating 4.0
                              :price-range 1 }
                             {:name "Canoe"
                              :address "66 Wellington St."
                              :image "g0lZAilNKqlfQTNLUtWp3Q/ls.jpg"
                              :rating 3.9
                              :price-range 4 }]))))


      (o/step "update app-view to show restaurants"
              (o/replace "core.cljs"
                         "app-view"
                         '(defn app-view []
                            [:div
                             [:ul
                              (for [r restaurants]
                                [:li
                                 [:div.name (r :name)]
                                 [:div.address (r :address)]])]])))

      (o/step "show other restaurant info"
              (o/before "core.cljs"
                        '(def restaurants)
                        '(defn id->image [id]
                           (str "https://s3-media2.fl.yelpcdn.com/bphoto/" id)))

              (o/append "core.cljs"
                '(defn app-view [:div [:ul (for (:li))]])
                '[:div.rating (r :rating)])

              (o/append "core.cljs"
                '(defn app-view [:div [:ul (for (:li))]])
                '[:div.price-range (r :price-range)]))

      ;... more steps

      ))
(render orbit (.-body js/document))

