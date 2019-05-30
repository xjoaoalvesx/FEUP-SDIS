package network;

import java.net.InetSocketAddress;

public interface Node {
	

	InetSocketAddress getLocalAddress();

	void startWorkers();

}