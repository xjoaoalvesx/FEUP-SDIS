package network;

import service.RemoteService;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class Peer implements RemoteService{

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

		try{

			Socket socket = new Socket(serverIP, serverPort);

			String message = registerPeerMessage();

			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			// BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			dos.writeBytes(message);

			socket.close();
			while(true){

			}
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