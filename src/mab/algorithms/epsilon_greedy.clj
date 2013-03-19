(ns mab.algorithms.epsilon-greedy
  (:use [mab arm simulator]))


(defn select-arm 
  "Select an arm using the given epsilon for exploration."
  [epsilon arms]
  ; if all the arms have the same expected value, pick one at random. this will
  ; help prevent the overselection of the last arm
  (if (all-arm-values-equal? arms)
    (random-arm-tuple arms)
    (if (> (rand 1) epsilon)
      (max-value-tuple arms)
      (random-arm-tuple arms))))


(defn test-algorithm
  "Tests the algorithm.

  sample-space: a seq of mean reward probabilities e.g. [0.1 0.9 0.1 0.1 0.1]
  horizon: pulls of arms e.g. 250
  iterations: how many iterations of horizon pulls e.g. 5000
  epsilons: epsilons to test e.g [0.1 0.2 0.3]

  Returns a hashmap of epsilon => tabular result with columns
    epsilon, sim num, t arm chosen, reward, cumulative reward at t

  "
  [sample-space horizon iterations epsilons]
  (let [n (count sample-space)
        arms (initialize-arm-map n)
        bandit (create-bandit sample-space)
        best-arm (best-mean-index sample-space)]
    (println (format "Best arm is %d" best-arm))
    (mapcat (fn [e]
           (simulation-seq->table 
             (repeatedly-simulate-seq bandit 
                                      (partial select-arm e) 
                                      update-arm 
                                      arms
                                      horizon 
                                      iterations) 
             (list (format "eg %2.2f" e))))
         epsilons)))

