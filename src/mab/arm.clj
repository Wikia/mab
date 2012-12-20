(ns mab.arm)


(defn create-arm 
  "Create a bandit arm."
  ([count value meta] {:count count
                       :value value
                       :meta meta})
  ([count value]      (create-arm count value {}))
  ([]                 (create-arm 0 0 {})))



(defn arm-count [arm]
  (get arm :count 0))

(defn update-count [arm count]
  (assoc arm :count count))

(defn increment-count [arm]
  (update-in arm [:count] inc))

(defn arm-value [arm]
  (get arm :value 0))

(defn update-value [arm value]
  (assoc arm :value value))

(defn arm-meta [arm]
  (get arm :meta {}))

(defn arm-position [arms arm]
  (.indexOf arms arm))


