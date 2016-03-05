---
title: Hello World App
description: TODO
dependencies: [ setup-env ]
---

( make sure you have set up your system: [[guides/setup-env]])

 - open up Terminal

 - `cd` to a folder where you will want your project to be

   `cd ~/Desktop`


 - create a folder for the project:

   `mkdir app0`

 - create a `src` folder and a `app` folder inside of it

   `mkdir -p src/app`

 - create a `resources` folder and a `public` folder inside of it

   `mkdir -p resources/public`

 - create a file called `project.clj`

   `touch project.clj`

 - create a `core.cljs` file inside of `./src/app`

   `touch ./src/app/core.cljs`

 - create a `index.html` file inside of `./resources/public`

   `touch ./resources/public/index.html`

 - open your project folder with your editor

   `open ./ -a Atom`

 - check that your project folder structure should look like this:

   ```
   project.clj
   resources
     public
       index.html
   src
     app
       core.cljs
   ```

 - edit `project.clj` to have the following content:

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

 - edit `core.cljs` to have the following content:

  ```clojure
  (ns app.core
    (:require [reagent.core :as r]))

  (enable-console-print!)

  (println "Hello Console!")

  (defn app-view []
    [:div "Hello World!"])

  (r/render-component [app-view] (.-body js/document))
  ```

 - edit `index.html` to have the following content:

  ```html
  <!DOCTYPE html>
  <html>
    <head></head>
    <body>
      <script src="/js/dev.js" type="text/javascript"></script>
    </body>
  </html>
  ```

 - back in Terminal, run Figwheel:

   `lein figwheel`

 - in Chrome, go to `http://localhost:3499`

   you should see the text "Hello Console!" on the page

 - in Chrome, do View > Developer > Javascript Console

   which should bring up a console, and in the logs it should say "Hello Console!"


what we're going to build:



