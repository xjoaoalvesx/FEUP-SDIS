package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class Server {

	private int port;
	private String ip;
	private ServerSocket serverSocket;

	private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, String>> peers; 	// ID -> port, ip
	// private ConcurrentMap<String, String> files; 								// fileID -> chunkID
	// private ConcurrentHashMap<String, Integer> chunks; 							// chunkID -> ID

	public Server(int port){

		this.port = port;
		this.ip = getServerIP();
		System.out.println("Server IP: " + ip);

		this.peers = new ConcurrentHashMap<>();
		// this.files = new ConcurrentHashMap<>();
		// this.chunks = new ConcurrentHashMap<>();

		try{

			this.serverSocket = new ServerSocket(port);

			while(true){

				Socket socket = serverSocket.accept();

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			    // DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

			    String msg = bufferedReader.readLine();
			    manageMessage(msg);
			    // dos.writeBytes(clientSentence);

				socket.close();
			}
		}
		catch(IOException ioException){

			ioException.printStackTrace();
		}
		
	}

	private static String getServerIP(){

		String ip = "";
		try{

			ip = InetAddress.getLocalHost().getHostAddress();
		}
		catch(UnknownHostException unknownHostException){

			unknownHostException.printStackTrace();
		}

		return ip;
	}

	private void manageMessage(String msg){

		String[] msgData = msg.split(" ");
		switch(msgData[0]){

			case "REGISTER":
				manageREGISTER(msgData);
				break;

			default:
				break;
		}
	}

	// REGISTER peerID peerPort peerIP
	private void manageREGISTER(String[] msgData){

		int peerID = Integer.parseInt(msgData[1]);
		int peerPort = Integer.parseInt(msgData[2]);
		String peerIP = msgData[3];


		ConcurrentHashMap<Integer, String> chm = new ConcurrentHashMap<Integer, String>(); 
		chm.put(peerPort, peerIP);
		peers.put(peerID, chm);
		System.out.println(peers);
	}
}