package com.ursa.tools.amazon.util;

import java.util.Random;

public class RandomNumber {
	public static int getInt(int min, int max) {
	    Random random = new Random();
	    return random.nextInt(max - min) + min;
	}
}
