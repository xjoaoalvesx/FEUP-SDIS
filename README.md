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
```
java -classpath bin service.TestApp <initiator_peer_id> <backup_protocol> <file_path> <replication_degree>
```
E.g.: java -classpath bin service.TestApp 1 BACKUP "files/test.txt" 1


##### Delete
```
java -classpath bin service.TestApp <initiator_peer_id> <delete_protocol> <file_path>
```
E.g.: java -classpath bin service.TestApp 1 DELETE "files/test.txt"


##### Restore
```
java -classpath bin service.TestApp <initiator_peer_id> <restore_protocol> <file_path>
```
E.g.: java -classpath bin service.TestApp 1 RESTORE "files/test.txt"
