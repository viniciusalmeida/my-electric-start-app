(ns user ^:dev/always
  (:require app.foobar
            hyperfiddle.electric
            hyperfiddle.electric-dom2))

(def electric-main
  (hyperfiddle.electric/boot
    (binding [hyperfiddle.electric-dom2/node js/document.body]
      (app.foobar/Foobar.))))

(defonce reactor nil)

(defn ^:dev/after-load ^:export start! []
  (assert (nil? reactor) "reactor already running")
  (set! reactor (electric-main
                  #(js/console.log "Reactor success:" %)
                  #(js/console.error "Reactor failure:" %))))

(defn ^:dev/before-load stop! []
  (when reactor (reactor))
  (set! reactor nil))