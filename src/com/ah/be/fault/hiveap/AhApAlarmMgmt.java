package com.ah.be.fault.hiveap;

import com.ah.bo.hiveap.HiveAp;

public interface AhApAlarmMgmt {

	void reachMaxApNum(HiveAp hiveAp, String... msg);

	void vhmRestore(HiveAp hiveAp, boolean isNewApDiscovery);

	void vhmAbsence(HiveAp hiveAp, String specifiedDomainName, boolean isHmolHomeDiscoveryDisabled);

	void vhmDisable(HiveAp hiveAp, String disabledDomainName, boolean isNewDiscovery, boolean isHmolHomeDiscoveryDisabled);

	void mismatchHiveApModel(HiveAp hiveAp, String discoveredModel, String configuredModel);

	void homeDiscoveryDisabled(HiveAp hiveAp);

	void vhmConflict(HiveAp hiveAp, String serialNum, String importedDomainName,
			String configuredDomainName);

}