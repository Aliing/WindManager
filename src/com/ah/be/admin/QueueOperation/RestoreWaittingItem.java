package com.ah.be.admin.QueueOperation;

import com.ah.bo.admin.HmDomain;

public class RestoreWaittingItem {
	
	private HmDomain   domain;
	
	private String     strPath;
	
	private String     strFileName;
	
	private boolean    bShutDownFlag  = false;
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof RestoreWaittingItem)) {
			return false;
		}

		final RestoreWaittingItem item = (RestoreWaittingItem) o;

		return this.domain != null ? this.domain.equals(item.domain)
				: item.domain == null;
	}
	
	public void setDomain(HmDomain hmdomain)
	{
		this.domain = hmdomain;
	}
	
	public HmDomain getDomain()
	{
		return this.domain;
	}
	
	public void setFilePath(String sPath)
	{
		this.strPath = sPath;
	}
	
	public String getFilePath()
	{
		return this.strPath;
	}
	
	public void setFileName(String sName)
	{
		this.strFileName = sName;
	}
	
	public String getFileName()
	{
		return this.strFileName;
	}
	public void setShutDownFlag(boolean bFlag)
	{
		this.bShutDownFlag = bFlag;
	}

	public boolean getShutDownFlag()
	{
		return this.bShutDownFlag;
	}

}
