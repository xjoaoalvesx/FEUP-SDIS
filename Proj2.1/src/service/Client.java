package service;

import java.rmi.*;
import java.rmi.registry.*;
import java.util.*;

public class Client implements Runnable{

	private String peerID;
	private String protocol;

	private ArrayList<Runnable> protocolHandlers;
	private int subProtocolIndex;

	private RemoteService stub;


	public Client(String peerID, String protocol){

		this.peerID = peerID;
		this.protocol = protocol;

		protocolHandlers = new ArrayList<Runnable> ();
		protocolHandlers.add(this::backupStart);
		protocolHandlers.add(this::deleteStart);
		protocolHandlers.add(this::restoreStart);

		switch(protocol){

			case "BACKUP":
				this.subProtocolIndex = 0;
				break;

			case "DELETE":
				this.subProtocolIndex = 1;
				break;

			case "RESTORE":
				this.subProtocolIndex = 2;
				break;

			default:
				break;
		}
	}


	public static void main(String[] args){

		if(args.length != 2){

			System.out.println("Usage:");
			System.out.println("\tjava -classpath bin service.Client <peer_id> <protocol>");
			return;
		}

		String peerID = args[0];
		String protocol = args[1];

		Client client = new Client(peerID, protocol);
		// new Thread(client).start();
	}

	@Override
	public void run() {
    	initStub();
    	protocolHandlers.get(subProtocolIndex).run();
    }

    private void initStub(){

    	String name = "P" + peerID;
    	try{
    		Registry registry = LocateRegistry.getRegistry(null);
    		stub = (RemoteService) registry.lookup(name);
    	} catch (Exception e){
    		System.err.println("Error finding stub");
    		e.printStackTrace();
    	}
    }

    private void backupStart(){

    	System.out.println("backupStart");
    	try {
            stub.backup("path");
        } catch (RemoteException e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

    private void deleteStart(){

    	System.out.println("deleteStart");
    	try {
            stub.delete("path");
        } catch (RemoteException e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

    private void restoreStart(){

    	System.out.println("restoreStart");
    	try {
            stub.restore("path");
        } catch (RemoteException e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

}