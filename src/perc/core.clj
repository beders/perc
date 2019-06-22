(ns perc.core
  (:require [clojure.string :as string]
            [clojure.walk :as w]))

(defn get-keyword [kns kn]
  (if kn
    (if (= "" kns)
      (keyword (-> ::_ namespace) kn)
      (keyword kns kn))
    (keyword kns)))

(defn get-val [l index]
  (if-not (= "" index)
    (if (= index "&")
      `~l
      `(nth ~l ~(dec (read-string index))))
    `(first ~l)))

(defn handle [sym el]
  (if (= el (symbol (str "perclocal" sym)))
    (throw (Exception. (str "No nesting for reader tag #%/" sym)))
    (if (string/starts-with? (str el) (str sym))
      (let [[param kns kn] (string/split (str el) #":")
            [_ index] param
            l (symbol (str "perclocal" sym))
            v (get-val l (str index))]
        (if-not (or kns kn)
          `~v
          `(~(get-keyword kns kn) ~v)))
      el)))


(defn % [expr]
  (let []
    `(fn [& ~(symbol "perclocal%")]
       ~(w/postwalk (partial handle "%") expr))))

(defn %% [expr]
  (let []
    `(fn [& ~(symbol "perclocal%%")]
       ~(w/postwalk (partial handle "%%") expr))))

(defn %%% [expr]
  (let []
    `(fn [& ~(symbol "perclocal%%%")]
       ~(w/postwalk (partial handle "%%%") expr))))

(defn $ [expr$]
  (let []
    `(fn [& ~(symbol "perclocal$")]
       ~(w/postwalk (partial handle "$") expr$))))

(defn $$ [expr$$]
  (let []
    `(fn [& ~(symbol "perclocal$$")]
       ~(w/postwalk (partial handle "$$") expr$$))))

(defn $$$ [expr$$$]
  (let []
    `(fn [& ~(symbol "perclocal$$$")]
       ~(w/postwalk (partial handle "$$$") expr$$$))))

(defn ? [expr?]
  `(fn [& ~(symbol "perclocal?")]
     ~(w/postwalk (partial handle "?") expr?)))

(defn ?? [expr??]
  `(fn [& ~(symbol "perclocal??")]
     ~(w/postwalk (partial handle "??") expr??)))

(defn ??? [expr???]
  `(fn [& ~(symbol "perclocal???")]
     ~(w/postwalk (partial handle "???") expr???)))
