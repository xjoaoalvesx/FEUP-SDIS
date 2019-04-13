package subprotocols.workers;

import service.Peer;
import messages.Message;
import filesystem.Chunk;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DeleteWorker implements Runnable {

	private Peer parent_peer;
	private Message message;
	private String fileId;

	public DeleteWorker(Peer parent_peer, Message message){
		this.parent_peer = parent_peer;
		this.message = message;
		this.fileId = message.getFileId();
	}	

	@Override
	public void run(){

		if(!parent_peer.getPeerSystemManager().hasChunksofFile(this.fileId)){
			System.out.println("File has no chunks backed up! Cancelling DELETE ...");
			return;
		}

		ConcurrentMap<Integer, Chunk> backup_chunks_map = parent_peer.getPeerSystemManager().removeChunksOfFileBackup(this.fileId);

		for (Chunk chunk : backup_chunks_map.values()){
			parent_peer.getPeerSystemManager().removeChunkFromSystem(chunk.getFileID(), Integer.toString(chunk.getID()));
		}

		System.out.println("DeleteWorker!");
	}



}