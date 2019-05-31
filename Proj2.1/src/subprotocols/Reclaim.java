package service;

import network.Peer;
import network.Server;

public class Reclaim implements Runnable{
	
	private Peer peer;
	private int desired_size;

	public Reclaim(Peer peer, int size){
		this.peer = peer;
		this.desired_size = size;
	}

	public void run(){
		int current_max_size = this.peer.getSpace();
		int current_free_space = this.peer.getAvailableSpace();

		if(this.desired_size > current_max_size){
			this.peer.setSpace(this.desired_size);
		}else if(this.desired_size < current_max_size){
			if(current_max_size - current_free_space <= this.desired_size0){
				this.peer.setSpace(this.desired_size);
			}else{
				this.removeFiles(this.desired_size);
			}
		}
	}
	
	//TODO function that removes files until size is achieved
	private void removeFiles(int size){
		
	}

	private void sendRestoredMessage(){
	
	}
}
