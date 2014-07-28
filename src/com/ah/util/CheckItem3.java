package com.ah.util;

public class CheckItem3 {
	protected String id;

	protected String value;

	public CheckItem3(String id, String value) {
		this.id = id;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CheckItem3)) {
			return false;
		}
		return id.equals(((CheckItem3) other).getId());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}

