package com.ah.test;

/*
 * @author Chris Scheers
 */

import java.util.ArrayList;
import java.util.List;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.common.NmsUtil;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.test.util.HmTest;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

public class HiveAPsTest extends HmTest {
	private static final Tracer log = new Tracer(HiveAPsTest.class
			.getSimpleName());

	private final String operation;

	private final int nAPs;

	private final BeVersionInfo versionInfo;

	public HiveAPsTest(String operation, int nAPs) {
		super();
		this.operation = operation;
		this.nAPs = nAPs;
		this.versionInfo = NmsUtil.getVersionInfo();
	}

	public void run() {
		String user = "u" + getId();
		int bulkSize = 20;
		try {
			if ("testHiveApsCreate".equals(operation)) {
				for (int i = 1; i <= nAPs; i++) {
					QueryUtil.createBo(createHiveAP(user + "_ap" + i));
				}
			} else if ("testHiveApsUpdate".equals(operation)) {
				int count = 0;
				for (int i = 1; i <= nAPs; i++) {
					if (updateHiveAP(user + "_ap" + i)) {
						count++;
					}
				}
				log.info("run", "# APs found: " + count);
				log.info("run", "Free before: "
						+ Runtime.getRuntime().freeMemory());
				Runtime.getRuntime().gc();
				log.info("run", "Free after: "
						+ Runtime.getRuntime().freeMemory());
			} else if ("testHiveApsRemove".equals(operation)) {
				BoMgmt.removeAllBos(HiveAp.class, null, null, null, null);
			} else if ("testHiveApsBulkCreate".equals(operation)) {
				List<HiveAp> hiveAps = new ArrayList<HiveAp>();
				for (int i = 1; i <= nAPs; i++) {
					hiveAps.add(createHiveAP(user + "_ap" + i));
					if (hiveAps.size() == bulkSize) {
						QueryUtil.bulkCreateBos(hiveAps);
						hiveAps = new ArrayList<HiveAp>();
					}
				}
				if (hiveAps.size() > 0) {
					QueryUtil.bulkCreateBos(hiveAps);
				}
			} else if ("testHiveApsBulkUpdate".equals(operation)) {
				int count = 0;
				List<String> macAddresses = new ArrayList<String>();
				for (int i = 1; i <= nAPs; i++) {
					macAddresses.add("MAC-" + user + "_ap" + i);
					if (macAddresses.size() == bulkSize) {
						List<HiveAp> hiveAps = findHiveAPs(macAddresses);
						count += hiveAps.size();
						updateHiveAPs(hiveAps);
						macAddresses = new ArrayList<String>();
					}
				}
				if (macAddresses.size() > 0) {
					List<HiveAp> hiveAps = findHiveAPs(macAddresses);
					count += hiveAps.size();
					updateHiveAPs(hiveAps);
				}

				log.info("run", "# APs found by bulk query: " + count);
			} else {
				log.info("run", "Unknown test operation: " + operation);
			}
		} catch (Exception e) {
			log.error("run", "Test failed: ", e);
		}
	}

	protected HiveAp createHiveAP(String hiveApName) throws Exception {
		HiveAp hiveAp = new HiveAp();
		hiveAp.setIpAddress(AhDecoder.int2IP((int) Math
				.round(Math.random() * 1000000000)));
		hiveAp.setNetmask("255.255.255.0");
		hiveAp.setHostName(hiveApName);
		hiveAp.setMacAddress("MAC-" + hiveApName);
		// hiveAp.setSerial("S/N " + Math.round(Math.random() * 10000000));
		hiveAp.setWifi0RadioProfile(null);
		hiveAp.setWifi1RadioProfile(null);
		hiveAp.setNativeVlan(6);
		hiveAp.setPassPhrase(NmsUtil.generatePassphrase());
		hiveAp.setSoftVer(NmsUtil.getHiveOSVersion(versionInfo));
		return hiveAp;
	}

	protected boolean updateHiveAP(String hiveApName) throws Exception {
		List<?> bos = BoMgmt.findBos(HiveAp.class, null, new FilterParams(
				"macAddress", "MAC-" + hiveApName), null, null);
		if (bos.size() > 0) {
			HiveAp hiveAp = (HiveAp) bos.get(0);
			updateHiveAP(hiveAp);
			BoMgmt.updateBo(hiveAp, null, null);
			return true;
		} else {
			QueryUtil.createBo(createHiveAP(hiveApName));
			return false;
		}
	}

	protected boolean updateHiveAPByAttribute(String hiveApName)
			throws Exception {
		HiveAp hiveAp = (HiveAp) BoMgmt.findBoByAttribute(HiveAp.class,
				"macAddress", "MAC-" + hiveApName, null, null);
		if (hiveAp != null) {
			updateHiveAP(hiveAp);
			QueryUtil.updateBo(hiveAp);
			return true;
		} else {
			QueryUtil.createBo(createHiveAP(hiveApName));
			return false;
		}
	}

	protected void updateHiveAP(HiveAp hiveAp) {
		long discoveryTime = hiveAp.getDiscoveryTime();
		if (discoveryTime != 0) {
			hiveAp.setDiscoveryTime(System.currentTimeMillis());
		}
	}

	protected List<HiveAp> findHiveAPs(List<String> macAddresses)
			throws Exception {
		List<?> bos = QueryUtil.executeQuery(HiveAp.class, null,
				new FilterParams("macAddress", macAddresses));
		List<HiveAp> hiveAps = new ArrayList<HiveAp>(bos.size());

		for (Object obj : bos) {
			hiveAps.add((HiveAp) obj);
		}

		return hiveAps;
	}

	protected void updateHiveAPs(List<HiveAp> hiveAps) throws Exception {
		for (HiveAp hiveAp : hiveAps) {
			updateHiveAP(hiveAp);
		}
		QueryUtil.bulkUpdateBos(hiveAps);
	}

}