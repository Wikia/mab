(ns mab.util
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn write-to-csv 
  "Write the simulation data to the given file."
  [file data]
  (with-open [out-file (io/writer file)] 
    (csv/write-csv out-file data)))
  

