(ns user
  (:require hyperfiddle.electric-jetty-server
            shadow.cljs.devtools.api
            shadow.cljs.devtools.server
            [ring.adapter.jetty9 :as jetty]
            [reitit.ring :as ring]
            [ring.util.response :as resp]
            [reitit.ring.middleware.muuntaja :as middleware.muuntaja]
            [muuntaja.core :as muutanja]
            [hyperfiddle.electric-jetty-adapter :as adapter]))

(defn no-cache [next-handler]
  (fn [ring-req]
    (assoc-in (next-handler ring-req) [:headers "Cache-Control"] "No-Store")))

(def router
  (ring/router
    [["/" {:get (fn [_] (resp/resource-response "index.html" {:root "public"}))}]
     ["/assets/*" (ring/create-resource-handler {:root "public"})]]))

(defn main [& args]
  (println "Starting Electric compiler and server...")
  (shadow.cljs.devtools.server/start!) ; serves index.html as well
  (shadow.cljs.devtools.api/watch :dev) ; depends on shadow server
  (jetty/run-jetty (ring/ring-handler router
                                      (fn [_] {:status 404 :body "Not found"})
                                      {:muuntaja   muutanja/instance
                                       :middleware [no-cache middleware.muuntaja/format-middleware]})
                   {:port       3001
                    :join?      false
                    :websockets {"/" (fn [ring-req]
                                       (adapter/electric-ws-adapter
                                         (partial adapter/electric-ws-message-handler
                                                  ring-req)))}}))

(comment
  (def server (main))
  (.stop server))