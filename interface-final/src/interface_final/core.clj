(ns interface-final.core
  (:require [interface-final.auxiliar :refer :all]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as str])
  (:gen-class))

;; Função para limpar o terminal
(defn clear-screen []
  (if (.contains (System/getProperty "os.name") "Windows")
    (-> (sh "cmd" "/c" "cls") :out println)
    (-> (sh "clear") :out println)))


;;gerar menu de opções para o usuário
(defn menu []
  (println "1. Registrar transacao na Block Chain")
  (println "2. Mostrar transacao do gerenciador financeiro")
  (println "3. Criar transacao")
  (println "4. Mostrar blocos da Block Chain")
  (println "5. Sair")
  (print "Escolha uma opcao: ")
  (flush))

(defn opcoes [numero]
  (let [comando (Integer/parseInt numero)]
    (cond
      ;;regirtrar tudo
      (= comando 1)(registrar-todos)
      ;;Mostrar transacao
      (= comando 2) (show)
      ;;Criar transacao
      (= comando 3) (submenu)
      ;;Mostrar Blocos
      (= comando 4) (blocos)
      ;;sair do sistema
      (= comando 5) (println "Saindo do sistema...")
      :else (println "Opção inválida!"))))


;fazer recursividade do menu
(defn interfaxe [] 
  (menu)
  (let [input (read-line)]
    (if (= input "5")
      (println "Ate logo!")
      (do
        (opcoes input)
        (read-line) 
        (recur))))
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (interfaxe))
