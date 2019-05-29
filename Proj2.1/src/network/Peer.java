package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.util.Scanner;

public class Peer {

	private int peerID;
	private String peerIP;
	private int peerPort;
	private String serverIP;
	private int serverPort;

	public Peer(int peerID, int peerPort, String serverIP, int serverPort){

		this.peerID = peerID;
		this.peerIP = getPeerIP();
		this.peerPort = peerPort;
		this.serverIP = serverIP;
		this.serverPort = serverPort;

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

	// REGISTER peerID peerPort peerIP
	private String registerPeerMessage(){

		String str = "REGISTER";
		str += " ";
		str += String.valueOf(peerID);
		str += " ";
		str += String.valueOf(peerPort);;
		str += " ";
		str += peerIP;
		return str;
	}

	private static String getPeerIP(){

		String ip = "";
		try{

			ip = InetAddress.getLocalHost().getHostAddress();
		}
		catch(UnknownHostException unknownHostException){

			unknownHostException.printStackTrace();
		}

		return ip;
	}
}