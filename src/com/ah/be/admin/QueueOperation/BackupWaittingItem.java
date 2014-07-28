package com.ah.be.admin.QueueOperation;

import com.ah.bo.admin.HmDomain;

public class BackupWaittingItem {
	
	private HmDomain   domain;
	
	private int        iContent;
	
	private boolean    bShutDownFlag  = false;
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof BackupWaittingItem)) {
			return false;
		}

		final BackupWaittingItem item = (BackupWaittingItem) o;

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
	
	public void setContent(int icontent)
	{
		this.iContent = icontent;
	}
	
	public int getContent()
	{
		return this.iContent;
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
