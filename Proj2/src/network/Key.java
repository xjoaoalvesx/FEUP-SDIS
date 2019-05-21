package network;

import java.io.*;


public final class Key implements Serializable {
	
	// private static final long serialVersionUID = 
	
	public static final byte SIZE = 32;

	// peer id shoud be between 0 and 2^(m) -> 2^key_size
	public static final int MIN = 0;
	public static final int MAX = (int) Math.pow(2, SIZE);

	public final long key_value;

	public Key(final long key){

		if(key < MIN){
			throw new IllegalArgumentException("Key must be higher than" + MIN);
		}

		this.key_value = key % MAX;
	}

	/*
	 *	Checks if key is in interval ]lower_bound, upper_bound]
	 * 
	 */
	public boolean IsKeyBetween(final Key lower_bound, final Key upper_bound){
		
		final long lower = lower_bound.key_value;
		final long upper = upper_bound.key_value;

		if(lower < upper){
			return this.key_value > lower && this.key_value < upper;
		}
		else{
			return this.key_value > lower || this.key_value <= upper;
		}
	}

	@Override
	public boolean equals(final Object o){
		if(! (o instanceof 	Key)){
			return false;
		}

		return this == o || this.key_value == ((Key) o).key_value;
	}
}