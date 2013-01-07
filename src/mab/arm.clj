(ns mab.arm)

(defn create-arm-uuid
  []
  (java.util.UUID/randomUUID))


(defn create-arm 
  "Create a bandit arm."
  ([count value uuid meta] {:count count
                       :value value
                       :uuid uuid
                       :meta meta})
  ([count value]      (create-arm count value (create-arm-uuid) nil))
  ([count value uuid] (create-arm count value uuid nil))
  ([]                 (create-arm 0 0 (create-arm-uuid) nil)))


(defn initialize-arm-vector 
  "Create a vector af initialized arms."
  ([n] (vec (repeatedly n  #(create-arm 0 0))))
  ([counts values] (vec 
                       (map #(apply create-arm %)
                            (partition 2 (interleave counts values)))))
  ([counts values uuids] (vec 
                       (map #(apply create-arm %)
                            (partition 3 (interleave counts values uuids))))))


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

(defn arm-uuid [arm]
  (get arm :uuid))

(defn arm-position [arms arm]
  (.indexOf arms arm))




