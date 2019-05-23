package network;


/*
 *  This interface identifies a Chord Node in a p2p network
 */
public interface PeerNode extends Remote {
	
	Key getNodeKey();

	PeerNode findSuccessor();
	
	PeerNode findPredecessor();

	Future lookup(Key key);

	void put(Key key, Serializable object);



}