package subprotocols;

import service.Peer;

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
    	System.out.println("backup run");
    }

    private Message create_putchunk(Chunk chunk, String version){
    	return new Message("PUTCHUNK", )
    }

}