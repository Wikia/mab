library("plyr")
library("ggplot2")

results <- read.csv("plots/ucb1.csv", header = FALSE)
names(results) <- c("Algo", "Sim", "T", "ChosenArm", "Reward", "CumulativeReward")

# Plot average reward as a function of time.
stats <- ddply(results,
               c("T"),
               function (df) {mean(df$Reward)})
ggplot(stats, aes(x = T, y = V1)) +
  geom_line() +
  ylim(0, 1) +
  xlab("Time") +
  ylab("Average Reward") +
  ggtitle("Performance of the UCB1 Algorithm")
ggsave("plots/ucb1_average_reward.png")

# Plot frequency of selecting correct arm as a function of time.
# In this instance, 1 is the correct arm.
stats <- ddply(results,
               c("T"),
               function (df) {mean(df$ChosenArm == 1)})
ggplot(stats, aes(x = T, y = V1)) +
  geom_line() +
  ylim(0, 1) +
  xlab("Time") +
  ylab("Probability of Selecting Best Arm") +
  ggtitle("Accuracy of the UCB1 Algorithm")
ggsave("plots/ucb1_average_accuracy.png")

# Plot variance of chosen arms as a function of time.
stats <- ddply(results,
               c("T"),
               function (df) {var(df$ChosenArm)})
ggplot(stats, aes(x = T, y = V1)) +
  geom_line() +
  xlab("Time") +
  ylab("Variance of Chosen Arm") +
  ggtitle("Variability of the UCB1 Algorithm")
ggsave("plots/ucb1_variance_choices.png")

# Plot cumulative reward as a function of time.
stats <- ddply(results,
               c("T"),
               function (df) {mean(df$CumulativeReward)})
ggplot(stats, aes(x = T, y = V1)) +
  geom_line() +
  xlab("Time") +
  ylab("Cumulative Reward of Chosen Arm") +
  ggtitle("Cumulative Reward of the UCB1 Algorithm")
ggsave("plots/ucb1_cumulative_reward.png")
