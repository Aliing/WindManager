package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;

/**
 * 
 * @author zhang
 *
 */
public interface IpPolicyProfileInt {
	
	public enum IpPolicyActionValue{
		permit, deny,inter_station_traffic_drop,nat
	}
	
	public enum IpPolicyLog{
		initiate_session, terminate_session, packet_drop, cr
	}
	
	public boolean isConfigIpPolicy();
	
	public String getIpPolicyGuiName();
	
	public String getApVersion();

	public String getIpPolicyName();
	
//	public String getUpdateTime();
	
	public int getIpPolicyIdSize();
	
	public int getIpPolicyIdName(int index);
	
	public String getPolicyFromValue(int index) throws Exception;
	
	public String getPolicyToValue(int index) throws Exception;
	
	public String getPolicyServiceName(int index);
	
	public boolean isConfigPolicyAction(int index, IpPolicyActionValue actionType);
	
	public boolean isConfigPolicyLog(int index) throws CreateXMLException;
	
	public boolean isConfigPolicyLogValue(int index, IpPolicyLog logType);
	
	public boolean isConfigBefore(int index);
	
	public int getPolicyBeforeIdValue(int index);
	
	public String getBeforeValue(int index);
	
}
