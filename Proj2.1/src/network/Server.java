package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;
import javax.net.ssl.SSLSocket;
import javax.xml.crypto.Data;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class Server {

	private int port;
	private String ip;
	//private ServerSocket serverSocket;

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

		System.setProperty("javax.net.ssl.keyStore", "src/network/myKeyStore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "123456");
		//Optional
		//System.setProperty("java.net.debug", "all");

		try{

			SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
			SSLServerSocket sslServerSocket = (SSLServerSocket)sslServerSocketfactory.createServerSocket(this.port,50,InetAddress.getLocalHost());
			System.out.println("Echo Server Started & Ready to accept Client Connection"); 

			while(true){

				SSLSocket sslSocket = (SSLSocket)sslServerSocket.accept();

				try{

					System.out.println("A new client is connected : " + sslSocket);

					DataInputStream inputStream = new DataInputStream(sslSocket.getInputStream());
					DataOutputStream outputStream = new DataOutputStream(sslSocket.getOutputStream());
					
					System.out.println("Assigning a new thread for this client");

					Thread t = new PeerHandler(sslSocket, inputStream, outputStream);

					t.start();
				}
				catch(Exception e){
					sslSocket.close();
					e.printStackTrace();
				}
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

class PeerHandler extends Thread{
	
	private SSLSocket ssl;
	private DataInputStream input;
	private DataOutputStream output;

	public PeerHandler(SSLSocket s, DataInputStream input, DataOutputStream output){
		this.ssl = s;
		this.input = input;
		this.output = output;
	}

    @Override
    public void run()  
    { 
		String received;
		String toreturn;

        while (true)  
        { 
            try { 
				output.writeUTF("Write Exit to terminate connection");
				received = input.readUTF();

				if(received.equals("Exit")){
					System.out.println("Client " + this.ssl + "sends exit...");
					this.ssl.close();
					System.out.println("Connection closed");
					break;
				}
				
            } catch (IOException e) { 
                e.printStackTrace(); 
            } 
		}
		try{
			this.input.close();
			this.output.close();
		}catch(IOException e){
			e.printStackTrace();
		}
    }
}