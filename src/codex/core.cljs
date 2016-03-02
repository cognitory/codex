(ns codex.core
  (:require [ajax.core :refer [GET POST]]
            [clojure.string :as string]
            [reagent.core :as r]))

(enable-console-print!)

(defonce app-state (r/atom {}))

(defonce REPO_URL "https://api.github.com/repos/cognitory/codex/")
(defonce SITE_URL "http://cognitory.github.io/codex/")

(defn fetch []
  (GET (str REPO_URL "contents/guides")
    {:response-format :json
     :keywords? true
     :handler (fn [files]
                (doseq [file files]
                  (GET (str SITE_URL (file :path))
                    {:handler (fn [content]
                                (let [id (string/replace-first (file :name) #"\.md" "")]
                                  (swap! app-state assoc-in [:guides id] {:id id
                                                                          :path (file :path)
                                                                          :content content})))})))})

  (GET (str REPO_URL "contents/tldrs")
    {:response-format :json
     :keywords? true
     :handler (fn [files]
                (doseq [file files]
                  (GET (str SITE_URL (file :path))
                    {:handler (fn [content]
                                (let [id (string/replace-first (file :name) #"\.md" "")]
                                  (swap! app-state assoc-in [:tldrs id] {:id id
                                                                         :path (file :path)
                                                                         :content content})))})))}))

(defn app-view []
  [:div
   [:h1 "Hello World"]
   [:h2 "Guides"]
   [:div.guides
    (for [[_ guide] (@app-state :guides)]
      [:a {:key (guide :id)
           :href (guide :path)} (guide :id)]) ]
   [:h2 "TLDRs"]
   [:div.tldrs
    (for [[_ tldr] (@app-state :tldrs)]
      [:a {:key (tldr :id)
           :href (tldr :path)} (tldr :id)])]])

(defn init []
  (r/render-component [app-view] (.-body js/document)))

(init)
