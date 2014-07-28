package com.ah.be.ls.stat;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.db.configuration.ConfigurationUtils;
import com.ah.be.license.LicenseInfo;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.lan.LanProfile;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.UserProfile;
import com.ah.util.HiveApUtils;
import com.ah.util.Tracer;

public class StatManager {

	public static final int DEFAULT_REPORT_INTERVAL = 24; // hours

	// private static final Log log =
	// LogFactory.getLog("commonlog.StatManager");
	private static final Tracer log = new Tracer(
			StatManager.class.getSimpleName());

	private static final String VERSION = "v3";

	private static List<StatConfig> stats;

	private static StatManager instance = new StatManager();

	private int idelHours = 0;

	public void idel(int hour) {
		idelHours = idelHours + hour;
	}

	public boolean isActived() {
		String prop = System.getProperty("data.mining.report.interval");
		int interval = StringUtils.isNumeric(prop) ? Integer.parseInt(prop)
				: DEFAULT_REPORT_INTERVAL;
		return idelHours >= interval;
	}

	public void restartIdel() {
		idelHours = 0;
	}

	private StatManager() {
		stats = readStatConfig();
	}

	public static StatManager getInstance() {
		return instance;
	}

	private static List<StatConfig> readStatConfig() {
		if (stats != null && !stats.isEmpty()) {
			return stats;
		}
		stats = new ArrayList<StatConfig>();
		// read from stat-config.xml
		SAXReader reader = new SAXReader();
		File ff = new File(System.getenv("HM_ROOT")
				+ "/resources/stat-config.xml");
		if (!ff.exists()) {
			// for test
			ff = new File("stat-config.xml");
		}
		try {
			Document doc = reader.read(ff);

			Element roota = doc.getRootElement();
			log.info("StatManager", "roota..nodcount=" + roota.nodeCount());

			Iterator<?> iters = roota.elementIterator("feature");
			while (iters.hasNext()) {
				StatConfig stat = new StatConfig();
				Element foo = (Element) iters.next();
				if (foo.attribute("ignore") != null) {
					continue;
				}
				stat.setFeatureId(Integer.valueOf(foo.attributeValue("id")));
				stat.setFeatureName(foo.attributeValue("name"));

				Element e2 = foo.element("bo-class");
				Element e4 = foo.element("search-rule");
				stat.setBoClassName(e2.attributeValue("name"));
				stat.setSearchRule(e4.attributeValue("value"));
				stat.setSearchType(e4.attributeValue("type"));

				stats.add(stat);
			}

			return stats;
		} catch (ClassNotFoundException e) {
			log.error("StatManager", "readStatConfig: ClassNotFoundException",
					e);
		} catch (Exception e) {
			log.error("StatManager", "readStatConfig: Exception", e);
		}

		return null;
	}

	/*- unused codes, commented by mfjin
	public void stat(String uploadPath) throws DocumentException, IOException, Exception {
		log.info("stats.size=" + stats.size());
		if (stats == null || stats.isEmpty()) {
			throw new Exception("no stat config");
		}

		SAXReader reader = new SAXReader();
		InputStream in = new ByteArrayInputStream("<stat></stat>".getBytes());
		Document doc = reader.read(in);
		Element roote = doc.getRootElement();
		log.info("roote.getName()=" + roote.getName());

		Set<Long> aps = new HashSet<Long>();

		// one by one count
		for (StatConfig stat : stats) {
			log.info("stat=" + stat.getFeatureId() + ", " + stat.getFeatureName() + ", "
					+ stat.getBoClassName() + ", search rule=" + stat.getSearchRule());

			try {
				List<? extends HmBo> bos;
				if (stat.getSearchType() != null && stat.getSearchType().equals("native")) {
					List<?> ids2 = QueryUtil.executeNativeQuery(stat.getSearchRule());
					log.info("ids2.size=" + ids2.size());
					List<Long> ids = new ArrayList<Long>(ids2.size());
					for (Object obj : ids2) {
						BigInteger id2 = (BigInteger) obj;
						ids.add(id2.longValue());
					}

					if (!ids.isEmpty()) {
						bos = QueryUtil.executeQuery(AlgConfiguration.class, null,
								new FilterParams("id in(:s1)", new Object[] { ids }));
					} else {
						bos = new ArrayList<HmBo>(0);
					}
				} else if (stat.getSearchRule() == null || stat.getSearchRule().trim().equals("")) {
					bos = QueryUtil.executeQuery(stat.getBoClass(), null, null);
				} else {
					bos = QueryUtil.executeQuery(stat.getBoClass(), null, new FilterParams(stat
							.getSearchRule(), new Object[] {}));
				}
				log.info("bos.size=" + bos.size());
				for (HmBo bo : bos) {
					Set<Long> aps2 = ConfigurationUtils.getRelevantHiveAp(bo);
					aps.addAll(aps2);
				}
			} catch (Exception e) {
				log.error("stat: stat[" + stat.getFeatureId() + "] failed", e);
			}
			log.info("aps.size=" + aps.size());
			addToDoc(roote, stat.getFeatureId(), stat.getFeatureName(), aps.size());
			aps.clear();
		}
		File ff = new File(uploadPath + "/stat-result.xml");
		ff.createNewFile();
		BufferedWriter output = new BufferedWriter(new FileWriter(ff));
		output.write(doc.asXML());
		output.close();
	}*/

	private Map<Long, String> apVhmIdMappings;
	private Map<String, Integer> vhmApCountMappings;
	private List<Long> targetedApIds;
	private Map<Long, String> vhms;

	public Document stat() throws DocumentException, IOException, Exception {
		log.info("StatManager", "stats.size=" + stats.size());
		if (stats == null || stats.isEmpty()) {
			throw new Exception("no stat config");
		}
		boolean isHMOL = NmsUtil.isProduction();
		boolean isHM = !NmsUtil.isHostedHMApplication();
		if (!isHMOL && !isHM) {
			log.warn("StatManager",
					"the server is not production HMOL or Stand alone HM, do not do data mining.");
			return null;
		}

		Document document = generateDocumentTemplate();

		Map<String, List<Element>> entity = new HashMap<String, List<Element>>();

		getTargetedHiveApVhmMapping();

		if (isHMOL) {// HMOL
			vhms = getHmolVhmList();
			if (vhms.isEmpty()) {
				log.warn("StatManager",
						"the server do not have any HMOL customer, do not do data mining.");
				return null;
			}
		}

		for (StatConfig stat : stats) {
			Map<String, Element> featureElements = buildOfDocumentFragment(
					stat, isHMOL);
			if (null == featureElements) {
				continue;
			}
			// store all document fragment
			for (String key : featureElements.keySet()) {
				List<Element> elements = entity.get(key);
				if (null == elements) {
					elements = new ArrayList<Element>();
					entity.put(key, elements);
				}
				elements.add(featureElements.get(key));
			}
		}

		// generate document
		if (isHMOL) {
			// create document root element
			Element root = document.addElement("stats");
			root.addAttribute("version", VERSION);
			for (String vhmId : entity.keySet()) {
				Element vhmRoot = createVhmStatElement(vhmId);
				List<Element> elements = entity.get(vhmId);
				for (Element element : elements) {
					vhmRoot.add(element);
				}
				root.add(vhmRoot);
			}
		} else {
			// fetch system id
			String systemId = "";
			LicenseInfo licenseInfo = HmBeLicenseUtil.getLicenseInfo();
			if (licenseInfo != null && null != licenseInfo.getSystemId()) {
				systemId = licenseInfo.getSystemId();
			}
			// create document root element
			Element root = document.addElement("stat");
			root.addAttribute("version", VERSION);
			root.addAttribute("system-id", systemId);
			List<Element> elements = entity.get("HM");
			if (null != elements) {
				for (Element element : elements) {
					root.add(element);
				}
			}
		}
		return document;
	}

	private Document generateDocumentTemplate() {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		return document;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Element> buildOfDocumentFragment(StatConfig stat,
			boolean isHMOL) {
		log.info("StatManager",
				"stat=" + stat.getFeatureId() + ", " + stat.getFeatureName()
						+ ", " + stat.getBoClassName() + ", search rule="
						+ stat.getSearchRule());
		Map<String, Element> map = null;
		try {
			long start = System.currentTimeMillis();
			Set<Long> apIds = new HashSet<Long>();
			List<? extends HmBo> bos = null;
			switch (stat.getFeatureId()) {
			case 67:
			case 68:
			case 69:
				List<?> ids2 = QueryUtil.executeNativeQuery(stat
						.getSearchRule());
				log.debug("StatManager", "ids2.size=" + ids2.size());
				List<Long> ids = new ArrayList<Long>(ids2.size());
				for (Object obj : ids2) {
					ids.add(((BigInteger) obj).longValue());
				}
				if (!ids.isEmpty()) {
					bos = QueryUtil.executeQuery(AlgConfiguration.class, null,
							new FilterParams("id in (:s1)",
									new Object[] { ids }));
					for (HmBo bo : bos) {
						Set<Long> aps = ConfigurationUtils
								.getRelevantHiveAp(bo);
						apIds.addAll(aps);
					}
				}
				map = buildNormalDocumentFragment(stat, isHMOL, apIds);
				break;
			case 81:
			case 82:
			case 83:
			case 84:
			case 85:
			case 86:
			case 87:
			case 88:
			case 89:
			case 90:
			case 91:
			case 92:
				// query HiveAp itself
				List<Long> list = (List<Long>) QueryUtil.executeQuery(
						"select id from "
								+ stat.getBoClass().getCanonicalName(), null,
						buildFilterParams(stat));
				apIds.addAll(list);
				map = buildNormalDocumentFragment(stat, isHMOL, apIds);
				break;
			case 93:
				String softVer = "5.1.1.0";
				// query IDS policy
				bos = QueryUtil.executeQuery(stat.getBoClass(), null,
						buildFilterParams(stat));
				Set<Long> tmpIds = new HashSet<Long>();
				for (HmBo bo : bos) {
					Set<Long> aps = ConfigurationUtils.getRelevantHiveAp(bo);
					tmpIds.addAll(aps);
				}
				// filter out ap version lower or equals base softVer;
				List<SimpleHiveAp> simpleAps = CacheMgmt.getInstance()
						.getManagedApList();
				Set<Long> softVerAps = new HashSet<Long>();
				for (SimpleHiveAp simpleAp : simpleAps) {
					if (NmsUtil.compareSoftwareVersion(softVer,
							simpleAp.getSoftVer()) >= 0) {
						softVerAps.add(simpleAp.getId());
					}
				}
				for (Long tmpId : tmpIds) {
					if (softVerAps.contains(tmpId)) {
						apIds.add(tmpId);
					}
				}
				map = buildNormalDocumentFragment(stat, isHMOL, apIds);
				break;
			case 94:
				map = buildAPAndRouterPopulation(stat, isHMOL);
				break;
			case 95:
				bos = QueryUtil.executeQuery(stat.getBoClass(), null,
						buildFilterParams(stat));
				for (HmBo bo : bos) {
					Set<Long> aps = ConfigurationUtils.getRelevantHiveAp(bo);
					apIds.addAll(aps);
				}
				apIds.addAll(getEthUsing8021XApIds());
				map = buildDeviceNumberAndPercentagePerModel(stat, isHMOL,
						apIds);
				break;
			case 96:
				bos = QueryUtil.executeQuery(stat.getBoClass(), null,
						buildFilterParams(stat));
				for (HmBo bo : bos) {
					Set<Long> aps = ConfigurationUtils.getRelevantHiveAp(bo);
					apIds.addAll(aps);
				}
				map = buildDeviceNumberAndPercentagePerModel(stat, isHMOL,
						apIds);
				break;
			case 97: // RADIUS Server
				Set<Long> radiusServerApIds = getRadiusServerApIds();
				if (null != radiusServerApIds) {
					apIds.addAll(radiusServerApIds);
				}
				map = buildDeviceNumberAndPercentagePerModel(stat, isHMOL,
						apIds);
				break;
			case 102: // RADIUS Caching
				Set<Long> radiusCachingApIds = getRadiusCachingApIds();
				if (null != radiusCachingApIds) {
					apIds.addAll(radiusCachingApIds);
				}
				map = buildDeviceNumberAndPercentagePerModel(stat, isHMOL,
						apIds);
				break;
			case 98: // RADIUS proxy
				Set<Long> radiusProxyApIds = getRadiusProxyApIds();
				if (null != radiusProxyApIds) {
					apIds.addAll(radiusProxyApIds);
				}
				map = buildDeviceNumberAndPercentagePerModel(stat, isHMOL,
						apIds);
				break;
			case 99:
				map = buildDeviceMeshAndBridge(stat, isHMOL);
				break;
			case 100:
				map = buildCvgAndActiveTunnel(stat, isHMOL);
				break;
			case 101:
				map = buildActiveClientAndHiveApCount(stat, isHMOL);
				break;
			case 103:
				map = buildPoePowerSetting(stat, isHMOL);
				break;
			case 104:
				map = buildUserManagerStatistics(stat, isHMOL);
				break;
			default:
				// query other bo
				bos = QueryUtil.executeQuery(stat.getBoClass(), null,
						buildFilterParams(stat));
				for (HmBo bo : bos) {
					Set<Long> aps = ConfigurationUtils.getRelevantHiveAp(bo);
					apIds.addAll(aps);
				}
				map = buildNormalDocumentFragment(stat, isHMOL, apIds);
				break;
			}
			long end = System.currentTimeMillis();
			log.info("StatManager",
					"query stat feature id: [" + stat.getFeatureId()
							+ "] cost: " + (end - start) + " ms.");
		} catch (Exception e) {
			log.error("StatManager",
					"stat: stat feature id: [" + stat.getFeatureId()
							+ "] failed", e);
		}
		return map;
	}

	private Set<Long> getEthUsing8021XApIds() {
		Set<Long> ids = new HashSet<Long>();
		// query ap330, ap350 as route mode
		String query = "select id from " + LanProfile.class.getCanonicalName();
		String where = "eth1_on = :s1 And enabled8021X = :s2";
		Object[] values = new Object[2];
		values[0] = true;
		values[1] = true;
		List<?> list = QueryUtil.executeQuery(query, null, new FilterParams(
				where, values));
		Set<Long> apIds = new HashSet<Long>();
		if (!list.isEmpty()) {
			for (Object id : list) {
				LanProfile lanProfile = new LanProfile();
				lanProfile.setId((Long) id);
				Set<Long> tmpIds = ConfigurationUtils
						.getRelevantHiveAp(lanProfile);
				apIds.addAll(tmpIds);
			}
		}
		if (!apIds.isEmpty()) {
			query = "select id from " + HiveAp.class.getCanonicalName();
			FilterParams filter = HiveApUtils.getLan8021XFeatureHiveApFilter(
					true, true, apIds);
			list = QueryUtil.executeQuery(query, null, filter);
			if (!list.isEmpty()) {
				for (Object id : list) {
					ids.add((Long) id);
				}
			}
		}

		// query routes
		query = "select id from " + LanProfile.class.getCanonicalName();
		where = "(eth1_on = :s1 or eth2_on = :s1 or eth3_on = :s1 or eth4_on = :s1) and enabled8021X =:s2";
		values = new Object[2];
		values[0] = true;
		values[1] = true;
		list = QueryUtil.executeQuery(query, null, new FilterParams(where,
				values));
		apIds = new HashSet<Long>();
		if (!list.isEmpty()) {
			for (Object id : list) {
				LanProfile lanProfile = new LanProfile();
				lanProfile.setId((Long) id);
				Set<Long> tmpIds = ConfigurationUtils
						.getRelevantHiveAp(lanProfile);
				apIds.addAll(tmpIds);
			}
		}
		if (!apIds.isEmpty()) {
			query = "select id from " + HiveAp.class.getCanonicalName();
			FilterParams filter = HiveApUtils.getLan8021XFeatureRouterFilter(
					true, true, apIds);
			list = QueryUtil.executeQuery(query, null, filter);
			if (!list.isEmpty()) {
				for (Object id : list) {
					ids.add((Long) id);
				}
			}
		}
		return ids;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Element> buildUserManagerStatistics(StatConfig stat,
			boolean isHMOL) {
		Map<String, Element> elems = new HashMap<String, Element>();
		List<String> userGroupNameList = new ArrayList<String>();
		userGroupNameList.add(HmUserGroup.GM_ADMIN);
		userGroupNameList.add(HmUserGroup.GM_OPERATOR);
		
		if (isHMOL) {
			Map<Long, Integer> devicesCountMap = new HashMap<Long, Integer>();
			Map<Long, Integer> temporaryGuestUserCountMap = new HashMap<Long, Integer>();
			Map<Long, Integer> permanentGuestUserCountMap = new HashMap<Long, Integer>();
			
			List<Long> ownerIdList = new ArrayList<Long>();
			
			List<?> hmUserOwnerIds = QueryUtil.executeQuery("SELECT DISTINCT bo.owner.id FROM " + HmUser.class.getSimpleName() + " AS bo", null, new FilterParams("bo.owner.domainName != :s1 AND bo.owner.domainName != :s2 AND bo.owner.supportGM = true AND bo.userGroup.groupName in (:s3)", new Object[]{HmDomain.HOME_DOMAIN, HmDomain.GLOBAL_DOMAIN, userGroupNameList}));
			List<?> userProfileOwnerIds = (List<Object>) QueryUtil.executeQuery("SELECT DISTINCT bo.owner.id FROM " + UserProfile.class.getSimpleName() + " AS bo", null, new FilterParams("bo.blnUserManager = true AND bo.owner.supportGM = true", new Object[]{}));
			for(Object obj:hmUserOwnerIds) {
				if(userProfileOwnerIds.contains(obj)) {
					ownerIdList.add(Long.parseLong(String.valueOf(obj)));
				}
			}
			
			List<Object> devicesCountList = (List<Object>) QueryUtil.executeQuery("SELECT bo.owner.id, COUNT(bo.id) FROM " + HiveAp.class.getSimpleName() + " AS bo", null, new FilterParams("manageStatus = :s1 AND simulated = false GROUP BY bo.owner.id", new Object[]{HiveAp.STATUS_MANAGED}));
			for(Object devicesCountObj:devicesCountList) {
				Object[] devicesCountObject = (Object[]) devicesCountObj;
				long ownerId = (long) devicesCountObject[0];
				int devicesCountByOwner = Integer.parseInt(String.valueOf(devicesCountObject[1]));
				devicesCountMap.put(ownerId, devicesCountByOwner);
			}
			
			if(!ownerIdList.isEmpty()) {
				List<Object> temporaryGuestUserCountList = (List<Object>) QueryUtil.executeQuery("SELECT bo.owner.id, COUNT(bo.id) FROM " + LocalUser.class.getSimpleName() + " AS bo", null, new FilterParams("userType = :s1 AND bo.owner.id in (:s2) AND status != :s3 AND status != :s4 AND ((visitorName is not null AND visitorName != '') OR (visitorCompany is not null AND visitorCompany != '')) GROUP BY bo.owner.id", new Object[]{LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK, ownerIdList, LocalUser.STATUS_REVOKED, LocalUser.STATUS_PARTIAL_REVOKED}));
				for(Object temporaryGuestUserCountObj:temporaryGuestUserCountList) {
					Object[] temporaryGuestUserCountObject = (Object[]) temporaryGuestUserCountObj;
					long ownerId = (long) temporaryGuestUserCountObject[0];
					int temporaryGuestUserCountByOwner = Integer.parseInt(String.valueOf(temporaryGuestUserCountObject[1]));
					temporaryGuestUserCountMap.put(ownerId, temporaryGuestUserCountByOwner);
				}
				
				List<Object> permanentGuestUserCountList = (List<Object>) QueryUtil.executeQuery("SELECT bo.owner.id, COUNT(bo.id) FROM " + LocalUser.class.getSimpleName() + " AS bo", null, new FilterParams("userType = :s1 AND bo.owner.id in (:s2) AND activated = :s3 GROUP BY bo.owner.id", new Object[]{LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK, ownerIdList, true}));
				for(Object permanentGuestUserCountObj:permanentGuestUserCountList) {
					Object[] permanentGuestUserCountObject = (Object[]) permanentGuestUserCountObj;
					long ownerId = (long) permanentGuestUserCountObject[0];
					int temporaryGuestUserCountByOwner = Integer.parseInt(String.valueOf(permanentGuestUserCountObject[1]));
					permanentGuestUserCountMap.put(ownerId, temporaryGuestUserCountByOwner);
				}
			}
			
			// create document fragment
			for (Long id : vhms.keySet()) {
				String vhmId = vhms.get(id);
				
				int devicesCount = devicesCountMap.get(id) == null? 0:devicesCountMap.get(id);
				int temporaryGuestUserCount = temporaryGuestUserCountMap.get(id) == null? 0:temporaryGuestUserCountMap.get(id);
				int permanentGuestUserCount = permanentGuestUserCountMap.get(id) == null? 0:permanentGuestUserCountMap.get(id);
				boolean enableUserManager = ownerIdList.contains(id);

				Element elem = createFeatureElement(stat.getFeatureId(),
						stat.getFeatureName());
				addUserManagerStatisticsAttribute(elem, devicesCount, temporaryGuestUserCount, permanentGuestUserCount, enableUserManager);
				elems.put(vhmId, elem);
			}	
		} else {
			List<?> hmUserOwnerIds = QueryUtil.executeQuery("SELECT DISTINCT bo.owner.id FROM " + HmUser.class.getSimpleName() + " AS bo", null, new FilterParams("bo.userGroup.groupName in (:s1) AND bo.owner.supportGM = true", new Object[]{userGroupNameList}));
			boolean enableUserManager = false;
			
			List<Object> userProfileOwnerIds = (List<Object>) QueryUtil.executeQuery("SELECT DISTINCT bo.owner.id FROM " + UserProfile.class.getSimpleName() + " AS bo", null, new FilterParams("bo.blnUserManager = true AND bo.owner.supportGM = true", new Object[]{}));
			
			for(Object obj:hmUserOwnerIds) {
				if(userProfileOwnerIds.contains(obj)) {
					enableUserManager = true;
					break;
				}
			}
			
			int devicesCount = 0, temporaryGuestUserCount = 0, permanentGuestUserCount = 0;
			devicesCount = (int) QueryUtil.findRowCount(HiveAp.class, new FilterParams("manageStatus = :s1 AND simulated = false", new Object[]{HiveAp.STATUS_MANAGED}));
			if(enableUserManager) {
				temporaryGuestUserCount = (int) QueryUtil.findRowCount(LocalUser.class, new FilterParams("userType = :s1 AND status != :s2 AND status != :s3 AND ((visitorName is not null AND visitorName != '') OR (visitorCompany is not null AND visitorCompany != ''))", new Object[]{LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK, LocalUser.STATUS_REVOKED, LocalUser.STATUS_PARTIAL_REVOKED}));
				permanentGuestUserCount = (int) QueryUtil.findRowCount(LocalUser.class, new FilterParams("userType = :s1 AND activated = :s2 ", new Object[]{LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK, true}));
			}
			// create document fragment
			Element elem = createFeatureElement(stat.getFeatureId(),
					stat.getFeatureName());
			addUserManagerStatisticsAttribute(elem, devicesCount, temporaryGuestUserCount, permanentGuestUserCount, enableUserManager);
			elems.put("HM", elem);
		}
		return elems;
	}
	
	private Map<String, Element> buildPoePowerSetting(StatConfig stat,
			boolean isHMOL) {
		Map<String, Element> elems = new HashMap<String, Element>();
		if (isHMOL) {
			Map<Long, Integer> totalCount = new HashMap<Long, Integer>();
			Map<Long, Integer> totalAfCount = new HashMap<Long, Integer>();
			Map<Long, Integer> totalAtCount = new HashMap<Long, Integer>();
			String query = "select h.id, h.owner, l.reporter, max(poepower) from hive_ap h left join hm_lldp_information l on h.macaddress=l.reporter where managestatus = "
					+ HiveAp.STATUS_MANAGED
					+ " and simulated = false group by l.reporter, h.id, h.owner";
			List<?> list = QueryUtil.executeNativeQuery(query);
			if (!list.isEmpty()) {
				for (Object object : list) {
					Object[] objects = (Object[]) object;
					Long owner = ((BigInteger) objects[1]).longValue();
					Integer power = (Integer) objects[3];
					Integer total = totalCount.get(owner);
					totalCount.put(owner, total == null ? 1 : total + 1);
					if (null != power) {
						int intValue = power.intValue();
						if (intValue > 154) {
							Integer at = totalAtCount.get(owner);
							totalAtCount.put(owner, at == null ? 1 : at + 1);
						} else if (intValue > 0) {
							Integer af = totalAfCount.get(owner);
							totalAfCount.put(owner, af == null ? 1 : af + 1);
						}
					}
				}
			}
			// create document fragment
			for (Long id : vhms.keySet()) {
				String vhmId = vhms.get(id);
				Integer vhmTotalCount = totalCount.get(id);
				Integer vhmAfCount = totalAfCount.get(id);
				Integer vhmAtCount = totalAtCount.get(id);

				Element elem = createFeatureElement(stat.getFeatureId(),
						stat.getFeatureName());
				addPowerSettingAttribute(elem, vhmAfCount == null ? 0
						: vhmAfCount, vhmAtCount == null ? 0 : vhmAtCount,
						vhmTotalCount == null ? 0 : vhmTotalCount);
				elems.put(vhmId, elem);
			}
		} else {
			int totalCount = 0, afCount = 0, atCount = 0;
			String query = "select h.id, l.reporter, max(poepower) from hive_ap h left join hm_lldp_information l on h.macaddress=l.reporter where managestatus = "
					+ HiveAp.STATUS_MANAGED
					+ " and simulated = false group by l.reporter, h.id";
			List<?> list = QueryUtil.executeNativeQuery(query);
			if (!list.isEmpty()) {
				totalCount = list.size();
				for (Object object : list) {
					Object[] objects = (Object[]) object;
					Integer power = (Integer) objects[2];
					if (null != power) {
						int intValue = power.intValue();
						if (intValue > 154) {
							atCount++;
						} else if (intValue > 0) {
							afCount++;
						}
					}
				}
			}
			// create document fragment
			Element elem = createFeatureElement(stat.getFeatureId(),
					stat.getFeatureName());
			addPowerSettingAttribute(elem, afCount, atCount, totalCount);
			elems.put("HM", elem);
		}
		return elems;
	}

	private Map<String, Element> buildActiveClientAndHiveApCount(
			StatConfig stat, boolean isHMOL) {
		Map<String, Element> elems = new HashMap<String, Element>();
		if (isHMOL) {
			Map<Long, Integer> totalCounts = new HashMap<Long, Integer>();
			String query = "select count(1), owner from ah_clientsession group by owner";
			List<?> list = QueryUtil.executeNativeQuery(query);
			for (Object object : list) {
				Object[] objects = (Object[]) object;
				int count = ((BigInteger) objects[0]).intValue();
				long owner = ((BigInteger) objects[1]).longValue();
				totalCounts.put(owner, count);
			}
			// create document fragment
			for (Long id : vhms.keySet()) {
				String vhmId = vhms.get(id);
				Integer vhmClientCount = totalCounts.get(id);
				Integer vhmApCount = vhmApCountMappings.get(vhmId);
				Element elem = createFeatureElement(stat.getFeatureId(),
						stat.getFeatureName());
				vhmClientCount = null == vhmClientCount ? 0 : vhmClientCount;
				vhmApCount = null == vhmApCount ? 0 : vhmApCount;
				addClientApAttribute(elem, vhmClientCount, vhmApCount);
				elems.put(vhmId, elem);
			}
		} else {
			int clientCount = 0;
			String query = "select count(1) from ah_clientsession";
			List<?> list = QueryUtil.executeNativeQuery(query);
			if (!list.isEmpty()) {
				clientCount = ((BigInteger) list.get(0)).intValue();
			}
			int apCount = targetedApIds.size();
			// create document fragment
			Element elem = createFeatureElement(stat.getFeatureId(),
					stat.getFeatureName());
			addClientApAttribute(elem, clientCount, apCount);
			elems.put("HM", elem);
		}

		return elems;
	}

	private Map<String, Element> buildCvgAndActiveTunnel(StatConfig stat,
			boolean isHMOL) {
		Map<String, Element> elems = new HashMap<String, Element>();
		if (isHMOL) {
			Map<Long, List<String>> totalCvgs = new HashMap<Long, List<String>>();
			Map<Long, Map<String, Integer>> totalCvgTunnelCounts = new HashMap<Long, Map<String, Integer>>();

			String query = "select macAddress, owner.id from "
					+ HiveAp.class.getCanonicalName();
			FilterParams filter = HiveApUtils.getCvgModeFilter(true, true);
			List<?> list = QueryUtil.executeQuery(query, null, filter);
			for (Object object : list) {
				Object[] objects = (Object[]) object;
				String macAddress = (String) objects[0];
				Long vhmId = (Long) objects[1];
				List<String> vhmCvgs = totalCvgs.get(vhmId);
				if (null == vhmCvgs) {
					vhmCvgs = new ArrayList<String>();
					totalCvgs.put(vhmId, vhmCvgs);
				}
				vhmCvgs.add(macAddress);
			}

			query = "select vpn.serverid, count(1), vpn.owner from hm_vpnstatus as vpn left join hive_ap as ap on vpn.serverid = ap.macaddress where vpn.clientid is not null and ap.hiveApModel in ("
					+ HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA + "," + HiveAp.HIVEAP_MODEL_VPN_GATEWAY + ")"
					+ " and ap.id is not null group by vpn.serverid, vpn.owner";
			list = QueryUtil.executeNativeQuery(query);
			for (Object object : list) {
				Object[] objects = (Object[]) object;
				String mac = objects[0].toString();
				int count = ((BigInteger) objects[1]).intValue();
				long owner = ((BigInteger) objects[2]).longValue();
				Map<String, Integer> vhmResult = totalCvgTunnelCounts
						.get(owner);
				if (null == vhmResult) {
					vhmResult = new HashMap<String, Integer>();
					totalCvgTunnelCounts.put(owner, vhmResult);
				}
				vhmResult.put(mac, count);
			}
			// create document fragment
			for (Long id : vhms.keySet()) {
				String vhmId = vhms.get(id);
				// Integer vhmCvgCount = totalCounts.get(id);
				List<String> vhmCvgs = totalCvgs.get(id);
				Map<String, Integer> vhmCvgTunnelCounts = totalCvgTunnelCounts
						.get(id);
				Element elem = createFeatureElement(stat.getFeatureId(),
						stat.getFeatureName());
				Element ele = createCvgsElement(vhmCvgs == null ? 0 : vhmCvgs
						.size());
				if (null != vhmCvgs) {
					for (String mac : vhmCvgs) {
						boolean isL2 = isL2CvgTunnel(mac);
						Integer cvgTunnelCount = null;
						if (null != vhmCvgTunnelCounts) {
							cvgTunnelCount = vhmCvgTunnelCounts.get(mac);
						}
						ele.add(createCvgsTunnelElement(mac,
								null == cvgTunnelCount ? 0 : cvgTunnelCount,
								isL2));
					}
				}
				elem.add(ele);
				elems.put(vhmId, elem);
			}
		} else {
			List<String> totalCvgs = new ArrayList<String>();
			Map<String, Integer> cvgTunnelCounts = new HashMap<String, Integer>();

			String query = "select macAddress from "
					+ HiveAp.class.getCanonicalName();
			FilterParams filter = HiveApUtils.getCvgModeFilter(true, true);
			List<?> list = QueryUtil.executeQuery(query, null, filter);
			for (Object object : list) {
				totalCvgs.add((String) object);
			}

			query = "select vpn.serverid, count(1) from hm_vpnstatus as vpn left join hive_ap as ap on vpn.serverid = ap.macaddress where vpn.clientid is not null and ap.hiveApModel in ("
					+ HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA + "," + HiveAp.HIVEAP_MODEL_VPN_GATEWAY + ")"
					+ " and ap.id is not null group by vpn.serverid";
			list = QueryUtil.executeNativeQuery(query);
			for (Object object : list) {
				Object[] objects = (Object[]) object;
				String mac = objects[0].toString();
				int count = ((BigInteger) objects[1]).intValue();
				cvgTunnelCounts.put(mac, count);
			}
			// create document fragment
			Element elem = createFeatureElement(stat.getFeatureId(),
					stat.getFeatureName());
			Element ele = createCvgsElement(totalCvgs.size());
			for (String mac : totalCvgs) {
				boolean isL2 = isL2CvgTunnel(mac);
				Integer tunnelCount = cvgTunnelCounts.get(mac);
				ele.add(createCvgsTunnelElement(mac, tunnelCount == null ? 0
						: tunnelCount, isL2));
			}
			elem.add(ele);
			elems.put("HM", elem);
		}

		return elems;
	}

	private boolean isL2CvgTunnel(String macAddress) {
		boolean isL2 = false;
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(macAddress);
		if (null != ap) {
			isL2 = ap.getDeviceType() == HiveAp.Device_TYPE_HIVEAP;
		}
		return isL2;
	}

	private Map<String, Element> buildDeviceMeshAndBridge(StatConfig stat,
			boolean isHMOL) {
		Map<String, Element> elems = new HashMap<String, Element>();
		if (isHMOL) {
			// mesh nodes
			Map<Long, Integer> meshNodes = new HashMap<Long, Integer>();
			FilterParams filter = HiveApUtils.getMeshingApFilter(true, true);
			List<?> list = QueryUtil.executeQuery("select id, owner.id from "
					+ HiveAp.class.getCanonicalName(), null, filter);
			for (Object object : list) {
				Object[] objects = (Object[]) object;
				// Long id = (Long) objects[0];
				Long owner = (Long) objects[1];
				Integer count = meshNodes.get(owner);
				if (null == count) {
					meshNodes.put(owner, 0);
				}
				meshNodes.put(owner, meshNodes.get(owner) + 1);
			}

			// mesh enabled nodes
			Map<Long, Integer> meshEnabledNodes = new HashMap<Long, Integer>();
			filter = HiveApUtils.getMeshingEnabledApFilter(true, true);
			list = QueryUtil.executeQuery("select id, owner.id from "
					+ HiveAp.class.getCanonicalName(), null, filter);
			for (Object object : list) {
				Object[] objects = (Object[]) object;
				// Long id = (Long) objects[0];
				Long owner = (Long) objects[1];
				Integer count = meshEnabledNodes.get(owner);
				if (null == count) {
					meshEnabledNodes.put(owner, 0);
				}
				meshEnabledNodes.put(owner, meshEnabledNodes.get(owner) + 1);
			}
			// bridge nodes
			Map<Long, Integer> bridgeNodes = new HashMap<Long, Integer>();
			filter = HiveApUtils.getBridgeApFilter(true, true);
			list = QueryUtil.executeQuery("select id, owner.id from "
					+ HiveAp.class.getCanonicalName(), null, filter);
			for (Object object : list) {
				Object[] objects = (Object[]) object;
				// Long id = (Long) objects[0];
				Long owner = (Long) objects[1];
				Integer count = bridgeNodes.get(owner);
				if (null == count) {
					bridgeNodes.put(owner, 0);
				}
				bridgeNodes.put(owner, bridgeNodes.get(owner) + 1);
			}

			// create document fragment
			for (Long id : vhms.keySet()) {
				String vhmId = vhms.get(id);
				Integer totalCount = vhmApCountMappings.get(vhmId);
				Integer meshEnabledCount = meshEnabledNodes.get(id);
				Integer meshCount = meshNodes.get(id);
				Integer bridgeCount = bridgeNodes.get(id);

				Element elem = createFeatureElement(stat.getFeatureId(),
						stat.getFeatureName());
				addMeshBridgeAttribute(elem, (meshEnabledCount == null ? 0
						: meshEnabledCount.intValue()), (meshCount == null ? 0
						: meshCount.intValue()), (bridgeCount == null ? 0
						: bridgeCount.intValue()), (totalCount == null ? 0
						: totalCount.intValue()));
				elems.put(vhmId, elem);
			}
		} else {
			int meshCount = 0;
			int meshEnabledCount = 0;
			int bridgeCount = 0;
			// total count;
			int totalCount = targetedApIds.size();

			// mesh node
			FilterParams filter = HiveApUtils.getMeshingApFilter(true, true);
			List<?> list = QueryUtil.executeQuery("select id from "
					+ HiveAp.class.getCanonicalName(), null, filter);
			meshCount = list.size();
			// mesh enabled node
			filter = HiveApUtils.getMeshingEnabledApFilter(true, true);
			list = QueryUtil.executeQuery(
					"select id from " + HiveAp.class.getCanonicalName(), null,
					filter);
			meshEnabledCount = list.size();
			// bridge node
			filter = HiveApUtils.getBridgeApFilter(true, true);
			list = QueryUtil.executeQuery(
					"select id from " + HiveAp.class.getCanonicalName(), null,
					filter);
			bridgeCount = list.size();

			// create document fragment
			Element elem = createFeatureElement(stat.getFeatureId(),
					stat.getFeatureName());
			addMeshBridgeAttribute(elem, meshEnabledCount, meshCount,
					bridgeCount, totalCount);
			elems.put("HM", elem);
		}
		return elems;
	}

	private Set<Long> getRadiusProxyApIds() {
		FilterParams filter = HiveApUtils.getRadiusProxyApFilter(true, true);
		List<?> list = QueryUtil.executeQuery(
				"select id from " + HiveAp.class.getCanonicalName(), null,
				filter);
		Set<Long> ids = new HashSet<Long>();
		for (Object object : list) {
			ids.add((Long) object);
		}
		return ids;
	}

	private Set<Long> getRadiusServerApIds() {
		FilterParams filter = HiveApUtils.getRadiusServerApFilter(true, true);
		List<?> list = QueryUtil.executeQuery(
				"select id from " + HiveAp.class.getCanonicalName(), null,
				filter);
		Set<Long> ids = new HashSet<Long>();
		for (Object object : list) {
			ids.add((Long) object);
		}
		return ids;
	}

	private Set<Long> getRadiusCachingApIds() {
		Set<Long> ids = new HashSet<Long>();
		String query = "select id from "
				+ RadiusOnHiveap.class.getCanonicalName();
		List<?> list = QueryUtil.executeQuery(query, null, new FilterParams(
				"cacheEnable", true));
		if (list.isEmpty()) {
			return ids;
		}
		Set<Long> cachingIds = new HashSet<Long>();
		for (Object object : list) {
			cachingIds.add((Long) object);
		}
		FilterParams filter = HiveApUtils.getRadiusCachingApFilter(true, true,
				cachingIds);
		list = QueryUtil.executeQuery(
				"select id from " + HiveAp.class.getCanonicalName(), null,
				filter);
		for (Object object : list) {
			ids.add((Long) object);
		}
		return ids;
	}

	private Map<String, Element> buildDeviceNumberAndPercentagePerModel(
			StatConfig stat, boolean isHMOL, Set<Long> apIds) {
		Map<String, Element> elems = new HashMap<String, Element>();
		if (isHMOL) {
			Map<Long, Map<Integer, Integer>> total = new HashMap<Long, Map<Integer, Integer>>();
			Map<Long, Map<Integer, Integer>> selected = new HashMap<Long, Map<Integer, Integer>>();
			String query = "select hiveApModel, count(id), owner from hive_ap where managestatus="
					+ HiveAp.STATUS_MANAGED
					+ " and simulated = false group by hiveApModel, owner";
			List<?> totalList = QueryUtil.executeNativeQuery(query);
			for (Object object : totalList) {
				Object[] objects = (Object[]) object;
				int apModel = ((Short) objects[0]).intValue();
				int count = ((BigInteger) objects[1]).intValue();
				long owner = ((BigInteger) objects[2]).longValue();
				Map<Integer, Integer> vhmTotal = total.get(owner);
				if (null == vhmTotal) {
					vhmTotal = new HashMap<Integer, Integer>();
					total.put(owner, vhmTotal);
				}
				vhmTotal.put(apModel, count);
			}
			if (!apIds.isEmpty()) {
				query = "select hiveApModel, count(id), owner from hive_ap where managestatus="
						+ HiveAp.STATUS_MANAGED
						+ " and simulated = false and id in ("
						+ getSqlParameters(apIds)
						+ ") group by hiveApModel, owner";
				List<?> selectedList = QueryUtil.executeNativeQuery(query);
				for (Object object : selectedList) {
					Object[] objects = (Object[]) object;
					int apModel = ((Short) objects[0]).intValue();
					int count = ((BigInteger) objects[1]).intValue();
					long owner = ((BigInteger) objects[2]).longValue();
					Map<Integer, Integer> vhmSelected = selected.get(owner);
					if (null == vhmSelected) {
						vhmSelected = new HashMap<Integer, Integer>();
						selected.put(owner, vhmSelected);
					}
					vhmSelected.put(apModel, count);
				}
			}
			// create document fragment
			for (Long id : vhms.keySet()) {
				String vhmId = vhms.get(id);
				Map<Integer, Integer> vhmSelected = selected.get(id);
				Map<Integer, Integer> vhmTotal = total.get(id);
				Element elem = createFeatureElement(stat.getFeatureId(),
						stat.getFeatureName());
				if (null != vhmTotal) {
					for (Integer apModel : vhmTotal.keySet()) {
						Integer totalCount = vhmTotal.get(apModel);
						Integer selectedCount = null;
						if (null != vhmSelected) {
							selectedCount = vhmSelected.get(apModel);
						}
						elem.add(createPlatformElement(String.valueOf(apModel),
								(totalCount == null ? 0 : totalCount),
								(selectedCount == null ? 0 : selectedCount)));
					}
				}
				elems.put(vhmId, elem);
			}
		} else {
			Map<Integer, Integer> total = new HashMap<Integer, Integer>();
			Map<Integer, Integer> selected = new HashMap<Integer, Integer>();
			String query = "select hiveApModel, count(id) from hive_ap where managestatus="
					+ HiveAp.STATUS_MANAGED
					+ " and simulated = false group by hiveApModel";
			List<?> totalList = QueryUtil.executeNativeQuery(query);
			for (Object object : totalList) {
				Object[] objects = (Object[]) object;
				int apModel = ((Short) objects[0]).intValue();
				int count = ((BigInteger) objects[1]).intValue();
				total.put(apModel, count);
			}
			if (!apIds.isEmpty()) {
				query = "select hiveApModel, count(id) from hive_ap where managestatus="
						+ HiveAp.STATUS_MANAGED
						+ " and simulated = false and id in ("
						+ getSqlParameters(apIds) + ") group by hiveApModel";
				List<?> selectedList = QueryUtil.executeNativeQuery(query);
				for (Object object : selectedList) {
					Object[] objects = (Object[]) object;
					int apModel = ((Short) objects[0]).intValue();
					int count = ((BigInteger) objects[1]).intValue();
					selected.put(apModel, count);
				}
			}
			// create document fragment
			Element elem = createFeatureElement(stat.getFeatureId(),
					stat.getFeatureName());
			for (Integer apModel : total.keySet()) {
				Integer totalCount = total.get(apModel);
				Integer selectedCount = selected.get(apModel);
				elem.add(createPlatformElement(String.valueOf(apModel),
						(totalCount == null ? 0 : totalCount),
						(selectedCount == null ? 0 : selectedCount)));
			}
			elems.put("HM", elem);
		}
		return elems;
	}

	private String getSqlParameters(Set<Long> ids) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (Long id : ids) {
			if (!isFirst) {
				sb.append(",");
			}
			sb.append(id);

			if (isFirst) {
				isFirst = false;
			}
		}
		return sb.toString();
	}

	private Map<String, Element> buildAPAndRouterPopulation(StatConfig stat,
			boolean isHMOL) {
		Map<String, Element> elems = new HashMap<String, Element>();
		if (isHMOL) {
			String sql = "select hiveApModel, deviceType, count(id), owner from hive_ap where managestatus="
					+ HiveAp.STATUS_MANAGED
					+ " and simulated = false group by hiveApModel,deviceType, owner";
			List<?> apRoutes = QueryUtil.executeNativeQuery(sql);
			Map<Long, Map<String, Map<Integer, Integer>>> result = new HashMap<Long, Map<String, Map<Integer, Integer>>>();
			for (Object object : apRoutes) {
				Object[] apRoute = (Object[]) object;
				int apModel = ((Short) apRoute[0]).intValue();
				int deviceType = ((Short) apRoute[1]).intValue();
				int apCount = ((BigInteger) apRoute[2]).intValue();
				long owner = ((BigInteger) apRoute[3]).longValue();
				Map<String, Map<Integer, Integer>> vhmResult = result
						.get(owner);
				if (null == vhmResult) {
					vhmResult = new HashMap<String, Map<Integer, Integer>>();
					result.put(owner, vhmResult);
				}
				Map<Integer, Integer> apModelMap = vhmResult.get(String
						.valueOf(apModel));
				if (null == apModelMap) {
					apModelMap = new HashMap<Integer, Integer>();
					vhmResult.put(String.valueOf(apModel), apModelMap);
				}
				apModelMap.put(deviceType, apCount);
			}
			// create document fragment
			for (Long id : vhms.keySet()) {
				String vhmId = vhms.get(id);
				Map<String, Map<Integer, Integer>> vhmResult = result.get(id);
				Element elem = createFeatureElement(stat.getFeatureId(),
						stat.getFeatureName());
				if (null != vhmResult) {
					buildApAndRouteElement(vhmResult, elem);
				}
				elems.put(vhmId, elem);
			}
		} else {
			String sql = "select hiveApModel, deviceType, count(id) from hive_ap where managestatus="
					+ HiveAp.STATUS_MANAGED
					+ " and simulated = false group by hiveApModel,deviceType";
			List<?> apRoutes = QueryUtil.executeNativeQuery(sql);
			Map<String, Map<Integer, Integer>> result = new HashMap<String, Map<Integer, Integer>>();
			for (Object object : apRoutes) {
				Object[] apRoute = (Object[]) object;
				int apModel = ((Short) apRoute[0]).intValue();
				int deviceType = ((Short) apRoute[1]).intValue();
				int apCount = ((BigInteger) apRoute[2]).intValue();
				Map<Integer, Integer> apModelMap = result.get(String
						.valueOf(apModel));
				if (null == apModelMap) {
					apModelMap = new HashMap<Integer, Integer>();
					result.put(String.valueOf(apModel), apModelMap);
				}
				apModelMap.put(deviceType, apCount);
			}
			// create document fragment
			Element elem = createFeatureElement(stat.getFeatureId(),
					stat.getFeatureName());
			buildApAndRouteElement(result, elem);
			elems.put("HM", elem);
		}
		return elems;
	}

	private Map<String, Element> buildNormalDocumentFragment(StatConfig stat,
			boolean isHMOL, Set<Long> apIds) {
		Map<String, Element> elems = new HashMap<String, Element>();
		if (isHMOL) {
			// for HMOL, need to get count per vHM.
			Map<String, Integer> counts = new HashMap<String, Integer>();
			if (!apIds.isEmpty()) {
				for (Long apId : apIds) {
					String vhmId = apVhmIdMappings.get(apId);
					Integer count = counts.get(vhmId);
					if (null == count) {
						count = new Integer(0);
						counts.put(vhmId, count);
					}
					counts.put(vhmId, counts.get(vhmId) + 1);
				}
			}
			for (Long id : vhms.keySet()) {
				String vhmId = vhms.get(id);
				Integer vhmApcount = counts.get(vhmId);
				Integer vhmTotalCount = vhmApCountMappings.get(vhmId);
				Element elem = createFeatureElement(stat.getFeatureId(),
						stat.getFeatureName());
				addNumberTotalAttribute(elem, (null == vhmApcount ? 0
						: vhmApcount.intValue()), (null == vhmTotalCount ? 0
						: vhmTotalCount.intValue()));
				elems.put(vhmId, elem);
			}
		} else {
			// make sure it's in stat ap list
			int apCount = 0;
			for (Long apId : apIds) {
				if (targetedApIds.contains(apId)) {
					apCount++;
				}
			}
			int totalCount = targetedApIds.size();
			// if (apCount > 0) {
			Element elem = createFeatureElement(stat.getFeatureId(),
					stat.getFeatureName());
			addNumberTotalAttribute(elem, apCount, totalCount);
			elems.put("HM", elem);
			// }
		}
		return elems;
	}

	private void buildApAndRouteElement(
			Map<String, Map<Integer, Integer>> result, Element elem) {
		for (String apModel : result.keySet()) {
			Map<Integer, Integer> types = result.get(apModel);
			Element ele = createPlatformElement(apModel);
			if (String.valueOf(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA).equals(apModel)
					||String.valueOf(HiveAp.HIVEAP_MODEL_VPN_GATEWAY).equals(apModel)) {
				// CVG always counted as CVG mode
				Integer count = 0;
				for (Integer type : types.keySet()) {
					count = count + types.get(type);
				}
				ele.add(createCvgElement(count));
			} else {
				for (Integer type : types.keySet()) {
					Integer count = types.get(type);
					switch (type) {
					case (int) HiveAp.Device_TYPE_HIVEAP:
						ele.add(createL2Element(count));
						break;
					case (int) HiveAp.Device_TYPE_BRANCH_ROUTER:
					case (int) HiveAp.Device_TYPE_VPN_BR:
						ele.add(createL3Element(count));
						break;
					case (int) HiveAp.Device_TYPE_VPN_GATEWAY:
						ele.add(createCvgElement(count));
						break;
					case (int) HiveAp.Device_TYPE_SWITCH:
						ele.add(createSwitchElement(count));
						break;
					}
				}
			}
			elem.add(ele);
		}
	}

	private FilterParams buildFilterParams(StatConfig stat) {
		if (null != stat.getSearchRule()
				&& !"".equals(stat.getSearchRule().trim())) {
			return new FilterParams(stat.getSearchRule(), new Object[] {});
		} else {
			return null;
		}
	}

	/**
	 * The targeted HiveAp should be managed HiveAP, and not simulated HiveAP as
	 * well.
	 */
	private void getTargetedHiveApVhmMapping() {
		apVhmIdMappings = new HashMap<Long, String>();
		vhmApCountMappings = new HashMap<String, Integer>();
		targetedApIds = new ArrayList<Long>();
		String where = "manageStatus = :s1 and simulated = :s2";
		Object[] values = new Object[] { HiveAp.STATUS_MANAGED, false };
		String query = "select id, owner.vhmID from "
				+ HiveAp.class.getCanonicalName();
		List<?> list = QueryUtil.executeQuery(query, null, new FilterParams(
				where, values));
		for (Object obj : list) {
			Object[] objects = (Object[]) obj;
			Long apId = (Long) objects[0];
			String vhmId = (String) objects[1];

			// collect for HM unit;
			targetedApIds.add(apId);

			if (null == vhmId || "".equals(vhmId.trim())) {
				continue;
			}
			// collect for HMOL unit;
			apVhmIdMappings.put(apId, vhmId);

			Integer totalCount = vhmApCountMappings.get(vhmId);
			if (null == totalCount) {
				vhmApCountMappings.put(vhmId, 0);
			}
			vhmApCountMappings.put(vhmId, vhmApCountMappings.get(vhmId) + 1);
		}
	}

	/**
	 * Get customer vHM list, filter out 'home domain'.
	 *
	 * @return
	 */
	private Map<Long, String> getHmolVhmList() {
		Map<Long, String> map = new HashMap<Long, String>();
		List<HmDomain> domains = CacheMgmt.getInstance().getCacheDomains();
		if (null != domains && !domains.isEmpty()) {
			for (HmDomain domain : domains) {
				if (null == domain.getVhmID()
						|| "".equals(domain.getVhmID().trim())) {
					continue;
				}
				if (domain.getRunStatus() != HmDomain.DOMAIN_DEFAULT_STATUS) {
					continue;
				}
				map.put(domain.getId(), domain.getVhmID());
			}
		}
		log.info("StatManager", "data mining for HMOL: " + map);
		return map;
	}

	private Element createFeatureElement(int featureId, String featureName) {
		Element ele = DocumentHelper.createElement("feature");
		ele.addAttribute("id", String.valueOf(featureId));
		ele.addAttribute("name", featureName);
		return ele;
	}

	private Element createVhmStatElement(String vhmId) {
		Element elem = DocumentHelper.createElement("stat");
		return elem.addAttribute("vhm-id", vhmId);
	}

	private Element createPlatformElement(String apModel) {
		Element ele = DocumentHelper.createElement("platform");
		ele.addAttribute("type", String.valueOf(apModel));
		return ele;
	}

	private Element createL2Element(int number) {
		Element ele = DocumentHelper.createElement("L2");
		return ele.addAttribute("count", String.valueOf(number));
	}

	private Element createL3Element(int number) {
		Element ele = DocumentHelper.createElement("L3");
		return ele.addAttribute("count", String.valueOf(number));
	}

	private Element createSwitchElement(int number) {
		Element ele = DocumentHelper.createElement("SW");
		return ele.addAttribute("count", String.valueOf(number));
	}

	private Element createCvgElement(int number) {
		Element ele = DocumentHelper.createElement("CVG");
		return ele.addAttribute("count", String.valueOf(number));
	}

	private Element createCvgsElement(int number) {
		Element ele = DocumentHelper.createElement("CVGS");
		return ele.addAttribute("count", String.valueOf(number));
	}

	private Element createCvgsTunnelElement(String mac, int number, boolean isL2) {
		Element ele = DocumentHelper.createElement("CVG");
		ele.addAttribute("mac", mac);
		ele.addAttribute("tunnel-type", isL2 ? "L2" : "L3");
		return ele.addAttribute("tunnel-count", String.valueOf(number));
	}

	private Element createPlatformElement(String apModel, int total, int number) {
		Element ele = DocumentHelper.createElement("platform");
		ele.addAttribute("type", apModel);
		ele.addAttribute("count", String.valueOf(number));
		ele.addAttribute("total-count", String.valueOf(total));
		return ele;
	}
	
	private Element addUserManagerStatisticsAttribute(Element ele, int devicesCount,
			int temporaryGuestUserCount, int permanentGuestUserCount, boolean enableUserManager) {
		ele.addAttribute("devices-count", String.valueOf(devicesCount));
		ele.addAttribute("temporary-count", String.valueOf(temporaryGuestUserCount));
		ele.addAttribute("permanent-count", String.valueOf(permanentGuestUserCount));
		ele.addAttribute("enable", String.valueOf(enableUserManager));
		return ele;
	}

	private Element addPowerSettingAttribute(Element ele, int afCount,
			int atCount, int totalCount) {
		ele.addAttribute("af-count", String.valueOf(afCount));
		ele.addAttribute("at-count", String.valueOf(atCount));
		ele.addAttribute("total-count", String.valueOf(totalCount));
		return ele;
	}

	private Element addMeshBridgeAttribute(Element ele, int meshEnabled,
			int mesh, int bridge, int total) {
		ele.addAttribute("mesh-enabled-count", String.valueOf(meshEnabled));
		ele.addAttribute("mesh-count", String.valueOf(mesh));
		ele.addAttribute("bridge-count", String.valueOf(bridge));
		ele.addAttribute("total-count", String.valueOf(total));
		return ele;
	}

	private Element addClientApAttribute(Element ele, int clientCount,
			int apCount) {
		ele.addAttribute("client-count", String.valueOf(clientCount));
		ele.addAttribute("ap-count", String.valueOf(apCount));
		return ele;
	}

	private Element addNumberTotalAttribute(Element elem, int number, int total) {
		Element ele = elem.addAttribute("count", String.valueOf(number));
		ele.addAttribute("total-count", String.valueOf(total));
		return ele;
	}
}