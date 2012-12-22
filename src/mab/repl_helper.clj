(ns mab.repl-helper
  (:use [clojure pprint walk]
        [mab arm simulator])
  (:require [mab.algorithms.epsilon-greedy :as eg]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))


(defn write-to-csv 
  [file data]
  (with-open [out-file 
              (io/writer file)] 
    (csv/write-csv out-file data)))

(defn test-epsilons
  [epsilons]
  (map #(repeatedly-simulate-seq bandit 
                     (partial eg/select-arm %) 
                     eg/update-arm 
                     arms
                     250 100) epsilons))

(defn average-reward->csv
  [sims]
    (map 
      #(map (fn [s] (.toString s)) %) 
      (apply 
        tabulate-simulation-results 
        identity 
        (map #(average-simulation-results average-reward-at-t %) sims))))

(def mean-sample-space (shuffle [0.1 0.1 0.1 0.1 0.9])) 
(def bandit (create-bandit mean-sample-space)) 
(def arms (initialize-arm-vector (count mean-sample-space) 
                                            (fn [n] (format "name %d" n))))

; repeatedly simulate the given bandit 100 times across 250 pulls
;(def s (repeatedly-simulate-seq bandit 
;                     (partial eg/select-arm 0.1) 
;                     eg/update-arm 
;                     arms
;                     250 300))
;
;; average the reward at t across 100 simulations with 250 pulls each
;(def a (average-simulation-results average-reward-at-t s)) 
;
;(def data (map 
;            #(map (fn [s] (.toString s)) %) 
;            (tabulate-simulation-results identity a)))

(def etest (test-epsilons [0.1 0.2 0.3 0.4 0.5]))
(def data (average-reward->csv etest))
;(write-to-csv "plots/epsilon-greedy.csv" data)
