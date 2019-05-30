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

public class Server implements Node{

	private InetSocketAddress server_address;
	private int port;
	private String ip;
	private MessageHandler messageHandler;
	private Listener listener;
	//private ServerSocket serverSocket;

	private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, String>> peers; 	// ID -> port, ip
	// private ConcurrentMap<String, String> files; 								// fileID -> chunkID
	// private ConcurrentHashMap<String, Integer> chunks; 							// chunkID -> ID

	public Server(InetSocketAddress address){

		this.server_address = address;
		this.port = server_address.getPort();
		this.ip = server_address.getAddress().getHostAddress();

		this.messageHandler = new MessageHandler(this);
		this.listener = new Listener(this, messageHandler);

		System.out.println("Server IP: " + ip);

		this.peers = new ConcurrentHashMap<>();
		// this.files = new ConcurrentHashMap<>();
		// // this.chunks = new ConcurrentHashMap<>();

		// System.setProperty("javax.net.ssl.keyStore", "src/network/myKeyStore.jks");
		// System.setProperty("javax.net.ssl.keyStorePassword", "123456");
		
		startWorkers();
		
	}



	@Override
	public void startWorkers(){
		listener.start();
	}

	@Override
	public InetSocketAddress getLocalAddress(){
		return this.server_address;
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