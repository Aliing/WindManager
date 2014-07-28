package com.ah.be.config.create.source;


/**
 * @author zhang
 * @version 2009-2-25 15:16:59
 */

public interface RadiusBindGroupInt {
	
	public String getRadiusGuiName();
	
	public String getRadiusName();
	
	public boolean isExistLocalUserGroup();
	
	public int getUserGroupSize();
	
	public String getUserGroupName(int index);

}
