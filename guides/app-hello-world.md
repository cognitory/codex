---
title: Hello World App
description: TODO
dependencies: [ setup-env ]
---

First, make sure you have set up your system, as per: [[guides/setup-env]].

Open a Terminal window

Go to a folder where you will want your project to be:

```sh
cd ~/Desktop
```

Create a folder for the project:

```sh
mkdir app0
```

Create a `src` folder and an `app` folder inside of it:

```sh
mkdir -p src/app
```

Create a `resources` folder and a `public` folder inside of it:

```sh
mkdir -p resources/public
```

Create a file called `project.clj`:

```sh
touch project.clj
```

Create a `core.cljs` file inside of `./src/app`:

```sh
touch ./src/app/core.cljs
```

Create an `index.html` file inside of `./resources/public`:

```sh
touch ./resources/public/index.html
```

Open your project folder with your editor:

```sh
open ./ -a Atom
```

Check that your project folder structure looks like this:

```misc
project.clj
resources
 public
   index.html
src
 app
   core.cljs
```

Edit `project.clj` to have the following content:

```clojure
(defproject app "0.0.1"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [re-frame "0.7.0-alpha-3"]]

  :plugins [[lein-figwheel "0.5.0-6"]]

  :figwheel {:server-port 3499}

  :cljsbuild {:builds
              [{:id "dev"
                :figwheel true
                :source-paths ["src"]
                :compiler {:main app.core
                           :asset-path "/js/dev"
                           :output-to "resources/public/js/dev.js"
                           :output-dir "resources/public/js/dev"
                           :verbose true}}]})
```

Edit `core.cljs` to have the following content:

```clojure
(ns app.core
  (:require [reagent.core :as r]))

(enable-console-print!)

(println "Hello Console!")

(defn app-view []
  [:div "Hello World!"])

(r/render-component [app-view] (.-body js/document))
```

Edit `index.html` to have the following content:

```html
<!DOCTYPE html>
<html>
  <head></head>
  <body>
    <script src="/js/dev.js" type="text/javascript"></script>
  </body>
</html>
```

Back in Terminal, run Figwheel:

```sh
rlwrap lein figwheel
```

In Chrome, go to `http://localhost:3499`

You should see the text `Hello Console!` on the page.

In Chrome, do `View > Developer > Javascript Console`, which should bring up a console, and in the logs it should say `Hello Console!`.




