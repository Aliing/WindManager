package com.ah.be.performance.messagehandle.timer;

import java.util.List;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.event.BeSwitchPortStatsEvent;
import com.ah.be.communication.event.BeSwitchPortStatsReportEvent;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;

public class TimerQuerySwitchPortStats implements Runnable {
	
	public final static int			TIMER_PERIOD = 10*60;
	
	private byte CYCLE_APNUM = 10;

	private final short RELAXTIME = 1000;

	
	public TimerQuerySwitchPortStats() {
		try {
			CYCLE_APNUM = Byte.parseByte(ConfigUtil.getConfigInfo(
					ConfigUtil.SECTION_PERFORMANCE,
					ConfigUtil.KEY_POLLING_DEVICE_NUMBER_PER_SEC, "10"));
		}catch (Exception e) {
		}
	}
	
	@Override
	public void run() {
		Thread.currentThread().setName(this.getClass().getName());
		try {
			List<SimpleHiveAp> apList = CacheMgmt.getInstance()
					.getManagedApList();
			if (apList.isEmpty()) {
				return;
			}

			int index = 0;
			for (SimpleHiveAp ap : apList) {
				if (ap.isPhysicalSwitch()) {
					sendSwitchPortStatsQuery(ap);
					if (NmsUtil.compareSoftwareVersion(ap.getSoftVer(),"6.1.1.0") >= 0) {
						sendSwitchPortStatsReportQuery(ap);
					}
					if (++index == CYCLE_APNUM) {
						Thread.sleep(RELAXTIME);
						index = 0;
					}
				}
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,
					"Exception in query switch port stats timer thread", e);
		}
	}

	private void sendSwitchPortStatsQuery(SimpleHiveAp ap) {
		try {
			BeSwitchPortStatsEvent event = new BeSwitchPortStatsEvent();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			event.setSimpleHiveAp(ap);
			event.setSequenceNum(sequenceNum);
			event.buildPacket();
			HmBeCommunicationUtil.sendRequest(event);
		} catch (Exception e) {
		}
	}
	
	private void sendSwitchPortStatsReportQuery(SimpleHiveAp ap) {
		try {
			BeSwitchPortStatsReportEvent event = new BeSwitchPortStatsReportEvent();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			event.setSimpleHiveAp(ap);
			event.setSequenceNum(sequenceNum);
			event.buildPacket();
			HmBeCommunicationUtil.sendRequest(event);
		} catch (Exception e) {
		}
	}
}