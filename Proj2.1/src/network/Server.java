package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {

	private int port;
	private String ip;

	private ConcurrentHashMap<Integer, String> peers; 				// ID -> ip
	// private ConcurrentMap<String, String> files; 				// fileID -> chunkID
	// private ConcurrentHashMap<String, Integer> chunks; 			// chunkID -> ID

	public Server(int port){

		this.port = port;
		this.ip = getServerIP();

		this.peers = new ConcurrentHashMap<>();
		// this.files = new ConcurrentHashMap<>();
		// this.chunks = new ConcurrentHashMap<>();

		System.out.println(this.port);
		System.out.println(this.ip);
		System.out.println(this.peers);
	}

	public static String getServerIP(){

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