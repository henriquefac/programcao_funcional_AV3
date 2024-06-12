(ns interface-final.auxiliar
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [clojure.string :as str]))


;; Função para concatenar caminho
(defn endereco [rota porta]
  (str "http://localhost:" porta rota))

;; Função para formatar conteúdo como JSON
(defn conteudo-como-json [transacao]
  {:content-type :json
   :body (json/generate-string transacao)
   :throw-exceptions false})

;; Função para criar uma transação
(defn criar-transacao [valor tipo]
  (conteudo-como-json {:valor valor :tipo tipo}))

(defn criar-transacao-filtro [valor tipo filtros]
  (conteudo-como-json {:valor valor :tipo tipo :filtros filtros}))


;;primeira coisa, mostrar o financeiro



(defn show []
  (let [lista-transacoes (json/parse-string (:body (client/get (endereco "/transacao" 3000))) true)]
    (doseq [n (:transacoes lista-transacoes)]
      (println "----------------------")
      (doseq [chave (keys n)]
        (println chave (n chave)))
      ))
  
  )


;;mostrar lista de blocos

(defn blocos []
  (let [lista-transacoes (json/parse-string (:body (client/get (endereco "/cadeia" 3001))) true)]
    (if (= (count (:BlockChain lista-transacoes)) 0)
      (println "Sem blocos registrados")
    (doseq [transacao (:BlockChain lista-transacoes)]
      (println "------------------------")
      (doseq [chave (keys transacao)]
        (println chave (transacao chave))))))
  )


;;pegar lista do financeiro e colocar o bloco
;;vai requisitar de /transacao do financeiro
;;a listaa de transacoes vai ser passada como argumento para /minerar de blockchain
(defn registrar-todos []
  (let [lista-transacoes (json/parse-string (:body (client/get (endereco "/transacao" 3000))) true)] 
    (client/post (endereco "/minerar" 3001) (conteudo-como-json lista-transacoes))
    (println "Bloco registrado"))
  
  )


(defn define-tipo [tipo]
  (cond
    (= tipo "r") "receita"
    (= tipo "d") "despesa"))


;;saber se contem string vazia
(defn contem-string-vazia? [vetor]
  (some #(= "" %) vetor))



;; Função para registrar uma transação
(defn transacao [argumentos]
  (let [valor (Integer/parseInt (:valor argumentos))
        tipo (define-tipo (:tipo argumentos))
        filtros (str/split (:filtros argumentos) #",")] 
    (cond
      (contem-string-vazia? filtros) (client/post (endereco "/transacoes" 3000) (criar-transacao valor tipo)) 
      :else (client/post (endereco "/transacoes" 3000) (criar-transacao-filtro valor tipo filtros))
      )
    ))


(defn submenu []
  (let [valor (do (println "Valor da transacao: ") (read-line))
        tipo (do (println "Tipo de transacao: ") (read-line))
        filtros (do (println "Filtros: ") (read-line))]
    (println "Transacao feita")
    (transacao {:valor valor
                :tipo tipo
                :filtros filtros})
    ))