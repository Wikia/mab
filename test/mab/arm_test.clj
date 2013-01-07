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


(def init-counts (take 5 (cycle [0])))
(def init-values (take 5 (cycle [0])))
(def init-uuids  (range 5))
(def init-arms (initialize-arm-vector init-counts init-values init-uuids))

(fact 
  (arm-uuid (nth init-arms 3)) => 3)

(fact 
  (arm-uuid (nth init-arms 4)) => 4)

