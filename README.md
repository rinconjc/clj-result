# README

This Clojure library provides a concise and efficient way to manage errors and exceptions that may occur when executing functions. It offers Result values that encapsulate both successful and failed outcomes, inspired by the Rust programming language's Result trait. With this library, you can easily propagate errors and handle them in a functional manner, improving the reliability and maintainability of your code.

## Usage

### Returning a result value from your functions

```clojure
(ns your-namespace.core
    (:require [clj-result/core :as result]))

(defn parse-email [email]
    (if (re-matches #".+@.+" email)
        (result/ok email)
        (result/error "invalid email format")))

```

### Checking the result value when calling your functions

```clojure
(ns your-namespace.core
    (:require [clj-result/core :as result]))

(let [result  (parse-email "hello@example.org")]
    (assert (result/ok? result))
    (assert (nil? (result/error? result))))

```

### Converting exceptions to error results

```clojure
(ns your-namespace.core
    (:require [clj-result/core :as result :refer [try-result]]))

(assert (result/error? (try-result (/ 5 0))))

(assert (= 4 (result/ok? (try-result (/ 8 2)))))

```
