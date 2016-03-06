repl:
  clojure.repl/pst

numbers:
   create:
     1 int
     1.5 long
   arithmetic:
     + - * / max min inc dec
   compare:
     = < > <= >=
   random:
     rand rand-int

strings:
   create:
     "string" str
   use:
     count join split replace
   test:
     blank? includes?

  regex:
    #"pattern"
    re-find re-matches

keyword:
 keyword

literals:
  true false nil


collections:
  count
  test:
    empty? every? some

  lists:
    create:
      ()
    examine:
      first nth
    change:
      conj rest

  vectors:
    create:
      [] vec
    examine:
      (some-vec idx) get
    change:
      assoc assoc-in conj

  sets:
    create:
      #{} set
    examine:
      (some-set item) get contains?
    change:
      conj disj
    ops:
      union difference intersection
    test:
      subset? superset?

   maps:
     create:
       {} group-by hash
     examine:
       (some-map k) (k some-map) get get-in contains? keys vals
     change:
       assoc assoc-in dissoc merge select-keys update-in




= not= not
true? false? nil? some?

sequences:
  shorter:

  longer:

  process:

  search:
    filter remove

  todo:
    reduce map

    apply
    some
    for doseq
    concat
    first last
    rand-nth
    rest drop
    shuffle
    sort sort-by
    reverse

misc:
  def defonce
  let if-let when-let
  if when if-not when-not cond case
  #\_() comment ; comment
  ns
  and or
  println


functions:
  defn
  fn
  -> ->>

atoms:
  atom swap! reset! deref

js interop:
  . .. doto
  clj->js js->clj #js { }

conventions:
  foo?
  foo!
  _
