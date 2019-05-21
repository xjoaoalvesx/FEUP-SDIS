package network;

import java.io.*;
import java.rmi.*;
import java.util.concurrent.*;


/*
 *  This interface identifies a Chord Node in a p2p network
 */
public interface PeerNode extends Remote {
	
	Key getNodeKey();

	PeerNode successor();
	
	PeerNode predecessor();

	Future lookup(Key key);

	void put(Key key, Serializable object);



}