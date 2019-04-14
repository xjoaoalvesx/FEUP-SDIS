package subprotocols.workers;

import subprotocols.Restore;
import service.Peer;
import messages.Message;
import java.io.IOException;


import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RestoreWorker implements Runnable{
	
	private Peer parent_peer;
	private String version;
	private Message message;
    private Random random;
    private Future scheduledHandler;
    private String received_fileId;
    private String received_chunkNo;


	public RestoreWorker(Peer parent_peer, Message m, String received_fileId, String received_chunkNo){
		this.parent_peer = parent_peer;
		this.version = m.getVersion();
		this.message = m;
        this.random = new Random();
        this.scheduledHandler = null;
        this.received_fileId = received_fileId;
        this.received_chunkNo = received_chunkNo;
	}

	@Override
    public void run(){

    	if (Integer.parseInt(message.getSenderId()) == parent_peer.getId()) {
            System.out.println("Ignoring CHUNKs from own files");
            return;
        }

        if(message.getFileId().equals(this.received_fileId) && message.getChunkNo().equals(this.received_chunkNo)){
            System.out.print("Another peer already answered");
            return;
        }

        String fileId = message.getFileId();
        String chunkNo = message.getChunkNo();

        byte[] data = parent_peer.getPeerSystemManager().getChunkData(fileId, chunkNo);

        if(data == null){
            System.out.println("File was not found in current Peer data");
            return;
        }
        Message chunk_m = create_chunk_message(data);

        try {
           parent_peer.sendMessageMDR(chunk_m);
        } catch (IOException e) {
            System.out.println("Error: Could not send message to MDR channel(CHUNK)!");
        }
  


    }


    private Message create_chunk_message(byte[] data){
    	return new Message("CHUNK", version, Integer.toString(parent_peer.getId()), message.getFileId(), message.getChunkNo(), "" , data);
    }
    
}