package network;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public interface Node {
	

	InetSocketAddress getLocalAddress();

	void startWorkers();

	public void addPeer(InetSocketAddress peer_add , int idPeer);

	public ArrayList<InetSocketAddress> getPeers();

	public int getId();

}