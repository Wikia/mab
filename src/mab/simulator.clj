(ns mab.simulator
  (:use [mab arm]))

(defn create-bernoulli-arm [p]
  (fn [] 
    (if (> (rand 1) p)
      0
      1)))

(defn create-bandit [means]
  (map create-bernoulli-arm means))


(defn best-arm-index [means]
  (.indexOf means (apply max-key max means)))


(defn draw-bernoulli-arm [a]
  (a))

(defn initialize-arm-vector [n name-fn]
  (vec
    (map 
      #(create-arm 0 0 {:name (name-fn %)})
      (range n))))

(defn create-result []
  {:t 0 :cumulative-reward 0})

(defn t [r]
  (get r :t 0))

(defn cumulative-reward [r]
  (get r :cumulative-reward 0))

(defn inc-t [r]
  (update-in r [:t] inc))

(defn update-cumulative-reward [r reward]
  (assoc r :cumulative-reward 
         (+ (cumulative-reward r) reward)))
  

(defn simulate [bandit select update {:keys [arms results]}] 
  (let [arm (select arms)
        pos (arm-position arms arm)
        reward (draw-bernoulli-arm (nth bandit pos))]
    {:arms (update arms pos reward)
     :results (inc-t (update-cumulative-reward results reward))}))


(defn simulation-seq [bandit select update arms]
  (drop 1 (iterate (partial simulate bandit select update)
           {:arms arms 
            :results (create-result)})))






  



