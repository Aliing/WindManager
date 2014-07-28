package com.ah.apiengine.element;

public class MvResponseInfo {
	
	public static final int MV_STATUS_WAITTING  = 1;
	public static final int MV_STATUS_RUNNING   = 2;
	public static final int MV_STATUS_FINISHED  = 3;
	
	public static final int MV_PROCESS_STATUS_NO_OPERATION     = 0;
	public static final int MV_PROCESS_STATUS_BACKUP_DATA      = 1;
	public static final int MV_PROCESS_STATUS_TRANSFER_DATA    = 2;
	public static final int MV_PROCESS_STATUS_RESTORE_DATA     = 3;
	public static final int MV_PROCESS_STATUS_CHANGE_CONFIG    = 4;
	
	private String strDomainName;
	
	private String strSrcIp;
	
	private String strDestIp;
	
	private int    iOperateStatus;
	
	private int    iProcessStatus;
	
	private boolean bResult;
	
	private String strMsg;
	
	public void setDomainName(String strName)
	{
		this.strDomainName = strName;
	}
	
	public String getDomainName()
	{
		return this.strDomainName;
	}
	
	public void setSrcIp(String strIp)
	{
		this.strSrcIp = strIp;
	}
	
	public String getSrcIp()
	{
		return this.strSrcIp;
	}
	
	public void setDestIp(String strIp)
	{
		this.strDestIp = strIp; 
	}
	
	public String getDestIp()
	{
		return this.strDestIp;
	}
	
	public void setMVStatus(int iStatus)
	{
		this.iOperateStatus = iStatus;
	}
	
	public int getMVStatus()
	{
		return this.iOperateStatus;
	}
	
	public void setProcessStatus(int iStatus)
	{
		this.iProcessStatus = iStatus;
	}
	
	public int getProcessStatus()
	{
		return this.iProcessStatus;
	}
	
	public void setResult(boolean bRslt)
	{
		this.bResult = bRslt;
	}
	
	public boolean getResult()
	{
		return this.bResult;
	}
	
	public void setMsg(String strmsg)
	{
		this.strMsg = strmsg;
	}

	public String getMsg()
	{
		return this.strMsg;
	}

}
