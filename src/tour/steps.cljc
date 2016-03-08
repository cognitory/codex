(ns tour.steps
  #?(:cljs (:require-macros [tour.steps :refer [deftour]])))

#?(:clj
(defmacro deftour [tour-name & resources]
  `(def ~tour-name {:resources '~resources
                    :steps (atom [])})))

(defn verify-step-body!
  "Assert that the step body is formatted properly and return the steps as a
  vector of vectors"
  [body]
  (loop [body body
         steps []]
    (if (empty? body)
      steps
      (let [[resource action & after] body]
        (assert (string? resource)
          (str "Step resource must be a string, got " (pr-str resource)))
        (assert (contains? #{:add :before :replace :after} action)
          (str "Unknown action " action))
        (case action
          :add (do (assert (>= (count after) 1) "Missing argument for :add")
                   (recur (drop 1 after)
                          (conj steps [resource action (vec (take 1 after))])))

          (:before :after :replace)
          (do (assert (>= (count after) 2)
                (str "Missing argument(s) for " action))
              (recur (drop 2 after)
                     (conj steps [resource action (vec (take 2 after))]))))))))

(defn add-step! [tour step-name & steps]
  (let [steps (verify-step-body! steps)]
    (swap! (:steps tour) conj {:name step-name :actions steps})))

(defmulti apply-action
  (fn [current-state [resource action params]] action))

(defmethod apply-action :add
  [current-state [resource _ [add]]]
  (update-in current-state [resource] conj add))

(defn apply-step
  [current-state {:keys [name actions] :as step}]
  (reduce apply-action current-state actions))

(defn output-tour
  [tour]
  (let [initial-state (zipmap (:resources tour) (repeat []))]
    (reductions apply-step initial-state @(tour :steps))))
