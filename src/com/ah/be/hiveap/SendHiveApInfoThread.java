/**
 *@filename		SendHiveApInfoThread.java
 *@version
 *@author		Fiona
 *@createtime	2010-8-13 AM 10:28:56
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.hiveap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.ls.ClientSenderCenter;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class SendHiveApInfoThread implements Runnable
{

	private static final Tracer log = new Tracer(SendHiveApInfoThread.class.getSimpleName());

	private static SendHiveApInfoThread instance;
	
	private List<HiveApInfoForLs> apInfoList = new ArrayList<HiveApInfoForLs>();
	
	public synchronized static SendHiveApInfoThread getInstance() {
		if (instance == null) {
			instance = new SendHiveApInfoThread();
		}

		return instance;
	}
	
	@Override
	public void run()
	{
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		// send HiveAP info failed
		if (null != apInfoList && !apInfoList.isEmpty()) {
			try {
				ClientSenderCenter.sendAPConnectStatInfo(apInfoList);
				apInfoList.clear();
			} catch (Exception e) {
				log.error("run", "Exception while sending HiveAP information to License Server.", e);
			}
		}
		List<HiveAp> firstHiveAps = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams("manageStatus = :s1 AND discoveryReported = :s2 " +
			"AND simulated = :s3 AND discoveryTime > :s4 AND owner.runStatus != :s5", new Object[]{HiveAp.STATUS_MANAGED, false, false, 0L, HmDomain.DOMAIN_DISABLE_STATUS}));
		if (!firstHiveAps.isEmpty()) {
			List<HiveApInfoForLs> apListForLs = new ArrayList<HiveApInfoForLs>(firstHiveAps.size());
			Collection<String> allMacs = new ArrayList<String>(firstHiveAps.size());
			boolean hmOnline = NmsUtil.isHostedHMApplication();
			for (HiveAp singleAp : firstHiveAps) {
				HiveApInfoForLs apForLs = new HiveApInfoForLs();
				
				if (!NmsUtil.isValidSerialNumber(singleAp.getSerialNumber())) {
					continue;
				}

				// serial number
				apForLs.setSerialNumber(singleAp.getSerialNumber());
				
				// mac address
				apForLs.setMacAddress(singleAp.getMacAddress());
				allMacs.add(singleAp.getMacAddress());
				
				// first connect time
				apForLs.setFirstConnectTime(singleAp.getDiscoveryTime());
				
				// last connect time
				apForLs.setLastConnectTime(singleAp.getConnChangedTime());
				
				// ap product name
				apForLs.setProductName(singleAp.getProductName());
				
				// ap soft version
				apForLs.setSoftVer(singleAp.getSoftVer());
				
				HmDomain hmDom = singleAp.getOwner();
				// system id or vhm id
				apForLs.setHmId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
				if (!hmDom.isHomeDomain() && hmOnline) {
					apForLs.setHmId(hmDom.getVhmID());
				}
				
				// vhm name
				apForLs.setVhmName(hmDom.getDomainName());
				
				// time zone
				apForLs.setTimeZone(hmDom.getTimeZoneString());
				
				apListForLs.add(apForLs);
			}
			
			// send HiveAP information to license server
			try {
				if (!apListForLs.isEmpty())
					ClientSenderCenter.sendAPConnectStatInfo(apListForLs);
				
				// update the flag
				if (!allMacs.isEmpty())
					BoMgmt.getHiveApMgmt().updateHiveApReportFlag(allMacs);
			} catch (Exception e) {
				log.error("run", "Exception occurred while sending HiveAP information to License Server for the first time.", e);
			}
		}
	}

	public List<HiveApInfoForLs> getApInfoList()
	{
		return apInfoList;
	}

	public void setApInfoList(List<HiveApInfoForLs> apInfoList)
	{
		this.apInfoList = apInfoList;
	}

}