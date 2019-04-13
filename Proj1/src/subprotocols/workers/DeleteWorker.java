package subprotocols.workers;

import service.Peer;
import messages.Message;
import filesystem.Chunk;

import java.io.IOException;

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

		//removes chunks of current file to delete
		ConcurrentMap<Integer, Chunk> backup_chunks_map = parent_peer.getPeerSystemManager().removeChunksOfFileBackup(this.fileId);
		System.out.println(backup_chunks_map.size());

		try{
			parent_peer.getPeerSystemManager().removeDirFromSystem(this.fileId);
		}catch(IOException e){
			System.out.println("Error removing folder");
		}
		

		System.out.println("DeleteWorker!");
	}



}