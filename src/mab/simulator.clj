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
  "Given a seq of reward probabilities, create a Bernoulli arm for each. 
  Each Bernoulli arm is mapped to it's corresponding reward mean index 
  in the sample space."
  [means]
  (zipmap (range (count means)) 
          (map create-bernoulli-arm means)))

(defn create-keyed-bandit 
  "Given a seq of reward probabilities, create a Bernoulli arm for each under the corresponding keys."
  [means keys]
  (zipmap keys
          (map create-bernoulli-arm means)))

(defn best-mean-index 
  "Find the \"best\" arm or the arm with the highest p. For use with seq of means."
  [means]
  (.indexOf means (apply max-key max means)))


(defn draw-bernoulli-arm 
  "Draw a Bernoulli arm."
  [a]
  (a))


(defn create-result 
  "Create a result. :t and :cumulative-reward are initialzed to 0."
  [sim-num]
  {:t 0 
   :sim-num sim-num
   :chosen nil 
   :reward 0
   :cumulative-reward 0})


(defn create-simulation-map 
  [arms sim-num]
  {:arms arms 
   :results (create-result sim-num)})


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


(defn sim-num
  "Get the simulation number."
  [r]
  (get r :sim-num 1))


(defn cumulative-reward 
  "Get the cumulative reward from a result."
  [r]
  (get r :cumulative-reward 0))


(defn chosen
  "Get the arm chosen from a result."
  [r]
  (get r :chosen nil))


(defn reward
  "Get the reward from a result."
  [r]
  (get r :reward 0))

(defn inc-t 
  "Increment t in a result."
  [r]
  (update-in r [:t] inc))


(defn update-cumulative-reward 
  "Update the cumulative reward. Adds reward to the current reward."
  [r reward]
  (assoc r :cumulative-reward 
         (+ (cumulative-reward r) reward)))


(defn update-chosen
  "Update the chosen position."
  [r pos]
  (assoc r :chosen pos))


(defn update-reward
  "Update the reward."
  [r reward]
  (assoc r :reward reward))
  

(defn simulate 
  "Simulate the pull of an arm. Given the bandit an algorithm selection function 
  and update function updates the arms and the result hash with the results of a single draw.

  bandit: seq of Bernoulli arms
  select: should be of the form (select arms)
  update: of the form (update arms pos reward)
  hashmap: {:arms seq of arms created with mab.arms/create-arm
            :results initialized with (create-result sim-num)}
  
  "
  [bandit select update {:keys [arms results]}] 
  (let [tuple (select arms)
        idx (tuple-idx tuple)
        reward (draw-bernoulli-arm (arm-by-idx bandit idx))]
    {:arms (update arms idx reward)
     :results (-> results
                  (update-cumulative-reward reward)
                  (update-chosen idx)
                  (update-reward reward)
                  (inc-t))}))



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
                          update-arm 
                          (initialize-arm-map (count mean-sample-space)
                          sim-num))))
  
  "
  [bandit select update arms sim-num]
  (drop 1 (iterate (partial simulate bandit select update)
                   (create-simulation-map arms sim-num))))


(defn repeatedly-simulate-seq
  "Repeatedly run a given simulation. This allows for easy averaging the results 
  over multiple runs.

  (repeatedly-simulate-seq bandit 
                  (partial eg/select-arm 0.1) 
                  eg/update-arm 
                  (initialize-arm-map (count mean-sample-space))
                  250 1000)

  [...250]
  ...
  1000

  "
  [bandit select update arms horizon iterations]
  (map
    (fn [sim-num]
      (take horizon
            (simulation-seq bandit select update arms sim-num)))
    (range 1 (inc iterations))))



(defn extract-columns
  "Extract the columns required for analysis from a simulation. This includes:
    * time t
    * chosen arm
    * reward at time t
    * cumulative reward at time t

  Returs a seq mainly because we expect to prepend columns later for reporting.
  "
  [r]
    (seq ((juxt sim-num t chosen reward cumulative-reward) (simulation-result r))))


(defn add-columns
  "Add cols to s by prepending them onto the front. Maintains the order of cols."
  [s cols]
  (reduce
    (fn [acc e] 
      (conj acc e)) 
    (seq s) 
    (reverse cols)))


(defn simulation-seq->table
  "Given a simulation seq, extract the columns for analysis from eath simulation. Prepends params
  onto the extracted columns (see extract-columns).
  
  Simulations are tabular. See repeatedly-simulate-seq.

  To organize them, we want to add a column heading (e.g. epsilon) and the simulation number so 
  that results can be averaged over simulations using R. And then finnaly, we want to combine them all 
  into one table space for easy consumption.
  
  "
  [s & [params]]
  (mapcat 
    (fn [t] ; pull out the columns and add params to the front
        (map #(add-columns (extract-columns %) params) t))
      s))

(defn arm-reward-rate
  [arm]
  (if (> (arm-count arm) 0)
    (/ (arm-value arm) 
       (float (arm-count arm)))
    0))

(defn avg [nums]
  (/ (reduce + 0 nums)
     (float (count nums))))

(defn reward-rate-simulation 
  "Simulate (count sample-space) arms and compute the final reward rate for each arm. 
  The final value in each arm position is the average reward rate of each arm over the iterations."
  [sample-space selector horizon iterations]
  (let [psim (repeatedly-simulate-seq (create-bandit sample-space) 
                                      selector
                                      update-arm 
                                      (initialize-arm-map (count sample-space)) 
                                      horizon
                                      iterations)]
    (map avg
         ;; organize column-wise by arm row-wise by iteration organize column-wise by arm row-wise by iteration
         (apply map vector 
                ; compute the arm
                (map #(map (comp arm-reward-rate tuple-arm) %)
                     ; take the arms from the last simulation and convert to a seq
                     (map (comp seq simulation-arms last) psim))))))

