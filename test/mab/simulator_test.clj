(ns mab.simulator-test
  (:use [clojure pprint]
        [midje.sweet]
        [mab arm simulator])
  (:require [mab.algorithms.epsilon-greedy :as eg]))

(def mean-sample-space (shuffle [0.1 0.1 0.1 0.1 0.9])) 
(def bandit (create-bandit mean-sample-space)) 

(facts :bandit 
       (draw-bernoulli-arm (create-bernoulli-arm 1)) => 1
       (count (create-bandit mean-sample-space)) => (count mean-sample-space)
       (best-mean-index [0 2 5 1 3]) => 2

       (let [keyed (create-keyed-bandit [0.0 0.1 0.9 0.3 0.4] [0 1 2 3 4])]
         (-> keyed
           (get 0)
           (meta)
           (:probability)) => 0.0
         (-> keyed
           (get 1)
           (meta)
           (:probability)) => 0.1
         (-> keyed
           (get 2)
           (meta)
           (:probability)) => 0.9
         (-> keyed
           (get 3)
           (meta)
           (:probability)) => 0.3
         (-> keyed
           (get 4)
           (meta)
           (:probability)) => 0.4))



(facts :results
       (t (inc-t (create-result 1))) => 1
       (cumulative-reward (update-cumulative-reward (create-result 1) 10)) => 10)


(facts :simulation-map
  (let [sim (create-simulation-map (initialize-arm-map 3) 1)]
    (sim-num sim) => 1
    (extract-columns sim) => truthy
    (count (extract-columns sim)) => 5))
    

(facts :simulation-seq-table
       (let [sim (repeatedly-simulate-seq bandit 
                                          (partial eg/select-arm 1.0)
                                          update-arm 
                                          (initialize-arm-map (count mean-sample-space))
                                          2
                                          2)
             table (simulation-seq->table sim)]
         (count table) => 4
         (first (first table)) => 1
         (second (first table)) => 1
         (first (second table)) => 1
         (second (second table)) => 2

         (first (nth table 2)) => 2
         (second (nth table 2)) => 1
         (first (nth table 3)) => 2
         (second (nth table 3)) => 2
         (filter false? (map number? (mapcat identity table))) => empty?))

(facts :add-colums
       (add-columns '(c d e) '(a b)) => '(a b c d e)
       (add-columns '(c d e) nil) => '(c d e))

