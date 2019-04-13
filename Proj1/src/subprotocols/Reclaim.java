package subprotocols;

import service.Peer;
import subprotocols.workers.RestoreWorker;
import messages.Message;
import messages.Response;
import messages.Getchunk;
import filesystem.Chunk;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

import static filesystem.PeerSystemManager.joinChunks;


public class Reclaim implements Runnable {

	private Peer parent_peer;
	private int desired_size;

	public Reclaim(Peer parent_peer, int size){

		this.parent_peer = parent_peer;
		this.desired_size = size;

	}

	@Override
    public void run(){

        

    }


    private void sendREMOVEDrequest(String fileId, int chunkNo){

        Message m = new Response("REMOVED", "1.0", Integer.toString(parent_peer.getId()), fileId, Integer.toString(chunkNo));

        try{
            parent_peer.sendMessageMC(m);
        }catch(IOException e){
            System.out.println("Error sending message to MC channel (GETCHUNK) !");
        }
    }

    public Peer getParentPeer(){
    	return parent_peer;
    }

}