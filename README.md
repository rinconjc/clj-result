# README

This library provides a concise and efficient way to manage errors and exceptions that may occur when executing functions. It offers Result values that encapsulate both successful and failed outcomes, inspired by the Rust's Result trait. With this library, you can easily propagate errors and handle them in a functional manner, improving the reliability and maintainability of your code.

## Usage

* Add dependency to **deps.edn**:

```edn
{:deps
    {io.github.rinconjc/clj-result {:git/sha "405bad6"}}
}
```

### Returning a result value from your functions

```clojure
(ns your-namespace.core
    (:require [result/core :as result]))

(defn parse-email [email]
    (if (re-matches #".+@.+" email)
        (result/ok email)
        (result/error "invalid email format")))

```

### Checking the result value when calling your functions

```clojure
(ns your-namespace.core
    (:require [result/core :as result]))

(assert (result/ok? (parse-email "hello@example.org")))
(assert (result/error? (parse-email "hello")))

```

### Converting exceptions to error results

```clojure
(ns your-namespace.core
    (:require [result/core :as result :refer [result-of]]))

(assert (result/error? (result-of (/ 5 0))))

(assert (= 4 (result/ok? (result-of (/ 8 2)))))

```
