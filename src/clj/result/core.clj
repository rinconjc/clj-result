(ns result.core
  (:import
   (clojure.lang ISeq Util$EquivPred)))

(deftype ^:private Result [value]
  ISeq
  (seq [this]
    (when-let [val (:ok value)]
      (list val)))
  (first [this]
    (when-let [ok-val (:ok value)]
      ok-val))
  Util$EquivPred
  (equiv [this other]
    (and (instance? Result other)
         (= (.-value this) (.-value other)))))

(defn ok
  "Creates a Result object representing a successful computation with the given value.

Usage: (ok value)

Returns: A Result object containing the given value wrapped in a `:ok` key.

Examples:
  (ok 5)      ; Returns a Result object with {:ok 5}
  (ok \"foo\")  ; Returns a Result object with {:ok \"foo\"}
"
  [x]
  (->Result {:ok x}))

(defn error
  "Creates a Result object representing a failed computation with the given error.

Usage: (error e)

Returns: A Result object containing the given error wrapped in an `:error` key.

Examples:
  (error \"An error occurred\") ; Returns a Result object with {:error \"An error occurred\"}
  (error (Exception.))         ; Returns a Result object with {:error #<Exception java.lang.Exception>}
"
  [e]
  (->Result {:error e}))

(defn ok?
  "Checks whether the given Result object represents a successful computation.

Usage: (ok? result)

Returns: The value of the Result object wrapped in a `:ok` key, or nil if the Result object represents a failed computation.

Examples:
  (def res (ok 5))
  (ok? res)    ; Returns 5
  (ok? (error \"An error occurred\")) ; Returns nil
"
  [result]
  (:ok (.-value result)))

(defn error?
  "Checks whether the given Result object represents a failed computation.

Usage: (error? result)

Returns: The value of the Result object wrapped in an `:error` key, or nil if the Result object represents a successful computation.

Examples:
  (def res (error \"An error occurred\"))
  (error? res)    ; Returns \"An error occurred\"
  (error? (ok 5)) ; Returns nil
"
  [result]
  (:error (.-value result)))

(defn ok-or
  "Returns the value of a successful computation represented by the given Result object, or a default value if the computation failed.

Usage: (ok-or result default)

Returns: The value of the Result object wrapped in an `:ok` key, or the default value if the Result object represents a failed computation.

Examples:
  (def res (ok 5))
  (ok-or res 0)     ; Returns 5
  (ok-or (error \" An error occurred \") 0) ; Returns 0
"
  [result default]
  (or (ok? result) default))

(defn ok-or-else
  "Returns the value of the result if it is Ok, or else the result of calling `else-fn` with the error value.
   If the result is Ok, the function returns its value. If the result is an Error, `else-fn` is called with the error value as its argument, and its return value is returned instead.

   Parameters:
   - result: a Result value
   - else-fn: a function that will be called with the error value if `result` is an Error.

   Returns:
   - The value of the result if it is Ok, or the result of calling `else-fn` with the error value otherwise.
  "
  [result else-fn]
  (or (ok? result) (else-fn (error? result))))

(defmacro result-of
  "Executes the given body of expressions and wraps the result in a Result object. If an exception occurs during the execution, it returns the exception wrapped in an error Result object.

Usage: (result-of expr1 expr2 ...)

Returns: A Result object containing the result of the expression or the error.

Examples:
  (result-of (/ 10 2))   ; Returns a Result object (ok 5)
(result-of (/ 10 0))   ; Returns a Result object (error ArithmeticException)
"
  [& body]
  `(try
     (ok (do ~@body))
     (catch Throwable e#
       (error e#))))

(defn map-result
  "Maps a function f over the value of a Result, returning a new Result with the result of the function if the original Result was a Ok, or the error value if it was an Error."
  [result f]
  (if-let [ok-val (ok? result)]
    (try
      (let [r (f ok-val)]
        (if (instance? Result r)
          r
          (ok r)))
      (catch Throwable e (error e)))
    result))

