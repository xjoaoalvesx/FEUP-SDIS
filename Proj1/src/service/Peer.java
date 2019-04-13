package service;

import channels.Channel;
import filesystem.PeerSystemManager;
import subprotocols.Backup;
import subprotocols.Restore;
import subprotocols.Delete;
import messages.Message;
import messages.MessageHandler;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Peer implements RemoteService{

	private final int id;
	private String protocol_version;
	private String access_point;
	private Channel mc;
	private Channel mdb;
	private Channel mdr;
	private PeerSystemManager manager;
	private int max_space;
	private int available_space;

	private ScheduledExecutorService scheduler;

	public Peer(String protocol_version, int id, String access_point, String[] mc_name, String[] mdb_name, String[] mdr_name){
		this.id = id;
		this.protocol_version = protocol_version;
		this.access_point = access_point;

		startChannels(mc_name, mdb_name, mdr_name);
		manager = new PeerSystemManager(this);

		scheduler = new ScheduledThreadPoolExecutor(8);

		max_space = 9600000; // space for 150 chunks
		available_space = 9600000;

		System.out.println("Peer " + id + " entered the network!");
	}

	public static void main(String args[]){

		if(args.length != 6){
			System.out.println("Peer: java -classpath bin service.Peer" + " <protocol_version> <peer_id> <service_access_point>" + 
							   " <mc> <mdb> <mdr>");
			return;
		}

		int peerID = Integer.parseInt(args[1]);
		String protocol_version = args[0];
		String service_access_point = args[2];

		String[] mc_name = args[3].split("/");
		String[] mdb_name = args[4].split("/");
		String[] mdr_name = args[5].split("/");

		try {
			Peer peer = new Peer(protocol_version, peerID, service_access_point, mc_name, mdb_name, mdr_name);
			RemoteService stub = (RemoteService) UnicastRemoteObject.exportObject(peer, 0);

			Registry registry = LocateRegistry.getRegistry();
			String name = "P" + args[1];
			registry.rebind(name, stub); // rebind for testing (change to bind if necessary)
			System.out.println("Server ready!");
		} catch(Exception e){
			System.out.println("Server exception: " + e.toString());
			System.exit(1);
		}


	}

	private void startChannels(String[] mc_name, String[] mdb_name, String[] mdr_name){
		this.mc = new Channel("MC", this, mc_name[0], mc_name[1]);
		this.mdb = new Channel("MDB", this, mdb_name[0], mdb_name[1]);
		this.mdr = new Channel("MDR", this, mdr_name[0], mdr_name[1]);

		new Thread(this.mc).start();
		new Thread(this.mdb).start();
		new Thread(this.mdr).start();
	}

	public void sendMessageMC(Message msg) throws IOException{
		mc.sendMessage(msg.getMessageInBytes());
	}

	public void sendMessageMDB(Message msg) throws IOException{
		mdb.sendMessage(msg.getMessageInBytes());
	}

	public void sendMessageMDR(Message msg) throws IOException{
		mdr.sendMessage(msg.getMessageInBytes());
	}

	public void executeMessageHandler(Message m){
		scheduler.execute(new MessageHandler(this, m));
	}

	@Override
    public void backup(String path, int replicationDegree) {
        scheduler.execute(new Backup(this, protocol_version, path, replicationDegree));
    }

    @Override
    public void restore(String path) {
        scheduler.execute(new Restore(this, protocol_version, path));
    }

    @Override
    public void delete(String path) {
        scheduler.execute(new Delete(this, protocol_version, path));
    }

    @Override
    public void reclaim() {
        System.out.println("RECLAIM");
    }

    @Override
    public void state() {
        System.out.println("STATE");
    }
	
	public int getId(){
		return this.id;
	}

	public int getMaxSpace(){
		return this.max_space;
	}

	public int getAvailableSpace(){
		return this.available_space;
	}

	public void setMaxSpace(int space){
		this.max_space = space;
	}

	public void setAvailableSpace(int space){
		this.available_space = space;
	}

	public PeerSystemManager getPeerSystemManager(){
		return manager;
	}

	public boolean finishedRestoringFile(String file_path, String fileId){
		int totalChunks = getPeerSystemManager().getNumChunks(file_path);
		int chunksRestored = getPeerSystemManager().getChunksRestored(fileId).size();

		return totalChunks == chunksRestored;
	}

	public boolean finishedReclaimingSpace(int desired_space){	
		return desired_space == this.max_space;
	}
}