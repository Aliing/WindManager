package com.ah.be.config.create.source;


/**
 * 
 * @author zhang
 *
 */
public interface MobilityThresholdProfileInt {

	public static final String ROAMING_THRESHOLD_HIGH = "high";
	public static final String ROAMING_THRESHOLD_LOW = "low";
	public static final String ROAMING_THRESHOLD_MEDIUM = "medium";
	
	public String getHiveApGuiName();
	
	public String getHiveApName();
	
	public String getApVersion();
	
	public String getUpdateTime();
	
	public boolean isConfigureThresholdType(String type);
}
