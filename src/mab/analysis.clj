(ns mab.analysis
  (:require 
    [clojure.java.shell :as shell]
    [environ.core :refer [env]]
    (mab 
      [arm :refer :all]
      [simulator :refer :all]
      [util :refer [write-to-csv]])
    [mab.algorithms.epsilon-greedy :as eg]
    [mab.algorithms.ucb1 :as ucb1]
    [mab.algorithms.softmax :as softmax]
    [mab.algorithms.random :as r]))

(defn split-cmd 
  "Split a command string (e.g. \"Rscript plots/ucb1.R\") into it's constituent parts for input to sh."
  [cmd]
  (if (string? cmd)
    (clojure.string/split cmd #"\s+")
    cmd))

(defn shell-exec
  "Exec the given command on the shell."
  [cmd]
  (apply shell/sh (split-cmd cmd)))



(defn -main
  [& [file]]
  (println (format "Running analysis from file %s.\n" file))
  (load-file file))
