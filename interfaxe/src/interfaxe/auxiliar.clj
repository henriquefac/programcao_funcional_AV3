(ns interfaxe.auxiliar
  (:require [cheshire.core :as json]
            [clj-http.client :as client]))

;; Função para concatenar caminho
(defn endereco [rota porta]
  (str "http://localhost:" porta rota))

;; Função para formatar conteúdo como JSON
(defn conteudo-como-json [transacao]
  {:content-type :json
   :body (json/generate-string transacao)
   :throw-exceptions false})

;; Função para criar uma transação
(defn transacao [valor tipo]
  (conteudo-como-json {:valor valor :tipo tipo}))

;; Função para registrar uma transação
(defn registrar [argumentos]
  (let [valor (Integer/parseInt (:valor argumentos))
        tipo (:tipo argumentos)]
    (println (transacao valor tipo))
    (client/post (endereco "/transacoes" 3000) (transacao valor tipo))))


;;função para pegar do financeiro a lista de transacoes
;;path -> /transacao

(defn show []
  (let [lista-transacoes (json/parse-string (:body (client/get (endereco "/transacao" 3000))) true)]
    (doseq [n (:transacoes lista-transacoes)]
      (println n))))


;;mostrar lista de blocos

(defn blocos []
  (let [lista-transacoes (json/parse-string (:body (client/get (endereco "/cadeia" 3001))) true)]
    
    (doseq [transacao (:BlockChain lista-transacoes)]
      (println "------------------------")
      (doseq [chave (keys transacao)]
        (println chave (transacao chave)))))
  )


;;pegar lista do financeiro e colocar o bloco