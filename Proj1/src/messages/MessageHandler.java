package messages;

import service.Peer;
import messages.Message;

import java.io.IOException;
import java.util.ArrayList;

import static filesystem.PeerSystemManager.createDirectories;

public class MessageHandler implements Runnable {


	private Peer parent_peer;
	private Message message;
    private ArrayList<Runnable> message_handlers;
    private int handler_index;

	public MessageHandler(Peer parent_peer, Message msg){
		this.parent_peer = parent_peer;
		this.message = msg;

        message_handlers = new ArrayList<Runnable> ();
        message_handlers.add(this::handle_putchunk);
        message_handlers.add(this::handle_stored);

        switch(msg.getMessageType()){
            case "PUTCHUNK":
                this.handler_index = 0;
                break;
            case "STORED":
                this.handler_index = 1;
                break;

        }
	}

	@Override
    public void run() {
    	message_handlers.get(handler_index).run();
    }

    private void handle_putchunk(){
    	int senderId = Integer.parseInt(message.getSenderId());
    	String fileId = message.getFileId();
    	String chunkNo = message.getChunkNo();
    	int replicationDeg = Integer.parseInt(message.getReplicationDeg());

    	if (senderId == this.parent_peer.getId()){
    		return;
    	}

    	byte[] chunk = message.getBody();
        System.out.println("received msg");
        System.out.println(message.getMessageInBytes().length);

    	String chunk_path = "src/filesystem/Peer" + this.parent_peer.getId() + "/backup/" + fileId;

    	createDirectories(chunk_path);

    	saveChunk(fileId, chunkNo, replicationDeg, chunk, chunk_path);

        Message stored = createSTORED(message.getVersion(), message.getSenderId(), fileId, chunkNo);

        // sleep random 0-400ms
        try{
                parent_peer.sendMessageMC(stored);
            }catch (IOException e){
                System.out.println("Error: Could not send message(STORED) to MC channel!");
            }


    }

    private void saveChunk(String fileId, String chunkNo, int replicationDeg, byte[] chunk, String chunk_path){

    	try {
    		parent_peer.getPeerSystemManager().saveFile(chunkNo, chunk_path, chunk);
        } catch (IOException e) {
            System.out.println("Fail saving the chunk!");
        }

    }

    private Message createSTORED(String version, String senderId, String fileId, String chunkNo){
        return new Response("STORED", version, senderId, fileId, chunkNo);
    }

    private void handle_stored(){
        parent_peer.getPeerSystemManager().incDegree(message.getFileId(), message.getChunkNo(), message.getSenderId());
    }
}