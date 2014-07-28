package com.ah.be.admin.adminOperateImpl;
/**
 *@filename		AhRootCADTO.java
 *@version
 *@author		lanbao
 *@createtime	Aug 21, 2007 4:40:49 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */


import java.io.Serializable;

/**
 * @author		lanbao
 * @version		V1.0.0.0 
 */
public class BeRootCADTO implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	private String m_strCommName;
	
	private String m_strOrgName;
	
	private String m_strOrgUnit;
	
	private String m_strLocalityName;
	
	private String m_strStateName;
	
	private String m_strCountryCode;
	
	private String m_strEmailAddress;
	
	private String m_strValidity;
	
	private String m_strKeySize;
	
	private String m_strPassword;
	
	private String m_strFileName;
	
	private String m_domain_name;
	
	private String m_subject_altname;
	
	public void setCommName(String strName)
	{
		m_strCommName = strName;
	}
	
	public String getCommName()
	{
		return m_strCommName;
	}
	
	public void setOrgName(String strName)
	{
		m_strOrgName = strName;
	}
	
	public String getOrgName()
	{
		return m_strOrgName;
	}
	
	public void setOrgUnit(String strUnit)
	{
		m_strOrgUnit = strUnit;
	}
	
	public String getOrgUnit()
	{
		return m_strOrgUnit;
	}
	
	public void setLocalityName(String strName)
	{
		m_strLocalityName = strName;
	}
	
	public String getLocalityName()
	{
		return m_strLocalityName;
	}
	
	public void setStateName(String strName)
	{
		m_strStateName = strName;
	}
	
	public String getStateName()
	{
		return m_strStateName;
	}
	
	public void setCountryCode(String strCountryCode)
	{
		m_strCountryCode = strCountryCode;
	}
	
	public String getCountryCode()
	{
		return m_strCountryCode;
	}
	
	public void setEmailAddress(String strEmailAddress)
	{
		m_strEmailAddress = strEmailAddress;
	}
	
	public String getEmailAddress()
	{
		return m_strEmailAddress;
	}
	
	public void setValidity(String strValidity)
	{
		m_strValidity = strValidity;
	}
	
	public String getValidity()
	{
		return m_strValidity;
	}
	
	public void setKeySize(String strKeySize)
	{
		m_strKeySize = strKeySize;
	}
	
	public String getKeySize()
	{
		return m_strKeySize;
	}
	
	public void setPassword(String strPsd)
	{
		m_strPassword = strPsd;
	}
	
	public String getPassword()
	{
		return m_strPassword;
	}
	
	public void setFileName(String strFileName)
	{
		m_strFileName = strFileName;
	}
	
	public String getFileName()
	{
		return m_strFileName;
	}
	
	public void setDomainName(String strDomainName)
	{
		m_domain_name = strDomainName;
	}
	
	public String getDomainName()
	{
		return m_domain_name;
	}
	
	public void setAltName(String strAltName)
	{
		m_subject_altname = strAltName;
	}
	
	public String getAltName()
	{
		return m_subject_altname;
	}
}
