/**
 *@filename		VpnNetworkSub.java
 *@version
 *@author		fisher
 *@createtime	2008-9-12 PM 01:35:37
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * @author		fisher
 * @version		V1.0.0.0 
 */
@Embeddable
public class VpnNetworkClassification implements Serializable{
	
	private static final long	serialVersionUID	= 1L;
	
	private int key;
	
	private String ipAddress;
	
	private int tag;
	
	private String tagValue;

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public String getTagValue() {
		return tagValue;
	}

	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}
	
	public String getTagString(){
		switch (tag) {
			case 1 : return "Tag1";
			case 2 : return "Tag2";
			case 3 : return "Tag3";
			default: return "Tag0";
		}
	}
	
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof VpnNetworkClassification))
			return false;

		final VpnNetworkClassification item = (VpnNetworkClassification) o;

		if (key!=item.key) {
			return false;
		}

		if (ipAddress!=null && ipAddress.length()>0 && item.ipAddress!=null && item.ipAddress.length()>0) {
			if (!ipAddress.equals(item.ipAddress)) {
				return false;
			}
		}

		if (tag!=item.tag) {
			return false;
		}

		if ((tagValue != null && tagValue.length() > 0) ? !tagValue.equalsIgnoreCase(item.tagValue)
				: (item.tagValue != null && item.tagValue.length() > 0)) {
			return false;
		}
		
		return true;
	}

	public int hashCode() {
		int result;
		result = key;
		result = 29 * result + tag;
		return result;
	}
	
	@Override
	public VpnNetworkClassification clone() {
		try {
			return (VpnNetworkClassification) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
	
}