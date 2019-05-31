package service;

import network.Peer;
import network.Server;

public class Delete implements Runnable{
	
	private String file_path;
	private String fileId;

	public Delete(Peer peer, String filePath){

		this.peer = peer;
		this.file_path = filePath;
		this.fileId = //TODO get id from system

	}


	public void run(){
		if(fileId == null){
		    System.out.println("DELETE canceled: file does not exist !");
		    return;
		}

		sendDELETErequest();

		parent_peer.getPeerSystemManager().removeFileToRestore(file_path);

	    	System.out.println("Delete run");
	}

	// TODO send delete
    	private void sendDELETErequest(){

   	}
}
