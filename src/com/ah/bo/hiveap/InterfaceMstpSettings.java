package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Range;

@Embeddable
public class InterfaceMstpSettings implements Serializable {

	public InterfaceMstpSettings() {

	}

	private static final long serialVersionUID = -4363453370204533030L;

	public static final short DEVICE_DEFAULT_PRIORITY = 128;
	public static final short DEVICE_DEFAULT_PATH_COST = 128;
	public static final short MAX_TIMES = 15;
	public static final short MIN_TIMES = 0;
	public static final short BASE_PRIORITY = 16;
	public static final short DEFAULT_PRIORITY = -1;
	
	private int devicePriority = DEVICE_DEFAULT_PRIORITY;
	private int devicePathCost = DEFAULT_PRIORITY;
	private short interfaceNum;
	private short instance;

	@Transient
	private String devicePortName;
	
	@Transient
	private String defaultPathCost;
	
	@Transient
	@Range(min = MIN_TIMES, max = MAX_TIMES)
	private short times;

	@Transient
	private boolean portChannelMemberPort;

	public boolean isPortChannelMemberPort() {
		return portChannelMemberPort;
	}

	public void setPortChannelMemberPort(boolean portChannelMemberPort) {
		this.portChannelMemberPort = portChannelMemberPort;
	}

	public short getInstance() {
		return instance;
	}

	public void setInstance(short instance) {
		this.instance = instance;
	}

	public String getDefaultPathCost() {
		if (this.getDevicePathCost() == DEFAULT_PRIORITY) {
			defaultPathCost = "";
		} else {
			this.defaultPathCost = Integer.toString(this.getDevicePathCost());
		}
		return defaultPathCost;
	}

	public void setDefaultPathCost(String defaultPathCost) {
		this.defaultPathCost = defaultPathCost;
		if ("".equals(defaultPathCost)) {
			this.setDevicePathCost(DEFAULT_PRIORITY);
		} else {
			this.setDevicePathCost(Integer.parseInt(defaultPathCost));
		}
	}

	public int getDevicePriority() {
		return devicePriority;
	}

	public void setDevicePriority(int devicePriority) {
		this.devicePriority = devicePriority;
	}

	public int getDevicePathCost() {
		return devicePathCost;
	}

	public void setDevicePathCost(int devicePathCost) {
		this.devicePathCost = devicePathCost;
	}

	public short getInterfaceNum() {
		return interfaceNum;
	}

	public void setInterfaceNum(short interfaceNum) {
		this.interfaceNum = interfaceNum;
	}

	public String getDevicePortName() {
		return devicePortName;
	}

	public void setDevicePortName(String devicePortName) {
		this.devicePortName = devicePortName;
	}

	public short getTimes() {
		this.times = (short) (this.devicePriority / BASE_PRIORITY);
		return times;
	}

	public void setTimes(short times) {
		this.times = times;
		this.setDevicePriority(BASE_PRIORITY * times);
	}
}