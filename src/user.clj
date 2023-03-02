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

(defn wrap-electric [next-handler]
  (fn [ring-request]
    (if (jetty/ws-upgrade-request? ring-request)
      (jetty/ws-upgrade-response (adapter/electric-ws-adapter (partial adapter/electric-ws-message-handler ring-request)))
      (next-handler ring-request))))

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
                                       :middleware [no-cache
                                                    middleware.muuntaja/format-middleware
                                                    wrap-electric]})
                   {:port       3001
                    :join?      false}))

(comment
  (def server (main))
  (.stop server))