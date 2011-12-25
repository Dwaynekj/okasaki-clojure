(ns ml.datatype-tests
    (:use ml.datatype)
    (:use clojure.test))

(defmacro with-private-fns [[ns fns] & tests]
  "Refers private fns from ns and runs tests in context."
  `(let ~(reduce #(conj %1 %2 `(ns-resolve '~ns '~%2)) [] fns)
       ~@tests))

(defdatatype ::type expr (ctor arg1 arg2))

(deftest empty-constructor-returns-keyword-in-current-namespace
    (is (= ::expr expr)))

(deftest non-empty-constructor-returns-vector
    (is (= [::ctor 'x 'y] (ctor 'x 'y))))

(deftest constructors-have-the-right-metadata
    (is (= ::type (:ml.datatype/datatype (meta #'expr))))
    (is (= ::type (:ml.datatype/datatype (meta #'ctor)))))

(deftest caseof-works-properly
    (let [test #(caseof [%] [expr] 0 [[ctor x y]] (+ x y))]
	(is (= 0 (test expr)))
	(is (= 3 (test (ctor 1 2))))))

(deflazy ::lazy lexpr (lctor arg1 arg2))

(deftest empty-constructor-returns-delayed-keyword-in-current-namespace
    (is (delay? lexpr))
    (is (= ::lexpr (force lexpr))))

(deftest non-empty-constructor-returns-delayed-vector
    (is (delay? (lctor 'x 'y)))
    (is (= [::lctor 'x 'y] (force (lctor 'x 'y)))))

(deftest caseof-works-properly-with-lazy
    (let [test #(caseof [%] [lexpr] 0 [[lctor x y]] (+ x y))]
	(is (= 0 (test lexpr)))
	(is (= 3 (test (lctor 1 2))))))

(with-private-fns [ml.datatype [lazy?]]
    (deftest we-can-detect-lazy-patterns
        (is (lazy? `lexpr))
        (is (lazy? `[lctor x y]))))