(ns mab.algorithms.random
  (:use [mab arm simulator]))

; purely for testing and comparison purposes. 

(defn select-arm 
  "Select an arm at random 100% of the time."
  [arms]
  (random-arm-tuple arms))


(defn test-algorithm
  "Test using sample space over horizon for the given iterations."
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
                               iterations)
      (list "random"))))
