package network;

import network.workers.Listener;
import network.workers.MessageHandler;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;
import javax.net.ssl.SSLSocket;
import javax.xml.crypto.Data;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import filesystem.PeerSystemManager;

public class Server implements Node{

	private InetSocketAddress server_address;
	private int port;
	private String ip;
	private MessageHandler messageHandler;
	private Listener listener;
	//private ServerSocket serverSocket;

	private ConcurrentHashMap<Integer, InetSocketAddress> peers; 	// ID -> port, ip
	private ConcurrentHashMap<String, Set<InetSocketAddress>> backup_files_map; // filepath -> set(peers(have the key file))
	private ConcurrentMap<String, String> files_map; // filePath -> fileId

	public Server(InetSocketAddress address){

		this.server_address = address;
		this.port = server_address.getPort();
		this.ip = server_address.getAddress().getHostAddress();

		this.messageHandler = new MessageHandler(this);
		this.listener = new Listener(this, messageHandler);

		System.out.println("Server IP: " + ip);

		this.peers = new ConcurrentHashMap<>();
		this.backup_files_map = new ConcurrentHashMap<>();
		this.files_map = new ConcurrentHashMap<>();
		// this.files = new ConcurrentHashMap<>();
		// // this.chunks = new ConcurrentHashMap<>();

		System.setProperty("javax.net.ssl.keyStore", "src/network/server.keys");
		System.setProperty("javax.net.ssl.keyStorePassword", "123456");
		System.setProperty("javax.net.ssl.trustStore", "src/network/truststore");
		System.setProperty("javax.net.ssl.trustStorePassword", "123456");


		startWorkers();
	}



	@Override
	public void addPeer(InetSocketAddress peer_add , int idPeer){
		peers.putIfAbsent(idPeer, peer_add);
	}

	@Override
	public void addBackupFile(String fileId, InetSocketAddress peer_add){
		backup_files_map.putIfAbsent(fileId, new HashSet<InetSocketAddress>());
		backup_files_map.get(fileId).add(peer_add);
	}

	@Override
	public void addFile(String fileId, String filePath){
		files_map.putIfAbsent(filePath, fileId);
	}

	@Override
	public String getFile(String filePath){
		return files_map.get(filePath);
	}	


	@Override
	public ArrayList<InetSocketAddress> getPeers(){

		ArrayList<InetSocketAddress> list = new ArrayList<>();

		for (Map.Entry<Integer, InetSocketAddress> entry : peers.entrySet()) {
    		list.add(entry.getValue());
		}

		return list;
}

	@Override
	public ArrayList<InetSocketAddress> getBackupFilesMap(String filePath){

		String fileID = getFile(filePath);

		ArrayList<InetSocketAddress> list = new ArrayList<>();

		Set<InetSocketAddress> isa = backup_files_map.get(fileID);

		for (InetSocketAddress i : isa){
			list.add(i);
		}

		return list;
	}


	@Override
	public void startWorkers(){
		listener.start();
	}

	@Override
	public InetSocketAddress getLocalAddress(){
		return this.server_address;
	}

	@Override
	public int getId(){
		return 1;
	}

	@Override
	public InetSocketAddress getServerAddress(){
		return this.server_address;
	}

	@Override
	public PeerSystemManager getManager(){
		return null;
	}

}
