(ns mab.repl-helper
  (:use [clojure pprint]
        [mab arm simulator util])
  (:require [mab.algorithms.epsilon-greedy :as eg]
            [mab.algorithms.ucb1 :as ucb1]
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
(def mean-sample-space [0.9 0.1 0.1 0.1 0.1])

(def bandit (create-bandit mean-sample-space))
(def etest (eg/test-algorithm mean-sample-space 250 1000 [0.1 0.2 0.3 0.4 0.5]))
(def arms (initialize-arm-vector (count mean-sample-space)))

(def s (repeatedly-simulate-seq bandit 
                         ucb1/select-arm
                         (fn [a p r]
                           (-> a 
                               (ucb1/update-curiosity-bonus (total-arm-counts arms))
                               (update-arm p r)))
                         arms
                         10 
                         1))

;(write-to-csv "plots/epsilon-greedy.csv" etest)

; (def d (ucb1/test-algorithm [0.0024 0.0016 0.0011 0.001 0.0008 0.0008 0.0008] 1000000 2)


