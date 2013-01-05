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
                                   eg/update-arm 
                                   (initialize-arm-vector (count mean-sample-space)))))]
  (fact
    (reduce + 0 (map #(:count %) (:arms sim))) => 1000)

  (let [avg-rwd (float (/ (cumulative-reward (:results sim))
                          (t (:results sim))))]
    (fact
      (> avg-rwd 0.75) => truthy)))


(let [table (tabulate-simulation-results identity [4 5 6] [7 8 9])]
  (fact (first (first table)) => 1)
  (fact (second (first table)) => 4)
  (fact (first (second table)) => 2)
  (fact (second (second table)) => 5))

(let [v (average-simulation-results identity [(range 10) (map inc (range 10)) (map inc (range 10))])]
  (fact (first v) => 2/3)
  (fact (second v) => 5/3))

