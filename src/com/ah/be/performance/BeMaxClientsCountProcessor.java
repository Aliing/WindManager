package com.ah.be.performance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.admin.restoredb.AhRestoreNewTools;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.db.BulkUpdateUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.ReportPagingImpl;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhClientsOsInfoCount;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhMaxClientsCount;
import com.ah.bo.performance.AhSsidClientsCount;
import com.ah.util.MgrUtil;

public class BeMaxClientsCountProcessor implements Runnable {

	private ScheduledExecutorService			scheduler;

	// time unit is minutes
	private int							interval	= 10;

	/**
	 * Construct method
	 */
	public BeMaxClientsCountProcessor() {
	}

	public void startTask() {
		List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class,
				null, null);
		if (!list.isEmpty()) {
			interval = list.get(0).getClientPeriod();
		}
		
		if (interval<5 || interval>30) {
			interval=10;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		long remainMinute = (calendar.getTimeInMillis() - System
				.currentTimeMillis()) / 60000;
		remainMinute=remainMinute+1;
		remainMinute=remainMinute%10;
		if (remainMinute==0) {
			remainMinute=10;
		}

		// start refresh scheduler
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();

			scheduler.scheduleAtFixedRate(this, remainMinute, interval, TimeUnit.MINUTES);
		}
	}

	@Override
	public void run() {
		try {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			Calendar ca = Calendar.getInstance();
			ca.clear(Calendar.MILLISECOND);
			ca.clear(Calendar.SECOND);
			
			DebugUtil
					.performanceDebugInfo("BeMaxClientsCountProcessor.run(): Start cacluete max clients thread.");

			ConcurrentMap<Long, AhMaxClientsCount> maxClientsCountMap = CacheMgmt.getInstance()
					.getMaxClientsCountMap();
			Collection<AhMaxClientsCount> counterCollection = maxClientsCountMap.values();
			for(AhMaxClientsCount amc: counterCollection){
				amc.setTimeStamp(ca.getTimeInMillis());
			}
//			QueryUtil.bulkCreateBos(counterCollection);
			BulkUpdateUtil.bulkInsertForMaxClientsCount(counterCollection);

			// calculate CLient number
			calculateClientNumber(ca);
			
			calculateClientOsNumber(ca);
			
		} catch (Exception e) {
			DebugUtil.performanceDebugError("BeMaxClientsCountProcessor.run() catch exception", e);
		} catch (Error e) {
			DebugUtil.performanceDebugError("BeMaxClientsCountProcessor.run() catch error.", e);
		} finally {
			// re-init cache for max client counts
			try {
				CacheMgmt.getInstance().initMaxClientsCountMap();
			} catch (Exception ex) {
				DebugUtil.performanceDebugError("BeMaxClientsCountProcessor.run initMaxClientsCountMap catch error.", ex);
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void calculateClientNumber(Calendar ca){
		try {
			/*
			 * first, get all target local user out of database
			 */
			//SortParams sort = new SortParams("timeStamp");
			//String where = "timeStamp>= :s1 AND timeStamp <= :s2 AND apName in(:s3) AND owner.id = :s4";
			SortParams  sort = new SortParams("owner,apMac,apName,clientSSID,userProfileName");
			
			ReportPagingImpl page = new ReportPagingImpl(AhClientSession.class);
			page.setPageSize(50000);
			page.clearRowCount();
			
			List<?> bos;
			/*
			 * verify each local user, if PSK is rotated, update the local user
			 */
			List<AhSsidClientsCount> lst = new ArrayList<AhSsidClientsCount>();
			AhSsidClientsCount w0=null;
			AhSsidClientsCount w1=null;
			AhSsidClientsCount w2=null;
			AhSsidClientsCount pre=null;
			while(page.hasNext()) {
				bos = page.next().executeQuery("select distinct owner,apMac,apName,clientSSID, userProfileName,clientMACProtocol, clientChannel, clientmac from ah_clientsession", sort, null);
				for(Object bo : bos) {
					if(bo == null) {
						continue;
					}
					Object[] oneObj = (Object[])bo;
					Long ownerId = Long.parseLong(oneObj[0].toString());
					String apMac = oneObj[1].toString();
					String apName = oneObj[2].toString();
					String ssidName;
					String usprofileName = (oneObj[4]==null? "": oneObj[4].toString());
					int channel = Integer.parseInt(oneObj[6].toString());
					if (channel<=0) {
						ssidName = "wired";
					} else {
						ssidName = oneObj[3].toString();
					}
					int radioType = Integer.parseInt(oneObj[5].toString());
					if (channel==0) {
						radioType = AhInterfaceStats.RADIOTYPE_OTHER;
					}else if (radioType==AhAssociation.CLIENTMACPROTOCOL_AMODE 
							|| radioType==AhAssociation.CLIENTMACPROTOCOL_NAMODE
							|| radioType==AhAssociation.CLIENTMACPROTOCOL_ACMODE ){
						radioType = AhInterfaceStats.RADIOTYPE_5G;
					} else {
						radioType=AhInterfaceStats.RADIOTYPE_24G;
					}
					if (pre==null){
						pre = new AhSsidClientsCount();
						pre.setOwner(AhRestoreNewTools.CreateBoWithId(HmDomain.class,ownerId));
						pre.setApMac(apMac);
						pre.setApName(apName);
						pre.setSsid(ssidName);
						pre.setUserProfileName(usprofileName);
						pre.setRadioMode(radioType);
					}
//					int clientCount = Integer.parseInt(oneObj[5].toString());
					if (!pre.getOwner().getId().equals(ownerId) || 
							!pre.getApMac().equals(apMac) || 
							!pre.getSsid().equals(ssidName) ||
							!pre.getUserProfileName().equals(usprofileName)){
						if (w2!=null) {
							lst.add(w2);
						}
						if (w0!=null) {
							lst.add(w0);
						}
						if (w1!=null) {
							lst.add(w1);
						}
						
						pre.setOwner(AhRestoreNewTools.CreateBoWithId(HmDomain.class,ownerId));
						pre.setApMac(apMac);
						pre.setApName(apName);
						pre.setSsid(ssidName);
						pre.setRadioMode(radioType);
						pre.setUserProfileName(usprofileName);
						w0 = null;
						w1 = null;
						w2 = null;
					}	
					if (radioType==AhInterfaceStats.RADIOTYPE_OTHER) {
						if (w2==null) {
							w2 = new AhSsidClientsCount();
							w2.setOwner(AhRestoreNewTools.CreateBoWithId(HmDomain.class,ownerId));
							w2.setApMac(apMac);
							w2.setApName(apName);
							w2.setSsid(ssidName);
							w2.setRadioMode(radioType);
							w2.setUserProfileName(usprofileName);
							w2.setClientCount(1);
							w2.setTimeStamp(ca.getTimeInMillis());
						} else {
							w2.setClientCount(w2.getClientCount()+1);
						}
					} else if (radioType==AhInterfaceStats.RADIOTYPE_24G) {
						if (w0==null) {
							w0 = new AhSsidClientsCount();
							w0.setOwner(AhRestoreNewTools.CreateBoWithId(HmDomain.class,ownerId));
							w0.setApMac(apMac);
							w0.setApName(apName);
							w0.setSsid(ssidName);
							w0.setRadioMode(radioType);
							w0.setUserProfileName(usprofileName);
							w0.setClientCount(1);
							w0.setTimeStamp(ca.getTimeInMillis());
						} else {
							w0.setClientCount(w0.getClientCount()+1);
						}
					} else {
						if (w1==null) {
							w1 = new AhSsidClientsCount();
							w1.setOwner(AhRestoreNewTools.CreateBoWithId(HmDomain.class,ownerId));
							w1.setApMac(apMac);
							w1.setApName(apName);
							w1.setSsid(ssidName);
							w1.setUserProfileName(usprofileName);
							w1.setRadioMode(radioType);
							w1.setClientCount(1);
							w1.setTimeStamp(ca.getTimeInMillis());
						} else {
							w1.setClientCount(w1.getClientCount()+1);
						}
					}
					
					if (lst.size()>=1000) {
						BulkUpdateUtil.bulkInsert(AhSsidClientsCount.class, lst);
						//QueryUtil.bulkCreateBos(lst);
						lst.clear();
					}
				}
			}
			if (w2!=null) {
				lst.add(w2);
			}
			if (w0!=null) {
				lst.add(w0);
			}
			if (w1!=null) {
				lst.add(w1);
			}
			if (!lst.isEmpty()){
				BulkUpdateUtil.bulkInsert(AhSsidClientsCount.class, lst);
				//QueryUtil.bulkCreateBos(lst);
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError("BeMaxClientsCountProcessor.calculateClientNumber() catch exception", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void calculateClientOsNumber(Calendar ca){
		try {
			/*
			 * first, get all target local user out of database
			 */
			//SortParams sort = new SortParams("timeStamp");
			//String where = "timeStamp>= :s1 AND timeStamp <= :s2 AND apName in(:s3) AND owner.id = :s4";
			GroupByParams  groupBy = new GroupByParams(new String[]{"owner", "apMac", "apName","clientSSID","clientOsInfo"});
			FilterParams filter = new FilterParams("clientOsInfo is not null and clientOsInfo !=:s1", new Object[]{""});
			ReportPagingImpl page = new ReportPagingImpl(AhClientSession.class);
			page.setPageSize(50000);
			page.clearRowCount();
			
			List<?> bos;
			
			/*
			 * verify each local user, if PSK is rotated, update the local user
			 */
			List<AhClientsOsInfoCount> lst = new ArrayList<AhClientsOsInfoCount>();
			while(page.hasNext()) {
				bos = page.next().executeQuery("select owner,apMac,apName,clientSSID, clientOsInfo, count(clientOsInfo) from ah_clientsession", null, filter, groupBy);
				for(Object bo : bos) {
					if(bo == null) {
						continue;
					}
					Object[] oneObj = (Object[])bo;
					AhClientsOsInfoCount acc = new AhClientsOsInfoCount();
					acc.setOwner(AhRestoreNewTools.CreateBoWithId(HmDomain.class,Long.parseLong(oneObj[0].toString())));
					acc.setApMac(oneObj[1].toString());
					acc.setApName(oneObj[2].toString());
					acc.setSsid((oneObj[3]==null || oneObj[3].toString().equals(""))?"wired":oneObj[3].toString());
					acc.setOsInfo(oneObj[4].toString());
					acc.setClientCount(Integer.parseInt(oneObj[5].toString()));
					acc.setTimeStamp(ca.getTimeInMillis());
					lst.add(acc);
					if (lst.size()==1000) {
						BulkUpdateUtil.bulkInsert(AhClientsOsInfoCount.class, lst);
						//QueryUtil.bulkCreateBos(lst);
						lst.clear();
					}
				}
			}
			if (!lst.isEmpty()){
				BulkUpdateUtil.bulkInsert(AhClientsOsInfoCount.class, lst);
				//QueryUtil.bulkCreateBos(lst);
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError("BeMaxClientsCountProcessor.calculateClientNumber() catch exception", e);
		}
	}

	public boolean shutdown() {
		if (!scheduler.isShutdown()) {
			// Disable new tasks from being submitted.
			scheduler.shutdown();
		}
//		instance= null;
		return true;
	}

}