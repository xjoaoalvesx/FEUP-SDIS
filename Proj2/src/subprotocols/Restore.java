package service;

import network.Peer;
import network.Server;

public class Restore implements Runnable{
	
	private Peer peer;
	private String file_id;
	private String file_path;

	public Restore(Peer peer, String file_id, String file_path){
		this.peer = peer;
		this.file_id = file_id;
		this.file_path = file_path;
	}

	public void run(){
		if(fileId == null){
		    System.out.println("File not available to RESTORE!");
		    return;
		}
		
		// TODO function that gets number of chunks
	    	int num_chunks = 0;
		
		// ask where is all the stuff to the server Change bellow accordingly
	    	for(int i = 0; i < num_chunks; i++){
	    		sendGETCHUNKrequest(i);
	    	}

		while(!parent_peer.finishedRestoringFile(file_path, fileId)){
		    Thread.yield();
		}

		System.out.println("All chunks have been received!");

	   

		try {
		    String chunks_path = "peers/Peer" + peer.getId() + "/restored";
		    Path path = Paths.get(file_path);
		    String name = path.getFileName().toString();
		    //join chuncks functions TODO

		}catch(IOException e){
		    System.out.println("Failed saving file");
		}


    	}


	private void sendGETCHUNKrequest(){

	}
}
