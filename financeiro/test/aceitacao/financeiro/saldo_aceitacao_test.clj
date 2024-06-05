(ns financeiro.saldo-aceitacao-test
  (:require  [midje.sweet	:refer	:all]
             [financeiro.auxiliares	:refer	:all]
             [cheshire.core	:as	json]
             [financeiro.handler	:refer	[app]]
             [ring.adapter.jetty :refer [run-jetty]]
             [clj-http.client :as	http]
             [financeiro.db :refer :all]
             [financeiro.db :as db]))


;;move iniciação do servidor para uma etapa de setup

(against-background [(before :facts [(iniciar-servidor porta)
                                     (limpar)])
                     (after :facts (parar-servidor))]
                    (fact "O	saldo	inicial	é	0" :aceitacao
                    		;;	faz	uma	requisição	HTTP	e	verifica	que	o	retorno	é	0
                          (json/parse-string (conteudo "/saldo") true)	=>		{:saldo	0})

                    (fact "O	saldo	é	10	quando	a	única	transação	é	uma	receita	de	10"
                          :aceitacao
                          (http/post	(endereco "/transacoes")
                                     {:content-type	:json
                                      :body	(json/generate-string	{:valor	10
                                                                   :tipo	"receita"})})
                          (json/parse-string	(conteudo "/saldo")	true)	=>	{:saldo	10}))


(against-background [(before :facts [(iniciar-servidor porta)
                                     (limpar)])
                     (after :facts (parar-servidor))]

                    (fact "O saldo é 1000 quando criamos duas receitas de 2000
                                               e uma despesa de 3000" :aceitacao

                          (http/post (endereco "/transacoes") (receita 2000))
                          (http/post (endereco "/transacoes") (receita 2000))
                          (http/post (endereco "/transacoes") (despesa 3000))
                          (json/parse-string	(conteudo "/saldo")	true)	=>	{:saldo	1000}))


(against-background [(before :facts (iniciar-servidor porta))
                     (after :facts (parar-servidor))] 
(fact "Rejeita	uma	transação	sem	valor"	:aceitacao
      (let	[resposta	(http/post	(endereco "/transacoes")
                                (conteudo-como-json	{:tipo
                                                     "receita"}))]
        (:status	resposta)	=>	422))

(fact "Rejeita uma transacao com valor negativo" :aceitacao
      (let [resposta (http/post (endereco "/transacoes")
                                (receita -2000))]
        (:status resposta) => 422))

(fact "Rejeita transacao com valor que não é um número" :aceitacao
      (let [resposta (http/post (endereco "/transacoes")
                                (receita "mi"))]
        (:status resposta) => 422))

(fact "Rejeita transacao sem tipo" :aceitacao
      (let  [resposta (http/post (endereco "/transacoes")
                                 (conteudo-como-json {:valor 70}))]
        (:status resposta) => 422))

(fact "Rejeita uma transacao com tipo desconhecido" :aceitacao
      (let [resposta (http/post (endereco "/transacoes")
                                (conteudo-como-json {:valor 100
                                                     :tipo "desconhecido"}))]
        (:status resposta) => 422)))


