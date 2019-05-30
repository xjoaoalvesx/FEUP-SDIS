package service;

import network.Peer;
import network.Server;

import java.io.*;
import java.net.*;
import java.rmi.registry.*;
import java.rmi.server.*;


public class StartPeer {

	public static void main(String[] args){

		if(args.length != 2 && args.length != 5){

			System.out.println("Usage:");
			System.out.println("\tjava -classpath bin service.StartPeer <server_ip> <server_port>");
			System.out.println("\tor");
			System.out.println("\tjava -classpath bin service.StartPeer <peer_ip> <peer_port> <peer_id> <server_ip> <server_port>");
			return;
		}

		String ip = args[0];
		int port = Integer.parseInt(args[1]);

		InetAddress inetAd = null;
		try{
			inetAd = InetAddress.getByName(ip);
		}catch(UnknownHostException e){
			e.printStackTrace();
		}

		if(args.length == 2){
				System.out.println("Server has been created on the " + port + " port.");
				Server server = new Server(new InetSocketAddress(inetAd, port));

		}
		else{

			int peerID = Integer.parseInt(args[2]);
			String serverIP = args[3];
			int serverPort = Integer.parseInt(args[4]);

			InetAddress server_ad = null;
			try{
				server_ad = InetAddress.getByName(serverIP);
			}catch(UnknownHostException e){
				e.printStackTrace();
			}

			try {
				Peer peer = new Peer(peerID, new InetSocketAddress(inetAd, port), new InetSocketAddress(server_ad, serverPort));
				RemoteService stub = (RemoteService) UnicastRemoteObject.exportObject(peer, 0);

				Registry registry = LocateRegistry.getRegistry();
				String name = "P" + String.valueOf(peerID);
				registry.rebind(name, stub); // rebind for testing (change to bind if necessary)
				
			} catch(Exception e){
				System.out.println("Server exception: " + e.toString());
				System.exit(1);
			}
		}
	}
}
