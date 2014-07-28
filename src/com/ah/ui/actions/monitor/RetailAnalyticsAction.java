package com.ah.ui.actions.monitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.ah.be.app.AhAppContainer;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.PresenceUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.performance.BePresenceProcessor;
import com.ah.be.performance.BePresenceProcessor.SensorTrackingClient;
import com.ah.be.rest.client.CustomersAndStoresService;
import com.ah.be.rest.client.CustomersAndStoresService.PresenceSensorPojo;
import com.ah.be.rest.client.CustomersAndStoresService.PresenceStorePojo;
import com.ah.be.rest.client.RestAhPreSenceService;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.performance.AhPresenceSensorData;
import com.ah.bo.performance.AhStoreSensorData;
import com.ah.bo.report.PresenceAnalyticsCustomer;
import com.ah.ui.actions.BaseAction;
import com.ah.util.HiveApUtils;
import com.ah.util.HmException;
import com.ah.util.JodaTimeZone;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;

public class RetailAnalyticsAction extends BaseAction {

	private static final Tracer log = new Tracer(
			RetailAnalyticsAction.class.getSimpleName());
	private static String presenceLiteDashboardUrl = NmsUtil.getConfigProperty(
			"presence.lite.dashboard.url").trim();
	private static String presencePermiumDashboardUrl = NmsUtil
			.getConfigProperty("presence.permium.dashboard.url").trim();
	private static String presencePermiumSignUrl = NmsUtil.getConfigProperty(
			"presence.permium.sign.url").trim();

	private static final long serialVersionUID = 1L;

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_REPORT_RETAILANALYTICS);
	}

	@Override
	public String execute() throws Exception {
		if ("configure".equals(operation)) {
			boolean isRegistered = PresenceUtil.isCustomerRegistered(domainId);
			if (!isRegistered) {
				addActionError(MgrUtil
						.getUserMessage("error.presence.customer.unregister"));
				prepareDependObjects();
				return SUCCESS;
			} else {
				prepareDependObjects();
				prepareObjects();
				return INPUT;
			}
		} else if ("register".equals(operation)) {
			boolean registered = registerCustomer();
			if (!registered) {
				prepareDependObjects();
				return SUCCESS;
			} else {
				firstTime = true;
				prepareDependObjects();
				return INPUT;
			}
		} else if ("loadStoreData".equals(operation)) {
			jsonObject = getStores();
			return "json";
		} else if ("createStore".equals(operation)) {
			log.info("operation: " + operation + ", storeName: " + storeName);
			jsonObject = createOrUpdateStore(storeName, true);
			return "json";
		} else if ("updateStore".equals(operation)) {
			log.info("operation: " + operation + ", storeName: " + storeName);
			jsonObject = createOrUpdateStore(storeName, false);
			return "json";
		} else if ("removeStore".equals(operation)) {
			log.info("operation: " + operation + ", selectedStores: "
					+ selectedStores);
			jsonObject = disableStore(selectedStores);
			return "json";
		} else if ("startTracking".equals(operation)) {
			log.info("operation: " + operation + ", track sensor: "
					+ sensorName);
			jsonObject = trackingSensor(sensorName);
			return "json";
		} else if ("stopTracking".equals(operation)) {
			log.info("operation: " + operation);
			stopTrackingSensor();
			return null;
		} else if ("trackPolling".equals(operation)) {
			log.info("operation: " + operation + ", track sensor: "
					+ sensorName);
			jsonObject = pollTrackingSensor(sensorName);
			return "json";
		} else if ("retailSensorData".equals(operation)) {
			storeSensorPojoMap.put(domainId, getStoreSensorPojoMap());
			initializeSensorData();
			return "retailSensorData";
		} else if ("getJsonSensorData".equals(operation)) {
			jsonObject = getJsonStoreSensorData();
			return "json";
		} else if ("syncPresenceCustomerInfo".equals(operation)) {
			jsonObject = syncPresenceCustomerInfo();
			return "json";
		} else {
			prepareDependObjects();
			return SUCCESS;
		}
	}

	private void prepareObjects() {
		if (null != getDomain()) {
			storeTimezone = getDomain().getTimeZoneString();
		}
	}

	private void prepareDependObjects() {
		try {
			boolean reg = PresenceUtil.isCustomerRegistered(domainId);
			customerRegistered = reg ? CUSTOMER_REGISTER_YES
					: CUSTOMER_REGISTER_NO;
		} catch (HmException e) {
			customerRegistered = CUSTOMER_REGISTER_UNKNOWN;
		}
	}
	
	private JSONObject syncPresenceCustomerInfo() throws JSONException{
		JSONObject jsonObject = new JSONObject();
		String customerId = PresenceUtil.getPresenceCustomerId(domainId);
		if (null == customerId || "".equals(customerId)) {
			jsonObject.put("msg", "Presence Customer ID is invalid.");
			return jsonObject;
		}
		try {
			boolean isUpdated = PresenceUtil.syncCustomerInformation(customerId);
			jsonObject.put("updated", isUpdated);
		} catch (HmException e) {
			jsonObject.put("err", e.getMessage());
		}
		return jsonObject;
	}

	private JSONObject pollTrackingSensor(String macAddress)
			throws JSONException {
		JSONObject jsonObject = new JSONObject();
		if (StringUtils.isEmpty(macAddress)) {
			log.error("pollTrackingSensor, sensor mac is empty: " + macAddress);
			return jsonObject;
		}
		String realMac = macAddress.replaceAll(":", "");
		HiveAp device = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress",
				realMac);
		if (null != device) {
			jsonObject.put("c", device.getConnectionTimeString());
			jsonObject.put("s", device.getConnectedStringNoColor());
			jsonObject.put("conn", device.isConnected());
			jsonObject.put("p", true);
		}
		long current = System.currentTimeMillis();
		SensorTrackingClient[] sensorData = AhAppContainer.HmBe
				.getPerformModule().getBePresenceProcessor()
				.getSensorData(request.getSession().getId(), realMac);
		if (null != sensorData) {
			JSONArray jsonArray = new JSONArray();
			for (SensorTrackingClient client : sensorData) {
				if (null == client) {
					continue;
				}
				String mac = client.getMacAddress();
				long firstSeen = client.getFirstSeen();
				String time = NmsUtil
						.transformTime((int) (current - firstSeen) / 1000);
				JSONObject json = new JSONObject();
				json.put("m", mac);
				json.put("t", time);
				jsonArray.put(json);
			}
			jsonObject.put("list", jsonArray);
		} else {
			log.error("pollTrackingSensor, sensor is not currently in tracking: "
					+ realMac);
		}
		return jsonObject;
	}

	private JSONObject trackingSensor(String macAddress) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		if (StringUtils.isEmpty(macAddress)) {
			log.error("trackingSensor, sensor mac is empty: " + macAddress);
			return jsonObject;
		}
		String realMac = macAddress.replaceAll(":", "");
		HiveAp device = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress",
				realMac);
		if (null != device) {
			jsonObject.put("c", device.getConnectionTimeString());
			jsonObject.put("s", device.getConnectedStringNoColor());
			jsonObject.put("conn", device.isConnected());
			jsonObject.put("p", true);
			AhAppContainer.HmBe
					.getPerformModule()
					.getBePresenceProcessor()
					.addSensorDataListener(request.getSession().getId(),
							realMac);
		} else {
			log.error("trackingSensor, but cannot find related device: "
					+ realMac);
		}
		return jsonObject;
	}

	private void stopTrackingSensor() {
		AhAppContainer.HmBe.getPerformModule().getBePresenceProcessor()
				.removeSensorDataListener(request.getSession().getId());
	}

	private boolean registerCustomer() throws JSONException {
		try {
			String presenceCustomerId = PresenceUtil
					.getPresenceCustomerId(domainId);
			if (StringUtils.isEmpty(presenceCustomerId)) {
				// need to register
				// get unregister id;
				while (true) {
					presenceCustomerId = PresenceUtil.generateCustomerId();
					boolean isInUse = PresenceUtil
							.isCustomerIdInUse(presenceCustomerId);
					if (!isInUse) {
						break;
					}
				}
			} else {
				boolean isRegistered = PresenceUtil
						.isCustomerIdInUse(presenceCustomerId);
				if (isRegistered) {
					addActionMessage(MgrUtil
							.getUserMessage("info.presence.customer.register.already"));
					return true;
				}
			}
			// register
			boolean success = PresenceUtil.registerCustomer(presenceCustomerId,
					getDomain());
			if (!success) {
				addActionError(MgrUtil
						.getUserMessage("error.presence.customer.register"));
				return false;
			} else {
				addActionMessage(MgrUtil
						.getUserMessage("info.presence.customer.register"));
				return true;
			}
		} catch (HmException e) {
			addActionError(MgrUtil.getUserMessage(e));
			return false;
		}
	}

	private JSONObject getStores() throws JSONException {
		jsonObject = new JSONObject();
		String presenceCustomerId = PresenceUtil
				.getPresenceCustomerId(domainId);
		if (null == presenceCustomerId || "".equals(presenceCustomerId)) {
			jsonObject.put("msg", "Presence Customer ID is invalid.");
			return jsonObject;
		}
		JSONObject stores = CustomersAndStoresService.getStoresForCustomer(
				presenceCustomerId, "true");
		if (null == stores) {
			jsonObject.put("msg",
					MgrUtil.getUserMessage("error.presence.store.fetch"));
			return jsonObject;
		}
		// jsonObject.put("stores", stores);
		jsonObject = stores;
		JSONArray array = stores.getJSONArray("stores");
		if (null != array && array.length() > 0) {
			for (int i = 0; i < array.length(); i++) {
				JSONObject store = array.getJSONObject(i);
				String storeName = store.getString("name");
				String id = store.getString("id");
				JSONObject sensors = CustomersAndStoresService
						.getSensorsForStore(presenceCustomerId, storeName,
								"true");
				if (null != sensors) {
					JSONArray sensorArray = sensors.getJSONArray("sensors");
					JSONArray sensorArray2 = new JSONArray();
					if (null != sensorArray && sensorArray.length() > 0) {
						for (int j = 0; j < sensorArray.length(); j++) {
							JSONObject sensor = sensorArray.getJSONObject(j);
							String mac = sensor.getString("mac_address");
							if (null == mac) {
								continue;
							}
							if (null == PresenceUtil.getSimpleHiveAp(domainId,
									mac.replaceAll(":", ""))) {
								log.warn(String
										.format("device %s is not currently under customer management, domain id is %s",
												mac, domainId));
								continue;
							}
							sensorArray2.put(sensor);
						}
					}
					sensors.put("sensors", sensorArray2);
					sensors.put("store", store);
					jsonObject.put(id, sensors);
				}
			}
		}
		return jsonObject;
	}
	
	/* use one API to fetch all stores & data feed for a customer */
	private JSONObject getStores_v2() throws JSONException {
		jsonObject = new JSONObject();
		String presenceCustomerId = PresenceUtil
				.getPresenceCustomerId(domainId);
		if (null == presenceCustomerId || "".equals(presenceCustomerId)) {
			jsonObject.put("msg", "Presence Customer ID is invalid.");
			return jsonObject;
		}
		JSONObject result = CustomersAndStoresService
				.getStoresFeedsForCustomer(presenceCustomerId);
		if (null == result) {
			jsonObject.put("msg",
					MgrUtil.getUserMessage("error.presence.store.fetch"));
			return jsonObject;
		}
		Map<String, JSONObject> activedStores = new HashMap<>();
		Map<String, JSONArray> activedFeeds = new HashMap<>();
		setActivedStoresAndFeeds(result, activedStores, activedFeeds);
		jsonObject = new JSONObject();
		jsonObject.put("stores", new JSONArray(activedStores.values()));
		for (String storeName : activedStores.keySet()) {
			JSONArray storeFeeds = activedFeeds.get(storeName);
			JSONObject store = activedStores.get(storeName);
			JSONObject feeds = new JSONObject();
			feeds.put("sensors", storeFeeds);
			// data feeds ref to store
			feeds.put("store", store);
			// stores ref to data feeds
			jsonObject.put(store.getString("id"), feeds);
		}
		return jsonObject;
	}

	private void setActivedStoresAndFeeds(JSONObject verboseCustomer,
			Map<String, JSONObject> activedStores,
			Map<String, JSONArray> activedFeeds) throws JSONException {
		JSONObject entity = verboseCustomer.getJSONObject("entity");
		if (null != entity) {
			JSONArray stores = entity.getJSONArray("stores");
			JSONArray feeds = entity.getJSONArray("data_feeds");
			// actived stores
			if (null != stores && stores.length() > 0) {
				for (int i = 0; i < stores.length(); i++) {
					JSONObject store = stores.getJSONObject(i);
					String storeName = store.getString("name");
					boolean actived = store.getBoolean("active");
					if (!actived) {
						continue;
					}
					activedStores.put(storeName, store);
				}
			}
			// actived feeds for actived store
			if (null != feeds && feeds.length() > 0) {
				for (int i = 0; i < feeds.length(); i++) {
					JSONObject feed = feeds.getJSONObject(i);
					String storeName = feed.getString("store_name");
					String mac = feed.getString("mac_address");
					boolean actived = feed.getBoolean("active");
					if (null == mac) {
						continue;
					}
					if (!actived) {
						continue;
					}
					if (null == PresenceUtil.getSimpleHiveAp(domainId,
							mac.replaceAll(":", ""))) {
						log.warn(String
								.format("device %s is not currently under customer management, domain id is %s",
										mac, domainId));
						continue;
					}
					JSONArray storeFeeds = activedFeeds.get(storeName);
					if (null == storeFeeds) {
						storeFeeds = new JSONArray();
						activedFeeds.put(storeName, storeFeeds);
					}
					storeFeeds.put(feed);
				}
			}
		}
	}

	private JSONObject createOrUpdateStore(String storeName, boolean isCreate)
			throws JSONException {
		JSONObject jsonObject = new JSONObject();
		// check registration
		boolean isRegistered;
		try {
			isRegistered = PresenceUtil.isCustomerRegistered(domainId);
		} catch (HmException e) {
			jsonObject.put("msg", MgrUtil.getUserMessage(e));
			return jsonObject;
		}
		if (!isRegistered) {
			jsonObject.put("msg", MgrUtil
					.getUserMessage("error.presence.customer.unregister"));
			return jsonObject;
		}
		String presenceCustomerId = PresenceUtil
				.getPresenceCustomerId(domainId);
		if (null == presenceCustomerId || "".equals(presenceCustomerId)) {
			jsonObject.put("msg", "Presence Customer ID is invalid.");
			return jsonObject;
		}
		// store
		boolean success = CustomersAndStoresService
				.updateStore(new PresenceStorePojo(presenceCustomerId,
						storeName, storeTimezone, storeAddr, storeCity,
						storeState, true));
		if (!success) {
			if (isCreate) {
				jsonObject.put("msg", MgrUtil.getUserMessage(
						"error.presence.store.create", storeName));
			} else {
				jsonObject.put("msg", MgrUtil.getUserMessage(
						"error.presence.store.update", storeName));
			}
			return jsonObject;
		}
		if (isCreate) {
			jsonObject.put("suc", MgrUtil.getUserMessage(
					"info.presence.store.create", storeName));
		} else {
			jsonObject.put("suc", MgrUtil.getUserMessage(
					"info.presence.store.update", storeName));
		}
		// query sensor
		JSONObject sensorsForStore = CustomersAndStoresService
				.getSensorsForStore(presenceCustomerId, storeName, "true");
		if (null == sensorsForStore) {
			jsonObject.put("msg", MgrUtil.getUserMessage(
					"error.presence.store.sensor.query", storeName));
			return jsonObject;
		}
		List<JSONObject> deactivation = new ArrayList<JSONObject>();
		JSONArray sensors = sensorsForStore.getJSONArray("sensors");
		if (null != sensors && sensors.length() > 0) {
			for (int i = 0; i < sensors.length(); i++) {
				JSONObject sensor = sensors.getJSONObject(i);
				String macWithColons = sensor.getString("mac_address");
				if (null == selectedDevices
						|| !selectedDevices.contains(macWithColons)) {
					log.info(String
							.format("sensor %s is not in current list, put it to de-activation list.",
									macWithColons));
					deactivation.add(sensor);
				} else {
					log.info(String
							.format("sensor %s is in current list, remove it from config list.",
									macWithColons));
					selectedDevices.remove(macWithColons);
				}
			}
		}
		// update sensor
		List<PresenceSensorPojo> sensorPojos = new ArrayList<PresenceSensorPojo>();
		if (deactivation.size() > 0) {
			for (JSONObject sensor : deactivation) {
				String sensorName = sensor.getString("name");
				String macAddress = sensor.getString("mac_address");
				sensorPojos.add(new PresenceSensorPojo(presenceCustomerId,
						storeName, sensorName, getHiveApHostname(macAddress),
						macAddress, false));
			}
		}
		if (null != selectedDevices && selectedDevices.size() > 0) {
			for (String macAddress : selectedDevices) {
				String clearMacAddress = macAddress.replaceAll(":", "");
				sensorPojos.add(new PresenceSensorPojo(presenceCustomerId,
						storeName, storeName + "-" + clearMacAddress,
						getHiveApHostname(macAddress), macAddress, true));
			}
		}
		if (!sensorPojos.isEmpty()) {
			success = CustomersAndStoresService.updateSensors(sensorPojos);
			if (!success) {
				if (isCreate) {
					jsonObject.put("fs", MgrUtil.getUserMessage(
							"error.presence.store.create.sensor.mapping",
							storeName));
				} else {
					jsonObject.put("fs", MgrUtil.getUserMessage(
							"error.presence.store.update.sensor.mapping",
							storeName));
				}
			}
		}
		return jsonObject;
	}

	private JSONObject disableStore(List<String> storeNames)
			throws JSONException {
		jsonObject = new JSONObject();
		if (null == storeNames || storeNames.size() == 0) {
			jsonObject.put("msg", "Cannot get store name.");
			return jsonObject;
		}
		String presenceCustomerId = PresenceUtil
				.getPresenceCustomerId(domainId);
		if (null == presenceCustomerId || "".equals(presenceCustomerId)) {
			jsonObject.put("msg", "Presence Customer ID is invalid.");
			return jsonObject;
		}
		/*String storeName = storeNames.get(0);*/
		/*- do not need to de-active sensors when disable a store, Euclid will de-active these sensors
		List<PresenceSensorPojo> sensorPojos = new ArrayList<PresenceSensorPojo>();
		// update sensors to de-activated
		JSONObject sensorsForStore = CustomersAndStoresService
				.getSensorsForStore(vhmOrSystemId, storeName, "true");
		if (null == sensorsForStore) {
			jsonObject.put("msg", MgrUtil.getUserMessage(
					"error.presence.store.sensor.query", storeName));
			return jsonObject;
		}
		JSONArray sensors = sensorsForStore.getJSONArray("sensors");
		if (null != sensors && sensors.length() > 0) {
			for (int i = 0; i < sensors.length(); i++) {
				JSONObject sensor = sensors.getJSONObject(i);
				String sensorName = sensor.getString("name");
				String macAddress = sensor.getString("mac_address");
				sensorPojos.add(new PresenceSensorPojo(vhmOrSystemId,
						storeName, sensorName, macAddress, false));
			}
		}

		if (!sensorPojos.isEmpty()) {
			boolean success = CustomersAndStoresService
					.updateSensors(sensorPojos);
			if (!success) {
				jsonObject.put("msg", MgrUtil.getUserMessage(
						"error.presence.store.disable", storeName));
				return jsonObject;
			}
		}*/
		// update store to de-activated
		StringBuffer msgSbf=new StringBuffer("");
		int msgIndex=0;
		StringBuffer sucSbf=new StringBuffer("");
		int sucIndex=0;
		for(String storeName:storeNames){
			boolean success = CustomersAndStoresService
					.updateStore(new PresenceStorePojo(presenceCustomerId,
							storeName, false));
			if(!success){
				if(msgIndex!=0){
					msgSbf.append(",");
				}
				msgSbf.append(storeName);
				msgIndex++;
			}else{
				if(sucIndex!=0){
					sucSbf.append(",");
				}
				sucSbf.append(storeName);
				sucIndex++;
			}
		}
		if(msgSbf.length()!=0){
			jsonObject.put("msg", MgrUtil.getUserMessage(
					"error.presence.store.disable", msgSbf.toString()));
		}
		if(sucSbf.length()!=0){
			jsonObject.put("suc", MgrUtil.getUserMessage(
					"info.presence.store.remove", sucSbf.toString()));
		}
		return jsonObject;
	}

	public String getCustomerRetailDashboardUrl() throws JSONException {
		log.info("getCustomerRetailDashboardUrl, " + presenceLiteDashboardUrl);
		String presenceCustomerId = PresenceUtil
				.getPresenceCustomerId(domainId);
		try {
			return presenceLiteDashboardUrl + "?auth_token="
					+ PresenceUtil.getCustomerUserAuthToken(presenceCustomerId);
		} catch (HmException e) {
			addActionError(MgrUtil.getUserMessage(e));
			return presenceLiteDashboardUrl + "?auth_token=null";
		}
	}

	public String getCustomerRetailPermiumSignUrl() {
		log.info("getCustomerRetailPermiumSignUrl, " + presencePermiumSignUrl);
		return presencePermiumSignUrl;
	}

	public String getCustomerRetailPermiumDashboardUrl() {
		log.info("getCustomerRetailPermiumDashboardUrl, "
				+ presencePermiumDashboardUrl);
		return presencePermiumDashboardUrl;
	}

	public List<TextItem> getTimeZones() {
		return JodaTimeZone.getJodaTimeLocaleList();
	}

	public String getTotalDevices() throws JSONException {
		JSONArray devices = new JSONArray();
		List<?> list;
		String query = "select bo.hostName, bo.macAddress, bo.wifi0RadioProfile.enabledPresence, bo.wifi1RadioProfile.enabledPresence from "
				+ HiveAp.class.getCanonicalName() + " bo";
		String where = "hiveApModel in (:s1) and manageStatus = :s2";
		Object[] values = new Object[] {
				HiveApUtils.getPresenceSupportDeviceFilter(),
				HiveAp.STATUS_MANAGED };
		list = QueryUtil.executeQuery(query, new SortParams("bo.macAddress"),
				new FilterParams(where, values), domainId);
		for (Object object : list) {
			Object[] objects = (Object[]) object;
			String host = (String) objects[0];
			String mac = (String) objects[1];
			Boolean wifi0 = (Boolean) objects[2];
			Boolean wifi1 = (Boolean) objects[3];
			boolean enabled = (null != wifi0 && wifi0)
					|| (null != wifi1 && wifi1);
			JSONObject json = new JSONObject();
			json.put("mac", getMacAddressWithColons(mac));
			json.put("host", host);
			json.put("enabled", enabled);
			devices.put(json);
		}
		return devices.toString();
	}

	private String getMacAddressWithColons(String macAddress) {
		if (StringUtils.isEmpty(macAddress)) {
			return macAddress;
		}
		char[] chars = macAddress.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			if (i > 0 && i % 2 == 0) {
				sb.append(":");
			}
			sb.append(chars[i]);
		}
		return sb.toString().toUpperCase();
	}

	private SimpleHiveAp getSimpleHiveAp(String macAddress) {
		if (StringUtils.isEmpty(macAddress)) {
			return null;
		}
		return CacheMgmt.getInstance().getSimpleHiveAp(macAddress);
	}

	private String getHiveApHostname(String macAddressWithColon) {
		if (null == macAddressWithColon) {
			return null;
		}
		String macAddress = macAddressWithColon.replaceAll(":", "");
		SimpleHiveAp sha = getSimpleHiveAp(macAddress);
		if (null == sha) {
			return macAddressWithColon;
		}
		if (null == sha.getHostname()) {
			return macAddressWithColon;
		}
		return sha.getHostname();
	}

//	public boolean getCustomerLiteVersion() {
//		String presenceCustomerId = PresenceUtil
//				.getPresenceCustomerId(domainId);
//		return PresenceUtil.isLiteVersion(presenceCustomerId);
//	}

	private String storeName = "";
	private String storeAddr = "";
	private String storeTimezone = "";
	private String storeCity = "";
	private String storeState = "";
	private String sensorName = "";
	private List<String> selectedDevices;
	private List<String> selectedStores;

	public static final short CUSTOMER_REGISTER_UNKNOWN = 0;
	public static final short CUSTOMER_REGISTER_YES = 1;
	public static final short CUSTOMER_REGISTER_NO = 2;
	private short customerRegistered;
	private boolean firstTime = false;

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public void setStoreTimezone(String storeTimezone) {
		this.storeTimezone = storeTimezone;
	}

	public String getStoreTimezone() {
		return storeTimezone;
	}

	public void setStoreAddr(String storeAddr) {
		this.storeAddr = storeAddr;
	}

	public void setStoreCity(String storeCity) {
		this.storeCity = storeCity;
	}

	public void setStoreState(String storeState) {
		this.storeState = storeState;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public void setSelectedDevices(List<String> selectedDevices) {
		this.selectedDevices = selectedDevices;
	}

	public void setSelectedStores(List<String> selectedStores) {
		this.selectedStores = selectedStores;
	}

	public short getCustomerRegistered() {
		return customerRegistered;
	}

	public boolean getFirstTime() {
		return firstTime;
	}

	// status of connect with Euclid server
	public boolean isConnectEuclid() {
		return PresenceUtil.getConnectEuclidStatus();
	}

	private void initializeSensorData() {
		try {
			if (null == getSensorPojoMap()) {
				return;
			}
			Map<String, Long> timeMap = new HashMap<String, Long>();
			timeMap.putAll(RestAhPreSenceService.getTimeMap());
			Map<String, AhPresenceSensorData> newMinuteSensorDataMap = new HashMap<String, AhPresenceSensorData>();
			Map<String, AhPresenceSensorData> newHourSensorDataMap = new HashMap<String, AhPresenceSensorData>();
			Map<String, AhPresenceSensorData> newDaysensorDataMap = new HashMap<String, AhPresenceSensorData>();
			List<String> macAddressList = new ArrayList<String>();
			for (Object obj : getSensorPojoMap().keySet().toArray()) {
				List<PresenceSensorPojo> sensorPojoList = getSensorPojoMap()
						.get(obj);
				for (PresenceSensorPojo pojo : sensorPojoList) {
					String macAddress = pojo.getMac();
					String hostName = pojo.getHostname();
					newMinuteSensorDataMap.put(macAddress,
							new AhPresenceSensorData(macAddress, hostName));
					newHourSensorDataMap.put(macAddress,
							new AhPresenceSensorData(macAddress, hostName));
					newDaysensorDataMap.put(macAddress,
							new AhPresenceSensorData(macAddress, hostName));
					macAddressList.add(macAddress);
					if (null == timeMap.get(macAddress)) {
						continue;
					}
					if (postTime < timeMap.get(macAddress)) {
						postTime = timeMap.get(macAddress);
					}
				}
			}
			// set minuteTimeStamp,hourTimeStamp and dayTimeStamp
			setAllTypeTime();
			// put sensorData in Map
			Map<String, AhPresenceSensorData> minuteSensorDataMap = new HashMap<String, AhPresenceSensorData>();
			minuteSensorDataMap.putAll(BePresenceProcessor
					.getMinuteSensorDataMap());
			Map<String, AhPresenceSensorData> hourSensorDataMap = new HashMap<String, AhPresenceSensorData>();
			hourSensorDataMap
					.putAll(BePresenceProcessor.getHourSensorDataMap());
			Map<String, AhPresenceSensorData> daysensorDataMap = new HashMap<String, AhPresenceSensorData>();
			daysensorDataMap.putAll(BePresenceProcessor.getDaySensorDataMap());
			// last minute data
			setSensorDataMapValue(macAddressList, minuteSensorDataMap,
					newMinuteSensorDataMap, MINUTETYPE, minuteTimeStamp);
			this.setMinuteSensorData(getStoreSensorDataList(newMinuteSensorDataMap));
			// last hour data
			setSensorDataMapValue(macAddressList, hourSensorDataMap,
					newHourSensorDataMap, HOURTYPE, hourTimeStamp);
			this.setHourSensorData(getStoreSensorDataList(newHourSensorDataMap));
			// last day data
			setSensorDataMapValue(macAddressList, daysensorDataMap,
					newDaysensorDataMap, DAYTYPE, dayTimeStamp);
			this.setDaySensorData(getStoreSensorDataList(newDaysensorDataMap));
		} catch (Exception e) {
			log.error("initializeSensorData error:", e);
			e.printStackTrace();
		}
	}

	private void setSensorDataMapValue(List<String> macAddressList,
			Map<String, AhPresenceSensorData> sensorDataMap,
			Map<String, AhPresenceSensorData> newSensorDataMap, int type,
			long timeStamp) {
		double bandWidths = 0;
		int clientMacs = 0;
		for (Object key : sensorDataMap.keySet().toArray()) {
			String newKey = key.toString().substring(0,
					key.toString().indexOf(":"));
			if (!macAddressList.contains(newKey)) {
				continue;
			}
			AhPresenceSensorData sensorData = sensorDataMap.get(key);
			if (sensorData.getTimeStamp() == timeStamp) {
				continue;
			}
			AhPresenceSensorData obj = newSensorDataMap.get(newKey);
			if (null == obj) {
				continue;
			}
			obj.setObjects(obj.getObjects() + sensorData.getObjects());
			obj.setClientMacs(obj.getClientMacs() + sensorData.getClientMacs());
			obj.setBandWidth(obj.getBandWidth() + sensorData.getBandWidth());
			bandWidths += sensorData.getBandWidth();
			clientMacs += sensorData.getClientMacs();
		}
		if (type == MINUTETYPE) {
			// minute
			this.setMinuteBandWidths(bandWidths);
			this.setMinuteClients(clientMacs);
		} else if (type == HOURTYPE) {
			// hour
			this.setHourBandWidths(bandWidths);
			this.setHourClients(clientMacs);
		} else if (type == DAYTYPE) {
			// day
			this.setDayBandWidths(bandWidths);
			this.setDayClients(clientMacs);
		}
	}

	private List<AhStoreSensorData> getStoreSensorDataList(
			Map<String, AhPresenceSensorData> sensorDataMap) {
		List<AhStoreSensorData> storeSensorDataList = new ArrayList<AhStoreSensorData>();
		for (Object obj : getSensorPojoMap().keySet().toArray()) {
			int objectCount = 0;
			int clientMacCount = 0;
			int bandWidthCount = 0;
			List<AhPresenceSensorData> sensorDataList = new ArrayList<AhPresenceSensorData>();
			List<PresenceSensorPojo> sensorPojoList = getSensorPojoMap().get(
					obj);
			if (null != sensorPojoList) {
				for (PresenceSensorPojo sensor : sensorPojoList) {
					AhPresenceSensorData sensorData = sensorDataMap.get(sensor
							.getMac());
					if (null == sensorData) {
						continue;
					}
					objectCount += sensorData.getObjects();
					clientMacCount += sensorData.getClientMacs();
					bandWidthCount += sensorData.getBandWidth();
					SimpleHiveAp hiveAp = CacheMgmt.getInstance()
							.getSimpleHiveAp(sensorData.getMacAddress());
					if (null != hiveAp
							&& hiveAp.getConnectStatus() == HiveAp.CONNECT_UP) {
						sensorData.setConnectStatus(true);
					}
					sensorDataList.add(sensorData);
				}
				AhStoreSensorData storeSensor = new AhStoreSensorData();
				storeSensor.setStoreName(obj.toString());
				storeSensor.setObjectCount(objectCount);
				storeSensor.setClientMacCount(clientMacCount);
				storeSensor.setDeviceCount(sensorDataList.size());
				storeSensor.setBandWidthCount(bandWidthCount);
				storeSensor.setSensorDataList(sensorDataList);
				storeSensorDataList.add(storeSensor);
			}
		}
		return storeSensorDataList;
	}

	private Map<String, List<PresenceSensorPojo>> getStoreSensorPojoMap() {
		String presenceCustomerId = PresenceUtil
				.getPresenceCustomerId(domainId);
		if (null == presenceCustomerId || "".equals(presenceCustomerId)) {
			this.setStoreErrorMsg("Presence Customer ID is invalid.");
			return null;
		}
		Map<String, List<PresenceSensorPojo>> storeSensorPojoMap = new LinkedHashMap<String, List<PresenceSensorPojo>>();
		try {
			long start = System.currentTimeMillis();
			JSONObject storeObject = CustomersAndStoresService
					.getStoresForCustomer(presenceCustomerId, "true");
			if (null == storeObject) {
				this.setStoreErrorMsg(MgrUtil
						.getUserMessage("error.presence.store.fetch"));
				return null;
			}
			JSONArray storeArray = storeObject.getJSONArray("stores");
			for (int i = 0; i < storeArray.length(); i++) {
				JSONObject object = (JSONObject) storeArray.opt(i);
				String storeName = object.getString("name");
				boolean active = object.getBoolean("active");
				JSONObject sensorObject = CustomersAndStoresService
						.getSensorsForStore(presenceCustomerId, storeName,
								String.valueOf(active));
				if (null == sensorObject) {
					continue;
				}
				List<PresenceSensorPojo> sensorList = new ArrayList<PresenceSensorPojo>();
				JSONArray sensorArray = sensorObject.getJSONArray("sensors");
				for (int j = 0; j < sensorArray.length(); j++) {
					JSONObject object2 = (JSONObject) sensorArray.opt(j);
					if (null == object2) {
						continue;
					}
					String macAddress = object2.getString("mac_address");
					if (null != macAddress) {
						macAddress = macAddress.replace(":", "");
					}
					SimpleHiveAp simpleHiveAp = PresenceUtil.getSimpleHiveAp(
							domainId, macAddress);
					if (null == simpleHiveAp) {
						continue;
					}
					String hostName = simpleHiveAp.getHostname();
					sensorList.add(new PresenceSensorPojo(storeName, hostName,
							macAddress));
				}
				storeSensorPojoMap.put(storeName, sensorList);
			}
			long end = System.currentTimeMillis();
			log.info(String
					.format("get stores and sensors for presence customer id %s cost %s ms.",
							presenceCustomerId, String.valueOf(end - start)));
		} catch (JSONException e) {
			log.error("getStoreSensorPojoMap", e);
		}
		return storeSensorPojoMap;
	}
	
	/* use one API to fetch all stores & data feed for a customer */
	private Map<String, List<PresenceSensorPojo>> getStoreSensorPojoMap_v2() {
		String presenceCustomerId = PresenceUtil
				.getPresenceCustomerId(domainId);
		if (null == presenceCustomerId || "".equals(presenceCustomerId)) {
			this.setStoreErrorMsg("Presence Customer ID is invalid.");
			return null;
		}
		Map<String, List<PresenceSensorPojo>> storeSensorPojoMap = new LinkedHashMap<String, List<PresenceSensorPojo>>();
		try {
			long start = System.currentTimeMillis();
			JSONObject result = CustomersAndStoresService
					.getStoresFeedsForCustomer(presenceCustomerId);
			if (null == result) {
				this.setStoreErrorMsg(MgrUtil
						.getUserMessage("error.presence.store.fetch"));
				return null;
			}
			Map<String, JSONObject> activedStores = new HashMap<>();
			Map<String, JSONArray> activedFeeds = new HashMap<>();
			setActivedStoresAndFeeds(result, activedStores, activedFeeds);
			for (String storeName : activedStores.keySet()) {
				JSONArray feeds = activedFeeds.get(storeName);
				List<PresenceSensorPojo> sensorList = new ArrayList<PresenceSensorPojo>();
				for (int i = 0; i < feeds.length(); i++) {
					JSONObject feed = feeds.getJSONObject(i);
					String macAddress = feed.getString("mac_address");
					if (null != macAddress) {
						macAddress = macAddress.replace(":", "");
					}
					SimpleHiveAp simpleHiveAp = PresenceUtil.getSimpleHiveAp(
							domainId, macAddress);
					String hostName = simpleHiveAp.getHostname();
					sensorList.add(new PresenceSensorPojo(storeName, hostName,
							macAddress));
				}
				storeSensorPojoMap.put(storeName, sensorList);
			}
			long end = System.currentTimeMillis();
			log.info(String
					.format("get stores and sensors for presence customer id %s cost %s ms.",
							presenceCustomerId, String.valueOf(end - start)));
		} catch (JSONException e) {
			log.error("getStoreSensorPojoMap", e);
		}
		return storeSensorPojoMap;
	}

	private JSONObject getJsonStoreSensorData() {
		JSONObject jsonObject = new JSONObject();
		try {
			initializeSensorData();
			jsonObject.put("lastPostTime", getLastPostTime());
			jsonObject.put("minuteBandWidths", getMinuteBandWidths());
			jsonObject.put("hourBandWidths", getHourBandWidths());
			jsonObject.put("dayBandWidths", getDayBandWidths());

			jsonObject.put("minuteClients", getMinuteClients());
			jsonObject.put("hourClients", getHourClients());
			jsonObject.put("dayClients", getDayClients());

			jsonObject.put("minuteTimeRange", getMinuteTimeRange());
			jsonObject.put("hourTimeRange", getHourTimeRange());
			jsonObject.put("dayTimeRange", getDayTimeRange());

			List<AhStoreSensorData> minuteSensorDataList = this
					.getMinuteSensorData();
			setJsonSensorDataStr(minuteSensorDataList, MINUTETYPE, jsonObject);
			List<AhStoreSensorData> hourSensorDataList = this
					.getHourSensorData();
			setJsonSensorDataStr(hourSensorDataList, HOURTYPE, jsonObject);
			List<AhStoreSensorData> daySensorDataList = this.getDaySensorData();
			setJsonSensorDataStr(daySensorDataList, DAYTYPE, jsonObject);
		} catch (Exception e) {
			log.error("getJsonStoreData error:", e);
			e.printStackTrace();
		}
		return jsonObject;
	}

	private void setJsonSensorDataStr(List<AhStoreSensorData> sensorDataList,
			int type, JSONObject jsonObject) throws JSONException {
		if (null == sensorDataList) {
			return;
		}
		String objectCount = "";
		String clientMacCount = "";
		String bandWidthCount = "";
		String jsonDataStr = "";
		if (type == MINUTETYPE) {
			// minute
			objectCount = "minuteObjectCount";
			clientMacCount = "minuteClientMacCount";
			bandWidthCount = "minuteBandWidthCount";
			jsonDataStr = "minuteJsonDataStr";
		} else if (type == HOURTYPE) {
			// hour
			objectCount = "hourObjectCount";
			clientMacCount = "hourClientMacCount";
			bandWidthCount = "hourBandWidthCount";
			jsonDataStr = "hourJsonDataStr";
		} else if (type == DAYTYPE) {
			// day
			objectCount = "dayObjectCount";
			clientMacCount = "dayClientMacCount";
			bandWidthCount = "dayBandWidthCount";
			jsonDataStr = "dayJsonDataStr";
		}

		int index = 0;
		for (AhStoreSensorData storeData : sensorDataList) {
			jsonObject.put(objectCount + index, storeData.getObjectCount());
			jsonObject.put(clientMacCount + index,
					storeData.getClientMacCount());
			jsonObject.put(bandWidthCount + index,
					storeData.getBandWidthCount());
			if (null != storeData.getSensorDataList()) {
				StringBuffer sb = new StringBuffer();
				for (AhPresenceSensorData sensorData : storeData
						.getSensorDataList()) {
					sb.append("<tr>");
					sb.append("<td style='width: 2%'>");
					if (sensorData.isConnectStatus()) {
						sb.append("<img class='dinl' hspace='2' src='"
								+ request.getContextPath()
								+ "/images/HM-capwap-up.png"
								+ "' title='Connected'>");
					} else {
						sb.append("<img class='dinl' hspace='2' src='"
								+ request.getContextPath()
								+ "/images/HM-capwap-down.png"
								+ "' title='Disconnected'>");
					}
					sb.append("</td>");
					sb.append("<td style='width: 27%'><a href='javascript:;' onclick=parent.showTrackPanel('"
							+ sensorData.getMacAddress()
							+ "','"
							+ storeData.getStoreName()
							+ "','"
							+ sensorData.getHostName()
							+ "');>"
							+ sensorData.getHostName() + "</a></td>");
					sb.append("<td style='width: 23%'>"
							+ sensorData.getObjects() + "</td>");
					sb.append("<td style='width: 25%'>"
							+ sensorData.getClientMacs() + "</td>");
					sb.append("<td>" + sensorData.getConvertBandWidth()
							+ "</td>");
					sb.append("</tr>");
				}
				jsonObject.put(jsonDataStr + index, sb.toString());
			}
			index++;
		}
	}

	// sensorData by last minute
	private List<AhStoreSensorData> minuteSensorData;
	// sensorData by last Hour
	private List<AhStoreSensorData> hourSensorData;
	// sensorData by last day
	private List<AhStoreSensorData> daySensorData;

	private double minuteBandWidths;
	private double hourBandWidths;
	private double dayBandWidths;

	private int minuteClients;
	private int hourClients;
	private int dayClients;
	// Last time sensor data (a JSON object) was posted onto the Euclid server.
	private long postTime;
	private long minuteTimeStamp;
	private long hourTimeStamp;
	private long dayTimeStamp;

	private long minuteOlderTimeStamp;
	private long hourOlderTimeStamp;
	private long dayOlderTimeStamp;

	private static final int MINUTETYPE = 1;
	private static final int HOURTYPE = 2;
	private static final int DAYTYPE = 3;
	private String storeErrorMsg;

	private static Map<Long, Map<String, List<PresenceSensorPojo>>> storeSensorPojoMap = new HashMap<Long, Map<String, List<PresenceSensorPojo>>>();

	public Map<String, List<PresenceSensorPojo>> getSensorPojoMap() {
		return storeSensorPojoMap.get(domainId);
	}

	public String getLastPostTime() {
		if (postTime == 0) {
			return "";
		}
		return getTimeZoneTime(postTime);
	}
	
	public String getEuclidId(){
		String sql="select customerId from "+ PresenceAnalyticsCustomer.class.getSimpleName() +" where owner.id="+domainId;
		List<?> list =QueryUtil.executeQuery(sql, 1);
		String customerId =(String) list.get(0);
		return customerId;
	}

	private String getTimeZoneTime(long timeStamp) {
		return AhDateTimeUtil.getSpecifyDateTime(timeStamp, userContext
				.getSwitchDomain() == null ? getUserTimeZone() : userContext
				.getSwitchDomain().getTimeZone(), getDomain());
	}

	private void setAllTypeTime() {
		Calendar cal = Calendar.getInstance();
		int minuteValue = cal.get(Calendar.MINUTE);
		int hourValue = cal.get(Calendar.HOUR_OF_DAY);
		int dayValue = cal.get(Calendar.DAY_OF_MONTH);

		cal.set(Calendar.MILLISECOND, 0);
		minuteTimeStamp = cal.getTime().getTime();

		cal.set(Calendar.MINUTE, minuteValue - 1);
		this.setMinuteOlderTimeStamp(cal.getTime().getTime());
		cal.set(Calendar.MINUTE, minuteValue);

		cal.set(Calendar.SECOND, 0);
		hourTimeStamp = cal.getTime().getTime();

		cal.set(Calendar.HOUR_OF_DAY, hourValue - 1);
		this.setHourOlderTimeStamp(cal.getTime().getTime());
		cal.set(Calendar.HOUR_OF_DAY, hourValue);

		cal.set(Calendar.MINUTE, 0);
		dayTimeStamp = cal.getTime().getTime();

		cal.set(Calendar.DAY_OF_MONTH, dayValue - 1);
		this.setDayOlderTimeStamp(cal.getTime().getTime());
	}

	public int getMinuteClients() {
		return minuteClients;
	}

	public void setMinuteClients(int minuteClients) {
		this.minuteClients = minuteClients;
	}

	public int getHourClients() {
		return hourClients;
	}

	public void setHourClients(int hourClients) {
		this.hourClients = hourClients;
	}

	public int getDayClients() {
		return dayClients;
	}

	public void setDayClients(int dayClients) {
		this.dayClients = dayClients;
	}

	public String getMinuteBandWidths() {
		return PresenceUtil.convertValue(minuteBandWidths);
	}

	public void setMinuteBandWidths(double minuteBandWidths) {
		this.minuteBandWidths = minuteBandWidths;
	}

	public String getHourBandWidths() {
		return PresenceUtil.convertValue(hourBandWidths);
	}

	public void setHourBandWidths(double hourBandWidths) {
		this.hourBandWidths = hourBandWidths;
	}

	public String getDayBandWidths() {
		return PresenceUtil.convertValue(dayBandWidths);
	}

	public void setDayBandWidths(double dayBandWidths) {
		this.dayBandWidths = dayBandWidths;
	}

	public void setMinuteSensorData(List<AhStoreSensorData> minuteSensorData) {
		this.minuteSensorData = minuteSensorData;
	}

	public List<AhStoreSensorData> getMinuteSensorData() {
		return minuteSensorData;
	}

	public void setHourSensorData(List<AhStoreSensorData> hourSensorData) {
		this.hourSensorData = hourSensorData;
	}

	public List<AhStoreSensorData> getHourSensorData() {
		return hourSensorData;
	}

	public List<AhStoreSensorData> getDaySensorData() {
		return daySensorData;
	}

	public void setDaySensorData(List<AhStoreSensorData> daySensorData) {
		this.daySensorData = daySensorData;
	}

	public String getMinuteTimeRange() {
		return getTimeZoneTime(minuteOlderTimeStamp) + " - "
				+ getTimeZoneTime(minuteTimeStamp);
	}

	public void setMinuteOlderTimeStamp(long minuteOlderTimeStamp) {
		this.minuteOlderTimeStamp = minuteOlderTimeStamp;
	}

	public String getHourTimeRange() {
		return getTimeZoneTime(hourOlderTimeStamp) + " - "
				+ getTimeZoneTime(hourTimeStamp);
	}

	public void setHourOlderTimeStamp(long hourOlderTimeStamp) {
		this.hourOlderTimeStamp = hourOlderTimeStamp;
	}

	public String getDayTimeRange() {
		return getTimeZoneTime(dayOlderTimeStamp) + " - "
				+ getTimeZoneTime(dayTimeStamp);
	}

	public void setDayOlderTimeStamp(long dayOlderTimeStamp) {
		this.dayOlderTimeStamp = dayOlderTimeStamp;
	}

	public String getStoreErrorMsg() {
		return storeErrorMsg;
	}

	public void setStoreErrorMsg(String storeErrorMsg) {
		this.storeErrorMsg = storeErrorMsg;
	}

	private JSONObject jsonObject;
	private JSONArray jsonArray;

	@Override
	public String getJSONString() {
		if (jsonArray == null) {
			log.debug("getJSONString", "JSON string: " + jsonObject.toString());
			return jsonObject.toString();
		} else {
			log.debug("getJSONString", "JSON string: " + jsonArray.toString());
			return jsonArray.toString();
		}
	}
}
