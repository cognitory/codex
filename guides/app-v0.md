---
title: Building a Simple App
description: TODO
dependencies: [ setup-env ]
---

demo of what we're going to build:
  [Rustyspoon Demo](http://cognitory.github.io/rustyspoon/index.html)

 - set up your system: [[guides/setup-env]]

 - set up the start of your application: [[guides/app-hello-world]]



# Making a List of Restaurants to Display

Let's start by displaying a list of restaurants. To do that, we first need information about a few restaurants. We will create a 'vector' of 'maps', each map containing information about a restaurant.

Inside of of `core.cljs` add the following below `(enable-console-print!)`:

```clojure
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
    :price-range 4 }])
```

`def` is used to gives names to things that we will use later. Here, we define a list of restaurants using `(def restaurants ...)`

The square brackets `[ ... ]` are used to create a *vector*, which is an ordered list of things. For example, here is a vector of a few numbers: `[ 1 3 5 6 7 ]`.

Our restaurant vector is a list of maps. A *map* is created by using brackets `{ ... }`. A map contains multiple *keys* and *values*, with each *key* corresponding to some *value* -- you can think of it like a dictionary, where words are keys and their definitions are values. Or a phonebook (names = keys, numbers = values).

In our restaurant vector, each map represents a restaurant. Each of these contains the `:name`, `:address`, `:image`, `:rating` and `:price-range` keys, with either strings or numbers as values.

Let's get these restaurants showing!

Replace your `(defn app-view ...)` with the following:

```clojure
(defn app-view []
  [:div
   [:ul
    (for [r restaurants]
      [:li
       [:div.name (r :name)]
       [:div.address (r :address)]])]])
```

Save the file. Your browser should immediately update and show a list of restaurants, each with a name and address.

`(defn app-view ...)` defines a *function* called `app-view`. A function is like a mini program that can take in various inputs and, manipulate those inputs, and return an output. For example, the following function will squares an number passed into it: `(defn square [x] (* x x))` This function can be used in other parts of our program by writing: `(square 3)`

Our `app-view` function takes no inputs (the `[]` has nothing inside) and will return a data structure similar to:

```clojure
[:div
  [:ul
    [:li
      [:div.name "Byblos]
      [:div.address "11 Duncan Street"]]
    [:li
      [:div.name "George"]
      [:div.address "111 Queen St. E"]]
```

This structure is a vector with many nested *keywords* and vectors.

The `for` construct provides a way to do the same operation on a collection of data.
It has two parts: What we're going to loop over and what we're going to do on each thing.

Above, the "what we're going to loop over" is `[r restaurants]`.
This says we will be going over each element in the collection named `restaurants` and we will call the element we're currently looking at `r`.
In Clojure, we call this a *binding form*.

The "what we're going to do for each thing" part, often called the *loop body* above is the `[:li [:div.name (r :name)] [:div.address (r :address)]]`.
You can see that the variable `r` which we declared above in the *binding form* is being used in the body.

Reagent will take this vector and convert it to the corresponding *HTML*, which the browser understands how to display. The corresponding HTML to the above is:

```html
<div>
  <ul>
    <li>
      <div class='name'>Byblos</div>
      <div class='address'>11 Duncan Street</div>
    </li>
    <li>
      <div class='name'>George</div>
      <div class='address'>111 Queen St. E</div>
    </li>
  </ul>
</div>
```

In the above HTML, we see three types of elements -- `div`, `ul`, and `li` -- and one attribute, `class`.
HTML elements in general look like `<tag attribute="some value" other-attribute="other value">....</tag>`.
The `...` part can be more HTML, text, or nothing.


We are using a Clojure style called "Hiccup" to represent HTML using vectors.
The above example in Hiccup would look like `[:tag {:attribute "some value" :other-attribute "other value"} ...]`.
This is a slightly more compact way of representing the HTML that also lets us use nice Clojure functions to manipulate the HTML we will be generating.

Next, let's add a little helper function to get the image link for a particular restaurant. Add the following above `(def restaurants ...)`:

```clojure
(defn id->image [id]
  (str "https://s3-media2.fl.yelpcdn.com/bphoto/" id))
```

This is defining a function called `id->image` that takes one argument called `id`.
We then use the `str` function to attach that id to the end of the url that will give us a link to the appropriate image.

Add in other information:

```clojure
(defn app-view []
  [:div
   [:ul
    (for [r restaurants]
      [:li
       [:img {:src (id->image (r :image))}]
       [:div.name (r :name)]
       [:div.address (r :address)]
       [:div.rating (r :rating)]
       [:div.price-range (r :price-range)]])]])
```

Style:

```clojure
(defn app-view []
  [:div
   [:ul {:style {:margin 0
                 :padding 0
                 :list-style "none"}}
    (for [r restaurants]
      (let [h "5em"]
        [:li {:style {:height h
                      :margin-bottom "1em"}}
         [:img {:src (id->image (r :image))
                :style {:width h
                        :height h
                        :float "left"
                        :margin-right "0.5em"}}]
         [:div.name {:style {:font-weight "bold"}} (r :name)]
         [:div.address (r :address)]
         [:div.rating (r :rating)]
         [:div.price-range {:style {:color "green"}}
          (repeat (r :price-range) "$")]]))]])
```

Factor out `restaurant-view`:

```clojure
(defn restaurant-view [r]
  (let [h "5em"]
    [:div {:style {:height h
                   :margin-bottom "1em"}}
     [:img {:src (id->image (r :image))
            :style {:width h
                    :height h
                    :float "left"
                    :margin-right "0.5em"}}]
     [:div.name {:style {:font-weight "bold"}} (r :name)]
     [:div.address (r :address)]
     [:div.rating (r :rating)]
     [:div.price-range {:style {:color "green"}}
      (repeat (r :price-range) "$")]]))

(defn app-view []
  [:div
   [:ul {:style {:margin 0
                 :padding 0
                 :list-style "none"}}
    (for [r restaurants]
      [:li {:style {:margin-bottom "1em"}}
       (restaurant-view r)])]])
```

# Creating Our Header w/ Search Field and Filter Buttons

Add a `header-view` and factor out `restaurants-view`:

```clojure
(defn header-view []
  [:div.header
   [:input.search {:placeholder "Search"}]
   [:div.price-range
    [:button "$"]
    [:button "$$"]
    [:button "$$$"]
    [:button "$$$$"]]
   [:div.sort
    [:button "Name"]
    [:button "Rating"]]])

(defn restaurants-view []
  [:ul {:style {:margin 0
                :padding 0
                :list-style "none"}}
   (for [r restaurants]
     [:li {:style {:margin-bottom "1em"}
           :key (r :name)}
      (restaurant-view r)])])

(defn app-view []
  [:div
   (header-view)
   (restaurants-view)])
```

You can see we added `:key` to the map for our `:li` tag in the for loop.
We do this so that React, the underlying system that handles actually displaying things can differentiate each list element.
Why this is important will be explained later :)

# Implementing Our Sort Toggle

Sort our restaurants by rating:

```clojure
(defn restaurants-view []
  [:ul {:style {:margin 0
                :padding 0
                :list-style "none"}}
   (for [r (sort-by :rating restaurants)]
     [:li {:style {:margin-bottom "1em"}}
      (restaurant-view r)])])
```

Create an app-state atom:

```clojure
(defonce app-state (r/atom {:sort :rating}))
```

An `atom` is a piece of data that controls access to some other data and allows it to be updated.
In this case, the atom is wrapping the map `{:sort :rating}`.
We will use this atom to have application state that will update as we change things.

`defonce` is like the `def` we used previously, except it only happens once.
We do this so when the app gets reloaded when changes are made to the code, the state won't get overridden back to the initial value.
To see this, try changing the `defonce` to a def, do some things in the app, then make a change to your code and save.
You should see that figwheel will reload the app and your app will go back to its initial state.

Change our sort-by to use the value of `:sort` from `app-state`:

```clojure
(defn restaurants-view []
  [:ul {:style {:margin 0
                :padding 0
                :list-style "none"}}
   (let [sort-key (@app-state :sort)]
     (for [r (sort-by sort-key restaurants)]
       [:li {:style {:margin-bottom "1em"}}
        (restaurant-view r)]))])
```

Change `app-state` `:sort` by clicking on header buttons:

```clojure
[:div.sort
    [:button {:onClick (fn [_]
                         (swap! app-state assoc :sort :name))}
             "Name"]
    [:button {:onClick (fn [_]
                         (swap! app-state assoc :sort :rating))}
             "Rating"]]
```

Change display of sort buttons depending on if they are clicked or not:

```clojure
(let [current-sort (@app-state :sort)]
     [:div.sort
      [:button {:style (if (= :name current-sort)
                         {:background "red"}
                         {:background "gray"})
                :onClick (fn [_]
                           (swap! app-state assoc :sort :name))}
               "Name"]
      [:button {:style (if (= :rating current-sort)
                         {:background "red"}
                         {:background "gray"})
                :onClick (fn [_]
                           (swap! app-state assoc :sort :rating))}
               "Rating"]])
```


# Implement Price Range Filtering

Add `:price-ranges` to the `app-state` atom:

```clojure
(defonce app-state (r/atom {:sort :rating
                            :price-ranges #{1 2 3 4}}))
```


Only show restaurants in the price range:

```clojure
(defn restaurants-view []
  [:ul {:style {:margin 0
                :padding 0
                :list-style "none"}}
   (let [sort-key (@app-state :sort)
         price-ranges (@app-state :price-ranges)]
     (for [r (->> restaurants
                  (filter (fn [r] (contains? price-ranges (r :price-range))))
                  (sort-by sort-key))]
       [:li {:style {:margin-bottom "1em"}
             :key (r :name)}
        (restaurant-view r)]))])
```

TODO: add a way to test this / see the effect immediately
(Change the initial state of atom to #{1} and refresh)

Make buttons change the `:price-ranges` state and display different based on the state:

```clojure
(defn header-view []
  [:div.header
   [:input.search {:placeholder "Search"}]
   (let [price-ranges (@app-state :price-ranges)]
     [:div.price-range
      (let [active? (contains? price-ranges 1)]
        [:button {:style (if active?
                           {:background "red"}
                           {:background "gray"})
                  :onClick (fn [_]
                             (if active?
                               (swap! app-state #(assoc %1 :price-ranges (disj (%1 :price-ranges) 1)))
                               (swap! app-state #(assoc %1 :price-ranges (conj (%1 :price-ranges) 1)))))}
         "$"])
      (let [active? (contains? price-ranges 2)]
        [:button {:style (if active?
                           {:background "red"}
                           {:background "gray"})
                  :onClick (fn [_]
                             (if active?
                               (swap! app-state #(assoc %1 :price-ranges (disj (%1 :price-ranges) 2)))
                               (swap! app-state #(assoc %1 :price-ranges (conj (%1 :price-ranges) 2)))))}
         "$$"])
      (let [active? (contains? price-ranges 3)]
        [:button {:style (if active?
                           {:background "red"}
                           {:background "gray"})
                  :onClick (fn [_]
                             (if active?
                               (swap! app-state #(assoc %1 :price-ranges (disj (%1 :price-ranges) 3)))
                               (swap! app-state #(assoc %1 :price-ranges (conj (%1 :price-ranges) 3)))))}
         "$$$"])
      (let [active? (contains? price-ranges 4)]
        [:button {:style (if active?
                           {:background "red"}
                           {:background "gray"})
                  :onClick (fn [_]
                             (if active?
                               (swap! app-state #(assoc %1 :price-ranges (disj (%1 :price-ranges) 4)))
                               (swap! app-state #(assoc %1 :price-ranges (conj (%1 :price-ranges) 4)))))}
         "$$$$"])])
   (let [current-sort (@app-state :sort)]
     [:div.sort
      [:button {:style (if (= :name current-sort)
                         {:background "red"}
                         {:background "gray"})
                :onClick (fn [_]
                           (swap! app-state assoc :sort :name))} "Name"]
      [:button {:style (if (= :rating current-sort)
                         {:background "red"}
                         {:background "gray"})
                :onClick (fn [_]
                           (swap! app-state assoc :sort :rating))} "Rating"]])])
```

Refactor the filter buttons:

```clojure
(defn header-view []
  [:div.header
   [:input.search {:placeholder "Search"}]

   (let [price-ranges [1 2 3 4]
         current-price-ranges (@app-state :price-ranges)]
     [:div.price-range
      (for [price-range price-ranges]
        (let [active? (contains? current-price-ranges price-range)]
          [:button {:style (if active?
                             {:background "red"}
                             {:background "gray"})
                    :onClick (fn [_]
                               (if active?
                                 (swap! app-state #(assoc %1 :price-ranges (disj (%1 :price-ranges) price-range)))
                                 (swap! app-state #(assoc %1 :price-ranges (conj (%1 :price-ranges) price-range)))))}
           (repeat price-range "$")]))])

   (let [current-sort (@app-state :sort)]
     [:div.sort
      [:button {:style (if (= :name current-sort)
                         {:background "red"}
                         {:background "gray"})
                :onClick (fn [_]
                           (swap! app-state assoc :sort :name))} "Name"]
      [:button {:style (if (= :rating current-sort)
                         {:background "red"}
                         {:background "gray"})
                :onClick (fn [_]
                           (swap! app-state assoc :sort :rating))} "Rating"]])])
```

# Implementing Search

Make typing in search field change `:query` in `app-state`:

```clojure
(defonce app-state (r/atom {:sort :rating
                            :price-ranges #{1 2 3 4}
                            :query ""}))
```

```clojure
[:input.search {:on-change (fn [e]
                                (swap! app-state assoc :query (.. e -target -value)))
                   :placeholder "Search"}]
```

```clojure
(defn restaurants-view []
  [:ul {:style {:margin 0
                :padding 0
                :list-style "none"}}
   (let [sort-key (@app-state :sort)
         price-ranges (@app-state :price-ranges)
         query (@app-state :query)]
     (for [r (->> restaurants
                  (filter (fn [r] (contains? price-ranges (r :price-range))))
                  (filter (fn [r] (clojure.string/includes? (r :name) query)))
                  (sort-by sort-key))]
       [:li {:style {:margin-bottom "1em"}
             :key (r :name)}
        (restaurant-view r)]))])
```

# Refactor to use Transactions

# Refactor to use Subscriptions

# Move CSS Into a Seperate File

# Make Selecting a Restaraunt Show Its Page

# Allow Creating of a New Restaurant

# Pull Restaurants from a Database

# Add New Restaurants to a Database

# Can Leave Comments (+ save to database)

# Users can Register and Login

# Users Can Rate Restaurants

# Users Have Profiles

# URLs for different pages
