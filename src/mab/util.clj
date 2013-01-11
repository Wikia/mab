(ns mab.util
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))


(defn write-to-csv 
  "Write the simulation data to the given file."
  [file data & opts]
  (let [writer (if opts (apply io/writer file opts) (io/writer file))]
  (with-open [out-file writer] 
    (csv/write-csv out-file data))))
  

