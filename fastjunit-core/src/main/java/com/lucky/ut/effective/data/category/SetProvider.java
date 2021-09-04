package com.lucky.ut.effective.data.category;

import com.lucky.ut.effective.data.ProviderConfig;
import com.lucky.ut.effective.utils.RandomUtils;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhourj
 * @date 2020/9/18 13:52
 */
public class SetProvider {

	public static Set generate(Class clazz) {
		int size = RandomUtils.nextSize(ProviderConfig.sizeRange[0], ProviderConfig.sizeRange[1]);
		Set<Object> result =  new HashSet();
		for (int index = 0; index < size; index++) {
			Object item = new ObjectProvider(clazz).generate();
			result.add(item);
		}
		return result;
	}
}
