package com.ah.bo.mgmt;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.bo.hiveap.Idp;

public interface IdpMgmt {

	/*
	 * Add a list of IDPs
	 */
	void addIdps(Map<String, Idp> idps, String reportHiveApNodeId, Long domainId)
			throws Exception;

	/*
	 * Update IDP
	 */
	void updateIdp(Idp idp) throws Exception;

	/*
	 * Remove all the IDPs reported by specify HiveAP
	 */
	void removeIdps(String reportHiveApNodeId, Long domainId);

	/*
	 * Remove specify IDPs reported by specify HiveAP
	 */
	void removeIdps(List<String> bssids, String reportHiveApNodeId,
			Long domainId) throws Exception;

	/*
	 * Remove a list of IDPs
	 */
	int removeIdps(Collection<Long> id, Long domainId) throws Exception;

	/*
	 * Remove a list of IDPs which are mitigated by rogue ap
	 */
	void removeMitigationIdps(List<String> bssids, String reportHiveApNodeId,
			Long domainId) throws Exception;

	/*
	 * Update a list of IDPs mitigation flag
	 */
	void updateMitigationFlag(List<String> bssids, String reportHiveApNodeId,
			boolean mitigated) throws Exception;

	/*
	 * get the rogue AP refresh interval value
	 */
	int getRefreshInterval(Long domainId);

	/*
	 * get the threshold value indicate strong/weak RSSI
	 */
	int getSignalThreshold(Long domainId);

	/*
	 * get the manually moved rogue APs
	 */
	Set<String> getEnclosedRogueAps(Long domainId);

	/*
	 * get the manually moved friendly APs
	 */
	Set<String> getEnclosedFriendlyAps(Long domainId);

	/*
	 * add newly bssid as rogueAps, return the total rogueAps
	 */
	Set<String> addEnclosedRogueAps(Set<String> bssids, Long domainId);

	/*
	 * add newly bssid as friendlyAps, return the total friendlyAps
	 */
	Set<String> addEnclosedFriendlyAps(Set<String> bssids, Long domainId);

	/*
	 * remove a set of bssid as rogueAps, return the total rogueAps
	 */
	Set<String> removeEnclosedRogueAps(Set<String> bssids, Long domainId);

	/*
	 * remove a set of bssid as friendlyAps, return the total friendlyAps
	 */
	Set<String> removeEnclosedFriendlyAps(Set<String> bssids, Long domainId);

	/*
	 * update the rogue AP refresh interval value
	 */
	boolean updateRefreshInterval(Long domainId, int value);

	/*
	 * update the threshold value indicate strong/weak RSSI
	 */
	boolean updateSignalThreshold(Long domainId, int value);

	/*
	 * update the IDP location on the map
	 */
	void updateIdpLocation(Set<String> bssids, Point2D xy, Long mapId, Long domainId);

	void updateManagedHiveAPBssidFilter(Long domainId, boolean filter);
	
	boolean getManagedHiveAPBssidFilter(Long domainId);
	
	int removeIdpsByBssid(Collection<String> bssidList, Long domainId) throws Exception;
}
