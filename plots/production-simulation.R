prod_sim_eg <- scan(file="plots/prod-sim-eg.csv")
prod_sim_random <- scan(file="plots/prod-sim-random.csv")
prod_sim_three_pull_eg <- scan(file="plots/prod-sim-three-pull-eg.csv")
prod_sim_three_pull_random <- scan(file="plots/prod-sim-three-pull-random.csv")

png("plots/prod-sim-eg-random.png")

boxplot(prod_sim_eg,prod_sim_random,prod_sim_three_pull_eg,prod_sim_three_pull_random,
    col=c("lightgreen", "lightblue"), xlab="Algorithm", ylab="CTR")
axis(side=1, 1:4, labels=c("E-Greedy", "Random", "Three E-Greedy", "Three Random"), cex.axis=0.8)
title("Epsilon Greedy vs Random Simulations\nof Production")
dev.off()
