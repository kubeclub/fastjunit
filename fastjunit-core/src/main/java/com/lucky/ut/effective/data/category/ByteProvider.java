package com.lucky.ut.effective.data.category;

import com.lucky.ut.effective.utils.RandomUtils;

/**
 * @author zhourj
 * @date 2020/9/18 10:58
 */
public class ByteProvider implements Provider<Byte> {
	@Override
	public Byte generate() {
		return RandomUtils.nextByte();
	}
}
