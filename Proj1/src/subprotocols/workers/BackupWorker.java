package subprotocols.workers;

import service.Peer;
import messages.Message;
import filesystem.Chunk;
import subprotocols.Backup;

import java.io.IOException;

public class BackupWorker implements Runnable {

	private static final int RETRIES = 5;
	private Chunk chunk;
	private Peer parent_peer;
	private String protocol_version;

	public BackupWorker(Backup backup, Chunk chunk){

		this.chunk = chunk;
		this.parent_peer = backup.getParentPeer();
		this.protocol_version = backup.getProtocolVersion();

	}

	@Override
    public void run() {


    	int interval = 1000; // 1 second

    	Message message = create_putchunk_message(chunk, protocol_version);

    	for(int t = 0; t < RETRIES; t++){

    		try{
    			parent_peer.sendMessageMDB(message);
    		}catch (IOException e){
    			System.out.println("Error: Could not send message to MDB channel!");
    		}

    		try{
    			Thread.sleep(interval);
    		}catch(InterruptedException e){
    			System.out.println(e.getMessage());
    		}
    		
    		interval *= 2;

    	}


    }


    private Message create_putchunk_message(Chunk chunk, String version){
    	return new Message("PUTCHUNK", version, Integer.toString(parent_peer.getId()), 
    		chunk.getFileID().toString(), Integer.toString(chunk.getID()), Integer.toString(chunk.getReplicationDegree()), chunk.getChunkData());
    }


}