package subprotocols;

import service.Peer;

public class Restore implements Runnable {

	private Peer parent_peer;
	private String version;
	private String file_path;
	
	public Restore(Peer parent_peer, String version, String filePath){

		this.parent_peer = parent_peer;
		this.version = version;
		this.file_path = filePath;

	}

	@Override
    public void run(){


    	for(int i = 0; i < parent_peer.getPeerSystemManager().getNumChunks(file_path); i++){
    		System.out.println(i);
    	}





    	System.out.println("Restore run");

    }

}