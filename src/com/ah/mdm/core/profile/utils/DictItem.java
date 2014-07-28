package com.ah.mdm.core.profile.utils;

import java.io.Serializable;

public class DictItem implements Serializable {
	
	private static final long serialVersionUID = -3742699948243550475L;

	private String keyCode;
	
	private String valueCode;

	public String getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}

	public String getValueCode() {
		return valueCode;
	}

	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}
	
	
}
