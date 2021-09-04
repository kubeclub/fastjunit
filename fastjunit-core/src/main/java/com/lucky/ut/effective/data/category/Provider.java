package com.lucky.ut.effective.data.category;


/**
 * @author zhourj
 * @date 2020/9/18 11:02
 */
public interface Provider<T> {
	/**
	 * 生成数据
	 * @return
	 */
	T generate();
}
