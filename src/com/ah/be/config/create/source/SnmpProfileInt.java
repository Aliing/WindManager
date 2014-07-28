package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;


/**
 * 
 * @author zhang
 *
 */
public interface SnmpProfileInt {
	
	public enum EnumVersion{
		v1, v2c, v3, any
	}
	
	public enum EnumOperation{
		reader, trap_host
	}
	
	public String getSnmpGuiName();
	
	public String getSnmpName();
	
	public String getApVersion();

	public String getUpdateTime();
	
	public boolean isConfigSnmpLocation();
	
	public String getSnmpLocation();
	
	public boolean isConfigureSnmp();
	
	public boolean isConfigureSnmpContact();
	
	public String getSnmpContact();
	
	public boolean isConfigureSnmpReader();
	
	public boolean isConfigureSnmpTrapHost();
	
	public int getSnmpTrapHostV1Size();
	
	public int getSnmpTrapHostV2Size();
	
	public String getSnmpTrapV1Address(int index);
	
	public boolean isConfigSnmpTrapV1Community(int index);
	
	public String getSnmpTrapV1Community(int index);
	
	public String getSnmpTrapV2Address(int index);
	
	public boolean isConfigSnmpTrapV2Community(int index);
	
	public String getSnmpTrapV2Community(int index);
	
	public boolean isConfigureReaderV1();
	
	public boolean isConfigureReaderV2();
	
	public boolean isConfigureReaderAny();
	
	public int getReaderV1CommunitySize();
	
	public int getReaderV1IpSize(int index);
	
	public int getReaderV2CommunitySize();
	
	public int getReaderV2IpSize(int index);
	
	public int getReaderAnyCommunitySize();
	
	public int getReaderAnyIpSize(int index);
	
	public String getReaderV1Community(int i, int j);
	
//	public String getReaderV1IpHost(int i, int j);
	
	public String getReaderV2Community(int i, int j);
	
	public String getReaderAnyCommunity(int i, int j);
	
//	public String getReaderV2IpHost(int i, int j);
	
	public boolean isEnableOverCapwap();
	
	public boolean isEnableOverSnmp();
	
	public boolean isEnableVpnTunnel(String serverAddr) throws CreateXMLException;
	
	public boolean isConfigReaderV3();
	
	public int getReaderV3Size();
	
	public String getAdminName(int index, EnumOperation optEnum);
	
	public String getAuthValue(int index, EnumOperation optEnum);
	
	public String getAuthPas(int index, EnumOperation optEnum);
	
	public String getEncryption(int index, EnumOperation optEnum);
	
	public String getEncryptionPas(int index, EnumOperation optEnum);
	
	public int getV3TrapAdminSize();
	
	public int getV3TrapSize();
	
	public String getV3TrapHostName(int index);
	
	public String getV3TrapHostAdmin(int index);
	
	public String getV3TrapHostVpnTunnel(int index) throws CreateXMLException;
	
//	public boolean isConfigEncryption(int index, EnumOperation optEnum);
}
