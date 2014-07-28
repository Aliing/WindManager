package com.ah.util;

public class CheckItem2 extends CheckItem {
	
	private boolean defaultFlag;
	
	private boolean checked;
	
	public CheckItem2(Long id, String value) {
		this(id, value, false);
	}
	
	public CheckItem2(Long id, String value, boolean checked) {
		this(id, value, checked, false);
	}
	
	public CheckItem2(Long id, String value, boolean checked, boolean defaultFlag) {
		super(id, value);
		this.checked = checked;
		this.defaultFlag = defaultFlag;
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
