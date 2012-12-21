(ns mab.repl-helper
  (:use [clojure.pprint]
        [mab arm simulator])
  (:require [mab.algorithms.epsilon-greedy :as eg]))

(def mean-sample-space (shuffle [0.1 0.1 0.1 0.1 0.9])) 
(def bandit (create-bandit mean-sample-space)) 
(def o 
  (last
    (take 1000 
          (simulation-seq bandit 
                          (partial eg/select-arm 0.1) 
                          eg/update-arm 
                          (initialize-arm-vector (count mean-sample-space) 
                                                 (fn [n] (format "name %d" n)))))))

