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

        int actual_size = this.parent_peer.getMaxSpace();

        if(this.desired_size > actual_size){
            this.parent_peer.setMaxSpace(this.desired_size);
            parent_peer.getPeerSystemManager().setAvailableSpace();
        }else if (this.desired_size < actual_size){
            if(actual_size - this.desired_size <= this.parent_peer.getAvailableSpace()){
                this.parent_peer.setMaxSpace(this.desired_size);
                this.parent_peer.getPeerSystemManager().setAvailableSpace();
            }else{
                removeNewestFiles();
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
        
        removeUntilSpaceFree(file_times);

    }

    //find the newest chunk and remove it, repeat until the desired space is freed
    private void removeUntilSpaceFree(ArrayList<ArrayList<String>> file_times){
        ArrayList<String> newest = new ArrayList<String>();
        ArrayList<ArrayList<String>> temp = file_times;

        for(ArrayList<String> arr : temp){
            if(newest.size() == 0){
                newest = arr;
            }else if(Integer.parseInt(newest.get(0)) < Integer.parseInt(arr.get(0))){
                newest = arr;
            }
        }

        this.parent_peer.getPeerSystemManager().removeChunkFromSystem(newest.get(1), newest.get(2));
        temp.remove(newest);
        File chunks_folder = new File(this.parent_peer.getPeerSystemManager().getPath() + newest.get(1));
        if(chunks_folder.listFiles().length == 0 && newest.get(1) != null){
            try{
                this.parent_peer.getPeerSystemManager().removeDirFromSystem(newest.get(1));  
            }catch (IOException e) {
                System.out.println("Error removing file: " + newest.get(1));
            }
        }
        sendREMOVEDrequest(newest.get(1), Integer.parseInt(newest.get(2)));

        this.parent_peer.getPeerSystemManager().setAvailableSpace();
        int actual_size = this.parent_peer.getMaxSpace();

        if(actual_size - this.desired_size <= this.parent_peer.getAvailableSpace()){
            this.parent_peer.setMaxSpace(this.desired_size);
            this.parent_peer.getPeerSystemManager().setAvailableSpace();
        }else{
            removeUntilSpaceFree(temp);
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