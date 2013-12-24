(defproject yencode-clj "0.0.1-SNAPSHOT"
  :description "a yenc decoder in Clojure"
  :url "http://github.com/marcliberatore/yencode-clj"
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles
  {:dev {:resource-paths ["test-resources"]
         :dependencies [[midje "1.5.1"]
                        [lein-midje "3.1.3"]]}})
