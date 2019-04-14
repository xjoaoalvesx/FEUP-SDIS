Dentro da pasta do projeto (Proj1) ---->> Abrir terminal linux

-> Para compilar basta correr o script(make.sh):
	sh make.sh

-> Para inicializar o registo RMI correr o script(startRMI.sh):
	sh startRMI.sh

-> Para incializar um Peer, correr o script (peer_enh.sh):
	sh peer_enh.sh <version> <peer_id>   (exemplo : sh peer_enh.sh 1.0 1)

Para correr um protocolo (ainda dentro da pasta Proj1) :
	Foi criado um script para cada um dos protocolos : (backup.sh, delete.sh, reclaim.sh, restore.sh, state.sh)
	é possível correr qualquer um : sh <protocolo>.sh
	- ou então manualmente -
		uso do TestApp: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2
			exemplos : java -classpath bin service.TestApp 1 BACKUP "files/test.pdf" 1
					   java -classpath bin service.TestApp 1 DELETE "files/test.pdf"
					   java -classpath bin service.TestApp 1 RESTORE "files/test.pdf"

Após iniciar um Peer, uma pasta dentro do diretório do projeto é criada com o nome "Peers"
e dentro dessa pasta vão sendo criadas as pastas correspondentes a cada Peer inicializado.


