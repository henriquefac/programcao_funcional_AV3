(ns api-blockchain.auxiliares
  (:require
   [api-blockchain.blockchain-tools :refer :all]) )


(defn requerir [body]
  (peek (chain_block (select-keys body [:valor :tipo :rotulos]))))

(defn list-chain []
  {:transacoes (block-chain)})



;;filtrar requisicao pelo indice
(defn filtrar [indice]
  (first (filter #(= (int (get % :index)) indice) (block-chain))))
