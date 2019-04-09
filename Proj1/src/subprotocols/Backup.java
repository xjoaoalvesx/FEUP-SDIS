package subprotocols;

import service.Peer;
import filesystem.Chunk;
import messages.Message;

public class Backup implements Runnable{

	private Peer parent_peer;
	private String version;
	private String path;
	private int replicationDegree;

	public Backup(Peer parent_peer, String version, String path, int replicationDegree){
		this.parent_peer = parent_peer;
		this.version = version;
		this.path = path;
		this.replicationDegree = replicationDegree;

		System.out.println("The Backup Started!");
	}

	@Override
    public void run() {
    	//enviar putchunks
    	System.out.println("backup run");
    }

    private Message create_putchunk_message(Chunk chunk, String version){
    	return new Message("PUTCHUNK", version, Integer.toString(parent_peer.getId()), 
    		chunk.getFileID().toString(), Integer.toString(chunk.getID()), Integer.toString(chunk.getReplicationDegree()), chunk.getChunkData());
    }

}