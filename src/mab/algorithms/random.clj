(ns mab.algorithms.random
  (:use [mab arm simulator]))


(defn select-arm [arms]
  (nth arms (rand-int (count arms))))


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
