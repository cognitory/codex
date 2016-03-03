(ns codex.core
  (:require [ajax.core :refer [GET POST]]
            [clojure.string :as string]
            [reagent.core :as r]
            [secretary.core :as secretary :include-macros true :refer-macros [defroute]]
            [pushy.core :as pushy]
            cljsjs.js-yaml))

(enable-console-print!)

(defn tee [x]
  (println x) x)

(defn keyword-keys [m]
  (reduce (fn [memo [k v]]
            (assoc memo (keyword k) v)) {} m))

(defonce app-state (r/atom {:page {:type :index}
                            :groups {}
                            :tldrs {}}))

(defonce REPO_URL "https://api.github.com/repos/cognitory/codex/")

(defroute index-path "/codex" []
  (swap! app-state assoc :page {:type :index}))

(defroute guide-path "/codex/guides/:id" [id]
  (swap! app-state assoc :page {:type :guide :id id}))

(defroute tldr-path "/codex/tldrs/:id" [id]
  (swap! app-state assoc :page {:type :tldr :id id}))

(defn parse-content [raw-content]
  (let [parts (string/split raw-content "---")]
    (if (>= (count parts) 3)
      (assoc (keyword-keys (js->clj (js/jsyaml.safeLoad (parts 1)))) :content (parts 2))
      {:content raw-content})))

(defn fetch! []
  (GET (str REPO_URL "contents/guides")
    {:response-format :json
     :keywords? true
     :handler (fn [files]
                (doseq [file files]
                  (GET (file :download_url)
                    {:handler (fn [raw-content]
                                (let [id (string/replace-first (file :name) #"\.md" "")]
                                  (swap! app-state assoc-in [:guides id] (assoc (parse-content raw-content) :id id))))})))})

  (GET (str REPO_URL "contents/tldrs")
    {:response-format :json
     :keywords? true
     :handler (fn [files]
                (doseq [file files]
                  (GET (file :download_url)
                    {:handler (fn [raw-content]
                                (let [id (string/replace-first (file :name) #"\.md" "")]
                                  (swap! app-state assoc-in [:tldrs id] (assoc (parse-content raw-content) :id id))))})))}))

(defn seed! []
  (swap! app-state assoc
         :guides {"foo" {:id "foo"
                         :content "did you know about foo?"}
                  "bar" {:id "bar"
                         :content "hey bar is pretty cool"}}
         :tldrs {"baz" {:id "baz"
                        :content "what is baz? good question"}
                 "xyz" {:id "xyz"
                        :content "don't forget about xyz!"}}))

(defn tldr-view []
  (let [id (get-in @app-state [:page :id])
        tldr (get-in @app-state [:tldrs id])]
    [:div
     [:h1 (tldr :id)]
     [:p (tldr :content)]]))

(defn guide-view []
  (let [id (get-in @app-state [:page :id])
        guide (get-in @app-state [:guides id])]
   [:div
    [:h1 (guide :id)]
    [:p (guide :content)]]))

(defn index-view []
  [:div
   [:h1 "Index"]])

(defn app-view []
  [:div
   [:h1 [:a {:href (index-path)} "Codex"]]
   [:div.sidebar
    [:h2 "Guides"]
    [:div.guides
     (for [[_ guide] (@app-state :guides)]
       [:a {:key (guide :id)
            :href (guide-path guide)} (guide :id)]) ]
    [:h2 "TLDRs"]
    [:div.tldrs
     (for [[_ tldr] (@app-state :tldrs)]
       [:a {:key (tldr :id)
            :href (tldr-path tldr)} (tldr :id)])]]
   [:div.main
    (case (get-in @app-state [:page :type])
      :tldr (tldr-view)
      :guide (guide-view)
      :index (index-view))]])

(defn init []
  (fetch!)
  (pushy/start! (pushy/pushy secretary/dispatch!
                             (fn [x] (when (secretary/locate-route x) x))))
  (r/render-component [app-view] (.-body js/document)))

(defonce once
  (do (init)))
