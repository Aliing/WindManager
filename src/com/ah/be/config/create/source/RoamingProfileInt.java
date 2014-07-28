package com.ah.be.config.create.source;


/**
 * 
 * @author zhang
 *
 */
public interface RoamingProfileInt {
	
	public String getHiveProfileGuiName();
	
	public String getHiveProfileName();
	
	public String getApVersion();
	
	public String getUpdateTime();
	
	public int getRoamingPort();
	
	public boolean isEnabledL3Setting();
	
	public boolean isConfigureNeighbor();
	
	public int getUpdateInterval();
	
	public int getCacheAgeout();
	
	public int getNeighborQueryInteval();
	
	public int getNeighborQueryTime();
	
	public int getNeighborIncludeSize();
	
	public int getNeighborExcludeSize();
	
	public boolean isConfigNeighborQuery();
	
	public boolean isConfigureNeighborInclude();
	
	public boolean isConfigureNeighborExclude();
	
	public String getNeighborIncludeIp(int index);
	
	public String getNeighborIncludeNetMask(int index);
	
	public String getNeighborExcludeIp(int index);
	
	public boolean isRoamingCacheAccessEnable();
	
	public boolean isRoamingCacheBackhaulEnable();
	
}
