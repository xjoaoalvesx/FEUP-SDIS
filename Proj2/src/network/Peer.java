package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class Peer implements PeerNode {

	private Key key;
	private InetSocketAddress nodeAdress;
	private InetSocketAddress predecessor;

	private ConcurrentHashMap<Integer, InetSocketAddress> fingers;
	
	

	private int peer_id;
	private int port;


	public Peer(int peer_id, int port){

		this.peer_id = peer_id;
		this.port = port;
	}
}