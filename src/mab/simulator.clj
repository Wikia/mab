(ns mab.simulator
  (:use [mab arm]))

(defn create-bernoulli-arm [p]
  (fn [] 
    (if (> (rand 1) p)
      0
      1)))

(defn draw-bernoulli-arm [a]
  (a))




