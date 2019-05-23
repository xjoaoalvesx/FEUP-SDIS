package service;

import network.Peer;
import network.Server;

import java.io.*;
import java.net.*;


public class StartPeer {

	public static void main(String[] args){

		if(args.length != 1 && args.length != 2){
			System.out.println("Usage:");
			System.out.println("\tjava -classpath bin service.StartPeer <port>");
			System.out.println("\tor");
			System.out.println("\tjava -classpath bin service.StartPeer <port> <peer_id>");
			return;
		}

		int port = Integer.parseInt(args[0]);

		if(args.length == 1){

			Server server = new Server(port);
			System.out.println("Server has been created on the " + port + " port.");
		}
		else{

			int peerID = Integer.parseInt(args[1]);
			Peer peer = new Peer(peerID, port);
			System.out.println("Peer " + peerID + " has been created on the " + port + " port.");
		}
	}
}