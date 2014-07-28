package com.ah.be.admin.QueueOperation;

import com.ah.be.admin.hhmoperate.RestoreInfo;

public class RestoreStatusItem {
	
    private long       lDomainId;
	
	private int        iStatus;
	
	private int        iBeforeCount;
	
	//how to ??
	private long       lWaittingTime;

	private RestoreInfo oRestoreInfo;
	
	//status
	public static final int RESTORE_WAITTING       = 1;
	public static final int RESTORE_RUNNING        = 2;
	public static final int RESTORE_FINISHED       = 3;
	
	//unit for waitting time(sec)
	public static final long UNIT_TIME_WAITTING  = 300;
	
	public void setDomainId(long lId)
	{
	    this.lDomainId = lId;	
	}
	
	public long getDomainId()
	{
		return this.lDomainId;
	}
	
	public void setStatus(int istatus)
	{
		this.iStatus = istatus;
	}
	
	public int getStatus()
	{
		return this.iStatus;
	}
	
	public void setBeforeCount(int icount)
	{
		this.iBeforeCount = icount;
	}
	
	public int getBeforeCount()
	{
		return this.iBeforeCount;
	}
	
	public void setWaittingTime(long ltime)
	{
		this.lWaittingTime = ltime;
	}
	
	public long getWaittingTime()
	{
		return this.lWaittingTime;
	}
	
	public void setRestoreInfo(RestoreInfo oInfo)
	{
		this.oRestoreInfo = oInfo;
	}
	
	public RestoreInfo getRestoreInfo()
	{
		return this.oRestoreInfo;
	}
}
