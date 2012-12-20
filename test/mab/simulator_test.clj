(ns mab.simulator-test
  (:use [midje.sweet]
        [mab arm simulator]))


(let [b (create-bernoulli-arm 1)]
  (fact
    (draw-bernoulli-arm b) => 1))
