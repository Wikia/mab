(ns mab.util
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))


(defn write-to-csv 
  "Write the simulation data to the given file."
  [file data & opts]
  (let [writer (if opts (apply io/writer file opts) (io/writer file))]
  (with-open [out-file writer] 
    (csv/write-csv out-file data))))
  

(defn eq-key
  "Similar to max-key but tests that all of the (f x) are equal."
  ([f x] x)
  ([f x y] (if (= (f x) (f y)) x false))
  ([f x y & more]
   (reduce #(eq-key f %1 %2) (eq-key f x y) more)))


(defn avg 
  "Compute the average of nums."
  [nums]
  (/ (reduce + 0 nums)
     (float (count nums))))


(defn map-on-map-vals
  "map f over the values of m.
    Example (map-on-map-vals inc-count arms)"
  [f m]
  (into {} (for [[k v] m] [k (f v)])))
