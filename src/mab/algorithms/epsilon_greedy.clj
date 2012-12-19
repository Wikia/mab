(ns mab.algorithms.epsilon-greedy
  (:use [mab arm]))


(defn compute-value [n value reward] 
  (+ (* (/ (- n 1) n) value) (* (/ 1 n) reward)))

(defn update-arm [arms arm-position reward]
  (let [chosen-arm (increment-count (nth arms arm-position))
        n (arm-count chosen-arm)
        value (arm-value chosen-arm)
        new-value (compute-value n value reward)]
    (assoc arms arm-position 
           (update-value chosen-arm new-value))))

(defn max-value [arms]
  (apply max-key arm-value arms))

(defn find-max-value-arm [arms]
  (let [m (max-value arms)]
    (arm-position arms m)))

(defn select-arm [arms epsilon]
  (if (> (rand 1) epsilon)
    (find-max-value-arm arms)
    (nth arms (rand-int (count arms)))))
