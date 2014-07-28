package com.ah.be.performance;

import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.util.EntityUtils;

import com.ah.be.common.PresenceUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.event.BePresenceResultEvent;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.rest.client.RestAhPreSenceService;
import com.ah.be.rest.util.FormatedJsonHierarchicalStreamDriver;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.performance.AhPresence;
import com.ah.bo.performance.AhPresenceDeviceInfo;
import com.ah.bo.performance.AhPresenceSensorData;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.thoughtworks.xstream.XStream;

public class BePresenceProcessor {
	private static final Tracer log = new Tracer(BePresenceProcessor.class);

	private final BlockingQueue<BeBaseEvent> presenceQueue;
	private final BlockingQueue<AhPresenceSensorData> sensorDataQueue;
	private static final int eventQueueSize = 10000;
	private static final int concurrentThreadNum = PresenceUtil.PRESENCE_POST_CONCURRENT_COUNT;
	private final Thread[] postMan;
	private HttpClient postClient;
	private HttpClient detectClient;
	private boolean isContinue = false;
	private static Map<String, AhPresenceSensorData> minuteSensorDataMap = new ConcurrentHashMap<String, AhPresenceSensorData>();
	private static Map<String, AhPresenceSensorData> hourSensorDataMap = new ConcurrentHashMap<String, AhPresenceSensorData>();
	private static Map<String, AhPresenceSensorData> daySensorDataMap = new ConcurrentHashMap<String, AhPresenceSensorData>();
	private final ScheduledExecutorService scheduler;
	private ScheduledFuture<?> scheduledTask;

	public BePresenceProcessor() {
		postMan = new Thread[concurrentThreadNum];
		postClient = PresenceUtil.getHttpClientInstance(concurrentThreadNum);
		detectClient = PresenceUtil.getHttpClientInstance(1);
		presenceQueue = new LinkedBlockingQueue<BeBaseEvent>(eventQueueSize);
		sensorDataQueue = new LinkedBlockingQueue<AhPresenceSensorData>(
				eventQueueSize);
		scheduler = Executors.newSingleThreadScheduledExecutor();

		isContinue = true;
	}

	public void startTask() {
		log.info("<BE Thread> Presence Processor is running, post concurrent thread number: "
				+ concurrentThreadNum);
		for (int i = 0; i < concurrentThreadNum; i++) {
			PresenceDataProcessThread dataProcessThread = new PresenceDataProcessThread();
			dataProcessThread.setName("presentDataProcessThread - " + i);
			dataProcessThread.start();
			postMan[i] = dataProcessThread;
		}

		// polling detect euclid server connection status
		scheduledTask = scheduler.scheduleWithFixedDelay(new ScheduledThread(),
				1, 10, TimeUnit.MINUTES);
		scheduledTask = scheduler.scheduleWithFixedDelay(
				new RemovePostDataThread(), 0, 1, TimeUnit.SECONDS);
		DealPostDataThread dealPostDataThread = new DealPostDataThread();
		dealPostDataThread.setName("dealPostDataThread");
		dealPostDataThread.start();
	}

	// start the ScheduledThread immediately if Update Presence Settings by
	// admin
	public void startScheduledThread() {
		ScheduledThread scheduledThread = new ScheduledThread();
		scheduledThread.start();
	}

	public void addEvent(BePresenceResultEvent event) {
		try {
			boolean result = presenceQueue.offer(event);
			if (false == result) {
				presenceQueue.take();
				presenceQueue.offer(event);
				log.warn("BePresenceProcessor.presenceQueue is full, old data has be washed out");
			}
		} catch (Exception e) {
			log.error(
					"BePresenceProcessor.addEvent():Exception while add event to queue",
					e);
		}
	}

	public boolean shutdown(boolean isOff) {
		if (isOff) {
			isContinue = false;

			presenceQueue.clear();
			int threadCount = postMan.length;
			for (int i = 0; i < threadCount; i++) {
				BeBaseEvent stopThreadEvent = new AhShutdownEvent();
				presenceQueue.offer(stopThreadEvent);
			}
			sensorDataQueue.clear();
			AhPresenceSensorData sensorData = new AhPresenceSensorData();
			sensorDataQueue.offer(sensorData);

			if (null != postClient) {
				postClient.getConnectionManager().shutdown();
			}
			if (null != detectClient) {
				detectClient.getConnectionManager().shutdown();
			}
			try {
				shutdownScheduler();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log.info("<BE Thread> Presence data Processor is shutdown");
		}
		return true;
	}

	public class PresenceDataProcessThread extends Thread {
		public void run() {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			log.info("<BE Thread> Presence Data Process Thread is running...");
			XStream xStream = new XStream(
					new FormatedJsonHierarchicalStreamDriver());
			xStream.autodetectAnnotations(true);
			while (isContinue) {
				try {
					BeBaseEvent event = presenceQueue.take();
					if (event.getEventType() == BeEventConst.AH_SHUTDOWN_EVENT) {
						log.info("Application is shutdown, close presenDataProcess thread: "
								+ Thread.currentThread().getName());
						break;
					}
					if (log.getLogger().isDebugEnabled()) {
						log.debug("Current Presence Data Queue size: "
								+ presenceQueue.size() + ", maximum: "
								+ eventQueueSize);
					}
					AhPresence presence = ((BePresenceResultEvent) event)
							.getAhPresence();
					String macAddress = presence.getMacAddress();
					SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance()
							.getSimpleHiveAp(macAddress);
					boolean statusFlag = false;
					Long domainId = null;
					if (simpleHiveAp != null) {
						statusFlag = simpleHiveAp.getManageStatus() == HiveAp.STATUS_MANAGED ? true
								: false;
						domainId = simpleHiveAp.getDomainId();
					}
					if (null == domainId) {
						log.error("Cannot find domain id for sensor data from "
								+ macAddress + ", ignore the data.");
						continue;
					}
					boolean isEnabled = PresenceUtil
							.isPresenceEnabled(domainId);
					if (!isEnabled) {
						log.info("Presence setting not enabled for domain: "
								+ domainId + ", do not post sensor data from: "
								+ macAddress + " to Euclid server.");
					} else {
						if (presence != null
								&& presence.getDeviceInfoList() != null
								&& presence.getDeviceInfoList().size() != 0
								&& statusFlag) {
							// notify sensor data listeners
							notifySensorDataListeners(presence);

							String jsonString = xStream.toXML(presence);
							boolean postStatus = RestAhPreSenceService
									.postPreSenceData(postClient, presence,
											domainId, jsonString);
							if (postStatus) {
								AhPresenceSensorData sensorData = new AhPresenceSensorData();
								if (presence.getTriggerType() == AhPresence.NEW_CLIENT) {
									sensorData.setClientMacs(presence
											.getDeviceInfoList().size());
								}
								sensorData.setObjects(1);
								sensorData.setBandWidth(jsonString.length());
								sensorData.setMacAddress(macAddress);
								boolean result = sensorDataQueue
										.offer(sensorData);
								if (false == result) {
									sensorDataQueue.take();
									sensorDataQueue.offer(sensorData);
								}
							}
						}
					}
				} catch (Exception e) {
					log.error(
							"BePresenceProcessor.PresenceDataProcessThread.run() Exception in processor thread",
							e);
				} catch (Error e) {
					log.error(
							"BePresenceProcessor.PresenceDataProcessThread.run() Error in processor thread",
							e);
				}
			}
		}
	}

	private int minuteParamValue;
	private int hourParamValue;

	private class RemovePostDataThread extends Thread {
		public void run() {
			try {
				// remove last minute Data
				Calendar cal = Calendar.getInstance();
				int minuteValue = cal.get(Calendar.MINUTE);
				int hourValue = cal.get(Calendar.HOUR_OF_DAY);
				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.MINUTE, minuteValue - 1);
				for (Object obj : minuteSensorDataMap.keySet().toArray()) {
					AhPresenceSensorData sensorData = minuteSensorDataMap
							.get(obj);
					if (sensorData.getTimeStamp() < cal.getTime().getTime()) {
						minuteSensorDataMap.remove(obj);
					}
				}
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, minuteValue);
				cal.set(Calendar.HOUR_OF_DAY, hourValue - 1);
				if (minuteParamValue != minuteValue) {
					// remove last hour data
					for (Object obj : hourSensorDataMap.keySet().toArray()) {
						AhPresenceSensorData sensorData = hourSensorDataMap
								.get(obj);
						if (sensorData.getTimeStamp() < cal.getTime().getTime()) {
							hourSensorDataMap.remove(obj);
						}
					}
					minuteParamValue = minuteValue;
				}
				// remove last day data
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, hourValue);
				cal.set(Calendar.DAY_OF_MONTH,
						cal.get(Calendar.DAY_OF_MONTH) - 1);
				if (hourParamValue != hourValue) {
					for (Object obj : daySensorDataMap.keySet().toArray()) {
						AhPresenceSensorData sensorData = daySensorDataMap
								.get(obj);
						if (sensorData.getTimeStamp() < cal.getTime().getTime()) {
							daySensorDataMap.remove(obj);
						}
					}
					hourParamValue = hourValue;
				}
			} catch (Exception e) {
				log.error(
						"BePresenceProcessor.RemovePostDataThread.run() Exception in processor thread",
						e);
			}
		}
	}

	private class DealPostDataThread extends Thread {
		public void run() {
			while (isContinue) {
				try {
					// deal with last minute data
					AhPresenceSensorData sensorData = sensorDataQueue.take();
					String macAddress = sensorData.getMacAddress();
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.MILLISECOND, 0);
					long minuteTimeStamp = cal.getTime().getTime();
					String minutekey = macAddress + ":" + minuteTimeStamp;
					cal.set(Calendar.SECOND, 0);
					long hourTimeStamp = cal.getTime().getTime();
					String hourkey = macAddress + ":" + hourTimeStamp;
					cal.set(Calendar.MINUTE, 0);
					long dayTimeStamp = cal.getTime().getTime();
					String daykey = macAddress + ":" + dayTimeStamp;

					if (!minuteSensorDataMap.containsKey(minutekey)) {
						minuteSensorDataMap.put(minutekey,
								new AhPresenceSensorData(macAddress,
										minuteTimeStamp));
					}
					AhPresenceSensorData minuteObj = minuteSensorDataMap
							.get(minutekey);
					mergeSensorData(minuteObj, sensorData);
					// deal with last hour data
					if (!hourSensorDataMap.containsKey(hourkey)) {
						hourSensorDataMap.put(hourkey,
								new AhPresenceSensorData(macAddress,
										hourTimeStamp));
					}
					AhPresenceSensorData hourObj = hourSensorDataMap
							.get(hourkey);
					mergeSensorData(hourObj, sensorData);
					// deal with last day data
					if (!daySensorDataMap.containsKey(daykey)) {
						daySensorDataMap.put(daykey, new AhPresenceSensorData(
								macAddress, dayTimeStamp));
					}
					AhPresenceSensorData dayObj = daySensorDataMap.get(daykey);
					mergeSensorData(dayObj, sensorData);
				} catch (InterruptedException e) {
					log.error(
							"BePresenceProcessor.DealPostDataThread.run() Exception in processor thread",
							e);
				}
			}
		}
	}

	private void mergeSensorData(AhPresenceSensorData obj,
			AhPresenceSensorData sensorData) {
		obj.setBandWidth(obj.getBandWidth() + sensorData.getBandWidth());
		obj.setObjects(obj.getObjects() + sensorData.getObjects());
		obj.setClientMacs(obj.getClientMacs() + sensorData.getClientMacs());
	}

	private class ScheduledThread extends Thread {
		@Override
		public void run() {
			if (PresenceUtil.isPresenceSettingEnabled()) {
				HttpHead headMethod = new HttpHead(
						PresenceUtil.PRESENCE_DATA_POST_URL);
				HttpResponse httpResp;
				try {
					httpResp = detectClient.execute(headMethod);
					int statusCode = httpResp.getStatusLine().getStatusCode();
					if (statusCode == 200) {
						PresenceUtil.setConnectEuclidStatus(true);
					} else if (statusCode == 500) {
						PresenceUtil.setConnectEuclidStatus(false);
					}
					EntityUtils.consumeQuietly(httpResp.getEntity());
				} catch (SocketTimeoutException e) {
					PresenceUtil.setConnectEuclidStatus(false);
				} catch (Exception e) {
					log.error(
							"BePresenceProcessor.ScheduledThread.run() Exception in processor thread",
							e);
				} finally {
					PresenceUtil.updateEuclidAlarm();
					if (null != headMethod) {
						headMethod.releaseConnection();
					}
				}
			}
		};
	};

	private ConcurrentMap<String, Map<String, SensorTrackingClient[]>> sensorListeners = new ConcurrentHashMap<String, Map<String, SensorTrackingClient[]>>();

	/**
	 * add tracking sensor into the list for a specified session.
	 */
	public void addSensorDataListener(String sessionId, String macAddress) {
		log.info(String
				.format("new tracking sensor add into list, with sessionId %s, sensor MAC address %s, current tracking list size %s.",
						sessionId, macAddress, sensorListeners.size()));
		Map<String, SensorTrackingClient[]> sensors = new HashMap<String, SensorTrackingClient[]>();
		SensorTrackingClient[] clients = new SensorTrackingClient[100];
		sensors.put(macAddress, clients);
		sensorListeners.put(sessionId, sensors);
	}

	/**
	 * remove tracking sensor from the list for a specified session.
	 * 
	 * @param sessionId
	 */
	public void removeSensorDataListener(String sessionId) {
		sensorListeners.remove(sessionId);
	}

	public SensorTrackingClient[] getSensorData(String sessionId,
			String macAddress) {
		Map<String, SensorTrackingClient[]> sensors = sensorListeners
				.get(sessionId);
		if (null != sensors) {
			synchronized (sensors) {
				SensorTrackingClient[] clients = sensors.get(macAddress);
				if (null != clients) {
					Arrays.sort(clients, firstSeenComparator);
					return clients;
				}
			}
		}
		return null;
	}

	public void notifySensorDataListeners(AhPresence presence) {
		if (sensorListeners.isEmpty()) {
			return;
		}
		long current = System.currentTimeMillis();
		String macAddress = presence.getMacAddress();
		byte clientType = presence.getTriggerType();
		List<AhPresenceDeviceInfo> deviceInfoList = presence
				.getDeviceInfoList();
		if ((clientType != AhPresence.NEW_CLIENT && clientType != AhPresence.AGING_CLIENT)
				|| null == deviceInfoList) {
			return;
		}
		for (Map<String, SensorTrackingClient[]> sensors : sensorListeners
				.values()) {
			synchronized (sensors) {
				SensorTrackingClient[] clients = sensors.get(macAddress);
				if (null == clients) {
					continue;
				}
				log.info(String
						.format("sensor %s is currently in tracking, add client into list.",
								macAddress));
				for (AhPresenceDeviceInfo deviceInfo : deviceInfoList) {
					String clientMac = deviceInfo.getStationIdUnEncrypt();
					SensorTrackingClient client = new SensorTrackingClient(
							clientMac, current);
					if (clientType == AhPresence.NEW_CLIENT) {
						// check repeated
						Arrays.sort(clients, macAddressComparator);
						int pos = Arrays.binarySearch(clients, client,
								macAddressComparator);
						if (pos >= 0) {
							clients[pos] = client;
							continue;
						}
						// check null
						pos = ArrayUtils.indexOf(clients, null);
						if (pos >= 0) {
							clients[pos] = client;
							continue;
						}
						// replace first seen
						Arrays.sort(clients, firstSeenComparator);
						clients[0] = client;
					} else if (clientType == AhPresence.AGING_CLIENT) {
						// check existed
						Arrays.sort(clients, macAddressComparator);
						int pos = Arrays.binarySearch(clients, client,
								macAddressComparator);
						if (pos >= 0) {
							clients[pos] = null;
						}
					}
				}
			}
		}
	}

	Comparator<SensorTrackingClient> macAddressComparator = new Comparator<SensorTrackingClient>() {
		@Override
		public int compare(SensorTrackingClient o1, SensorTrackingClient o2) {
			if (null == o1 && null == o2) {
				return 0;
			} else if (null == o1) {
				return 1;
			} else if (null == o2) {
				return -1;
			}
			return o1.getMacAddress().compareTo(o2.getMacAddress());
		}
	};

	Comparator<SensorTrackingClient> firstSeenComparator = new Comparator<SensorTrackingClient>() {
		@Override
		public int compare(SensorTrackingClient o1, SensorTrackingClient o2) {
			if (null == o1 && null == o2) {
				return 0;
			} else if (null == o1) {
				return 1;
			} else if (null == o2) {
				return -1;
			}
			return new Long(o1.firstSeen).compareTo(new Long(o2.firstSeen));
		}
	};

	public static class SensorTrackingClient {
		private String macAddress;
		private long firstSeen;

		public SensorTrackingClient(String macAddress, long firstSeen) {
			super();
			this.macAddress = macAddress;
			this.firstSeen = firstSeen;
		}

		public String getMacAddress() {
			return macAddress;
		}

		public void setMacAddress(String macAddress) {
			this.macAddress = macAddress;
		}

		public long getFirstSeen() {
			return firstSeen;
		}

		public void setFirstSeen(long firstSeen) {
			this.firstSeen = firstSeen;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	private void shutdownScheduler() throws InterruptedException {
		if (scheduler == null || scheduler.isShutdown()) {
			return;
		}
		// Cancel scheduled task.
		if (scheduledTask != null) {
			scheduledTask.cancel(false);
		}
		// Disable new tasks from being submitted.
		scheduler.shutdown();
		// Wait a while for existing tasks to terminate.
		if (scheduler.awaitTermination(1L, TimeUnit.SECONDS)) {
			return;
		}
		// Cancel currently executing tasks.
		scheduler.shutdownNow();
	}

	public static Map<String, AhPresenceSensorData> getMinuteSensorDataMap() {
		return minuteSensorDataMap;
	}

	public static Map<String, AhPresenceSensorData> getHourSensorDataMap() {
		return hourSensorDataMap;
	}

	public static Map<String, AhPresenceSensorData> getDaySensorDataMap() {
		return daySensorDataMap;
	}

}