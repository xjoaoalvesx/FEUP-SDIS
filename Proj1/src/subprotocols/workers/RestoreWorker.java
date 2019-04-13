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

	public RestoreWorker(Peer parent_peer, Message m){
		this.parent_peer = parent_peer;
		this.version = m.getVersion();
		this.message = m;
        this.random = new Random();
        this.scheduledHandler = null;
	}

	@Override
    public void run(){

    	if (Integer.parseInt(message.getSenderId()) == parent_peer.getId()) {
            System.out.println("Ignoring CHUNKs from own files");
            return;
        }

        String fileId = message.getFileId();
        String chunkNo = message.getChunkNo();

        byte[] data = parent_peer.getPeerSystemManager().getChunkData(fileId, chunkNo);

        Message chunk_m = create_chunk_message(data);



        scheduledHandler = parent_peer.getExecutor().schedule(() -> {
            try {
                parent_peer.sendMessageMDR(chunk_m);
            } catch (IOException e) {
                System.out.println("Error: Could not send message to MDR channel(CHUNK)!");
            }
        },  random.nextInt(400), TimeUnit.MILLISECONDS);


    }


    private Message create_chunk_message(byte[] data){
    	return new Message("CHUNK", version, Integer.toString(parent_peer.getId()), message.getFileId(), message.getChunkNo(), "" , data);
    }
    
}