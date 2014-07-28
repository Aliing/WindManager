package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhRouterLTEVZInfo;

public class RestoreAhRouterLTEVZInfo {
	public static final String ahRouterLTEVZInfoTableName = "HM_ROUTER_LTE_VZ_INFO";

	public static boolean restoreAhRouterLTEVZInfo() {
		try {
			List<AhRouterLTEVZInfo> ahRouterLTEVZInfoList = getAllAhRouterLTEVZInfo();
			if (null != ahRouterLTEVZInfoList) {
				QueryUtil.restoreBulkCreateBos(ahRouterLTEVZInfoList);
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	public static List<AhRouterLTEVZInfo> getAllAhRouterLTEVZInfo()
			throws AhRestoreColNotExistException, AhRestoreException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of HM_ROUTER_LTE_VZ_INFO.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(ahRouterLTEVZInfoTableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in HM_ROUTER_LTE_VZ_INFO table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		boolean isColPresent;
		String colName;
		AhRouterLTEVZInfo ahRouterLTEVZInfo;
		List<AhRouterLTEVZInfo> ahRouterLTEVZInfoList = new ArrayList<AhRouterLTEVZInfo>();
		for (int i = 0; i < rowCount; i++) {
			ahRouterLTEVZInfo = new AhRouterLTEVZInfo();

			/**
			 * Set mac
			 */
			colName = "mac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'mac' column is not exist.");
				continue;
			}
			String mac = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setMac(mac);

			/**
			 * Set networkMode
			 */
			colName = "networkMode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'networkMode' column is not exist.");
				continue;
			}
			String networkMode = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setNetworkMode(networkMode.getBytes()[0]);

			/**
			 * Set connectStatus
			 */
			colName = "connectStatus";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'connectStatus' column is not exist.");
				continue;
			}
			String connectStatus = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setConnectStatus(connectStatus.getBytes()[0]);

			/**
			 * Set rssi
			 */
			colName = "rssi";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'rssi' column is not exist.");
				continue;
			}
			String rssi = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setRssi(rssi.getBytes()[0]);

			/**
			 * Set rssi
			 */
			colName = "rsrq";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'rsrq' column is not exist.");
				continue;
			}
			String rsrq = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setRsrq(rsrq.getBytes()[0]);

			/**
			 * Set rsrp
			 */
			colName = "rsrp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'rsrp' column is not exist.");
				continue;
			}
			String rsrp = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setRsrp(rsrp.getBytes()[0]);

			/**
			 * Set bars
			 */
			colName = "bars";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'bars' column is not exist.");
				continue;
			}
			String bars = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setBars(bars.getBytes()[0]);

			/**
			 * Set modemFlag
			 */
			colName = "modemFlag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'modemFlag' column is not exist.");
				continue;
			}
			String modemFlag = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setModemFlag(modemFlag.getBytes()[0]);

			/**
			 * Set interfaceName
			 */
			colName = "interfaceName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'interfaceName' column is not exist.");
				continue;
			}
			String interfaceName = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setInterfaceName(interfaceName);

			/**
			 * Set firmwareVersion
			 */
			colName = "firmwareVersion";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'firmwareVersion' column is not exist.");
				continue;
			}
			String firmwareVersion = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setFirmwareVersion(firmwareVersion);

			/**
			 * Set manufacture
			 */
			colName = "manufacture";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'manufacture' column is not exist.");
				continue;
			}
			String manufacture = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setManufacture(manufacture);
			/**
			 * Set hardwareID
			 */
			colName = "hardwareID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'hardwareID' column is not exist.");
				continue;
			}
			String hardwareID = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setHardwareID(hardwareID);

			/**
			 * Set simIccid
			 */
			colName = "simIccid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'simIccid' column is not exist.");
				continue;
			}
			String simIccid = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setSimIccid(simIccid);

			/**
			 * Set imei
			 */
			colName = "imei";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'imei' column is not exist.");
				continue;
			}
			String imei = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setImei(imei);

			/**
			 * Set carrier
			 */
			colName = "carrier";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'carrier' column is not exist.");
				continue;
			}
			String carrier = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setCarrier(carrier);

			/**
			 * Set cellID
			 */
			colName = "cellID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'cellID' column is not exist.");
				continue;
			}
			String cellID = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setCellID(cellID);

			/**
			 * Set systemMode
			 */
			colName = "systemMode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'systemMode' column is not exist.");
				continue;
			}
			String systemMode = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setSystemMode(systemMode);

			/**
			 * Set simStatus
			 */
			colName = "simStatus";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'simStatus' column is not exist.");
				continue;
			}
			String simStatus = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setSimStatus(simStatus);

			/**
			 * Set modemMode
			 */
			colName = "modemMode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'simStatus' column is not exist.");
				continue;
			}
			String modemMode = xmlParser.getColVal(i, colName);
			ahRouterLTEVZInfo.setModemMode(modemMode);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ahRouterLTEVZInfoTableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'HM_ROUTER_LTE_VZ_INFO' data be lost, cause: 'owner' column is not available");
				continue;
			}
			ahRouterLTEVZInfo.setOwner(AhRestoreNewMapTools
					.getHmDomain(ownerId));
			ahRouterLTEVZInfoList.add(ahRouterLTEVZInfo);
		}
		return ahRouterLTEVZInfoList;
	}
}
