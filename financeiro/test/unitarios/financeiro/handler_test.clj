(ns financeiro.handler-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [financeiro.handler :refer :all]
            [cheshire.core	:as	json]
            [financeiro.db	:as	db]
            ))

(facts "Dá	um	'Olá, mundo!'	na	rota	raiz"
       (fact "o	status	da	reposta	é	200"
             (let	[response	(app	(mock/request	:get	"/"))]
               (:status	response)	=>	200))
       (fact "o	texto	do	corpo	é	'Olá, Mundo!'"
             (let	[response	(app	(mock/request	:get	"/"))]
               (:body	response)	=>	"Olá, Mundo!")))

(facts "Saldo inicial é 0"
       (against-background [(json/generate-string {:saldo 0})
                           => "{\"saldo\":0}"
                           (db/saldo) => 0])
       
       (let [response (app (mock/request :get "/saldo"))]
         (fact "o formato é 'application/request'"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")

         (fact "o status resposta é 0"
               (:status response) => 200)
         (fact "o texto do corpo é um JSON cuja chave é saldo e o valor é 0"
               (:body response) => "{\"saldo\":0}")))


(facts "Rota	inválida	não	existe"
       (let [response (app (mock/request :get "/invlid"))]
         (fact "o código de erro é 404"
               (:status response) => 404)
         
         (fact "o texto de corpo é 'Recurso não encontrado"
               (:body response) => "Recurso não encontrado")
         
         ) 
       
       )

(facts "Registra	uma	receita	no	valor	de	10"
							;;	cria	um	mock	para	a	função	db/registrar
       (against-background	(db/registrar	{:valor	10
                                          :tipo	"receita"})
                           =>	{:id	1	:valor	10	:tipo	"receita"})
       (let	[response
             (app	(->	(mock/request	:post	"/transacoes")
																								;;	cria	o	conteúdo	do	POST
                      (mock/json-body	{:valor	10 :tipo	"receita"})))]
         (fact "o	status	da	resposta	é	201"
               (:status	response)	=>	201)
         (fact "o	texto	do	corpo	é	um	JSON
																com	o	conteúdo	enviado	e	um	id"
               (:body	response)	=>
               "{\"id\":1,\"valor\":10,\"tipo\":\"receita\"}")))