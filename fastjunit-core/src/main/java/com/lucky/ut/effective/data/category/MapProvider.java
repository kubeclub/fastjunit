package com.lucky.ut.effective.data.category;

import com.lucky.ut.effective.data.ProviderConfig;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhourj
 * @date 2020/9/18 13:52
 */
public class MapProvider{
	ProviderConfig providerConfig;
	Class keyClass = String.class;
	Class ValueClass = String.class;

	public MapProvider(){};
	public MapProvider(Type[] params){
		if(params.length>=2){
			keyClass = (Class)params[0];
			ValueClass = (Class)params[1];
		}
		this.providerConfig = new ProviderConfig();
	}

	public Map generate() {
		Map<String,Object> result = new HashMap(1);
		result.put(new StringProvider().generate(),new ObjectProvider(ValueClass).generate());
		return result;
	}
}
