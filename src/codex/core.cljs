(ns codex.core
  (:require [ajax.core :refer [GET POST]]
            [clojure.string :as string]))

(enable-console-print!)

(defonce app-state (atom {}))

(defonce REPO_URL "https://api.github.com/repos/cognitory/codex/")
(defonce SITE_URL "http://cognitory.github.io/codex/")

(defn key-by [k arr]
  (reduce (fn [memo i]
            (assoc memo (k i) i)) arr {}))

(defn init []
  (GET (str REPO_URL "contents/guides")
    {:response-format :json
     :keywords? true
     :handler (fn [files]
                (doseq [file files]
                  (GET (str SITE_URL (file :path))
                    {:handler (fn [content]
                                (let [key (string/replace-first (file :name) #"\.md" "")]
                                  (swap! app-state assoc-in [:guides key] {:content content})))})))})

  (GET (str REPO_URL "contents/tldrs")
    {:response-format :json
     :keywords? true
     :handler (fn [files]
                (doseq [file files]
                  (GET (str SITE_URL (file :path))
                    {:handler (fn [content]
                                (let [key (string/replace-first (file :name) #"\.md" "")]
                                  (swap! app-state assoc-in [:tldrs key] {:content content})))})))}))
