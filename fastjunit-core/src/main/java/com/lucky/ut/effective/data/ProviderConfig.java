package com.lucky.ut.effective.data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhourj
 * @date 2020/9/18 10:54
 * 数据供给器的配置
 */
public class ProviderConfig {
	public static byte[] byteRange = {0, 127};
	public static boolean[] booleanSeed = {true, false};
	public static short[] shortRange = {0, 1000};
	public static int[] intRange = {0, 10000};
	public static Integer stringSize =32;
	public static float[] floatRange = {0.0f, 10000.00f};
	public static double[] doubleRange = {0.0, 10000.00};
	public static int decimalScale = 2;
	public static long[] longRange = {0L, 10000L};
	public static String[] dateRange = {"1970-01-01", "2100-12-31"};
	public static int[] timeRange = {0,24,0,60,0,60};
	public static int[] sizeRange = {1, 10};

	/**
	 * enum缓存
	 */
	private static Map<String, Enum[]> enumCache = new HashMap<>();

	public static char[] charSeed =
			{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
					'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
					'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	public static String[] stringSeed =
			{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
					"l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F",
					"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};



	public static void cacheEnum(String name, Enum[] enums) {
		enumCache.put(name, enums);
	}

	public static Enum[] getcacheEnum(String enumClassName) {
		return enumCache.get(enumClassName);
	}
}
