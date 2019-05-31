# FEUP-SDIS

## Second Project
A peer-to-peer distributed backup service for the Internet.

### Compile and start RMI
```
sh make.sh
cd bin
rmiregistry
```

### Run

#### Server
```
java -classpath bin service.StartPeer <server_ip> <server_port>
```
E.g.: java -classpath bin service.StartPeer 10.227.160.10 29555


#### Peer
```
java -classpath bin service.StartPeer <peer_ip> <peer_port> <peer_id> <server_ip> <server_port>
```
E.g.: java -classpath bin service.StartPeer 10.227.160.12 29556 1 10.227.160.10 29555

#### TestApp
```
java -classpath bin service.TestApp <initiator_peer_id> <subprotocol> <opnd_1> [<opnd_2>]
```

##### Backup

E.g.: java -classpath bin service.TestApp 1 BACKUP "files/test.txt" 2


##### Delete

E.g.: java -classpath bin service.TestApp 1 DELETE "files/test.txt"
