package network;

import service.RemoteService;
import network.workers.MessageHandler;
import network.workers.Listener;
import filesystem.PeerSystemManager;
import subprotocols.Backup;
import subprotocols.Delete;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;


import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Peer implements Node, RemoteService{

	private int peerID;


	private final InetSocketAddress peerAddress;
	private InetSocketAddress serverAddress;

	private MessageHandler messageHandler;
	private Listener listener;

	private PeerSystemManager manager;
	private ExecutorService executor;

	public Peer(int peerID, InetSocketAddress address, InetSocketAddress server_ad){

		this.peerID = peerID;
		this.peerAddress = address;
		this.serverAddress = server_ad;

		System.setProperty("javax.net.ssl.keyStore", "src/network/server.keys");
		System.setProperty("javax.net.ssl.keyStorePassword", "123456");
		System.setProperty("javax.net.ssl.trustStore", "src/network/truststore");
		System.setProperty("javax.net.ssl.trustStorePassword", "123456");

		this.messageHandler = new MessageHandler(this);
		this.listener = new Listener(this, messageHandler);
		this.manager = new PeerSystemManager(this);
		this.executor = Executors.newFixedThreadPool(5);
		startWorkers();

		registerToServer(serverAddress);
	}





	public void registerToServer(InetSocketAddress server){

		System.out.println("Joining Server at " + server);

		if(server == null || server.equals(getLocalAddress())){
			System.out.println("Failed to register to server");
		}

		Message request = Message.request(Message.Type.REGISTER, peerAddress, this.peerID);

		Message response = messageHandler.dispatchRequest(server, request);

		if(response.getMessageType() == Message.Type.ACCEPTED){
			System.out.println("Peer is now assigned to server : " + server + "\n");
		}
	}

	@Override
	public void startWorkers(){
		listener.start();
	}


	@Override
	public void backup(String path, int replicationDegree){

		executor.submit(new Backup(this, path, replicationDegree));
	}

	@Override
	public void delete(String path){

		executor.submit(new Delete(this, path));
	}

	@Override
	public void restore(String path){

		System.out.println("restore");
	}

	@Override
	public InetSocketAddress getLocalAddress(){
		return this.peerAddress;
	}

	@Override
	public InetSocketAddress getServerAddress(){
		return this.serverAddress;
	}

	@Override
	public ArrayList<InetSocketAddress> getPeers(){
		return null;
	}

	@Override
	public void addPeer(InetSocketAddress peer_add , int idPeer){
		return;
	}

	@Override
	public int getId(){
		return this.peerID;
	}

	public MessageHandler getMessageHandler(){
		return this.messageHandler;
	}

	@Override
	public void addBackupFile(String fileId, InetSocketAddress peer_add){
		return;
	}

	@Override
	public ArrayList<InetSocketAddress> getBackupFilesMap(String filePath){
		return null;
	}

	@Override
	public void addFile(String fileId, String filePath){
		return;
	}

	@Override
	public String getFile(String filePath){
		return null;
	}

	@Override
	public PeerSystemManager getManager(){
		return this.manager;
	}

}
