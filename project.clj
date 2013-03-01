(defproject com.huddler/mab "0.2.1-SNAPSHOT"
  :description "Multi-armed bandit algorithms and testing."
  :url "http://www.huddler.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :jvm-opts ["-Xmx2g" "-Xms2g" "-server" "-XX:+UseConcMarkSweepGC" "-XX:+CMSIncrementalMode"] 
  :repl-options { :init-ns mab.repl-helper }
  :profiles {:dev {:plugins [[lein-midje "2.0.3"]]
                   :dependencies [[lein-midje-lazytest "0.1.0"] 
                                  [lazytest "1.2.3"]]}}
  :plugins [[codox "0.6.4"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.clojure/tools.cli "0.2.2"]
                 [environ "0.2.1"]
                 [midje "1.4.0"]])
