set datafile separator ","
set title ""
# set terminal png
# set output "plots/epsilon-greedy.jpeg"
set ylabel "average reward"
set xlabel "t"
plot "plots/epsilon-greedy.csv" using 1:2 ti "0.1" with lines, "plots/epsilon-greedy.csv" using 1:3 ti "0.2" with lines, "plots/epsilon-greedy.csv" using 1:4 ti "0.3" with lines, "plots/epsilon-greedy.csv" using 1:5 ti "0.4" with lines, "plots/epsilon-greedy.csv" using 1:6 ti "0.5" with lines
