(ns mab.algorithms.softmax
  (:require [mab.arm :refer (update-arm map-on-arm-vals arm-value random-arm-tuple initialize-arm-map tuple-arm)]
            [mab.simulator :refer (create-bandit best-mean-index simulation-seq->table repeatedly-simulate-seq)]))


(defn arm->prob
  "Compute exp(value / temperature) for a given arm."
  [temperature arm]
  (Math/exp 
    (/ (arm-value arm) 
       temperature)))

(defn arm->proportional-prob
  "Compute an arms proportional probability."
  [temperature arm sum]
  (/ (arm->prob temperature arm) sum))

(defn sum-arm-prob
  "Computes the sum of the arm probabilities."
  [temperature arms]
  (apply + (vals (map-on-arm-vals (partial arm->prob temperature) arms))))


(defn proportional-probability-seq
  "Generate a seq of tuples with (cumulative proportional probability, arm tuple). This will facilitate
  the use of seq abstractions in finding the arm that pushes the cumulative probability above a limit.

  [(float arm) (float arm) ...]
  
  "
  [temperature arms]
  (let [sum (sum-arm-prob temperature arms)
        arm-seq (seq arms)
        first-arm (first arm-seq)]
    (reduce 
      (fn [acc a]
        (conj acc
              ; add the proportional prob of the last arm to the proportional
              ; prob of this arm
              (list (+ (first (last acc)) 
                       (arm->proportional-prob temperature (tuple-arm a) sum))
                    a))) 
      [(list (arm->proportional-prob temperature (tuple-arm first-arm) sum) first-arm)] 
      (rest arm-seq))))


(defn select-arm
  "Select an arm using the given temperature."
  [temperature arms]
  (let [z (rand)]
    (if-let [by-prob (second ; take the arm portion. see proportional-probability-seq
                       (first 
                         (drop-while #(<= (first %) z)
                                     (proportional-probability-seq temperature arms))))]
      by-prob
      (last (seq arms)))))



(defn test-algorithm
  "Tests the algorithm.

  sample-space: a seq of mean reward probabilities e.g. [0.1 0.9 0.1 0.1 0.1]
  horizon: pulls of arms e.g. 250
  iterations: how many iterations of horizon pulls e.g. 5000
  temperatures: temperatures to test e.g [0.1 0.2 0.3]

  Returns a hashmap of temperature => tabular result with columns
    temp, sim num, t arm chosen, reward, cumulative reward at t

  "
  [sample-space horizon iterations temperatures]
  (let [n (count sample-space)
        arms (initialize-arm-map n)
        bandit (create-bandit sample-space)
        best-arm (best-mean-index sample-space)]
    (println (format "Best arm is %d" best-arm))
    (mapcat (fn [t]
           (simulation-seq->table 
             (repeatedly-simulate-seq bandit 
                                      (partial select-arm t) 
                                      update-arm 
                                      arms
                                      horizon 
                                      iterations) 
             (list (format "softmax %2.2f" t))))
         temperatures)))
             

