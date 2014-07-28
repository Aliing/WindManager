/**
 * 
 */
package com.ah.be.admin.adminOperateImpl;

/**
 * @author root
 *
 */
public class BeVersionInfo 
{
	private String m_strMainVersion;
	
	private String m_strSubVersion;
	
	private String m_strStatus;
	
	private String m_strBuildTime;
	
	private int imageUid;
	
	private String m_strTvMainVer;
	
	private String m_strTvSubVer;
	
	public int getImageUid()
	{
		return imageUid;
	}

	public void setImageUid(int imageUid)
	{
		this.imageUid = imageUid;
	}

	public void setMainVersion(String strVersion)
	{
		m_strMainVersion = strVersion;
	}
	
	public String getMainVersion()
	{
		return m_strMainVersion;
	}
	
	public void setSubVersion(String strVersion)
	{
		m_strSubVersion = strVersion;
	}
	
	public String getSubVersion()
	{
		return m_strSubVersion;
	}
	
	public void setStatus(String strStatus)
	{
		m_strStatus = strStatus;
	}
	
	public String getStatus()
	{
		return m_strStatus;
	}
	
	public void setBuildTime(String strTime)
	{
		m_strBuildTime = strTime;
	}
	
	public String getBuildTime()
	{
		return m_strBuildTime;
	}

	/**
	 * getter of m_strTvMainVer
	 * @return the m_strTvMainVer
	 */
	public String getTvMainVer() {
		return m_strTvMainVer;
	}

	/**
	 * setter of m_strTvMainVer
	 * @param tvMainVer the m_strTvMainVer to set
	 */
	public void setTvMainVer(String tvMainVer) {
		m_strTvMainVer = tvMainVer;
	}

	/**
	 * getter of m_strTvSubVer
	 * @return the m_strTvSubVer
	 */
	public String getTvSubVer() {
		return m_strTvSubVer;
	}

	/**
	 * setter of m_strTvSubVer
	 * @param tvSubVer the m_strTvSubVer to set
	 */
	public void setTvSubVer(String tvSubVer) {
		m_strTvSubVer = tvSubVer;
	}
	
	
}
