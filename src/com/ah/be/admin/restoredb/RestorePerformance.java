/**
 *@filename		RestoreConfigTemplate.java
 *@version
 *@author		Fisher
 *@createtime	2007-11-7 PM 06:55:18
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.log.BeLogTools;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.dashboard.AhDashBoardGroup;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.dashboard.AhDashboardLayout;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.performance.ActiveClientFilter;
import com.ah.bo.performance.AhClientEditValues;
import com.ah.bo.performance.AhCustomReport;
import com.ah.bo.performance.AhCustomReportField;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhNewReport;
import com.ah.bo.performance.AhReport;
import com.ah.bo.performance.AhSummaryPage;
import com.ah.bo.performance.AhUserReport;

/**
 * @author Fisher
 * @version V1.0.0.0
 */
public class RestorePerformance {

	private static final int RECORDS_NUMBER_ONE_BATCH = 1000;

	public static void restoreNeighborExt() {
		String tableName = "hm_neighbor";
		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			GetColumnValueInterface defaultValue = new GetColumnValueInterface() {
				public String getValue(String columnName, String value, AhRestoreGetXML xmlParser,
						int row) {
					if (columnName.equalsIgnoreCase("time")) {

						String statTime;
						try {
							statTime = xmlParser.getColVal(row, "stattime");
						} catch (Exception e) {
							statTime = null;
						}

						if (null == statTime)
							return String.valueOf(System.currentTimeMillis());
						else {
							Date stat = AhRestoreCommons.convertDate(statTime);
							return String.valueOf(stat.getTime());
						}
					} else if (columnName.equalsIgnoreCase("timezone")) {
						long lID;
						try {
							lID = AhRestoreCommons.convertLong(xmlParser.getColVal(row, "owner"));
						} catch (AhRestoreException e) {
							return "America/Los_Angeles";
						} catch (AhRestoreColNotExistException e) {
							return "America/Los_Angeles";
						}
						HmDomain oDomain = AhRestoreNewMapTools.getHmDomain(lID);
						if (oDomain==null) {
							return "America/Los_Angeles";
						}
						return oDomain.getTimeZoneString();
					} else {
						return "";
					}
				}
			};

			// change default column info by name
			aa.setColumnInfoByName("time", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, defaultValue);
			aa.setColumnInfoByName("timezone", AhConvertXMLToCSV.COLUMN_TYPE_STRING, defaultValue);
			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(tableName, tableName);
		}
	}

	public static void restoreAssociationExt() {
		String tableName = "hm_association";
		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			GetColumnValueInterface defaultValue = new GetColumnValueInterface() {
				public String getValue(String columnName, String value, AhRestoreGetXML xmlParser,
						int row) {
					if ( columnName.equalsIgnoreCase("clientrssi")) {
						try {
							boolean exist = true;
							exist = xmlParser.checkColExist("snr");
							if(exist) {
								return xmlParser.getColVal(row, "clientrssi");
							} else {
								int rssi = AhRestoreCommons.convertInt(xmlParser.getColVal(row, "clientrssi")) - 95;
								return String.valueOf(rssi);
							}
						} catch (Exception e) {
							return "0";
						}
					} else if (columnName.equalsIgnoreCase("time")) {
						String statTime;
						try {
							statTime = xmlParser.getColVal(row, "stattime");
						} catch (Exception e) {
							statTime = null;
						}

						if (null == statTime)
							return String.valueOf(System.currentTimeMillis());
						else {
							Date stat = AhRestoreCommons.convertDate(statTime);
							return String.valueOf(stat.getTime());
						}
					} else if (columnName.equalsIgnoreCase("timezone")) {
						long lID;
						try {
							lID = AhRestoreCommons.convertLong(xmlParser.getColVal(row, "owner"));
						} catch (AhRestoreException e) {
							return "America/Los_Angeles";
						} catch (AhRestoreColNotExistException e) {
							return "America/Los_Angeles";
						}
						HmDomain oDomain = AhRestoreNewMapTools.getHmDomain(lID);
						if (oDomain==null) {
							return "America/Los_Angeles";
						}
						return oDomain.getTimeZoneString();
					} else if ( columnName.equalsIgnoreCase("snr")) {
						try {
							return xmlParser.getColVal(row, "clientrssi");
						} catch (Exception e) {
							return "0";
						}
					} else {
						return "";
					}
				}
			};

			// change default column info by name
			aa.setColumnInfoByName("time", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, defaultValue);
			aa.setColumnInfoByName("timezone", AhConvertXMLToCSV.COLUMN_TYPE_STRING, defaultValue);
			aa.setColumnInfoByName("clientrssi", AhConvertXMLToCSV.COLUMN_TYPE_OTHER_USE_DEFAULT, defaultValue);
			aa.setColumnInfoByName("snr", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, defaultValue);
			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(tableName, tableName);
		}
	}

	public static void restorePCIData() {
		AhDBRestoreTool.restoreTable("hm_pcidata");
	}

	public static void restoreAhMaxClientCountData() {
		AhDBRestoreTool.restoreTable("max_clients_count");
	}

	public static void restoreAhSsidClientCountData() {
		AhDBRestoreTool.restoreTable("ssid_clients_count");
		AhDBRestoreTool.restoreTable("ssid_clients_count_hour");
		AhDBRestoreTool.restoreTable("ssid_clients_count_day");
		AhDBRestoreTool.restoreTable("ssid_clients_count_week");
	}

	public static void restoreAhClientOsInfoCountData() {
		AhDBRestoreTool.restoreTable("clients_osinfo_count");
		//AhDBRestoreTool.restoreTable("clients_osinfo_count_hour");
		//AhDBRestoreTool.restoreTable("clients_osinfo_count_day");
		//AhDBRestoreTool.restoreTable("clients_osinfo_count_week");
	}
	
	public static void restoreAhDeviceRebootHistoryCountData() {
		AhDBRestoreTool.restoreTable("hm_device_reboot_history");
	}

	public static void restoreAhSLAStats() {
		AhDBRestoreTool.restoreTable("ah_sla_stats");
	}

	public static void restoreApConnectionHistory() {
		String tableName = "ap_connect_history_info";
		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			GetColumnValueInterface defaultValue = new GetColumnValueInterface() {
				public String getValue(String columnName, String value, AhRestoreGetXML xmlParser,
						int row) {
					if (columnName.equalsIgnoreCase("mapid")) {
						if (value == null || value.trim().length() == 0
								|| value.equalsIgnoreCase("null")) {
							return "";
						}
						Long newMapContainerId = AhRestoreNewMapTools.getMapMapContainer(Long.parseLong(value.trim()));
						if (newMapContainerId!=null) {
							return newMapContainerId.toString();
						}
						return "";
					} else {
						return "";
					}
				}
			};

			aa.setColumnInfoByName("mapid", AhConvertXMLToCSV.COLUMN_TYPE_OTHER_USE_DEFAULT,
					defaultValue);

			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(tableName, tableName);
		}
	}

	/**
	 * retore history client data using copy csv into db
	 */
	public static void restoreHistoryClientExt() {
		String fileName = "ah_clientsession_history";
		if (!AhRestoreGetXML.checkXMLFileExist(fileName)) {
			fileName = "hm_historyclientsession";
			if (!AhRestoreGetXML.checkXMLFileExist(fileName)) {
				fileName = "ah_clientsession";
			}
		}
		String tableName = "ah_clientsession_history";

		GetColumnValueInterface defaultValue = new GetColumnValueInterface() {
			public String getValue(String columnName, String value, AhRestoreGetXML xmlParser,
					int row) {

				if (columnName.equalsIgnoreCase("starttimezone")) {
					long lID;
					try {
						lID = AhRestoreCommons.convertLong(xmlParser.getColVal(row, "owner"));
					} catch (AhRestoreException e) {
						return "America/Los_Angeles";
					} catch (AhRestoreColNotExistException e) {
						return "America/Los_Angeles";
					}
					HmDomain oDomain = AhRestoreNewMapTools.getHmDomain(lID);
					if (oDomain==null) {
						return "America/Los_Angeles";
					}
					return oDomain.getTimeZoneString();
				} else if (columnName.equalsIgnoreCase("endtimezone")) {
					long lID;
					try {
						lID = AhRestoreCommons.convertLong(xmlParser.getColVal(row, "owner"));
					} catch (AhRestoreException e) {
						return "America/Los_Angeles";
					} catch (AhRestoreColNotExistException e) {
						return "America/Los_Angeles";
					}
					HmDomain oDomain = AhRestoreNewMapTools.getHmDomain(lID);
					if (oDomain==null) {
						return "America/Los_Angeles";
					}
					return oDomain.getTimeZoneString();
				} else if (columnName.equalsIgnoreCase("starttimestamp")) {
					String starttime;
					try {
						starttime = xmlParser.getColVal(row, "starttime");
					} catch (Exception e) {
						starttime = null;
					}

					if (null == starttime)
						return String.valueOf(System.currentTimeMillis());
					else {
						return String.valueOf(AhRestoreCommons.convertDate(starttime).getTime());
					}
				} else if (columnName.equalsIgnoreCase("endtimestamp")) {
					String endtime;
					try {
						endtime = xmlParser.getColVal(row, "endtime");
					} catch (Exception e) {
						endtime = null;
					}

					if (null == endtime)
						return String.valueOf(System.currentTimeMillis());
					else {
						return String.valueOf(AhRestoreCommons.convertDate(endtime).getTime());
					}
				} else if (columnName.equalsIgnoreCase("mapid")) {
					if (value == null || value.trim().length() == 0
							|| value.equalsIgnoreCase("null")) {
						return "";
					}
					Long newMapContainerId = AhRestoreNewMapTools.getMapMapContainer(Long.parseLong(value.trim()));
					if (newMapContainerId!=null) {
						return newMapContainerId.toString();
					}
					return "";
				} else {
					return "";
				}
			}
		};

		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			// change default column info by name
			aa.setColumnInfoByName("clientcwpused", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, "2");
			aa.setColumnInfoByName("clientmacprotocol", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, "1");
			aa.setColumnInfoByName("mapid", AhConvertXMLToCSV.COLUMN_TYPE_OTHER_USE_DEFAULT,
					defaultValue);
			aa.setColumnInfoByName("starttimestamp", AhConvertXMLToCSV.COLUMN_TYPE_OTHER,
					defaultValue);
			aa.setColumnInfoByName("endtimestamp", AhConvertXMLToCSV.COLUMN_TYPE_OTHER,
					defaultValue);
			aa.setColumnInfoByName("starttimezone", AhConvertXMLToCSV.COLUMN_TYPE_OTHER,
					defaultValue);
			aa
					.setColumnInfoByName("endtimezone", AhConvertXMLToCSV.COLUMN_TYPE_OTHER,
							defaultValue);
			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(fileName, tableName);
		}
		//migrate data into data retention report table
//				String dataRetentionUserNameInfo = "user_name_info";
//				String dataRetentionUserNameSeen = "user_name_seen";
//				String dataRetentionUserNameDevices = "user_name_devices";
//				String dataRetentionClientDeviceInfo = "client_device_info";
//				String dataRetentionClientDeviceSeen = "client_device_seen";
//				
//				if (AhRestoreGetXML.checkXMLFileExist(fileName) && 
//						(!AhRestoreGetXML.checkXMLFileExist(dataRetentionUserNameInfo) &&  
//						 !AhRestoreGetXML.checkXMLFileExist(dataRetentionUserNameSeen) &&  
//						 !AhRestoreGetXML.checkXMLFileExist(dataRetentionUserNameDevices) &&  
//						 !AhRestoreGetXML.checkXMLFileExist(dataRetentionClientDeviceInfo) &&  
//						 !AhRestoreGetXML.checkXMLFileExist(dataRetentionClientDeviceSeen))
//						&& MigrateDataFromClientSessionHistory.checkDBTableExist(dataRetentionUserNameInfo)) {
//					MigrateDataFromClientSessionHistory.migrateClientSessionIntoClientReport();
//				}
	}

	public static void restoreRadioStatsExt() {
		String tableName = "hm_radiostats";
		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			GetColumnValueInterface defaultValue = new GetColumnValueInterface() {
				public String getValue(String columnName, String value, AhRestoreGetXML xmlParser,
						int row) {
					if (columnName.equalsIgnoreCase("stattimestamp")) {
						String statTime;
						try {
							statTime = xmlParser.getColVal(row, "stattime");
						} catch (Exception e) {
							statTime = null;
						}
						if (null == statTime)
							return String.valueOf(System.currentTimeMillis());
						else {
							Date stat = AhRestoreCommons.convertDate(statTime);
							return String.valueOf(stat.getTime());
						}
					} else if (columnName.equalsIgnoreCase("timezone")) {
						long lID;
						try {
							lID = AhRestoreCommons.convertLong(xmlParser.getColVal(row, "owner"));
						} catch (AhRestoreException e) {
							return "America/Los_Angeles";
						} catch (AhRestoreColNotExistException e) {
							return "America/Los_Angeles";
						}
						HmDomain oDomain = AhRestoreNewMapTools.getHmDomain(lID);
						if (oDomain==null) {
							return "America/Los_Angeles";
						}
						return oDomain.getTimeZoneString();
					} else {
						return "";
					}
				}
			};

			// change default column info by name
			aa.setColumnInfoByName("stattimestamp", AhConvertXMLToCSV.COLUMN_TYPE_OTHER,
					defaultValue);
			aa.setColumnInfoByName("timezone", AhConvertXMLToCSV.COLUMN_TYPE_STRING, defaultValue);
			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(tableName, tableName);
		}
	}

	public static void restoreVifStatsExt() {
		String tableName = "hm_vifstats";
		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			GetColumnValueInterface defaultValue = new GetColumnValueInterface() {
				public String getValue(String columnName, String value, AhRestoreGetXML xmlParser,
						int row) {
					if (columnName.equalsIgnoreCase("stattimestamp")) {
						String statTime;
						try {
							statTime = xmlParser.getColVal(row, "stattime");
						} catch (Exception e) {
							statTime = null;
						}
						if (null == statTime)
							return String.valueOf(System.currentTimeMillis());
						else {
							Date stat = AhRestoreCommons.convertDate(statTime);
							return String.valueOf(stat.getTime());
						}
					} else if (columnName.equalsIgnoreCase("timezone")) {
						long lID;
						try {
							lID = AhRestoreCommons.convertLong(xmlParser.getColVal(row, "owner"));
						} catch (AhRestoreException e) {
							return "America/Los_Angeles";
						} catch (AhRestoreColNotExistException e) {
							return "America/Los_Angeles";
						}
						HmDomain oDomain = AhRestoreNewMapTools.getHmDomain(lID);
						if (oDomain==null) {
							return "America/Los_Angeles";
						}
						return oDomain.getTimeZoneString();
					} else {
						return "";
					}
				}
			};

			// change default column info by name
			aa.setColumnInfoByName("stattimestamp", AhConvertXMLToCSV.COLUMN_TYPE_OTHER,
					defaultValue);
			aa.setColumnInfoByName("timezone", AhConvertXMLToCSV.COLUMN_TYPE_STRING, defaultValue);
			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(tableName, tableName);
		}
	}

	public static void restoreRadioAttributeExt() {
		String tableName = "hm_radioattribute";
		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			GetColumnValueInterface defaultValue = new GetColumnValueInterface() {
				public String getValue(String columnName, String value, AhRestoreGetXML xmlParser,
						int row) {
					if (columnName.equalsIgnoreCase("stattimestamp")) {
						String statTime;
						try {
							statTime = xmlParser.getColVal(row, "stattime");
						} catch (Exception e) {
							statTime = null;
						}
						if (null == statTime)
							return String.valueOf(System.currentTimeMillis());
						else {
							Date stat = AhRestoreCommons.convertDate(statTime);
							return String.valueOf(stat.getTime());
						}
					} else if (columnName.equalsIgnoreCase("timezone")) {
						long lID;
						try {
							lID = AhRestoreCommons.convertLong(xmlParser.getColVal(row, "owner"));
						} catch (AhRestoreException e) {
							return "America/Los_Angeles";
						} catch (AhRestoreColNotExistException e) {
							return "America/Los_Angeles";
						}
						HmDomain oDomain = AhRestoreNewMapTools.getHmDomain(lID);
						if (oDomain==null) {
							return "America/Los_Angeles";
						}
						return oDomain.getTimeZoneString();
					} else {
						return "";
					}
				}
			};

			// change default column info by name
			aa.setColumnInfoByName("stattimestamp", AhConvertXMLToCSV.COLUMN_TYPE_OTHER,
					defaultValue);
			aa.setColumnInfoByName("timezone", AhConvertXMLToCSV.COLUMN_TYPE_STRING, defaultValue);
			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(tableName, tableName);
		}
	}

	public static void restoreAhXIfExt() {
		String tableName = "hm_xif";
		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			GetColumnValueInterface defaultValue = new GetColumnValueInterface() {
				public String getValue(String columnName, String value, AhRestoreGetXML xmlParser,
						int row) {
					if (columnName.equalsIgnoreCase("stattimestamp")) {
						String statTime;
						try {
							statTime = xmlParser.getColVal(row, "stattime");
						} catch (Exception e) {
							statTime = null;
						}
						if (null == statTime)
							return String.valueOf(System.currentTimeMillis());
						else {
							Date stat = AhRestoreCommons.convertDate(statTime);
							return String.valueOf(stat.getTime());
						}
					} else if (columnName.equalsIgnoreCase("timezone")) {
						long lID;
						try {
							lID = AhRestoreCommons.convertLong(xmlParser.getColVal(row, "owner"));
						} catch (AhRestoreException e) {
							return "America/Los_Angeles";
						} catch (AhRestoreColNotExistException e) {
							return "America/Los_Angeles";
						}
						HmDomain oDomain = AhRestoreNewMapTools.getHmDomain(lID);
						if (oDomain==null) {
							return "America/Los_Angeles";
						}
						return oDomain.getTimeZoneString();
					} else {
						return "";
					}
				}
			};

			// change default column info by name
			aa.setColumnInfoByName("ifadminstatus", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, "2");
			aa.setColumnInfoByName("ifconfmode", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, "1");
			aa.setColumnInfoByName("ifoperstatus", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, "2");
			aa.setColumnInfoByName("stattimestamp", AhConvertXMLToCSV.COLUMN_TYPE_OTHER,
					defaultValue);
			aa.setColumnInfoByName("timezone", AhConvertXMLToCSV.COLUMN_TYPE_STRING, defaultValue);
			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(tableName, tableName);
		}
	}

	/**
	 * restore ah_clienteditvalues
	 */
	public static void restoreClientEditResults() {
		try {
			final String tableName = "ah_clienteditvalues";
			int index = 0;

			while (true) {
				String fileName = tableName;
				if (index > 0) {
					fileName = tableName + "_" + index;
				}
				index++;

				List<AhClientEditValues> allClientEditValuesList = getAllClientEditResults(fileName);
				if (allClientEditValuesList == null) {
					return;
				}

				List<AhClientEditValues> restoreDtoList = new ArrayList<AhClientEditValues>();
				for (int i = 0, size = allClientEditValuesList.size(); i < size; i++) {
					restoreDtoList.add(allClientEditValuesList.get(i));
					if (restoreDtoList.size() == RECORDS_NUMBER_ONE_BATCH) {
						try {
							QueryUtil.restoreBulkCreateBos(restoreDtoList);
						} catch (Exception e) {
							AhRestoreDBTools.logRestoreMsg("restore " + fileName, e);
						}

						restoreDtoList.clear();
					}
				}

				if (restoreDtoList.size() > 0) {
					try {
						QueryUtil.restoreBulkCreateBos(restoreDtoList);
					} catch (Exception e) {
						AhRestoreDBTools.logRestoreMsg("restore " + fileName, e);
					}
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("restore ah_clienteditvalues", e);
		}
	}

	private static List<AhClientEditValues> getAllClientEditResults(String fileName)
			throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of ah_clienteditvalues.xml
		 */
		if (!xmlParser.readXMLOneFile(fileName)) {
			return null;
		}

		/**
		 * No one row data stored in ah_clienteditvalues table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AhClientEditValues> clientEditResultList = new ArrayList<AhClientEditValues>(rowCount);

		for (int i = 0; i < rowCount; i++) {

			AhClientEditValues clientEditValues = new AhClientEditValues();

			/**
			 * Set clientMac
			 */
			String colName = "clientMac";
			boolean isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ah_clienteditvalues", colName);
			String clientMac = isColPresent ? xmlParser.getColVal(i, colName) : "";
			clientEditValues.setClientMac(AhRestoreCommons.convertString(clientMac));

			/**
			 * Set type
			 */
			colName = "type";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ah_clienteditvalues",
					colName);
			String type = isColPresent ? xmlParser.getColVal(i, colName) : "";
			clientEditValues.setType((short)AhRestoreCommons.convertInt(type));

			/**
			 * set expiration time
			 */
			colName = "expirationTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ah_clienteditvalues",
					colName);
			String expirationTime = isColPresent ? xmlParser.getColVal(i, colName) : "";
			clientEditValues.setExpirationTime(AhRestoreCommons.convertLong(expirationTime));

			/**
			 * Set clientHostname
			 */
			colName = "clientHostname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ah_clienteditvalues",
					colName);
			String clientHostname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			clientEditValues.setClientHostname(AhRestoreCommons.convertString(clientHostname));


			/**
			 * Set clientIP
			 */
			colName = "clientIP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ah_clienteditvalues",
					colName);
			String clientIP = isColPresent ? xmlParser.getColVal(i, colName) : "";
			clientEditValues.setClientIP(AhRestoreCommons.convertString(clientIP));

			/**
			 * Set clientUsername
			 */
			colName = "clientUsername";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ah_clienteditvalues",
					colName);
			String clientUsername = isColPresent ? xmlParser.getColVal(i, colName) : "";
			clientEditValues.setClientUsername(AhRestoreCommons.convertString(clientUsername));

			/**
			 * Set comment1
			 */
			colName = "comment1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ah_clienteditvalues",
					colName);
			String comment1 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			clientEditValues.setComment1(AhRestoreCommons.convertString(comment1));

			/**
			 * Set comment2
			 */
			colName = "comment2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ah_clienteditvalues",
					colName);
			String comment2 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			clientEditValues.setComment2(AhRestoreCommons.convertString(comment2));

			/**
			 * Set email
			 */
			colName = "email";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ah_clienteditvalues",
					colName);
			String email = isColPresent ? xmlParser.getColVal(i, colName) : "";
			clientEditValues.setEmail(AhRestoreCommons.convertString(email));

			/**
			 * Set company name
			 */
			colName = "companyName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ah_clienteditvalues",
					colName);
			String companyName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			clientEditValues.setCompanyName(AhRestoreCommons.convertString(companyName));

			/**
			 * Set ssid name
			 */
			colName = "ssidName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ah_clienteditvalues",
					colName);
			String ssidName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			clientEditValues.setSsidName(AhRestoreCommons.convertString(ssidName));

			/**
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ah_clienteditvalues", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'ah_clienteditvalues' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			clientEditValues.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			clientEditResultList.add(clientEditValues);
		}

		return clientEditResultList;
	}

	private static List<AhReport> getAllAhReport() throws AhRestoreColNotExistException,
			AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hm_report.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_report");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in hm_report table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AhReport> ahReportInfo = new ArrayList<AhReport>();
		boolean isColPresent;
		String colName;
		AhReport ahReportDTO;

		for (int i = 0; i < rowCount; i++) {
			ahReportDTO = new AhReport();

			/**
			 * Set name
			 */
			colName = "name";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			if (!isColPresent) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_report' data be lost, cause: 'name' column is not exist.");
				/**
				 * The name column must be exist in the table of hm_report
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("") || name.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_report' data be lost, cause: 'name' column value is null.");
				continue;
			}
			ahReportDTO.setName(name.trim());
			if (BeParaModule.DEFAULT_REPORT_NAME.equals(name.trim())||"def-rogue-cleints_report".equalsIgnoreCase(name.trim())) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_report' data be lost, cause: 'name' column value equals "+BeParaModule.DEFAULT_REPORT_NAME+" or def-rogue-cleints_report");
				continue;
			}

			/**
			 * Set reporttype
			 */
			colName = "reporttype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String reporttype = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (reporttype == null || reporttype.trim().equals("")
					|| reporttype.trim().equalsIgnoreCase("null")
					|| reporttype.trim().equalsIgnoreCase("mostClientsAPs")
					|| reporttype.trim().equalsIgnoreCase("clientCount")
					|| reporttype.trim().equalsIgnoreCase("clientAuth")
					|| reporttype.trim().equalsIgnoreCase("clientVendor")
					) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_report' data be lost, cause: 'reporttype' column value is null.");
				continue;
			}
			ahReportDTO.setReportType(reporttype);

			/**
			 * Set defaultflag
			 */
			colName = "defaultflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String defaultflag = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			if (AhRestoreCommons.convertStringToBoolean(defaultflag)){
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_report' data be lost, cause: 'defaultflag' column is not exist.");
				continue;
			}

			/**
			 * Set apname
			 */
			colName = "apname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String apName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setApName(AhRestoreCommons.convertString(apName));

			/**
			 * Set emailaddress
			 */
			colName = "emailaddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String emailaddress = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setEmailAddress(AhRestoreCommons.convertString(emailaddress));

			/**
			 * Set enabledemail
			 */
			colName = "enabledemail";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String enabledemail = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ahReportDTO.setEnabledEmail(AhRestoreCommons.convertStringToBoolean(enabledemail));

			/**
			 * Set enabledrecurrence
			 */
			colName = "enabledrecurrence";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String enabledrecurrence = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ahReportDTO.setEnabledRecurrence(AhRestoreCommons
					.convertStringToBoolean(enabledrecurrence));

			/**
			 * Set excutetype
			 */
			colName = "excutetype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String excutetype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setExcuteType(AhRestoreCommons.convertString(excutetype));

			/**
			 * Set newoldflg
			 */
			colName = "newoldflg";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String newoldflg = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setNewOldFlg(AhRestoreCommons.convertInt(newoldflg));

			/**
			 * Set recurrencetype
			 */
			colName = "recurrencetype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String recurrencetype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setRecurrenceType(AhRestoreCommons.convertString(recurrencetype));

			/**
			 * Set reportperiod
			 */
			colName = "reportperiod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String reportperiod = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setReportPeriod(AhRestoreCommons.convertInt(reportperiod));

			/**
			 * Set role
			 */
			colName = "role";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String role = isColPresent ? xmlParser.getColVal(i, colName) : "3";
			ahReportDTO.setRole(AhRestoreCommons.convertInt(role));

			/**
			 * Set compliancetype
			 */
			colName = "compliancetype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String compliancetype = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ahReportDTO.setComplianceType(AhRestoreCommons.convertInt(compliancetype));

			/**
			 * Set ssidname
			 */
			colName = "ssidname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String ssidname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setSsidName(AhRestoreCommons.convertString(ssidname));

			/**
			 * Set authmac
			 */
			colName = "authmac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String authmac = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setAuthMac(AhRestoreCommons.convertString(authmac));

			/**
			 * Set authhostname
			 */
			colName = "authhostname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String authhostname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setAuthHostName(AhRestoreCommons.convertString(authhostname));

			/**
			 * Set authusername
			 */
			colName = "authusername";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String authusername = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setAuthUserName(AhRestoreCommons.convertString(authusername));

			/**
			 * Set authip
			 */
			colName = "authip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String authip = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setAuthIp(AhRestoreCommons.convertString(authip));

			/**
			 * Set authtype
			 */
			colName = "authtype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String authtype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setAuthType(AhRestoreCommons.convertInt(authtype));

			/**
			 * Set starttime
			 */
			colName = "starttime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String starttime = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (starttime == null || starttime.equals("") || starttime.equalsIgnoreCase("null")) {
				ahReportDTO.setStartTime(0);
			} else {
				try {
					ahReportDTO.setStartTime(Long.parseLong(starttime));
				} catch (Exception e) {
					ahReportDTO.setStartTime(AhRestoreCommons.convertDate(starttime).getTime());
				}
			}
			
			/**
			 * Set pciStartTime
			 */
			colName = "pciStartTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String pciStartTime = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			if (pciStartTime == null || pciStartTime.equals("") || pciStartTime.equalsIgnoreCase("null")) {
				ahReportDTO.setPciStartTime(0);
			} else {
				try {
					ahReportDTO.setPciStartTime(AhRestoreCommons.convertLong(pciStartTime));
				} catch (Exception e) {
					ahReportDTO.setPciStartTime(0);
				}
			}
			
			/**
			 * Set pciEndTime
			 */
			colName = "pciEndTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String pciEndTime = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			if (pciEndTime == null || pciEndTime.equals("") || pciEndTime.equalsIgnoreCase("null")) {
				ahReportDTO.setPciEndTime(0);
			} else {
				try {
					ahReportDTO.setPciEndTime(AhRestoreCommons.convertLong(pciEndTime));
				} catch (Exception e) {
					ahReportDTO.setPciEndTime(0);
				}
			}

			/**
			 * Set timeaggregation
			 */
			colName = "timeaggregation";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String timeaggregation = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setTimeAggregation(AhRestoreCommons.convertInt(timeaggregation));

			/**
			 * Set weekday
			 */
			colName = "weekday";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String weekday = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setWeekDay(AhRestoreCommons.convertInt(weekday));

			/**
			 * Set location_id
			 */
			colName = "location_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String location_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!location_id.equals("") && !location_id.trim().equalsIgnoreCase("null")) {
				// String strMapName =
				// AhRestoreMapTool.getMapMapContainer(location_id);
				// MapContainerNode location = (MapContainerNode)
				// QueryUtil.findBoByAttribute(
				// MapContainerNode.class, "mapName", strMapName,
				// AhRestoreDBTools.HM_RESTORE_DOMAIN.getId());
				// ahReportDTO.setLocation(location);
				Long newLocationId = AhRestoreNewMapTools.getMapMapContainer(Long.parseLong(location_id.trim()));
				MapContainerNode mapContainer = QueryUtil.findBoById(MapContainerNode.class, newLocationId);
				ahReportDTO.setLocation(mapContainer);
			}

			/**
			 * Set detaildomainname
			 */
			colName = "detaildomainname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_report", colName);
			String detaildomainname = isColPresent ? xmlParser.getColVal(i, colName) : "home";
			ahReportDTO.setDetailDomainName(AhRestoreCommons.convertString(detaildomainname));

			// /**
			// * Set owner
			// */
			// colName = "owner";
			// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
			// "hm_report", colName);
			// String owner = isColPresent ? xmlParser.getColVal(i, colName) :
			// "";
			/**
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hm_report", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_report' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			ahReportDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			ahReportInfo.add(ahReportDTO);

		}

		return ahReportInfo;
	}

	public static boolean restoreAhReport() {

		try {
			List<AhReport> allAhReport = getAllAhReport();
			if (null == allAhReport) {
				return false;
			} else {
				QueryUtil.restoreBulkCreateBos(allAhReport);
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<AhNewReport> getAllAhNewReport() throws AhRestoreColNotExistException,
			AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hm_new_report.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_new_report");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in hm_new_report table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AhNewReport> ahReportInfo = new ArrayList<AhNewReport>();
		boolean isColPresent;
		String colName;
		AhNewReport ahReportDTO;

		for (int i = 0; i < rowCount; i++) {
			ahReportDTO = new AhNewReport();

			/**
			 * Set name
			 */
			colName = "name";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			if (!isColPresent) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_new_report' data be lost, cause: 'name' column is not exist.");
				/**
				 * The name column must be exist in the table of hm_new_report
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("") || name.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_new_report' data be lost, cause: 'name' column value is null.");
				continue;
			}
			ahReportDTO.setName(name.trim());


			/**
			 * Set reporttype
			 */
			colName = "reporttype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String reporttype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setReportType(AhRestoreCommons.convertInt(reporttype));

			/**
			 * Set defaultflag
			 */
			colName = "defaultflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String defaultflag = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			if (AhRestoreCommons.convertStringToBoolean(defaultflag)){
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_new_report' data be lost, cause: 'defaultflag' column value is 'true'.");
				continue;
			}

			/**
			 * Set ssidName
			 */
			colName = "ssidName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String ssidName = isColPresent ? xmlParser.getColVal(i, colName) : "All";
			ahReportDTO.setSsidName(ssidName);

			/**
			 * Set emailaddress
			 */
			colName = "emailaddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String emailaddress = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setEmailAddress(emailaddress);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setDescription(description);

			/**
			 * Set excutetype
			 */
			colName = "excutetype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String excutetype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setExcuteType(excutetype);

			/**
			 * Set frequency
			 */
			colName = "frequency";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String frequency = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setFrequency(AhRestoreCommons.convertInt(frequency));

			/**
			 * Set customDay
			 */
			colName = "customDay";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String customDay = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ahReportDTO.setCustomDay(AhRestoreCommons.convertStringToBoolean(customDay));

			/**
			 * Set customTime
			 */
			colName = "customTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String customTime = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ahReportDTO.setCustomTime(AhRestoreCommons.convertStringToBoolean(customTime));

			/**
			 * Set customDayValue
			 */
			colName = "customDayValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String customDayValue = isColPresent ? xmlParser.getColVal(i, colName) : "0111110";
			ahReportDTO.setCustomDayValue(customDayValue);

			/**
			 * Set customTimeStart
			 */
			colName = "customTimeStart";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String customTimeStart = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ahReportDTO.setCustomTimeStart(AhRestoreCommons.convertInt(customTimeStart));

			/**
			 * Set customTimeEnd
			 */
			colName = "customTimeEnd";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String customTimeEnd = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ahReportDTO.setCustomTimeEnd(AhRestoreCommons.convertInt(customTimeEnd));

			/**
			 * Set reportperiod
			 */
			colName = "reportperiod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String reportperiod = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setReportPeriod(AhRestoreCommons.convertInt(reportperiod));

			/**
			 * Set startTime
			 */
			colName = "startTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String startTime = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ahReportDTO.setStartTime(AhRestoreCommons.convertLong(startTime));

			/**
			 * Set endTime
			 */
			colName = "endTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String endTime = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ahReportDTO.setEndTime(AhRestoreCommons.convertLong(endTime));

			/**
			 * Set location_id
			 */
			colName = "location_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_new_report", colName);
			String location_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!location_id.equals("") && !location_id.trim().equalsIgnoreCase("null")) {
				Long newLocationId = AhRestoreNewMapTools.getMapMapContainer(Long.parseLong(location_id.trim()));
				MapContainerNode mapContainer = QueryUtil.findBoById(MapContainerNode.class, newLocationId);
				ahReportDTO.setLocation(mapContainer);
			}

			/**
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hm_new_report", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_new_report' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			ahReportDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			ahReportInfo.add(ahReportDTO);

		}

		return ahReportInfo;
}

public static boolean restoreAhNewReport() {

	try {
		List<AhNewReport> allAhReport = getAllAhNewReport();
		if (null == allAhReport) {
			return false;
		} else {
			List<AhDashboard> lst = new ArrayList<AhDashboard>();
			for(AhNewReport asp: allAhReport){
				AhDashboard da = new AhDashboard();
				da.setOwner(asp.getOwner());
				da.setActive(false);
				da.setDefaultFlag(false);
				da.setDashName(asp.getName());
				da.setUserName("");
				da.setDescription(asp.getDescription());
				
				da.setDaType(AhDashboard.DASHBOARD_TYPE_REPORT);
				if (asp.getExcuteType().equals(AhNewReport.NEW_REPORT_EXCUTETYPE_IMME)){
					da.setReportScheduleStatus(AhDashboard.REPORT_STATUS_UNSCHEDULED);
				} else {
					da.setReportScheduleStatus(AhDashboard.REPORT_STATUS_SCHEDULED);
				}
				if (!asp.getSsidName().equals("All")){
					da.setFilterObjectType(AhDashBoardGroup.DA_GROUP_TYPE_SSID);
					da.setFilterObjectId(asp.getSsidName());
				}
				if (asp.getLocation()!=null) {
					da.setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_MAP);
					da.setObjectId(asp.getLocation().getId().toString());
				}
				
				da.setRefrequency(asp.getFrequency());
				da.setReCustomDay(asp.isCustomDay());
				da.setReCustomTime(asp.isCustomTime());
				da.setReCustomDayValue(asp.getCustomDayValue());
				da.setReCustomTimeStart(asp.getCustomTimeStart());
				da.setReCustomTimeEnd(asp.getCustomTimeEnd());
				da.setReEmailAddress(asp.getEmailAddress());
				
				lst.add(da);
			}
			if (!lst.isEmpty()) {
				QueryUtil.restoreBulkCreateBos(lst);
			}
			List<AhDashboardLayout> lstLayout = new ArrayList<AhDashboardLayout>();
			List<Integer> lstWidget = new ArrayList<Integer>();
			for(int i=26;i<=40; i++) {
				lstWidget.add(i);
			}
			lstWidget.add(51);
			
			for(int i=0; i < lst.size(); ++i)
			{
				lstLayout.addAll(createLayoutAndWidget(lstWidget, lst.get(i)));
			}
			if (!lstLayout.isEmpty()) {
				QueryUtil.restoreBulkCreateBos(lstLayout);
			}
			
			
//			QueryUtil.restoreBulkCreateBos(allAhReport);
		}
	} catch (Exception e) {
		AhRestoreDBTools.logRestoreMsg(e.getMessage());
		return false;
	}
	return true;
}

	private static List<AhSummaryPage> getAllAhSummaryPage() throws AhRestoreColNotExistException,
			AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hm_summary_page.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_summary_page");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in hm_summary_page table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AhSummaryPage> ahSummaryPageInfo = new ArrayList<AhSummaryPage>();
		boolean isColPresent;
		String colName;
		AhSummaryPage ahSummaryPageDTO;

		for (int i = 0; i < rowCount; i++) {
			ahSummaryPageDTO = new AhSummaryPage();

			/**
			 * Set username
			 */
			colName = "username";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			if (!isColPresent) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_summary_page' data be lost, cause: 'username' column is not exist.");
				/**
				 * The name column must be exist in the table of hm_summary_page
				 */
				continue;
			}

			String username = xmlParser.getColVal(i, colName);
			ahSummaryPageDTO.setUserName(username.trim());

			/**
			 * Set attribute
			 */
			colName = "attribute";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String attribute = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahSummaryPageDTO.setAttribute(attribute);

			/**
			 * Set ckwidgetaphealth
			 */
			colName = "ckwidgetaphealth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetaphealth = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			ahSummaryPageDTO.setCkwidgetAPhealth(AhRestoreCommons.convertStringToBoolean(ckwidgetaphealth));

			/**
			 * Set ckwidgetapmostclientcount
			 */
			colName = "ckwidgetapmostclientcount";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetapmostclientcount = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			ahSummaryPageDTO.setCkwidgetAPmostClientCount(AhRestoreCommons.convertStringToBoolean(ckwidgetapmostclientcount));

			/**
			 * Set ckwidgetapmostbandwidth
			 */
			colName = "ckwidgetapmostbandwidth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetapmostbandwidth = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetAPmostBandwidth(AhRestoreCommons.convertStringToBoolean(ckwidgetapmostbandwidth));

			/**
			 * Set ckwidgetapmostinterference
			 */
			colName = "ckwidgetapmostinterference";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetapmostinterference = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetAPmostInterference(AhRestoreCommons.convertStringToBoolean(ckwidgetapmostinterference));

			/**
			 * Set ckwidgetapmostcrcerror
			 */
			colName = "ckwidgetapmostcrcerror";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetapmostcrcerror = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetAPmostCrcError(AhRestoreCommons.convertStringToBoolean(ckwidgetapmostcrcerror));

			/**
			 * Set ckwidgetapmosttxretry
			 */
			colName = "ckwidgetapmosttxretry";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetapmosttxretry = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetAPmostTxRetry(AhRestoreCommons.convertStringToBoolean(ckwidgetapmosttxretry));

			/**
			 * Set ckwidgetapmostrxretry
			 */
			colName = "ckwidgetapmostrxretry";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetapmostrxretry = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetAPmostRxRetry(AhRestoreCommons.convertStringToBoolean(ckwidgetapmostrxretry));

			/**
			 * Set ckwidgetapsecurity
			 */
			colName = "ckwidgetapsecurity";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetapsecurity = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetAPsecurity(AhRestoreCommons.convertStringToBoolean(ckwidgetapsecurity));

			/**
			 * Set ckwidgetapcompliance
			 */
			colName = "ckwidgetapcompliance";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetapcompliance = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetAPcompliance(AhRestoreCommons.convertStringToBoolean(ckwidgetapcompliance));

			/**
			 * Set ckwidgetapalarm
			 */
			colName = "ckwidgetapalarm";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetapalarm = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			ahSummaryPageDTO.setCkwidgetAPalarm(AhRestoreCommons.convertStringToBoolean(ckwidgetapalarm));

			/**
			 * Set ckwidgetapbandwidth
			 */
			colName = "ckwidgetapbandwidth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetapbandwidth = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			ahSummaryPageDTO.setCkwidgetAPbandwidth(AhRestoreCommons.convertStringToBoolean(ckwidgetapbandwidth));

			/**
			 * Set ckwidgetapsla
			 */
			colName = "ckwidgetapsla";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetapsla = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetAPsla(AhRestoreCommons.convertStringToBoolean(ckwidgetapsla));

			/**
			 * Set ckwidgetapversion
			 */
			colName = "ckwidgetapversion";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetapversion = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetAPversion(AhRestoreCommons.convertStringToBoolean(ckwidgetapversion));

			/**
			 * Set ckwidgetauditlog
			 */
			colName = "ckwidgetauditlog";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetauditlog = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetAuditLog(AhRestoreCommons.convertStringToBoolean(ckwidgetauditlog));

			/**
			 * Set ckwidgetapuptime
			 */
			colName = "ckwidgetapuptime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetapuptime = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetAPuptime(AhRestoreCommons.convertStringToBoolean(ckwidgetapuptime));

			/**
			 * Set ckwidgetactiveuser
			 */
			colName = "ckwidgetactiveuser";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetactiveuser = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetActiveUser(AhRestoreCommons.convertStringToBoolean(ckwidgetactiveuser));

			/**
			 * Set ckwidgetcinfo
			 */
			colName = "ckwidgetcinfo";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetcinfo = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			ahSummaryPageDTO.setCkwidgetCinfo(AhRestoreCommons.convertStringToBoolean(ckwidgetcinfo));

			/**
			 * Set ckwidgetcmosttxairtime
			 */
			colName = "ckwidgetcmosttxairtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetcmosttxairtime = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			ahSummaryPageDTO.setCkwidgetCmostTxAirtime(AhRestoreCommons.convertStringToBoolean(ckwidgetcmosttxairtime));
			/**
			 * Set ckwidgetcmostrxairtime
			 */
			colName = "ckwidgetcmostrxairtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetcmostrxairtime = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			ahSummaryPageDTO.setCkwidgetCmostRxAirtime(AhRestoreCommons.convertStringToBoolean(ckwidgetcmostrxairtime));
			/**
			 * Set ckwidgetcmostfailure
			 */
			colName = "ckwidgetcmostfailure";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetcmostfailure = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			ahSummaryPageDTO.setCkwidgetCmostFailure(AhRestoreCommons.convertStringToBoolean(ckwidgetcmostfailure));

			/**
			 * Set ckwidgetcvendor
			 */
			colName = "ckwidgetcvendor";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetcvendor = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetCvendor(AhRestoreCommons.convertStringToBoolean(ckwidgetcvendor));

			/**
			 * Set ckwidgetcradio
			 */
			colName = "ckwidgetcradio";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetcradio = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			ahSummaryPageDTO.setCkwidgetCradio(AhRestoreCommons.convertStringToBoolean(ckwidgetcradio));

			/**
			 * Set ckwidgetcuserprofile
			 */
			colName = "ckwidgetcuserprofile";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetcuserprofile = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetCuserprofile(AhRestoreCommons.convertStringToBoolean(ckwidgetcuserprofile));

			/**
			 * Set ckwidgetcsla
			 */
			colName = "ckwidgetcsla";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetcsla = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			ahSummaryPageDTO.setCkwidgetCsla(AhRestoreCommons.convertStringToBoolean(ckwidgetcsla));

			/**
			 * Set ckwidgetsinfo
			 */
			colName = "ckwidgetsinfo";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetsinfo = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			ahSummaryPageDTO.setCkwidgetSinfo(AhRestoreCommons.convertStringToBoolean(ckwidgetsinfo));

			/**
			 * Set ckwidgetsuser
			 */
			colName = "ckwidgetsuser";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetsuser = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetSuser(AhRestoreCommons.convertStringToBoolean(ckwidgetsuser));

			/**
			 * Set ckwidgetscpu
			 */
			colName = "ckwidgetscpu";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetscpu = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			ahSummaryPageDTO.setCkwidgetScpu(AhRestoreCommons.convertStringToBoolean(ckwidgetscpu));

			/**
			 * Set ckwidgetsperformanceinfo
			 */
			colName = "ckwidgetsperformanceinfo";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_summary_page", colName);
			String ckwidgetsperformanceinfo = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			ahSummaryPageDTO.setCkwidgetSperformanceInfo(AhRestoreCommons.convertStringToBoolean(ckwidgetsperformanceinfo));

			// /**
			// * Set owner
			// */
			// colName = "owner";
			// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
			// "hm_report", colName);
			// String owner = isColPresent ? xmlParser.getColVal(i, colName) :
			// "";
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hm_summary_page", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_summary_page' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			ahSummaryPageDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			ahSummaryPageInfo.add(ahSummaryPageDTO);
		}

		return ahSummaryPageInfo;
	}


	public static boolean restoreAhSummaryPage() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		//Device Status 
		map.put("ckwidgetAPhealth", 1);
		//Device Alarms  
		map.put("ckwidgetAPalarm", 2);
		//Device SLA Compliance
		map.put("ckwidgetAPsla", 26);
		//Devices with the Most Clients (Last Hour)
		map.put("ckwidgetAPmostClientCount", 33);
		//Devices with the Most Bandwidth
		map.put("ckwidgetAPmostBandwidth", 28);
		//Devices with the Most Interference Based on CRC Errors (Last Hour) 
		map.put("ckwidgetAPmostInterference", 3);
		//Device Interfaces with the Highest CRC Error Rate (Last Hour)
		map.put("ckwidgetAPmostCrcError", 4);
		//Device Interfaces with the Highest Tx Retry Rate (Last Hour)
		map.put("ckwidgetAPmostTxRetry", 5);
		//Device Interfaces with the Highest Rx Retry Rate (Last Hour)
		map.put("ckwidgetAPmostRxRetry", 6);
		//Device Configuration Compliance
		map.put("ckwidgetAPcompliance", 7);
		//HiveOS Version 
		map.put("ckwidgetAPversion", 8);
		//Network Security 
		map.put("ckwidgetAPsecurity", 19);
		//Network Data Usage (Last 24 Hours) 
		map.put("ckwidgetAPbandwidth", 25);
		//Percentage of Devices Up 
		map.put("ckwidgetAPuptime", 20);
		//Last 5 Audit Logs 
		map.put("ckwidgetAuditLog", 21);
		//HiveManager System Information 
		map.put("ckwidgetSinfo", 22);
		//Admin Login Information
		map.put("ckwidgetSuser", 23);
		//System CPU/Memory Usage 
		map.put("ckwidgetScpu", 24);
		//Client Status 
		map.put("ckwidgetCinfo", 11);
		//Client Radio Mode Distribution 
		map.put("ckwidgetCradio", 12);
		//Active Clients by User Profile
		map.put("ckwidgetCuserprofile", 13);
		//Client Vendor Distribution 
		map.put("ckwidgetCvendor", 14);
		//Total Active Clients (Last 24 Hours) 
		map.put("ckwidgetActiveUser", 42);
		//Clients with the Most Tx Airtime Usage
		map.put("ckwidgetCmostTxAirtime", 16);
		//Clients with the Most Rx Airtime Usage
		map.put("ckwidgetCmostRxAirtime", 17);
		//Clients with the Most Association Failures (Last Hour) 
		map.put("ckwidgetCmostFailure", 18);
		//Client SLA Compliance 
		map.put("ckwidgetCsla", 27);

//		private boolean ckwidgetSperformanceInfo;
		
		Set<String> superUserSet=null;
		
		try {
			List<AhSummaryPage> allAhSummaryPage = getAllAhSummaryPage();
			if (null == allAhSummaryPage) {
				return false;
			} else {
				List<AhDashboard> lst = new ArrayList<AhDashboard>();
				Map<Integer, List<Integer>> lstWidget = new HashMap<Integer, List<Integer>>();
				int index=0;
				for(AhSummaryPage asp: allAhSummaryPage){
					AhDashboard da = new AhDashboard();
					da.setOwner(asp.getOwner());
					da.setActive(false);
					da.setDefaultFlag(false);
					da.setDashName("Migrated Widgets");
					da.setPosition(1);
					da.setUserName(asp.getUserName());
					da.setDaType(AhDashboard.DASHBOARD_TYPE_DASH);
					
					if (lstWidget.get(index)==null) {
						List<Integer> lstTmp = new ArrayList<Integer>();
						lstWidget.put(index,lstTmp);
					}
					// Device Stats
//					if (asp.getCkwidgetAPhealth()) {
//						lstWidget.get(index).add(map.get("ckwidgetAPhealth"));
//					}
					//Device Alarms  
					if (asp.getCkwidgetAPalarm()) {
						lstWidget.get(index).add(map.get("ckwidgetAPalarm"));
					}
					//Device SLA Compliance
//					if (asp.getCkwidgetAPsla()) {
//						lstWidget.get(index).add(map.get("ckwidgetAPsla"));
//					}
					//Devices with the Most Clients (Last Hour)
					if (asp.getCkwidgetAPmostClientCount()) {
						lstWidget.get(index).add(map.get("ckwidgetAPmostClientCount"));
					}
					//Devices with the Most Bandwidth
					//if (asp.getCkwidgetAPmostBandwidth()) {
					//	lstWidget.get(index).add(map.get("ckwidgetAPmostBandwidth"));
					//}
					//Devices with the Most Interference Based on CRC Errors (Last Hour) 
					//if (asp.getCkwidgetAPmostInterference()) {
					//	lstWidget.get(index).add(map.get("ckwidgetAPmostInterference"));
					//}
					//Device Interfaces with the Highest CRC Error Rate (Last Hour)
					//Device Interfaces with the Highest Tx Retry Rate (Last Hour)
					//Device Interfaces with the Highest Rx Retry Rate (Last Hour)
					if (asp.getCkwidgetAPmostCrcError() || asp.getCkwidgetAPmostTxRetry() 
							|| asp.getCkwidgetAPmostRxRetry() || asp.getCkwidgetAPmostInterference()) {
						lstWidget.get(index).add(40);
					}
					//Device Configuration Compliance
					if (asp.getCkwidgetAPcompliance()) {
						lstWidget.get(index).add(map.get("ckwidgetAPcompliance"));
					}		
					//HiveOS Version
//					if (asp.getCkwidgetAPversion()) {
//						lstWidget.get(index).add(map.get("ckwidgetAPversion"));
//					}
					//Network Security 
//					if (asp.getCkwidgetAPsecurity()) {
//						lstWidget.get(index).add(map.get("ckwidgetAPsecurity"));
//					}
					//Network Data Usage (Last 24 Hours) 
					if (asp.getCkwidgetAPbandwidth()) {
						lstWidget.get(index).add(map.get("ckwidgetAPbandwidth"));
					}
					//Percentage of Devices Up 
//					if (asp.getCkwidgetAPuptime()) {
//						lstWidget.get(index).add(map.get("ckwidgetAPuptime"));
//					}
					//Last 5 Audit Logs 
//					if (asp.getCkwidgetAuditLog()) {
//						lstWidget.get(index).add(map.get("ckwidgetAuditLog"));
//					}
					if (asp.getOwner().isHomeDomain()) {
						if (superUserSet==null) {
							superUserSet = new HashSet<String>();
							List<HmUser> userList =  QueryUtil.executeQuery(HmUser.class , null, 
									new FilterParams("userGroup.groupName", HmUserGroup.ADMINISTRATOR), asp.getOwner().getId());
							if (!userList.isEmpty()) {
								for (HmUser u: userList){
									superUserSet.add(u.getUserName());
								}
							}
						}
						if (superUserSet!=null && superUserSet.contains(asp.getUserName())) {
							//HiveManager System Information 
							if (asp.getCkwidgetSinfo()) {
								lstWidget.get(index).add(map.get("ckwidgetSinfo"));
							}
							//Admin Login Information
							if (asp.getCkwidgetSuser()) {
								lstWidget.get(index).add(map.get("ckwidgetSuser"));
							}
							//System CPU/Memory Usage 
							if (asp.getCkwidgetScpu()) {
								lstWidget.get(index).add(map.get("ckwidgetScpu"));
							}
						}
					}
					//Client Status 
//					if (asp.getCkwidgetCinfo()) {
//						lstWidget.get(index).add(map.get("ckwidgetCinfo"));
//					}
					//Client Radio Mode Distribution 
					if (asp.getCkwidgetCradio()) {
						lstWidget.get(index).add(map.get("ckwidgetCradio"));
					}
					//Client SLA Compliance 
//					if (asp.getCkwidgetCsla()) {
//						lstWidget.get(index).add(map.get("ckwidgetCsla"));
//					}
					//Active Clients by User Profile
					if (asp.getCkwidgetCuserprofile()) {
						lstWidget.get(index).add(map.get("ckwidgetCuserprofile"));
					}
					//Client Vendor Distribution 
					if (asp.getCkwidgetCvendor()) {
						lstWidget.get(index).add(map.get("ckwidgetCvendor"));
					}
					//Total Active Clients (Last 24 Hours) 
//					if (asp.getCkwidgetActiveUser()) {
//						lstWidget.get(index).add(map.get("ckwidgetActiveUser"));
//					}
					//Clients with the Most Tx Airtime Usage
//					if (asp.getCkwidgetCmostTxAirtime()) {
//						lstWidget.get(index).add(map.get("ckwidgetCmostTxAirtime"));
//					}
					//Clients with the Most Rx Airtime Usage
//					if (asp.getCkwidgetCmostRxAirtime()) {
//						lstWidget.get(index).add(map.get("ckwidgetCmostRxAirtime"));
//					}
					//Clients with the Most Association Failures (Last Hour) 
					if (asp.getCkwidgetCmostFailure()) {
						lstWidget.get(index).add(map.get("ckwidgetCmostFailure"));
					}
					if (lstWidget.get(index).isEmpty()) {
						continue;
					}
					lst.add(da);
					index++;
				}
				if (!lst.isEmpty()) {
					QueryUtil.restoreBulkCreateBos(lst);
				}
				List<AhDashboardLayout> lstLayout = new ArrayList<AhDashboardLayout>();
				for(int i=0; i < lst.size(); ++i)
				{
					lstLayout.addAll(createLayoutAndWidget(lstWidget.get(i), lst.get(i)));
				}
				if (!lstLayout.isEmpty()) {
					QueryUtil.restoreBulkCreateBos(lstLayout);
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	private static List<AhDashboardLayout> createLayoutAndWidget(List<Integer> keys, AhDashboard da ){
		List<DashboardComponent> daList = QueryUtil.executeQuery(DashboardComponent.class, null, 
				new FilterParams("key in (:s1) and defaultFlag=:s2",new Object[]{keys, true}), null,new ImplQueryBo());
		List<AhDashboardLayout> result = new ArrayList<>();
		AhDashboardLayout layout1 = new AhDashboardLayout();
		layout1.setItemOrder((byte)0);
		layout1.setDashboard(da);
		layout1.setSizeType(AhDashboardLayout.SIZE_LARGE);
		layout1.setOwner(da.getOwner());
		result.add(layout1);
		
		AhDashboardLayout layout2 = new AhDashboardLayout();
		layout2.setItemOrder((byte)1);
		layout2.setDashboard(da);
		layout2.setSizeType(AhDashboardLayout.SIZE_LARGE);
		layout2.setOwner(da.getOwner());
		result.add(layout2);

		int layoutsLen = result.size();
		int curPos = 0;
		AhDashboardLayout layoutTmp;
		for(DashboardComponent dac: daList) {
			layoutTmp = result.get(curPos%layoutsLen);
			AhDashboardWidget wd = new AhDashboardWidget();
			wd.setOwner(da.getOwner());
			wd.setMainTitle(dac.getComponentName());
			wd.setItemOrder(layoutTmp.getDaWidgets().size());
			wd.setWidgetConfig(dac);
			wd.setDaLayout(layoutTmp);
			layoutTmp.getDaWidgets().add(wd);
			curPos++;
		}
		
		return result;
	}
	
	static class ImplQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof DashboardComponent) {
				DashboardComponent profile = (DashboardComponent) bo;
				if (profile.getComponentMetric()!=null){
					profile.getComponentMetric().getId();
				}
			}

			return null;
		}
	}

	private static Map<String, List<AhCustomReportField>> getAllCustomReportField() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hm_custom_report_field_table.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_custom_report_field_table");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<AhCustomReportField>> customFieldInfo = new HashMap<String, List<AhCustomReportField>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set custom_report_id
			 */
			colName = "custom_report_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hm_custom_report_field_table", colName);
			if (!isColPresent)
			{
				/**
				 * The custom_report_id column must be exist in the table of hm_custom_report_field_table
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * Set custom_report_field_id
			 */
			colName = "custom_report_field_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hm_custom_report_field_table", colName);
			if (!isColPresent)
			{
				/**
				 * The custom_report_field_id column must be exist in the table of hm_custom_report_field_table
				 */
				continue;
			}

			String customFieldId = xmlParser.getColVal(i, colName);
			if (customFieldId == null || customFieldId.trim().equals("")
				|| customFieldId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			if (!customFieldId.equals("") && !customFieldId.trim().equalsIgnoreCase("null")) {
				AhCustomReportField customField = QueryUtil.findBoById(
						AhCustomReportField.class, AhRestoreCommons.convertLong(customFieldId));
				if (customField != null) {
					if (customFieldInfo.get(profileId) == null) {
						List<AhCustomReportField> customList= new ArrayList<AhCustomReportField>();
						customList.add(customField);
						customFieldInfo.put(profileId, customList);
					} else {
						customFieldInfo.get(profileId).add(customField);
					}
				}
			}
		}

		return customFieldInfo;
	}

	private static List<AhCustomReport> getAllAhCustomReport() throws AhRestoreColNotExistException,
			AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hm_custom_report.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_custom_report");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in hm_custom_report table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AhCustomReport> ahReportInfo = new ArrayList<AhCustomReport>();
		boolean isColPresent;
		String colName;
		AhCustomReport ahReportDTO;

		for (int i = 0; i < rowCount; i++) {
			ahReportDTO = new AhCustomReport();

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hm_custom_report", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setId(Long.valueOf(id));
			/**
			 * Set name
			 */
			colName = "name";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			if (!isColPresent) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_custom_report' data be lost, cause: 'name' column is not exist.");
				/**
				 * The name column must be exist in the table of hm_custom_report
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("") || name.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_custom_report' data be lost, cause: 'name' column value is null.");
				continue;
			}
			ahReportDTO.setName(name.trim());

			/**
			 * Set reporttype
			 */
			colName = "reporttype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String reporttype = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (reporttype == null || reporttype.trim().equals("") || reporttype.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_custom_report' data be lost, cause: 'reporttype' column is not exist.");
				continue;
			}
			ahReportDTO.setReportType(AhRestoreCommons.convertInt(reporttype));

			/**
			 * Set reportdetailtype
			 */
			colName = "reportdetailtype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String reportdetailtype = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (reportdetailtype == null || reportdetailtype.trim().equals("") || reportdetailtype.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_custom_report' data be lost, cause: 'reportdetailtype' column is not exist.");
				continue;
			}
			ahReportDTO.setReportDetailType(AhRestoreCommons.convertInt(reportdetailtype));

			/**
			 * Set apname
			 */
			colName = "apname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String apName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setApName(apName);

			/**
			 * Set reportperiod
			 */
			colName = "reportperiod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String reportperiod = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setReportPeriod(AhRestoreCommons.convertInt(reportperiod));

			/**
			 * Set interfacerole
			 */
			colName = "interfacerole";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String interfacerole = isColPresent ? xmlParser.getColVal(i, colName) : "3";
			ahReportDTO.setInterfaceRole(AhRestoreCommons.convertInt(interfacerole));

			/**
			 * Set ssidname
			 */
			colName = "ssidname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String ssidname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setSsidName(ssidname);

			/**
			 * Set authmac
			 */
			colName = "authmac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String authmac = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setAuthMac(authmac);

			/**
			 * Set authhostname
			 */
			colName = "authhostname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String authhostname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setAuthHostName(authhostname);

			/**
			 * Set authusername
			 */
			colName = "authusername";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String authusername = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setAuthUserName(authusername);

			/**
			 * Set authip
			 */
			colName = "authip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String authip = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setAuthIp(authip);

			/**
			 * Set longsortby
			 */
			colName = "longsortby";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String longsortby = isColPresent ? xmlParser.getColVal(i, colName) : "1101";
			ahReportDTO.setLongSortBy(AhRestoreCommons.convertLong(longsortby));

			/**
			 * Set sortbytype
			 */
			colName = "sortbytype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String sortbytype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setSortByType(AhRestoreCommons.convertInt(sortbytype));

			/**
			 * Set location_id
			 */
			colName = "location_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String location_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!location_id.equals("") && !location_id.trim().equalsIgnoreCase("null")) {
				Long newLocationId = AhRestoreNewMapTools.getMapMapContainer(Long.parseLong(location_id.trim()));
				MapContainerNode mapContainer = QueryUtil.findBoById(MapContainerNode.class, newLocationId);
				ahReportDTO.setLocation(mapContainer);
			}

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_custom_report", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setDescription(description);

			/**
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hm_custom_report", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_custom_report' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			ahReportDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			ahReportInfo.add(ahReportDTO);
		}

		return ahReportInfo;
	}

	public static boolean restoreAhCustomReport() {
		try {
			List<AhCustomReport> allAhReport = getAllAhCustomReport();
			Map<String, List<AhCustomReportField>> allField = getAllCustomReportField();
			if (null == allAhReport) {
				return false;
			} else {
				for(AhCustomReport reportClass:allAhReport){
					List<AhCustomReportField> customReportFieldList = allField.get(reportClass.getId().toString());
					if (customReportFieldList!=null){
						Collections.sort(customReportFieldList, new Comparator<AhCustomReportField>(){
							@Override
							public int compare(AhCustomReportField o1, AhCustomReportField o2) {
								return Integer.parseInt(String.valueOf(o1.getId()-o2.getId()));
							}
						});
					}
					reportClass.setCustomFields(allField.get(reportClass.getId().toString()));
					reportClass.setId(null);
				}

				QueryUtil.restoreBulkCreateBos(allAhReport);
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<AhUserReport> getAllAhUserReport() throws AhRestoreColNotExistException,
			AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hm_user_report.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_user_report");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in hm_user_report table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AhUserReport> ahReportInfo = new ArrayList<AhUserReport>();
		boolean isColPresent;
		String colName;
		AhUserReport ahReportDTO;

		for (int i = 0; i < rowCount; i++) {
			ahReportDTO = new AhUserReport();

			/**
			 * Set name
			 */
			colName = "name";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_report", colName);
			if (!isColPresent) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_user_report' data be lost, cause: 'name' column is not exist.");
				/**
				 * The name column must be exist in the table of hm_user_report
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("") || name.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_user_report' data be lost, cause: 'name' column value is null.");
				continue;
			}
			ahReportDTO.setName(name.trim());

			/**
			 * Set reporttype
			 */
			colName = "reporttype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_report", colName);
			String reporttype = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (name == null || name.trim().equals("") || name.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_user_report' data be lost, cause: 'reporttype' column is not exist.");
				continue;
			}
			ahReportDTO.setReportType(reporttype);

			/**
			 * Set apname
			 */
			colName = "apname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_report", colName);
			String apName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setApName(apName);

			/**
			 * Set reportperiod
			 */
			colName = "reportperiod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_report", colName);
			String reportperiod = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ahReportDTO.setReportPeriod(AhRestoreCommons.convertInt(reportperiod));

			/**
			 * Set authmac
			 */
			colName = "authmac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_report", colName);
			String authmac = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setAuthMac(authmac);

			/**
			 * Set authhostname
			 */
			colName = "authhostname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_report", colName);
			String authhostname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setAuthHostName(authhostname);

			/**
			 * Set authusername
			 */
			colName = "authusername";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_report", colName);
			String authusername = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setAuthUserName(authusername);

			/**
			 * Set authip
			 */
			colName = "authip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_report", colName);
			String authip = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setAuthIp(authip);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_report", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahReportDTO.setDescription(description);

			// /**
			// * Set owner
			// */
			// colName = "owner";
			// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
			// "hm_user_report", colName);
			// String owner = isColPresent ? xmlParser.getColVal(i, colName) :
			// "";
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hm_user_report", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hm_user_report' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			ahReportDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			ahReportInfo.add(ahReportDTO);
		}

		return ahReportInfo;
	}

	public static boolean restoreAhUserReport() {

		try {
			List<AhUserReport> allAhReport = getAllAhUserReport();
			if (null == allAhReport) {
				return false;
			} else {
				QueryUtil.restoreBulkCreateBos(allAhReport);
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<ActiveClientFilter> getAllActiveClientFilters() throws AhRestoreColNotExistException,
			AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of ACTIVECLIENT_FILTER.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("activeclient_filter");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in hiveap_filter table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<ActiveClientFilter> filterList = new ArrayList<ActiveClientFilter>();
		boolean isColPresent;
		String colName;
		ActiveClientFilter filter;

		for (int i = 0; i < rowCount; i++) {
			filter = new ActiveClientFilter();

			/**
			 * Set filtername
			 */
			colName = "filterName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activeclient_filter",
					colName);
			if (!isColPresent) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'activeclient_filter' data be lost, cause: 'filterName' column is not exist.");
				/**
				 * The filtername column must be exist in the table of
				 * hiveap_filter
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("") || name.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'activeclient_filter' data be lost, cause: 'filterName' column value is null.");
				continue;
			}
			filter.setFilterName(name.trim());

			/**
			 * Set username
			 */
			colName = "userName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activeclient_filter",
					colName);
			String username = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (username == null || username.trim().equals("")
					|| username.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'activeclient_filter' data be lost, cause: 'userName' column is not exist.");
				continue;
			}
			filter.setUserName(username);

			/**
			 * Set filterApName
			 */
			colName = "filterApName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activeclient_filter",
					colName);
			String filterApName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			filter.setFilterApName(filterApName);

			/**
			 * Set filterClientMac
			 */
			colName = "filterClientMac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activeclient_filter",
					colName);
			String filterClientMac = isColPresent ? xmlParser.getColVal(i, colName) : "";
			filter.setFilterClientMac(filterClientMac);

			/**
			 * Set filterClientIP
			 */
			colName = "filterClientIP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activeclient_filter",
					colName);
			String filterClientIP = isColPresent ? xmlParser.getColVal(i, colName) : "";
			filter.setFilterClientIP(filterClientIP);

			/**
			 * Set filterClientHostName
			 */
			colName = "filterClientHostName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activeclient_filter",
					colName);
			String filterClientHostName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			filter.setFilterClientHostName(filterClientHostName);

			/**
			 * Set filterClientUserName
			 */
			colName = "filterClientUserName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activeclient_filter",
					colName);
			String filterClientUserName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			filter.setFilterClientUserName(filterClientUserName);

			/**
			 * Set filterOverallClientHealth
			 */
			colName = "filterOverallClientHealth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activeclient_filter",
					colName);
			String filterOverallClientHealth = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if(!"".equals(filterOverallClientHealth)){
				filter.setFilterOverallClientHealth((byte)AhRestoreCommons.convertInt(filterOverallClientHealth));
			}
			if (RestoreHiveAp.restore_from_fuji_before) {
				if (filter.getFilterOverallClientHealth()==0) {
					filter.setFilterOverallClientHealth((byte)-1);
				}
			}

			/**
			 * Set filterClientOsInfo
			 */
			colName = "filterClientOsInfo";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activeclient_filter",
					colName);
			String filterClientOsInfo = isColPresent ? xmlParser.getColVal(i, colName) : "";
			filter.setFilterClientOsInfo(filterClientOsInfo);

			/**
			 * Set filterClientVLAN
			 */
			colName = "filterClientVLAN";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activeclient_filter",
					colName);
			String filterClientVLAN = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if(!"".equals(filterClientVLAN)){
				filter.setFilterClientVLAN(AhRestoreCommons.convertInt(filterClientVLAN));
			}
			if (RestoreHiveAp.restore_from_fuji_before) {
				if (filter.getFilterClientVLAN()==0) {
					filter.setFilterClientVLAN((byte)-1);
				}
			}

			/**
			 * Set filterClientUserProfId
			 */
			colName = "filterClientUserProfId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activeclient_filter",
					colName);
			String filterClientUserProfId = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if(!"".equals(filterClientUserProfId)){
				filter.setFilterClientUserProfId(AhRestoreCommons.convertInt(filterClientUserProfId));
			}
			if (RestoreHiveAp.restore_from_fuji_before) {
				if (filter.getFilterClientUserProfId()==0) {
					filter.setFilterClientUserProfId((byte)-1);
				}
			}

			/**
			 * Set filterClientChannel
			 */
			colName = "filterClientChannel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activeclient_filter",
					colName);
			String filterClientChannel = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if(!"".equals(filterClientChannel)){
				filter.setFilterClientChannel(AhRestoreCommons.convertInt(filterClientChannel));
			}
			if (RestoreHiveAp.restore_from_fuji_before) {
				if (filter.getFilterClientChannel()==0) {
					filter.setFilterClientChannel((byte)-1);
				}
			}

			/**
			 * Set filterTopologyMap
			 */
			colName = "filterTopologyMap";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activeclient_filter",
					colName);
			if (isColPresent) {
				String filtertopology = xmlParser.getColVal(i, colName);
				if (!filtertopology.equalsIgnoreCase("null") && !filtertopology.equals("")){
					Long newfiltertopologyId = AhRestoreNewMapTools.getMapMapContainer(Long.parseLong(filtertopology.trim()));
					if (newfiltertopologyId!=null) {
						filter.setFilterTopologyMap(newfiltertopologyId);
					}
				}
//				String mapName = AhRestoreMapTool.getMapMapContainer(filtertopology);
//				if (mapName != null) {
//					MapContainerNode node = AhNewBoMapTool.getMapContainer(mapName);
//					if (node != null) {
//						filter.setFilterTopologyMap(node.getId());
//					}
//				}
			}

			/**
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					 "activeclient_filter", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'activeclient_filter' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			filter.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			filterList.add(filter);

		}

		return filterList;
	}

	public static boolean restoreActiveClientFilter() {
		try {
			List<ActiveClientFilter> filterList = getAllActiveClientFilters();
			if (null == filterList) {
				return false;
			} else {
				QueryUtil.restoreBulkCreateBos(filterList);
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("restore active client filters", e);
			return false;
		}
		return true;
	}

	public static void restoreInterferenceStats() {
		AhDBRestoreTool.restoreTable("hm_interferencestats");
	}

	public static void restoreACSPNeighbor() {
		AhDBRestoreTool.restoreTable("hm_acspneighbor");
	}

	public static void restoreBandWidthHistory() {
		String tableName = "hm_bandwidthsentinel_history";
		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			// change default column info by name
			aa.setColumnInfoByName("channelUltil", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, -1);
			aa.setColumnInfoByName("interferenceUltil", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, -1);
			aa.setColumnInfoByName("txUltil", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, -1);
			aa.setColumnInfoByName("rxUltil", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, -1);
			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(tableName, tableName);
		}
	}

	public static void restoreAdminLoginSession() {
		AhDBRestoreTool.restoreTable("ah_adminlogin_session");
		AhDBRestoreTool.restoreTable("ah_userlogin_session");
	}
	
	public static void restoreAppData() {
		//AhDBRestoreTool.restoreTable("hm_repo_app_data");
		AhDBRestoreTool.restoreTable("hm_repo_app_data_hour");
	}
	
	public static void restoreAppFlow() {
		AhDBRestoreTool.restoreTable("ah_app_flow_day");
		AhDBRestoreTool.restoreTable("ah_app_flow_month");
		AhDBRestoreTool.restoreTable("hm_repo_app_data_all");
		AhDBRestoreTool.restoreTable("hm_repo_app_data_all_last_week");
	}
	
	public static void restoreDeviceStats() {

		AhDBRestoreTool.restoreTable("hm_device_stats");
		
		AhDBRestoreTool.restoreTable("ah_report_compliance");
	}

	public static void restoreInterfaceStats() {
		restoreInterfaceStatsWithTableName("hm_interface_stats");
		restoreInterfaceStatsWithTableName("hm_interface_stats_hour");
		restoreInterfaceStatsWithTableName("hm_interface_stats_day");
		restoreInterfaceStatsWithTableName("hm_interface_stats_week");
	}
	
	public static void restoreInterfaceStatsWithTableName(String tableName) {
		//String tableName = "ap_connect_history_info";
		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			GetColumnValueInterface defaultValue = new GetColumnValueInterface() {
				public String getValue(String columnName, String value, AhRestoreGetXML xmlParser,
						int row) {
					if (columnName.equalsIgnoreCase("radioType")) {
						String ifName;
						try {
							ifName = xmlParser.getColVal(row, "ifname");
						} catch (Exception e) {
							ifName = null;
						}
						if (null == ifName) {
							return String.valueOf(AhInterfaceStats.RADIOTYPE_OTHER);
						} else {
							if(ifName.equalsIgnoreCase("wifi0")) {
								return String.valueOf(AhInterfaceStats.RADIOTYPE_24G);
							} else if(ifName.equalsIgnoreCase("wifi1")) {
								return String.valueOf(AhInterfaceStats.RADIOTYPE_5G);
							} else {
								return String.valueOf(AhInterfaceStats.RADIOTYPE_OTHER);
							}
						}
					} else {
						return "";
					}
				}
			};

			aa.setColumnInfoByName("radioType", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, defaultValue);

			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(tableName, tableName);
		}
	}
	
	public static void restoreSystemLog() {
		AhDBRestoreTool.restoreTable("HM_SYSTEMLOG");
	}
	
	public static void restoreUpgradeLog() {
		AhDBRestoreTool.restoreTable("HM_UPGRADE_LOG");
	}

	public static void restoreClientStats() {
		String tableName = "hm_client_stats";
		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			// change default column info by name
			aa.setColumnInfoByName("radioType", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, 1);
			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(tableName, tableName);
		}
		AhDBRestoreTool.restoreTable("hm_client_stats_hour");
		AhDBRestoreTool.restoreTable("hm_client_stats_day");
		AhDBRestoreTool.restoreTable("hm_client_stats_week");
	}

	public static void restoreNewSLAStats() {
		AhDBRestoreTool.restoreTable("ah_new_sla_stats");
		//AhDBRestoreTool.restoreTable("ah_new_sla_stats_hour");
		//AhDBRestoreTool.restoreTable("ah_new_sla_stats_day");
		//AhDBRestoreTool.restoreTable("ah_new_sla_stats_week");
	}
	
	public static void restoreAhSwitchPortPeriodStats() {
		AhDBRestoreTool.restoreTable("hm_switch_port_period_stats");
	}
	
	/**
	 * restore all tables which are used to do row-up in Report Back-End
	 * @author zdu  
	 * 	zdu@aerohive.com
	 */
	public static void restoreReportRollupTables( ){
	    AhDBRestoreTool.restoreTable("hm_repo_rollup_record");
	    
	    AhDBRestoreTool.restoreTable("hm_repo_device_stats");
	    
	    AhDBRestoreTool.restoreTable("hm_repo_apcpumem_date");
	    AhDBRestoreTool.restoreTable("hm_repo_apcpumem_hour");
	    AhDBRestoreTool.restoreTable("hm_repo_apcpumem_month");
	    AhDBRestoreTool.restoreTable("HM_REPO_APCPUMEM_WEEK");
	    
	    AhDBRestoreTool.restoreTable("hm_repo_app_data_date");
//	    AhDBRestoreTool.restoreTable("hm_repo_app_data_hour");
	    AhDBRestoreTool.restoreTable("hm_repo_app_data_month");
	    AhDBRestoreTool.restoreTable("hm_repo_app_data_week");
	    
	    AhDBRestoreTool.restoreTable("hm_repo_client_data_date");
	    AhDBRestoreTool.restoreTable("hm_repo_client_data_hour");
	    AhDBRestoreTool.restoreTable("hm_repo_client_data_month");
	    AhDBRestoreTool.restoreTable("hm_repo_client_data_week");
	    
	    AhDBRestoreTool.restoreTable("hm_repo_hmcpumem_date");
	    AhDBRestoreTool.restoreTable("hm_repo_hmcpumem_hour");	    
	    AhDBRestoreTool.restoreTable("hm_repo_hmcpumem_month");
	    AhDBRestoreTool.restoreTable("hm_repo_hmcpumem_week");
	    
	    AhDBRestoreTool.restoreTable("hm_repo_network_interface_date");	  
	    AhDBRestoreTool.restoreTable("hm_repo_network_interface_hour");
	    AhDBRestoreTool.restoreTable("hm_repo_network_interface_month");	  
	    AhDBRestoreTool.restoreTable("hm_repo_network_interface_week");
	    
	    AhDBRestoreTool.restoreTable("hm_repo_newsla_stats_date");	  
	    AhDBRestoreTool.restoreTable("hm_repo_newsla_stats_hour");
	    AhDBRestoreTool.restoreTable("hm_repo_newsla_stats_month");	  
	    AhDBRestoreTool.restoreTable("hm_repo_newsla_stats_week");	
	    
	    AhDBRestoreTool.restoreTable("hm_repo_port_interface_date");
	    AhDBRestoreTool.restoreTable("hm_repo_port_interface_hour");	  
	    AhDBRestoreTool.restoreTable("hm_repo_port_interface_month");	  
	    AhDBRestoreTool.restoreTable("hm_repo_port_interface_week");
	    
	    AhDBRestoreTool.restoreTable("hm_repo_ssid_count_date");
	    AhDBRestoreTool.restoreTable("hm_repo_ssid_count_week");	  
	    AhDBRestoreTool.restoreTable("hm_repo_ssid_count_month");	
	    
	    AhDBRestoreTool.restoreTable("hm_repo_switch_period_hour");
	    AhDBRestoreTool.restoreTable("hm_repo_switch_period_date");	  
	    AhDBRestoreTool.restoreTable("hm_repo_switch_period_week");
	    AhDBRestoreTool.restoreTable("hm_repo_switch_period_month");
	    
	    AhDBRestoreTool.restoreTable("hm_repo_client_count_hour");
	    AhDBRestoreTool.restoreTable("hm_repo_client_count_date");	  
	    AhDBRestoreTool.restoreTable("hm_repo_client_count_week");
	    AhDBRestoreTool.restoreTable("hm_repo_client_count_month");
	    
	    //AhDBRestoreTool.restoreTable("hm_repo_rowup_log");
	    AhDBRestoreTool.restoreTable("hm_repo_rowup_status");
	    AhDBRestoreTool.restoreTable("hm_repo_rowup_job");
	    
	}

	/**
	 * retore network device history data using copy csv into db
	 */
	public static void restoreNetworkDeviceHistory() {
		String fileName = "network_device_history";
		String tableName = "network_device_history";

		GetColumnValueInterface defaultValue = new GetColumnValueInterface() {
			public String getValue(String columnName, String value, AhRestoreGetXML xmlParser,
					int row) {

				if (columnName.equalsIgnoreCase("networkpolicy")) {
				    	 if (value != null && !(value.trim().equals(""))
									&& !(value.trim().equalsIgnoreCase("null"))) {
								Long template_id_new = AhRestoreNewMapTools.getMapConfigTemplate(AhRestoreCommons.convertLong(value.trim()));
								if (null != template_id_new) {
									return template_id_new.toString();
								}
								return "0";
						}else{
							return "0";
						}
				} else if (columnName.equalsIgnoreCase("topologygroup")) {
					if (value == null || value.trim().length() == 0
							|| value.equalsIgnoreCase("null")) {
						return "{}";
					}
					 String topologygroup = value.trim();
		    		 String s = topologygroup.substring(1).substring(0,topologygroup.length()-2);
			    	 String[] ss = s.split(",") ;
			    	 String[] array = new String[ss.length];
			    	 if(null != ss && ss.length > 0){
			    		 for(int j=0; j<ss.length; j++){
			    			 if (ss[j] != null && !(ss[j].trim().equals(""))
										&& !(ss[j].trim().equalsIgnoreCase("null"))) {
									Long map_id_new = AhRestoreNewMapTools.getMapMapContainer(AhRestoreCommons.convertLong(ss[j]));
									if (null != map_id_new) {
										array[j] = map_id_new.toString();
									} else {
										array[j] = "";
									}
								}
				    	 }
			    	 }
					
					if (array!=null && array.length >0) {
						StringBuffer sb = new StringBuffer();
						for(String st: array){
							if(!"".equals(st)){
								sb.append(st);
								sb.append(",");
							}
						}
						if(sb != null && !"".equals(sb.toString())){
							String sa = sb.toString().substring(0, sb.toString().lastIndexOf(","));
							return "{"+sa+"}";
						}else{
							return "{}";
						}
					}
					return "{}";
				} else {
					return "";
				}
			}
		};

		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			// change default column info by name
			aa.setColumnInfoByName("networkpolicy", AhConvertXMLToCSV.COLUMN_TYPE_OTHER_USE_DEFAULT,
					defaultValue);
			aa.setColumnInfoByName("topologygroup", AhConvertXMLToCSV.COLUMN_TYPE_STRING_USE_DEFAULT,
					defaultValue);
			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(fileName, tableName);
		}
	}
	
}