# Rustyspoon

Demo of what we're going to build:
  [Rustyspoon Demo](http://cognitory.github.io/rustyspoon/index.html)

## Hello World

First, make sure you have set up your system, as per: [[guides/setup-env]].

Create a folder `rustyspoon` for your project someplace, for example, on your Desktop.

Open the project folder with your editor.

Create files and folders so that your project folder structure looks like this:

```misc
rustyspoon
  project.clj
  resources
   public
     index.html
  src
   rustyspoon
     core.cljs
```

Edit `project.clj` to have the following content:

```clojure
(defproject rustyspoon "0.0.1"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [re-frame "0.7.0-alpha-3"]
                 [garden "1.3.2"]]

  :plugins [[lein-figwheel "0.5.0-6"]]

  :figwheel {:server-port 3499}

  :cljsbuild {:builds
              [{:id "dev"
                :figwheel true
                :source-paths ["src"]
                :compiler {:main rustyspoon.core
                           :asset-path "/js/dev"
                           :output-to "resources/public/js/dev.js"
                           :output-dir "resources/public/js/dev"
                           :verbose true}}]})
```

Edit `core.cljs` to have the following content:

!!!hello world/0-4!!!

@@@hello world@@@

Edit `index.html` to have the following content:

```html
<!DOCTYPE html>
<html>
  <head></head>
  <body>
    <div id="app"></div>
    <script src="/js/dev.js" type="text/javascript"></script>
  </body>
</html>
```

Open a Terminal window, and go to the folder you created:

```sh
cd ~/Desktop/rustyspoon
```

Run Figwheel:

```sh
rlwrap lein figwheel
```

In Chrome, go to `http://localhost:3499`

You should see the text `Hello Console!` on the page.

In Chrome, do `View > Developer > Javascript Console`, which should bring up a console, and in the logs it should say `Hello Console!`.



## Making a List of Restaurants to Display

Let's start by displaying a list of restaurants. To do that, we first need information about a few restaurants. We will create a 'vector' of 'maps', each map containing information about a restaurant.

Inside of `core.cljs` add the following below `(enable-console-print!)`:

!!!define restaurants array/0-!!!

@@@define restaurants array@@@

`def` is used to gives names to things that we will use later. Here, we define a list of restaurants using `(def restaurants ...)`

The square brackets `[ ... ]` are used to create a *vector*, which is an ordered list of things. For example, here is a vector of a few numbers: `[ 1 3 5 6 7 ]`.

Our restaurant vector is a list of maps.
A *map* is created by using brackets `{ ... }`.
A map contains multiple *keys* and *values*, with each *key* corresponding to some *value* -- you can think of it like a dictionary, where words are keys and their definitions are values, or a phonebook (names = keys, numbers = values).

We can look up a value from a map by doing `(some-map :my-key)`.
For example, if we had `(def my-map {:name "Canoe" :rating 3.9})` then `(my-map :name)` would give `"Canoe"`.
If the key we're looking up is a *keyword* -- i.e. it starts with a colon, like `:name` and `:rating` above -- then we can also use the opposite order for looking up, like `(:rating my-map)` to get `9.3`.

In our restaurant vector, each map represents a restaurant.-
Each of these contains the `:name`, `:address`, `:image`, `:rating` and `:price-range` keys, with either strings or numbers as values.

Let's get these restaurants showing!

Replace your `(defn app-view ...)` with the following:

!!!update app-view to show restaurants/0-!!!

@@@update app-view to show restaurants@@@

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
We could call the variable anything we want -- we could write `(for [foobar restaurants] [ ... (foobar :name)])` if we wanted.
The variable name we use in a loop is something we choose based on what will make it clear what's happening when we read the code later.

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

## Displaying Images

The `:image` values in our restaurant data are ids of images taken from Yelp. To display them, we need the full URL, so let's add a little helper function to get the image link for a particular restaurant. Add the following above `(def restaurants ...)`:

!!!add image function/0-!!!
@@@add image function@@@

This is defining a function called `id->image` that takes one argument called `id`.
We then use the `str` function to attach that id to the end of the url that will give us a link to the appropriate image.

Now, edit `app-view` to include:

!!!display image/0-!!!
@@@display image@@@


## Show Other Restaurant Info

Now, try changing the code to have the other restaurant information show up (`:rating` and `:price-range`).

.

.

.

Did you figure it out? Your `app-view` should look like this:

!!!show other restaurant info/0-1!!!
@@@show other restaurant info@@@


## Styling

Let's make things a bit prettier. Add the following above `id->image`:

!!!add styles/0-!!!

And change `app-view`:

!!!add styles/1-!!!

@@@add styles@@@

## Factor out `restaurant-view`:

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

## Creating Our Header w/ Search Field and Filter Buttons

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

Add a function to change the value of `:sort` in `app-state`:

```clojure
(defn set-sort! [sort]
  (swap! app-state assoc :sort sort))
```

Change `app-state` `:sort` by clicking on header buttons:

```clojure
[:div.sort
    [:button {:onClick (fn [_]
                         (set-sort! :name))}
             "Name"]
    [:button {:onClick (fn [_]
                         (set-sort! :rating))}
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
                           (set-sort! :name))}
               "Name"]
      [:button {:style (if (= :rating current-sort)
                         {:background "red"}
                         {:background "gray"})
                :onClick (fn [_]
                           (set-sort!))}
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

Add a function to toggle the value of `:price-ranges` given a price-range:

```clojure
(defn toggle-price-range! [price-range]
  (if (contains? (@app-state :price-ranges) price-range)
    (swap! app-state #(assoc %1 :price-ranges (disj (%1 :price-ranges) price-range)))
    (swap! app-state #(assoc %1 :price-ranges (conj (%1 :price-ranges) price-range)))))
```

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
                             (toggle-price-range! 1))}
         "$"])
      (let [active? (contains? price-ranges 2)]
        [:button {:style (if active?
                           {:background "red"}
                           {:background "gray"})
                  :onClick (fn [_]
                             (toggle-price-range! 2))}
         "$$"])
      (let [active? (contains? price-ranges 3)]
        [:button {:style (if active?
                           {:background "red"}
                           {:background "gray"})
                  :onClick (fn [_]
                             (toggle-price-range! 3))}
         "$$$"])
      (let [active? (contains? price-ranges 4)]
        [:button {:style (if active?
                           {:background "red"}
                           {:background "gray"})
                  :onClick (fn [_]
                             (toggle-price-range! 4))}
         "$$$$"])])
   (let [current-sort (@app-state :sort)]
     [:div.sort
      [:button {:style (if (= :name current-sort)
                         {:background "red"}
                         {:background "gray"})
                :onClick (fn [_]
                           (set-sort! :name))} "Name"]
      [:button {:style (if (= :rating current-sort)
                         {:background "red"}
                         {:background "gray"})
                :onClick (fn [_]
                           (set-sort! :rating))} "Rating"]])])
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
                               (toggle-price-range! price-range))}
           (repeat price-range "$")]))])

   (let [current-sort (@app-state :sort)]
     [:div.sort
      [:button {:style (if (= :name current-sort)
                         {:background "red"}
                         {:background "gray"})
                :onClick (fn [_]
                           (set-sort! :name))} "Name"]
      [:button {:style (if (= :rating current-sort)
                         {:background "red"}
                         {:background "gray"})
                :onClick (fn [_]
                           (set-sort! :name))} "Rating"]])])
```

# Implementing Search

Make typing in search field change `:query` in `app-state`:

```clojure
(defonce app-state (r/atom {:sort :rating
                            :price-ranges #{1 2 3 4}
                            :query ""}))
```

```clojure
(defn set-query! [query]
  (swap! app-state assoc :query query))
```

```clojure
[:input.search {:on-change (fn [e]
                                (set-query! (.. e -target -value)))
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
