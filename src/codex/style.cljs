(ns codex.style
  (:require [garden.core :refer [css]]
            [garden.stylesheet :refer [at-import]]))

(def styles
  (css
    [
     ; reagent escapes quotes, causing problems
     ;(at-import "https://fonts.googleapis.com/css?family=Alegreya|Source+Code+Pro")
     ;(at-import "./reset.css")

    [:body
     {:font-family "Alegreya"
      :line-height 1.5}]

     [:code
     {:font-family "Source Code Pro"
      :border-radius "5px"}]

     [:p
      [:code
       {:padding "0em 0.25em"
        :display "inline-block"}]]

     [:pre
      [:code
       {:font-size "0.85em"
        :background "#2B2852"
        :color "white"
        :padding "0.5em 0.75em"
        :display "inline-block"
        :margin "0 1em 1em 0"
        :max-width "100%"
        :overflow "scroll"}]]

     [:h1 :h2 :h3
      {:font-weight "bold"
       :margin "1em 0"}]

     [:h1
      {:font-size "1.8em"}]

     [:h2
      {:font-size "1.5em"}]

     [:em
      {:font-style "italic"}]

     [:p
      {:margin "1em 0"}]

     [:.logo
      {:position "absolute"
       :top "1em"
       :left 0
       :background "#2B2852"
       :margin 0
       :padding "0.5em"}

      [:a
       {:color "white"
        :text-decoration "none"}]]

     [:.main
      {:max-width "100%"
       :overflow "hidden"
       :padding "1em"}]

     [:a.dne
      {:color "red"
       :text-decoration "underline"}]]))
