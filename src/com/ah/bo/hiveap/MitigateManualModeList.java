/**
 *@filename		MitigateManualModeList.java
 *@version
 *@author		Fiona
 *@createtime	2011-4-2 AM 11:14:15
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.hiveap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class MitigateManualModeList implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	
	private String rogueApMac = "";
	
	private String rogueApVendor = "";
	
	private List<MitigateManualModeItem> sameHiveApList = new ArrayList<MitigateManualModeItem>();

	public String getRogueApMac()
	{
		return rogueApMac;
	}

	public void setRogueApMac(String rogueApMac)
	{
		this.rogueApMac = rogueApMac;
	}

	public List<MitigateManualModeItem> getSameHiveApList()
	{
		return sameHiveApList;
	}

	public void setSameHiveApList(List<MitigateManualModeItem> sameHiveApList)
	{
		this.sameHiveApList = sameHiveApList;
	}

	public String getRogueApVendor()
	{
		return rogueApVendor;
	}

	public void setRogueApVendor(String rogueApVendor)
	{
		this.rogueApVendor = rogueApVendor;
	}

}
