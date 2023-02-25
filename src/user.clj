(ns user
  (:require hyperfiddle.electric-jetty-server
            shadow.cljs.devtools.api
            shadow.cljs.devtools.server))

(def electric-server-config
  {:host "0.0.0.0", :port 8080, :resources-path "resources/public"})

(defn main [& args]
  (println "Starting Electric compiler and server...")
  (shadow.cljs.devtools.server/start!) ; serves index.html as well
  (shadow.cljs.devtools.api/watch :dev) ; depends on shadow server
  (def server (hyperfiddle.electric-jetty-server/start-server! electric-server-config))
  (comment (.stop server)))