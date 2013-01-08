(ns mab.algorithms.epsilon-greedy-test
  (:use [midje.sweet]
        [mab arm simulator])
  (:require [mab.algorithms.epsilon-greedy :as mab-eg]))

(def test0 (create-arm 0 0 0))
(def test1 (create-arm 1 1 1))
(def test2 (create-arm 1 2 2))
(def test3 (create-arm 1 3 3))

(def arms [test0 test1 test2 test3])

(def mean-sample-space [0.9 0.1 0.1 0.1 0.1])
(def bandit (create-bandit mean-sample-space))

; epsilon at 0 should always pick the max-value arm
(fact
  (arm-uuid 
    (mab-eg/select-arm 0 arms)) => 3)
      
(let [sim  (last
             (take 1000 
                   (simulation-seq bandit 
                                   (partial mab-eg/select-arm 0.1) 
                                   update-arm 
                                   (initialize-arm-vector (count mean-sample-space)))))]
  (fact
    (reduce + 0 (map #(:count %) (:arms sim))) => 1000)

  (let [avg-rwd (float (/ (cumulative-reward (:results sim))
                          (t (:results sim))))]
    (println (format "Average reward is %2.2f" avg-rwd))
    (fact
      (> avg-rwd 0.70) => truthy)))


