package com.ah.be.admin.restoredb;

import com.ah.be.fault.BeFaultConst;
import com.ah.be.log.BeLogTools;
import com.ah.bo.admin.HmDomain;
import com.ah.util.datetime.AhDateTimeUtil;

public class RestoreEventAndAlarm {
	public void restoreEvent() {
		String tableName = "ah_event";
		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			GetColumnValueInterface defaultValue = new GetColumnValueInterface() {
				public String getValue(String colName, String value, AhRestoreGetXML xmlParser,
						int row) {
					try {
						if (colName.equalsIgnoreCase("time")) {
							try {
								String trapTime = xmlParser.getColVal(row, "trapTime");
								if (null == trapTime || trapTime.equalsIgnoreCase("NULL")) {
									return "0";
								}
								return String.valueOf(AhDateTimeUtil.changeDateStringToLong(
										trapTime, "yyyy-MM-dd HH:mm:ss"));
							} catch (Exception e) {
								BeLogTools.error("restore ah_event: get trapTime exception: "
										+ e.getMessage());
								return "0";
							}
						} else if (colName.equalsIgnoreCase("time_zone")) {
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
						}

					} catch (Exception e) {
						AhRestoreDBTools.logRestoreMsg(
								"Exception in update map of table ah_alarm, exception:", e);
					}
					return null;
				}
			};

			aa.setColumnInfoByName("time", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, defaultValue);
			aa.setColumnInfoByName("time_zone", AhConvertXMLToCSV.COLUMN_TYPE_STRING, defaultValue);

			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(tableName, tableName);
		}
	}

	public void restoreAlarm() {
		String tableName = "ah_alarm";
		AhDBTool aa = new AhDBTool();
		if (aa.generateColumnInfo(tableName)) {
			GetColumnValueInterface defaultValue = new GetColumnValueInterface() {
				public String getValue(String colName, String value, AhRestoreGetXML xmlParser,
						int row) {
					try {
						if (colName.equalsIgnoreCase("trap_time")) {
							try {
								String trapTime = xmlParser.getColVal(row, "trapTime");
								if (null == trapTime || trapTime.equalsIgnoreCase("NULL")) {
									return "0";
								}
								return String.valueOf(AhDateTimeUtil.changeDateStringToLong(
										trapTime, "yyyy-MM-dd HH:mm:ss"));
							} catch (Exception e) {
								BeLogTools.error("restore ah_alarm: get trapTime exception: "
										+ e.getMessage());
								return "0";
							}
						} else if (colName.equalsIgnoreCase("trap_time_zone")) {
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
						} else if (colName.equalsIgnoreCase("modify_time")) {
							try {
								String trapTime = xmlParser.getColVal(row, "modifyTime");
								if (null == trapTime || trapTime.equalsIgnoreCase("NULL")) {
									return "0";
								}
								return String.valueOf(AhDateTimeUtil.changeDateStringToLong(
										trapTime, "yyyy-MM-dd HH:mm:ss"));
							} catch (Exception e) {
								BeLogTools.error("restore ah_alarm: get modifyTime exception: "
										+ e.getMessage());
								return "0";
							}
						} else if (colName.equalsIgnoreCase("modify_time_zone")) {
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
						} else if (colName.equalsIgnoreCase("clear_time")) {
							try {
								String trapTime = xmlParser.getColVal(row, "clearTime");
								if (null == trapTime || trapTime.equalsIgnoreCase("NULL")) {
									return "0";
								}
								return String.valueOf(AhDateTimeUtil.changeDateStringToLong(
										trapTime, "yyyy-MM-dd HH:mm:ss"));
							} catch (Exception e) {
								BeLogTools.error("restore ah_alarm: get clearTime exception: "
										+ e.getMessage());
								return "0";
							}
						} else if (colName.equalsIgnoreCase("clear_time_zone")) {
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
						} else if (colName.equalsIgnoreCase("alarmsubtype")) {
							short alarmSubType = BeFaultConst.ALARM_SUBTYPE_UNKNOWN;
							try {
								short alarmType = (short)AhRestoreCommons.convertInt(xmlParser.getColVal(row, "alarmtype"));
								short probablecause = (short)AhRestoreCommons.convertInt(xmlParser.getColVal(row, "probablecause"));
								
								if(alarmType == BeFaultConst.ALARM_TYPE_CAPWAP) {
									if(probablecause == BeFaultConst.ALARM_SUBTYPE_CAPWAP_LINK || 
											probablecause == BeFaultConst.ALARM_SUBTYPE_CAPWAP_PASSPHRASE)
									alarmSubType = probablecause;
								} else if(alarmType == BeFaultConst.ALARM_TYPE_SNMP_RADIO ||
										alarmType == BeFaultConst.ALARM_TYPE_SNMP_CONFIG){
									if(probablecause == BeFaultConst.ALARM_SUBTYPE_FAILURE_KERNELDUMP ||
											probablecause == BeFaultConst.ALARM_SUBTYPE_FAILURE_AHCLEAR ||
											probablecause == BeFaultConst.ALARM_SUBTYPE_FAILURE_AHUNKNOWN ||
											probablecause == BeFaultConst.ALARM_SUBTYPE_FAILURE_AHFLASHFAILURE ||
											probablecause == BeFaultConst.ALARM_SUBTYPE_FAILURE_AHFANFAILURE ||
											probablecause == BeFaultConst.ALARM_SUBTYPE_FAILURE_AHPOWERSUPPLYFAILURE ||
											probablecause == BeFaultConst.ALARM_SUBTYPE_FAILURE_AHSOFTWAREUPGRADEFAILURE ||
											probablecause == BeFaultConst.ALARM_SUBTYPE_FAILURE_AHRADIOFAILURE ||
											probablecause == BeFaultConst.ALARM_SUBTYPE_FAILURE_AHCONFFAILURE)
									alarmSubType = probablecause;
								} else if(alarmType == BeFaultConst.ALARM_TYPE_DOS) {
									alarmSubType = BeFaultConst.ALARM_SUBTYPE_DOS_BSSIDSPOOLING;
								} else if(alarmType == BeFaultConst.ALARM_TYPE_BOMB_WARNING) {
									alarmSubType = BeFaultConst.ALARM_SUBTYPE_BOMB_WARNING_LICENSEEXPIRATION;
								}
								
								return String.valueOf(alarmSubType);
							} catch (AhRestoreException e) {
								return String.valueOf(alarmSubType);
							} catch (AhRestoreColNotExistException e) {
								return String.valueOf(alarmSubType);
							}
							}
					} catch (Exception e) {
						AhRestoreDBTools.logRestoreMsg(
								"Exception in update map of table ah_alarm, exception:", e);
					}
					return null;
				}
			};

			CheckRowValidInterface updateMap = new CheckRowValidInterface() {
				public boolean isValid(AhRestoreGetXML xmlParser, int row) {
					try {
						String macAddress = xmlParser.getColVal(row, "apid");
						String severityStr = xmlParser.getColVal(row, "severity");
						if (null == macAddress || macAddress.equalsIgnoreCase("NULL")) {
							return true;
						}
						if (null == severityStr || severityStr.equalsIgnoreCase("NULL")) {
							return true;
						}
						short severity = (short) Integer.parseInt(severityStr);
						Short s = AhRestoreNewMapTools.getAlarmSeverity(macAddress);
						if (null != s && s > 0) {
							if (severity > s) {
								AhRestoreNewMapTools.setAlarmSeverity(macAddress, severity);
							}
						} else {
							AhRestoreNewMapTools.setAlarmSeverity(macAddress, severity);
						}
					} catch (Exception e) {
						AhRestoreDBTools.logRestoreMsg(
								"Exception in update map of table ah_alarm, exception:", e);
					}
					return true;
				}
			};

			aa.setColumnInfoByName("trap_time", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, defaultValue);
			aa.setColumnInfoByName("trap_time_zone", AhConvertXMLToCSV.COLUMN_TYPE_STRING,
					defaultValue);
			aa
					.setColumnInfoByName("modify_time", AhConvertXMLToCSV.COLUMN_TYPE_OTHER,
							defaultValue);
			aa.setColumnInfoByName("modify_time_zone", AhConvertXMLToCSV.COLUMN_TYPE_STRING,
					defaultValue);
			aa.setColumnInfoByName("clear_time", AhConvertXMLToCSV.COLUMN_TYPE_OTHER, defaultValue);
			aa.setColumnInfoByName("clear_time_zone", AhConvertXMLToCSV.COLUMN_TYPE_STRING,
					defaultValue);

			aa.setColumnInfoByName("alarmsubtype", AhConvertXMLToCSV.COLUMN_TYPE_OTHER,defaultValue);
			
			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.setCheckInterface(updateMap);
			xmlToCSV.importAllXMLToDB(tableName, tableName);
		}
	}
}
