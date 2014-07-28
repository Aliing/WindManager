package com.ah.be.rest.client;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.be.common.PresenceUtil;
import com.ah.util.Tracer;

public class CustomersAndStoresService {

	private static final Tracer log = new Tracer(
			CustomersAndStoresService.class.getSimpleName());

	private static String presenceUrl = NmsUtil.getConfigProperty(
			"presence.config.api.url").trim();
	private static String partnerCredential = NmsUtil.getConfigProperty(
			"presence.partner.credential").trim();
	// static String presenceUrl =
	// "https://api-beta.euclidelements.com/partners";
	// static String partnerCredential = "CoUo93xEq9ybGRu2zxqr";

	private static org.apache.http.client.HttpClient client = PresenceUtil
			.getHttpClientInstance(2);

	private static void getAllCustomers(String active) {
		String targetURL = presenceUrl + "/clients?auth_token="
				+ partnerCredential + "&active=" + active;
		log.info("Target URL: " + targetURL);
		HttpGet getMethod = new HttpGet(targetURL);
		getMethod
				.addHeader("Content-Type", "application/x-www-form-urlencoded");
		log.info("Get all customers.");
		try {
			long start = System.currentTimeMillis();
			HttpResponse httpResp = client.execute(getMethod);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			long end = System.currentTimeMillis();
			log.info("statusCode: " + statusCode + ", cost: " + (end - start)
					+ "ms.");
			JSONObject response = new JSONObject("{ clients: "
					+ EntityUtils.toString(httpResp.getEntity()) + "}");
			log.info(response.toString(3));
		} catch (Exception e) {
			log.error("Get all customers failed: " + e);
		} finally {
			getMethod.releaseConnection();
		}
	}

	public static JSONObject getCustomer(String customerId) {
		HttpGet getMethod = null;
		try {
			long start = System.currentTimeMillis();
			String targetURL = presenceUrl + "/client?auth_token="
					+ partnerCredential + "&name="
					+ URLEncoder.encode(customerId, "UTF-8");
			log.info("Target URL: " + targetURL);
			getMethod = new HttpGet(targetURL);
			log.info("Get customer: " + customerId);
			HttpResponse httpResp = client.execute(getMethod);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			long end = System.currentTimeMillis();
			log.info("statusCode: " + statusCode + ", cost: " + (end - start)
					+ "ms.");
			JSONObject response = new JSONObject("{ clients: "
					+ EntityUtils.toString(httpResp.getEntity()) + "}");
			log.info(response.toString(3));
			return response;
		} catch (Exception e) {
			log.error("Get all customers failed: " + e);
			return null;
		} finally {
			if (null != getMethod)
				getMethod.releaseConnection();
		}
	}

	public static String registerCustomer(PresenceCustomerPojo customerPojo) {
		String targetURL = presenceUrl + "/clients?auth_token="
				+ partnerCredential;
		String customerId = customerPojo.getName();
		String description = customerPojo.getDescription();
		boolean active = customerPojo.isActive();
		HttpPut putMethod = new HttpPut(targetURL);
		log.info("Registering customer: " + customerId + ", " + description);
		try {
			long start = System.currentTimeMillis();
			JSONObject customer = new JSONObject();
			customer.put("name", customerId);
			customer.put("description", description);
			customer.put("active", active);
			Collection<JSONObject> customers = new Vector<JSONObject>();
			customers.add(customer);
			List<NameValuePair> entities = new ArrayList<NameValuePair>();
			NameValuePair nv = new BasicNameValuePair("clients", new JSONArray(
					customers).toString());
			entities.add(nv);
			putMethod.setEntity(new UrlEncodedFormEntity(entities));
			HttpResponse httpResp = client.execute(putMethod);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			long end = System.currentTimeMillis();
			log.info("statusCode: " + statusCode + ", cost: " + (end - start)
					+ "ms.");
			JSONObject response = new JSONObject(EntityUtils.toString(httpResp
					.getEntity()));
			log.info(response.toString(3));
			customer = (JSONObject) response.get(customerId);
			if (customer == null) {
				log.info("Customer registration failed.");
			} else {
				log.info(customer.toString(3));
				String status = customer.getString("status");
				if ("ok".equals(status)) {
					return customer.getString("credential");
				}
			}
		} catch (Exception e) {
			log.error("Customer registration failed: " + e);
		} finally {
			putMethod.releaseConnection();
		}
		return null;
	}

	public static JSONObject getStoresForCustomer(String customerId,
			String active) {
		HttpGet getMethod = null;
		try {
			long start = System.currentTimeMillis();
			String targetURL = presenceUrl + "/stores?auth_token="
					+ partnerCredential + "&client_name="
					+ URLEncoder.encode(customerId, "UTF-8") + "&active="
					+ active;
			log.info("Target URL: " + targetURL);
			getMethod = new HttpGet(targetURL);
			log.info("Get stores for customer: " + customerId);
			HttpResponse httpResp = client.execute(getMethod);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			long end = System.currentTimeMillis();
			log.info("statusCode: " + statusCode + ", cost: " + (end - start)
					+ "ms.");
			JSONObject response = new JSONObject("{ stores: "
					+ EntityUtils.toString(httpResp.getEntity()) + "}");
			log.info(response.toString(3));
			return response;
		} catch (Exception e) {
			log.error("Get all stores failed: " + e);
			return null;
		} finally {
			if (null != getMethod)
				getMethod.releaseConnection();
		}
	}

	public static JSONObject getStoresFeedsForCustomer(String customerId) {
		HttpGet getMethod = null;
		try {
			long start = System.currentTimeMillis();
			String targetURL = presenceUrl + "/client?auth_token="
					+ partnerCredential + "&name="
					+ URLEncoder.encode(customerId, "UTF-8") + "&verbose=true";
			log.info("Target URL: " + targetURL);
			getMethod = new HttpGet(targetURL);
			log.info("Get stores/data feeds for customer: " + customerId);
			HttpResponse httpResp = client.execute(getMethod);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			long end = System.currentTimeMillis();
			log.info("statusCode: " + statusCode + ", cost: " + (end - start)
					+ "ms.");
			JSONObject response = new JSONObject("{ entity: "
					+ EntityUtils.toString(httpResp.getEntity()) + "}");
			log.info(response.toString(3));
			return response;
		} catch (Exception e) {
			log.error("Get all stores/data feeds failed: " + e);
			return null;
		} finally {
			if (null != getMethod)
				getMethod.releaseConnection();
		}
	}

	private static void getStore(String customerId, String store_name) {
		HttpGet getMethod = null;
		try {
			long start = System.currentTimeMillis();
			String targetURL = presenceUrl + "/store?auth_token="
					+ partnerCredential + "&client_name="
					+ URLEncoder.encode(customerId, "UTF-8") + "&name="
					+ URLEncoder.encode(store_name, "UTF-8");
			log.info("Target URL: " + targetURL);
			getMethod = new HttpGet(targetURL);
			log.info("Get store: " + store_name + " for customer: "
					+ customerId);
			HttpResponse httpResp = client.execute(getMethod);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			long end = System.currentTimeMillis();
			log.info("statusCode: " + statusCode + ", cost: " + (end - start)
					+ "ms.");
			JSONObject response = new JSONObject("{ stores: "
					+ EntityUtils.toString(httpResp.getEntity()) + "}");
			log.info(response.toString(3));
		} catch (Exception e) {
			log.error("Get store failed: " + e);
		} finally {
			if (null != getMethod)
				getMethod.releaseConnection();
		}
	}

	public static boolean updateStore(PresenceStorePojo storePojo) {
		String targetURL = presenceUrl + "/stores?auth_token="
				+ partnerCredential;
		String customerId = storePojo.getCustomerName();
		String store_name = storePojo.getName();
		String store_number = storePojo.getNumber();
		String time_zone = storePojo.getTimezone();
		String zipcode = storePojo.getZipCode();
		String address = storePojo.getAddress();
		String city = storePojo.getCity();
		String state = storePojo.getState();
		boolean active = storePojo.isActive();
		HttpPut putMethod = new HttpPut(targetURL);
		log.info("Create/update store for: " + customerId + ": " + store_name);
		try {
			long start = System.currentTimeMillis();
			JSONObject store = new JSONObject();
			store.put("client_name", customerId);
			store.put("name", store_name);
			store.put("active", active);
			if (null != zipcode)
				store.put("zipcode", zipcode);
			if (null != store_number)
				store.put("store_number", store_number);
			if (null != address)
				store.put("address", address);
			if (null != city)
				store.put("city", city);
			if (null != state)
				store.put("state", state);
			if (null != time_zone)
				store.put("time_zone", time_zone);
			Collection<JSONObject> stores = new Vector<JSONObject>();
			stores.add(store);
			List<NameValuePair> entities = new ArrayList<NameValuePair>();
			NameValuePair nv = new BasicNameValuePair("stores", new JSONArray(
					stores).toString());
			entities.add(nv);
			putMethod.setEntity(new UrlEncodedFormEntity(entities));
			HttpResponse httpResp = client.execute(putMethod);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			long end = System.currentTimeMillis();
			log.info("statusCode: " + statusCode + ", cost: " + (end - start)
					+ "ms.");
			JSONObject response = new JSONObject(EntityUtils.toString(httpResp
					.getEntity()));
			log.info(response.toString(3));
			store = (JSONObject) response.get(store_name);
			if (store == null) {
				log.info("Store update failed.");
				return false;
			} else {
				log.info(store.toString(3));
				String status = store.getString("status");
				if (!"ok".equals(status)) {
					log.info("Store update failed: " + status);
				}
				return "ok".equals(status);
			}
		} catch (Exception e) {
			log.error("Store update failed: " + e);
			return false;
		} finally {
			putMethod.releaseConnection();
		}
	}

	public static JSONObject getSensorsForStore(String customerId,
			String store_name, String active) {
		HttpGet getMethod = null;
		try {
			long start = System.currentTimeMillis();
			String targetURL = presenceUrl + "/data_feeds?auth_token="
					+ partnerCredential + "&client_name="
					+ URLEncoder.encode(customerId, "UTF-8") + "&store_name="
					+ URLEncoder.encode(store_name, "UTF-8") + "&active="
					+ active;
			log.info("Target URL: " + targetURL);
			getMethod = new HttpGet(targetURL);
			log.info("Get data feeds for customer: " + customerId
					+ " and store: " + store_name);
			HttpResponse httpResp = client.execute(getMethod);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			long end = System.currentTimeMillis();
			log.info("statusCode: " + statusCode + ", cost: " + (end - start)
					+ "ms.");
			JSONObject response = new JSONObject("{ sensors: "
					+ EntityUtils.toString(httpResp.getEntity()) + "}");
			log.info(response.toString(3));
			return response;
		} catch (Exception e) {
			log.error("Get all data feeds failed: " + e);
			return null;
		} finally {
			if (null != getMethod)
				getMethod.releaseConnection();
		}
	}

	private static void getSensor(String customerId, String store_name,
			String sensor_name) {
		HttpGet getMethod = null;
		try {
			long start = System.currentTimeMillis();
			String targetURL = presenceUrl + "/data_feed?auth_token="
					+ partnerCredential + "&client_name="
					+ URLEncoder.encode(customerId, "UTF-8") + "&store_name="
					+ URLEncoder.encode(store_name, "UTF-8") + "&name="
					+ URLEncoder.encode(sensor_name, "UTF-8");
			log.info("Target URL: " + targetURL);
			getMethod = new HttpGet(targetURL);
			log.info("Get data feed " + sensor_name + " for customer "
					+ customerId + " and store: " + store_name);
			HttpResponse httpResp = client.execute(getMethod);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			long end = System.currentTimeMillis();
			log.info("statusCode: " + statusCode + ", cost: " + (end - start)
					+ "ms.");
			JSONObject response = new JSONObject("{ sensors: "
					+ EntityUtils.toString(httpResp.getEntity()) + "}");
			log.info(response.toString(3));
		} catch (Exception e) {
			log.error("Get data feed failed: " + e);
		} finally {
			if (null != getMethod)
				getMethod.releaseConnection();
		}
	}

	public static boolean updateSensor(PresenceSensorPojo sensorPojo) {
		List<PresenceSensorPojo> sensorPojos = new ArrayList<PresenceSensorPojo>();
		sensorPojos.add(sensorPojo);
		return updateSensors(sensorPojos);
	}

	public static boolean updateSensors(List<PresenceSensorPojo> sensorPojos) {
		String targetURL = presenceUrl + "/data_feeds?auth_token="
				+ partnerCredential;
		HttpPut putMethod = new HttpPut(targetURL);
		try {
			long start = System.currentTimeMillis();
			JSONArray sensors = new JSONArray();
			for (PresenceSensorPojo sensorPojo : sensorPojos) {
				String customerId = sensorPojo.getCustomerName();
				String store_name = sensorPojo.getStoreName();
				String sensor_name = sensorPojo.getName();
				String sensor_host = sensorPojo.getHostname();
				String sensor_mac = sensorPojo.getMac();
				boolean active = sensorPojo.isActive();
				log.info("Create/update sensor for: " + customerId
						+ " in store " + store_name + ": " + sensor_name + ", "
						+ sensor_mac);
				JSONObject sensor = new JSONObject();
				sensor.put("client_name", customerId);
				sensor.put("store_name", store_name);
				sensor.put("name", sensor_name);
				sensor.put("description", sensor_host);
				sensor.put("mac_address", sensor_mac);
				sensor.put("active", active);
				sensors.put(sensor);
			}
			List<NameValuePair> entities = new ArrayList<NameValuePair>();
			NameValuePair nv = new BasicNameValuePair("data_feeds",
					sensors.toString());
			entities.add(nv);
			putMethod.setEntity(new UrlEncodedFormEntity(entities));
			HttpResponse httpResp = client.execute(putMethod);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			long end = System.currentTimeMillis();
			log.info("statusCode: " + statusCode + ", cost: " + (end - start)
					+ "ms.");
			JSONObject response = new JSONObject(EntityUtils.toString(httpResp
					.getEntity()));
			log.info(response.toString(3));
			for (PresenceSensorPojo sensorPojo : sensorPojos) {
				JSONObject sensor = (JSONObject) response.get(sensorPojo
						.getName());
				if (sensor == null) {
					log.info("Sensor update failed.");
					return false;
				} else {
					log.info(sensor.toString(3));
					String status = sensor.getString("status");
					if (!"ok".equals(status)) {
						log.info("Sensor update failed: " + status);
						return false;
					}
				}
			}
			return true;
		} catch (Exception e) {
			log.error("Sensor update failed: " + e);
			return false;
		} finally {
			putMethod.releaseConnection();
		}
	}

	private static void getAllUsers(String active) {
		String targetURL = presenceUrl + "/users?auth_token="
				+ partnerCredential + "&active=" + active;
		log.info("Target URL: " + targetURL);
		HttpGet getMethod = new HttpGet(targetURL);
		try {
			long start = System.currentTimeMillis();
			log.info("Get all users.");
			HttpResponse httpResp = client.execute(getMethod);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			long end = System.currentTimeMillis();
			log.info("statusCode: " + statusCode + ", cost: " + (end - start)
					+ "ms.");
			JSONObject response = new JSONObject("{ users: "
					+ EntityUtils.toString(httpResp.getEntity()) + "}");
			log.info(response.toString(3));
		} catch (Exception e) {
			log.error("Get all users failed: " + e);
		} finally {
			getMethod.releaseConnection();
		}
	}

	public static String getUser(String email) {
		email = StringUtils.lowerCase(email);
		HttpGet getMethod = null;
		try {
			long start = System.currentTimeMillis();
			String targetURL = presenceUrl + "/user?auth_token="
					+ partnerCredential + "&email="
					+ URLEncoder.encode(email, "UTF-8");
			log.info("Target URL: " + targetURL);
			getMethod = new HttpGet(targetURL);
			log.info("Get user: " + email);
			HttpResponse httpResp = client.execute(getMethod);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			long end = System.currentTimeMillis();
			log.info("statusCode: " + statusCode + ", cost: " + (end - start)
					+ "ms.");
			JSONObject response = new JSONObject("{ users: "
					+ EntityUtils.toString(httpResp.getEntity()) + "}");
			log.info(response.toString(3));
			JSONArray users = response.getJSONArray("users");
			if (users != null && users.length() > 0) {
				JSONObject user = users.getJSONObject(0);
				if (user != null) {
					return (String) user.get("authentication_token");
				}
			}
			return "";
		} catch (Exception e) {
			log.error("Get user failed: " + e);
			return null;
		} finally {
			if (null != getMethod)
				getMethod.releaseConnection();
		}
	}

	public static boolean updateUser(String customerId, String email,
			boolean active) {
		String targetURL = presenceUrl + "/users?auth_token="
				+ partnerCredential;
		email = StringUtils.lowerCase(email);
		HttpPut putMethod = new HttpPut(targetURL);
		log.info("Create/update user for: " + customerId + ": " + email);
		try {
			long start = System.currentTimeMillis();
			JSONObject user = new JSONObject();
			user.put("client_name", customerId);
			user.put("email", email);
			user.put("active", active);
			Collection<JSONObject> stores = new Vector<JSONObject>();
			stores.add(user);
			List<NameValuePair> entities = new ArrayList<NameValuePair>();
			NameValuePair nv = new BasicNameValuePair("users", new JSONArray(
					stores).toString());
			entities.add(nv);
			putMethod.setEntity(new UrlEncodedFormEntity(entities));
			HttpResponse httpResp = client.execute(putMethod);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			long end = System.currentTimeMillis();
			log.info("statusCode: " + statusCode + ", cost: " + (end - start)
					+ "ms.");
			JSONObject response = new JSONObject(EntityUtils.toString(httpResp
					.getEntity()));
			log.info(response.toString(3));
			user = (JSONObject) response.get(email);
			if (user == null) {
				log.info("User update failed.");
				return false;
			} else {
				log.info(user.toString(3));
				String status = user.getString("status");
				if (!"ok".equals(status)) {
					log.info("User update failed: " + status);
				}
				return "ok".equals(status);
			}
		} catch (Exception e) {
			log.error("User update failed: " + e);
			return false;
		} finally {
			putMethod.releaseConnection();
		}
	}

	public static void main(String[] args) {
		String vhmId = "VHM-3ZK71M3";
		String credential = registerCustomer(new PresenceCustomerPojo(vhmId,
				"Buy More", true));
		log.info("Credential for customer: " + vhmId + " is: " + credential);
		getAllCustomers("true");
		getCustomer(vhmId);
		String store_name1 = "Buy More Santa Clara - Appliances";
		String store_name2 = "Buy More Santa Clara - Electronics";
		String store_name3 = "Office";
		String store_name4 = "Buy More Sunnyvale - Electronics";
		updateStore(new PresenceStorePojo(vhmId, store_name1, "SC001", "95054",
				true));
		updateStore(new PresenceStorePojo(vhmId, store_name2, "SC002", "95054",
				true));
		updateStore(new PresenceStorePojo(vhmId, store_name3, "SC003", "95054",
				false));
		updateStore(new PresenceStorePojo(vhmId, store_name4,
				"America/Los_Angeles", "addr3", "city3", "state3", true));
		getStoresForCustomer(vhmId, "both");
		getStore(vhmId, store_name4);
		updateSensor(new PresenceSensorPojo(vhmId, store_name2, "AP4", "H_AP4",
				"0018330C9AB0", true));
		updateSensor(new PresenceSensorPojo(vhmId, store_name2, "AP5", "H_AP5",
				"0018330B7F20", false));
		updateSensor(new PresenceSensorPojo(vhmId, store_name2, "AP3", "H_AP3",
				"0018330A3C60", true));
		getSensorsForStore(vhmId, store_name2, "both");
		getSensor(vhmId, store_name2, "AP5");
		getAllUsers("both");
		String email = "admin@aerohive.com";
		updateUser(vhmId, email, true);
		String auth_token = getUser(email);
		log.info("Auth token for user: " + email + " is: " + auth_token);
	}

	public static class PresenceCustomerPojo {
		private String name;
		private String description;
		private boolean active;

		public PresenceCustomerPojo(String name, String description,
				boolean active) {
			super();
			this.name = name;
			this.description = description;
			this.active = active;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

	}

	public static class PresenceStorePojo {
		private String customerName;
		private String name;
		private String number;
		private String zipCode;
		private String address;
		private String city;
		private String state;
		private String timezone;
		private boolean active;

		public PresenceStorePojo(String customerName, String name,
				boolean active) {
			super();
			this.customerName = customerName;
			this.name = name;
			this.active = active;
		}

		public PresenceStorePojo(String name, boolean active) {
			super();
			this.name = name;
			this.active = active;
		}

		public PresenceStorePojo(String customerName, String name,
				String number, String zipCode, boolean active) {
			super();
			this.customerName = customerName;
			this.name = name;
			this.number = number;
			this.zipCode = zipCode;
			this.active = active;
		}

		public PresenceStorePojo(String customerName, String name,
				String timezone, String address, String city, String state,
				boolean active) {
			super();
			this.customerName = customerName;
			this.name = name;
			this.timezone = timezone;
			this.address = address;
			this.city = city;
			this.state = state;
			this.active = active;
		}

		public String getCustomerName() {
			return customerName;
		}

		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getZipCode() {
			return zipCode;
		}

		public void setZipCode(String zipCode) {
			this.zipCode = zipCode;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getTimezone() {
			return timezone;
		}

		public void setTimezone(String timezone) {
			this.timezone = timezone;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

	}

	public static class PresenceSensorPojo {
		private String customerName;
		private String storeName;
		private String name;
		private String hostname;
		private String mac;
		private boolean active;

		public PresenceSensorPojo(String customerName, String storeName,
				String name, String hostname, String mac, boolean active) {
			super();
			this.customerName = customerName;
			this.storeName = storeName;
			this.name = name;
			this.hostname = hostname;
			this.mac = mac;
			this.active = active;
		}

		public PresenceSensorPojo(String storeName, String hostname, String mac) {
			super();
			this.storeName = storeName;
			this.hostname = hostname;
			this.mac = mac;
		}

		public String getCustomerName() {
			return customerName;
		}

		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}

		public String getStoreName() {
			return storeName;
		}

		public void setStoreName(String storeName) {
			this.storeName = storeName;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getHostname() {
			return hostname;
		}

		public void setHostname(String hostname) {
			this.hostname = hostname;
		}

		public String getMac() {
			return mac;
		}

		public void setMac(String mac) {
			this.mac = mac;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

	}
}
