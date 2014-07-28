package com.ah.be.performance.messagehandle.impl;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.event.BeL7SignatureFileVersionResultEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.messagehandle.MessageHandleInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;

public class HandleL7SignatureEvent implements MessageHandleInterface {
	static private HandleL7SignatureEvent instance = null;
	
	private HandleL7SignatureEvent() {
		
	}
	
	public static HandleL7SignatureEvent getInstance() {
		if(instance == null) {
			instance = new HandleL7SignatureEvent();
		}
		return instance;
	}
	
	public void handleMessage(BeBaseEvent event) {
		try {
			if(event.getClass() == BeL7SignatureFileVersionResultEvent.class) {
				handleL7SignatureFileVersionEvent(event);
				return;
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE, "MessageHandleThreadPool:Exception when handle message for "+event.getClass().getName(), e);
		}
	}
	
	private void handleL7SignatureFileVersionEvent(BeBaseEvent event) {
		BeL7SignatureFileVersionResultEvent resultEvent = (BeL7SignatureFileVersionResultEvent)event;
		
		//omit if Device is not exist or is not managed
		SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(resultEvent.getApMac());
		if (simpleHiveAp == null || simpleHiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			return;
		}

		try {
			BoMgmt.getHiveApMgmt().updateL7SignatureVersion(resultEvent.getApMac(), resultEvent.getL7SignatureFileVersion());
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE, "handleL7SignatureFileVersionEvent:Fail to update signature file version for AP "+resultEvent.getApMac(), e);
		}
	}
}