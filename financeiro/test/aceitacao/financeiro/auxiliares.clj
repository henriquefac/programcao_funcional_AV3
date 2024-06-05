(ns	financeiro.auxiliares
  (:require	[ring.adapter.jetty	:refer	[run-jetty]]
            [financeiro.handler	:refer	[app]]
            [clj-http.client	:as	http]
            [cheshire.core	:as	json]))

(def servidor (atom nil))

(defn iniciar-servidor [porta]
  (swap! servidor
         (fn [_] (run-jetty app {:port porta :join? false}))))

(defn parar-servidor [] 
  (.stop @servidor))

;;definir porta padrao
(def porta 3001)

;;concatenar caminho
(defn endereco [rota] (str "http://localhost:" porta rota))

;;abstrair uso de http/client concatenando funções
(def requisicao-para (comp http/get endereco))

;;pegar conteudo do body após reuisicao
(defn conteudo [rota] (:body (requisicao-para rota)))

(defn conteudo-como-json [transacao]
  {:content-type :json
   :body (json/generate-string transacao)
   :throw-exceptions false})

(defn despesa [valor]
  (conteudo-como-json {:valor valor :tipo "despesa"}))

(defn receita [valor]
  (conteudo-como-json {:valor valor :tipo "receita"}))