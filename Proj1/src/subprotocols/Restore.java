package subprotocols;

import service.Peer;
import subprotocols.workers.RestoreWorker;
import messages.Message;
import messages.Getchunk;
import filesystem.Chunk;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

import static filesystem.PeerSystemManager.joinChunks;


public class Restore implements Runnable {

	private Peer parent_peer;
	private String version;
	private String file_path;
	private String fileId;

	public Restore(Peer parent_peer, String version, String filePath){

		this.parent_peer = parent_peer;
		this.version = version;
		this.file_path = filePath;
        this.fileId = parent_peer.getPeerSystemManager().getFileIdbyPath(file_path);

	}

	@Override
    public void run(){

        if(fileId == null){
            System.out.println("File not available to RESTORE!");
        }

        parent_peer.getPeerSystemManager().setRestoring(true, this.fileId);

    	int num_chunks = parent_peer.getPeerSystemManager().getNumChunks(file_path);

    	for(int i = 0; i < num_chunks; i++){
    		sendGETCHUNKrequest(i);
    	}

        while(!parent_peer.finishedRestoringFile(file_path, fileId)){
            Thread.yield();
        }

        System.out.println("All chunks have been received!");

        ConcurrentMap<Integer, Chunk> chunks = parent_peer.getPeerSystemManager().getChunksRestored(this.fileId);

        try {
            String chunks_path = "src/filesystem/Peer" + parent_peer.getId() + "/restored";
            Path path = Paths.get(file_path);
            String name = path.getFileName().toString();
            parent_peer.getPeerSystemManager().saveFile(name, chunks_path, joinChunks(new ArrayList<>(chunks.values())));
        }catch(IOException e){
            System.out.println("Failed saving file");
        }

        parent_peer.getPeerSystemManager().setRestoring(false, this.fileId);

    	System.out.println("Restore run");

    }


    private void sendGETCHUNKrequest(int chunkNo){

        Message m = new Getchunk(version, Integer.toString(parent_peer.getId()), fileId, Integer.toString(chunkNo));

        try{
            parent_peer.sendMessageMC(m);
        }catch(IOException e){
            System.out.println("Error sending message to MC channel (GETCHUNK) !");
        }
    }

    public Peer getParentPeer(){
    	return parent_peer;
    }

    public String getProtocolVersion(){
    	return version;
    }

}