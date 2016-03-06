(ns codex.core
  (:require [ajax.core :refer [GET POST]]
            [clojure.string :as string]
            [reagent.core :as r]
            [secretary.core :as secretary :include-macros true :refer-macros [defroute]]
            [markdown.core :as md]
            [cljsjs.js-yaml]
            [codex.router :as router]))

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
(defonce CONTENT_URL
  (if (= js/window.location.hostname "localhost")
    "/"
    "https://raw.githubusercontent.com/cognitory/codex/gh-pages/"))

(defroute index-path "/" []
  (swap! app-state assoc :page {:type :index}))

(defroute guide-path "/guides/:id" [id]
  (swap! app-state assoc :page {:type :guide :id id}))

(defroute tldr-path "/tldrs/:id" [id]
  (swap! app-state assoc :page {:type :tldr :id id}))

(defn parse-content [raw-content]
  (let [parts (string/split raw-content "---")]
    (if (>= (count parts) 3)
      (assoc (keyword-keys (js->clj (js/jsyaml.safeLoad (parts 1)))) :content (parts 2))
      {:content raw-content})))

(defn fetch! []
  (GET (str CONTENT_URL "resources/public/guides.json" #_REPO_URL #_"contents/guides")
    {:response-format :json
     :keywords? true
     :handler (fn [files]
                (doseq [file files]
                  (GET (str CONTENT_URL (file :path))
                    {:handler (fn [raw-content]
                                (let [id (string/replace-first (file :name) #"\.md" "")]
                                  (swap! app-state assoc-in [:guides id] (assoc (parse-content raw-content) :id id))))})))})
  (GET (str CONTENT_URL "resources/public/tldrs.json" #_REPO_URL #_"contents/tldrs")
    {:response-format :json
     :keywords? true
     :handler (fn [files]
                (doseq [file files]
                  (GET (str CONTENT_URL (file :path))
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
                        :content "don't forget about xyz!"
                        :resources ["http://example.com/"]}}))

(defn tldr-view []
  (let [id (get-in @app-state [:page :id])
        tldr (get-in @app-state [:tldrs id])]
    [:div
     [:h1 (or (:title tldr) (:id tldr))]
     [:div {:style {:whitespace "pre"}}
      (:content tldr)]
     [:h2 "Resources"]
     [:ul.resources
      (for [link (:resources tldr)]
        [:li [:a {:href link} link] ])]
     [:h2 "Related"]
     [:ul.resources
      (for [id (:related tldr)]
        [:li [:a {:href (tldr-path {:id id})} id]])]]))

(defn linkify [text state]
  [(string/replace text
                   #"\[\[(tldrs|guides)/([a-z\-]*)(?:\|(.*?))?\]\]"
                   (fn [[_ type id title]]
                     (let [type (keyword type)
                           url (case type
                                 :guides (guide-path {:id id})
                                 :tldrs (tldr-path {:id id}))
                           resource (get-in @app-state [type id])
                           out-text (or title (:title resource) id)]
                       (if resource
                         (str "<a href='" url "''>" out-text "</a>")
                         (str "<a class='dne'>" out-text "</a>")))))
   state])

(defn guide-view []
  (let [id (get-in @app-state [:page :id])
        guide (get-in @app-state [:guides id])]
   [:div
    [:h1 (or (:title guide) (:id guide))]
    [:div {:style {:white-space "pre-wrap"}
           :dangerouslySetInnerHTML
           {:__html (md/md->html (:content guide)
                                 :custom-transformers [linkify])}}]]))

(defn index-view []
  [:div
   [:h1 "Index"]])

(defn app-view []
  [:div
   [:div.sidebar
    [:h1 [:a {:href (index-path)} "Codex"]]
    [:h2 "Guides"]
    [:div.guides
     (for [guide (->> (@app-state :guides)
                      vals
                      (remove (fn [g] (string/blank? (g :content)))))]
       [:a {:key (guide :id)
            :style {:display "block"}
            :href (guide-path guide)}
        (guide :id)]) ]
    [:h2 "TLDRs"]
    [:div.tldrs
     (for [tldr (->> (@app-state :tldrs)
                     vals
                     (remove (fn [t] (and
                                       (empty? (t :resources))
                                       (string/blank? (t :content))))))]
       [:a {:key (tldr :id)
            :style {:display "block"}
            :href (tldr-path tldr)}
        (tldr :id)])]]
   [:div.main {:style {:max-width "40em"
                       :margin "0 auto"}}
    (case (get-in @app-state [:page :type])
      :tldr (tldr-view)
      :guide (guide-view)
      :index (index-view))]])

(defonce once
  (do
    (fetch!)
    (router/init!)))

(defn init []
  (r/render-component [app-view] (.-body js/document)))

(init)
