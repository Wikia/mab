(ns mab.algorithms.ucb1
  (:use [mab arm simulator]))

(defn untested-arm [arms]
  (first (take 1 (filter #(= (arm-count %) 0) arms))))

(defn update-curiosity-bonus [arms total-draws]
  (vec (map (fn [a]
              (if (and (> (arm-count a) 0) 
                       (> total-draws 0))
                  (let [bonus (/ (Math/sqrt (* 2 (Math/log total-draws))) 
                                 (float (arm-count a)))
                        update (+ (float (arm-value a)) bonus)]
                    (update-value a update))
                a)) 
            arms)))


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
                               (fn [a p r]
                                 (-> a 
                                     (update-curiosity-bonus (total-arm-counts arms))
                                     (update-arm p r)))
                               arms
                               horizon 
                               iterations))))

