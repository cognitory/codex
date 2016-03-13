(ns rustyspoon.orbit
  (:require [orbit.core :as o]
            [orbit.render :refer [render]]))

(def orbit
  (-> (o/init)

      (o/step "init"
              (o/resource "core.cljs"))

      (o/step "hello world"
              (o/add "core.cljs"
                '(ns rustyspoon.core
                   (:require [reagent.core :as r]
                             [garden.core :as garden])))

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
                '(r/render [app-view] (js/document.getElementById "app"))))

      (o/step "define restaurants array"
              (o/before "core.cljs"
                        '(defn app-view)
                        (quote
                          ^{:id "restaurants-data"}
                          (def restaurants
                            [{:name "Byblos"
                              :address "11 Duncan Street"
                              :image "kgXfBW9csGml_ZicwCB5Xg"
                              :rating 4.5
                              :price-range 3 }
                             {:name "George"
                              :address "111 Queen St. E"
                              :image "gH783lm_UYR8b78s3Ul5Rg"
                              :rating 4.4
                              :price-range 4 }
                             {:name "Kaiju"
                              :address "384 Yonge St."
                              :image "WQvsAGnWJcjUQQH3DMw8gA"
                              :rating 4.3
                              :price-range 1 }
                             {:name "Richmond Station"
                              :address "1 Richmond St West"
                              :image "AGtyni4gZtoWSRz_U0Axwg"
                              :rating 4.2
                              :price-range 3 }
                             {:name "Banh Mi Boys"
                              :address "392 Queen St. West"
                              :image "S1JS93tjQLqSwXMeWz0z7g"
                              :rating 4.0
                              :price-range 1 }
                             {:name "Canoe"
                              :address "66 Wellington St."
                              :image "g0lZAilNKqlfQTNLUtWp3Q"
                              :rating 3.9
                              :price-range 4 }]))))


      (o/step "update app-view to show restaurants"
              (o/replace "core.cljs"
                         "app-view"
                         (quote
                           ^{:id "app-view"}
                           (defn app-view []
                             [:div.app
                              (for [r restaurants]
                                ^{:id "restaurant-div"}
                                [:div.restaurant {:key (r :name)}
                                 [:div.name (r :name)]
                                 [:div.address (r :address)]])]))))

      (o/step "add image function"
              (o/before "core.cljs"
                        '(def restaurants)
                        '(defn id->image [id]
                           (str "https://s3-media2.fl.yelpcdn.com/bphoto/" id "/ls.jpg"))))

      (o/step "display image"
              (o/before "core.cljs"
                        '(defn app-view [:div.app (for (:div.restaurant (:div.name)))])
                        '[:img {:src (id->image (r :image))}]))

      (o/step "show other restaurant info"


              (o/append "core.cljs"
                '(defn app-view [:div.app (for (:div.restaurant))])
                '[:div.rating (r :rating)])

              (o/append "core.cljs"
                '(defn app-view [:div.app (for (:div.restaurant))])
                '[:div.price-range (repeat (r :price-range) "$")]))

      (o/step "add styles"
              (o/before "core.cljs"
                        '(defn id->image)
                        '(def styles
                           (garden/css
                             (let [h "5em"]
                               [:.app
                                [:.restaurant
                                 {:height h
                                  :margin "1em"}
                                 [:img
                                  {:width h
                                   :height h
                                   :float "left"
                                   :margin-right "0.5em"}]
                                 [:.name
                                  {:font-weight "bold"}]
                                 [:.price-range
                                  {:color "green"}]]]))))

              (o/prepend "core.cljs"
                         '(defn app-view [:div.app])
                         '[:style styles]))

(o/step "factor out a restaurant-view"
        (o/before "core.cljs"
                  '(defn app-view)
                  '(defn restaurant-view [r]
                     [:div.restaurant
                      [:img {:src (id->image (r :image))}]
                      [:div.name (r :name)]
                      [:div.address (r :address)]
                      [:div.rating (r :rating)]
                      [:div.price-range
                       (repeat (r :price-range) "$")]]))

        (o/replace "core.cljs"
                   "restaurant-div"
                   (quote
                     [restaurant-view r])))

(o/step "add a header-view"
        (o/before "core.cljs"
                  '(defn restaurant-view)
                  '(defn header-view []
                     [:div.header
                      [:input.search {:placeholder "Search"}]
                      [:div.price-range
                       [:button "$"]
                       [:button "$$"]
                       [:button "$$$"]
                       [:button "$$$$"]]
                      [:div.sort
                       [:button.name "Name"]
                       [:button.rating "Rating"]]]))

        (o/before "core.cljs"
                  '(defn app-view (:div.app (for)))
                  '[header-view])

        (o/prepend "core.cljs"
                   '(def styles (garden/css (let (:.app))))
                   '[:.header
                     {:background "#CD5C5C"
                      :margin-bottom "1em"
                      :padding "1em"}
                     [:.search
                      {:width "100%"
                       :border-radius "5px"
                       :border "none"
                       :padding "0.5em"
                       :margin-bottom "1em"
                       :box-sizing "border-box"}]]))

(o/step "factor out a restaurant-list view"
        (o/before "core.cljs"
                  '(defn app-view)
                  (quote
                    ^{:id "restaurant-list-view"}
                    (defn restaurant-list-view []
                      [:div.restaurant-list
                       (for [r restaurants]
                         [restaurant-view r])])))

        (o/replace "core.cljs"
                   "app-view"
                   '(defn app-view []
                      [:div.app
                       [:style styles]
                       [header-view]
                       [restaurant-list-view]])))

(o/step "implementing sort toggle"
        (o/before "core.cljs"
                  '(def styles)
                  (quote
                    ^{:id "app-state"}
                    (def app-state (r/atom {:sort :rating}))))

        (o/replace "core.cljs"
                   "restaurant-list-view"
                   '(defn restaurant-list-view []
                      [:div.restaurant-list
                       (let [sort-key (@app-state :sort)]
                         (for [r (sort-by sort-key restaurants)]
                           [restaurant-view r]))]))

        (o/before "core.cljs"
                  '(def styles)
                  '(defn set-sort! [sort]
                     (swap! app-state assoc :sort sort)))

        (o/prepend "core.cljs"
                   '(defn header-view (:div.header (:div.sort (:button.name))))
                   '{:on-click (fn [_] (set-sort! :name))})

        (o/prepend "core.cljs"
                   '(defn header-view (:div.header (:div.sort (:button.rating))))
                   '{:on-click (fn [_] (set-sort! :rating))})
        )

      ;... more steps
      ))

(render orbit (.-body js/document))

