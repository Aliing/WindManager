package com.ah.bo.mgmt;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.config.hiveap.ScriptConfigObject;
import com.ah.be.config.hiveap.UpdateObject;
import com.ah.be.db.configuration.ConfigurationProcessor.ConfigurationType;
import com.ah.be.db.discovery.event.AhDiscoveryEvent;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.hiveap.HiveApUpdateResult;

/*
 * @author Chris Scheers
 */
public interface HiveApMgmt {

	void init();

	void destroy();

	/**
	 * <p>
	 * Reset CAPWAP status for each HiveAp whose 'connected' attribute is 't' in
	 * the database. Actually, all the persistence HiveAp objects are considered
	 * to be disconnected with HiveManager after it starts.
	 * </p>
	 *
	 * @param domainId
	 *            identity of specified domain.
	 * @throws Exception
	 *             if any problem occurs while performing database operation.
	 */
	void resetCapwapStatus(Long domainId) throws Exception;

	/**
	 * Reset CAPWAP status for each HiveAp whose 'connected' attribute is 't' in
	 * the database. Actually, all the persistence HiveAp objects are considered
	 * to be disconnected with HiveManager after it starts, for the connected
	 * status, it must be connected in the home domain, now it's removed from
	 * home, and add into this domain.
	 *
	 * @param domainId
	 *            identity of specified domain.
	 * @throws Exception
	 *             if any problem occurs while performing database operation.
	 */
	void resetConnectStatusViaCAPWAP(Long domainId) throws Exception;

	/**
	 * Update hiveAp and update result when receive the CLI event.
	 *
	 * @param hiveAp
	 *            -
	 * @param ur
	 *            -
	 * @param sco
	 *            -
	 * @param newConfigVer
	 *            -
	 * @throws Exception
	 *             -
	 */
	void updateConfigResult(HiveAp hiveAp, HiveApUpdateResult ur, UpdateObject upObject,
			int newConfigVer) throws Exception;

	void updateUserDatabaseResult(HiveAp hiveAp, HiveApUpdateResult ur, ScriptConfigObject sco)
			throws Exception;

	void updateDelayTime(String apMac, int delayTime,
			short connectStatus) throws Exception;

	void updateImageResult(HiveAp hiveAp, HiveApUpdateResult ur) throws Exception;

	void updateSignatureResult(HiveAp hiveAp, HiveApUpdateResult ur) throws Exception;
	
	public void updateL7SignatureVersion(String hiveApMac, int signatureVer) throws Exception;

	void updateHiveApType(String hiveApMacAddr, short hiveApType) throws Exception;

	void updateHiveApRunningHive(String hiveApMac, String runningHive) throws Exception;

	/*
	 * Turn on checkout flag, which will generate a event while HiveAp next
	 * connect. After that, HiveAp updateManager module will receive this event,
	 * and do something needed.
	 *
	 * @param hiveAp
	 *            -
	 * @param sequenceNum
	 *            -
	 * @throws Exception
	 *             -
	 */
	//use APConnectionEvent instead
	//void updateStateToDelta(HiveAp hiveAp, int sequenceNum) throws Exception;

	void updateConfigurationIndication(HiveAp updateHiveAp) throws Exception;

//	void updateConfigurationIndication(String macAddress, int reportedConfigVer) throws Exception;

	void updateConfigurationIndicationForReboot(HiveAp rebootAp) throws Exception;

	void updateConfigurationIndication(HiveAp auditHiveAp, Date auditTime, boolean match,
			ConfigurationType type) throws Exception;

	void updateConfigurationIndication(Set<Long> ids, boolean pending, int pendingIndex,
			String desc, ConfigurationType type) throws Exception;

	// for bug 20171
	void updateConfigurationIndication(boolean pending, int pendingIndex,
			String desc, ConfigurationType type) throws Exception;
	
	void setAutoProvisioningConfig(HiveAp hiveAp, HiveApAutoProvision autoProvisioningConfig);

	void sendDiscoveryEvent(AhDiscoveryEvent discoveryEvent);

	void sendDiscoveryEvent(Collection<AhDiscoveryEvent> discoveryEvents);

	void updateDiscoveredHiveAps(Map<String, BeAPConnectEvent> reqsHolder);
	
	void updateDiscoveredPreviewConfigHiveAps(HiveAp ap, BeAPConnectEvent event, String oldMacaddress);

	HiveAp updateHiveApSeverity(String hiveApMac, Long domainId, short newSeverity)
			throws Exception;

	HiveAp updateAdminDtlsInfo(HiveAp hiveAp, String cfgAdmin, String cfgPsd) throws Exception;

	void updateLldpCdpInfo(String hiveApMac, String eth0DeviceId,
			String eth0PortId, String eth0SystemId, String eth1DeviceId,
			String eth1PortId, String eth1SystemId) throws Exception;

	void updateHiveApReportFlag(Collection<String> macs) throws Exception;
	
	void updateConfigIndicationForL7Device(Long ownerId, boolean pending,
			int pendingIndex, String desc, ConfigurationType type) throws Exception;

}