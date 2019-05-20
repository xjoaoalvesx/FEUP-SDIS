package service;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class StartPeer {

	public static void main(String[] args){

		if(args.length != 1){
			System.out.println("Usage:\njava -classpath bin service.StartPeer <peer_id>");
			return;
		}

		int peer_id = Integer.parseInt(args[0]);

		Peer peer = new Peer(peer_id);

		
		System.out.println("Peer " + String.valueOf(peer_id) + " has been created.");
	}
}