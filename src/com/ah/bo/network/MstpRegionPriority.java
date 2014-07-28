package com.ah.bo.network;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Range;

@Embeddable
public class MstpRegionPriority implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1259028869395498736L;

	public static final int DEFAULT_PRIORITY = -1;
	
	private short instance;
	private int priority = DEFAULT_PRIORITY;
	private String vlan;
	public static final int BASE_PRIORITY = 4096;
	
	@Transient
	@Range (min = MIN_TIMES, max = MAX_TIMES)
	private short times = MIN_TIMES;
	public static final short MAX_TIMES = 15;
	public static final short MIN_TIMES = 0;
	
	@Transient
	private List<String> vlanList;
	
	public short getInstance() {
		return instance;
	}
	public void setInstance(short instance) {
		this.instance = instance;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getVlan() {
		return vlan;
	}
	public void setVlan(String vlan) {
		this.vlan = vlan;
	}
	public List<String> getVlanList() {
		return vlanList;
	}
	public void setVlanList(List<String> vlanList) {
		this.vlanList = vlanList;
	}
	public short getTimes() {
		this.times = (short) (this.priority / BASE_PRIORITY);
		return times;
	}
	public void setTimes(short times) {
		this.times = times;
		this.setPriority(BASE_PRIORITY * times);
	}
}