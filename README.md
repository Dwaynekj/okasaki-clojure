# okasaki-clojure

An implementation of some data structures described in Okasaki's book "Purely 
Functional Data Structures".

I'm trying to follow _almost directly_ the ML implementations using some sugar on David Nolen's [core.match]
(https://github.com/clojure/core.match) library.

## Eager datatypes

These types are evaluated eagerly (as is ML, as far as I know).

For instance, a _datatype_ for unbalanced binary search trees can be defined as:

    (defdatatype
        ::UnbalancedBST
        Empty        
        (Node a x b)) 

and _functions_ using pattern matching over these trees as:

    (defun insert [x t]
        [x Empty] 
            (Node Empty x Empty)
        [x ([Node a y b] :as s)]
            (cond 
                (< x y) (Node (insert x a) y b)
                (< y x) (Node a y (insert x b))
                :else   s))

    (defun member [x t]
        [_ Empty]
            false
        [x [Node a y b]]
            (cond
                (< x y) (recur x a)
                (< y x) (recur x b)
                :else   true))

## Lazy datatypes

These types are evaluated lazyly. For instance, you can define a streas as:

    (deflazy
        ::Streams
        Nil
        (Cons elem stream))

and then, a function that returns the infinite stream of naturals

    (defn nats
          ([]  (nats 0))
          ([n] (Cons n (nats (inc n)))))

## Known problems

When defining defuns the same symbol cannot appear both in the argument list and in the patterns. For instance:

     user> (defdatatype ::Dummy (Pair x y))
     #'user/Pair
     user> (defun one [p] [[Pair p _]] p)
     #'user/one
     user> (one (Pair 1 2))
     nil

does not work (we use p both in ths arguments list and in the pattern). But

     user> (defun one [pp] [[Pair p _]] p)
     #'user/one
     user> (one (Pair 1 2))
     1

works ok.

#### (c) Juan Manuel Gimeno Illa
