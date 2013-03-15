(ns mab.algorithms.epsilon-greedy-test
  (:use [midje.sweet]
        [mab arm simulator util])
  (:require [mab.algorithms.epsilon-greedy :as mab-eg]))


(def mean-sample-space [0.9 0.1 0.1 0.1 0.1])
(def bandit (create-bandit mean-sample-space))

      
(let [sim  (last
             (take 1000 
                   (simulation-seq bandit 
                                   (partial mab-eg/select-arm 0.1) 
                                   update-arm 
                                   (initialize-arm-map (count mean-sample-space))
                                   1)))]
  (fact
    (reduce + 0 (map (comp arm-count tuple-arm) (:arms sim))) => 1000)

  (let [avg-rwd (float (/ (cumulative-reward (:results sim))
                          (t (:results sim))))]
    (fact
      (> avg-rwd 0.70) => truthy)))


(facts :accuracy :slow
       (get (frequencies->probability (simulate-best-arm-selection (partial mab-eg/select-arm 0.15) 5 8000 100)) true) => 1.0)
