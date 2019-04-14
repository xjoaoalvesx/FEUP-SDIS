package subprotocols.workers;

import service.Peer;
import messages.Message;
import filesystem.Chunk;

import java.io.IOException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReclaimWorker implements Runnable {

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

        

		System.out.println("Reclaim Worker!");
	}



}