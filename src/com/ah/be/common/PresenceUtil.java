package com.ah.be.common;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.fault.BeFaultConst;
import com.ah.be.license.LicenseInfo;
import com.ah.be.ls.ClientTrustManager;
import com.ah.be.rest.client.CustomersAndStoresService;
import com.ah.be.rest.client.CustomersAndStoresService.PresenceCustomerPojo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.report.PresenceAnalyticsCustomer;
import com.ah.util.HmException;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class PresenceUtil {

	private static final Tracer log = new Tracer(
			PresenceUtil.class.getSimpleName());

	public static final int PRESENCE_MAX_DEVICE_COUNT = Integer
			.parseInt(NmsUtil.getConfigProperty(
					"presence.support.device.maximum").trim());
	public static final int PRESENCE_POST_CONCURRENT_COUNT = Integer
			.parseInt(NmsUtil
					.getConfigProperty("presence.data.post.concurrent").trim());
	public static final String PRESENCE_DATA_POST_URL = NmsUtil
			.getConfigProperty("presence.data.post.url");
	private static int SOCKET_TIMEOUT = Integer.parseInt(NmsUtil
			.getConfigProperty("presence.data.post.socketTimeout").trim());
	private static Boolean presenceGlobalStatus;
	private static Map<Long, String> domainInfo = null;
	private static Map<String, String> customerInfo = new ConcurrentHashMap<String, String>();
//	private static Map<String, String> versionInfo = new ConcurrentHashMap<String, String>();
	private static Map<String, String> userInfo = new ConcurrentHashMap<String, String>();

	private static void saveCustoemrInformation(String customerId,
			HmDomain domain) {
		try {
			long current = System.currentTimeMillis();
			PresenceAnalyticsCustomer customer = new PresenceAnalyticsCustomer();
			customer.setCustomerId(customerId);
			customer.setOwner(domain);
			customer.setCreateAt(DateFormatUtils.format(current,
					DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern()));
			QueryUtil.createBo(customer);
			putPresenceCustomerId(customerId, domain.getId());
		} catch (Exception e) {
			log.error("saveCustoemrInformation error, domain: "
					+ domain.getDomainName());
		}
	}
	
	public static boolean syncCustomerInformation(String customerId) throws HmException{
		String c = customerInfo.get(customerId);
		String u = userInfo.get(customerId);
		requestCustomerInformation(customerId);
		String nc = customerInfo.get(customerId);
		String nu = userInfo.get(customerId);
		boolean changedCredential = false, changedUserToken = false;
		if(null == c){
			changedCredential = nc != null;
		}else{
			changedCredential = !c.equals(nc);
		}
		if(null == u){
			changedUserToken = nu != null;
		}else{
			changedUserToken = !u.equals(nu);
		}
		boolean r = changedCredential || changedUserToken;
		if(r){
			log.warn("customer #: + " + customerId + "origin credential: " + c + ", user token: " + u + ", new credential: " + nc + ", user token: " + nu);
		}
		return r;
	}

	private static void requestCustomerInformation(String customerId)
			throws HmException {
		log.info("isCustomerRegistered, customerId: " + customerId);
		if (StringUtils.isEmpty(customerId)) {
			return;
		}
		// get customer
		JSONObject customer = CustomersAndStoresService.getCustomer(customerId);
		if (null == customer) {
			throw new HmException(
					"cannot get customer via partner API. customerId: "
							+ customerId, "error.presence.service.available");
		}
		// get user
		String email = customerId + "@aerohive.com";
		String userAuthToken = CustomersAndStoresService.getUser(email);
		if (null == userAuthToken) {
			throw new HmException("cannot get user via partner API. email: "
					+ email, "error.presence.service.available");
		}

		try {
			String credential = "";
			JSONArray clients = customer.getJSONArray("clients");
			if (null != clients && clients.length() > 0) {
				credential = clients.getJSONObject(0).getString("credential");
				// comment version from 6.1r6 as we do not used it on UI
				// version = clients.getJSONObject(0).getString("tier");
			}
			customerInfo.put(customerId, credential);
//			versionInfo.put(customerId, version);
			userInfo.put(customerId, userAuthToken);
		} catch (JSONException e) {
			log.error("request customer inforamtion error.", e);
			throw new HmException("Parse client information json data failed", "error.presence.customer.data.parse");
		}
	}

	private static String fillCustomerSystemOrVhmId(HmDomain domain) {
		boolean isHMOL = NmsUtil.isHostedHMApplication();
		if (isHMOL) {
			return domain.getVhmID();
		} else {
			String systemId = "";
			LicenseInfo licenseInfo = HmBeLicenseUtil.getLicenseInfo();
			if (licenseInfo != null && null != licenseInfo.getSystemId()) {
				systemId = licenseInfo.getSystemId();
			}
			return systemId + "||" + domain.getDomainName();
		}
	}

	public static boolean registerCustomer(String customerId, HmDomain domain)
			throws HmException {
		if (StringUtils.isEmpty(customerId)) {
			return false;
		}
		String desc = fillCustomerSystemOrVhmId(domain);
		log.info("registerCustomer, description is: " + desc);
		// register customer
		String credential = CustomersAndStoresService
				.registerCustomer(new PresenceCustomerPojo(customerId, desc,
						true));
		if (StringUtils.isEmpty(credential)) {
			return false;
		}
		// register user
		String email = customerId + "@aerohive.com";
		boolean updated = CustomersAndStoresService.updateUser(customerId,
				email, true);
		if (!updated) {
			log.error("registerCustomer, update user failed.");
			return false;
		}
		String userAuthToken = CustomersAndStoresService.getUser(email);
		if (StringUtils.isEmpty(userAuthToken)) {
			return false;
		}
		saveCustoemrInformation(customerId, domain);
		requestCustomerInformation(customerId);
		return true;
	}

	public static String getCustomerUserAuthToken(String customerId)
			throws JSONException, HmException {
		if (null == customerId) {
			return null;
		}
		if (!customerInfo.containsKey(customerId)
				|| !userInfo.containsKey(customerId)) {
			requestCustomerInformation(customerId);
		}
		return userInfo.get(customerId);
	}

	public static String getCustomerCredential(String customerId)
			throws JSONException, HmException {
		if (!customerInfo.containsKey(customerId)
				|| !userInfo.containsKey(customerId)) {
			requestCustomerInformation(customerId);
		}
		return customerInfo.get(customerId);
	}

//	public static boolean isLiteVersion(String customerId) {
//		String version = versionInfo.get(customerId);
//		return (null == version || "lite".equals(version));
//	}

	private static void putPresenceCustomerId(String customerId, Long domainId) {
		if (null != domainInfo) {
			synchronized (customerInfo) {
				domainInfo.put(domainId, customerId);
				log.info("current presence customer info: " + domainInfo);
			}
		}
	}

	public static void initPresenceCustomerCache() {
		synchronized (customerInfo) {
			Map<Long, String> customers = new ConcurrentHashMap<Long, String>();
			String query = "select customerId, owner.id from "
					+ PresenceAnalyticsCustomer.class.getCanonicalName();
			List<?> list = QueryUtil.executeQuery(query, null, null);
			for (Object object : list) {
				Object[] objects = (Object[]) object;
				customers.put((Long) objects[1], (String) objects[0]);
			}
			domainInfo = customers;
			log.info("load presence customer info from db: " + domainInfo);
			Set<String> invalidCustomers = new HashSet<String>();
			for(String customerId : customerInfo.keySet()){
				if(!customers.values().contains(customerId)){
					invalidCustomers.add(customerId);
				}
			}
			for(String customerId : invalidCustomers){
				log.info("customer #: " + customerId + " is not existed in current HiveManager, remove its presence customer info from cache.");
				customerInfo.remove(customerId);
				userInfo.remove(customerId);
			}
		}
	}

	/**
	 * Query customer id store on HiveManager.
	 * 
	 * @param domainId
	 * @return
	 */
	public static String getPresenceCustomerId(Long domainId) {
		log.info("getPresenceCustomerId, domainId: " + domainId);
		if (null == domainId) {
			return null;
		}
		if (null == domainInfo) {
			initPresenceCustomerCache();
		}
		return domainInfo.get(domainId);
	}

	private static void initPresenceStateCache() {
		synchronized (customerInfo) {
			String query = "select presenceEnable from "
					+ HMServicesSettings.class.getCanonicalName();
			List<?> list = QueryUtil.executeQuery(query, null, null, BoMgmt
					.getDomainMgmt().getHomeDomain().getId());
			presenceGlobalStatus = (null == list ? false : (Boolean) list
					.get(0));
		}
	}

	public static void setPresenceStatus(boolean isEnabled) {
		presenceGlobalStatus = isEnabled;
	}

	public static boolean isPresenceSettingEnabled() {
		if (null == presenceGlobalStatus) {
			initPresenceStateCache();
		}
		return presenceGlobalStatus;
	}

	public static String generateCustomerId() {
		return "AH_" + System.currentTimeMillis();
	}

	public static boolean isCustomerIdInUse(String customerId)
			throws HmException {
		JSONObject customer = CustomersAndStoresService.getCustomer(customerId);
		if (null == customer) {
			throw new HmException(
					"cannot get customer via partner API. customerId: "
							+ customerId, "error.presence.service.available");
		}
		try {
			JSONArray clients = customer.getJSONArray("clients");
			return (null != clients && clients.length() > 0);
		} catch (JSONException e) {
			log.error("isCustomerIdInUse, request customer inforamtion error.",
					e);
			return false;
		}
	}

	public static boolean isCustomerRegistered(Long domainId)
			throws HmException {
		String customerId = getPresenceCustomerId(domainId);
		log.info("isCustomerRegistered, customerId: " + customerId);
		if (StringUtils.isEmpty(customerId)) {
			return false;
		}
		if (!customerInfo.containsKey(customerId)
				|| !userInfo.containsKey(customerId)) {
			requestCustomerInformation(customerId);
		}
		String credential = customerInfo.get(customerId);
		String userAuthToken = userInfo.get(customerId);
		if (StringUtils.isEmpty(credential)
				|| StringUtils.isEmpty(userAuthToken)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * determine if presence function enabled for specified domain, it based on
	 * two settings: <li>The global settings on HiveManager Settings page</li>
	 * <li>The customer has agree presence agreement on Retail Analytics page</li>
	 * <br>
	 * 
	 * @param domainId
	 * @return
	 */
	public static boolean isPresenceEnabled(Long domainId) {
		try {
			// global switch
			boolean presenceGlobalStatus = isPresenceSettingEnabled();
			if (!presenceGlobalStatus) {
				return false;
			}
			// customer switch
			boolean isRegistered = isCustomerRegistered(domainId);
			if (!isRegistered) {
				return false;
			}
		} catch (HmException e) {
			log.error("isPresenceEnabled", e.getMessage());
			return false;
		}
		return true;
	}

	public static HttpClient getHttpClientInstance(int maxConnections) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { new ClientTrustManager() },
					null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
					.getSocketFactory()));
			schemeRegistry.register(new Scheme("https", 443, ssf));
			PoolingClientConnectionManager connMgr = new PoolingClientConnectionManager(
					schemeRegistry);
			connMgr.setMaxTotal(maxConnections);
			connMgr.setDefaultMaxPerRoute(maxConnections);

			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, SOCKET_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
			HttpClient httpClient = new DefaultHttpClient(connMgr, params);
			return httpClient;
		} catch (Exception e) {
			log.error("getHttpClientInstance error.", e);
			return null;
		}
	}

	// convert bandwidth value
	public static String convertValue(double bandWidth) {
		String vonvertBandWidth = "";
		bandWidth = new BigDecimal(bandWidth / 1024).setScale(2,
				BigDecimal.ROUND_HALF_UP).doubleValue();
		vonvertBandWidth = bandWidth + " KB";
		if (bandWidth > 1024) {
			bandWidth = new BigDecimal(bandWidth / 1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			vonvertBandWidth = bandWidth + " MB";
		} else if (bandWidth > 1024) {
			bandWidth = new BigDecimal(bandWidth / 1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			vonvertBandWidth = bandWidth + " GB";
		}
		return vonvertBandWidth;
	}

	private static AtomicBoolean connectEuclidStatus = new AtomicBoolean(true);
	private static AtomicInteger postFailCount = new AtomicInteger();
	private static AtomicBoolean createdAlarm=new AtomicBoolean();
	private static AtomicBoolean clearedAlarm = new AtomicBoolean(true);
	public static void updateEuclidAlarm() {
		if(!getConnectEuclidStatus()){
			postFailCount.incrementAndGet();
		}else{
			postFailCount.set(0);
		}
		if (!getConnectEuclidStatus() && !createdAlarm.get()
				        && postFailCount.intValue()>=5) {
			    createdAlarm.set(true);
			    clearedAlarm.set(false);
				AhAppContainer
				.getBeFaultModule()
				.saveAlarm(
						"-",
						MgrUtil.getUserMessage("config.presence.euclidserver.isDown"),
						BeFaultConst.ALERT_SERVERITY_CRITICAL,
						BeFaultConst.ALARM_TYPY_HM,
						BeFaultConst.ALARM_HM_EUCLID_SERVER,
						0,
						0,
						BeFaultConst.TRAP_SEND_MAIL_TYPEX[19],
						BoMgmt.getDomainMgmt().getHomeDomain()
								.getDomainName(), "-");
		} else if (getConnectEuclidStatus() && !clearedAlarm.get()) {
				createdAlarm.set(false);
				clearedAlarm.set(true);
				AhAppContainer
					.getBeFaultModule()
					.saveAlarm(
							"-",
						MgrUtil.getUserMessage("config.presence.euclidserver.isUp"),
						BeFaultConst.ALERT_SERVERITY_CLEAR,
						BeFaultConst.ALARM_TYPY_HM,
						BeFaultConst.ALARM_HM_EUCLID_SERVER,
						0,
						0,
						BeFaultConst.TRAP_SEND_MAIL_TYPEX[19],
						BoMgmt.getDomainMgmt().getHomeDomain()
								.getDomainName(), "-");
		}
	}

	public static SimpleHiveAp getSimpleHiveAp(Long domainId, String macAddress) {
		if (null == domainId) {
			return null;
		}
		SimpleHiveAp simpleHiveAp;
		try {
			Map<Long, Map<String, SimpleHiveAp>> apMap = CacheMgmt
					.getInstance().getHiveApCache();
			if (null == apMap) {
				return null;
			}
			Map<String, SimpleHiveAp> hiveAps = apMap.get(domainId);
			simpleHiveAp = hiveAps.get(macAddress);
			if (null != simpleHiveAp
					&& simpleHiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
				return null;
			}
		} catch (Exception e) {
			log.debug("getSimpleHiveAp error:", e);
			return null;
		}
		return simpleHiveAp;
	}

	public static boolean getConnectEuclidStatus() {
		return connectEuclidStatus.get();
		
	}

	public static void setConnectEuclidStatus(boolean connectStatus) {
		connectEuclidStatus.set(connectStatus);
	}
	
}
