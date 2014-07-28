package com.ah.util;

import java.io.Serializable;

public class BooleanItem implements Serializable {
	private static final long serialVersionUID = 7268825958134427722L;
	
	protected boolean key;
	
	protected String value;
	
	public BooleanItem(boolean key, String value) {
		setKey(key);
		setValue(value);
	}

	public boolean isKey() {
		return key;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
