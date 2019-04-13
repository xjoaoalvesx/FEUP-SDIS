package subprotocols;

import service.Peer;
import filesystem.PeerSystemManager;
import messages.Message;

import java.io.IOException;

public class Delete implements Runnable {
	
	private Peer parent_peer;
	private String version;
	private String file_path;
	private String fileId;

	public Delete(Peer parent_peer, String version, String filePath){

		this.parent_peer = parent_peer;
		this.version = version;
		this.file_path = filePath;
		this.fileId = parent_peer.getPeerSystemManager().getFileIdbyPath(file_path);

	}

	@Override
    public void run(){

    	if(fileId == null){
            System.out.println("DELETE canceled: file does not exist !");
            return;
        }

        sendDELETErequest();

        parent_peer.getPeerSystemManager().removeFileToRestore(file_path);

    	System.out.println("Delete run");
    }

    private void sendDELETErequest(){

    	Message m = new Message("DELETE", version, Integer.toString(parent_peer.getId()), fileId, "", "");

    	try{
            parent_peer.sendMessageMC(m);
        }catch(IOException e){
            System.out.println("Error sending message to MC channel (DELETE) !");
        }
    }
    

}