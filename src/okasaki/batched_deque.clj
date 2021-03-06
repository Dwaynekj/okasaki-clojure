(ns okasaki.batched-deque
    (:refer-clojure :exclude [cons empty? last split-at])
    (:use datatype.core
          okasaki.list))

;; Solution to exercise 5.1

(defdatatype
    ::Deque
    (Quad cfront front crear rear))

(def Empty (->Quad 0 Nil 0 Nil))

(defun empty?
    ;;[queue]
    [[Quad 0 Nil 0 Nil]] true
    :else                false)

(defn- split-and-reverse-bottom
    [n l]
    (let [ntop (quot n 2)
          nbottom (- n ntop)
          [top rbottom] (split-at ntop l)
          bottom (rev rbottom)]
        [ntop top nbottom bottom]))

(defun ^:private checkf
    ;;[queue]
    [([Quad 0 Nil cr r] :as q)] (if (> cr 1)
                            (let [[cnr nr cnf nf] (split-and-reverse-bottom cr r)]
                                (->Quad cnf nf cnr nr))
                            q)
    [([Quad cf f 0 Nil] :as q)] (if (> cf 1)
                            (let [[cnf nf cnr nr] (split-and-reverse-bottom cf f)]
                                (->Quad cnf nf cnr nr))
                            q)
    [q] q)

(defun cons
    ;;[elem deque]
    [x [Quad cf f cr r]] (checkf (->Quad (inc cf) (->Cons x f) cr r)))

(defun head
    ;;[queue]
    [[Quad 0 Nil 0 Nil]]          (throw (IllegalStateException. "Attempting head of an empty queue"))
    [[Quad _ [Cons h _] _ _]]     h
    [[Quad 0 Nil 1 [Cons h Nil]]] h)

(defun tail
    ;;[queue]
    [[Quad 0 Nil 0 Nil]]        (throw (IllegalStateException. "Attempting tail of an empty queue"))
    [[Quad c [Cons h t] cr r]]  (checkf (->Quad (dec c) t cr r))
    [[Quad 0 Nil c [Cons h t]]] (checkf (->Quad 0 Nil (dec c) t)))

(defun snoc
    ;;[elem queue]
    [x [Quad cf f cr r]] (checkf (->Quad cf f (inc cr) (->Cons x r))))

(defun last
    ;;[queue]
    [[Quad 0 Nil 0 Nil]]          (throw (IllegalStateException. "Attempting last of an empty queue"))
    [[Quad _ _ _ [Cons h t]]]     h
    [[Quad 1 [Cons h Nil] 0 Nil]] h)

(defun init
    ;;[queue]
    [[Quad 0 Nil 0 Nil]]        (throw (IllegalStateException. "Attempting init of an empty queue"))
    [[Quad cf f cr [Cons h t]]] (checkf (->Quad cf f (dec cr) t))
    [[Quad c [Cons h t] 0 Nil]] (checkf (->Quad (dec c) t 0 Nil)))
