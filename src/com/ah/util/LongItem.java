package com.ah.util;

import java.io.Serializable;

public class LongItem implements Serializable {
	private static final long serialVersionUID = 1L;

	protected long key;

	protected long value;

	public LongItem(long key, long value) {
		this.key = key;
		this.value = value;
	}

	public long getKey() {
		return key;
	}

	public long getValue() {
		return value;
	}
}
