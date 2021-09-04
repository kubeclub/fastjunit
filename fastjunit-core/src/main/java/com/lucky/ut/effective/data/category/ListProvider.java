package com.lucky.ut.effective.data.category;

import com.lucky.ut.effective.data.ProviderConfig;
import com.lucky.ut.effective.utils.RandomUtils;
import org.testng.collections.Lists;

import java.util.List;

/**
 * @author zhourj
 * @date 2020/9/18 13:52
 */
public class ListProvider<T>{
	Class<T> clazz;
	ProviderConfig providerConfig;
	public ListProvider(Class<T> clazz){
		this.clazz = clazz;
		this.providerConfig = new ProviderConfig();
	}

	public List<T> generate() {
		int size = RandomUtils.nextSize(ProviderConfig.sizeRange[0], ProviderConfig.sizeRange[1]);
		List<T> result =  Lists.newArrayList(size);
		for (int index = 0; index < size; index++) {
			result.add(new ObjectProvider<T>(clazz).generate());
		}
		return result;
	}
}
