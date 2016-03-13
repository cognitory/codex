(ns orbit.render
  (:require [reagent.core :as r]
            [reagent.ratom :include-macros true :refer-macros [reaction]]
            [re-frame.core :as rf]
            [clojure.string :as string]
            [fipp.clojure :as fipp]
            [cljs.pprint :refer [pprint]]
            [garden.core :refer [css]]
            [garden.stylesheet :refer [at-import]]
            [cljs.js :refer [empty-state eval js-eval]]))

(def styles
  (css [
        (at-import "https://fonts.googleapis.com/css?family=Alegreya|Source+Code+Pro")
        [:#app
         {:position "absolute"
          :right 0
          :width "30%"
          :top 0
          :bottom 0}]
        [:.code
         {:font-family "Source Code Pro"
          :border-radius "5px"
          :white-space "pre-wrap"
          :line-height "1.25"

          :font-size "0.85em"
          :background "#2B2852"
          :color "white"
          :padding "0.5em 0.75em"
          :display "inline-block"
          :margin "0 1em 1em 0"
          :max-width "100%"
          :overflow "scroll"}]

        [:.steps
         [:.step
          [:&.active
           {:font-weight "bold"}

           ]]]

        ]))

(enable-console-print!)

(defn tee [x]
  (println x) x)

(defn- eval-code [code]
  (doseq [part code]
    (let [result (eval (empty-state)
                       part
                       {:eval js-eval
                        :source-map true
                        :context :expr}
                       (fn [result]
                         result))])))

(defn- eval-current-code [app-state step]
  (eval-code (get-in app-state [:orbit :history step :resources "core.cljs"])))

(defn set-step! [app-state [_ step]]
  (eval-current-code app-state step)
  (assoc app-state :step step))

(defn init! [app-state [_ orbit]]
  (-> (assoc app-state :orbit orbit)
      #_(set-step! [nil 0])))

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
  [:div#app])

(defn- file-view [file-name code]
  [:div.file
   [:div.name file-name]
   [:div.code
    (->> code
         (map #(with-out-str (fipp/pprint %1 {:width 50})))
         (string/join "\n")) ]])

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
  (r/render-component [orbit-view] dom-target))
