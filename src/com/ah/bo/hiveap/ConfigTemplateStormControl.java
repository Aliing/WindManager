/**
 *@filename		ConfigTemplateStormControl.java
 *@version
 *@author		Wenping
 *@createtime	2012-8-30 PM 07:16:52
 *Copyright (c) 2006-2012 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.util.MgrUtil;

/**
 * @author Wenping
 * @version V1.0.0.0
 */
@Embeddable
public class ConfigTemplateStormControl implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final short STORM_CONTROL_RATE_LIMIT_BYTE=0;
	public static final short STORM_CONTROL_RATE_LIMIT_PACKET=1;
	public static final String STORM_CONTROL_RATE_LIMIT_TYPE_BPS = "KBPS";
	public static final String STORM_CONTROL_RATE_LIMIT_TYPE_PPS = "PPS";
	public static final String STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE = "Percentage";
	public static final long STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID = 0;
	public static final long STORM_CONTROL_RATE_LIMIT_TYPE_PPS_ID = 1;
	public static final long STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE_ID = 2;
	public static final long STORM_CONTROL_RATE_LIMIT_TYPE_BPS_DEFULT_VALUE = 200000;
	public static final long STORM_CONTROL_RATE_LIMIT_TYPE_PPS_DEFULT_VALUE = 200000;
	public static final long STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE_DEFULT_VALUE = 20;

	private short interfaceNum;
	
	private String interfaceType; // for network policy
	
	private boolean allTrafficType;
	
	private boolean broadcast;
	
	private boolean unknownUnicast;
	
	private boolean multicast;
	
	private boolean tcpsyn;
	
	private long rateLimitType = STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID;
	
	private long rateLimitValue;
	
	@Transient
	private String rateLimitRange;
	
	@Transient
	private boolean disableRateLimit;
	
	@Transient
	private int rateLimitValueLength;
	
	@Transient
	public boolean isSFP(short hiveApModel){
		return DeviceInfType.getInstance(interfaceNum, hiveApModel).getDeviceInfType() == DeviceInfType.SFP;
	}
	
	public String getInterfaceName(){
		return MgrUtil.getEnumString("enum.switch.interface." + interfaceNum);
	}
	
	public boolean isAllTrafficType() {
		return allTrafficType;
	}

	public void setAllTrafficType(boolean allTrafficType) {
		this.allTrafficType = allTrafficType;
	}

	public String getInterfaceType() {
		return interfaceType;
	}

	public boolean isBroadcast() {
		return broadcast;
	}

	public boolean isUnknownUnicast() {
		return unknownUnicast;
	}

	public boolean isTcpsyn() {
		return tcpsyn;
	}

	public long getRateLimitValue() {
		return rateLimitValue;
	}

	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
	}

	public void setBroadcast(boolean broadcast) {
		this.broadcast = broadcast;
	}

	public void setUnknownUnicast(boolean unknownUnicast) {
		this.unknownUnicast = unknownUnicast;
	}

	public void setTcpsyn(boolean tcpsyn) {
		this.tcpsyn = tcpsyn;
	}

	public void setRateLimitValue(long rateLimitValue) {
		this.rateLimitValue = rateLimitValue;
	}

	public boolean isMulticast() {
		return multicast;
	}

	public void setMulticast(boolean multicast) {
		this.multicast = multicast;
	}
	
	public long getRateLimitType() {
		return rateLimitType;
	}

	public String getRateLimitRange() {
		return rateLimitRange;
	}

	public int getRateLimitValueLength() {
		return rateLimitValueLength;
	}

	public void setRateLimitType(long rateLimitType) {
		this.rateLimitType = rateLimitType;
	}

	public void setRateLimitRange(String rateLimitRange) {
		this.rateLimitRange = rateLimitRange;
	}

	public void setDisableRateLimit(boolean disableRateLimit) {
		this.disableRateLimit = disableRateLimit;
	}

	public void setRateLimitValueLength(int rateLimitValueLength) {
		this.rateLimitValueLength = rateLimitValueLength;
	}
	
	public short getInterfaceNum() {
		return interfaceNum;
	}

	public void setInterfaceNum(short interfaceNum) {
		this.interfaceNum = interfaceNum;
	}

	public boolean isDisableRateLimit() {
		if(broadcast || multicast || tcpsyn || unknownUnicast){
			return false;
		} else {
			return true;
		}
	}
}