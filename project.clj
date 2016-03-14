(defproject codex "0.0.1"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [cljs-ajax "0.5.3"]
                 [re-frame "0.7.0-alpha-3"]
                 [secretary "1.2.3"]
                 [cljsjs/js-yaml "3.3.1-0"]
                 [markdown-clj "0.9.86"]
                 [garden "1.3.2"]
                 [fipp "0.6.4"]
                 [cljsjs/highlight "8.4-0"]]

  :plugins [[lein-cljsbuild "1.1.2"]
            [lein-figwheel "0.5.0-6"]]

  :clean-targets ^{:protect false}
  ["resources/public/js/dev"]

  :figwheel {:server-port 3450
             :css-dirs ["resources/public/css"]}

  :cljsbuild {:builds
              [{:id "codex"
                :figwheel true
                :source-paths ["src" "test"]
                :compiler {:main codex.core
                           :asset-path "/js/codex"
                           :output-to "resources/public/js/codex.js"
                           :output-dir "resources/public/js/codex"
                           :verbose true}}
               {:id "codex-release"
                :figwheel true
                :source-paths ["src"]
                :compiler {:main codex.core
                           :asset-path "/codex/resources/public/js/codex-release"
                           :output-to "resources/public/js/codex-release.js"
                           :output-dir "resources/public/js/codex-release"
                           :optimizations :advanced
                           :pretty-print false }}
               {:id "rustyspoon"
                :figwheel true
                :source-paths ["src"]
                :compiler {:main rustyspoon.orbit
                           :asset-path "/js/rustyspoon"
                           :output-to "resources/public/js/rustyspoon.js"
                           :output-dir "resources/public/js/rustyspoon"
                           :verbose true}}
               {:id "rustyspoon-release"
                :figwheel true
                :source-paths ["src"]
                :compiler {:main rustyspoon.orbit
                           :asset-path "/codex/resources/public/js/rustyspoon-release"
                           :output-to "resources/public/js/rustyspoon-release.js"
                           :output-dir "resources/public/js/rustyspoon-release"
                           :optimizations :simple
                           :pretty-print false}}]}

  :min-lein-version "2.5.0"

  )
