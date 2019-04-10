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
    	handle_putchunk(message);
    }

    private void handle_putchunk(Message msg){
    	int senderId = Integer.parseInt(msg.getSenderId());
    	String fileId = msg.getFileId();
    	String chunkNo = msg.getChunkNo();
        System.out.println(msg.getReplicationDeg());
    	int replicationDeg = Integer.parseInt(msg.getReplicationDeg());

        System.out.println(Integer.toString(this.parent_peer.getId()));
    	if (senderId == this.parent_peer.getId()){
    		return;
    	}

    	byte[] chunk = msg.getBody();

    	String chunck_path = "src/filesystem/Peer" + this.parent_peer.getId() + "/" + fileId;

        System.out.print(chunck_path);
    	createDirectories(chunck_path);

        System.out.println("pilas");
    	saveChunk(fileId, chunkNo, replicationDeg, chunk, chunck_path);


    }

    private void saveChunk(String fileId, String chunkNo, int replicationDeg, byte[] chunk, String chunk_path){

        System.out.println("loles");
    	try {
            System.out.println("TRY SAVE");
    		parent_peer.getPeerSystemManager().saveFile(chunkNo, chunk_path, chunk);
        } catch (IOException e) {
            System.out.println("Fail saving the chunk!");
        }

    }
}