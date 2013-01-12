(ns mab.simulator-test
  (:use [clojure pprint]
        [midje.sweet]
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
  (best-mean-index [0 2 5 1 3]) => 2)

(fact
  (t (inc-t (create-result))) => 1)

(fact 
  (cumulative-reward
    (update-cumulative-reward (create-result) 10)) => 10)


(facts "create simulation map"
  (let [sim (create-simulation-map (initialize-arm-map 3))]
    (extract-columns sim) => truthy
    (count (extract-columns sim)) => 4))
    

(facts "simulation seq to table"
       (let [sim (repeatedly-simulate-seq bandit 
                                          (partial eg/select-arm 1.0)
                                          update-arm 
                                          (initialize-arm-map (count mean-sample-space))
                                          2
                                          2)
             table (simulation-seq->table sim)]
         (count table) => 4
         (filter false? (map number? (mapcat identity table))) => empty?))

