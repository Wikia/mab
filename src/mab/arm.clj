(ns mab.arm
  (:use [mab.util :only (all-equal?)]))

(defn create-arm
  "Create a bandit arm."
  ([count value] {:count count
                  :value value})
  ([]            (create-arm 0 0)))


(defn initialize-arm-map
  "Create a vector af initialized arms."
  ([n]
   (zipmap (range n) (take n (repeatedly create-arm))))
  ([counts values]
   (zipmap (range (count counts)) (map create-arm counts values))))

(defn arm-count
  "Get the count from an arm."
  [arm]
  (get arm :count 0))

(defn update-count
  "Update the count of an arm."
  [arm count]
  (assoc arm :count count))

(defn inc-count
  "Increment the count of an arm."
  ([arm step]
   (update-in arm [:count] (fnil (partial + step) 0)))
  ([arm]
   (inc-count arm 1)))

(defn arm-value
  "Get the value from an arm."
  [arm]
  (get arm :value 0))

(defn update-value
  "Update the value from an arm."
  [arm value]
  (assoc arm :value value))

(defn inc-value
  "Increment the value of an arm"
  [arm]
  (update-in arm [:value] (fnil inc 0)))

(defn arm-score 
  "Get the score from an arm. Scores are ephemeral values associated with an arm."
  [arm]
  (get arm :score 0))

(defn update-score 
  "Update the value from an arm. Scores are ephemeral values associated with an arm."
  [arm score]
  (assoc arm :score score))


(defn arm-reward-rate
  "Comute an arm's reward rate."
  [arm]
  (if (> (arm-count arm) 0)
    (/ (arm-value arm) 
       (float (arm-count arm)))
    0))


(defn compute-update-value
  "Compute the updated value for an arm."
  ([current-count current-value reward inc-by]
   (+
     (float (* (/ (- current-count inc-by) current-count) current-value))
     (float (* (/ 1 current-count) reward))))
  ([current-count current-value reward]
   (compute-update-value current-count current-value reward 1)))


(defn arm-by-idx
  "Get an arm by the index."
  [arms idx]
  (arms idx))

(defn replace-arm
  "Replace the arm at idx in arms with the given arm."
  [arms idx arm]
  (assoc arms idx arm))

(defn update-arm
  "Update an arm with a reward."
  ([arms idx reward inc-by]
   (let [chosen-arm (arms idx (create-arm 0 0))
         chosen-arm (inc-count chosen-arm inc-by)
         current-count (arm-count chosen-arm)
         current-value (arm-value chosen-arm)
         new-value (compute-update-value current-count current-value reward inc-by)]
     (assoc arms idx (update-value chosen-arm new-value))))
  ([arms idx reward]
   (update-arm arms idx reward 1)))

(defn tuple-idx
  "Given an arm tuple (idx, arm) returns the index."
  [t]
  (first t))

(defn tuple-arm
  "Given an arm tuple (idx, arm) returns the arm."
  [t]
  (second t))

(defn max-value-tuple
  "Returns a arm tuple for the arm with the maximum value."
  [arms]
  (apply max-key (comp arm-value tuple-arm) arms))

(defn max-score-tuple
  "Returns a arm tuple for the arm with the maximum score."
  [arms]
  (apply max-key (comp arm-score tuple-arm) arms))

(defn total-arm-counts
  "Sum the total counts for the arms."
  [arms]
  (reduce + 0 (map arm-count (vals arms))))


(defn random-arm-idx
  "Get a random arm index."
  [arms]
  (nth (keys arms) (rand-int (count arms))))


(defn random-arm-tuple
  "Get a random arm."
  [arms]
  (nth (seq arms) (rand-int (count arms))))


(defn remove-arm
  "Remove the arm with \"index\" from \"arms\"."
  [arms idx]
  (dissoc arms idx))


(defn select-n-arms
  "Selects n distinct arms. Good for selecting n distinct arms using a given selection funcition. 
  Returns the n keys of the selected arms."
  [selectfn arms n]
  (loop [a arms
         ret []]
    (cond (or (= (count ret) n) (empty? a)) ret
          :else
          (let [selected (selectfn a)]
            ; this would be faster using dissoc and a map
            (recur (remove-arm a (first selected)) (conj ret (first selected)))))))


(defn best-value-rate-arm
  "Find the arm with the higest value / count ratio."
  [arms]
  (apply max-key (fn [a]
                   (let [ap (tuple-arm a)]
                     (if (> (arm-count ap) 0)
                       (/ (arm-value ap)
                          (arm-count ap))
                       0)))
         arms))

(defn all-arm-values-equal?
  "Test if all of the arm-value componets of arms are equal."
  [arms]
  (all-equal? (map (comp arm-value second) arms)))

