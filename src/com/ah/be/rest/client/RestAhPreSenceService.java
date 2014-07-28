package com.ah.be.rest.client;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;

import com.ah.be.common.PresenceUtil;
import com.ah.bo.performance.AhPresence;
import com.ah.util.HmException;
import com.ah.util.Tracer;

public class RestAhPreSenceService {

	private static final Tracer log = new Tracer(
			RestAhPreSenceService.class.getSimpleName());
	private static AtomicInteger postSumData = new AtomicInteger();
	// The last time of post data.
	private static Map<String, Long> timeMap = new ConcurrentHashMap<String, Long>();

	public static boolean postPreSenceData(HttpClient client,
			AhPresence ahPresence, Long domainId, String jsonString) {
		boolean postStatus = false;
		String targetURL = PresenceUtil.PRESENCE_DATA_POST_URL + "?sid=" + ahPresence.getSensorId();
		String userCredential = null;
		HttpPost postMethod = null;
		try {
			int typeIndex = jsonString.indexOf(":");
			if (typeIndex == -1) {
				return false;
			}
			userCredential = getCustomerCredential(domainId);
			if (StringUtils.isEmpty(userCredential)) {
				log.error("no credential for domain which id is " + domainId);
				return false;
			}
			long start = System.currentTimeMillis();
			postMethod = new HttpPost(targetURL);
			jsonString = jsonString.substring(typeIndex + 1,
					jsonString.length() - 1);
			List<NameValuePair> entities = new ArrayList<NameValuePair>();
			NameValuePair nv = new BasicNameValuePair("cd", userCredential);
			entities.add(nv);
			nv = new BasicNameValuePair("bd", jsonString);
			entities.add(nv);
			postMethod.setEntity(new UrlEncodedFormEntity(entities));
			HttpResponse httpResp = client.execute(postMethod);
			long end = System.currentTimeMillis();
			int statusCode = httpResp.getStatusLine().getStatusCode();
			String result = EntityUtils.toString(httpResp.getEntity());
			if (statusCode == 200) {
				postStatus = true;
				PresenceUtil.setConnectEuclidStatus(true);
				timeMap.put(ahPresence.getMacAddress(), Calendar.getInstance()
						.getTime().getTime());
				printPostDataLog();
				log.info(String
						.format("post presence data successfully. Sensor ID %s, MAC: %s, status code %s, cost %s ms.",
								ahPresence.getSensorId(),
								ahPresence.getMacAddress(), statusCode,
								(end - start)));
			} else {
				log.error(String
						.format("post presence data failed, credential: %s, statusCode: %s, url: %s, post data: %s, result %s",
								userCredential, statusCode, targetURL,
								jsonString, result));
			}
			if (statusCode == 500) {
				PresenceUtil.setConnectEuclidStatus(false);
			}
			return postStatus;
		} catch (SocketTimeoutException e) {
			log.error("post presence data failed, exception type: + "
					+ e.getClass().getSimpleName() + "exception: "
					+ e.getMessage());
			PresenceUtil.setConnectEuclidStatus(false);
			return false;
		} catch (Exception ex) {
			log.error("post presence data failed, exception type: + "
					+ ex.getClass().getSimpleName() + "exception: "
					+ ex.getMessage());
			return false;
		} finally {
			if (log.getLogger().isDebugEnabled()) {
				log.debug(String.format("sensor post url: %s, credential: %s",
						targetURL, userCredential));
				log.debug("sensor data details: " + ahPresence);
				log.debug("sensor json data details: " + jsonString);
			}
			PresenceUtil.updateEuclidAlarm();
			if (null != postMethod) {
				postMethod.releaseConnection();
			}
		}
	}

	private static void printPostDataLog() {
		// print the log of summary JSON objects were posted onto the Euclid
		// server
		if ((postSumData.intValue() <= 1000 && postSumData.intValue() % 10 == 0)
				|| (postSumData.intValue() > 1000 && postSumData.intValue() % 100 == 0)) {
			log.info("The summary of JSON objects were posted onto the Euclid server:"
					+ postSumData);
		}
		postSumData.incrementAndGet();
	}

	private static String getCustomerCredential(Long domainId)
			throws JSONException {
		String vhmOrSystemId = PresenceUtil.getPresenceCustomerId(domainId);
		try {
			return PresenceUtil.getCustomerCredential(vhmOrSystemId);
		} catch (HmException e) {
			log.error("getCustomerCredential for domain: " + domainId
					+ " error.");
			return "";
		}
	}

	public static Map<String, Long> getTimeMap() {
		return timeMap;
	}
	
}
