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

; TODO make sims and iterations params
(defn test-epsilons
  [bandit arms sims iterations epsilons]
  (map #(repeatedly-simulate-seq bandit 
                     (partial eg/select-arm %) 
                     eg/update-arm 
                     arms
                     sims iterations) epsilons))

(defn simulation-results->csv
  [result-f sims]
    (map 
      #(map (fn [s] (.toString s)) %) 
      (apply 
        tabulate-simulation-results 
        identity 
        (map #(average-simulation-results result-f %) sims))))

(def mean-sample-space (shuffle [0.1 0.1 0.1 0.1 0.9])) 
(def best-arm (best-arm-index mean-sample-space))
(def bandit (create-bandit mean-sample-space)) 

(def init-counts (take (count mean-sample-space) (cycle [0])))
(def init-values (take (count mean-sample-space) (cycle [0])))
(def init-uuids (range (count mean-sample-space)))
(def arms (initialize-arm-vector init-counts init-values init-uuids))

; repeatedly simulate the given bandit 100 times across 250 pulls
(def s (repeatedly-simulate-seq bandit 
                     (partial eg/select-arm 0.1) 
                     eg/update-arm 
                     arms
                     250 100))
;
;; average the reward at t across 100 simulations with 250 pulls each
;(def a (average-simulation-results average-reward-at-t s)) 
;
;; % of times at time t that algorithm chose the best arm
;(def p (average-simulation-results 
; (partial probability-chose-best-arm (best-bandit-index bandit)) s))
;
;(def data (map 
;            #(map (fn [s] (.toString s)) %) 
;            (tabulate-simulation-results identity a)))

(def etest (test-epsilons bandit arms 250 1000 [0.1 0.2 0.3 0.4 0.5]))
(def reward-csv-data (simulation-results->csv average-reward-at-t etest))
(def best-arm-csv-data (simulation-results->csv 
                         (partial probability-chose-best-arm (best-bandit-index bandit)) etest))
; watch the consumption 
; top -o rsize -s 5 -n 20
;
;(write-to-csv "plots/epsilon-greedy-reward.csv" reward-csv-data)
;(write-to-csv "plots/epsilon-greedy-best-arm.csv" best-arm-csv-data)
