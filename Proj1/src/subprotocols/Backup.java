package subprotocols;

import service.Peer;
import filesystem.Chunk;
import messages.Message;
import subprotocols.workers.BackupWorker;

import java.io.IOException;

import static filesystem.PeerSystemManager.check;

public class Backup implements Runnable{

	private Peer parent_peer;
	private String version;
	private String path;
	private int replicationDegree;

	public Backup(Peer parent_peer, String version, String path, int replicationDegree){
		this.parent_peer = parent_peer;
		this.version = version;
		this.path = path;
		this.replicationDegree = replicationDegree;

		System.out.println("The Backup Started!");
	}

	@Override
    public void run() {

    	Chunk[] chunks = null;
    	try{
    		chunks = check(this.path);
    	}catch (IOException e){
    		e.printStackTrace();
    	}
    	

    	for(Chunk chunk : chunks){
    		Thread worker = new Thread (new BackupWorker(this, chunk));
    		worker.start();
    	}

    	System.out.println("backup run");
    }

    public Peer getParentPeer(){
    	return parent_peer;
    }

    public String getProtocolVersion(){
    	return version;
    }

    

}