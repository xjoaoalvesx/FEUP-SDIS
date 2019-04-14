package subprotocols;

import service.Peer;
import filesystem.PeerSystemManager;
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

public class State implements Runnable {

	private Peer parent_peer;

	public State(Peer parent_peer){
		this.parent_peer = parent_peer;
	}

	@Override
	public void run(){
		this.parent_peer.getPeerSystemManager().publishInformation();
	}


}