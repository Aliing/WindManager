package com.ah.bo.mgmt;

import java.util.Map;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmPermission;

public interface DomainMgmt {

	/**
	 * create Domain API
	 *
	 * @param hmDomain -
	 * @return -
	 * @throws Exception -
	 */
	Long createDomain(HmDomain hmDomain) throws Exception;

	/**
	 * clone Domain API
	 *
	 * @param srcDomain -
	 * @param destDomain -
	 * @return -
	 * @throws Exception -
	 */
	Long cloneDomain(HmDomain srcDomain,HmDomain destDomain) throws Exception;

	Long cloneDomain(HmDomain srcDomain, HmDomain destDomain, boolean blnCloneFromCreate) throws Exception;

	/**
	 * remove Domain API
	 *
	 * @param domainId -
	 * @param bFlag -
	 * @throws Exception -
	 */
	void removeDomain(Long domainId, boolean bFlag) throws Exception;
	
	/**
	 * remove Domain API
	 *
	 * @param domainId -
	 * @param bFlag -
	 * @param resetFlag - reset device to default configuration.
	 * @throws Exception -
	 */
	void removeDomain(Long domainId, boolean bFlag, boolean resetFlag) throws Exception;

	/**
	 * update Domain API
	 *
	 * @param hmDomain -
	 * @throws Exception -
	 */
	void updateDomain(HmDomain hmDomain) throws Exception;
	
	String disableDomain(String domainName);

	String enableDomain(String domainName);

	/**
	 * return Home Domain
	 *
	 * @return -
	 */
	HmDomain getHomeDomain();

	/**
	 * set Home Domain, value from Parameter Module
	 *
	 * @param homeDomain -
	 */
	void setHomeDomain(HmDomain homeDomain);

	/**
	 * return Global Domain
	 *
	 * @return -
	 */
	String getGlobalDomainCondition();

	/**
	 * get Global Domain
	 *
	 * @return -
	 */
	HmDomain getGlobalDomain();
	/**
	 * set Global Domain, value from Parameter Module
	 *
	 * @param globalDomain -
	 */
	void setGlobalDomain(HmDomain globalDomain);

	/**
	 * get cached domain list
	 *
	 * @return -
	 * @throws Exception -
	 */
//	List<HmDomain> getSwitchDomains() throws Exception;

	/**
	 * get remaining ap number
	 *
	 * @return -
	 */
	int getRemainingMaxAPNum();

	/**
	 * get default group id for new VHM group
	 *
	 * @param idCount -
	 * @return -
	 */
	int[] generateGroupAttribute(int idCount);

	/**
	 * check group id exists or not
	 *
	 * @param groupId -
	 * @param owner -
	 * @param updateGroupName -
	 * @return -
	 */
	boolean checkGroupAttributeExist(int groupId, HmDomain owner, String updateGroupName);

	void createFeatureKeys();

	Map<String, HmPermission> getPermissionReadOnly();

	Map<String, HmPermission> getPermissionWrite();

	Map<String, HmPermission> getPermissionPlanning();

	Map<String, HmPermission> getGMPermission(String groupType);

	Map<String, HmPermission> getTeacherPermission();

	void createDomainRelevantDirs(String domainName, boolean deleteFirst);

	int getNonHomeDomainAPNum();

}