(ns mab.repl-helper
  (:use [clojure.pprint]
        [mab arm simulator])
  (:require [mab.algorithms.epsilon-greedy :as eg]))

(def mean-sample-space (shuffle [0.1 0.1 0.1 0.1 0.9])) 
(def bandit (create-bandit mean-sample-space)) 
(def arms (initialize-arm-vector (count mean-sample-space) 
                                            (fn [n] (format "name %d" n))))

; repeatedly simulate the given bandit 100 times across 250 pulls
(def s (repeatedly-simulate-seq bandit 
                     (partial eg/select-arm 0.1) 
                     eg/update-arm 
                     arms
                     250 100))

; average the reward at t across 100 simulations with 250 pulls each
(def a (average-simulation-results average-reward-at-t s)) 
