(ns	financeiro.db)
;;	o	átomo	será	usado	como	banco	de	dados
(def	registros
  (atom	[]))

(defn	transacoes	[]
  @registros)

(defn	limpar	[]
  (reset!	registros	[]))


;;	armazena	uma	transação	no	átomo
;;	e	retorna	a	trasação	argumento	com	a	posição	dela	na	coleção
(defn	registrar	[transacao]
  (let	[colecao-atualizada	(swap!	registros	conj	transacao)]
    (merge	transacao	{:id	(count	colecao-atualizada)})))


;;calcular despesa e  receita a partir da transação
(defn- despesa? [transacao]
  (= (:tipo transacao) "despesa"))

(defn- calcular [acumulado transacao]
  (let [valor (:valor transacao)]
    (if (despesa? transacao)
      (- acumulado valor)
      (+ acumulado valor))))

(defn	transacoes-do-tipo	[tipo]
  (filter	#(=	tipo	(:tipo	%))	(transacoes)))


(defn	transacoes-com-filtro	[filtros]
  (let [rotulos (->> (:rotulos filtros)
                     (conj [])
                     (flatten)
                     (set))]
    (filter #(some rotulos (:rotulos %)) (transacoes))))


(defn saldo []
  (reduce calcular 0 @registros))