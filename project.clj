(defproject codex "0.0.1"
  :dependencies [[org.clojure/clojurescript "1.7.145"]]

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.5.0-1"]]

  :clean-targets ^{:protect false}
  ["resources/public/js/dev"]

  :figwheel {:server-port 3450}

  :cljsbuild {:builds
              [{:id "dev"
                :figwheel true
                :source-paths ["src"]
                :compiler {:main codex.core
                           :asset-path "/js/dev"
                           :output-to "resources/public/js/codex.js"
                           :output-dir "resources/public/js/dev"
                           :verbose true}}
               {:id "release"
                :source-paths ["src"]
                :compiler {:main codex.core
                           :asset-path "/js/prod"
                           :output-to "resources/public/js/codex.js"
                           :output-dir "resources/public/js/prod"
                           :optimizations :advanced
                           :pretty-print false }}]}

  :min-lein-version "2.5.0"

  )
