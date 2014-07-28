/* 
 * $RCSfile: RestoreTCAAlarms.java,v $ 
 * $Revision: 1.3.2.1 $ 
 * $Date: 2012/12/25 02:54:38 $ 
 * 
 * Copyright (C) 2012 Aerohive, Inc. All rights reserved. 
 * 
 * This software is the proprietary information of Aerohive, Inc. 
 * Use is subject to license terms. 
 */
package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.tca.TCAAlarm;

/**
 * <p>
 * Title: RestoreTCAAlarms
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * @author xxu
 * @mail xxu@aerohive.com
 * @version 1.0
 */
public class RestoreTCAAlarms {

	public static final String tcaAlarmTableName = "tca_alarm";

	public static boolean restoreTCAAlarms() {

		try {
			long start = System.currentTimeMillis();

			List<TCAAlarm> allTCAAlarms = getAllTCAAlarms();

			if (null == allTCAAlarms || allTCAAlarms.isEmpty()) {
				AhRestoreDBTools.logRestoreMsg("TCAAlarm is null or empty.");
			} else {
				AhRestoreDBTools.logRestoreMsg("Restore TCAAlarm size:"
						+ allTCAAlarms.size());
				// QueryUtil.restoreBulkCreateBos(allTCAAlarms);
				updateDefaultTCAAlarms(allTCAAlarms);
			}
			long end = System.currentTimeMillis();
			AhRestoreDBTools
					.logRestoreMsg("Restore TCAAlarm completely. Count:"
							+ (null == allTCAAlarms ? "0" : allTCAAlarms.size())
							+ ", cost:" + (end - start) + " ms.");
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore TCAAlarm error.", e);
			return false;
		}
		return true;

	}

	private static List<TCAAlarm> getAllTCAAlarms() throws AhRestoreException,
			AhRestoreColNotExistException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of tca_alarm.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tcaAlarmTableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in tca_alarm table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		boolean isColPresent;
		String colName;

		List<TCAAlarm> allTCAAlarms = new ArrayList<TCAAlarm>();
		TCAAlarm tcaAlarm;

		for (int i = 0; i < rowCount; i++) {
			tcaAlarm = new TCAAlarm();
			
			/**
			 * set meatureitem
			 */
			colName = "meatureitem";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tcaAlarmTableName, colName);
			String meatureStr=isColPresent ? xmlParser.getColVal(i, colName) : "";
			tcaAlarm.setMeatureItem(AhRestoreCommons.convertString(meatureStr));

			/**
			 * set interval
			 */
			colName = "interval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tcaAlarmTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.restoreLog(
								BeLogTools.DEBUG,
								"Restore table 'tca_alarm' data be lost, cause: 'interval' column is not exist.");
				/**
				 * The interval column must be exist in the table.
				 */
				continue;
			} else {
				tcaAlarm.setInterval(AhRestoreCommons.convertLong(xmlParser
						.getColVal(i, colName)));
			}
			/**
			 * set highthreshold
			 */
			colName = "highthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tcaAlarmTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.restoreLog(
								BeLogTools.DEBUG,
								"Restore table 'tca_alarm' data be lost, cause: 'highthreshold' column is not exist.");
				/**
				 * The highthreshold column must be exist in the table.
				 */
				continue;
			} else {
				tcaAlarm.setHighThreshold(AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)));
			}
			/**
			 * set lowthreshold
			 */
			colName = "lowthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tcaAlarmTableName, colName);
			if (!isColPresent) {
				BeLogTools
						.restoreLog(
								BeLogTools.DEBUG,
								"Restore table 'tca_alarm' data be lost, cause: 'lowthreshold' column is not exist.");
				/**
				 * The lowthreshold column must be exist in the table.
				 */
				continue;
			} else {
				tcaAlarm.setLowThreshold(AhRestoreCommons.convertLong(xmlParser
						.getColVal(i, colName)));
			}



			/**
			 * set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tcaAlarmTableName, colName);
			String descStr=isColPresent ? xmlParser.getColVal(i, colName) : "";
			tcaAlarm.setDescription(AhRestoreCommons.convertString(descStr));
		

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tcaAlarmTableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
				BeLogTools
						.restoreLog(
								BeLogTools.DEBUG,
								"Restore table 'tca_alarm' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			tcaAlarm.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			allTCAAlarms.add(tcaAlarm);
		}

		return allTCAAlarms;
	}
	
	private static void removeDefaultTCAAlarms() throws Exception{
		List<TCAAlarm> existList = QueryUtil.executeQuery(TCAAlarm.class, null,
				null);
		for (TCAAlarm restoreAlarm : existList) {
			
			QueryUtil.removeBo(TCAAlarm.class, restoreAlarm.getId());
		
		}
		
	}

	private static void updateDefaultTCAAlarms(List<TCAAlarm> allTCAAlarms)
			throws Exception {
		
		removeDefaultTCAAlarms();

//		List<TCAAlarm> resultTCAAlarms = new ArrayList<TCAAlarm>();
		// check the default alarm , if it's created, update it
//		List<TCAAlarm> existList = QueryUtil.executeQuery(TCAAlarm.class, null,
//				null);
//
//		for (TCAAlarm restoreAlarm : allTCAAlarms) {
////			boolean needUpdate = false;
//			for (TCAAlarm alarm : existList) {
//				if (!alarm.getMeatureItem().trim().isEmpty() && alarm.getMeatureItem().trim()
//						.equals(restoreAlarm.getMeatureItem().trim())) {
//					try {
////						needUpdate = true;
//						alarm.setHighThreshold(restoreAlarm.getHighThreshold());
//						alarm.setLowThreshold(restoreAlarm.getLowThreshold());
//						alarm.setInterval(restoreAlarm.getInterval());
//		//				alarm.setDescription(restoreAlarm.getDescription());
//						alarm.setOwner(restoreAlarm.getOwner());
//						QueryUtil.updateBo(alarm);
//
//					} catch (Exception e) {
//						AhRestoreDBTools.logRestoreMsg("Restore TCAAlarm fail:"
//								+ e);
//					}
//					break;
//				}
//			}
//
////			if (!needUpdate) {
////				resultTCAAlarms.add(restoreAlarm);
////			}
//
//		}
//		if (resultTCAAlarms.size() != 0 && !resultTCAAlarms.isEmpty()) {
			QueryUtil.restoreBulkCreateBos(allTCAAlarms);
//		}

	}
}
