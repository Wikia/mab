(ns mab.repl-helper
  (:use [clojure pprint]
        [mab arm simulator util])
  (:require [mab.algorithms.epsilon-greedy :as eg]
            [mab.algorithms.ucb1 :as ucb1]
            [mab.algorithms.softmax :as softmax]
            [mab.algorithms.random :as r]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))


; data file format
; epsilon, sim num, t, arm chosen, reward, cumulative reward
;
; watch the consumption 
; top -o rsize -s 5 -n 20

; don't make the best arm the last one or it will be chosen by default on the
; first run since the value for all of the arms at the start is 0. OTW, you
; will skew the results and get odd results
(def mean-sample-space [0.1 0.9 0.1 0.1 0.1])

(def bandit (create-bandit mean-sample-space))
(def arms (initialize-arm-map (count mean-sample-space)))

(def s (repeatedly-simulate-seq bandit 
                         ucb1/select-arm
                         update-arm
                         arms
                         10 
                         1))

; with sm after taking 10000 you get a stack overflow
;(time (last (simulation-seq->table [sm]))) 
(def sm (take 100 (simulation-seq bandit ucb1/select-arm update-arm (initialize-arm-map (count mean-sample-space)) 1)))


(def etest (eg/test-algorithm mean-sample-space 250 1000 [0.1 0.2 0.3 0.4 0.5]))
;(write-to-csv "plots/epsilon-greedy.csv" etest)
(def ucbtest (ucb1/test-algorithm mean-sample-space 250 1000))
;(write-to-csv "plots/ucb1.csv" ucbtest)
(def softmaxtest (softmax/test-algorithm mean-sample-space 250 1000 [0.1 0.2 0.3 0.4 0.5]))
;(write-to-csv "plots/softmax.csv" softmaxtest)
(def random (r/test-algorithm mean-sample-space 250 1000))

(def real-sample-space [0.0016 0.0024 0.0016 0.0011 0.001 0.0008 0.0008 0.0008])

;(def etest (eg/test-algorithm real-sample-space 10000 100 [0.1 0.2 0.3 0.4 0.5]))
(def etest-real (eg/test-algorithm real-sample-space 50000 100 [0.1 0.2]))
;(write-to-csv "plots/epsilon-greedy.csv" (apply concat etest-real))
(def ucbtest-real (ucb1/test-algorithm real-sample-space 50000 100))
;(write-to-csv "plots/ucb1.csv" ucbtest-real)
(def smtest-real (softmax/test-algorithm real-sample-space 50000 100 [0.1]))
;(write-to-csv "plots/softmax.csv" smtest-real)

(def rtest-real (r/test-algorithm real-sample-space 10000 100))
;(def rf (future (write-to-csv "plots/random.csv" rtest-real)))


(defn combined-test [sample-space h i]
  (do
    (write-to-csv 
       "plots/combined.csv"
      (eg/test-algorithm sample-space h i [0.1]))

    (write-to-csv 
       "plots/combined.csv"
      (ucb1/test-algorithm sample-space h i) :append true)

    (write-to-csv 
       "plots/combined.csv"
      (softmax/test-algorithm sample-space h i [0.1]) :append true)

    (write-to-csv 
       "plots/combined.csv"
      (r/test-algorithm sample-space h i) :append true)))

(defn production-ctr-simulation [sample-space selector horizon iterations]
  (let [psim (repeatedly-simulate-seq (create-bandit sample-space) 
                                      selector
                                      update-arm 
                                      (initialize-arm-map (count sample-space)) 
                                      horizon
                                      iterations)]
    (map #(/ (:cumulative-reward (:results (last %))) (float horizon)) psim)))


(defprotocol Simulation
  (sim [this]))

(defprotocol SimulationState
  (get-arms [this])
  (update-sim [this idx reward]))

(defrecord BanditSimulation [arms results]
  SimulationState
  (get-arms [this]
    (:arms this))
  (update-sim [this idx reward]
    (assoc this :arms (update-arm (:arms this) idx reward)
                :results (-> (:results this)
                             (update-cumulative-reward reward)
                             (update-chosen idx)
                             (update-reward reward)
                             (inc-t)))))

(defrecord MultiArmedBanditSimulation [bandit select sim-state]
  Simulation
  (sim [this]
    (let [tuple (select (get-arms sim-state))
          idx (tuple-idx tuple)
          reward (draw-bernoulli-arm (arm-by-idx bandit idx))]
      (update-sim sim-state idx reward))))


(defrecord NDrawMultiArmedBanditSimulation [bandit select n sim-state]
  Simulation
  (sim [this]
    (let [tuples (select-n-arms select (get-arms sim-state) n)
          idx (nth tuples (rand-int n))
          reward (draw-bernoulli-arm (arm-by-idx bandit idx))]
      (update-sim sim-state idx reward))))





(defrecord BanditSimulation [bandit select arms results]
  Simulation
  (sim [this]
    (let [tuple (select (:arms this))
          idx (tuple-idx tuple)
          reward (draw-bernoulli-arm (arm-by-idx bandit idx))]
        (assoc this :arms (update-arm (:arms this) idx reward)
                    :results (-> (:results this)
                                 (update-cumulative-reward reward)
                                 (update-chosen idx)
                                 (update-reward reward)
                                 (inc-t))))))


(defrecord NDrawBanditSimulation [bandit select n arms results]
  Simulation
  (sim [this]
    (let [tuples (select-n-arms select (:arms this) n)
          idx (nth tuples (rand-int n))
          reward (draw-bernoulli-arm (arm-by-idx bandit idx))]
        (assoc this :arms (update-arm (:arms this) idx reward)
                    :results (-> (:results this)
                                 (update-cumulative-reward reward)
                                 (update-chosen idx)
                                 (update-reward reward)
                                 (inc-t))))))
