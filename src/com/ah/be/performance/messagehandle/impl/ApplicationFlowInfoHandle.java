package com.ah.be.performance.messagehandle.impl;

import com.ah.be.common.cache.AppFlowCacheMgmt;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.common.db.BulkUpdateUtil;
import com.ah.be.communication.event.BeApplicationFlowInfoResultEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.messagehandle.MessageHandleInterface;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhAppFlowLog;

public class ApplicationFlowInfoHandle implements MessageHandleInterface {
	
	public void handleMessage(BeBaseEvent event) {
		if(event.getClass() != BeApplicationFlowInfoResultEvent.class) {
			return;
		}
		
		BeApplicationFlowInfoResultEvent resultEvent = (BeApplicationFlowInfoResultEvent)event;
		BeLogTools.info(HmLogConst.M_PERFORMANCE, "ApplicationFlowInfoHandle received event, appFlowList size : " + resultEvent.getAppFlowList().size());
		
		//for (int i = 0; i < resultEvent.getAppFlowList().size(); i++) {
		//	AhAppFlowLog log = resultEvent.getAppFlowList().get(i);
		//	BeLogTools.error(HmLogConst.M_PERFORMANCE, "app-code= " + log.getAppCode() + " bytes=" + log.getBytes() + " packets=" + log.getPackets() + " apmac=" + log.getApMac());
		//}
		
		//omit if Device is not exist or is not managed
		SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(resultEvent.getApMac());
		if (simpleHiveAp == null || simpleHiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			return;
		}
		HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId());
		
		for(AhAppFlowLog appFlowLog : resultEvent.getAppFlowList()) {
			appFlowLog.setOwner(owner);
		}		
		if(resultEvent.getSequenceNum() != 0) {
			try {
				AppFlowCacheMgmt.getInstance().saveFlowData(owner.getId(), resultEvent.getAppFlowList());
				for(AhAppFlowLog appFlowLog : resultEvent.getAppFlowList()) {
					appFlowLog.setTimeStamp(appFlowLog.getStartTime());
					appFlowLog.setOwnerId(owner.getId());
				}		
				//QueryUtil.bulkCreateBos(resultEvent.getAppFlowList());
				BulkUpdateUtil.bulkInsert(AhAppFlowLog.class, resultEvent.getAppFlowList());
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_PERFORMANCE, "ApplicationFlowInfoHandle, Exception when bulk create application flow data", e);
			}
		}
	}
	
}

