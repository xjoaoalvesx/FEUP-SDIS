package network;

import service.RemoteService;
import network.workers.MessageHandler;
import network.workers.Listener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Scanner;

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

	private String serverIP;
	private int serverPort;

	private final InetSocketAddress peerAddress;

	private MessageHandler messageHandler;
	private Listener listener;

	public Peer(int peerID, InetSocketAddress address, String serverIP, int serverPort){

		this.peerID = peerID;
		this.peerAddress = address;
		this.serverIP = serverIP;
		this.serverPort = serverPort;

		this.messageHandler = new MessageHandler(this);
		this.listener = new Listener(this, messageHandler);

		startWorkers();
	}





	// public boolean registerToServer(InetSocketAddress server){

	// 	System.out.println("Joining Server at " + server);

	// 	if(server == null || server.equals(getLocalAddress())){
	// 		System.out.println("Failed to register to server");
	// 	}

	// 	Message request = Message.request(Message.Type.REGISTER, peerAddress);

	// 	messageHandler.


	// }

	@Override
	public void startWorkers(){
		listener.start();
	}


	@Override
	public void backup(String path){

		System.out.println("backup");
	}

	@Override
	public void delete(String path){

		System.out.println("delete");
	}

	@Override
	public void restore(String path){

		System.out.println("restore");
	}

	@Override
	public InetSocketAddress getLocalAddress(){
		return this.peerAddress;
	}

}
