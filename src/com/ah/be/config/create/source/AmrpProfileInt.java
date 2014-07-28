package com.ah.be.config.create.source;

import com.ah.xml.be.config.AmrpMetricTypeValue;

/**
 * 
 * @author zhang
 *
 */
public interface AmrpProfileInt {
	
	public String getHiveApGuiName();
	
	public String getHiveApName();
	
	public String getApVersion();
	
	public String getUpdateTime();
	
	public int getAmrpNeighborSize();
	
	public AmrpMetricTypeValue getAmrpMetricType();
	
	public int getAmrpPollInterval();
	
	public String getAmrpNeighborMac(int index);
	
	public int getAmrpNeighborMin(int index);
	
	public int getAmrpNeighborMax(int index);

	public boolean isVpnClient();
	
	public int getHeartbeatInterval();
	
	public int getHeartbeatRetry();
	
	public int getPriority(InterfaceProfileInt.InterType interType);
}
