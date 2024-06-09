(ns interfaxe.core
  (:require [interfaxe.auxiliar :refer :all]
            [clojure.tools.cli :refer [parse-opts]]
            [cheshire.core :as json])
  (:gen-class))

;; Definição das opções de linha de comando
(def cli-options
  [["-c" "--comando COMANDO" "O comando a ser executado"]
   ["-v" "--valor VALOR" "valor da transacao" :default ""]
   ["-t" "--tipo TIPO" "tipo de transacao" :default ""]
   ["-f" "--filtros LISTA" "A lista de itens (separados por vírgulas)" :default ""]])

;; Função para identificar o comando e direcionar para o resultado adequado
(defn chamar-funcao [opcoes]
  (let [comando (:comando opcoes)
        comandos ["transacoes" "show" "registrar" "block"]]
    (cond
      ;; Registrar todas as transacoes do financeiro para o blockchain
      (= comando (get comandos 0)) (println (:body (registrar-todos)))
      ;; Mostrar todas as transacoes
      (= comando (get comandos 1)) (show)
      ;; Registrar transacao individual
      (= comando (get comandos 2)) (println (:body (registrar opcoes)))
      ;; Mostrar lista de blocos
      (= comando (get comandos 3)) (blocos)
      :else (println (str "Comando invalido; Opcoes: " comandos)))))

;; Função principal
(defn -main
  "API'S,.... DEVO CONSUMIR! HAHAHAAHAHAHAHAHAHAH"
  [& args]
  (let [{:keys [options]} (parse-opts args cli-options)]
    (chamar-funcao options)
    ))
