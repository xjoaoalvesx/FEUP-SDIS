package service;

import network.Peer;
import network.Server;

import java.io.*;
import java.net.*;
import java.rmi.registry.*;
import java.rmi.server.*;


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

			try {
				Peer peer = new Peer(peerID, port, serverIP, serverPort);
				RemoteService stub = (RemoteService) UnicastRemoteObject.exportObject(peer, 0);

				Registry registry = LocateRegistry.getRegistry();
				String name = "P" + String.valueOf(peerID);
				registry.rebind(name, stub); // rebind for testing (change to bind if necessary)
				System.out.println("Peer " + peerID + " has been created on the " + port + " port.");
			} catch(Exception e){
				System.out.println("Server exception: " + e.toString());
				System.exit(1);
			}
		}
	}
}