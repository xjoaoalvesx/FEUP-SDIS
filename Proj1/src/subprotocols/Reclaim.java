package subprotocols;

import service.Peer;
import messages.Message;
import messages.Response;
import messages.Getchunk;
import filesystem.Chunk;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

import static filesystem.PeerSystemManager.joinChunks;


public class Reclaim implements Runnable {

	private Peer parent_peer;
	private int desired_size;

	public Reclaim(Peer parent_peer, int size){

		this.parent_peer = parent_peer;
		this.desired_size = size;

	}

	@Override
    public void run(){

        int current_max_size = this.parent_peer.getMaxSpace();
        int current_free_space = this.parent_peer.getPeerSystemManager().setAvailableSpace();

        if(this.desired_size > current_max_size){
            this.parent_peer.setMaxSpace(this.desired_size);
            parent_peer.getPeerSystemManager().setAvailableSpace();
        }else if (this.desired_size < current_max_size){
            if(current_max_size - current_free_space <= this.desired_size){
                this.parent_peer.setMaxSpace(this.desired_size);
                this.parent_peer.getPeerSystemManager().setAvailableSpace();
            }else{
                this.removeNewestFiles();
            }
        }

        System.out.println("Space Restored");

    }

    private void removeNewestFiles(){

        
        String peer_directory_backup = this.parent_peer.getPeerSystemManager().getPath() + "/backup"; 
        File backup_dir = new File(peer_directory_backup); 
        File[] dirs = backup_dir.listFiles();
        ArrayList<ArrayList<String>> file_times = new ArrayList<ArrayList<String>>();
 
        for(File dir : dirs){ 
            File[] chunks = dir.listFiles(); 
            for(File chunk : chunks){
                ArrayList<String> file_info = new ArrayList<String>();
                file_info.add(String.valueOf(chunk.lastModified()));
                file_info.add(dir.getName());
                file_info.add(chunk.getName());
                file_times.add(file_info);
            } 
        }

        this.removeUntilSpaceFree(file_times);

    }

    //find the newest chunk and remove it, repeat until the desired space is freed
    private void removeUntilSpaceFree(ArrayList<ArrayList<String>> file_times){

        
        ArrayList<String> newest = new ArrayList<String>();
        ArrayList<ArrayList<String>> temp = file_times;

        for(int i = 0; i < temp.size(); i++){
            ArrayList<String> arr = temp.get(i);
            
            if(newest.size() == 0){
                newest = new ArrayList<String>(arr);
            }else if(Long.parseLong(newest.get(0)) <= Long.parseLong(arr.get(0))){
                newest = new ArrayList<String>(arr);
            }

        }

        this.parent_peer.getPeerSystemManager().removeChunkFromPeer(newest.get(1), newest.get(2));
        temp.remove(newest);
        File chunks_folder = new File(this.parent_peer.getPeerSystemManager().getPath() + "backup/" + newest.get(1));
        
        
        if(chunks_folder.listFiles().length == 0 && newest.get(1) != null){
            this.parent_peer.getPeerSystemManager().removeDirFromPeer(newest.get(1));  
            
        }
        sendREMOVEDrequest(newest.get(1), Integer.parseInt(newest.get(2)));

        this.parent_peer.getPeerSystemManager().setAvailableSpace();
        int actual_size = this.parent_peer.getMaxSpace();


        if(actual_size - this.parent_peer.getAvailableSpace() <= this.desired_size){
            this.parent_peer.setMaxSpace(this.desired_size);
            this.parent_peer.getPeerSystemManager().setAvailableSpace();
        }else{
            this.removeUntilSpaceFree(temp);
        }

    }


    private void sendREMOVEDrequest(String fileId, int chunkNo){

        Message m = new Response("REMOVED", "1.0", Integer.toString(parent_peer.getId()), fileId, Integer.toString(chunkNo));

        try{
            parent_peer.sendMessageMC(m);
        }catch(IOException e){
            System.out.println("Error sending message to MC channel (REMOVED) !");
        }
    }

    public Peer getParentPeer(){
    	return parent_peer;
    }

}