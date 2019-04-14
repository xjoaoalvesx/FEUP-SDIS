package messages;

import service.Peer;
import messages.Message;
import filesystem.Chunk;
import subprotocols.workers.RestoreWorker;
import subprotocols.workers.DeleteWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;



import static filesystem.PeerSystemManager.createDirectories;

public class MessageHandler implements Runnable {


	private Peer parent_peer;
	private Message message;
    private ArrayList<Runnable> message_handlers;
    private int handler_index;
    private Future scheduledHandler = null;
    private Random random;
    private static final String ENHANCEMENT = "2.0";

	public MessageHandler(Peer parent_peer, Message msg){
		this.parent_peer = parent_peer;
		this.message = msg;
        this.random = new Random();

        message_handlers = new ArrayList<Runnable> ();
        message_handlers.add(this::handle_putchunk);
        message_handlers.add(this::handle_stored);
        message_handlers.add(this::handle_getchunk);
        message_handlers.add(this::handle_chunk);
        message_handlers.add(this::handle_delete);

        switch(msg.getMessageType()){
            case "PUTCHUNK":
                this.handler_index = 0;
                break;
            case "STORED":
                this.handler_index = 1;
                break;
            case "GETCHUNK":
                this.handler_index = 2;
                break;   
            case "CHUNK":
                this.handler_index = 3;
                break;
            case "DELETE":
                this.handler_index = 4;
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

    	String chunk_path = "peers/Peer" + this.parent_peer.getId() + "/backup/" + fileId;

        Message stored = createSTORED(message.getVersion(), Integer.toString(parent_peer.getId()), fileId, chunkNo);
        System.out.println(message.getVersion());
        if(message.getVersion().equals(ENHANCEMENT)){
            System.out.println("ENHANCEMENT");
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                if (parent_peer.getPeerSystemManager().getDegree(fileId, chunkNo) < replicationDeg){
                    createDirectories(chunk_path);
                    boolean chunk_save = saveChunk(fileId, chunkNo, replicationDeg, chunk, chunk_path);
                        if(chunk_save){
                            try {
                                System.out.println("sending stored");
                                parent_peer.sendMessageMC(stored);
                            } catch (IOException e) {
                            System.out.println("Error: Could not send message(STORED) to MC channel!");
                            }
                        }
                }
            }, random.nextInt(400), TimeUnit.MILLISECONDS);
        }

        else {
            createDirectories(chunk_path);
        // normal
            boolean s = saveChunk(fileId, chunkNo, replicationDeg, chunk, chunk_path);

            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            try {
               parent_peer.sendMessageMC(stored);
            } catch (IOException e) {
               System.out.println("Error: Could not send message(STORED) to MC channel!");
            }
            },  random.nextInt(400), TimeUnit.MILLISECONDS);
        //normal
        }

    }

    private void handle_stored(){
        parent_peer.getPeerSystemManager().incDegree(message.getFileId(), message.getChunkNo(), message.getSenderId());
    }

    private void handle_getchunk(){
        Thread worker = new Thread(new RestoreWorker(parent_peer, message));
        worker.start();
    }

    private void handle_chunk(){

        if(!parent_peer.getPeerSystemManager().getRestoringState(message.getFileId())){
            return;
        }

        parent_peer.getPeerSystemManager().addChunkToFileRestore(new Chunk(Integer.parseInt(message.getChunkNo()), message.getFileId()
            , message.getBody() , 1));

        System.out.println("CHUNK HANDLER");
    }

    private void handle_delete(){
        Thread worker = new Thread(new DeleteWorker(parent_peer, message));
        worker.start();
    }


    private boolean saveChunk(String fileId, String chunkNo, int replicationDeg, byte[] chunk, String chunk_path){

    	try {
    		parent_peer.getPeerSystemManager().saveFile(chunkNo, chunk_path, chunk);
        } catch (IOException e) {
            System.out.println("Fail saving the chunk!");
            return false;
        }

        System.out.println("savechunk");

        parent_peer.getPeerSystemManager().addBackupChunk(new Chunk(Integer.parseInt(chunkNo), fileId, chunk, replicationDeg));
        return true;
    }

    private Message createSTORED(String version, String senderId, String fileId, String chunkNo){
        return new Response("STORED", version, senderId, fileId, chunkNo);
    }

    
}