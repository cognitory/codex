(ns orbit.render
  (:require [reagent.core :as r]
            [reagent.ratom :include-macros true :refer-macros [reaction]]
            [re-frame.core :as rf]
            [clojure.string :as string]
            [cljs.pprint :refer [pprint]]
            [garden.core :refer [css]]
            [garden.stylesheet :refer [at-import]]))

(def styles
  (css [
        (at-import "https://fonts.googleapis.com/css?family=Alegreya|Source+Code+Pro")
        [:.code
         {:font-family "Source Code Pro"
          :border-radius "5px"
          :white-space "pre-wrap"

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

(defn set-step! [app-state [_ step]]
  (assoc app-state :step step))

(defn init! [app-state [_ orbit]]
  (-> (assoc app-state :orbit orbit)
      (set-step! [nil 0])))

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

(defn- demo-view [])

(defn- file-view [file-name code]
  [:div
   [:div.name file-name]
   [:div.code (string/replace-first (with-out-str (pprint code))
                                    #"\[(.*)\]"
                                    "$1")]])

(defn code-view []
  (let [resources (rf/subscribe [:get-current-resources]) ]
    (fn []
      [:div
       (for [[file-name code] @resources]
         ^ {:key file-name} [file-view file-name code]
         )])))

(defn steps-view []
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
   ;[demo-view]
   [steps-view]
   [code-view]])

(defn render [orbit dom-target]
  (rf/dispatch-sync [:init! orbit])
  (r/render [orbit-view] dom-target))
