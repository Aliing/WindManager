/**
 *@filename		BeRebootInfoDTO.java
 *@version
 *@author		Xiaolanbao
 *@createtime	2007-9-27 07:13:58
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.admin.adminOperateImpl;

import java.io.Serializable;

/**
 * @author		Xiaolanbao
 * @version		V2.2.0.0 
 */
public class BeRebootInfoDTO implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	private String label = "";
	
	private String version = "";
	
	private boolean isBootLabel = false;
	
	private boolean isRunningSoft = false;
	
	private boolean canShow = false;
	public String getLabel()
	{
		return label;
	}
	
	public boolean isCanShow() {
		return canShow;
	}

	public void setCanShow(boolean canShow) {
		this.canShow = canShow;
	}

	public void setLabel(String strLabel)
	{
		label = strLabel;
	}
	
	public String getVersion()
	{
		return version;
	}
	
	public void setVersion(String strVersion)
	{
		version = strVersion;
	}
	
	public boolean getIsBootLabel()
	{
		return isBootLabel;
	}
	
	public void setIsBootLabel(boolean bFlag)
	{
		isBootLabel = bFlag;
	}
	
	public boolean getIsRunningSoft()
	{
		return isRunningSoft;
	}
	
	public void setIsRunningSoft(boolean bFlag)
	{
		isRunningSoft = bFlag;
	}
	
	public String getStatus()
	{
		if (isRunningSoft)
		{
			return "Active";
		}
		else
		{
			return "Standby";
		}
	}
}
