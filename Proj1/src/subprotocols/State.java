package subprotocols;

public class State implements Runnable {

	private Peer parent_peer;
	private PeerSystemManager peer_manager;

	public State(Peer parent_peer){
		this.parent_peer = parent_peer;
		this.peer_manager = parent_peer.getPeerSystemManager();
	}

	@Override
	public void run(){

	}


}