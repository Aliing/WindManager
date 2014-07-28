package com.ah.util;

import java.io.Serializable;

public class TextItem implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String key;

	protected String value;

	protected String toopTip;
	
	public TextItem(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public TextItem(String key, String value, String toolTip) {
		this.key = key;
		this.value = value;
		this.toopTip = toolTip;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
	/**
	 * @return the toopTip
	 */
	public String getToopTip() {
		return toopTip;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TextItem))
			return false;

		final TextItem textItem = (TextItem) o;

		if (null != key && !key.equals(textItem.getKey()))
			return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (value != null ? value.hashCode() : 0);
		result = 29 * result + key.hashCode();
		return result;
	}

	public void setToopTip(String toopTip) {
		this.toopTip = toopTip;
	}


}
