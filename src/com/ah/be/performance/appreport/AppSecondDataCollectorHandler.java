package com.ah.be.performance.appreport;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.db.BulkOperationProcessor;
import com.ah.bo.ApReportData;
import com.ah.bo.performance.AhAppDataSeconds;
import com.ah.bo.performance.AhReportAppDataSeconds;

public class AppSecondDataCollectorHandler extends BaseAppDataCollectorHandler {
	
	public ApReportData getSingleReportData(String apMac, byte[] data) {
		return getSingleReportData(apMac, FILE_TYPE_SECOND, data);
	}

	public boolean handToDataCollector(List<ApReportData> list) throws Exception {
		if (list == null || list.size() == 0) {
			return true;
		}
		boolean flag = false;
		//Class<? extends ApReportData> clazz = (list.get(0) instanceof AhAppDataHour) ? AhAppDataHour.class : AhAppDataSeconds.class;
		//Class<? extends ApReportData> clazz = AhAppDataSeconds.class;
		
		for (int i = 0; i < WAITDB_REPEAT_COUNT; i++) {
			flag = BulkOperationProcessor.addBoList(AhReportAppDataSeconds.class, convertSecondData(list));
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
	
	private List<AhReportAppDataSeconds> convertSecondData(List<ApReportData> list) {
		List<AhReportAppDataSeconds> resultList = new ArrayList<AhReportAppDataSeconds>();
		for (int i = 0; i < list.size(); i++) {
			AhAppDataSeconds bean = (AhAppDataSeconds) list.get(i);
			AhReportAppDataSeconds appDataSeconds = new AhReportAppDataSeconds();
			appDataSeconds.setApmac(bean.getApMac());
			appDataSeconds.setApplication(bean.getApplication());
			appDataSeconds.setClientmac(bean.getClientMac());
//			appDataSeconds.setHostname(bean.getHostName());
//			appDataSeconds.setOsname(bean.getOsName());
//			appDataSeconds.setVlan(bean.getVLan());
			appDataSeconds.setOwnerId(bean.getOwnerId());
			appDataSeconds.setUsername(bean.getUserName());
			appDataSeconds.setUserProfileName(bean.getUserProfileName());
			appDataSeconds.setSsid(bean.getSsid());
			appDataSeconds.setSeconds(bean.getSeconds());
			appDataSeconds.setAppSeconds(bean.getAppSeconds());
			appDataSeconds.setTimestamp(bean.getTimeStamp());
//			if (bean.getRadioType() == 1) {
//				appDataSeconds.setTx24bytecount(bean.getBytesUpLoad());
//				appDataSeconds.setRx24bytecount(bean.getBytesDownLoad());
//				appDataSeconds.setTx24framecount(bean.getPacketsUpLoad());
//				appDataSeconds.setRx24framecount(bean.getPacketsDownLoad());
//			} else if (bean.getRadioType() == 2) {
//				appDataSeconds.setTx5bytecount(bean.getBytesUpLoad());
//				appDataSeconds.setRx5bytecount(bean.getBytesDownLoad());
//				appDataSeconds.setTx5framecount(bean.getPacketsUpLoad());
//				appDataSeconds.setRx5framecount(bean.getPacketsDownLoad());
//			} else if (bean.getRadioType() == 3) {
//				appDataSeconds.setTxwiredbytecount(bean.getBytesUpLoad());
//				appDataSeconds.setRxwiredbytecount(bean.getBytesDownLoad());
//				appDataSeconds.setTxwiredframecount(bean.getPacketsUpLoad());
//				appDataSeconds.setRxwiredframecount(bean.getPacketsDownLoad());
//			}
			appDataSeconds.setBytes(bean.getBytesUpLoad() + bean.getBytesDownLoad());
			resultList.add(appDataSeconds);
		}
		return resultList;
	}

	

}
