package com.ah.bo.report.common;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.admin.restoredb.AhRestoreCommons;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.HmSystemInfoUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhMaxClientsCount;
import com.ah.nms.worker.report.SystemInfoProvider;
import com.ah.nms.worker.report.axis.by.top.SystemInfoAxis;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;

public class SystemInfoQueryImpl implements SystemInfoProvider {

	private static final Tracer log = new Tracer(SystemInfoQueryImpl.class.getSimpleName());
	
	public SystemInfoQueryImpl(){			
	}
	
	public Map getSystemInfo(long domainId,Map<String,Byte> metrics){
		Map map = new HashMap();
		
		BeVersionInfo versionInfo = NmsUtil.getVersionInfo();
		String buildTime = versionInfo.getBuildTime();
		try{
			HmDomain hm = QueryUtil.findBoById(HmDomain.class,domainId);
			String oldFormat ="yyyy-MM-dd hh:mm:ss";
			SimpleDateFormat oldSDF = new SimpleDateFormat(oldFormat);
			oldSDF.setTimeZone(hm.getTimeZone());		
			java.util.Date datetime = oldSDF.parse(versionInfo.getBuildTime());
			buildTime = AhDateTimeUtil.getFormatDateTime(datetime,hm);
		}catch(Exception e){			
		}
		
		if( metrics.containsKey(SystemInfoAxis.DEVICE_UP) || metrics.containsKey(SystemInfoAxis.DEVICE_DOWN)
				|| metrics.containsKey(SystemInfoAxis.DEVICE_ALARM) || metrics.containsKey(SystemInfoAxis.DEVICE_OUTDATE) ){
			int apHthUp = 0;
			int apHthDown = 0;
			int apHthAlarm = 0;
			int apHthOutdate = 0;
			List<?> findHiveAPList=QueryUtil.executeQuery("select connected,severity,pending,pending_user from "
					+ HiveAp.class.getSimpleName() + " bo", null,  new FilterParams("manageStatus = :s1 and owner.id = :s2",
							new Object[] { HiveAp.STATUS_MANAGED,domainId}));
			for(Object oneobj:findHiveAPList){
				Object[] tObj=(Object[])oneobj;
				// HiveAP down and up check
				if (AhRestoreCommons.convertStringToBoolean(tObj[0].toString())){
					apHthUp++;
				} else {
					apHthDown++;
				}
				// HiveAPs with alarm condition
				if (Integer.parseInt(String.valueOf(tObj[1]))>AhAlarm.AH_SEVERITY_UNDETERMINED){
					apHthAlarm++;
				}
				// HiveAPs with outdate configuration
				if (AhRestoreCommons.convertStringToBoolean(tObj[2].toString())
					|| AhRestoreCommons.convertStringToBoolean(tObj[2].toString())){
					apHthOutdate++;
				}				
			}
			map.put(SystemInfoAxis.DEVICE_UP, apHthUp);
			map.put(SystemInfoAxis.DEVICE_DOWN, apHthDown);
			map.put(SystemInfoAxis.DEVICE_ALARM, apHthAlarm);
			map.put(SystemInfoAxis.DEVICE_OUTDATE, apHthOutdate);
		}
		
		if( metrics.containsKey(SystemInfoAxis.DEVICE_NEW)){
			int unmanagedDeviceCount =  (int)QueryUtil.findRowCount(HiveAp.class, new FilterParams("manageStatus = :s1 and owner.id = :s2)",
					new Object[] { HiveAp.STATUS_NEW, domainId}));
			map.put(SystemInfoAxis.DEVICE_NEW,unmanagedDeviceCount);
		}
		
		if( metrics.containsKey(SystemInfoAxis.DEVICE_FRIENDLY) ||  metrics.containsKey(SystemInfoAxis.DEVICE_ROGUE)){
			int apSecFriend=0;
			int apSecRogue=0;
			List<?> findIdpAPList=QueryUtil.executeQuery(
					"select distinct ifMacAddress,idpType from "
					+ Idp.class.getSimpleName() + " bo", null,  new FilterParams("stationType = :s1 and owner.id = :s2",
							new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_AP,domainId }));

			for(Object oneobj:findIdpAPList){
				Object[] tObj=(Object[])oneobj;
				// Network Security
				if (Integer.parseInt(String.valueOf(tObj[1]))==BeCommunicationConstant.IDP_TYPE_ROGUE){
					apSecRogue++;
				} else {
					apSecFriend++;
				}
			}
			map.put(SystemInfoAxis.DEVICE_FRIENDLY,apSecFriend);
			map.put(SystemInfoAxis.DEVICE_ROGUE,apSecRogue);
		}
		
		if( metrics.containsKey(SystemInfoAxis.CLIENT_NETROGUE) ){
			List<?> rogueInNetCount = QueryUtil.executeQuery("select count(DISTINCT ifMacAddress) from "
					+  Idp.class.getSimpleName() + " bo", null,new FilterParams(
							"stationType = :s1 and idpType=:s2 and inNetworkFlag=:s3 and owner.id = :s4",
							new Object[] {
									BeCommunicationConstant.IDP_STATION_TYPE_CLIENT,
									BeCommunicationConstant.IDP_TYPE_ROGUE,
									BeCommunicationConstant.IDP_CONNECTION_IN_NET,domainId }));
			long rogueClientCountInNet = AhRestoreCommons.convertLong(rogueInNetCount.get(0).toString());
			map.put(SystemInfoAxis.CLIENT_NETROGUE,rogueClientCountInNet);
		}
		
		if(  metrics.containsKey(SystemInfoAxis.CLIENT_MAPROGUE)){
			List<?> rogueOnMapCount = QueryUtil.executeQuery("select count(DISTINCT ifMacAddress) from "
					+  Idp.class.getSimpleName() + " bo", null,new FilterParams(
							"stationType = :s1 and idpType=:s2 and mapId is not null and owner.id = :s3",
							new Object[] {
									BeCommunicationConstant.IDP_STATION_TYPE_CLIENT,
									BeCommunicationConstant.IDP_TYPE_ROGUE,domainId }));
			long rogueClientCountOnMap = AhRestoreCommons.convertLong(rogueOnMapCount.get(0).toString());
			map.put(SystemInfoAxis.CLIENT_MAPROGUE,rogueClientCountOnMap);
		}
		
		if(  metrics.containsKey(SystemInfoAxis.CLIENT_ACTIVE)){
			List<?> activeCount = DBOperationUtil.executeQuery("select count(DISTINCT clientMac) from  ah_clientsession",
					null, new FilterParams("connectstate = ? AND owner = ?",
							new Object[] { AhClientSession.CONNECT_STATE_UP, domainId }));		
			long activeClientCount = AhRestoreCommons.convertLong(activeCount.get(0).toString());
			map.put(SystemInfoAxis.CLIENT_ACTIVE,activeClientCount);
		}
		
		if(  metrics.containsKey(SystemInfoAxis.CLIENT_MAX)){
			long maxClientCount = 0;
			List<?> maxCount = QueryUtil.executeQuery("select max(maxClientCount) from "
					+  AhMaxClientsCount.class.getSimpleName(), null,new FilterParams(
							"timeStamp >= :s1 and owner.id = :s2 and globalFlg=:s3",
							new Object[] {
								System.currentTimeMillis()-1000*60*60*24,
								domainId,false }));
			if (maxCount.get(0)!=null) {
				maxClientCount = AhRestoreCommons.convertLong(maxCount.get(0).toString());
			}
			map.put(SystemInfoAxis.CLIENT_MAX,maxClientCount);
		}
		
		if(  metrics.containsKey(SystemInfoAxis.HM_HOSTNAME)){
			map.put(SystemInfoAxis.HM_HOSTNAME,HmBeOsUtil.getHostName());
		}
		
		if(  metrics.containsKey(SystemInfoAxis.HM_BUILD)){
			map.put(SystemInfoAxis.HM_BUILD,buildTime);
		}
		
		if(  metrics.containsKey(SystemInfoAxis.HM_MODEL)){
			map.put(SystemInfoAxis.HM_MODEL,HmBeAdminUtil.getHmKernelModel());
		}
		
		if(  metrics.containsKey(SystemInfoAxis.HM_SN)){
		    if ("AH-HM-1U".equals(HmBeAdminUtil.getHmKernelModel())) {
			map.put(SystemInfoAxis.HM_SN,HmBeAdminUtil.getHmSerialNumber());
		    }
		}
			
		if(  metrics.containsKey(SystemInfoAxis.HM_UP)){
			long linkTime = HmSystemInfoUtil.getJvmUptime()/1000;
			String systemUpTime = NmsUtil.transformTime((int) linkTime).replace(" 0 Secs", "");
			map.put(SystemInfoAxis.HM_UP,systemUpTime);
		}
		if(  metrics.containsKey(SystemInfoAxis.HM_HA)){
			List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, null);
			String haStatus = "unknown";
			if (!list.isEmpty()) {
				HASettings haSettings = list.get(0);
				if (haSettings.getHaStatus() != HASettings.HASTATUS_ENABLE) {
					haStatus = "Standalone";
				} else {
					haStatus = "HA is running";
				}
			}
			map.put(SystemInfoAxis.HM_HA,haStatus);
		}
		if(  metrics.containsKey(SystemInfoAxis.HM_PORTS) ||  metrics.containsKey(SystemInfoAxis.HM_LANS) ){
			String mgtState = "unknown";
			String lanState = "unknown";
			try {
				List<String> mgtInfos = HmBeAdminUtil.getEthInfo("eth0");

				mgtState = mgtInfos.get(1).replace("Mb/s", " Mbps") + "   " + mgtInfos.get(2);

				List<String> lanInfos = HmBeAdminUtil.getEthInfo("eth1");
				if (lanInfos.get(0).equalsIgnoreCase("off")) {
					lanState = "Down";
				} else {
					lanState = lanInfos.get(1).replace("Mb/s", " Mbps") + "   " + lanInfos.get(2);
				}

			} catch (Exception e) {
				log.error("getSystemInfo:mgtState:lanState", "catch exception", e);
				
			}
			map.put(SystemInfoAxis.HM_LANS,lanState);
			map.put(SystemInfoAxis.HM_PORTS,mgtState);
		}
		
		
		return map;
	}
	
}
