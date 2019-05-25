package service;

import network.Peer;
import network.Server;

import java.io.*;
import java.net.*;


public class StartPeer {

	public static void main(String[] args){

		if(args.length != 1 && args.length != 4){
			System.out.println("Usage:");
			System.out.println("\tjava -classpath bin service.StartPeer <server_port>");
			System.out.println("\tor");
			System.out.println("\tjava -classpath bin service.StartPeer <peer_port> <peer_id> <server_ip> <server_port>");
			return;
		}

		int port = Integer.parseInt(args[0]);

		if(args.length == 1){

			System.out.println("Server has been created on the " + port + " port.");
			Server server = new Server(port);
		}
		else{

			int peerID = Integer.parseInt(args[1]);
			String serverIP = args[2];
			int serverPort = Integer.parseInt(args[3]);
			System.out.println("Peer " + peerID + " has been created on the " + port + " port.");
			Peer peer = new Peer(peerID, port, serverIP, serverPort);
		}
	}
}