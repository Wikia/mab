(ns mab.algorithms.epsilon-greedy-test
  (:use [midje.sweet]
        [mab arm])
  (:require [mab.algorithms.epsilon-greedy :as mab-eg]))

(def test0 (create-arm 0 0 {:name "test0"}))
(def test1 (create-arm 1 1 {:name "test1"}))
(def test2 (create-arm 1 2 {:name "test2"}))
(def test3 (create-arm 1 3 {:name "test3"}))

(def arms [test0 test1 test2 test3])

(fact
  (mab-eg/compute-value 1 0 100) => 100)

(fact 
  (arm-value (nth (mab-eg/update-arm arms 0 100) 0)) => 100)

; epsilon at 0 should always pick the max-value arm
(fact
  (get 
    (arm-meta 
      (mab-eg/select-arm arms 0))
    :name) => "test3") 
      
