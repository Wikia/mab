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

