(ns orbit.render
  (:require [reagent.core :as r]
            [reagent.ratom :include-macros true :refer-macros [reaction]]
            [re-frame.core :as rf]
            [clojure.string :as string]
            [fipp.clojure :as fipp]
            [cljs.pprint :refer [pprint]]
            [garden.core :refer [css]]
            [garden.stylesheet :refer [at-import]]
            [cljs.js :refer [empty-state eval-str js-eval]]))

(def styles
  (css [
        (at-import "https://fonts.googleapis.com/css?family=Alegreya|Source+Code+Pro")
        [:#app-wrapper
         {:position "absolute"
          :right "1em"
          :width "30%"
          :top "1em"
          :bottom "1em"
          :background "black"
          :border-radius "10px"
          :padding "2em"
          :box-sizing "border-box"}
         [:#app
          {:background "white"
           :width "100%"
           :height "100%"
           :overflow-x "scroll"}]]

        [:.code
         {:font-family "Source Code Pro"
          :white-space "pre-wrap"
          :line-height "1.2"
          :font-size "0.8em"
          :padding "1.5em 1em"
          :position "absolute"
          :top 0
          :bottom 0
          :width "30em"
          :left 0
          :box-sizing "border-box"
          }]

        [:.steps
         {:position "absolute"
          :top 0
          :left "24em"}
         [:.step
          [:&.active
           {:font-weight "bold"}]]]]))

(enable-console-print!)

(defn tee [x]
  (println x) x)

(defn- eval-code [code]
  (eval-str (empty-state)
            (string/join "\n" code)
            'dummy-symbol
            {:ns 'cljs.user
             :static-fns true
             :def-emits-var false
             :eval js-eval
             ; NOTE: load does nothing; libs must be reqd by this ns
             :load (fn [name cb] (cb {:lang :clj :source "."}))
             :context :statement}
            (fn [result]
              result)))

(defn- eval-current-code [app-state step]
  (eval-code (get-in app-state [:orbit :history step :resources "core.cljs"])))

(defn set-step! [app-state [_ step]]
  (eval-current-code app-state step)
  (assoc app-state :step step))

(defn init! [app-state [_ orbit]]
  (assoc app-state :orbit orbit))

(rf/register-handler :init! init!)
(rf/register-handler :set-step! set-step!)

(rf/register-sub
  :get-current-step
  (fn [app-state _]
    (reaction (:step @app-state))))

(rf/register-sub
  :get-steps
  (fn [app-state _]
    (reaction (get-in @app-state [:orbit :history]))))

(rf/register-sub
  :get-current-resources
  (fn [app-state _]
     (reaction (get-in @app-state [:orbit :history (:step @app-state) :resources]))))

(defn- demo-view []
  [:div#app-wrapper
   [:div#app]])

(defn- file-view [file-name code]
  (let [highlight (fn [this]
                    (->> this
                         r/dom-node
                         (.-firstChild)
                         (.-nextSibling)
                         (.highlightBlock js/hljs)))]
    (r/create-class
      {:reagent-render
       (fn [file-name code]
         [:div.file
          [:div.name file-name]
          [:div.code.clojure
           (->> code
                (map #(with-out-str (fipp/pprint %1 {:width 50})))
                (string/join "\n"))]])
       :component-did-mount highlight
       :component-did-update highlight })))

(defn- resources-view []
  (let [resources (rf/subscribe [:get-current-resources]) ]
    (fn []
      [:div.resources
       (for [[file-name code] @resources]
         ^ {:key file-name} [file-view file-name code])])))

(defn- steps-view []
  (let [steps (rf/subscribe [:get-steps])
        current-step (rf/subscribe [:get-current-step])]
    (fn []
      [:div.steps
       (doall
         (for [index (range (count @steps))]
           (let [step (get @steps index)
                 name (:step step)]
             [:div.step {:key index
                         :on-click (fn [_]
                                     (rf/dispatch [:set-step! index]))
                         :class (when (= index @current-step) "active")}
              name])))])))

(defn orbit-view []
  [:div
   [:style {:type "text/css"
            :dangerouslySetInnerHTML {:__html styles}}]

   [demo-view]
   [steps-view]
   [resources-view]])

(defn render [orbit dom-target]
  (rf/dispatch-sync [:init! orbit])
  (r/render [orbit-view] dom-target)
  (rf/dispatch [:set-step! 0]))
