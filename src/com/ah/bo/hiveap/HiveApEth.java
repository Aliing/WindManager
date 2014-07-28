package com.ah.bo.hiveap;

import javax.persistence.Embeddable;

/*
 * modification history
 * 
 * add field 'bindInterface' and 'bindRole'
 * joseph chen, 04/10/2008
 * 
 */

@Embeddable
public class HiveApEth implements AhInterface {

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_IDEL_TIMEOUT = 180;

	private short operationMode;

	private short adminState;

	private short speed;

	private short duplex;

	private short bindInterface;

	private short bindRole;

//	private boolean useDefaultSettings = true;

	/*
	 * Unused this field from 3.5, it should be enabled by default when
	 * operation mode is bridge, otherwise disabled.
	 */
	private boolean macLearningEnabled = false;

	private int idelTimeout = DEFAULT_IDEL_TIMEOUT;

	public static final String ALLOWED_VLAN_AUTO = "auto";

	public static final String ALLOWED_VLAN_ALL = "all";

	private String allowedVlan = ALLOWED_VLAN_ALL;
	
	public static final int MULTIPLE_NATIVE_VLAN_DEFAULT = 1;

	private Integer multiNativeVlan;

	public short getOperationMode() {
		return operationMode;
	}

	public void setOperationMode(short operationMode) {
		this.operationMode = operationMode;
	}

	public short getAdminState() {
		return adminState;
	}

	public void setAdminState(short adminState) {
		this.adminState = adminState;
	}

	public short getSpeed() {
		return speed;
	}

	public void setSpeed(short speed) {
		this.speed = speed;
	}

	public short getDuplex() {
		return duplex;
	}

	public void setDuplex(short duplex) {
		this.duplex = duplex;
	}

	public short getBindInterface() {
		return bindInterface;
	}

	public void setBindInterface(short bindInterface) {
		this.bindInterface = bindInterface;
	}

	public short getBindRole() {
		return bindRole;
	}

	public void setBindRole(short bindRole) {
		this.bindRole = bindRole;
	}

//	public boolean isUseDefaultSettings() {
//		return useDefaultSettings;
//	}
//
//	public void setUseDefaultSettings(boolean useDefaultSettings) {
//		this.useDefaultSettings = useDefaultSettings;
//	}

	public boolean isMacLearningEnabled() {
		return macLearningEnabled;
	}

	public void setMacLearningEnabled(boolean macLearningEnabled) {
		this.macLearningEnabled = macLearningEnabled;
	}

	public int getIdelTimeout() {
		return idelTimeout;
	}

	public void setIdelTimeout(int idelTimeout) {
		this.idelTimeout = idelTimeout;
	}

	public String getAllowedVlan() {
		return allowedVlan;
	}

	public void setAllowedVlan(String allowedVlan) {
		this.allowedVlan = allowedVlan;
	}

	public void setMultiNativeVlan(Integer multiNativeVlan) {
		this.multiNativeVlan = multiNativeVlan;
	}

	public Integer getMultiNativeVlan() {
		return multiNativeVlan;
	}

}