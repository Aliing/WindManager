package com.ah.be.admin.QueueOperation;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.hhm.HhmUpgradeVersionInfo;

public class HHMUpdateWaittingItem {
	
	private HmDomain   domain;
	
	private int        iContent;
	
	private HhmUpgradeVersionInfo oUpdateInfo;
	
	private int        iUpdateType = FLAG_UPDATE_HHM2HHM;        
	
	private boolean    bShutDownFlag  = false;
	
	public final static int FLAG_UPDATE_HHM2HHM   = 1;
	public final static int FLAG_UPDATE_PLAN2DEMO = 2;
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof BackupWaittingItem)) {
			return false;
		}

		final HHMUpdateWaittingItem item = (HHMUpdateWaittingItem) o;

		return this.domain != null ? this.domain.equals(item.domain)
				: item.domain == null;
	}
	
	public void setDomain(HmDomain oDomain)
	{
		this.domain = oDomain;
	}

	public HmDomain getDomain()
	{
		return this.domain;
	}
	
	public void setContent(int icontent)
	{
		this.iContent = icontent;
	}
	
	public int getContent()
	{
		return this.iContent;
	}
	
	public void setUpgradeVersionInfo(HhmUpgradeVersionInfo oInfo)
	{
		this.oUpdateInfo = oInfo;
	}
	
	public HhmUpgradeVersionInfo getUpgradeVersionInfo()
	{
		return this.oUpdateInfo;
	}
	
	public void setShutDownFlag(boolean bFlag)
	{
		this.bShutDownFlag = bFlag;
	}

	public boolean getShutDownFlag()
	{
		return this.bShutDownFlag;
	}
	
	public void setUpdateType(int iType)
	{
		this.iUpdateType = iType;
	}
	
	public int getUpdateType()
	{
		return this.iUpdateType;
	}
}
