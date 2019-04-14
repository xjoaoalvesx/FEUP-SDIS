package subprotocols.workers;

import service.Peer;
import messages.Message;
import filesystem.Chunk;

import java.io.IOException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReclaimWorker implements Runnable {

	private static final int RETRIES = 5;
	private Peer parent_peer;
	private Message message;
    private String fileId;
    private String chunkNo;

	public ReclaimWorker(Peer parent_peer, Message message){
		this.parent_peer = parent_peer;
		this.message = message;
        this.fileId = message.getFileId();
        this.chunkNo = message.getChunkNo();
	}	

	@Override
	public void run(){

        if(parent_peer.getId() == Integer.parseInt(message.getSenderId()) || !(parent_peer.getPeerSystemManager().hasChunk(fileId, chunkNo))){
			return;
		}

		this.parent_peer.getPeerSystemManager().removeChunkFromMap(fileId, chunkNo, message.getSenderId());

		int rep = this.parent_peer.getPeerSystemManager().checkReplicationDegree(fileId, chunkNo);

		if(rep < 0){
			rep = 0 - rep;
			byte[] data = this.parent_peer.getPeerSystemManager().getChunkData(fileId, chunkNo);
			Message m = create_putchunk_message(message.getVersion(), String.valueOf(rep), data);

			int interval = 1000; // 1 second

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
				String key = this.fileId + this.chunkNo;
				if (parent_peer.getPeerSystemManager().getCurrentDegree(key) >= parent_peer.getPeerSystemManager().getDesiredDegree(key)){
					System.out.println("Desired degree achieved!");
					break;
				}
	
			}	
			
		}

		System.out.println("Reclaim Worker!");
	}

	private Message create_putchunk_message(String version , String replicationDegree, byte[] data){
    	return new Message("PUTCHUNK", version, Integer.toString(parent_peer.getId()), 
    		this.fileId, this.chunkNo, replicationDegree, data);
    }

}