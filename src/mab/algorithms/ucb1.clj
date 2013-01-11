(ns mab.algorithms.ucb1
  (:use [mab simulator arm]))

(defn untested-arm
  "Find any untested arms. An untest arm has a count of 0."
  [arms]
  (first 
    (filter #(= (arm-count (tuple-arm %)) 0) arms)))

(defn update-curiosity-bonus
  "Update the curiosity bonus for a given arm."
  [arm total-draws]
  (if (and (> (arm-count arm) 0)
           (> total-draws 0))
    (let [bonus (/ (Math/sqrt (* 2 (Math/log total-draws)))
                   (float (arm-count arm)))
          update (+ (float (arm-value arm)) bonus)]
      (update-value arm update))
    arm))

(defn update-curiosity-bonus-all 
  "Update the curiosity bonus for all of the arms."
  [arms total-draws]
  (map-on-arm-vals #(update-curiosity-bonus % total-draws) arms))


(defn select-arm 
  "Select an arm."
  [arms]
  (if-let [arm (untested-arm arms)]
    arm
    (max-value-tuple (update-curiosity-bonus-all arms (total-arm-counts arms)))))


(defn test-algorithm
  "Test the algorithm using the given sample space over horizon iterations times."
  [sample-space horizon iterations]
  (let [n (count sample-space)
        arms (initialize-arm-map n)
        bandit (create-bandit sample-space)
        best-arm (best-mean-index sample-space)]
    (println (format "Best arm is %d" best-arm))
    (simulation-seq->table
      (repeatedly-simulate-seq bandit
                               select-arm
                               update-arm
                               arms
                               horizon
                               iterations))))
