package com.lucky.ut.effective.data.category;

import com.lucky.ut.effective.data.ProviderConfig;
import com.lucky.ut.effective.utils.RandomUtils;

/**
 * @author zhourj
 * @date 2020/9/18 10:58
 */
public class BooleanProvider{
	public static Boolean generate() {
		boolean[] seed = ProviderConfig.booleanSeed;
		Integer index =  RandomUtils.nextInt(0, 2);
		return seed[index];
	}
}
