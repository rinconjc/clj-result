(ns core-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is]]
   [result.core :refer [error error? map-result ok ok-or ok-or-else ok?
                        result-of]])
  (:import
   java.lang.ArithmeticException))

;; Test creating and accessing Ok results
(deftest ok-test
  (is (ok? (ok 42)))
  (is (= (ok? (ok 42)) 42))
  (is (= (ok-or (ok 42) 0) 42))
  (is (= (ok-or-else (ok 42) (fn [] 0)) 42)))

;; Test creating and accessing Error results
(deftest error-test
  (is (error? (error "Oops!")))
  (is (= (error? (error "Oops!")) "Oops!"))
  (is (= (ok-or (error "Oops!") 0) 0))
  (is (= (ok-or-else (error "Oops!") (fn [_] 0)) 0)))

;; Test using try-result to catch exceptions
(deftest try-result-test
  (is (= (result-of (/ 10 2)) (ok 5)))
  (let [result  (result-of (/ 10 0))]
    (is (= ArithmeticException (type (error? result))))))

;; Test using Result values in a function
(defn divide [x y]
  (result-of (/ x y)))

(deftest function-test
  (is (= (divide 10 2) (ok 5)))
  (is (= ArithmeticException (type (error? (divide 10 0))))))

(deftest map-result-test
  ;; Test mapping a function over an Ok result
  (is (= (map-result (ok 42) inc) (ok 43)))
  (is (= (map-result (ok "foo") str/reverse) (ok "oof")))

  ;; Test mapping a function over an Error result
  (is (= (map-result (error "Oops!") inc) (error "Oops!")))
  (is (= (map-result (error "Oops!") str/reverse) (error "Oops!")))

  ;; Test mapping a function that throws an exception
  (is (error? (map-result (ok 42) #(throw (Exception. "Oops!")))))
  (is (= (map-result (error "Oops!") #(throw (Exception. "Oops!"))) (error "Oops!"))))

;; Test that the original Result is not modified
(deftest map-result-original-test
  (let [result (ok 42)]
    (is (= (map-result result inc) (ok 43)))
    (is (= result (ok 42)))))

;; Test that mapping a pure function returns a pure value
(deftest map-result-pure-test
  (let [result (ok 42)]
    (is (= (map-result result inc) (map-result result inc)))
    (is (= (map-result result #(str %)) (map-result result #(str %))))))

;; Test mapping a function that returns a Result
(deftest map-result-nested-test
  (let [result (ok 42)]
    (is (= (map-result result (fn [x] (ok (* x 2)))) (ok 84)))
    (is (= (map-result result (fn [_] (error "Oops!"))) (error "Oops!")))
    (is (= (map-result (error "Oops!") (fn [x] (ok (* x 2)))) (error "Oops!")))
    (is (= (map-result (error "Oops!") (fn [_] (error "Double oops!"))) (error "Oops!")))))
