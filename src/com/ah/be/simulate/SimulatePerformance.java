package com.ah.be.simulate;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.AhConvertBOToSQL;
import com.ah.be.common.db.BulkUpdateUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhBandWidthSentinelHistory;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhInterferenceStats;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhRadioStats;
import com.ah.bo.performance.AhVIfStats;
import com.ah.bo.performance.AhXIf;
import com.ah.bo.performance.XIfPK;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class SimulatePerformance {

	public static final int LAST_HOURS = 8;
//	public static int BULK_SIZE = 100;

	private static long getRandom(int[] base) {
		long ret = base[3];
		int ram = base[2] / 10;
		for (int i = 0; i < 10; i++) {
			ret += (long) (Math.random() * ram);
		}
		return ret;
		// return (long) (Math.random() * range + min);
	}

	/**
	 * generate last 8 hours performance
	 * 
	 * @param ap -
	 */
	public static void generatePerformance(HiveAp ap) {
		if (!ap.isSimulated())
			return;
		long now = System.currentTimeMillis();
		long stattimestamp = (now / 3600000 - LAST_HOURS) * 3600000 + 1;
		try {
			generatePerformance(stattimestamp, ap);
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"Fail to generate performance data of simulated HiveAp "
							+ ap.getHostName(), e);
		}
	}

	/**
	 * generate last 8 hours performance
	 * 
	 * @param apList -
	 */
	public static void generatePerformance(List<HiveAp> apList) {
		long now = System.currentTimeMillis();
		long stattimestamp = (now / 3600000 - LAST_HOURS) * 3600000 + 1;
		try {
			for (HiveAp ap : apList) {
				if (ap.isSimulated())
					generatePerformance(stattimestamp, ap);
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"Fail to generate simulated performance data", e);
		}
	}

	/**
	 * generate performance data
	 * 
	 * @param stattimestamp -
	 * @param ap -
	 */
	private static void generatePerformance(long stattimestamp, HiveAp ap) {
		long begin = System.currentTimeMillis();
		long begintimestamp;
		List<AhXIf> xifList = new ArrayList<>();
		List<AhVIfStats> vifStatsList = new ArrayList<>();
		List<AhXIf> ahourXifList = new ArrayList<>();
		List<AhVIfStats> ahourVifStatsList = new ArrayList<>();

		try {
			int ssidNumber = (int) (Math.random() * 7 + 1);
			ahourXifList.clear();
			ahourVifStatsList.clear();
			xifList.clear();
			vifStatsList.clear();
			begintimestamp = stattimestamp;
			for (int i = 0; i < LAST_HOURS; i++) {
				generateXIfAndVIfStats(begintimestamp, ap, ahourXifList,
						ahourVifStatsList, xifList, vifStatsList, ssidNumber);
				begintimestamp += 3600000;

			}
			QueryUtil.bulkCreateBos(xifList);
			QueryUtil.bulkCreateBos(vifStatsList);

		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"Fail to generate XIf and VIfstats data of simulated HiveAp "
							+ ap.getHostName(), e);
		}

		AhXIf wifi0XIf = null, wifi1XIf = null;
		for (AhXIf bo : ahourXifList) {
			if (bo.getIfName().equalsIgnoreCase("wifi0"))
				wifi0XIf = bo;
			if (bo.getIfName().equalsIgnoreCase("wifi1"))
				wifi1XIf = bo;
		}

		List<AhRadioAttribute> radioAttributeList = new ArrayList<>();
		List<AhRadioStats> radioStatsList = new ArrayList<>();
		List<AhRadioAttribute> ahourRadioAttributeList = new ArrayList<>();
		List<AhRadioStats> ahourRadioStatsList = new ArrayList<>();

		try {
			begintimestamp = stattimestamp;
			for (int i = 0; i < LAST_HOURS; i++) {
				generateRadioAndRadioStats(begintimestamp, ap,
						ahourRadioAttributeList, ahourRadioStatsList,
						radioAttributeList, radioStatsList, wifi0XIf, wifi1XIf);
				begintimestamp += 3600000;
			}
			QueryUtil.bulkCreateBos(radioAttributeList);
			QueryUtil.bulkCreateBos(radioStatsList);
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"Fail to generate RadioAttribute and RadioStats data of simulated HiveAp "
							+ ap.getHostName(), e);
		}
		List<AhInterferenceStats> interferenceList = new ArrayList<>();
		List<AhInterferenceStats> ahourInterferenceList = new ArrayList<>();
		try {
			begintimestamp = stattimestamp;
			for (int i = 0; i < LAST_HOURS; i++) {
				generateInterferenceStats(begintimestamp, ap,
						ahourInterferenceList, interferenceList, wifi0XIf,
						wifi1XIf);
				begintimestamp += 3600000;
			}
			BulkUpdateUtil.bulkInsertForInterference(interferenceList);
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"Fail to generate interference data of simulated HiveAp "
							+ ap.getHostName(), e);
		}

		List<AhNeighbor> neighborList = new ArrayList<>();
		List<AhNeighbor> ahourNeighborList = new ArrayList<>();
		try {
			begintimestamp = stattimestamp;
			for (int i = 0; i < LAST_HOURS; i++) {
				AhXIf xif;
				if (wifi1XIf != null)
					xif = wifi1XIf;
				else
					xif = wifi0XIf;
				generateNeighborStats(begintimestamp, ap, ahourNeighborList,
						neighborList, xif.getXifpk().getIfIndex(), ap
								.getSimulateNeighborInfo());
				begintimestamp += 3600000;
			}
			BulkUpdateUtil.bulkInsertForNeighbor(neighborList);
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"Fail to generate neighbor data of simulated HiveAp "
							+ ap.getHostName(), e);
		}

		List<AhAssociation> associationList = new ArrayList<>();
		List<AhAssociation> ahourAssociationList = new ArrayList<>();
		List<AhClientSession> clientsessionList = new ArrayList<>();
		List<AhBandWidthSentinelHistory> sentinelList = new ArrayList<>();
		try {
			// generate client
			List<String> clientList = new ArrayList<>();
			generateClient(ap, clientList);

			begintimestamp = stattimestamp;
			for (int i = 0; i < LAST_HOURS; i++) {
				generateClientAssiciation(begintimestamp, ap,
						ahourAssociationList, associationList, clientList);
				begintimestamp += 3600000;
			}
			generateClientSession(ap, clientsessionList, ahourAssociationList);
			
			generateClientBandWidthSentinel(ap, sentinelList,associationList);

			if (!associationList.isEmpty())
				BulkUpdateUtil.bulkInsertForAssociation(associationList);

			if (!clientsessionList.isEmpty())
				QueryUtil.executeNativeUpdate(AhConvertBOToSQL
						.convertClientSessionToSQL(clientsessionList));
			
			if (!sentinelList.isEmpty())
				BulkUpdateUtil.bulkInsertForBandWidthSentinel(sentinelList);
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"Fail to generate client data of simulated HiveAp "
							+ ap.getHostName(), e);
		}

		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"Generate performance data of simulated HiveAp "
						+ ap.getHostName() + ", eclipse time "
						+ (System.currentTimeMillis() - begin) + "ms");
	}

	/**
	 * generate xif and vifstats data
	 * 
	 * @param stattimestamp -
	 * @param ap -
	 * @param ahourXifList -
	 * @param ahourVifStatsList -
	 * @param xifList -
	 * @param vifStatsList -
	 * @param ssidNumber -
	 */
	private static void generateXIfAndVIfStats(long stattimestamp, HiveAp ap,
			List<AhXIf> ahourXifList, List<AhVIfStats> ahourVifStatsList,
			List<AhXIf> xifList, List<AhVIfStats> vifStatsList, int ssidNumber) {
		if (!ahourXifList.isEmpty()) {
			for (AhXIf xif : ahourXifList) {
				AhXIf bo = generateXIf(xif);
				bo.getXifpk().setStatTime(stattimestamp);
				xifList.add(bo);
			}
			for (AhVIfStats vifStats : ahourVifStatsList) {
				AhVIfStats bo = generateVIfStats(vifStats, false);
				bo.getXifpk().setStatTime(stattimestamp);
				vifStatsList.add(bo);
			}
			return;
		}
		int ifindex = 2;
		String ifname;
		String ssidname;
		// eth0
		generateOneXIfAndVIfStats(stattimestamp, ap, xifList, vifStatsList,
				ifindex, "eth0", null);
		ifindex++;
		if (ap.isEth1Available()) {
			// eth1
			generateOneXIfAndVIfStats(stattimestamp, ap, xifList, vifStatsList,
					ifindex, "eth1", null);
			ifindex++;
		}
		// wifi0
		generateOneXIfAndVIfStats(stattimestamp, ap, xifList, vifStatsList,
				ifindex, "wifi0", null);
		ifindex++;
		if (ap.isWifi1Available()) {
			// wifi1
			generateOneXIfAndVIfStats(stattimestamp, ap, xifList, vifStatsList,
					ifindex, "wifi1", null);
			ifindex++;
		}
		// wifi0.x
		for (int i = 1; i <= ssidNumber; i++) {
			ifname = "wifi0." + i;
			ssidname = "ssid" + i;
			generateOneXIfAndVIfStats(stattimestamp, ap, xifList, vifStatsList,
					ifindex, ifname, ssidname);
			ifindex++;
		}
		for (AhXIf xif : xifList) {
			ahourXifList.add(generateXIf(xif));
		}
		for (AhVIfStats vifStats : vifStatsList) {
			ahourVifStatsList.add(generateVIfStats(vifStats, true));
		}
	}

	/**
	 * generate one row of xif and vif stats
	 * 
	 * @param stattimestamp -
	 * @param ap -
	 * @param xifList -
	 * @param vifStatsList -
	 * @param ifindex -
	 * @param ifname -
	 * @param ssidname -
	 */
	private static void generateOneXIfAndVIfStats(long stattimestamp,
			HiveAp ap, List<AhXIf> xifList, List<AhVIfStats> vifStatsList,
			int ifindex, String ifname, String ssidname) {
		AhXIf xif = new AhXIf();

		xif.setOwner(ap.getOwner());

		XIfPK pk = new XIfPK();
//		pk.setTimeZone(ap.getOwner().getTimeZoneString());
		pk.setStatTime(stattimestamp);
		pk.setApName(ap.getHostName());
		pk.setIfIndex(ifindex);
		xif.setXifpk(pk);
		xif.setApMac(ap.getMacAddress());
		xif.setApSerialNumber(ap.getSerialNumber());

		xif.setIfPromiscuous(AhXIf.IFPROMISCUOUS_FALSE);
		if (ifname.equalsIgnoreCase("eth0") || ifname.equalsIgnoreCase("eth1")) {
			xif.setIfType(AhXIf.IFTYPE_PHYSICAL);
			xif.setIfMode(AhXIf.IFMODE_BACKHAUL);
		} else if (ifname.equalsIgnoreCase("wifi0")) {
			xif.setIfType(AhXIf.IFTYPE_PHYSICAL);
			xif.setIfMode(AhXIf.IFMODE_ACCESS);
		} else if (ifname.equalsIgnoreCase("wifi1")) {
			xif.setIfType(AhXIf.IFTYPE_PHYSICAL);
			xif.setIfMode(AhXIf.IFMODE_BACKHAUL);
		} else {
			xif.setIfType(AhXIf.IFTYPE_VIRTURAL);
			xif.setIfMode(AhXIf.IFMODE_ACCESS);
		}
		xif.setIfName(ifname);
		xif.setSsidName(ssidname);

		xif.setIfConfMode(AhXIf.IFMODE_NOTUSED);
		xif.setIfAdminStatus(AhXIf.IFADMINSTATUS_UP);
		xif.setIfOperStatus(AhXIf.IFOPERSTATUS_UP);
		xifList.add(xif);

		if (ssidname == null)
			return;
		AhVIfStats vifStats = new AhVIfStats();
		vifStats.setOwner(ap.getOwner());
		XIfPK pkVif = new XIfPK();
//		pkVif.setTimeZone(ap.getOwner().getTimeZoneString());
		pkVif.setStatTime(stattimestamp);
		pkVif.setApName(ap.getHostName());
		pkVif.setIfIndex(ifindex);
		vifStats.setXifpk(pkVif);
		vifStats.setApMac(ap.getMacAddress());
		vifStats.setApSerialNumber(ap.getSerialNumber());

		vifStatsList.add(vifStats);
	}

	/**
	 * clone AhXIf
	 * 
	 * @param xif -
	 * @return -
	 */
	private static AhXIf generateXIf(AhXIf xif) {
		AhXIf bo = new AhXIf();

		bo.setOwner(xif.getOwner());

		XIfPK pk = new XIfPK();
//		pk.setTimeZone(xif.getXifpk().getTimeZone());
		pk.setStatTime(xif.getXifpk().getStatTimeValue());
		pk.setApName(xif.getXifpk().getApName());
		pk.setIfIndex(xif.getXifpk().getIfIndex());
		bo.setXifpk(pk);
		bo.setApMac(xif.getApMac());
		bo.setApSerialNumber(xif.getApSerialNumber());

		bo.setIfPromiscuous(xif.getIfPromiscuous());
		bo.setIfType(xif.getIfType());
		bo.setIfMode(xif.getIfMode());
		bo.setIfName(xif.getIfName());
		bo.setSsidName(xif.getSsidName());

		bo.setIfConfMode(xif.getIfConfMode());
		bo.setIfAdminStatus(xif.getIfAdminStatus());
		bo.setIfOperStatus(xif.getIfOperStatus());
		return bo;
	}

	/**
	 * clone VIfStats
	 * 
	 * @param vifStats -
	 * @param onlyCopy -
	 * @return -
	 */
	private static AhVIfStats generateVIfStats(AhVIfStats vifStats,
			boolean onlyCopy) {
		AhVIfStats bo = new AhVIfStats();

		bo.setOwner(vifStats.getOwner());

		XIfPK pk = new XIfPK();
//		pk.setTimeZone(vifStats.getXifpk().getTimeZone());
		pk.setStatTime(vifStats.getXifpk().getStatTimeValue());
		pk.setApName(vifStats.getXifpk().getApName());
		pk.setIfIndex(vifStats.getXifpk().getIfIndex());
		bo.setXifpk(pk);
		bo.setApMac(vifStats.getApMac());
		bo.setApSerialNumber(vifStats.getApSerialNumber());

		if (!onlyCopy) {
			// get the next value
			int index_base = 0;
			vifStats
					.setRxVIfDataFrames(vifStats.getRxVIfDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setRxVIfUnicastDataFrames(vifStats
							.getRxVIfUnicastDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setRxVIfMulticastDataFrames(vifStats
							.getRxVIfMulticastDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setRxVIfBroadcastDataFrames(vifStats
							.getRxVIfBroadcastDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setRxVIfErrorFrames(vifStats.getRxVIfErrorFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setRxVIfDroppedFrames(vifStats.getRxVIfDroppedFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;

			vifStats
					.setTxVIfDataFrames(vifStats.getTxVIfDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setTxVIfUnicastDataFrames(vifStats
							.getTxVIfUnicastDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setTxVIfMulticastDataFrames(vifStats
							.getTxVIfMulticastDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setTxVIfBroadcastDataFrames(vifStats
							.getTxVIfBroadcastDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setTxVIfErrorFrames(vifStats.getTxVIfErrorFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setTxVIfDroppedFrames(vifStats.getTxVIfDroppedFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;

			vifStats
					.setTxVIfBeDataFrames(vifStats.getTxVIfBeDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setTxVIfBgDataFrames(vifStats.getTxVIfBgDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setTxVIfViDataFrames(vifStats.getTxVIfViDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setTxVIfVoDataFrames(vifStats.getTxVIfVoDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;

			vifStats
					.setRxVifAirtime(vifStats.getRxVifAirtime()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
			vifStats
					.setTxVifAirtime(vifStats.getTxVifAirtime()
							+ getRandom(SimulateConstant.SIMULATE_VIFSTATS_BASE[index_base]));
			index_base++;
		}

		bo.setRxVIfDataFrames(vifStats.getRxVIfDataFrames());
		bo.setRxVIfUnicastDataFrames(vifStats.getRxVIfUnicastDataFrames());
		bo.setRxVIfMulticastDataFrames(vifStats.getRxVIfMulticastDataFrames());
		bo.setRxVIfBroadcastDataFrames(vifStats.getRxVIfBroadcastDataFrames());
		bo.setRxVIfErrorFrames(vifStats.getRxVIfErrorFrames());
		bo.setRxVIfDroppedFrames(vifStats.getRxVIfDroppedFrames());

		bo.setTxVIfDataFrames(vifStats.getTxVIfDataFrames());
		bo.setTxVIfUnicastDataFrames(vifStats.getTxVIfUnicastDataFrames());
		bo.setTxVIfMulticastDataFrames(vifStats.getTxVIfMulticastDataFrames());
		bo.setTxVIfBroadcastDataFrames(vifStats.getTxVIfBroadcastDataFrames());
		bo.setTxVIfErrorFrames(vifStats.getTxVIfErrorFrames());
		bo.setTxVIfDroppedFrames(vifStats.getTxVIfDroppedFrames());

		bo.setTxVIfBeDataFrames(vifStats.getTxVIfBeDataFrames());
		bo.setTxVIfBgDataFrames(vifStats.getTxVIfBgDataFrames());
		bo.setTxVIfViDataFrames(vifStats.getTxVIfViDataFrames());
		bo.setTxVIfVoDataFrames(vifStats.getTxVIfVoDataFrames());

		bo.setRxVifAirtime(vifStats.getRxVifAirtime());
		bo.setTxVifAirtime(vifStats.getTxVifAirtime());

		return bo;
	}

	private static void generateRadioAndRadioStats(long stattimestamp,
			HiveAp ap, List<AhRadioAttribute> ahourRadioAttributeList,
			List<AhRadioStats> ahourRadioStatsList,
			List<AhRadioAttribute> radioAttributeList,
			List<AhRadioStats> radioStatsList, AhXIf wifi0, AhXIf wifi1) {
		if (!ahourRadioAttributeList.isEmpty()) {
			for (AhRadioAttribute radioAttribute : ahourRadioAttributeList) {
				AhRadioAttribute bo = generateRadioAttribute(radioAttribute);
				bo.getXifpk().setStatTime(stattimestamp);
				radioAttributeList.add(bo);
			}
			for (AhRadioStats radioStats : ahourRadioStatsList) {
				AhRadioStats bo = generateRadioStats(radioStats, false);
				bo.getXifpk().setStatTime(stattimestamp);
				radioStatsList.add(bo);
			}
			return;
		}

		// wifi0
		if (wifi0 != null) {
			generateOneRadioAndRadioStats(stattimestamp, ap,
					radioAttributeList, radioStatsList, wifi0.getXifpk()
							.getIfIndex(), true);
		}

		// wifi1
		if (wifi1 != null) {
			generateOneRadioAndRadioStats(stattimestamp, ap,
					radioAttributeList, radioStatsList, wifi1.getXifpk()
							.getIfIndex(), false);
		}

		for (AhRadioAttribute radioAttribute : radioAttributeList) {
			ahourRadioAttributeList.add(generateRadioAttribute(radioAttribute));
		}
		for (AhRadioStats radioStats : radioStatsList) {
			ahourRadioStatsList.add(generateRadioStats(radioStats, true));
		}
	}

	private static void generateOneRadioAndRadioStats(long stattimestamp,
			HiveAp ap, List<AhRadioAttribute> radioAttributeList,
			List<AhRadioStats> radioStatsList, int ifindex, boolean isWifi0) {
		AhRadioAttribute radioAttribute = new AhRadioAttribute();

		radioAttribute.setOwner(ap.getOwner());

		XIfPK pk = new XIfPK();
//		pk.setTimeZone(ap.getOwner().getTimeZoneString());
		pk.setStatTime(stattimestamp);
		pk.setApName(ap.getHostName());
		pk.setIfIndex(ifindex);
		radioAttribute.setXifpk(pk);
		radioAttribute.setApMac(ap.getMacAddress());
		radioAttribute.setApSerialNumber(ap.getSerialNumber());

		if (isWifi0)
			radioAttribute.setRadioChannel((int) (Math.random() * 15 + 5));
		else
			radioAttribute
					.setRadioChannel((int) (Math.random() * 15 + 5 + 145));
		radioAttribute.setRadioTxPower((int) (Math.random() * 15 + 5));
		radioAttribute
				.setRadioNoiseFloor(getRandom(SimulateConstant.SIMULATE_RADIOATTRIBUTE_BASE[0]));

		radioAttributeList.add(radioAttribute);

		AhRadioStats radioStats = new AhRadioStats();
		radioStats.setOwner(ap.getOwner());
		XIfPK pkRadio = new XIfPK();
//		pkRadio.setTimeZone(ap.getOwner().getTimeZoneString());
		pkRadio.setStatTime(stattimestamp);
		pkRadio.setApName(ap.getHostName());
		pkRadio.setIfIndex(ifindex);
		radioStats.setXifpk(pkRadio);
		radioStats.setApMac(ap.getMacAddress());
		radioStats.setApSerialNumber(ap.getSerialNumber());

		radioStatsList.add(radioStats);
	}

	/**
	 * clone AhRadioAttribute
	 * 
	 * @param radioAttribute -
	 * @return -
	 */
	private static AhRadioAttribute generateRadioAttribute(
			AhRadioAttribute radioAttribute) {
		AhRadioAttribute bo = new AhRadioAttribute();

		bo.setOwner(radioAttribute.getOwner());

		XIfPK pk = new XIfPK();
//		pk.setTimeZone(radioAttribute.getXifpk().getTimeZone());
		pk.setStatTime(radioAttribute.getXifpk().getStatTimeValue());
		pk.setApName(radioAttribute.getXifpk().getApName());
		pk.setIfIndex(radioAttribute.getXifpk().getIfIndex());
		bo.setXifpk(pk);
		bo.setApMac(radioAttribute.getApMac());
		bo.setApSerialNumber(radioAttribute.getApSerialNumber());

		bo.setRadioChannel(radioAttribute.getRadioChannel());
		bo.setRadioTxPower(radioAttribute.getRadioTxPower());
		bo
				.setRadioNoiseFloor(getRandom(SimulateConstant.SIMULATE_RADIOATTRIBUTE_BASE[0]));
		return bo;
	}

	/**
	 * generate RadioStats
	 * 
	 * @param radioStats -
	 * @param onlyCopy -
	 * @return -
	 */
	private static AhRadioStats generateRadioStats(AhRadioStats radioStats,
			boolean onlyCopy) {
		AhRadioStats bo = new AhRadioStats();

		bo.setOwner(radioStats.getOwner());

		XIfPK pk = new XIfPK();
//		pk.setTimeZone(radioStats.getXifpk().getTimeZone());
		pk.setStatTime(radioStats.getXifpk().getStatTimeValue());
		pk.setApName(radioStats.getXifpk().getApName());
		pk.setIfIndex(radioStats.getXifpk().getIfIndex());
		bo.setXifpk(pk);
		bo.setApMac(radioStats.getApMac());
		bo.setApSerialNumber(radioStats.getApSerialNumber());

		if (!onlyCopy) {
			// get the next value
			int index_base = 0;
			radioStats
					.setRadioTxDataFrames(radioStats.getRadioTxDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTxNonBeaconMgtFrames(radioStats
							.getRadioTxNonBeaconMgtFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTxUnicastDataFrames(radioStats
							.getRadioTxUnicastDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTxMulticastDataFrames(radioStats
							.getRadioTxMulticastDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTxBroadcastDataFrames(radioStats
							.getRadioTxBroadcastDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTxBeaconFrames(radioStats.getRadioTxBeaconFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTxTotalRetries(radioStats.getRadioTxTotalRetries()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTxTotalFramesDropped(radioStats
							.getRadioTxTotalFramesDropped()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTxTotalFrameErrors(radioStats
							.getRadioTxTotalFrameErrors()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTxFEForExcessiveHWRetries(radioStats
							.getRadioTxFEForExcessiveHWRetries()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;

			radioStats
					.setRadioRxTotalDataFrames(radioStats
							.getRadioRxTotalDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioRxUnicastDataFrames(radioStats
							.getRadioRxUnicastDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioRxMulticastDataFrames(radioStats
							.getRadioRxMulticastDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioRxBroadcastDataFrames(radioStats
							.getRadioRxBroadcastDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioRxMgtFrames(radioStats.getRadioRxMgtFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioRxTotalFrameDropped(radioStats
							.getRadioRxTotalFrameDropped()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;

			radioStats
					.setRadioTxBeDataFrames(radioStats.getRadioTxBeDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTxBgDataFrames(radioStats.getRadioTxBgDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTxViDataFrames(radioStats.getRadioTxViDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTxVoDataFrames(radioStats.getRadioTxVoDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTXRTSFailures(radioStats.getRadioTXRTSFailures()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;

			radioStats
					.setRadioRxAirtime(radioStats.getRadioRxAirtime()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;
			radioStats
					.setRadioTxAirtime(radioStats.getRadioTxAirtime()
							+ getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base]));
			index_base++;

			radioStats
					.setBandWidth((int) (getRandom(SimulateConstant.SIMULATE_RADIOSTATS_BASE[index_base])));
			index_base++;
		}
		bo.setRadioTxDataFrames(radioStats.getRadioTxDataFrames());
		bo.setRadioTxNonBeaconMgtFrames(radioStats
				.getRadioTxNonBeaconMgtFrames());
		bo
				.setRadioTxUnicastDataFrames(radioStats
						.getRadioTxUnicastDataFrames());
		bo.setRadioTxMulticastDataFrames(radioStats
				.getRadioTxMulticastDataFrames());
		bo.setRadioTxBroadcastDataFrames(radioStats
				.getRadioTxBroadcastDataFrames());
		bo.setRadioTxBeaconFrames(radioStats.getRadioTxBeaconFrames());
		bo.setRadioTxTotalRetries(radioStats.getRadioTxTotalRetries());
		bo.setRadioTxTotalFramesDropped(radioStats
				.getRadioTxTotalFramesDropped());
		bo.setRadioTxTotalFrameErrors(radioStats.getRadioTxTotalFrameErrors());
		bo.setRadioTxFEForExcessiveHWRetries(radioStats
				.getRadioTxFEForExcessiveHWRetries());

		bo.setRadioRxTotalDataFrames(radioStats.getRadioRxTotalDataFrames());
		bo
				.setRadioRxUnicastDataFrames(radioStats
						.getRadioRxUnicastDataFrames());
		bo.setRadioRxMulticastDataFrames(radioStats
				.getRadioRxMulticastDataFrames());
		bo.setRadioRxBroadcastDataFrames(radioStats
				.getRadioRxBroadcastDataFrames());
		bo.setRadioRxMgtFrames(radioStats.getRadioRxMgtFrames());
		bo
				.setRadioRxTotalFrameDropped(radioStats
						.getRadioRxTotalFrameDropped());

		bo.setRadioTxBeDataFrames(radioStats.getRadioTxBeDataFrames());
		bo.setRadioTxBgDataFrames(radioStats.getRadioTxBgDataFrames());
		bo.setRadioTxViDataFrames(radioStats.getRadioTxViDataFrames());
		bo.setRadioTxVoDataFrames(radioStats.getRadioTxVoDataFrames());
		bo.setRadioTXRTSFailures(radioStats.getRadioTXRTSFailures());

		bo.setRadioRxAirtime(radioStats.getRadioRxAirtime());
		bo.setRadioTxAirtime(radioStats.getRadioTxAirtime());

		bo.setBandWidth(radioStats.getBandWidth());

		return bo;
	}

	/**
	 * genereate interference data
	 * 
	 * @param stattimestamp -
	 * @param ap -
	 * @param ahourInterferenceList -
	 * @param interferenceList -
	 * @param wifi0XIf -
	 * @param wifi1XIf -
	 */
	private static void generateInterferenceStats(long stattimestamp,
			HiveAp ap, List<AhInterferenceStats> ahourInterferenceList,
			List<AhInterferenceStats> interferenceList, AhXIf wifi0XIf,
			AhXIf wifi1XIf) {
		if (!ahourInterferenceList.isEmpty()) {
			for (AhInterferenceStats inter : ahourInterferenceList) {
				AhInterferenceStats bo = generateInterference(inter, false);
				bo.getTimeStamp().setTime(stattimestamp);
				interferenceList.add(bo);
			}
			return;
		}
		// wifi0
		if (wifi0XIf != null) {
			generateOneInterferenceStats(stattimestamp, ap, interferenceList,
					wifi0XIf);
		}

		// wifi1
		if (wifi1XIf != null) {
			generateOneInterferenceStats(stattimestamp, ap, interferenceList,
					wifi1XIf);
		}

		for (AhInterferenceStats inter : interferenceList) {
			ahourInterferenceList.add(generateInterference(inter, true));
		}
	}

	/**
	 * generate one row neighbor
	 * 
	 * @param stattimestamp -
	 * @param ap -
	 * @param interferenceList -
	 * @param xif -
	 */
	private static void generateOneInterferenceStats(long stattimestamp,
			HiveAp ap, List<AhInterferenceStats> interferenceList, AhXIf xif) {
		AhInterferenceStats interferenceStats = new AhInterferenceStats();
		interferenceStats.setApMac(ap.getMacAddress());
		HmTimeStamp timeStamp = new HmTimeStamp(stattimestamp, ap.getOwner()
				.getTimeZoneString());
		interferenceStats.setTimeStamp(timeStamp);
		interferenceStats.setIfIndex(xif.getXifpk().getIfIndex());
		interferenceStats.setIfName(xif.getIfName());
		interferenceStats.setOwner(ap.getOwner());
		interferenceStats.setApName(ap.getHostName());

		interferenceStats.setChannelNumber((byte) (Math.random() * 15));

		interferenceList.add(generateInterference(interferenceStats, false));
	}

	/**
	 * clone object
	 * 
	 * @param inter -
	 * @param onlyCopy -
	 * @return -
	 */
	private static AhInterferenceStats generateInterference(
			AhInterferenceStats inter, boolean onlyCopy) {
		AhInterferenceStats bo = new AhInterferenceStats();
		bo.setApMac(inter.getApMac());
		HmTimeStamp timeStamp = new HmTimeStamp(inter.getTimeStamp().getTime(),
				inter.getTimeStamp().getTimeZone());
		bo.setTimeStamp(timeStamp);
		bo.setIfIndex(inter.getIfIndex());
		bo.setIfName(inter.getIfName());
		bo.setOwner(inter.getOwner());
		bo.setApName(inter.getApName());
		bo.setChannelNumber(inter.getChannelNumber());

		if (!onlyCopy) {
			// get the next value
			int index_base = 0;
			inter
					.setAverageTXCU((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setAverageRXCU((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setAverageInterferenceCU((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setAverageNoiseFloor((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setShortTermTXCU((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setShortTermRXCU((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setShortTermInterferenceCU((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setShortTermNoiseFloor((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setSnapShotTXCU((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setSnapShotRXCU((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setSnapShotInterferenceCU((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setSnapShotNoiseFloor((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setCrcError((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setInterferenceCUThreshold((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;
			inter
					.setCrcErrorRateThreshold((byte) getRandom(SimulateConstant.SIMULATE_INTERFERENCE_BASE[index_base]));
			index_base++;

			if (inter.getCrcError() >= inter.getCrcErrorRateThreshold())
				inter.setSeverity((byte) 4);
			else if (inter.getShortTermInterferenceCU() >= inter
					.getInterferenceCUThreshold())
				inter.setSeverity((byte) 3);
			else if (inter.getSnapShotInterferenceCU() >= inter
					.getInterferenceCUThreshold())
				inter.setSeverity((byte) 2);
			else
				inter.setSeverity((byte) 1);
		}

		bo.setAverageTXCU(inter.getAverageTXCU());
		bo.setAverageRXCU(inter.getAverageRXCU());
		bo.setAverageInterferenceCU(inter.getAverageInterferenceCU());
		bo.setAverageNoiseFloor(inter.getAverageNoiseFloor());
		bo.setShortTermTXCU(inter.getShortTermTXCU());
		bo.setShortTermRXCU(inter.getShortTermRXCU());
		bo.setShortTermInterferenceCU(inter.getShortTermInterferenceCU());
		bo.setShortTermNoiseFloor(inter.getShortTermNoiseFloor());
		bo.setSnapShotTXCU(inter.getSnapShotTXCU());
		bo.setSnapShotRXCU(inter.getSnapShotRXCU());
		bo.setSnapShotInterferenceCU(inter.getSnapShotInterferenceCU());
		bo.setSnapShotNoiseFloor(inter.getSnapShotNoiseFloor());
		bo.setCrcError(inter.getCrcError());
		bo.setInterferenceCUThreshold(inter.getInterferenceCUThreshold());
		bo.setCrcErrorRateThreshold(inter.getCrcErrorRateThreshold());
		bo.setSeverity(inter.getSeverity());

		return bo;
	}

	/**
	 * generate neighbor data
	 * 
	 * @param stattimestamp -
	 * @param ap -
	 * @param ahourNeighborList -
	 * @param neighborList -
	 * @param ifindex -
	 * @param neighbor -
	 */
	private static void generateNeighborStats(long stattimestamp, HiveAp ap,
			List<AhNeighbor> ahourNeighborList, List<AhNeighbor> neighborList,
			int ifindex, String neighbor) {
		if (!ahourNeighborList.isEmpty()) {
			for (AhNeighbor nbr : ahourNeighborList) {
				AhNeighbor bo = generateNeighbor(nbr, false);
				bo.getTimeStamp().setTime(stattimestamp);
				neighborList.add(bo);
			}
			return;
		}
		if (neighbor == null)
			return;
		String[] nbrArray = neighbor.split(",");
		for (String nbr : nbrArray) {
			generateOneNeighborStats(stattimestamp, ap, neighborList, ifindex,
					nbr);
		}

		for (AhNeighbor nbr : neighborList) {
			ahourNeighborList.add(generateNeighbor(nbr, true));
		}
	}

	/**
	 * generate one row neighbor
	 * 
	 * @param stattimestamp -
	 * @param ap -
	 * @param neighborList -
	 * @param ifindex -
	 * @param mac -
	 */
	private static void generateOneNeighborStats(long stattimestamp, HiveAp ap,
			List<AhNeighbor> neighborList, int ifindex, String mac) {
		AhNeighbor nbr = new AhNeighbor();

		nbr.setOwner(ap.getOwner());

		nbr.setApName(ap.getHostName());
		nbr.setApMac(ap.getMacAddress());
		HmTimeStamp timeStamp = new HmTimeStamp(stattimestamp, ap.getOwner()
				.getTimeZoneString());
		nbr.setTimeStamp(timeStamp);
		nbr.setApSerialNumber(ap.getSerialNumber());

		nbr.setIfIndex(ifindex);
		nbr.setNeighborAPID(mac);
		nbr.setLinkCost(getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[0]));
		nbr
				.setRssi((int) getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[1]));
		nbr.setLinkType((byte) 1);// (byte) (getRandom(100, 0) % 2));
		nbr.setLinkUpTime(120000);

		neighborList.add(nbr);
	}

	/**
	 * clone AhNeighbor
	 * 
	 * @param nbr -
	 * @param onlyCopy -
	 * @return -
	 */
	private static AhNeighbor generateNeighbor(AhNeighbor nbr, boolean onlyCopy) {
		AhNeighbor bo = new AhNeighbor();

		bo.setOwner(nbr.getOwner());

		HmTimeStamp timeStamp = new HmTimeStamp(nbr.getTimeStamp().getTime(),
				nbr.getTimeStamp().getTimeZone());

		bo.setApName(nbr.getApName());
		bo.setApMac(nbr.getApMac());
		bo.setTimeStamp(timeStamp);
		bo.setApSerialNumber(nbr.getApSerialNumber());

		bo.setIfIndex(nbr.getIfIndex());
		bo.setNeighborAPID(nbr.getNeighborAPID());
		bo.setLinkCost(getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[0]));
		bo.setRssi((int) getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[1]));
		bo.setLinkType(nbr.getLinkType());
		bo.setLinkUpTime(nbr.getLinkUpTime());

		if (!onlyCopy) {
			// get the next value
			nbr.setLinkUpTime(nbr.getLinkUpTime() + 3600000);

			int index_base = 2;

			nbr
					.setRxDataFrames(nbr.getRxDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
			nbr
					.setRxDataOctets(nbr.getRxDataOctets()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
			nbr
					.setRxMgtFrames(nbr.getRxMgtFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
			nbr
					.setRxUnicastFrames(nbr.getRxUnicastFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
			nbr
					.setRxMulticastFrames(nbr.getRxMulticastFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
			nbr
					.setRxBroadcastFrames(nbr.getRxBroadcastFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;

			nbr
					.setTxDataFrames(nbr.getTxDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
			nbr
					.setTxMgtFrames(nbr.getTxMgtFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
			nbr
					.setTxDataOctets(nbr.getTxDataOctets()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
			nbr
					.setTxUnicastFrames(nbr.getTxUnicastFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
			nbr
					.setTxMulticastFrames(nbr.getTxMulticastFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
			nbr
					.setTxBroadcastFrames(nbr.getTxBroadcastFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;

			nbr
					.setTxBeDataFrames(nbr.getTxBeDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
			nbr
					.setTxBgDataFrames(nbr.getTxBgDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
			nbr
					.setTxViDataFrames(nbr.getTxViDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
			nbr
					.setTxVoDataFrames(nbr.getTxVoDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_NEIGHBOR_BASE[index_base]));
			index_base++;
		}

		bo.setRxDataFrames(nbr.getRxDataFrames());
		bo.setRxDataOctets(nbr.getRxDataOctets());
		bo.setRxMgtFrames(nbr.getRxMgtFrames());
		bo.setRxUnicastFrames(nbr.getRxUnicastFrames());
		bo.setRxMulticastFrames(nbr.getRxMulticastFrames());
		bo.setRxBroadcastFrames(nbr.getRxBroadcastFrames());

		bo.setTxDataFrames(nbr.getTxDataFrames());
		bo.setTxMgtFrames(nbr.getTxMgtFrames());
		bo.setTxDataOctets(nbr.getTxDataOctets());
		bo.setTxUnicastFrames(nbr.getTxUnicastFrames());
		bo.setTxMulticastFrames(nbr.getTxMulticastFrames());
		bo.setTxBroadcastFrames(nbr.getTxBroadcastFrames());

		bo.setTxBeDataFrames(nbr.getTxBeDataFrames());
		bo.setTxBgDataFrames(nbr.getTxBgDataFrames());
		bo.setTxViDataFrames(nbr.getTxViDataFrames());
		bo.setTxVoDataFrames(nbr.getTxVoDataFrames());

		return bo;
	}

	/**
	 * generate client mac
	 * 
	 * @param ap -
	 * @param clientList -
	 */
	private static void generateClient(HiveAp ap, List<String> clientList) {
		if (ap.getSimulateClientInfo() == null
				|| ap.getSimulateClientInfo().equalsIgnoreCase(""))
			return;
		String[] split = ap.getSimulateClientInfo().split(",");
		int clientNumber = 0;
		int remainClientNumber = 0;
		int oneClientNumber;
		int remainPercent = 100;
		int clientPercent;
		String clientType = null;
		for (int i = 0; i < split.length; i++) {
			if (0 == i) {
				clientNumber = Integer.parseInt(split[i]);
				remainClientNumber = clientNumber;
			} else if (0 != (i % 2)) {
				if (split[i].length() > 12)
					clientType = null;
				else {
					clientType = split[i];
				}
			} else {
				if (clientType != null) {
					clientPercent = Integer.parseInt(split[i]);
					remainPercent -= clientPercent;
					if (remainPercent <= 0)
						oneClientNumber = remainClientNumber;
					else
						oneClientNumber = clientNumber * clientPercent / 100;
					oneClientNumber = oneClientNumber > remainClientNumber ? remainClientNumber
							: oneClientNumber;
					remainClientNumber -= oneClientNumber;

					// generate client
					for (int j = 0; j < oneClientNumber; j++) {
						ByteBuffer buffer = ByteBuffer.allocate(100);
						buffer.put(AhEncoder.hex2bytes(clientType));
						buffer.putShort((short) (ap.getSimulateCode()));
						buffer.put((byte) j);
						buffer.flip();
						String clientMacString = AhDecoder.bytes2hex(buffer, 6);
						clientList.add(clientMacString);
					}
				}
			}
		}
	}

	/**
	 * generate client association
	 * 
	 * @param stattimestamp -
	 * @param ap -
	 * @param ahourAssociationList -
	 * @param associationList -
	 * @param clientList -
	 */
	private static void generateClientAssiciation(long stattimestamp,
			HiveAp ap, List<AhAssociation> ahourAssociationList,
			List<AhAssociation> associationList, List<String> clientList) {
		if (!ahourAssociationList.isEmpty()) {
			for (AhAssociation ass : ahourAssociationList) {
				AhAssociation bo = generateAssociation(ass, false);
				bo.getTimeStamp().setTime(stattimestamp);
				associationList.add(bo);
			}
			return;
		}
		for (String client : clientList) {
			generateOneAssociation(stattimestamp, ap, associationList,
					client);
		}

		for (AhAssociation ass : associationList) {
			ahourAssociationList.add(generateAssociation(ass, true));
		}
	}

	/**
	 * generate one row neighbor
	 * 
	 * @param stattimestamp -
	 * @param ap -
	 * @param associationList -
	 * @param mac -
	 */
	private static void generateOneAssociation(long stattimestamp, HiveAp ap,
			List<AhAssociation> associationList, String mac) {
		AhAssociation ass = new AhAssociation();

		ass.setOwner(ap.getOwner());

		ass.setApName(ap.getHostName());
		ass.setApMac(ap.getMacAddress());
		HmTimeStamp timeStamp = new HmTimeStamp(stattimestamp, ap.getOwner()
				.getTimeZoneString());
		ass.setTimeStamp(timeStamp);
		ass.setApSerialNumber(ap.getSerialNumber());

		ass.setIfIndex(5);
		ass.setClientMac(mac);
		ass
				.setClientRSSI((int) getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[0]));
		ass.setClientIP(AhDecoder
				.int2IP(0XC0A86400 + (int) (Math.random() * 10000)));
		ass.setClientLinkUptime(120000);
		ass.setClientAuthMethod((byte) (Math.random() * 10 + 1));
		ass.setClientEncryptionMethod((byte) (Math.random() * 3));
		int macProtocol = (int) (Math.random() * 5);
		macProtocol = macProtocol <= 2 ? macProtocol : (macProtocol + 3);
		ass.setClientMACProtocol((byte) macProtocol);
		ass.setClientCWPUsed((byte) (Math.random() * 2 + 1));
		ass.setClientVLAN(1);
		ass.setClientUserProfId(1);
		ass.setClientChannel(8);

		// ass.setClientLastTxRate(buf.getInt());
		// ass.setClientLastRxRate(buf.getInt());
		// ass.setClientRxDataFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientRxDataOctets(AhDecoder.int2long(buf.getInt()));
		// ass.setClientRxMgtFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientRxUnicastFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientRxMulticastFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientRxBroadcastFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientRxMICFailures(AhDecoder.int2long(buf.getInt()));
		// ass.setClientTxDataFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientTxMgtFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientTxDataOctets(AhDecoder.int2long(buf.getInt()));
		// ass.setClientTxUnicastFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientTxMulticastFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientTxBroadcastFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientIP(AhDecoder.int2IP(buf.getInt()));
		// ass.setClientTxBeDataFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientTxBgDataFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientTxViDataFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientTxVoDataFrames(AhDecoder.int2long(buf.getInt()));
		// ass.setClientRxAirtime(AhDecoder.long2double(buf.getLong()) / 1000);
		// ass.setClientTxAirtime(AhDecoder.long2double(buf.getLong()) / 1000);

		ass.setClientHostname("host name");
		ass.setClientSSID("ssid1");
		ass.setClientUsername("user1");
		byte[] bssid = AhEncoder.hex2bytes(ap.getMacAddress());
		bssid[bssid.length - 1] -= 2;
		ass.setClientBSSID(AhDecoder.bytes2hex(bssid));
		ass.setClientAssociateTime((long) ((stattimestamp - 120000 - Math
				.random() * 1000000) / 1000));

		associationList.add(ass);
	}

	/**
	 * clone association
	 * 
	 * @param ass -
	 * @param onlyCopy -
	 * @return -
	 */
	private static AhAssociation generateAssociation(AhAssociation ass,
			boolean onlyCopy) {
		AhAssociation bo = new AhAssociation();

		bo.setOwner(ass.getOwner());

		HmTimeStamp timeStamp = new HmTimeStamp(ass.getTimeStamp().getTime(),
				ass.getTimeStamp().getTimeZone());

		bo.setApName(ass.getApName());
		bo.setApMac(ass.getApMac());
		bo.setTimeStamp(timeStamp);
		bo.setApSerialNumber(ass.getApSerialNumber());

		bo.setIfIndex(ass.getIfIndex());
		bo.setClientMac(ass.getClientMac());
		bo.setClientAuthMethod(ass.getClientAuthMethod());
		bo.setClientEncryptionMethod(ass.getClientEncryptionMethod());
		bo.setClientMACProtocol(ass.getClientMACProtocol());
		bo.setClientCWPUsed(ass.getClientCWPUsed());
		bo.setClientVLAN(ass.getClientVLAN());
		bo.setClientUserProfId(ass.getClientUserProfId());
		bo.setClientChannel(ass.getClientChannel());
		bo.setClientIP(ass.getClientIP());

		if (!onlyCopy) {
			// get the next value
			ass.setClientLinkUptime(ass.getClientLinkUptime() + 3600000);

			int index_base = 0;
			ass
					.setClientRSSI((int) getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientLastTxRate((int) getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientLastRxRate((int) getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientRxDataFrames(ass.getClientRxDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientRxDataOctets(ass.getClientRxDataOctets()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientRxMgtFrames(ass.getClientRxMgtFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientRxUnicastFrames(ass.getClientRxUnicastFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientRxMulticastFrames(ass
							.getClientRxMulticastFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientRxBroadcastFrames(ass
							.getClientRxBroadcastFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientRxMICFailures(ass.getClientRxMICFailures()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientTxDataFrames(ass.getClientTxDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientTxMgtFrames(ass.getClientTxMgtFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientTxDataOctets(ass.getClientTxDataOctets()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientTxUnicastFrames(ass.getClientTxUnicastFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientTxMulticastFrames(ass
							.getClientTxMulticastFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientTxBroadcastFrames(ass
							.getClientTxBroadcastFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientTxBeDataFrames(ass.getClientTxBeDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientTxBgDataFrames(ass.getClientTxBgDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientTxViDataFrames(ass.getClientTxViDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientTxVoDataFrames(ass.getClientTxVoDataFrames()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientRxAirtime(ass.getClientRxAirtime()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
			ass
					.setClientTxAirtime(ass.getClientTxAirtime()
							+ getRandom(SimulateConstant.SIMULATE_ASSOCIATION_BASE[index_base]));
			index_base++;
		}

		bo.setClientLinkUptime(ass.getClientLinkUptime());
		bo.setClientRSSI(ass.getClientRSSI());
		bo.setClientLastTxRate(ass.getClientLastTxRate());
		bo.setClientLastRxRate(ass.getClientLastRxRate());
		bo.setClientRxDataFrames(ass.getClientRxDataFrames());
		bo.setClientRxDataOctets(ass.getClientRxDataOctets());
		bo.setClientRxMgtFrames(ass.getClientRxMgtFrames());
		bo.setClientRxUnicastFrames(ass.getClientRxUnicastFrames());
		bo.setClientRxMulticastFrames(ass.getClientRxMulticastFrames());
		bo.setClientRxBroadcastFrames(ass.getClientRxBroadcastFrames());
		bo.setClientRxMICFailures(ass.getClientRxMICFailures());
		bo.setClientTxDataFrames(ass.getClientTxDataFrames());
		bo.setClientTxMgtFrames(ass.getClientTxMgtFrames());
		bo.setClientTxDataOctets(ass.getClientTxDataOctets());
		bo.setClientTxUnicastFrames(ass.getClientTxUnicastFrames());
		bo.setClientTxMulticastFrames(ass.getClientTxMulticastFrames());
		bo.setClientTxBroadcastFrames(ass.getClientTxBroadcastFrames());
		bo.setClientTxBeDataFrames(ass.getClientTxBeDataFrames());
		bo.setClientTxBgDataFrames(ass.getClientTxBgDataFrames());
		bo.setClientTxViDataFrames(ass.getClientTxViDataFrames());
		bo.setClientTxVoDataFrames(ass.getClientTxVoDataFrames());
		bo.setClientRxAirtime(ass.getClientRxAirtime());
		bo.setClientTxAirtime(ass.getClientTxAirtime());

		bo.setClientHostname(ass.getClientHostname());
		bo.setClientSSID(ass.getClientSSID());
		bo.setClientUsername(ass.getClientUsername());
		bo.setClientBSSID(ass.getClientBSSID());
		bo.setClientAssociateTime(ass.getClientAssociateTime());

		return bo;
	}

	private static void generateClientSession(HiveAp ap,
			List<AhClientSession> clientSessionList,
			List<AhAssociation> associationList) {
		for (AhAssociation ass : associationList) {
			generateOneClientSession(ap, ass, clientSessionList);
		}
	}
	
	private static void generateClientBandWidthSentinel(HiveAp ap,
			List<AhBandWidthSentinelHistory> sentinelList,
			List<AhAssociation> associationList) {
		for (AhAssociation ass : associationList) {
			if(Math.random()*10 >= 7)
				generateOneBandWidthSentinel(ap, ass, sentinelList);
		}
	}

	private static void generateOneClientSession(HiveAp ap, AhAssociation ass,
			List<AhClientSession> clientSessionList) {
		AhClientSession bo = new AhClientSession();
		bo.setApMac(ass.getApMac());
		bo.setApName(ass.getApName());
		bo.setClientMac(ass.getClientMac());
		bo.setIfIndex(ass.getIfIndex());
		bo.setMemo("");
		bo.setStartTimeStamp(ass.getClientAssociateTime() * 1000);
		bo.setStartTimeZone(ass.getTimeStamp().getTimeZone());
		bo.setClientIP(ass.getClientIP());
		bo.setClientUsername(ass.getClientUsername());
		bo.setApSerialNumber(ap.getSerialNumber());
		if (ap.getMapContainer() != null)
			bo.setMapId(ap.getMapContainer().getId());
		else
			bo.setMapId(null);
		bo.setOwner(ap.getOwner());
		bo.setComment1("");
		bo.setComment2("");

		bo.setClientHostname(ass.getClientHostname());
		bo.setClientUsername(ass.getClientUsername());
		bo.setClientSSID(ass.getClientSSID());
		bo.setClientCWPUsed(ass.getClientCWPUsed());
		bo.setClientAuthMethod(ass.getClientAuthMethod());
		bo.setClientEncryptionMethod(ass.getClientEncryptionMethod());
		bo.setClientMACProtocol(ass.getClientMACProtocol());
		bo.setClientVLAN(ass.getClientVLAN());
		bo.setClientUserProfId(ass.getClientUserProfId());
		bo.setClientChannel(ass.getClientChannel());
		bo.setClientBSSID(ass.getClientBSSID());
		long now = System.currentTimeMillis();
		bo.setEndTimeStamp((long)(now-Math.random()*(now/3600000)));
		bo.setConnectstate(AhClientSession.CONNECT_STATE_DOWN);
		bo.setEndTimeZone(ass.getTimeStamp().getTimeZone());

		clientSessionList.add(bo);
	}

	private static void generateOneBandWidthSentinel(HiveAp ap, AhAssociation ass,
			List<AhBandWidthSentinelHistory> sentinelList) {
		AhBandWidthSentinelHistory bo = new AhBandWidthSentinelHistory();
		bo.setApMac(ass.getApMac());
		bo.setApName(ass.getApName());
		bo.setClientMac(ass.getClientMac());	
		bo.setIfIndex(ass.getIfIndex());
		int status = ((int)(Math.random()*10))%3;
		bo.setBandWidthSentinelStatus(status == 1 ? 0:status);
		bo.setGuaranteedBandWidth(100);
		switch(bo.getBandWidthSentinelStatus())
		{
		case AhBandWidthSentinelHistory.STATUS_ALERT:
		case AhBandWidthSentinelHistory.STATUS_BAD:
			bo.setActualBandWidth(bo.getGuaranteedBandWidth()-(int)(Math.random()*50));
			bo.setAction((int)(Math.random()*2+1));
			break;
		case AhBandWidthSentinelHistory.STATUS_CLEAR:
			bo.setActualBandWidth(bo.getGuaranteedBandWidth()+50);
			bo.setAction(0);
			break;
		}
		
		bo.setTimeStamp(new HmTimeStamp(ass.getTimeStamp().getTime(),ass.getTimeStamp().getTimeZone()));
		bo.setOwner(ap.getOwner());
		
		sentinelList.add(bo);
	}
	
	public static void main(String[] argv) {
		try {
			for (int i = 0; i < 1; i++) {
				QueryUtil
						.executeNativeUpdate("truncate table hm_xif;truncate table hm_vifstats;truncate table hm_radioattribute;truncate table hm_radiostats;truncate table hm_neighbor;truncate table HM_INTERFERENCESTATS;truncate table hm_association;");
				long begin = System.currentTimeMillis();
				HiveAp ap = new HiveAp();
				ap.setHostName("AH-FFFF80");
				ap.setSerialNumber("sf");
				ap.setMacAddress("001977FFFF80");
				HmDomain owner = new HmDomain();
				owner.setId(2L);
				owner.setVersion(new Timestamp(0));
				owner.setTimeZone("GMT+8");
				ap.setOwner(owner);
				ap.setHiveApModel(HiveAp.HIVEAP_MODEL_110);
				ap
						.setSimulateClientInfo("10,0016CB,20,0019B9,30,001DB3,30,0012FE,20");
				ap.setSimulated(true);
				ap.setSimulateCode(100);
				SimulatePerformance.generatePerformance(ap);
				System.out.println("Eclipse time:"
						+ (System.currentTimeMillis() - begin));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}