package com.ah.be.admin.hhmoperate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;

public class APSwitchCenter {
	
	private Map<String, String> mCenter;
	
	public APSwitchCenter() {

	}
	
	private void initCenter()
	{
		String where = "apSwithStatus = :s1";
		Object[] values = new Object[1]; 
		values[0] = HMUpdateSoftwareInfo.NEED_AP_SWITH;	
		
		List<HMUpdateSoftwareInfo> infoList = QueryUtil.executeQuery(HMUpdateSoftwareInfo.class,
				null, new FilterParams(where,values));

		for (HMUpdateSoftwareInfo oInfo : infoList) {
			mCenter.put(oInfo.getDomainName(), oInfo.getIpAddress());
		}
	}
	
	public String getSwitchInfo(String strDomainName)
	{
		if(null == mCenter)
		{
			mCenter = new HashMap<String, String>();
			
			initCenter();
		}
		
		if(null == mCenter)
		{
			return null;
		}
		
		return mCenter.get(strDomainName);
	}
	
	public void addSwitchInfo(String strDomainName, String strIP)
	{
		if(null == mCenter)
		{
			return;
		}		
		
		mCenter.remove(strDomainName);		
		
		mCenter.put(strDomainName, strIP);
	}
	
	public void removeSwitchInfo(String strDomainName)
	{
		if(null == mCenter)
		{
			return;
		}
		
		mCenter.remove(strDomainName);		
	}

}