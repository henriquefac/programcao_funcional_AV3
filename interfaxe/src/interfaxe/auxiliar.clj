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
