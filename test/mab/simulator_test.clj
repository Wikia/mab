(ns mab.simulator-test
  (:use [midje.sweet]
        [mab arm simulator])
  (:require [mab.algorithms.epsilon-greedy :as eg]))

(def mean-sample-space (shuffle [0.1 0.1 0.1 0.1 0.9])) 
(def bandit (create-bandit mean-sample-space)) 

(let [b (create-bernoulli-arm 1)]
  (fact
    (draw-bernoulli-arm b) => 1))

(fact
  (count (create-bandit mean-sample-space)) => (count mean-sample-space))

(fact
  (best-arm-index [0 2 5 1 3]) => 2)

(fact
  (t (inc-t (create-result))) => 1)

(fact 
  (cumulative-reward
    (update-cumulative-reward (create-result) 10)) => 10)


(let [sim  (last
             (take 1000 
                   (simulation-seq bandit 
                                   (partial eg/select-arm 0.1) 
                                   update-arm 
                                   (initialize-arm-vector (count mean-sample-space)))))]
  (fact
    (reduce + 0 (map #(:count %) (:arms sim))) => 1000)

  (let [avg-rwd (float (/ (cumulative-reward (:results sim))
                          (t (:results sim))))]
    (fact
      (> avg-rwd 0.70) => truthy)))


(let [sim (create-simulation-map (initialize-arm-vector 3))]
  (facts
    (extract-columns sim) => truthy
    (count (extract-columns sim)) => 4))
    
