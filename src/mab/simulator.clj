(ns mab.simulator
  (:use [mab arm]))

(defn create-bernoulli-arm 
  "Creates a Bernoulli arm that will reward 1 with probability p."
  [p]
  (with-meta 
    (fn [] 
      (if (> (rand 1) p)
        0
        1)) 
    {:probability p}))

(defn create-bandit 
  "Given a seq of reward probabilities, create a Bernoulli arm for each."
  [means]
  (map create-bernoulli-arm means))


(defn best-arm-index 
  "Find the \"best\" arm or the arm with the highest p. For use with seq of means."
  [means]
  (.indexOf means (apply max-key max means)))

(defn best-bandit-index
  "Find the \"best\" Bernoulli arm of the bandit."
  [bandit]
  (.indexOf bandit (apply max-key #(get (meta %) :probability) bandit)))


(defn draw-bernoulli-arm 
  "Draw a Bernoulli arm."
  [a]
  (a))


(defn create-result 
  "Create a result. :t and :cumulative-reward are initialzed to 0."
  []
  {:t 0 
   :cumulative-reward 0})

(defn create-simulation-map 
  [arms]
  {:arms arms :results (create-result)})

(defn simulation-arms 
  [m]
  (get m :arms))

(defn simulation-result
  [m]
  (get m :results))

(defn t 
  "Get t from a result."
  [r]
  (get r :t 0))

(defn cumulative-reward 
  "Get the cumulative reward from a result."
  [r]
  (get r :cumulative-reward 0))

(defn inc-t 
  "Increment t in a result."
  [r]
  (update-in r [:t] inc))

(defn update-cumulative-reward 
  "Update the cumulative reward. Adds reward to the current reward."
  [r reward]
  (assoc r :cumulative-reward 
         (+ (cumulative-reward r) reward)))
  

(defn simulate 
  "Simulate the pull of an arm. Given the bandit an algorithm selection function 
  and update function updates the arms and the result hash with the results of a single draw.

  bandit: seq of Bernoulli arms
  select: should be of the form (select arms)
  update: of the form (update arms pos reward)
  hashmap: {:arms seq of arms created with mab.arms/create-arm
            :results initialized with (create-result)}
  
  "
  [bandit select update {:keys [arms results]}] 
  (let [arm (select arms)
        pos (arm-position arms arm)
        reward (draw-bernoulli-arm (nth bandit pos))]
    {:arms (update arms pos reward)
     :results (inc-t (update-cumulative-reward results reward))}))


(defn simulation-seq 
  "Generates a lazy sequence of simulations. See simulate above for descriptions of
  bandit, select and update. Arms is a seq of arms created by mab.arms/create-arm.

  (:require [mab.algorithms.epsilon-greedy :as eg]))
  ...
  (def mean-sample-space (shuffle [0.1 0.1 0.1 0.1 0.9])) 
  (def bandit (create-bandit mean-sample-space)) 

  (take 1000 
          (simulation-seq bandit 
                          (partial eg/select-arm 0.1) 
                          eg/update-arm 
                          (initialize-arm-vector (count mean-sample-space)))))
  
  "
  [bandit select update arms]
  (drop 1 (iterate (partial simulate bandit select update)
           {:arms arms 
            :results (create-result)})))


(defn repeatedly-simulate-seq
  "Repeatedly run a given simulation. This allows for easy averaging the results 
  over multiple runs.

  (repeatedly-simulate-seq bandit 
                  (partial eg/select-arm 0.1) 
                  eg/update-arm 
                  (initialize-arm-vector (count mean-sample-space))
                  250 1000)

  [...250]
  ...
  1000

  "
  [bandit select update arms sims iterations]
  (repeatedly iterations
              #(take sims
                    (simulation-seq bandit select update arms))))



(defn average-reward-at-t
  "Compute the average reward at time t."
  [r]
   (float 
     (/ (cumulative-reward (simulation-result r))
        (t (simulation-result r)))))

(defn probability-chose-best-arm
  "Percentage of times the best arm was chosen at time t."
  [best-index r]
  (float 
    (/ (arm-count (nth (simulation-arms r) best-index))
       (t (simulation-result r)))))


(defn datapoint-at-t-seq
  "Calculate a given data point at time. For example:

  (datapoint-at-t-seq average-reward-at-t s)
  "
  [f s]
  (map f s))


(defn tabulate-simulation-results
  "Combine the results of N collections by index number starting at 1. The first column 
  generated is the index number. For example, given:

  (tabulate-simulation-results identity [4 5 6] [7 8 9]) => ((1 4 7) (2 5 8) (3 6 9))

  This is helpful for generating plots by deriving values from simulations or aggregated
  simulation results.

  "
  [result-f & cols]
  (let [trials (vec (map inc (range (count (first cols)))))]
    (apply 
      map vector 
      (concat [trials]
              (map #(map result-f %) cols)))))


(defn average-simulation-results
  "Average result-f over the values in results. Convenience method for averaging a datapoint at time t over N simulations."
  [result-f results]
  (let [cnt (count results)]
    (map #(/ % cnt)
         (map 
           #(reduce + 0 %)
           (partition (count results)
                      (apply interleave
                             (map #(datapoint-at-t-seq result-f %) results)))))))


  



