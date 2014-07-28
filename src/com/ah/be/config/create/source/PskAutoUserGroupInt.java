package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;

/**
 * @author zhang
 * @version 2009-3-12 11:20:55
 */

public interface PskAutoUserGroupInt {
	
	public String getUserGroupGuiName();
	
	public String getUserGroupName();
	
	public boolean isConfigOldIndexRange();
	
	public String getIndexRangeValue();
	
	public int getRevokeUserSize();
	
	public int getAutoUserSize();
	
	public String getRevokeUserName(int index);
	
	public String getAutoUserName(int index);
	
	public boolean isConfigAutoGeneration();
	
	public boolean isConfigPpskBulk();
	
	public int getPpskBulkNumber() throws CreateXMLException;
	
	public String getPpskBulkInterval();
}
