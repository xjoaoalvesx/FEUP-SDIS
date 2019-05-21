package network;

public final class Key implements Serializable {
	
	// private static final long serialVersionUID = 
	
	public static final byte SIZE = 32;

	// peer id shoud be between 0 and 2^(m) -> 2^key_size
	public static final int MIN = 0;
	public static final int MAX = (int) Math.pow(2, SIZE);

	public final long key_value;

	public Key(final long key)
}