(ns mab.algorithms.ucb1-test
  (:use [midje.sweet]
        [mab arm simulator])
  (:require [mab.algorithms.ucb1 :as mab-ucb1]))


(def num-arms 10)
(def arms (initialize-arm-map num-arms))

(def mean-sample-space [0.9 0.1 0.1 0.1 0.1])
(def bandit (create-bandit mean-sample-space))

(facts "untested arm"
  (mab-ucb1/untested-arm arms) => truthy
  (arm-count (mab-ucb1/untested-arm arms)) => 0
  (mab-ucb1/untested-arm (map-on-arm-vals increment-count arms)) => falsey)

(facts "update curiosity bonus"
       ; these should all be > 1
       (filter false? (vals 
                         (map-on-arm-vals #(> (arm-score %) 1) 
                                          (mab-ucb1/update-curiosity-bonus-all 
                                            (map-on-arm-vals increment-count arms) 4)))) => empty?)

(facts "select arm"
       (let [chosen (mab-ucb1/select-arm (map-on-arm-vals increment-count arms))]
         (tuple-idx chosen) => number?
         (arm-score (tuple-arm chosen)) => #(> % 0)))

(facts "ucb1 reward"
       (let [sim (last (take 1000 (simulation-seq
                                    bandit
                                    mab-ucb1/select-arm
                                    update-arm
                                    (initialize-arm-map (count bandit))
                                    1)))
             avg-rwd (float (/ (cumulative-reward (:results sim))
                               (t (:results sim))))]
         (println (format "Average reward for UCB1 is %2.2f" avg-rwd))

         (reduce + 0 (map (comp arm-count tuple-arm) (:arms sim))) => 1000
         (> avg-rwd 0.80) => truthy))
