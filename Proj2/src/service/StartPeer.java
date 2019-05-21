package service;

import network.Peer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class StartPeer {

	public static void main(String[] args){

		if(args.length != 2){
			System.out.println("Usage:\njava -classpath bin service.StartPeer <peer_id> <port>");
			return;
		}

		int peer_id = Integer.parseInt(args[0]);
		int port = Integer.parseInt(args[1]);

		Peer peer = new Peer(peer_id, port);

		
		System.out.println("Peer " + args[0] + " has been created on the " + args[1] + "port.");
	}
}