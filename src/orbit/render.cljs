(ns orbit.render
  (:require [reagent.core :as r]
            [reagent.ratom :include-macros true :refer-macros [reaction]]
            [re-frame.core :as rf]
            [clojure.string :as string]
            [fipp.clojure :as fipp]
            [cljs.pprint :refer [pprint]]
            [garden.core :refer [css]]
            [garden.stylesheet :refer [at-import]]
            [markdown.core :as md]
            [secretary.core :as secretary :include-macros true :refer-macros [defroute]]
            [codex.router :as router]
            [ajax.core :refer [GET]]
            [cljs.js :refer [empty-state eval-str js-eval]]))



(def styles
  (css [
        (at-import "https://fonts.googleapis.com/css?family=Alegreya|Source+Code+Pro")
        [:.orbit
         {:display "flex"
          :position "absolute"
          :top 0
          :left 0
          :right 0
          :bottom 0
          :justify-content "space-between"}]

        [:.tutorial
         {:min-width "25em"
          :flex-grow 1
          :height "100%"
          :overflow-x "scroll"

          :font-family "Alegreya"
          :line-height 1.5
          :padding "3em"
          :box-sizing "border-box"
          :white-space "pre-wrap"}

         [:h1
          {:font-size "1.5em"}]

         [:h2
          {:font-size "1.25em"}]

         [:a.play
          {}]

          [:pre
           [:code
            {:font-family "Source Code Pro"
             :font-size "0.8em"
             :background "#2B2852"
             :color "white"
             :padding "0.5em 0.75em"
             :display "inline-block"
             :margin "0 1em 1em 0"
             :max-width "100%"
             :overflow "scroll"}]]]

        [:.resources
         {:min-width "25em"
          :flex-grow 1
          :height "100%"}
         [:.file
          {:height "100%"}
          [:.name
           {:display "none"}]
          [:.code
           {:font-family "Source Code Pro"
            :white-space "pre-wrap"
            :line-height "1.2"
            :font-size "0.8em"
            :padding "2em 2em"
            :height "100%"
            :overflow-x "scroll"
            :box-sizing "border-box"}]]]

        [:#app-wrapper
         {:min-width "25em"
          :background "black"
          :padding "2em"
          :box-sizing "border-box"
          :height "100%"}

         [:#app
          {:background "white"
           :min-width "20em"
           :min-height "20em"
           :width "100%"
           :height "100%"
           :overflow-x "scroll"}]]

        [:.steps
         {:position "absolute"
          :top 0
          :left 0
          :z-index 1}
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
            (fn [{:keys [error value] :as x}]
              (if error
                (do
                  (def *er x)
                  (js/console.log (str error)))))))

(defn- eval-current-code [app-state step]
  (eval-code (get-in app-state [:orbit :history step :resources "core.cljs"])))

(defn set-step! [app-state [_ step]]
  (eval-current-code app-state step)
  (assoc app-state :step step))

(defn init! [app-state [_ orbit]]
  (assoc app-state :orbit orbit))

(rf/register-handler :init! init!)
(rf/register-handler :set-step! set-step!)
(rf/register-handler :set-content!
                     (fn [app-state [_ content]]
                          (assoc app-state :content content)))
(rf/register-handler
  :set-step-by-name!
  (fn [app-state [_ name]]
       (let [id (->> (get-in app-state [:orbit :history])
                     (keep-indexed (fn [idx s] (when (= name (s :step)) idx)))
                     first)]
         (set-step! app-state [nil id]))))

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

(rf/register-sub
  :get-content
  (fn [app-state _]
    (reaction (get-in @app-state [:content]))))

(defroute step-path "/step/:name" [name]
  (rf/dispatch [:set-step-by-name! name]))

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

(rf/register-sub
  :get-code-for-step
  (fn [app-state [_ step-id resource-id index-start index-end]]
    (let [code (-> (get-in @app-state [:orbit :history])
                   (->> (filter (fn [s] (= step-id (s :step)))))
                   first
                   (get :resources)
                   (get resource-id)
                   (subvec index-start index-end)
                   (->> (map #(with-out-str (fipp/pprint %1 {:width 50}))))
                   (->> (string/join "\n")))]
    (reaction code))))

(defn- md-add-snippets [text state]
  [(string/replace text
                   #"!!!([a-z \-]*?)/([a-z\.]*?)/([0-9]*?)-([0-9]*?)!!!"
                   (fn [[_ step-id resource-id index-start index-end]]
                     (let [code (rf/subscribe [:get-code-for-step step-id resource-id (js/parseInt index-start) (js/parseInt index-end)])]
                       (str "<pre><code>" @code "</code></pre>"))))
   state])

(defn- md-add-go-to-step [text state]
  [(string/replace text
                   #"@@@([a-z \-]*?)@@@"
                   (fn [[_ step-id]]
                     (let [url (step-path {:name step-id})]
                       (str "<a href='" url "' class='run'>" "Run Step" "</a>"))))
   state])

(defn- tutorial-view []
  (let [content (rf/subscribe [:get-content])]
    (fn []
      [:div.tutorial
       {:dangerouslySetInnerHTML
        {:__html (md/md->html @content
                              :custom-transformers [md-add-snippets
                                                    md-add-go-to-step])}}])))

(defn orbit-view []
  [:div.orbit
   [:style {:type "text/css"
            :dangerouslySetInnerHTML {:__html styles}}]
   #_[steps-view]
   [tutorial-view]
   [resources-view]
   [demo-view]])

(defn render [orbit dom-target]
  (rf/dispatch-sync [:init! orbit])
  (r/render [orbit-view] dom-target)
  (GET (str "orbits/rustyspoon.md")
    {:handler (fn [raw-content]
                (rf/dispatch [:set-content! raw-content]))})
  #_(rf/dispatch [:set-step! 0]))

(defonce once
  (do
    (router/init!)))
