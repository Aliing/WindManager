package com.ah.be.common.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ah.be.admin.restoredb.AhRestoreCommons;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.PresenceUtil;
import com.ah.be.performance.BeOsInfoProcessor;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.OsVersion;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhClientEditValues;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhMaxClientsCount;
import com.ah.ui.actions.monitor.SystemStatusCache;
import com.ah.util.Tracer;

public class CacheMgmt {

	private static final Tracer											log			= new Tracer(
																							CacheMgmt.class
																									.getSimpleName());

	private static CacheMgmt											cacheMgmt;

	/**
	 * domainID - <apMac - simpleHiveAP>
	 */
	private final Map<Long, Map<String, SimpleHiveAp>>					hiveApCache;
	private final Map<Long, HmDomain>									domainCache;
	private HmDomain													globalDomain = null;
	private final ConcurrentMap<String, AhClientEditValues>				clientEditValuesCache;

	/**
	 * domainID - AhMaxClientsCount
	 */
	private final ConcurrentMap<Long, AhMaxClientsCount>				maxClientsCountMap;
	
	
	// for user define display time format
	private final ConcurrentMap<Long, HMServicesSettings>				hmServiceSettingForTimeZoneMap;

	public static final Long											TOTAL_KEY	= (long) 0;
	
	/**
	 * status about Student Manager
	 */
	private final StudentManagerInfo									smInfo = new StudentManagerInfo();

	/**
	 * domainID - <clientOsInfo - dhcp-option55>
	 */
	private final Map<Long, Map<String, String>>						clientOsInfoCache;
	
	private CacheMgmt() {
		hiveApCache = Collections.synchronizedMap(new HashMap<Long, Map<String, SimpleHiveAp>>(25));
		domainCache = Collections.synchronizedMap(new HashMap<Long, HmDomain>(25));

		long count = QueryUtil.findRowCount(AhClientEditValues.class, null);
		clientEditValuesCache = new ConcurrentHashMap<String, AhClientEditValues>(Math
				.max(20, (int) count));

		maxClientsCountMap = new ConcurrentHashMap<Long, AhMaxClientsCount>();
		
		hmServiceSettingForTimeZoneMap = new ConcurrentHashMap<Long, HMServicesSettings>();
		
		clientOsInfoCache = Collections.synchronizedMap(new HashMap<Long, Map<String, String>>(25));
	}

	public static CacheMgmt getInstance() {
		if(null == cacheMgmt) {
			cacheMgmt	= new CacheMgmt();
		}
		return cacheMgmt;
	}

	public void init() {
		log.info("init", "Initializing domain cache...");
		initHmDomainCache();
		log.info("init", "Domain cache initialized.");

		log.info("init", "Initializing AP cache...");
		initSimleHiveApCache();
		log.info("init", "AP cache initialized.");

		// Initialize system status cache
		log.info("init", "Initializing system status cache...");
		SystemStatusCache.getInstance().init(getCacheDomains());
		log.info("init", "System status cache initiasylized.");

		log.info("init", "Initializing client edit value cache...");
		initClientEditValuesCache();
		log.info("init", "Client edit value cache initialized.");

		log.info("init", "Initializing max client count map cache...");
		initMaxClientsCountMap();
		log.info("init", "Max client count map cached initialized.");
		
		log.info("init", "Initializing client OS detection cache...");
		initClientOsInfoCache();
		log.info("init", "Client OS detection cached initialized.");
		
		log.info("init", "Initializing OS object cache...");
		BeOsInfoProcessor.getInstance().initOsInfoMap();
		log.info("init", "OS object cached initialized.");
	}

	public Map<Long,Map<String,String>> initClientOsInfoCache() {
		clientOsInfoCache.clear();
		FilterParams filterParams = new FilterParams("option55 is not null and osversion != :s1",new Object[]{""});
		List<OsVersion> clientOsInfoList = QueryUtil.executeQuery(OsVersion.class, new SortParams("owner"), filterParams);
			
		Map<String,String> os_version_map = null;
		for(OsVersion osversion : clientOsInfoList){
			long key = osversion.getOwner().getId();
			if(clientOsInfoCache.get(key) == null || 
					clientOsInfoCache.get(key).isEmpty()){
				os_version_map = new HashMap<String,String>();
				os_version_map.put(osversion.getOption55(), osversion.getOsVersion());
				clientOsInfoCache.put(key, os_version_map);
			} else {
				os_version_map = clientOsInfoCache.get(key);
				os_version_map.put(osversion.getOption55(), osversion.getOsVersion());
				clientOsInfoCache.put(key, os_version_map);
			}
		}	
		
		return clientOsInfoCache;
	}
	
	public String getClientOsInfoFromCacheByOption55(String option55,HmDomain hmDomain) {
		String clientOsInfo = null;
		HmDomain gDomain = getCacheDomainByName(HmDomain.GLOBAL_DOMAIN);
		Map<String,String> vhmMap = clientOsInfoCache.get(hmDomain.getId());
		Map<String,String> globalMap = clientOsInfoCache.get(gDomain.getId());
		if(vhmMap == null || vhmMap.isEmpty()){
			if(globalMap != null && !globalMap.isEmpty()){
				clientOsInfo = globalMap.get(option55);
			}
		} else {
			clientOsInfo = vhmMap.get(option55);
			if(clientOsInfo == null || clientOsInfo.isEmpty()){
				clientOsInfo = globalMap.get(option55);
			}
		}
		return clientOsInfo;
	}
	
	public String getClientOsInfoFromCacheByOsVersion(String osversion,HmDomain hmDomain) {
		String clientOsInfo = null;
		HmDomain gDomain = getCacheDomainByName(HmDomain.GLOBAL_DOMAIN);
		Map<String,String> vhmMap = clientOsInfoCache.get(hmDomain.getId());
		Map<String,String> globalMap = clientOsInfoCache.get(gDomain.getId());
			
		if(vhmMap == null || vhmMap.isEmpty()){
			if(globalMap != null && !globalMap.isEmpty()){
				if(globalMap.containsValue(osversion)){
					clientOsInfo = osversion;
				}
			}
		} else {
			if(vhmMap.containsValue(osversion)){
				clientOsInfo = osversion;
			}
		}
		return clientOsInfo;
	}
	
	public void updateClientOsInfoToCache(String osversion,String option55,HmDomain hmDomain) {
		Map<String,String> os_version_map;
		if(clientOsInfoCache.get(hmDomain.getId()) == null || 
				clientOsInfoCache.get(hmDomain.getId()).isEmpty()){
			os_version_map = new HashMap<String,String>();
			os_version_map.put(option55, osversion);
			clientOsInfoCache.put(hmDomain.getId(), os_version_map);
		} else {
			os_version_map = clientOsInfoCache.get(hmDomain.getId());
			if(os_version_map.containsKey(option55)){
				os_version_map.remove(option55);
			}
			os_version_map.put(option55, osversion);
			clientOsInfoCache.put(hmDomain.getId(), os_version_map);
		}
	}
	
	public void removeClientOsInfoToCache(String osversion,String option55,HmDomain hmDomain) {
		Map<String,String> os_version_map;
	
			os_version_map = clientOsInfoCache.get(hmDomain.getId());
			if(os_version_map.containsKey(option55)){
				os_version_map.remove(option55);
			}
	}

	public void initMaxClientsCountMap() {
		maxClientsCountMap.clear();

		int totalCount = 0;
		for (Long domainID : domainCache.keySet()) {
			AhMaxClientsCount bo = new AhMaxClientsCount();
			bo.setMaxClientCount(caculateClientsCount(domainID));
			bo.setCurrentClientCount(bo.getMaxClientCount());
			bo.setClient24Count(caculateClientsCount(domainID, AhInterfaceStats.RADIOTYPE_24G));
			bo.setClient5Count(caculateClientsCount(domainID, AhInterfaceStats.RADIOTYPE_5G));
			bo.setClientwiredCount(caculateClientsCount(domainID, AhInterfaceStats.RADIOTYPE_OTHER));
			bo.setTotalCount(bo.getClient24Count() +  bo.getClient5Count() + bo.getClientwiredCount());
			bo.setOwner(domainCache.get(domainID));
			maxClientsCountMap.put(domainID, bo);

			totalCount += bo.getMaxClientCount();
		}

		// for all scope
		AhMaxClientsCount bo = new AhMaxClientsCount();
		bo.setMaxClientCount(totalCount);
		bo.setCurrentClientCount(totalCount);
		bo.setGlobalFlg(true);
		bo.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		maxClientsCountMap.put(TOTAL_KEY, bo);
	}

	public int caculateClientsCount(Long domainID) {
		Map<String, SimpleHiveAp> apCache = hiveApCache.get(domainID);
		int totalCount = 0;
		for (SimpleHiveAp ap : apCache.values()) {
			totalCount += ap.getActiveClientCount();
		}

		return totalCount;
	}
	
	public int caculateClientsCount(Long domainID, int radioMode) {
		Map<String, SimpleHiveAp> apCache = hiveApCache.get(domainID);
		int totalCount = 0;
		for (SimpleHiveAp ap : apCache.values()) {
			if (radioMode==AhInterfaceStats.RADIOTYPE_24G) {
				totalCount += ap.getClient24Count();
			} else if (radioMode==AhInterfaceStats.RADIOTYPE_5G) {
				totalCount += ap.getClient5Count();
			} else if (radioMode==AhInterfaceStats.RADIOTYPE_OTHER) {
				totalCount += ap.getClientWireCount();
			}
		}

		return totalCount;
	}

	public int caculateTotalClientCount() {
		int totalCount = 0;
		for (Long domainID : hiveApCache.keySet()) {
			totalCount += caculateClientsCount(domainID);
		}

		return totalCount;
	}

	public void initClientEditValuesCache() {
		clientEditValuesCache.clear();

		try {
			List<AhClientEditValues> list = QueryUtil.executeQuery(AhClientEditValues.class, null,
					null);
			for (AhClientEditValues clientEditValue : list) {
				clientEditValuesCache.put(getClientEditVlaueKey(clientEditValue), clientEditValue);
			}
		} catch (Exception e) {
			log.error("initClientEditValuesCache", "initClientEditValuesCache error.", e);
		}
	}

	private void initClientEditValuesCache(Long domainID) {
		try {
			List<AhClientEditValues> list = QueryUtil.executeQuery(AhClientEditValues.class, null,
					new FilterParams("owner.id", domainID));
			for (AhClientEditValues clientEditValue : list) {
				clientEditValuesCache.put(getClientEditVlaueKey(clientEditValue), clientEditValue);
			}
		} catch (Exception e) {
			log.error("initClientEditValuesCache", "initClientEditValuesCache error.", e);
		}
	}

	public void addClientEditValues(List<AhClientEditValues> list) {
		for (AhClientEditValues value : list) {
			clientEditValuesCache.put(getClientEditVlaueKey(value), value);
		}
		ReportCacheMgmt.getInstance().updateClientInfoByManual(list);
	}

	public void addClientEditValues(AhClientEditValues value) {
		clientEditValuesCache.put(getClientEditVlaueKey(value),value);
	}

	public AhClientEditValues getClientEditValues(String clientMac, HmDomain domain) {
		return clientEditValuesCache.get(getClientEditVlaueKey(clientMac, domain.getId(),AhClientEditValues.TYPE_USER_ADD,""));
	}
	
	public AhClientEditValues getClientEditValues(String clientMac, HmDomain domain,Short type,String ssidName) {
		return clientEditValuesCache.get(getClientEditVlaueKey(clientMac, domain.getId(),type,ssidName));
	}

	public void removeClientEditValues(List<AhClientEditValues> list) {
		for (AhClientEditValues value : list) {
			clientEditValuesCache.remove(getClientEditVlaueKey(value));
		}
	}

	public void removeClientEditValues(List<String> clientMacList, HmDomain domain) {
		for (String clientMac : clientMacList) {
			clientEditValuesCache.remove(getClientEditVlaueKey(clientMac, domain.getId(),AhClientEditValues.TYPE_USER_ADD,""));
		}
	}

	public void removeClientEditValues(Long domainID) {
		List<AhClientEditValues> list = new ArrayList<AhClientEditValues>();
		for(AhClientEditValues value : clientEditValuesCache.values()) {
			if(value.getOwner().getId().equals(domainID))
				list.add(value);
		}
		removeClientEditValues(list);
	}
	
	public void removeClientEditValues(Long domainID,short type) {
		List<AhClientEditValues> list = new ArrayList<AhClientEditValues>();
		for(AhClientEditValues value : clientEditValuesCache.values()) {
			if(value.getOwner().getId().equals(domainID) &&
				value.getType() == type)
				list.add(value);
		}
		removeClientEditValues(list);
	}
	
	public void removeClientEditValues(Long domainID,short type,String ssidName) {
		List<AhClientEditValues> list = new ArrayList<AhClientEditValues>();
		for(AhClientEditValues value : clientEditValuesCache.values()) {
			if(value.getOwner().getId().equals(domainID) &&
				value.getType() == type &&
				value.getSsidName().equalsIgnoreCase(ssidName))
				list.add(value);
		}
		removeClientEditValues(list);
	}

	public void removeCacheValues(Long domainId) {
		if (null == domainId) {
			return;
		}
		removeSimpleHiveApCache(domainId);
		removeHmDomainCache(domainId);
		removeClientEditValues(domainId);
		BeOsInfoProcessor.getInstance().removeVhmOsName(domainId);
		// remove systemStatusCache of this domain
		SystemStatusCache.getInstance().removeCacheByDomain(domainId);
		
	}

	public void initCacheValues(Long lDomainid) {
		long start = System.currentTimeMillis();

		// Long domainId = domain.getId();
		removeCacheValues(lDomainid);
		initSimpleHiveApCache(lDomainid);
		initHmDomainCache(lDomainid);
		initClientEditValuesCache(lDomainid);
		BeOsInfoProcessor.getInstance().resetOsName(lDomainid);
		// initialize SystemStatusCache for this new domain
		SystemStatusCache.getInstance().initCacheByDomain(lDomainid);
		// init presence cache
		PresenceUtil.initPresenceCustomerCache();
		long end = System.currentTimeMillis();
		log.info("initCacheValues", "initialize [" + lDomainid + "] Cache Values cost:"
				+ (end - start) + " ms.");
	}

	public void updateHmDomainCache(HmDomain domain) {
		if (null == domain) {
			return;
		}
		if (domainCache.isEmpty()) {
			initHmDomainCache();
		} else {
			synchronized (domainCache) {
				domain = QueryUtil.findBoById(HmDomain.class, domain.getId());
				domainCache.put(domain.getId(), domain);
			}
		}
	}

	/**
	 * when a new HiveAP add into database, invoke this function to synchronize the Cache values;
	 * 
	 * @param hiveAp
	 *            -
	 */
	public void addSimpleHiveAp(HiveAp hiveAp) {
		if (null == hiveAp) {
			return;
		}
		Long domainId = hiveAp.getOwner().getId();
		synchronized (hiveApCache) {
			Map<String, SimpleHiveAp> hiveAps = hiveApCache.get(domainId);
			if (null != hiveAps) {
				SimpleHiveAp sHiveAp = getSimpleHiveAp(hiveAp);
				SimpleHiveAp pHiveAp = hiveAps.put(hiveAp.getMacAddress(), sHiveAp);
				if (null != pHiveAp) {
					// put previous active client count to the new ap.
					hiveAps.get(hiveAp.getMacAddress()).setActiveClientCount(
							pHiveAp.getActiveClientCount());
					hiveAps.get(hiveAp.getMacAddress()).setClient24Count(
							pHiveAp.getClient24Count());
					hiveAps.get(hiveAp.getMacAddress()).setClient5Count(
							pHiveAp.getClient5Count());
					hiveAps.get(hiveAp.getMacAddress()).setClientWireCount(
							pHiveAp.getClientWireCount());
				}
			}
		}
	}

	public void bulkAddHiveAps(Collection<HiveAp> hiveAps) {
		if (hiveAps == null) {
			return;
		}

		synchronized (hiveApCache) {
			for (HiveAp hiveAp : hiveAps) {
				Long domainId = hiveAp.getOwner().getId();
				Map<String, SimpleHiveAp> domainHiveAps = hiveApCache.get(domainId);

				if (domainHiveAps != null) {
					SimpleHiveAp apInfo = getSimpleHiveAp(hiveAp);
					SimpleHiveAp pHiveAp = domainHiveAps.put(hiveAp.getMacAddress(), apInfo);
					if (null != pHiveAp) {
						// put previous active client count to the new ap.
						domainHiveAps.get(hiveAp.getMacAddress()).setActiveClientCount(
								pHiveAp.getActiveClientCount());
						domainHiveAps.get(hiveAp.getMacAddress()).setClient24Count(
								pHiveAp.getClient24Count());
						domainHiveAps.get(hiveAp.getMacAddress()).setClient5Count(
								pHiveAp.getClient5Count());
						domainHiveAps.get(hiveAp.getMacAddress()).setClientWireCount(
								pHiveAp.getClientWireCount());
					}
				}
			}
		}
	}

	/**
	 * The following value has been changed need to invoke this function to synchronize the Cache
	 * values; <li>HiveAP domainId; <li>HiveAP macAddress; <li>HiveAP host name; <li>HiveAP
	 * serialNumber; <li>HiveAP manageStatus; <li>HiveAP softVer; <li>HiveAP map container;
	 * 
	 * @param hiveAp
	 *            -
	 * 
	 */
	public void updateSimpleHiveAp(HiveAp hiveAp) {
		addSimpleHiveAp(hiveAp);
	}

	public void bulkUpdateHiveAps(Collection<HiveAp> hiveAps) {
		bulkAddHiveAps(hiveAps);
	}

	/**
	 * when a HiveAP removed from database, invoke this function to synchronize the Cache values;
	 * 
	 * @param hiveAp
	 *            -
	 */
	public void removeSimpleHiveAp(HiveAp hiveAp) {
		if (null == hiveAp) {
			return;
		}
		Long domainId = hiveAp.getOwner().getId();
		synchronized (hiveApCache) {
			Map<String, SimpleHiveAp> hiveAps = hiveApCache.get(domainId);
			if (hiveAps != null) {
				hiveAps.remove(hiveAp.getMacAddress());
			}
		}
	}

	/**
	 * The active client associate with HiveAP has changed, invoke this function to synchronize the
	 * Cache values;
	 * 
	 * @param hiveAp
	 *            -
	 * @param delta
	 *            - new added count
	 */
	public void activeClientAdd(SimpleHiveAp hiveAp, int delta, int radioMode) {
		if (null == hiveAp) {
			return;
		}
		Long domainId = hiveAp.getDomainId();
		synchronized (hiveApCache) {
			Map<String, SimpleHiveAp> hiveAps = hiveApCache.get(domainId);
			if (null != hiveAps) {
				SimpleHiveAp sHiveAp = hiveAps.get(hiveAp.getMacAddress());
				if (null != sHiveAp) {
					sHiveAp.setActiveClientCount(sHiveAp.getActiveClientCount() + delta);
					if (sHiveAp.getActiveClientCount() < 0) {
						sHiveAp.setActiveClientCount(0);
						log.error("activeClientAdd", "active client count on HiveAp:"
								+ hiveAp.getHostname() + " is negative!! Reset it to zero.");
					}
					if (radioMode==AhInterfaceStats.RADIOTYPE_24G) {
						sHiveAp.setClient24Count(sHiveAp.getClient24Count() + delta);
						if (sHiveAp.getClient24Count() < 0) {
							sHiveAp.setClient24Count(0);
							log.error("activeClientAdd", "2.4GHz active client count on HiveAp:"
									+ hiveAp.getHostname() + " is negative!! Reset it to zero.");
						}
					} else if (radioMode==AhInterfaceStats.RADIOTYPE_5G) {
						sHiveAp.setClient5Count(sHiveAp.getClient5Count() + delta);
						if (sHiveAp.getClient5Count() < 0) {
							sHiveAp.setClient5Count(0);
							log.error("activeClientAdd", "5GHz active client count on HiveAp:"
									+ hiveAp.getHostname() + " is negative!! Reset it to zero.");
						}
					} else if (radioMode==AhInterfaceStats.RADIOTYPE_OTHER) {
						sHiveAp.setClientWireCount(sHiveAp.getClientWireCount() + delta);
						if (sHiveAp.getClientWireCount() < 0) {
							sHiveAp.setClientWireCount(0);
							log.error("activeClientAdd", "wired active client count on HiveAp:"
									+ hiveAp.getHostname() + " is negative!! Reset it to zero.");
						}
					}
				}
			}
		}

//		if (delta > 0) {
			// for domain counter
			AhMaxClientsCount countBo = maxClientsCountMap.get(domainId);
			if (countBo == null) {
				countBo = new AhMaxClientsCount();
				countBo.setOwner(domainCache.get(domainId));
				maxClientsCountMap.put(domainId, countBo);
			}

			int oldCount = countBo.getMaxClientCount();
			int newCount = caculateClientsCount(domainId);
			if (oldCount <= newCount) {
				countBo.setMaxClientCount(newCount);
				int newCountWire = caculateClientsCount(domainId, AhInterfaceStats.RADIOTYPE_OTHER);
				int newCount24 = caculateClientsCount(domainId, AhInterfaceStats.RADIOTYPE_24G);
				int newCount5 = caculateClientsCount(domainId, AhInterfaceStats.RADIOTYPE_5G);
				countBo.setClient24Count(newCount24);
				countBo.setClient5Count(newCount5);
				countBo.setClientwiredCount(newCountWire);
				countBo.setTotalCount(newCountWire + newCount24 + newCount5);
				countBo.setTimeStamp(System.currentTimeMillis());
			}
			countBo.setCurrentClientCount(newCount);

			// for total counter
			AhMaxClientsCount totalCountBo = maxClientsCountMap.get(TOTAL_KEY);
			oldCount = totalCountBo.getMaxClientCount();
			newCount = caculateTotalClientCount();
			if (oldCount <= newCount) {
				totalCountBo.setMaxClientCount(newCount);
				totalCountBo.setTimeStamp(System.currentTimeMillis());
			}
			totalCountBo.setCurrentClientCount(newCount);
//		}
	}

	/**
	 * The active client associate with HiveAP has changed, invoke this function to synchronize the
	 * Cache values;
	 * 
	 * @param hiveAp
	 *            -
	 * @param delta
	 *            - removed count
	 */
	public void activeClientRemove(SimpleHiveAp hiveAp, int delta, int radioMode) {
		activeClientAdd(hiveAp, -delta, radioMode);
	}

	private void initHmDomainCache(Long domainId) {
		HmDomain domain = QueryUtil.findBoById(HmDomain.class, domainId);
		if (null != domain) {
			synchronized (domainCache) {
				domainCache.put(domain.getId(), domain);
			}
		}
	}

	/**
	 * Get simpleHiveAp by macAddress, it will search for the all domain, and return the HiveAP is
	 * in Managed/New list.
	 * 
	 * @param macAddress
	 *            -
	 * @return -
	 */
	public SimpleHiveAp getSimpleHiveAp(String macAddress) {
		if (null == macAddress || "".equals(macAddress.trim())) {
			return null;
		}

		for (Map<String, SimpleHiveAp> domainHiveAps : hiveApCache.values()) {
			SimpleHiveAp ap = domainHiveAps.get(macAddress);
			if (null != ap) {
				return ap;
			}
		}
		return null;
	}

	/**
	 * return ap list given map Id
	 * 
	 * @param mapId
	 *            -
	 * @return -
	 */
	public List<SimpleHiveAp> getApListByMapContainer(Long mapId) {
		List<SimpleHiveAp> list = new ArrayList<SimpleHiveAp>();

		for (Map<String, SimpleHiveAp> domainHiveAps : hiveApCache.values()) {
			if (null == domainHiveAps) {
				continue;
			}

			for (SimpleHiveAp hiveAp : domainHiveAps.values()) {
				if (null != hiveAp) {
					if ((mapId == null && hiveAp.getMapContainerId() == null)
							|| (mapId != null && mapId.equals(hiveAp.getMapContainerId()))) {
						list.add(hiveAp);
					}
				}
			}
		}

		return list;
	}

	public List<SimpleHiveAp> getApListByMapContainer(Long mapId, Long domainId) {
		List<SimpleHiveAp> apList = new ArrayList<SimpleHiveAp>();

		Map<String, SimpleHiveAp> apMap = hiveApCache.get(domainId);
		if (apMap == null || apMap.size() == 0) {
			return apList;
		}

		for (SimpleHiveAp hiveAp : apMap.values()) {
			if (null != hiveAp) {
				if ((mapId == null && hiveAp.getMapContainerId() == null)
						|| (mapId != null && mapId.equals(hiveAp.getMapContainerId()))) {
					apList.add(hiveAp);
				}
			}
		}

		return apList;
	}

	/**
	 * Get all simple HiveAP which are managed status for all the domains.
	 * 
	 * @return -
	 */
	public List<SimpleHiveAp> getManagedApList() {
		List<SimpleHiveAp> list = new ArrayList<SimpleHiveAp>();
		for (Map<String, SimpleHiveAp> domainHiveAps : hiveApCache.values()) {
			if (null == domainHiveAps) {
				continue;
			}
			for (SimpleHiveAp hiveAp : domainHiveAps.values()) {
				if (null != hiveAp && HiveAp.STATUS_MANAGED == hiveAp.getManageStatus()) {
					list.add(hiveAp);
				}
			}
		}
		//log.info("getManagedApList", "Managed status SimpleHiveAP size:" + list.size());
		return list;
	}
	
	/**
	 * Get all simple HiveAP for all the domains.
	 * 
	 * @return -
	 */
	public List<SimpleHiveAp> getAllApList() {
		List<SimpleHiveAp> list = new ArrayList<SimpleHiveAp>();
		for (Map<String, SimpleHiveAp> domainHiveAps : hiveApCache.values()) {
			if (null == domainHiveAps) {
				continue;
			}
			for (SimpleHiveAp hiveAp : domainHiveAps.values()) {
				if (null != hiveAp) {
					list.add(hiveAp);
				}
			}
		}
		return list;
	}

	/**
	 * Get all simple HiveAP which are managed status for given domains.
	 * 
	 * @param domainId
	 *            -
	 * @return -
	 */
	public List<SimpleHiveAp> getManagedApList(Long domainId) {
		List<SimpleHiveAp> list = new ArrayList<SimpleHiveAp>();
		Map<String, SimpleHiveAp> domainHiveAps = hiveApCache.get(domainId);
        if (null == domainHiveAps) {
            return list;
        }
		for (SimpleHiveAp hiveAp : domainHiveAps.values()) {
			if (null != hiveAp && HiveAp.STATUS_MANAGED == hiveAp.getManageStatus()) {
				list.add(hiveAp);
			}
		}
		return list;
	}
	
	/**
	 * Get all simple HiveAP for given domains.
	 * 
	 * @param domainId
	 *            -
	 * @return -
	 */
	public List<SimpleHiveAp> getAllApList(Long domainId) {
		List<SimpleHiveAp> list = new ArrayList<SimpleHiveAp>();
		Map<String, SimpleHiveAp> domainHiveAps = hiveApCache.get(domainId);
        if (null == domainHiveAps) {
            return list;
        }
		for (SimpleHiveAp hiveAp : domainHiveAps.values()) {
			if (null != hiveAp) {
				list.add(hiveAp);
			}
		}
		return list;
	}

	public int getApCount(Long domainId) {
		Map<String, SimpleHiveAp> domainHiveAps = hiveApCache.get(domainId);
        if (null == domainHiveAps) {
            return 0;
        }
        return domainHiveAps.size();
	}
	
	private void initSimpleHiveApCache(Long domainId) {
		long start = System.currentTimeMillis();
		List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams(
				"owner.id", domainId));
		synchronized (hiveApCache) {
			build_up(hiveAps, domainId, hiveApCache);
//			String query = "select count(bo),bo.apMac from AhClientSession bo where bo.owner.id = "
//					+ domainId + "AND bo.connectstate=" + AhClientSession.CONNECT_STATE_UP
//					+ " group by bo.apMac";
//
//			List<?> list = QueryUtil.executeQuery(query, Paging.MAX_RESULTS);
			String query = "select count(*),apMac from ah_clientsession where owner = "
				+ domainId + "AND connectstate=" + AhClientSession.CONNECT_STATE_UP
				+ " group by apMac";

			List<?> list = DBOperationUtil.executeQuery(query);
			log.info("initSimpleHiveApCache", "active client list size of HiveAP:" + list.size());
			for (Object object : list) {
				Object[] attributes = (Object[]) object;
				Long count = AhRestoreCommons.convertLong(attributes[0].toString());
				String macAddress = (String) attributes[1];
				if (null == macAddress || "".equals(macAddress)) {
					continue;
				}
				SimpleHiveAp sp = getSimpleHiveAp(macAddress);
				if (null != sp && null != count) {
					sp.setActiveClientCount(count.intValue());
				}
			}
						
			String queryWire = "select count(*),apMac from ah_clientsession where owner = "
					+ domainId + "AND connectstate=" + AhClientSession.CONNECT_STATE_UP
					+ " and wirelessClient=false"
					+ " group by apMac";

			List<?> listWire = DBOperationUtil.executeQuery(queryWire);
			log.info("initSimpleHiveApCache", "Wire active client list size of HiveAP:" + list.size());
			for (Object object : listWire) {
				Object[] attributes = (Object[]) object;
				Long count = AhRestoreCommons.convertLong(attributes[0].toString());
				String macAddress = (String) attributes[1];
				if (null == macAddress || "".equals(macAddress)) {
					continue;
				}
				SimpleHiveAp sp = getSimpleHiveAp(macAddress);
				if (null != sp && null != count) {
					sp.setClientWireCount(count.intValue());
				}
			}
				
			String query24 = "select count(*),apMac from ah_clientsession where owner = "
					+ domainId + "AND connectstate=" + AhClientSession.CONNECT_STATE_UP
					+ " and wirelessClient=true and (clientMACProtocol=" + AhAssociation.CLIENTMACPROTOCOL_BMODE
					+ " or clientMACProtocol=" + AhAssociation.CLIENTMACPROTOCOL_GMODE
					+ " or clientMACProtocol=" + AhAssociation.CLIENTMACPROTOCOL_NGMODE + ")"
					+ " group by apMac";

			List<?> list24 = DBOperationUtil.executeQuery(query24);
			log.info("initSimpleHiveApCache", "2.4 GHz active client list size of HiveAP:" + list.size());
			for (Object object : list24) {
				Object[] attributes = (Object[]) object;
				Long count = AhRestoreCommons.convertLong(attributes[0].toString());
				String macAddress = (String) attributes[1];
				if (null == macAddress || "".equals(macAddress)) {
					continue;
				}
				SimpleHiveAp sp = getSimpleHiveAp(macAddress);
				if (null != sp && null != count) {
					sp.setClient24Count(count.intValue());
				}
			}
				
			String query5 = "select count(*),apMac from ah_clientsession where owner = "
				+ domainId + "AND connectstate=" + AhClientSession.CONNECT_STATE_UP
				+ " and wirelessClient=true and (clientMACProtocol=" + AhAssociation.CLIENTMACPROTOCOL_AMODE
				+ " or clientMACProtocol=" + AhAssociation.CLIENTMACPROTOCOL_NAMODE
				+ " or clientMACProtocol=" + AhAssociation.CLIENTMACPROTOCOL_ACMODE + ")"
				+ " group by apMac";

			List<?> list5 = DBOperationUtil.executeQuery(query5);
			log.info("initSimpleHiveApCache", "active client list size of HiveAP:" + list.size());
			for (Object object : list5) {
				Object[] attributes = (Object[]) object;
				Long count = AhRestoreCommons.convertLong(attributes[0].toString());
				String macAddress = (String) attributes[1];
				if (null == macAddress || "".equals(macAddress)) {
					continue;
				}
				SimpleHiveAp sp = getSimpleHiveAp(macAddress);
				if (null != sp && null != count) {
					sp.setClient5Count(count.intValue());
				}
			}
		}

		long end = System.currentTimeMillis();
		log.info("initSimpleHiveApCache", "for #domain " + domainId
				+ ", initSimleHiveApCache cost:" + (end - start) + " ms, HiveAP size:"
				+ hiveApCache.get(domainId).size());
	}

	private void removeSimpleHiveApCache(Long domainId) {
		synchronized (hiveApCache) {
			hiveApCache.remove(domainId);
		}
	}

	private void removeHmDomainCache(Long domainId) {
		synchronized (domainCache) {
			domainCache.remove(domainId);
		}
		synchronized (maxClientsCountMap) {
			if(domainId!=null && !domainId.equals(TOTAL_KEY)){
				maxClientsCountMap.remove(domainId);
			}
		}
	}

	private void initSimleHiveApCache() {
		synchronized (hiveApCache) {
			hiveApCache.clear();
			for (Long domainId : domainCache.keySet()) {
				long start = System.currentTimeMillis();
				List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams(
						"owner.id", domainId));
				build_up(hiveAps, domainId, hiveApCache);
				long end = System.currentTimeMillis();
				log.info("initSimleHiveApCache", "initSimleHiveApCache cost:" + (end - start)
						+ " ms, HiveAP size:" + hiveApCache.get(domainId).size() + ", domainId:"
						+ domainId);
			}
		}
	}

	private void initHmDomainCache() {
		try {
//			List<HmDomain> bos = QueryUtil.executeQuery(HmDomain.class,
//					new SortParams("domainName"), new FilterParams("domainName != :s1",
//							new Object[] { HmDomain.GLOBAL_DOMAIN }));
			List<HmDomain> bos = QueryUtil.executeQuery(HmDomain.class,null,null);
			long start = System.currentTimeMillis();
			synchronized (domainCache) {
				domainCache.clear();
				for (HmDomain domain : bos) {
					if(domain.getDomainName().equalsIgnoreCase(HmDomain.GLOBAL_DOMAIN)) {
						globalDomain = domain;
					} else {
						domainCache.put(domain.getId(), domain);
					}
				}
			}
			long end = System.currentTimeMillis();
			log.info("initHmDomainCache", "initHmDomainCache cost:" + (end - start)
					+ " ms, cached domain count:" + domainCache.size());
		} catch (Exception e) {
			log.error("initHmDomainCache", "initHmDomainCache error.", e);
		}
	}

	private void build_up(List<HiveAp> hiveAps, Long domainId,
			Map<Long, Map<String, SimpleHiveAp>> hiveApCache) {
		Map<String, SimpleHiveAp> hiveAp_domain = new HashMap<String, SimpleHiveAp>(hiveAps.size());
		for (HiveAp hiveAp : hiveAps) {
			SimpleHiveAp sHiveAp = getSimpleHiveAp(hiveAp);
			hiveAp_domain.put(hiveAp.getMacAddress(), sHiveAp);
		}
		hiveApCache.put(domainId, hiveAp_domain);
	}

	public SimpleHiveAp getSimpleHiveAp(HiveAp hiveAp) {
		SimpleHiveAp sHiveAp = new SimpleHiveAp();
		sHiveAp.setId(hiveAp.getId());
		sHiveAp.setDomainId(hiveAp.getOwner().getId());
		sHiveAp.setHostname(hiveAp.getHostName());
		sHiveAp.setMacAddress(hiveAp.getMacAddress());
		sHiveAp.setManageStatus(hiveAp.getManageStatus());
		sHiveAp.setSerialNumber(hiveAp.getSerialNumber());
		sHiveAp.setSoftVer(hiveAp.getSoftVer());
		sHiveAp.setCapwapClientIp(hiveAp.getCapwapClientIp());
		sHiveAp.setIpAddress(hiveAp.getIpAddress());
		
        sHiveAp.setTag1(hiveAp.getClassificationTag1());
        sHiveAp.setTag2(hiveAp.getClassificationTag2());
        sHiveAp.setTag3(hiveAp.getClassificationTag3());
        
        sHiveAp.setProxyName(hiveAp.getProxyName());
        sHiveAp.setProxyPort(hiveAp.getProxyPort());
        sHiveAp.setProxyUsername(hiveAp.getProxyUsername());
        sHiveAp.setProxyPassword(hiveAp.getProxyPassword());
        sHiveAp.setCapwapLinkIp(hiveAp.getCapwapLinkIp());

		if (null != hiveAp.getMapContainer()) {
			sHiveAp.setMapContainerId(hiveAp.getMapContainer().getId());
		}
		
		sHiveAp.setHiveApModel(hiveAp.getHiveApModel());
		sHiveAp.setDeviceType(hiveAp.getDeviceType());
		sHiveAp.setSimulated(hiveAp.isSimulated());
		if(hiveAp.getWifi0()!=null) {
			if(hiveAp.getWifi0().getRadioMode()==AhInterface.RADIO_MODE_BG ||
					hiveAp.getWifi0().getRadioMode()==AhInterface.RADIO_MODE_NG) {
				sHiveAp.setWifi0RadioType(AhInterfaceStats.RADIOTYPE_24G);
			} else {
				sHiveAp.setWifi0RadioType(AhInterfaceStats.RADIOTYPE_5G);
			}
		}
		if(hiveAp.getWifi1()!=null) {
			if(hiveAp.getWifi1().getRadioMode()==AhInterface.RADIO_MODE_BG ||
					hiveAp.getWifi1().getRadioMode()==AhInterface.RADIO_MODE_NG) {
				sHiveAp.setWifi1RadioType(AhInterfaceStats.RADIOTYPE_24G);
			} else {
				sHiveAp.setWifi1RadioType(AhInterfaceStats.RADIOTYPE_5G);
			}
		}
		sHiveAp.setConnectStatus(hiveAp.getConnectStatus());
		sHiveAp.setEnableDelayAlarm(hiveAp.isEnableDelayAlarm());
		return sHiveAp;
	}

	/**
	 * get cache domain by id
	 * 
	 * @param id
	 *            :id of domain
	 * @return HmDomain
	 */
	public HmDomain getCacheDomainById(Long id) {
		synchronized (domainCache) {
			return domainCache.get(id);
		}
	}
	

	public HMServicesSettings getHMServiceSettingForTimeZoneByDomain(HmDomain owner){
		if (owner==null || owner.getId()==null) {
			return new HMServicesSettings();
		} else {
			if (hmServiceSettingForTimeZoneMap.get(owner.getId())==null) {
				HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", owner);
				if (bo==null) {
					return new HMServicesSettings();
				} else {
					hmServiceSettingForTimeZoneMap.put(owner.getId(), bo);
				}
			} 
			return hmServiceSettingForTimeZoneMap.get(owner.getId());
		}
	}
	
	// when user update the timezone display format in GUI, reset it.
	public void resetHMServiceSettingsForTimeZone(HmDomain owner, HMServicesSettings bo) {
		if (owner==null || owner.getId()==null || bo==null) {
			return;
		}
		if (hmServiceSettingForTimeZoneMap.get(owner.getId())==null) {
			return;
		}
		hmServiceSettingForTimeZoneMap.put(owner.getId(), bo);
		
	}

	public HmDomain getCacheDomainByName(String name) {
		HmDomain hmDomain = null;
		if(name.equalsIgnoreCase(HmDomain.GLOBAL_DOMAIN)) {
			hmDomain = globalDomain;
		} else {
			synchronized (domainCache) {
				for(HmDomain domain:domainCache.values()) {
					if(domain.getDomainName().equalsIgnoreCase(name)) {
						hmDomain = domain;
						break;
					}
				}
			}
		}
		return hmDomain;
	}

	/**
	 * get all cache domains, except global domain
	 * 
	 * @return -
	 */
	public List<HmDomain> getCacheDomains() {
		if (domainCache.isEmpty()) {
			initHmDomainCache();
		}
		return new ArrayList<HmDomain>(domainCache.values());
	}

	/**
	 * return domain count, including home&global
	 * 
	 * @return -
	 */
	public int getCacheDomainCount() {
		return domainCache.size();
	}

	public ConcurrentMap<Long, AhMaxClientsCount> getMaxClientsCountMap() {
		return maxClientsCountMap;
	}

	private String getClientEditVlaueKey(AhClientEditValues value) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(value.getClientMac()).append("-").append(value.getOwner().getId())
			.append("-").append(value.getType()).append("-").append(value.getSsidName());
		return buffer.toString();
	}
	
	private String getClientEditVlaueKey(String clientMac,Long owner,short type,String ssidName) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(clientMac).append("-").append(owner)
			.append("-").append(type).append("-").append(ssidName);
		return buffer.toString();
	}

	public StudentManagerInfo getSMInfo() {
		return smInfo;
	}

	public Map<Long, Map<String, SimpleHiveAp>> getHiveApCache() {
		return hiveApCache;
	}
	
}