package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class HiveApPreferredSsid implements Serializable {

	private static final long serialVersionUID = 1L;
	private int priority;
	private Long preferredId;
	
	@Transient
	private WifiClientPreferredSsid preferredSsid;
	
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public Long getPreferredId() {
		return preferredId;
	}
	public void setPreferredId(Long preferredId) {
		this.preferredId = preferredId;
	}
	
	public WifiClientPreferredSsid getPreferredSsid() {
		return preferredSsid;
	}
	public void setPreferredSsid(WifiClientPreferredSsid preferredSsid) {
		this.preferredSsid = preferredSsid;
	}
}
