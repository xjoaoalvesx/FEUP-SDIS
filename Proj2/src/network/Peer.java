package network;

public class Peer implements PeerNode {

	private Key key;
	private InetSocketAddress nodeAdress;
	private InetSocketAddress predecessor;

	private ConcurrentHashMap<Integer, InetSocketAdress> fingers;
	
	



}