(ns t-yencode
  (:require [clojure.java.io :as io]
            [midje.sweet :refer :all]
            [midje.util :refer [expose-testables]]
            [yencode :refer :all]))

(expose-testables yencode)

(fact "ybegin lines parse"
      (parse-ybegin
       "=ybegin line=128 size=123456 name=mybinary.dat") =>
       (contains {:line 128 :size 123456 :name "mybinary.dat"}))

(fact "ybegin lines with a 'part' field parse"
      (parse-ybegin
       "=ybegin part=1 line=128 size=500000 name=mybinary.dat") =>
       (contains {:line 128 :size 500000 :name "mybinary.dat" :part 1}))

(fact "yend lines parse"
      (parse-yend "=yend size=123456") =>
      (contains {:size 123456}))

(fact "yend lines for multipart files parse"
      (parse-yend "=yend size=100000 part=10 pcrc32=12a45c78 crc32=abcdef12 ") =>
      (contains {:size 100000 :part 10 :pcrc32 "12a45c78" :crc32 "abcdef12"}))

(fact "ydecode handles single-part test data"
      (ydecode (slurp (io/resource "test1.dat.yenc") :encoding "ISO-8859-1"))
      =>
      (slurp (io/resource "test1.dat") :encoding "ISO-8859-1"))

(fact "decoding data without a ybegin throws an exception"
      (ydecode "=yend ") => (throws java.io.IOException))

(fact "decoding data without a yend throws an exception"
      (ydecode "=ybegin ") => (throws java.io.IOException))
