library("plyr")
library("ggplot2")

results <- read.csv("plots/combined.csv", header = FALSE)
names(results) <- c("Algo", "Sim", "T", "ChosenArm", "Reward", "CumulativeReward")
results <- transform(results, Algo = factor(Algo))

# Plot frequency of selecting correct arm as a function of time.
# In this instance, 1 is the correct arm. 
# ***** see mean-sample-space and best-arm from the repl-helper for the best arm *****
stats <- ddply(results,
               c("Algo", "T"),
               function (df) {mean(df$ChosenArm == 1)})
ggplot(stats, aes(x = T, y = V1, group = Algo, color = Algo)) +
  geom_line() +
  ylim(0, 1) +
  xlab("Time") +
  ylab("Probability of Selecting Best Arm") +
  ggtitle("Accuracy of the Tested Algorithms")
ggsave("plots/combined_average_accuracy.png")

# Plot cumulative reward as a function of time.
stats <- ddply(results,
               c("Algo", "T"),
               function (df) {mean(df$CumulativeReward)})
ggplot(stats, aes(x = T, y = V1, group = Algo, color = Algo)) +
  geom_line() +
  xlab("Time") +
  ylab("Cumulative Reward of Chosen Arm") +
  ggtitle("Cumulative Reward of the Tested Algorithm")
ggsave("plots/combined_cumulative_reward.png")
