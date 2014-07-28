package com.ah.util;

import java.io.Serializable;

public class EnumItem implements Serializable,Comparable<EnumItem> {

	private static final long serialVersionUID = 1L;

	protected int key;

	protected String value;

	public EnumItem(int key, String value) {
		this.key = key;
		this.value = value;
	}

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof EnumItem))
			return false;

		final EnumItem	enumItem = (EnumItem)o;

		if (key != enumItem.getKey())
			return false;

		return true;
	}

	public int hashCode()
	{
		int result;
		result = (value != null ? value.hashCode() : 0);
		result = 29 * result + key;
		return result;
	}
	@Override
	public int compareTo(EnumItem o) {
		return key - o.getKey();
	}
}