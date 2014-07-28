package com.ah.be.performance.appreport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.db.BulkOperationProcessor;
import com.ah.bo.ApReportData;
import com.ah.bo.performance.AhAppDataHour;
import com.ah.bo.performance.AhReportAppDataHour;

public class AppHourDataCollectorHandler extends BaseAppDataCollectorHandler {
	
	public ApReportData getSingleReportData(String apMac, byte[] data) {
		return getSingleReportData(apMac, FILE_TYPE_HOUR, data);
	}

	public boolean handToDataCollector(List<ApReportData> list) throws Exception {
		if (list == null || list.size() == 0) {
			return true;
		}
		boolean flag = false;
		//Class<? extends ApReportData> clazz = (list.get(0) instanceof AhAppDataHour) ? AhAppDataHour.class : AhAppDataSeconds.class;
		for (int i = 0; i < WAITDB_REPEAT_COUNT; i++) {
			flag = BulkOperationProcessor.addBoList(AhReportAppDataHour.class, mergeHourData(list));
			if (flag) {
				break;
			}
			Thread.sleep(WAITDB_INTERVAL_TIME);
		}
		if (flag) {
			BeLogTools.info(HmLogConst.M_PERFORMANCE, String.format("deliver to %d data to bulkOperationProcessor success", list.size()));
		}
		else {
			BeLogTools.info(HmLogConst.M_PERFORMANCE, String.format("deliver to %d data to bulkOperationProcessor failure", list.size()));
		}
		return flag;
	}
	

	private List<AhReportAppDataHour> mergeHourData(List<ApReportData> list) {
		List<AhReportAppDataHour> resultList = new ArrayList<AhReportAppDataHour>();
		Map<String, AhReportAppDataHour> map = new HashMap<String, AhReportAppDataHour>();
		for (int i = 0; i < list.size(); i++) {
			AhAppDataHour bean = (AhAppDataHour) list.get(i);
			AhReportAppDataHour appDataHour;
			String key = bean.getClientMac() + bean.getApplication(); 
			if (map.get(key) == null) {
				appDataHour = new AhReportAppDataHour();
				appDataHour.setApmac(bean.getApMac());
				appDataHour.setApplication(bean.getApplication());
				appDataHour.setClientmac(bean.getClientMac());
//				appDataHour.setHostname(bean.getHostName());
//				appDataHour.setOsname(bean.getOsName());
//				appDataHour.setVlan(bean.getVLan());
				appDataHour.setOwnerId(bean.getOwnerId());
				appDataHour.setUsername(bean.getUserName());
				appDataHour.setUserProfileName(bean.getUserProfileName());
				appDataHour.setSsid(bean.getSsid());
				appDataHour.setSeconds(3600);
				appDataHour.setTimestamp(ReportHelper.getPureHourTime(bean.getTimeStamp()));
			} else {
				appDataHour = map.get(key);
			}
//			if (bean.getRadioType() == 1) {
//				appDataHour.setTx24bytecount(bean.getBytesUpLoad());
//				appDataHour.setRx24bytecount(bean.getBytesDownLoad());
//				appDataHour.setTx24framecount(bean.getPacketsUpLoad());
//				appDataHour.setRx24framecount(bean.getPacketsDownLoad());
//			} else if (bean.getRadioType() == 2) {
//				appDataHour.setTx5bytecount(bean.getBytesUpLoad());
//				appDataHour.setRx5bytecount(bean.getBytesDownLoad());
//				appDataHour.setTx5framecount(bean.getPacketsUpLoad());
//				appDataHour.setRx5framecount(bean.getPacketsDownLoad());
//			} else if (bean.getRadioType() == 3) {
//				appDataHour.setTxwiredbytecount(bean.getBytesUpLoad());
//				appDataHour.setRxwiredbytecount(bean.getBytesDownLoad());
//				appDataHour.setTxwiredframecount(bean.getPacketsUpLoad());
//				appDataHour.setRxwiredframecount(bean.getPacketsDownLoad());
//			}		
			appDataHour.setBytes(appDataHour.getBytes() + bean.getBytesUpLoad() + bean.getBytesDownLoad());
			map.put(key, appDataHour);
		}
		
		Set<String> set = map.keySet();
		for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
			String key = iter.next();
			resultList.add(map.get(key));
		}
		return resultList;
	}

	

}
