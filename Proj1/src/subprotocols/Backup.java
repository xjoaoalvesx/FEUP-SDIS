package subprotocols;

import service.Peer;
import filesystem.Chunk;
import messages.Message;
import subprotocols.workers.BackupWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


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
    		chunks = check(this.path, this.replicationDegree);
    	}catch (IOException e){
    		e.printStackTrace();
    	}

    	ArrayList<Thread> workers = new ArrayList<>(chunks.length);
    	

    	for(Chunk chunk : chunks){
    		Thread worker = new Thread (new BackupWorker(this, chunk));
    		workers.add(worker);
    		worker.start();
    	}

    	try{
    		for (Thread w : workers) {
            w.join();
        	}
        }catch (InterruptedException e){
        	System.out.println("Backup: failed joining threads");
        }

        addFileToRestore(this.path, chunks[0].getFileID());
    	System.out.println("backup run");
    }


    private void addFileToRestore(String filePath, String fileId){

        parent_peer.getPeerSystemManager().addFileToRestore(filePath, fileId);


    }

    public Peer getParentPeer(){
    	return parent_peer;
    }

    public String getProtocolVersion(){
    	return version;
    }

    

}