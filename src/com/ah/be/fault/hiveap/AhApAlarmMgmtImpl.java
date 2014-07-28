package com.ah.be.fault.hiveap;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.fault.BeFaultConst;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class AhApAlarmMgmtImpl implements AhApAlarmMgmt {

	private static final Tracer log = new Tracer(AhApAlarmMgmtImpl.class.getSimpleName());

	public AhApAlarmMgmtImpl() {
		super();
	}

	public void reachMaxApNum(HiveAp hiveAp, String... msg) {
		String desc = (msg.length <= 0 || msg[0] == null) ? MgrUtil.getUserMessage("warn.discovery.license.restriction", hiveAp
				.getMacAddress()) : msg[0];
		sendAlarm(hiveAp, desc);
	}

	public void vhmRestore(HiveAp hiveAp, boolean isNewApDiscovery) {
		String key = isNewApDiscovery ? "warn.new.ap.discovery.vhm.restoring"
				: "warn.existing.ap.discovery.vhm.restoring";
		String desc = MgrUtil.getUserMessage(key, new String[] { hiveAp.getOwner().getDomainName(),
				hiveAp.getMacAddress() });
		sendAlarm(hiveAp, desc);
	}

	public void vhmAbsence(HiveAp hiveAp, String configuredDomainName, boolean isHmolHomeDiscoveryDisabled) {
		String desc = isHmolHomeDiscoveryDisabled
				? MgrUtil.getUserMessage("warn.discovery.vhm.inexistence.home.discovery.disabled", new String[] { hiveAp.getMacAddress(), configuredDomainName })
				: MgrUtil.getUserMessage("warn.discovery.vhm.inexistence", configuredDomainName);
		sendAlarm(hiveAp, desc);
	}

	public void vhmDisable(HiveAp hiveAp, String disabledDomainName, boolean isNewDiscovery, boolean isHmolHomeDiscoveryDisabled) {
		String desc;

		if (isNewDiscovery) {
			if (isHmolHomeDiscoveryDisabled) {
				desc = MgrUtil.getUserMessage("warn.new.ap.discovery.vhm.disabled.home.discovery.disabled",
						new String[] { hiveAp.getMacAddress(), disabledDomainName });
			} else {
				desc = MgrUtil.getUserMessage("warn.new.ap.discovery.vhm.disabled",
						new String[] { disabledDomainName, hiveAp.getOwner().getDomainName() });
			}
		} else {
			desc = MgrUtil
					.getUserMessage("warn.existing.ap.discovery.vhm.disabled", new String[] {
							disabledDomainName, hiveAp.getMacAddress() });
		}

		sendAlarm(hiveAp, desc);
	}

	public void mismatchHiveApModel(HiveAp hiveAp, String discoveredModel, String configuredModel) {
		String desc = MgrUtil.getUserMessage("warn.discovery.ap.model.mismatch", new String[] {
				discoveredModel, configuredModel, hiveAp.getMacAddress() });
		sendAlarm(hiveAp, desc);
	}

	public void homeDiscoveryDisabled(HiveAp hiveAp) {
		String desc = MgrUtil.getUserMessage("warn.home.discovery.disabled", hiveAp.getMacAddress());
		sendAlarm(hiveAp, desc);
	}

	public void vhmConflict(HiveAp hiveAp, String serialNum, String importedDomainName,
			String configuredDomainName) {
		String desc = MgrUtil.getUserMessage("warn.discovery.vhm.assignment.conflict",
				new String[] { serialNum, importedDomainName, configuredDomainName });
		sendAlarm(hiveAp, desc);
	}

	private void sendAlarm(HiveAp hiveAp, String desc) {
		log.warning("sendAlarm", desc);
		HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_DISCOVERY, desc);
		AhAppContainer.getBeFaultModule().sendAlarm(hiveAp,
				(short) BeFaultConst.ALERT_SERVERITY_MAJOR, desc);
	}

}