(ns mab.arm-test
  (:use [midje.sweet]
        [mab arm]))

(def test-arm (create-arm 1 2 {}))

(def test1 (create-arm 0 0))
(def test2 (create-arm 0 0))
(def test3 (create-arm 0 0))

(def arms [test1 test2 test3])



(fact 
  (arm-count test-arm) => 1)

(fact 
  (arm-value test-arm) => 2)

(fact
  (arm-count 
    (update-count test-arm 2)) => 2)

(fact
  (arm-count 
    (increment-count test-arm)) => 2)

(fact
  (arm-position arms test1) => 0)

(fact
  (arm-position arms test2) => 1)

(fact
  (arm-position arms test3) => 2)


(def init-counts (take (count mean-sample-space) (cycle [0])))
(def init-values (take (count mean-sample-space) (cycle [0])))
(def init-uuids (range (count mean-sample-space)))
(def init-arms (initialize-arm-vector init-counts init-values init-uuids))

(fact 
  (arm-uuid (nth init-arms 3)) => 4)

(fact 
  (arm-uuid (nth init-arms 4)) => 4)

