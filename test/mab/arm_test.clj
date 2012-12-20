(ns mab.arm-test
  (:use [midje.sweet]
        [mab arm]))

(def test-arm (create-arm 1 2 {}))

(def test1 (create-arm 0 0 {:name "test1"}))
(def test2 (create-arm 0 0 {:name "test2"}))
(def test3 (create-arm 0 0 {:name "test3"}))

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


