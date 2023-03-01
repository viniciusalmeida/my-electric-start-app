(ns app.foobar
  #?(:cljs (:require-macros app.foobar))
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(e/defn Foobar []
  (e/client
    (dom/link (dom/props {:rel :stylesheet :href "/assets/app.css"}))
    (dom/h1
      (dom/text "Alô mamãe!"))))