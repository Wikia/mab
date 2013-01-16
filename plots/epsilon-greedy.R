library("plyr")
library("ggplot2")

results <- read.csv("plots/epsilon-greedy.csv", header = FALSE)
names(results) <- c("Epsilon", "Sim", "T", "ChosenArm", "Reward", "CumulativeReward")
results <- transform(results, Epsilon = factor(Epsilon))

# Plot average reward as a function of time.
stats <- ddply(results,
               c("Epsilon", "T"),
               function (df) {mean(df$Reward)})
ggplot(stats, aes(x = T, y = V1, group = Epsilon, color = Epsilon)) +
  geom_line() +
  ylim(0, 1) +
  xlab("Time") +
  ylab("Average Reward") +
  ggtitle("Performance of the Epsilon Greedy Algorithm")
ggsave("plots/standard_epsilon_greedy_average_reward.png")

# Plot frequency of selecting correct arm as a function of time.
# In this instance, 1 is the correct arm. 
# ***** see mean-sample-space and best-arm from the repl-helper for the best arm *****
stats <- ddply(results,
               c("Epsilon", "T"),
               function (df) {mean(df$ChosenArm == 1)})
ggplot(stats, aes(x = T, y = V1, group = Epsilon, color = Epsilon)) +
  geom_line() +
  ylim(0, 1) +
  xlab("Time") +
  ylab("Probability of Selecting Best Arm") +
  ggtitle("Accuracy of the Epsilon Greedy Algorithm")
ggsave("plots/standard_epsilon_greedy_average_accuracy.png")

# Plot variance of chosen arms as a function of time.
stats <- ddply(results,
               c("Epsilon", "T"),
               function (df) {var(df$ChosenArm)})
ggplot(stats, aes(x = T, y = V1, group = Epsilon, color = Epsilon)) +
  geom_line() +
  xlab("Time") +
  ylab("Variance of Chosen Arm") +
  ggtitle("Variability of the Epsilon Greedy Algorithm")
ggsave("plots/standard_epsilon_greedy_variance_choices.png")

# Plot cumulative reward as a function of time.
stats <- ddply(results,
               c("Epsilon", "T"),
               function (df) {mean(df$CumulativeReward)})
ggplot(stats, aes(x = T, y = V1, group = Epsilon, color = Epsilon)) +
  geom_line() +
  xlab("Time") +
  ylab("Cumulative Reward of Chosen Arm") +
  ggtitle("Cumulative Reward of the Epsilon Greedy Algorithm")
ggsave("plots/standard_epsilon_greedy_cumulative_reward.png")
