(ns api-blockchain.handler
  (:require [compojure.core :refer :all]
            [api-blockchain.auxiliares :refer :all]
            [compojure.route :as route]
            [cheshire.core	:as	json]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json	:refer	[wrap-json-body]]))
(defn	como-json	[conteudo	&	[status]]
  {:status	(or	status	200)
   :headers	{"Content-Type" "application/json; charset=utf-8"}
   :body	(json/generate-string	conteudo)})



(defroutes app-routes
  (GET "/cadeia" [] 
    (como-json (list-chain)))
  (POST "/minerar" requisicao 
    (let [body (:body requisicao)]
     (-> (requerir body)
         (como-json 201))))
  (GET "/transacao" {params :params}
    (let [nun (Integer/parseInt (:index params))]
      (-> (filtrar nun)
          (como-json))))
  (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-body {:keywords? true :bigdecimals? true})))
