(ns mab.algorithms.epsilon-greedy-test
  (:use [midje.sweet]
        [mab arm])
  (:require [mab.algorithms.epsilon-greedy :as mab-eg]))

(def test0 (create-arm 0 0 0))
(def test1 (create-arm 1 1 1))
(def test2 (create-arm 1 2 2))
(def test3 (create-arm 1 3 3))

(def arms [test0 test1 test2 test3])


; epsilon at 0 should always pick the max-value arm
(fact
  (arm-uuid 
    (mab-eg/select-arm 0 arms)) => 3)
      
