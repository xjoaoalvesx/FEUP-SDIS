package network;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public interface Node {


	InetSocketAddress getLocalAddress();

	void startWorkers();

	public void addPeer(InetSocketAddress peer_add , int idPeer);

	public ArrayList<InetSocketAddress> getPeers();

	public int getId();

	public InetSocketAddress getServerAddress();

	public void addBackupFile(String fileId, InetSocketAddress peer_add);

	public ArrayList<InetSocketAddress> getBackupFilesMap(String fileID);
}
