(def real-sample-space [0.000424022820137229 0.000464272405725212 0.000821778281904674 0.000821778281904674 0.000404241600977407 0.000681106116332925 0.000404827081003971 0.000731712013555928])

(def random-sample-space [0.000344791713109516 0.000461934276692784 0.000751259212885234 0.000591344199282995 0.000375297465889156 0.000724431818181818 0.00075399293535024])

(def horizon 100000)
(def iterations 100)

(defn n-draw-random-selector 
  "Wrapper for simulating the algorithmic selection of three arms and final selection of one at random."
  [select n arms]
    (let [tuples (select-n-arms select arms n)
          idx (nth tuples (rand-int n))]
      [idx (arm-by-idx arms idx)]))

(defn seq->table
  [s]
  (map list s))

; epsilon greedy
(def prod-sim-eg (reward-rate-simulation real-sample-space (partial eg/select-arm 0.1) horizon iterations))
(write-to-csv "plots/prod-sim-eg.csv" (seq->table prod-sim-eg))

(def eg-three-draw (partial #'n-draw-random-selector (partial eg/select-arm 0.1) 3))
(def prod-sim-three-pull-eg (reward-rate-simulation real-sample-space eg-three-draw horizon iterations))
(write-to-csv "plots/prod-sim-three-pull-eg.csv" (seq->table prod-sim-three-pull-eg))

; random
(def prod-sim-random (reward-rate-simulation real-sample-space r/select-arm horizon iterations))
(write-to-csv "plots/prod-sim-random.csv" (seq->table prod-sim-random))

(def random-three-draw (partial #'n-draw-random-selector r/select-arm 3))
(def prod-sim-three-pull-random (reward-rate-simulation real-sample-space random-three-draw horizon iterations)) 
(write-to-csv "plots/prod-sim-three-pull-random.csv" (seq->table prod-sim-three-pull-random))

(println "done.")

(shell-exec "Rscript plots/production-simulation.R")


