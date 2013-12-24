# yencode-clj

[yEnc] codec for Clojure. Currently only decoding of single-part files
has been implemented, and naively at that.

[yEnc]:http://www.yenc.org/

## Decoding

There is a single function, `ydecode` that decodes a string of the
correct character set (ISO-8859-1).

### Example

```clojure
(require '[yencode :as y])

(-> (slurp "test.dat.yenc" :encoding "ISO-8859-1")
    (y/ydecode))
```

## Installation

There is no pre-built library yet, as this is a work in progress.
`lein install` it locally for now.

## License

Copyright © 2013 Marc Liberatore

Licensed under the EPL. (See the file epl.html.)
