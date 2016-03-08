---
title: Rustyspoon - Hello World
description: TODO
dependencies: [ setup-env ]
---

First, make sure you have set up your system, as per: [[guides/setup-env]].

Create a folder `rustyspoon` for our project someplace, for example, on your Desktop.

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
                 [re-frame "0.7.0-alpha-3"]]

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

```clojure
(ns rustyspoon.core
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


