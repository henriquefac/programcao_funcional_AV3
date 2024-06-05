(ns financeiro.filtros-aceitacao-tets
  (:require  [midje.sweet	:refer	:all]
             [financeiro.auxiliares	:refer	:all]
             [cheshire.core	:as	json]
             [financeiro.handler	:refer	[app]]
             [ring.adapter.jetty :refer [run-jetty]]
             [clj-http.client :as	http]
             [financeiro.db :refer :all]
             ))


(def	transacoes-aleatorias
  '({:valor	7.0M	:tipo	"despesa"
     :rotulos	["sorvete"	"entretenimento"]}
    {:valor	88.0M	:tipo	"despesa"
     :rotulos	["livro"	"educação"]}
    {:valor	106.0M	:tipo	"despesa"
     :rotulos	["curso"	"educação"]}
    {:valor	8000.0M	:tipo	"receita"
     :rotulos	["salário"]}))

(against-background
 [(before :facts [(iniciar-servidor porta)
                  (limpar)
                  (doseq [transacao transacoes-aleatorias]
                    (registrar transacao))])
  (after :facts [(limpar)
                 (parar-servidor)])]
 
 (fact "Existem 3 despesas" :aceitacao
       (count (:transacoes (json/parse-string
                            (conteudo "/despesas") true))) => 3)
 
 (fact "Existe 1 receita" :aceitacao
       (count (:transacoes (json/parse-string
                            (conteudo "/receitas") true))) => 1)
 
 (fact "Existem 4 transacoes" :aceitacao
       (count (:transacoes (json/parse-string
                            (conteudo "/transacao") true))) => 4)
 
 (fact	"Existe	1	receita	com	rótulo	'salário'" :aceitacao
       (count	(:transacoes	(json/parse-string
                            (conteudo	"/transacoes?rotulos=salário")	true)))	=>	1)
 
 (fact	"Existem	2	despesas	com	rótulo	'livro'	ou	'curso'" :aceitacao
       (count	(:transacoes	(json/parse-string
                            (conteudo	"/transacoes?rotulos=livro&rotulos=curso")
                            true)))	=>	2)

 (fact	"Existem	2	despesas	com	rótulo	'educação'"
         (count	(:transacoes	(json/parse-string
                              (conteudo	"/transacoes?rotulos=educação")	true)))	=>	2)
 )
 
 
