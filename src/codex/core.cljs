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
                (swap! app-state assoc :guides
                       (->> files
                            (map (fn [f]
                                   {:key (string/replace-first (f :name) #"\.md" "")
                                    :file (f :name)}))
                            (key-by :key))))})
  (GET (str REPO_URL "contents/tldrs")
    {:response-format :json
     :keywords? true
     :handler (fn [files]
                (swap! app-state assoc :tldrs
                       (->> files
                            (map (fn [f]
                                   {:key (string/replace-first (f :name) #"\.md" "")
                                    :file (f :name)})))))}))
