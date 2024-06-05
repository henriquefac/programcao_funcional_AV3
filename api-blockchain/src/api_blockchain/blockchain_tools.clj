(ns api-blockchain.blockchain-tools
  (:require [cheshire.core	:as	json])
  (:import [java.security MessageDigest]
           [java.util Date]))



;;gerar hash a patir de string
(defn sha256 [s]
  (let [digest (MessageDigest/getInstance "SHA-256")]
    (->> (.digest digest (.getBytes s "UTF-8"))
         (map #(format "%02x" %))
         (apply str))))

;;gerar hash a partir de dados
(defn get-hash [index nonce transacao hash_antecessor]
  (sha256 (str index nonce transacao hash_antecessor)))

;;estruturar bloco
(defn bloco [index nonce transacao hash_antecessor hash]
  {:index index
   :nonce nonce
   :transacao transacao
   :hash_anterior hash_antecessor
   :hash hash})

;;minerar até conseguir o nonce
(defn mine [index transacao hash_antecessor]
  (loop [nonce 0]
    (let [hash (get-hash index nonce transacao hash_antecessor)]
      (if (.startsWith hash "0000")
        nonce
        (recur (inc nonce))))))

(defn criar-genesis []
  (let [hash_antecessor "0000000000000000000000000000000000000000000000000000000000000000"
        transacao "bloco genesis"
        index 0
        nonce (mine index transacao hash_antecessor)
        hash (get-hash index nonce transacao hash_antecessor)]
    (bloco index nonce transacao hash_antecessor hash)))
;;inicializar atom
(def block-atom (atom []))

(defn block-chain []
  @block-atom)
;;criar bloco de block chain
;;quando for adicionar um bloco, checar se existe bloco no atom
;;adicionar bloco no atom
(defn chain_block [transacao]
  #_{:clj-kondo/ignore [:missing-else-branch]}
  (if (empty? @block-atom)
    (reset! block-atom (conj [] (criar-genesis))))
  (let [ultimo-bloco (peek @block-atom)
        index-prox (inc (:index ultimo-bloco))
        nonce (mine index-prox transacao (:hash ultimo-bloco))
        hash (get-hash index-prox nonce transacao (:hash ultimo-bloco))
        novo-bloco (bloco index-prox nonce transacao (:hash ultimo-bloco) hash)]

    (swap! block-atom conj novo-bloco)))