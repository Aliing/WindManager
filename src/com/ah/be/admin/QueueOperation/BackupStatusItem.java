package com.ah.be.admin.QueueOperation;

import com.ah.be.admin.hhmoperate.BackupInfo;

public class BackupStatusItem {
    
	
	private long       lDomainId;
	
	
	private int        iStatus;
	
	private int        iBeforeCount;
	
	//how to ??
	private long       lWaittingTime;
	
	private BackupInfo  oBackupInfo;
	
	
	
	//status
	public static final int BACKUP_WAITTING       = 1;
	public static final int BACKUP_RUNNING        = 2;
	public static final int BACKUP_FINISHED       = 3;
	
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
	
	public void setBackupInfo(BackupInfo oInfo)
	{
		this.oBackupInfo = oInfo;
	}
	
	public BackupInfo getBackupInfo()
	{
		return this.oBackupInfo;
	}
	
}
