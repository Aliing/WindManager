package com.ah.be.sync;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.bo.hhm.SyncTaskOnHmol;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;

public class SyncTaskTimer implements Runnable {

	private final static Log log = LogFactory.getLog("commonlog.SyncTaskTimer");

	@Override
	public void run() {
		try {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			processSyncToPortal();
		} catch (Exception e) {
			log.error("SyncTaskTimer run failed!", e);
		}
	}

	private void processSyncToPortal() {
		if (!HmBeCommunicationUtil.isConnectedToPortal()) {
			log.warn("the portal is not connected! now cannot do sync task.");
			return;
		}

		List<?> aas = QueryUtil.executeQuery("select distinct vhmName from " + SyncTaskOnHmol.class.getSimpleName(), 1000);

		for (Object vhmName : aas) {
			if (!HmBeCommunicationUtil.isConnectedToPortal()) {
				log.warn("the portal is not connected! now cannot do sync task.");
				return;
			}

			if (TaskProcess.isDoingSync((String) vhmName)) {
				log.warn("the domain " + vhmName + " is syncing, ignore");
				continue;
			}
			TaskProcess.setDoingSync((String) vhmName);

			Thread proc = new TaskProcess((String) vhmName);
			proc.setName("sync to portal, domain is " + vhmName);
			proc.start();
		}
	}

}