(ns mab.algorithms.ucb1
  (:use [mab simulator arm]))

(defn untested-arm 
  [arms]
  (first (take 1 (filter #(= (arm-count %) 0) arms))))

(defn update-curiosity-bonus 
  [arm total-draws]
  (if (and (> (arm-count arm) 0) 
           (> total-draws 0))
    (let [bonus (/ (Math/sqrt (* 2 (Math/log total-draws))) 
                   (float (arm-count arm)))
          update (+ (float (arm-value arm)) bonus)]
      (update-value arm update))
    arm)) 

(defn update-curiosity-bonus-all [arms total-draws]
  (vec (map #(update-curiosity-bonus % total-draws) arms)))


(defn select-arm [arms]
  (if-let [arm (untested-arm arms)]
    arm
    (nth arms 
         (max-value-arm-idx 
           (update-curiosity-bonus arms (total-arm-counts arms))))))

(defn test-algorithm
  [sample-space horizon iterations]
  (let [n (count sample-space)
        arms (initialize-arm-vector n)
        bandit (create-bandit sample-space)
        best-arm (best-arm-index sample-space)]
    (println (format "Best arm is %d" best-arm))
    (simulation-seq->table 
      (repeatedly-simulate-seq bandit 
                               select-arm
                               update-arm
                               arms
                               horizon 
                               iterations))))

