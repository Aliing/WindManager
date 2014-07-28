package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class USBModemProfile implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String modemName;
	
	private String displayName;

	private String apn;
	
	private String dialupNum;
	
	private String userId;
	
	private String password;
	
	private String osVersion;
	
	public static final short CELLULAR_MODE_AUTO =1;
	public static final short CELLULAR_MODE_2G = 2;
	public static final short CELLULAR_MODE_3G = 3;
	public static final short CELLULAR_MODE_4G = 4;
	
	private short cellularMode;
	
	@Transient
	private String unSupportDevices = "";

	public String getModemName(){
		return this.modemName;
	}
	
	public void setModemName(String modemName){
		this.modemName = modemName;
	}
	
	public String getApn(){
		return this.apn;
	}
	
	public void setApn(String apn){
		this.apn = apn;
	}
	
	public String getDialupNum(){
		return this.dialupNum;
	}
	
	public void setDialupNum(String dialupNum){
		this.dialupNum = dialupNum;
	}
	
	public String getUserId(){
		return this.userId;
	}
	
	public void setUserId(String userId){
		this.userId = userId;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public short getCellularMode() {
		return cellularMode;
	}
	
	public String getCellularModeStr() {
		switch (cellularMode) {
		case CELLULAR_MODE_AUTO:
			return "auto";
		case CELLULAR_MODE_2G:
			return "2g";
		case CELLULAR_MODE_3G:
			return "3g";
		case CELLULAR_MODE_4G:
			return "lte";	
		default:
			return "auto";
		}
	}

	public void setCellularMode(short cellularMode) {
		this.cellularMode = cellularMode;
	}

	public String getUnSupportDevices() {
		return unSupportDevices;
	}

	public void setUnSupportDevices(String unSupportDevices) {
		this.unSupportDevices = unSupportDevices;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
