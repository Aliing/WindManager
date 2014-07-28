package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.PlannedAP;

public class RestorePlannedAP {

	public static final String tableName = "planned_ap";

	private static List<PlannedAP> getAllPlannedAPs()
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of planned_ap.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in planned_ap table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<PlannedAP> plannedAPs = new ArrayList<PlannedAP>();
		boolean isColPresent;
		String colName;
		PlannedAP ap;

		for (int i = 0; i < rowCount; i++) {
			ap = new PlannedAP();

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'planned_ap' data be lost, cause: 'id' column is not exist.");
				/**
				 * The id column must be exist in the table.
				 */
				continue;
			}

			/**
			 * Set apmodel
			 */
			colName = "apmodel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String apmodel = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			ap.apModel = (short) AhRestoreCommons.convertInt(apmodel);

			/**
			 * Set countrycode
			 */
			colName = "countrycode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String countrycode = isColPresent ? xmlParser.getColVal(i, colName)
					: "840";
			ap.countryCode = AhRestoreCommons.convertInt(countrycode);

			/**
			 * Set hostName
			 */
			colName = "hostName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String hostName = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			ap.hostName = AhRestoreCommons.convertString(hostName);

			/**
			 * Set wifi0channel
			 */
			colName = "wifi0channel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String wifi0channel = isColPresent ? xmlParser
					.getColVal(i, colName) : "0";
			ap.wifi0Channel = (short) AhRestoreCommons.convertInt(wifi0channel);

			/**
			 * Set wifi0channelwidth
			 */
			colName = "wifi0channelwidth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String wifi0channelwidth = isColPresent ? xmlParser.getColVal(i,
					colName) : "0";
			ap.wifi0ChannelWidth = (short) AhRestoreCommons
					.convertInt(wifi0channelwidth);

			/**
			 * Set wifi0enabled
			 */
			colName = "wifi0enabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String wifi0enabled = isColPresent ? xmlParser
					.getColVal(i, colName) : "true";
			ap.wifi0Enabled = AhRestoreCommons
					.convertStringToBoolean(wifi0enabled);

			/**
			 * Set wifi0power
			 */
			colName = "wifi0power";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String wifi0power = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			ap.wifi0Power = (short) AhRestoreCommons.convertInt(wifi0power);

			/**
			 * Set wifi1channel
			 */
			colName = "wifi1channel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String wifi1channel = isColPresent ? xmlParser
					.getColVal(i, colName) : "0";
			ap.wifi1Channel = (short) AhRestoreCommons.convertInt(wifi1channel);

			/**
			 * Set wifi1channelwidth
			 */
			colName = "wifi1channelwidth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String wifi1channelwidth = isColPresent ? xmlParser.getColVal(i,
					colName) : "0";
			ap.wifi1ChannelWidth = (short) AhRestoreCommons
					.convertInt(wifi1channelwidth);

			/**
			 * Set wifi1enabled
			 */
			colName = "wifi1enabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String wifi1enabled = isColPresent ? xmlParser
					.getColVal(i, colName) : "true";
			ap.wifi1Enabled = AhRestoreCommons
					.convertStringToBoolean(wifi1enabled);

			/**
			 * Set wifi1power
			 */
			colName = "wifi1power";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String wifi1power = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			ap.wifi1Power = (short) AhRestoreCommons.convertInt(wifi1power);

			/**
			 * Set x
			 */
			colName = "x";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String x = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ap.x = AhRestoreCommons.convertDouble(x);

			/**
			 * Set y
			 */
			colName = "y";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String y = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ap.y = AhRestoreCommons.convertDouble(y);

			/**
			 * Set parent_map_id
			 */
			colName = "parent_map_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String parent_map_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (parent_map_id != null && !(parent_map_id.trim().equals(""))
					&& !(parent_map_id.trim().equalsIgnoreCase("null"))) {
				Long parent_map_id_new = AhRestoreNewMapTools
						.getMapMapContainer(AhRestoreCommons
								.convertLong(parent_map_id));
				if (null != parent_map_id_new) {
					ap.setParentMap(AhRestoreNewTools.CreateBoWithId(
							MapContainerNode.class, parent_map_id_new));
				}
			}
			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'planned_ap' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			ap.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			plannedAPs.add(ap);
		}
		return plannedAPs;
	}

	public static boolean restorePlannedAP() {
		try {
			long start = System.currentTimeMillis();

			List<PlannedAP> allPlannedAPs = getAllPlannedAPs();
			List<PlannedAP> validAPs = new ArrayList<PlannedAP>();

			if (null == allPlannedAPs) {
				AhRestoreDBTools.logRestoreMsg("Planned AP is null");
			} else {
				AhRestoreDBTools.logRestoreMsg("Readed Planned AP size:"
						+ allPlannedAPs.size());
				for (int i = 0; i < allPlannedAPs.size(); i++) {
					PlannedAP ap = allPlannedAPs.get(i);
					if (ap != null) {
						// get parent map
						MapContainerNode map = ap.getParentMap();
						if (null != map) {
							validAPs.add(ap);
						}
					}
				}
				if (!validAPs.isEmpty()) {
					QueryUtil.restoreBulkCreateBos(validAPs);
				}
			}
			long end = System.currentTimeMillis();
			AhRestoreDBTools
					.logRestoreMsg("Restore PlannedAP completely. Count:"
							+ validAPs.size() + ", cost:" + (end - start)
							+ " ms.");
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore Planned AP error.", e);
			return false;
		}
		return true;
	}
}
