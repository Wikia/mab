(defproject com.huddler/mab "0.1.0-SNAPSHOT"
  :description "Multi-armed bandit algorithms and testing."
  :url "http://www.huddler.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :jvm-opts ["-Xmx1g" "-server"] 
  :repl-options { :init-ns mab.repl-helper }
  :profiles {:dev {:plugins [[lein-midje "2.0.3"]]}}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [midje "1.4.0"]
                 [org.clojure/data.csv "0.1.2"]
                 [clojure-csv/clojure-csv "2.0.0-alpha2"]])
