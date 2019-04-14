package service;

import java.util.ArrayList;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class TestApp implements Runnable {


	private String peer_ap;
    private String sub_protocol;
    private String opnd_1;
    private String opnd_2;
   	private ArrayList<Runnable> protocol_handlers;
   	private int sub_protocol_index;

    private RemoteService stub;

	public TestApp(String peer_ap, String sub_protocol, String opnd_1, String opnd_2){

		this.peer_ap = peer_ap;
		this.sub_protocol = sub_protocol;
		this.opnd_1 = opnd_1;
		this.opnd_2 = opnd_2;

		protocol_handlers = new ArrayList<Runnable> ();
		protocol_handlers.add(this::backup_start);
		protocol_handlers.add(this::restore_start);
		protocol_handlers.add(this::delete_start);
		protocol_handlers.add(this::reclaim_start);
		protocol_handlers.add(this::state_start);

		switch(sub_protocol){
			case "BACKUP":
				this.sub_protocol_index = 0;
				break;
			case "RESTORE":
				this.sub_protocol_index = 1;
				break;
			case "DELETE":
				this.sub_protocol_index = 2;
				break;
			case "RECLAIM":
				this.sub_protocol_index = 3;
				break;
			case "STATE":
				this.sub_protocol_index = 4;
				break;
		}

	}

	public static void main(String[] args){

		//checks for correct call of the application
		if(args.length < 2 || args.length > 4){
			System.out.println("Application: Java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");

		}
		
		String peer_ap = args[0];

		
		if(peer_ap == null){
			return;
		}

		String sub_protocol = args[1];
		String op1 = args.length > 2 ? args[2] : null;
		String op2 = args.length > 3 ? args[3] : null;

		TestApp application = new TestApp(peer_ap, sub_protocol, op1, op2);
		new Thread(application).start();


	}

	@Override
    public void run() {
    	initStub();
    	protocol_handlers.get(sub_protocol_index).run();
    }

    private void initStub(){

    	String name = "P" + peer_ap;
    	try{
    		Registry registry = LocateRegistry.getRegistry(null);
    		stub = (RemoteService) registry.lookup(name);
    	} catch (Exception e){
    		System.err.println("Error finding stub");
    		e.printStackTrace();
    	}
    }

    private void backup_start(){
    	try {
            stub.backup(this.opnd_1, Integer.parseInt(this.opnd_2));
        } catch (RemoteException e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

    private void restore_start(){
    	try {
            stub.restore(this.opnd_1);
        } catch (RemoteException e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

    private void delete_start(){
    	try {
            stub.delete(this.opnd_1);
        } catch (RemoteException e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

    private void reclaim_start(){
    	try {
            stub.reclaim(Integer.parseInt(this.opnd_1));
        } catch (RemoteException e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

    private void state_start(){
    	try {
            stub.state();
        } catch (RemoteException e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

}
