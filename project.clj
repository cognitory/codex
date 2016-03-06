(defproject codex "0.0.1"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [cljs-ajax "0.5.3"]
                 [re-frame "0.7.0-alpha-3"]
                 [secretary "1.2.3"]
                 [cljsjs/js-yaml "3.3.1-0"]
                 [markdown-clj "0.9.86"]]

  :plugins [[lein-cljsbuild "1.1.2"]
            [lein-figwheel "0.5.0-6"]]

  :clean-targets ^{:protect false}
  ["resources/public/js/dev"]

  :figwheel {:server-port 3450
             :css-dirs ["resources/public/css"]}

  :cljsbuild {:builds
              [{:id "dev"
                :figwheel true
                :source-paths ["src"]
                :compiler {:main codex.core
                           :asset-path "/js/dev"
                           :output-to "resources/public/js/dev.js"
                           :output-dir "resources/public/js/dev"
                           :verbose true}}
               {:id "release"
                :source-paths ["src"]
                :compiler {:main codex.core
                           :asset-path "/codex/resources/public/js/release"
                           :output-to "resources/public/js/codex.js"
                           :output-dir "resources/public/js/release"
                           :optimizations :advanced
                           :pretty-print false }}]}

  :min-lein-version "2.5.0"

  )
