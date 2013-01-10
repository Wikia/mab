(ns mab.arm-test
  (:use [midje.sweet]
        [clojure pprint]
        [mab arm]))

(def test-arm (create-arm 1 2))

(def test1 (create-arm 0 0))
(def test2 (create-arm 0 0))
(def test3 (create-arm 0 0))

(def arms (zipmap (range 3) [test1 test2 test3]))

(facts "compute update value"
       ; bogus but predictable
       (compute-update-value 2 2 1) => 1.5)

(facts "update arm"
       (arm-value (arm-by-idx (update-arm (update-arm (update-arm arms 0 1) 0 1) 0 1) 0)) => #(> % 0))
      
(facts "basic arm tests"
       (arm-count test-arm) => 1
       (arm-value test-arm) => 2
       (arm-count 
         (update-count test-arm 2)) => 2
       (arm-count 
         (increment-count test-arm)) => 2)

(facts "replace arm"
       (let [idx 0
             v 999]
         (arm-count 
           (arm-by-idx 
             (replace-arm arms idx (create-arm v v)) idx)) => v
         (arm-count
           (arm-by-idx
             (replace-arm arms v (create-arm v v)) v)) => v))

(facts "max value tuple"
      (arm-value 
        (tuple-arm 
          (max-value-tuple 
            (replace-arm arms 1 
                         (update-value 
                           (arm-by-idx arms 1) 10))))) => 10)


(facts "arm tuples"
       (tuple-idx (random-arm arms)) => number?
       (arm-count (tuple-arm (random-arm arms))) => number?
       (arm-value (tuple-arm (random-arm arms))) => number?)

(fact 
  (total-arm-counts (initialize-arm-map (range 1 5) (range 1 5))) => 10)

(facts "remove by index"
  (count (remove-arm arms 2)) => 2
  (count (remove-arm (remove-arm (remove-arm arms 2) 1) 0)) => 0)

(facts "random arm"
       (let [sample-size 10000.0
             arm-count 10
             band 0.6]
         (> band 
            (- 5 (/ (reduce + 0 (take sample-size (repeatedly #(random-arm-idx (initialize-arm-map 10))))) sample-size))) => truthy)
       (random-arm arms) => truthy
       (count (random-arm arms)) => 2
       (first (random-arm arms)) => truthy
       (second (random-arm arms)) => truthy
       (arm-count (random-arm arms)) => 0
       (arm-value (random-arm arms)) => 0)

(facts "select n arms"
       (count (select-n-arms random-arm 
                             arms (count arms))) => (count arms)
       (count (select-n-arms random-arm 
                             arms (inc (count arms)))) => (count arms))


(facts "map on arm values"
       (total-arm-counts arms) => 0
       (total-arm-counts (map-on-arm-vals increment-count arms)) => (count arms))
