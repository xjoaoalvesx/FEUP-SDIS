package service;

import java.rmi.*;
import java.rmi.registry.*;
import java.util.*;

public class TestApp implements Runnable{

	private String peerID;
	private String subprotocol;
	private String opnd1;
	private String opnd2;

	private ArrayList<Runnable> protocolHandlers;
	private int subProtocolIndex;

	private RemoteService stub;


	public TestApp(String peerID, String subprotocol, String opnd1, String opnd2){

		this.peerID = peerID;
		this.subprotocol = subprotocol;
		this.opnd1 = opnd1;
		this.opnd2 = opnd2;

		protocolHandlers = new ArrayList<Runnable> ();
		protocolHandlers.add(this::backupStart);
		protocolHandlers.add(this::deleteStart);
		protocolHandlers.add(this::restoreStart);

		switch(subprotocol){

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

		if(args.length != 4 && args.length != 3){

			System.out.println("Usage:");
			System.out.println("\tjava -classpath bin service.Client <peer_id> <subprotocol> <opnd_1> <opnd_2>");
			return;
		}

		String peerID = args[0];
		String protocol = args[1];
		// String opnd1 = args[2];
		// String opnd2 = args[3];

		String opnd1 = args.length > 2 ? args[2] : null;
		String opnd2 = args.length > 3 ? args[3] : null;

		TestApp testApp = new TestApp(peerID, protocol, opnd1, opnd2);
		new Thread(testApp).start();
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
            stub.backup(this.opnd1, Integer.parseInt(this.opnd2));
        } catch (RemoteException e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

    private void deleteStart(){

    	System.out.println("deleteStart");
    	try {
            stub.delete(this.opnd1);
        } catch (RemoteException e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

    private void restoreStart(){

    	System.out.println("restoreStart");
    	try {
            stub.restore(this.opnd1);
        } catch (RemoteException e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

}
