package com.ah.be.admin.adminOperateImpl;

public class BeUploadCfgInfo {
	
	public static String AH_UPLOAD_TYPE_UPDATE  = "update";
	
	public static String AH_UPLOAD_TYPE_RESTORE = "restore";
	
	public static String AH_UPLOAD_RUNNING_TRUE = "true";
	
	public static String AH_UPLOAD_RUNNINF_FALSE = "false";
	
    public static String AH_UPLOAD_LOCATION_REMOTE = "remote";
	
	public static String AH_UPLOAD_LOCATION_LOCAL = "local";
	
	public static String AH_UPLOAD_FINISHED_TRUE = "true";
	
	public static String AH_UPLOAD_FINISHED_FALSE = "false";
	
	private String m_strType;
	
	private String m_strRunningFlag;
	
	private String m_strName;
	
	//the unit is byte
	private String m_strSize;
	
	private String m_strLocation;
	
	private String m_strFinishFlag;
	
	public BeUploadCfgInfo(){}
	
	public void setType(String strType)
	{
		m_strType = strType;
	}
	
	public String getType()
	{
		return m_strType;
	}
	
	
	public void setRunningFlag(String strRunningFlag)
	{
		m_strRunningFlag = strRunningFlag;
	}
	
	public String getRunningFlag()
	{
		return m_strRunningFlag;
	}

	public void setName(String strName)
	{
		m_strName = strName;
	}
	
	public String getName()
	{
		return m_strName;
	}
	
	public void setSize(String strSize)
	{
		m_strSize = strSize;
	}
	
	public String getSize()
	{
		return m_strSize;
	}
	
	public void setLocation(String strLocation)
	{
		m_strLocation = strLocation;
	}
	
	public String getLocation()
	{
		return m_strLocation;
	}
	
	public void setFinishFlag(String strFinishFlag)
	{
		m_strFinishFlag = strFinishFlag;
	}
	
	public String getFinishFlag()
	{
		return m_strFinishFlag;
	}
}
