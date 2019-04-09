package messages;

import service.Peer;
import messages.Message;

import java.io.IOException;

import static filesystem.PeerSystemManager.createDirectories;

public class MessageHandler implements Runnable {


	private Peer parent_peer;
	private Message message;

	public MessageHandler(Peer parent_peer, Message msg){
		this.parent_peer = parent_peer;
		this.message = msg;
	}

	@Override
    public void run() {

    }

    private void handle_putchunk(Message msg){
    	String senderId = msg.getSenderId();
    	String fileId = msg.getFileId();
    	String chunkNo = msg.getChunkNo();
    	int replicationDeg = Integer.parseInt(msg.getReplicationDeg());

    	if (senderId == Integer.toString(this.parent_peer.getId())){
    		return;
    	}

    	byte[] chunk = msg.getBody();

    	String chunck_path = "src/filesystem/Peer" + this.parent_peer.getId() + "/" + fileId;

    	createDirectories(chunck_path);

    	saveChunk(fileId, chunkNo, replicationDeg, chunk, chunck_path);


    }

    private void saveChunk(String fileId, String chunkNo, int replicationDeg, byte[] chunk, String chunk_path){


    	try {
    		parent_peer.getPeerSystemManager().saveFile(chunkNo, chunk_path, chunk);
        } catch (IOException e) {
            System.out.println("Fail saving the chunk!");
        }

    }
}