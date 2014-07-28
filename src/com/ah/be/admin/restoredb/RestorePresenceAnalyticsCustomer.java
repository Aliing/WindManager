package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.report.PresenceAnalyticsCustomer;

public class RestorePresenceAnalyticsCustomer {

	private static final String table = "presence_analytics_customer";

	public static boolean restorePresenceAnalyticsCustomers() {
		try {
			long start = System.currentTimeMillis();
			List<PresenceAnalyticsCustomer> list = getAllCustomers();
			int count = 0;
			if (null != list && !list.isEmpty()) {
				count = list.size();
				QueryUtil.restoreBulkCreateBos(list);
			}
			long end = System.currentTimeMillis();
			AhRestoreDBTools
					.logRestoreMsg("Restore Presence analytics customer finished, count:"
							+ count + ". cost:" + (end - start) + " ms.");
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(
					"Restore Presence analytics customer catch exception ", e);
			return false;
		}
		return true;
	}

	private static List<PresenceAnalyticsCustomer> getAllCustomers()
			throws AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of presence_analytics_customer.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(table);
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read presence_analytics_customer.xml file.");
			return null;
		}

		/**
		 * No one row data stored in presence_analytics_customer table is
		 * allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<PresenceAnalyticsCustomer> configs = new ArrayList<PresenceAnalyticsCustomer>();

		boolean isColPresent;
		String colName;
		PresenceAnalyticsCustomer config;
		for (int i = 0; i < rowCount; i++) {
			try {
				config = new PresenceAnalyticsCustomer();

				/**
				 * Set customerId
				 */
				colName = "customerId";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						table, colName);
				String customerId = (isColPresent ? xmlParser.getColVal(i,
						colName) : "");
				if (customerId == null || customerId.trim().equals("")
						|| customerId.trim().equalsIgnoreCase("null")) {
					continue;
				}
				config.setCustomerId(customerId);
				
				/**
				 * createAt
				 */
				colName = "createAt";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						table, colName);
				String createAt = (isColPresent ? xmlParser.getColVal(i,
						colName) : "");
				if (createAt == null || createAt.trim().equals("")
						|| createAt.trim().equalsIgnoreCase("null")) {
					continue;
				}
				config.setCreateAt(createAt);

				/**
				 * Set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						table, colName);
				long ownerId = isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 1;

				if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
					continue;
				}
				config.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
				configs.add(config);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg(
						"get presence analytics cusotmer error", e);
			}
		}
		return configs.size() > 0 ? configs : null;
	}

}
