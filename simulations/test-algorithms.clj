
(def mean-sample-space [0.1 0.9 0.1 0.1 0.1])

(def bandit (create-bandit mean-sample-space))
(def arms (initialize-arm-map (count mean-sample-space)))

(def etest (eg/test-algorithm mean-sample-space 250 1000 [0.1 0.2 0.3 0.4 0.5]))
(write-to-csv "plots/epsilon-greedy.csv" etest)
(println (shell-exec "Rscript plots/epsilon-greedy.R"))

(def ucbtest (ucb1/test-algorithm mean-sample-space 250 1000))
(write-to-csv "plots/ucb1.csv" ucbtest)
(shell-exec "Rscript plots/ucb1.R")

(def softmaxtest (softmax/test-algorithm mean-sample-space 250 1000 [0.1 0.2 0.3 0.4 0.5]))
(write-to-csv "plots/softmax.csv" softmaxtest)
(shell-exec "Rscript plots/softmax.R")

(def random (r/test-algorithm mean-sample-space 250 1000))
(write-to-csv "plots/random.csv" random)
(shell-exec "Rscript plots/random.R")


(println "done.")
