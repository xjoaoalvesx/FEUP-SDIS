package service;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements RemoteService{

	private final int id;

	public Peer(int id){
		this.id = id;

		System.out.println("Peer " + id + " entered the network!");
	}

	public static void main(String args[]){

		if(args.length != 1){
			System.out.println("Peer: java -classpath bin service.Peer" + " <peer_id>");
			return;
		}

		int peerID = Integer.parseInt(args[0]);

		try {
			Peer peer = new Peer(peerID);
			RemoteService stub = (RemoteService) UnicastRemoteObject.exportObject(peer, 0);

			Registry registry = LocateRegistry.getRegistry();
			String name = "P" + args[0];
			registry.rebind(name, stub);
			System.out.println("Server ready!");
		} catch(Exception e){
			System.out.println("Server exception: " + e.toString());
			System.exit(1);
		}
	}



	@Override
    public void backup() {
        System.out.println("BACKUP");
    }

    @Override
    public void restore() {
        System.out.println("RESTORE");
    }

    @Override
    public void delete() {
        System.out.println("DELETE");
    }

    @Override
    public void reclaim() {
        System.out.println("RECLAIM");
    }

    @Override
    public void state() {
        System.out.println("STATE");
    }
	
}