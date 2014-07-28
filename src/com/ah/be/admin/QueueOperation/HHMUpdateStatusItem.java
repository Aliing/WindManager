package com.ah.be.admin.QueueOperation;

import com.ah.be.admin.hhmoperate.UpdateInfo;

public class HHMUpdateStatusItem {

	
	private long       lDomainId;	
	
	private int        iStatus;
	
	private int        iBeforeCount;
	
	//how to ??
	private long       lWaittingTime;
	
	private int        update_status;
	
	private UpdateInfo oReturnInfo;
	
	//status
	public static final int UPDATE_WAITTING       = 1;
	public static final int UPDATE_RUNNING        = 2;
	public static final int UPDATE_FINISHED       = 3;
	
	//unit for waitting time(sec)
	public static final long UNIT_TIME_WAITTING  = 600;
	
	//update_status
	public static final int Update_Status_No_operation     = 0;
	public static final int Update_Status_Backup_Data      = 1;
	public static final int Update_Status_Move_Data        = 2;
	public static final int Update_Status_Restore_Data     = 3;
	public static final int Update_Status_Change           = 4;
	
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

	public void setUpdateStatus(int iStatus)
	{
		this.update_status = iStatus;
	}
	
	public int getUpdateStatus()
	{
		return this.update_status;
	}
	
	public void setReturnInfo(UpdateInfo oInfo)
	{
		this.oReturnInfo = oInfo;
	}
	
	public UpdateInfo getReturnInfo()
	{
		return this.oReturnInfo;
	}
}
