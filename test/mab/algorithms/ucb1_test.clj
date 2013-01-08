(ns mab.algorithms.ucb1-test
  (:use [midje.sweet]
        [mab arm simulator])
  (:require [mab.algorithms.ucb1 :as mab-ucb1]))


(def num-arms 10)
(def init-counts (take num-arms (cycle [0])))
(def init-values (take num-arms (cycle [0])))
(def init-uuids  (range num-arms))
(def arms (initialize-arm-vector init-counts init-values init-uuids))

(def mean-sample-space [0.9 0.1 0.1 0.1 0.1])
(def bandit (create-bandit mean-sample-space))

(facts "ucb1/untested-arm"
  (mab-ucb1/untested-arm arms) => truthy
  (arm-count (mab-ucb1/untested-arm arms)) => 0
  (arm-uuid (mab-ucb1/untested-arm arms)) => 0
  (mab-ucb1/untested-arm (map increment-count arms)) => falsey)


(let [sim (last (take 1000 (simulation-seq
                             bandit
                             mab-ucb1/select-arm
                             (fn [a p r]
                               (-> a 
                                   (mab-ucb1/update-curiosity-bonus (total-arm-counts arms))
                                   (update-arm p r)))
                             (initialize-arm-vector (count bandit)))))
      avg-rwd (float (/ (cumulative-reward (:results sim))
                        (t (:results sim))))]
  (println (format "Average reward for UCB1 is %2.2f" avg-rwd))

  (facts "ucb1 reward"
         (reduce + 0 (map #(:count %) (:arms sim))) => 1000
         (> avg-rwd 0.80) => truthy))
