(ns mab.arm-test
  (:use [midje.sweet]
        [clojure pprint]
        [mab arm]))

(def test-arm (create-arm 1 2 {}))

(def test1 (create-arm 0 0))
(def test2 (create-arm 0 0))
(def test3 (create-arm 0 0))

(def arms [test1 test2 test3])


(facts "basic arm tests"
  (arm-count test-arm) => 1
  (arm-value test-arm) => 2
  (arm-count 
    (update-count test-arm 2)) => 2
  (arm-count 
    (increment-count test-arm)) => 2
  (arm-position arms test1) => 0
  (arm-position arms test2) => 1
  (arm-position arms test3) => 2)

(facts "remove by uuid"
  (count (remove-by-uuid arms (arm-uuid test3))) => 2
  (count 
    (remove-by-uuid 
      (remove-by-uuid 
        (remove-by-uuid arms (arm-uuid test3))
        (arm-uuid test2))
      (arm-uuid test1))) => 0)

(facts "select n arms"
       (count (select-n-arms (fn [a]
                               (nth a (rand-int (count a)))) 
                             arms (count arms))) => (count arms)
       (count (select-n-arms (fn [a]
                               (nth a (rand-int (count a)))) 
                             arms (inc (count arms)))) => (count arms))


(def init-counts (take 5 (cycle [0])))
(def init-values (take 5 (cycle [0])))
(def init-uuids  (range 5))
(def init-arms (initialize-arm-vector init-counts init-values init-uuids))

(fact 
  (arm-uuid (nth init-arms 3)) => 3)

(fact 
  (arm-uuid (nth init-arms 4)) => 4)

(fact
  (compute-value 1 0 100) => 100)

(fact 
  (arm-value (nth (update-arm arms 0 100) 0)) => 100)
