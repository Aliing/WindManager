package com.ah.test;

/*
 * @author Chris Scheers
 */

import java.util.Collection;
import java.util.List;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.test.util.HmTest;
import com.ah.util.Tracer;

public class TrapsTest extends HmTest {
	private static final Tracer log = new Tracer(
			TrapsTest.class.getSimpleName());

	protected String operation;

	protected int nTraps;

	public TrapsTest(String operation, int nTraps) {
		this.operation = operation;
		this.nTraps = nTraps;
	}

	public void run() {
		String user = "u" + getId();
		try {
			if ("testCreateAlarms".equals(operation)) {
				createAlarms(user);
			} else if ("testRemoveAlarms".equals(operation)) {
				updateAlarms(user);
				// BoMgmt.removeAllBos(AhAlarm.class, null, null, null, null);
				// BoMgmt.removeAllBos(AhClientSessionHistory.class, null, null,
				// null, null);
			} else if ("testCreateEvents".equals(operation)) {
				createEvents(user);
			} else if ("testRemoveEvents".equals(operation)) {
				BoMgmt.removeAllBos(AhEvent.class, null, null, null, null);
			}
		} catch (Exception e) {
			log.error("run", "Test failed: ", e);
			return;
		}
	}

	protected void updateAlarms(String user) throws Exception {
		String query = "select id from "
				+ AhClientSessionHistory.class.getSimpleName() + " bo";
		List<Long> boIds = (List<Long>) QueryUtil.executeQuery(query,
				new SortParams("id"), null, BoMgmt.getDomainMgmt()
						.getHomeDomain().getId(), 1);
		if (boIds.isEmpty()) {
			return;
		}
		long firstId = boIds.get(0);
		log.info("First ID: " + firstId);
		for (int i = 0; i < nTraps; i++) {
			// AhClientSessionHistory session = (AhClientSessionHistory)
			// QueryUtil
			// .findBoById(AhClientSessionHistory.class, firstId + i);
			// if (session == null) {
			// No more
			// break;
			// } else {
			// count++;
			// }
			QueryUtil
					.executeNativeUpdate("update ah_clientsession_history set mapid = "
							+ (nTraps + i) + " where id = " + (firstId + i));
		}
		log.error("createAlarms", "Finished updating " + nTraps
				+ " client session history objects.");
	}

	protected void createAlarms(String user) throws Exception {
		int bulkCount = 100;
		Collection<AhClientSessionHistory> hmBos = new java.util.Vector<AhClientSessionHistory>();
		StringBuffer hmInserts = new StringBuffer(
				"insert into ah_clientsession_history values ");
		int insertCount = 0;
		for (int i = 0; i < nTraps; i++) {
			AhClientSessionHistory session = new AhClientSessionHistory();
			session.setApMac("0019770015A0");
			session.setApName("AH-0015a0-" + user + "-" + i);
			session.setApSerialNumber("8a84K99DDD88");
			session.setClientMac("449AB2200944");
			session.setClientBSSID("802.1x");
			session.setClientHostname("some smartphone.");
			session.setClientSSID("HQ");
			session.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
			// QueryUtil.createBo(session);
			// hmBos.add(session);
			// if (hmBos.size() >= bulkCount) {
			// QueryUtil.bulkCreateBos(hmBos);
			// hmBos = new java.util.Vector<AhClientSessionHistory>();
			// }
			String insert = "(default, '"
					+ session.getApMac()
					+ "', '"
					+ session.getApName()
					+ "', '"
					+ session.getApSerialNumber()
					+ "', 5, 6, '"
					+ session.getClientBSSID()
					+ "', 8, 9, 10, '"
					+ session.getClientHostname()
					+ "', '10.10.128.4', 13, '"
					+ session.getClientMac()
					+ "', 'iOS', '"
					+ session.getClientSSID()
					+ "', 17, 'jjkk', 19, 'c20', 'c21', 'Aerohive', '23@aerohive.com', 24, 'tz25', 26, 'mgt27', 28, 'memo29', false, 31, 'tz32', "
					+ session.getOwner().getId() + ")";
			if (insertCount == 0) {
				hmInserts.append(insert);
			} else {
				hmInserts.append("," + insert);
			}
			if (++insertCount >= bulkCount) {
				QueryUtil.executeNativeUpdate(hmInserts.toString());
				insertCount = 0;
				hmInserts = new StringBuffer(
						"insert into ah_clientsession_history values ");
			}
			// QueryUtil.executeNativeUpdate("insert into ah_clientsession_history values "
			// + insert);
		}
		// if (hmBos.size() > 0) {
		// QueryUtil.bulkCreateBos(hmBos);
		// }
		if (insertCount > 0) {
			QueryUtil.executeNativeUpdate(hmInserts.toString());
		}
		log.error("createAlarms", "Finished creating " + nTraps
				+ " native client session history objects in bulk of "
				+ bulkCount);
	}

	protected void createAlarms_old(String user) throws Exception {
		for (int i = 0; i < nTraps; i += 4) {
			AhAlarm alarm = new AhAlarm();
			alarm.setApId("001977004024");
			alarm.setApName("host_" + user);
			alarm.setObjectName("Configuration");
			alarm.setSeverity(AhAlarm.AH_SEVERITY_CRITICAL);
			alarm.setAlarmSubType(AhAlarm.AH_PROBABLE_CAUSE_FAN_FAILURE);
			alarm.setCode(0);
			alarm.setTrapDesc("no interface wifi0 load-balance enable.");
			createAlarm(alarm);
			alarm = new AhAlarm();
			alarm.setApId("001977004024");
			alarm.setApName("host_" + user);
			alarm.setObjectName("Configuration");
			alarm.setSeverity(AhAlarm.AH_SEVERITY_MINOR);
			alarm.setAlarmSubType(AhAlarm.AH_PROBABLE_CAUSE_RADIO_FAILURE);
			alarm.setCode(0);
			alarm.setTrapDesc("Capwap linked up..");
			createAlarm(alarm);
			alarm = new AhAlarm();
			alarm.setApId("001977004023");
			alarm.setApName("host_" + user);
			alarm.setObjectName("Hardware Radio");
			alarm.setSeverity(AhAlarm.AH_SEVERITY_MAJOR);
			alarm.setAlarmSubType(AhAlarm.AH_PROBABLE_CAUSE_FLASH_FAILURE);
			alarm.setCode(0);
			alarm.setTrapDesc("no interface wifi0 load-balance enable.");
			createAlarm(alarm);
			alarm = new AhAlarm();
			alarm.setApId("00" + (1977001680 + i));
			alarm.setApName("host_" + user);
			alarm.setObjectName("Capwap");
			alarm.setSeverity(AhAlarm.AH_SEVERITY_CRITICAL);
			alarm.setAlarmSubType(AhAlarm.AH_PROBABLE_CAUSE_POWER_SUPPLY_FAILURE);
			alarm.setCode(1);
			alarm.setTrapDesc("Capwap linked down.");
			createAlarm(alarm);
			alarm = new AhAlarm();
			alarm.setApId("00" + (1977001680 + i));
			alarm.setApName("host_" + user);
			alarm.setObjectName("Capwap");
			alarm.setSeverity(AhAlarm.AH_SEVERITY_UNDETERMINED);
			alarm.setAlarmSubType(AhAlarm.AH_PROBABLE_CAUSE_CLEAR);
			alarm.setCode(1);
			alarm.setTrapDesc("Capwap linked up.");
			createAlarm(alarm);
			alarm = new AhAlarm();
			alarm.setApId("001977004025");
			alarm.setApName("host_" + user);
			alarm.setObjectName("Capwap");
			alarm.setSeverity(AhAlarm.AH_SEVERITY_MINOR);
			alarm.setAlarmSubType(AhAlarm.AH_PROBABLE_CAUSE_RADIO_FAILURE);
			alarm.setCode(1);
			alarm.setTrapDesc("no interface wifi0 load-balance enable.");
			createAlarm(alarm);
			alarm = new AhAlarm();
			alarm.setApId("001977004021");
			alarm.setApName("host_" + user);
			alarm.setObjectName("Capwap");
			alarm.setSeverity(AhAlarm.AH_SEVERITY_UNDETERMINED);
			alarm.setAlarmSubType(AhAlarm.AH_PROBABLE_CAUSE_SOFTWARE_UPGRADE_FAILURE);
			alarm.setCode(1);
			alarm.setTrapDesc("Software upgrade failed.");
			createAlarm(alarm);
			alarm = new AhAlarm();
			alarm.setApId("001977004227");
			alarm.setApName("host_" + user);
			alarm.setObjectName("Configuration");
			alarm.setSeverity(AhAlarm.AH_SEVERITY_MAJOR);
			alarm.setAlarmSubType(AhAlarm.AH_PROBABLE_CAUSE_FAN_FAILURE);
			alarm.setCode(0);
			alarm.setTrapDesc("no interface wifi0 load-balance enable.");
			createAlarm(alarm);

			long severity = Math.round(Math.random() * 5);
			for (int j = 30; j < 225; j += 2) {
				alarm = new AhAlarm();
				alarm.setApId("00" + (1977004001 + j));
				alarm.setApName("host_" + user + j);
				alarm.setObjectName("Configuration");
				alarm.setSeverity((short) severity);
				if (severity == AhAlarm.AH_SEVERITY_UNDETERMINED) {
					severity++;
				}
				if (severity < AhAlarm.AH_SEVERITY_CRITICAL
						|| severity == AhAlarm.AH_SEVERITY_UNDETERMINED) {
					severity++;
				} else {
					severity = AhAlarm.AH_SEVERITY_UNDETERMINED;
				}
				alarm.setAlarmSubType(AhAlarm.AH_PROBABLE_CAUSE_FAN_FAILURE);
				if (severity == AhAlarm.AH_SEVERITY_CRITICAL) {
					alarm.setCode(0);
					alarm.setTrapDesc("no interface wifi0 load-balance enable.");
				} else if (severity == AhAlarm.AH_SEVERITY_MAJOR) {
					alarm.setCode(1);
					alarm.setTrapDesc("Software upgrade failed.");
				} else if (severity == AhAlarm.AH_SEVERITY_MINOR) {
					alarm.setCode(2);
					alarm.setTrapDesc("Capwap linked up.");
				} else if (severity == AhAlarm.AH_SEVERITY_UNDETERMINED) {
					alarm.setCode(3);
					alarm.setTrapDesc("Capwap linked down.");
				} else {
					alarm.setCode(3);
					alarm.setTrapDesc("no interface wifi0 load-balance enable.");
				}
				createAlarm(alarm);
			}
		}
	}

	protected void createAlarm(AhAlarm alarm) throws Exception {
		List<HmDomain> domains = CacheMgmt.getInstance().getCacheDomains();
		alarm.setOwner(domains.get((int) (Math.random() * domains.size())));
		BoMgmt.getTrapMgmt().createAlarm(alarm);
	}

	protected void createEvents(String user) throws Exception {
		int bulkCount = 100;
		Collection<AhEvent> hmBos = new java.util.Vector<AhEvent>();
		for (int i = 0; i < nTraps; i += 4) {
			AhEvent event = new AhEvent();
			event.setApId("0019770015A0");
			event.setApName("AH-0015a0-" + user);
			event.setObjectName("IDP");
			event.setCode(0);
			event.setTrapDesc("IDP: AP 0019:7700:0587 removed detected.");
			event.setEventType(AhEvent.AH_EVENT_TYPE_THRESHOLD_CROSSING);
			event.setCurValue(825);
			event.setThresholdHigh(600);
			event.setThresholdLow(250);

			event.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
			event.setTrapTimeStamp(new HmTimeStamp(System.currentTimeMillis(),
					event.getOwner().getTimeZoneString()));
			hmBos.add(event);
			// QueryUtil.createBo(event);
			// BoMgmt.getTrapMgmt().createEvent(event);
			event = new AhEvent();
			event.setApId("00" + (1977001680 + i));
			event.setApName("AH-1920-Prius-" + user);
			event.setObjectName("AUTH");
			event.setCode(1);
			event.setTrapDesc("Station 001b:7752:e4f1 is de-authenticated from 0019:7700:1929 thru interface wifi1.2.");
			event.setEventType(AhEvent.AH_EVENT_TYPE_STATE_CHANGE);
			event.setCurrentState(AhEvent.AH_STATE_UP);
			event.setPreviousState(AhEvent.AH_STATE_DOWN);

			event.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
			event.setTrapTimeStamp(new HmTimeStamp(System.currentTimeMillis(),
					event.getOwner().getTimeZoneString()));
			hmBos.add(event);
			// QueryUtil.createBo(event);
			// BoMgmt.getTrapMgmt().createEvent(event);
			event = new AhEvent();
			event.setApId("00" + (1977001680 + i));
			event.setApName("AH-1920-Prius-" + user);
			event.setObjectName("AUTH");
			event.setCode(1);
			event.setTrapDesc("Station 001b:7752:e4f1 is authenticated to 0019:7700:1925 thru interface wifi0.2.");
			event.setEventType(AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE);
			event.setCurrentState(AhEvent.AH_STATE_UP);
			event.setIfIndex(4);
			event.setObjectType(AhEvent.AH_OBJECT_TYPE_NEIGHBORLINK);

			event.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
			event.setTrapTimeStamp(new HmTimeStamp(System.currentTimeMillis(),
					event.getOwner().getTimeZoneString()));
			hmBos.add(event);
			// QueryUtil.createBo(event);
			// BoMgmt.getTrapMgmt().createEvent(event);
			event = new AhEvent();
			event.setApId("00" + (1977001680 + i));
			event.setApName("AH-0015a0-" + user);
			event.setObjectName("IDP");
			event.setCode(1);
			event.setTrapDesc("IDP: AP 000b:86a1:f430 detected.");

			event.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
			event.setTrapTimeStamp(new HmTimeStamp(System.currentTimeMillis(),
					event.getOwner().getTimeZoneString()));
			hmBos.add(event);
			// QueryUtil.createBo(event);
			// BoMgmt.getTrapMgmt().createEvent(event);
			if (hmBos.size() > bulkCount) {
				QueryUtil.bulkCreateBos(hmBos);
				hmBos = new java.util.Vector<AhEvent>();
			}
		}
		if (hmBos.size() > 0) {
			QueryUtil.bulkCreateBos(hmBos);
		}
	}
}
