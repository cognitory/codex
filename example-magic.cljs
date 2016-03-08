(require '[tour.steps :refer [deftour verify-step-body! add-step!]])

(deftour hello-world-tour
  "core.cljs")

(add-step! hello-world-tour "hello world"
  "core.cljs" :add '(ns rustyspoon.core
                      (:require [reagent.core :as r]))

  "core.cljs" :add '(enable-console-print!)

  "core.cljs" :add '(println "Hello Console!")

  "core.cljs" :add '(defn app-view []
                      [:div "Hello World!"])

  "core.cljs" :add '(r/render-component [app-view] (.-body js/document)))

(add-step! hello-world-tour "define restaurants array"
  "core.cljs" :before '(defn app-view)
  '(def restaurants
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
       :price-range 4 }]))

(add-step! hello-world-tour "update app-view to show restaurants"
  "core.cljs" :replace '(defn app-view)
  '(defn app-view []
     [:div
      [:ul
       (for [r restaurants]
         [:li
          [:div.name (r :name)]
          [:div.address (r :address)]])]]))

(add-step! hello-world-tour "show other restaurant info"
  "core.cljs" :before '(def restaurants)
  '(defn id->image [id]
     (str "https://s3-media2.fl.yelpcdn.com/bphoto/" id))

  "core.cljs" :append '(defn app-view [:div [:ul (for (:li))]])
  '[:div.rating (r :rating)]

  "core.cljs" :append '(defn app-view [:div [:ul (for (:li))]])
  '[:div.price-range (r :price-range)])

(output-tour hello-world-tour)
