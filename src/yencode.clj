(ns yencode)

(def ^:private ybegin-re #"^=ybegin ")

(defn- ybegin? [s]
  (re-find ybegin-re s))

(def ^:private ybegin-part-re #"part=(\d*) ")
(def ^:private ybegin-line-re #"line=(\d*) ")
(def ^:private ybegin-size-re #"size=(\d*) ")
(def ^:private ybegin-name-re #"name=(.*)$")

(defn- ->long [^String s]
  (when s
    (Long/valueOf s)))

(defn- ^{:testable true} parse-ybegin [s]
  (let [[_ part] (re-find ybegin-part-re s)
        [_ line] (re-find ybegin-line-re s)
        [_ size] (re-find ybegin-size-re s)
        [_ name] (re-find ybegin-name-re s)]
    {:line (->long line)
     :size (->long size)
     :part (->long part)
     :name name}))

(def ^:private yend-size-re #" size=(\d*)")
(def ^:private yend-part-re #" part=(\d*)")
(def ^:private yend-pcrc32-re #" pcrc32=(\w*)")
(def ^:private yend-crc32-re #" crc32=(\w*)")

(defn- ^{:testable true} parse-yend [s]
  (let [[_ size] (re-find yend-size-re s)
        [_ part] (re-find yend-part-re s)
        [_ pcrc32] (re-find yend-pcrc32-re s)
        [_ crc32] (re-find yend-crc32-re s)]
    {:size (->long size)
     :part (->long part)
     :pcrc32 pcrc32
     :crc32 crc32}))

(def ^:private yend-re #"^=yend ")

(defn- yend? [s]
  (re-find yend-re s))

(defn- decode [s]
  (loop [remaining s
         critical? false
         result (transient [])]
    (if (seq remaining)
      (let [c (int (first remaining))]
        (if (= 61 c)
          (recur (rest remaining) true result)
          (do
            (if critical?
              (recur (rest remaining) false (conj! result
                                                   (char (mod (- c 64 42) 256))))
              (recur (rest remaining) false (conj! result
                                                   (char (mod (- c 42) 256)))))
            )))
      (apply str (persistent! result)))))

(defn ydecode
  "Decodes a string representing yencoded data, possibly with
  extraneous data outside the ybegin/yend block. Assumes that the
  string was read using the correct charset (ISO-8859-1)."
  [s]
  (let [lines (clojure.string/split-lines s)
        lines (drop-while (complement ybegin?) lines)
        [[ybegin-line] lines] (split-with ybegin? lines)
        [lines yend-lines] (split-with (complement yend?) lines)
        body (apply str lines)
        yend-line (first yend-lines)]
    (when-not ybegin-line
      (throw (java.io.IOException. "missing =ybegin")))
    (when-not yend-line
      (throw (java.io.IOException. "missing =yend")))
    (decode body)))
