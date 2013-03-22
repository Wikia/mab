(defproject com.huddler/mab "0.2.2"
  :description "Multi-armed bandit algorithms and testing."
  :url "http://www.huddler.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :jvm-opts ["-Xmx2g" "-Xms2g" "-server" "-XX:+UseConcMarkSweepGC" "-XX:+CMSIncrementalMode"] 
  :repl-options { :init-ns mab.repl-helper }
  :profiles {:dev {:plugins [[lein-midje "3.0.0"]]
                   :dependencies []}}
  :plugins [[codox "0.6.4"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.clojure/tools.cli "0.2.2"]
                 [org.clojure/tools.namespace "0.2.2"]
                 [midje "1.5.0"]
                 [environ "0.2.1"]])
