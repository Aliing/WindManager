package com.ah.be.performance.messagehandle.timer;

import java.util.List;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.event.BeApplicationFlowInfoEvent;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.appreport.AppFlowHelper;

public class TimerQueryApplicationFlow implements Runnable {
		
	private byte CYCLE_APNUM = 10;

	private final short RELAXTIME = 1000;
	
	private boolean debugFlag = false;
		
	public TimerQueryApplicationFlow() {
		try {
			CYCLE_APNUM = Byte.parseByte(ConfigUtil.getConfigInfo(
					ConfigUtil.SECTION_PERFORMANCE,
					ConfigUtil.KEY_POLLING_DEVICE_NUMBER_PER_SEC, "10"));
		}catch (Exception e) {
		}
	}

	public void setDebugFlag(boolean debugFlag) {
		this.debugFlag = debugFlag;
	}
	
	@Override
	public void run() {
		Thread.currentThread().setName(this.getClass().getName());
		try {
			List<Long> vhmIdList = AppFlowHelper.getRequestVhmIdList(debugFlag);
			List<SimpleHiveAp> apList = CacheMgmt.getInstance().getManagedApList();
			if (apList.isEmpty()) {
				return;
			}

			int index = 0;
			for (SimpleHiveAp ap : apList) {
				if (!vhmIdList.contains(ap.getDomainId())) {
					continue;
				}
				if (ap.getSoftVer().compareTo("6.0.2.0") < 0) {
					continue;
				}
				sendApplicationFlowRequest(ap);
				BeLogTools.info(HmLogConst.M_PERFORMANCE,
						"TimerQueryApplicationFlow send request success, ap : " + ap.getHostname() + "," + ap.getMacAddress());
				if (++index == CYCLE_APNUM) {
					Thread.sleep(RELAXTIME);
					index = 0;
				}
				 
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,
					"TimerQueryApplicationFlow send request exception ", e);
		}
	}
	
	private void sendApplicationFlowRequest(SimpleHiveAp ap) throws BeCommunicationEncodeException {
		BeApplicationFlowInfoEvent event = new BeApplicationFlowInfoEvent();
		int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
		event.setSimpleHiveAp(ap);
		event.setSequenceNum(sequenceNum);
		event.buildPacket();
		HmBeCommunicationUtil.sendRequest(event);
	}
	
}