package com.ah.be.db.discovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.capwap.AhCapwapConstants;
import com.ah.be.capwap.AhCapwapFsm;
import com.ah.be.common.NmsUtil;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApEth;
import com.ah.bo.hiveap.HiveApWifi;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;

public class AhCapwapFsmProcessor implements AhCapwapConstants {

	private static final long serialVersionUID = 1L;
	private static final Tracer logger = new Tracer(AhCapwapFsmProcessor.class
			.getSimpleName());

	private static final int FLUSH_INTERVAL = 1000;

	private final int flushCount;
	private int flushTimer;
	private final Map<String, AhCapwapFsm> localReqsHolder;

	public AhCapwapFsmProcessor(int flushCount) {
		this.flushCount = flushCount;
		localReqsHolder = new HashMap<String, AhCapwapFsm>(flushCount);
		initTimer();
	}

	private void initTimer() {
		flushTimer = FLUSH_INTERVAL;
	}

	public void decreaseFlustTimer() throws Exception {
		flushTimer -= 1000;

		if (flushTimer <= 0) {
			flush();// Flush Map<String, AhCapcapFsm> actively if there is not
			// any update request in 5 seconds.
		}
	}

	/**
	 * Create HiveAp.
	 * <p>
	 * 
	 * @param fsm
	 *            Finite state machine of CAPWAP.
	 * @return HiveAp instance to be used for database operation.
	 */
	private HiveAp createHiveAp(AhCapwapFsm fsm) {
		long currTime = System.currentTimeMillis();
		boolean isDynamicIp = fsm.getIpType() == IP_TYPE_DYNAMIC;

		// Build HiveAP instance.
		HiveAp hiveAp = new HiveAp();
		hiveAp.setSerialNumber(fsm.getSerialNum());
		hiveAp.setHostName(fsm.getHostname());
		hiveAp.setIpAddress(fsm.getIp());
		hiveAp.setCfgIpAddress(fsm.getIp());
		hiveAp.setNetmask(fsm.getNetmask());
		hiveAp.setCfgNetmask(fsm.getNetmask());
		hiveAp.setGateway(fsm.getGateway());
		hiveAp.setCfgGateway(fsm.getGateway());
		hiveAp.setMacAddress(fsm.getMac());
		hiveAp.setDhcp(isDynamicIp);
		hiveAp.setProductName(fsm.getHwVer());
		hiveAp.setSoftVer(fsm.getSwVer());
		hiveAp.setMetric(HiveAp.METRIC_TYPE_NORMAL);
		hiveAp.setMetricInteval(60);
		hiveAp.setLocation(fsm.getLocation());
		hiveAp.setCountryCode(fsm.getCountryCode());
		hiveAp.setRegionCode(fsm.getRegionCode());
		hiveAp.setOrigin(HiveAp.ORIGIN_DISCOVERED);
		hiveAp.setAdminUser("admin");
		hiveAp.setAdminPassword(NmsUtil.getOEMCustomer().getDefaultAPPassword());
		hiveAp.setDiscoveryTime(currTime);
		hiveAp.setConnChangedTime(currTime);
		hiveAp.setWifi0RadioProfile(null);
		hiveAp.setWifi1RadioProfile(null);
		hiveAp.setConnected(true);
		hiveAp.setNativeVlan(1);

		// Build Ethernet instance.
		HiveApEth hiveApEth = new HiveApEth();
		hiveApEth.setAdminState(HiveApEth.ADMIN_STATE_UP);
		hiveApEth.setOperationMode(HiveApEth.OPERATION_MODE_BACKHAUL);
		hiveApEth.setSpeed(HiveApEth.ETH_SPEED_AUTO);
		hiveApEth.setDuplex(HiveApEth.ETH_DUPLEX_AUTO);
		hiveAp.setEth0(hiveApEth);

		// Build WIFI instance.
		HiveApWifi wifi0 = new HiveApWifi();
		HiveApWifi wifi1 = new HiveApWifi();
		wifi0.setOperationMode(HiveApWifi.OPERATION_MODE_ACCESS);
		if(hiveAp.getSoftVer() != null && !"".equals(hiveAp.getSoftVer()) 
				&& NmsUtil.compareSoftwareVersion("4.0.1.0", hiveAp.getSoftVer()) > 0){
			wifi1.setOperationMode(HiveApWifi.OPERATION_MODE_BACKHAUL);
		}else{
			wifi1.setOperationMode(HiveApWifi.OPERATION_MODE_ACCESS);
		}
		hiveAp.setWifi0(wifi0);
		hiveAp.setWifi1(wifi1);
		hiveAp.init();
		hiveAp.initInterface();

		return hiveAp;
	}

	/**
	 * Update HiveAp.
	 * <p>
	 * 
	 * @param fsm
	 *            Finite state machine of CAPWAP.
	 * @param hiveAp
	 *            HiveAp instance to be updated into database.
	 */
	private void updateHiveAp(AhCapwapFsm fsm, HiveAp hiveAp) {
		long currTime = System.currentTimeMillis();
		FsmState fsmState = fsm.getFsmState();

		if (fsmState == FsmState.RUN) {
			short origin = hiveAp.getOrigin();
			short status = hiveAp.getManageStatus();
			boolean managed = hiveAp.isManageUponContact();

			if (origin == HiveAp.ORIGIN_CREATE && status == HiveAp.STATUS_NEW
					&& managed) {
				// Allow manually provisioned HiveAP to be managed.
				hiveAp.setManageStatus(HiveAp.STATUS_MANAGED);
			}

			long discoveryTime = hiveAp.getDiscoveryTime();
			boolean isDynamicIp = fsm.getIpType() == IP_TYPE_DYNAMIC;

			if (discoveryTime == 0) {
				hiveAp.setDiscoveryTime(currTime);
			}

			if (hiveAp.getCfgIpAddress().equals("")) {
				hiveAp.setCfgIpAddress(fsm.getIp());
			}

			hiveAp.setHostName(fsm.getHostname());
			hiveAp.setIpAddress(fsm.getIp());
			hiveAp.setNetmask(fsm.getNetmask());
			hiveAp.setGateway(fsm.getGateway());
			hiveAp.setMacAddress(fsm.getMac());
			hiveAp.setDhcp(isDynamicIp);
			hiveAp.setProductName(fsm.getHwVer());
			hiveAp.setSoftVer(fsm.getSwVer());
			hiveAp.setLocation(fsm.getLocation());
			hiveAp.setCountryCode(fsm.getCountryCode());
			hiveAp.setRegionCode(fsm.getRegionCode());
			hiveAp.setConnChangedTime(currTime);
			hiveAp.setConnected(true);
		} else {
			hiveAp.setConnected(false);
			hiveAp.setUpTime(0);
		}
	}

	private void flush() throws Exception {
		if (localReqsHolder.size() > 0) {
			// Look for all hive AP objects in the update requests map.
			List<HiveAp> bos = QueryUtil.executeQuery(HiveAp.class, null,
					new FilterParams("serialNumber", localReqsHolder.keySet()));

			if (!bos.isEmpty()) {
				// Update HiveAps which are already exist in the database.
				List<HiveAp> hiveAps = new ArrayList<HiveAp>(bos.size());
				HiveAp hiveAp;
				AhCapwapFsm fsm;

				for (Object bo : bos) {
					hiveAp = (HiveAp) bo;
					fsm = localReqsHolder.remove(hiveAp.getSerialNumber());
					logger
							.debug("flush", "Update HiveAp "
									+ fsm.getSerialNum());
					updateHiveAp(fsm, hiveAp);
					hiveAps.add(hiveAp);
				}

				QueryUtil.bulkUpdateBos(hiveAps);
			}

			if (localReqsHolder.size() > 0) {
				// Create new HiveAps.
				List<HiveAp> newHiveAps = new ArrayList<HiveAp>(localReqsHolder
						.size());

				for (AhCapwapFsm fsm : localReqsHolder.values()) {
					logger.debug("flush", "Add new HiveAp "
							+ fsm.getSerialNum());
					newHiveAps.add(createHiveAp(fsm));
				}

				QueryUtil.bulkCreateBos(newHiveAps);

				// Clear request holder.
				localReqsHolder.clear();
			}
		}

		// Reinitialize flush timer.
		initTimer();
	}

	public void updateAp(AhCapwapFsm fsm) throws Exception {
		localReqsHolder.put(fsm.getSerialNum(), fsm);

		if (localReqsHolder.size() >= flushCount) {
			flush();
		}
	}

}