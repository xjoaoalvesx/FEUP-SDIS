package util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Util {



	public static int generateID(){
		int max = (int) (Math.pow(2, Configuration.BIT_RANGE) - 1) // peer id -> number between 0 and (2^m - 1)
		return generateRandomInt(0, max);
	}

	public static int generateRandomInt(int least, int bound){
		return ThreadLocalRandom.current().nextInt(least, bound)
	}

	

}