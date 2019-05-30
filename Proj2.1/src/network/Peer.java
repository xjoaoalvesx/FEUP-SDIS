package network;

import service.RemoteService;
import network.workers.MessageHandler;


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

public class Peer implements RemoteService{

	private int peerID;
	private String peerIP;
	private int peerPort;
	private String serverIP;
	private int serverPort;

	private MessageHandler messageHandler;

	public Peer(int peerID, String peerIP, int peerPort, String serverIP, int serverPort){

		this.peerID = peerID;
		this.peerIP = peerIP;
		this.peerPort = peerPort;
		this.serverIP = serverIP;
		this.serverPort = serverPort;

		this.messageHandler = new MessageHandler(this);

		System.setProperty("javax.net.ssl.trustStore", "src/network/myTrustStore.jts");
		System.setProperty("javax.net.ssl.trustStorePassword", "123456");
		//System.setProperty("javax.net.debug", "all");

		try{

			Scanner scn = new Scanner(System.in);

			SSLSocketFactory sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
			SSLSocket sslSocket = (SSLSocket)sslsocketfactory.createSocket(this.serverIP,this.serverPort);
			DataOutputStream outputStream = new DataOutputStream(sslSocket.getOutputStream());
			DataInputStream inputStream = new DataInputStream(sslSocket.getInputStream());

			//sslSocket.startHandshake();
			while(true){
				System.out.println(inputStream.readUTF());
				String tosend = scn.nextLine();
				outputStream.writeUTF(tosend);

				if(tosend.equals("Exit")){
					System.out.println("Closing this connection : " + sslSocket);
					sslSocket.close();
					break;
				}

			}
			scn.close();
			inputStream.close();
			outputStream.close();
		}
		catch(IOException ioException){

			ioException.printStackTrace();
		}
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



	// REGISTER peerID peerPort peerIP
	private String registerPeerMessage(){

		String str = "REGISTER";
		str += " ";
		str += String.valueOf(peerID);
		str += " ";
		str += String.valueOf(peerPort);
		str += " ";
		str += peerIP;
		return str;
	}
}
