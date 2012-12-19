(ns mab.algorithms.epsilon-greedy-test
  (:use [midje.sweet]
        [mab arm])
  (:require [mab.algorithms.epsilon-greedy :as mab-eg]))

(def test1 (create-arm 0 0 {:name "test1"}))
(def test2 (create-arm 0 0 {:name "test2"}))
(def test3 (create-arm 0 0 {:name "test3"}))

(def arms [test1 test2 test3])

(fact
  (mab-eg/compute-value 1 0 100) => 100)

(fact 
  (arm-value (nth (mab-eg/update-arm arms 0 100) 0)) => 100)
      
