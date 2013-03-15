(ns mab.util-test
  (:require 
    [clojure.pprint :refer [pprint]]
    [midje.sweet :refer :all]
    [mab.util :refer :all]))

(facts :eq-key
       (eq-key identity true) => truthy
       (eq-key identity true true) => truthy
       (eq-key identity true true true) => truthy
       (eq-key identity true true true true) => truthy

       (eq-key identity true false) => falsey
       (eq-key identity false true) => falsey
       (eq-key identity true true false) => falsey
       (eq-key identity true true true false) => falsey

       (eq-key identity 1 1 1 1) => truthy
       (eq-key identity 1 1 2 1) => falsey
       (eq-key identity 1 true 1 1) => falsey

       (apply (partial eq-key identity) (take 10000 (cycle [1]))) => truthy
       (apply (partial eq-key inc) (take 10000 (cycle [1]))) => truthy
       (apply (partial eq-key dec) (take 10000 (cycle [1]))) => truthy)

(facts :avg
       (avg [1 2 3 4 5]) => 3.0
       (avg [1 1 1]) => 1.0
       (avg (take 100 (cycle [1 2]))) => 1.5)


(facts :map-on-map-vals
       (let [m (zipmap [1 2 3] [1 2 3])]
         (get (map-on-map-vals inc m) 3) => 4))

(facts :frequencies->probability
       (let [p (frequencies->probability (frequencies (take 1000 (cycle [:a :b]))))]
         (:a p) => 0.5
         (:b p) => 0.5)
       
       (:a (frequencies->probability (frequencies (take 100 (cycle [:a]))))) => 1.0)
       

