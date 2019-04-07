package service;


import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class TestApp implements Runnable {


	private String peer_ap;
    private String sub_protocol;
    private String opnd_1;
    private String opnd_2;

    private RemoteService stub;

	public TestApp(String peer_ap, String sub_protocol, String opnd_1, String opnd_2){

		this.peer_ap = peer_ap;
		this.sub_protocol = sub_protocol;
		this.opnd_1 = opnd_1;
		this.opnd_2 = opnd_2;


		//TODO: maybe use data structure for mapping the handlers (backup, restore, delete, reclaim, state)

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


}
