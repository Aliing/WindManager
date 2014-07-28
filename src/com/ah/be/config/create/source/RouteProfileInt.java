package com.ah.be.config.create.source;


/**
 * 
 * @author zhang
 *
 */
public interface RouteProfileInt {
	
	public String getHiveApGuiName();
	
	public String getHiveApName();
	
	public String getApVersion();
	
	public boolean isConfigureRoute();
	
	public String getUpdateTime();
	
	public String getRouteDestinationMAC();
	
	public String getRouteInterfaceName();
	
	public String getRouteNextHopMac();
}
