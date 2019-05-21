package network;

public class Peer implements PeerNode {

	private Key key;
	private InetSocketAddress nodeAdress;
	private Peer predecessor;

	private ConcurrentHashMap<Integer, InetSocketAdress> fingers;
	
	



}