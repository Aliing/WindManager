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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.dashboard.AhDashBoardGroup;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.dashboard.AhDashboardLayout;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.dashboard.DashboardComponentData;
import com.ah.bo.dashboard.DashboardComponentMetric;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhNewReport;
import com.ah.util.Tracer;
import com.ah.util.bo.dashboard.DaHelper;

/**
 * @author		Fisher
 * @version		V1.0.0.0
 */
public class RestoreDashboard {
	private static final Tracer log = new Tracer(RestoreDashboard.class.getSimpleName());
	
	/**
	 * below fields are used to get the relation between widget/layout and component key
	 * be sure dashboard components and layouts are restored before widgets
	 */
	private static final boolean blnAdditionalLayoutAdjust = true;
	// map: old dashboard component id and key
	private static Map<Long, Integer> _dacomponent_id_key_map = new HashMap<>();
	// map: new layout id and new dashboard id
	private static Map<Long, Long> _dalayout_dashboard_id_key_map = new HashMap<>();
	// map: new layout id and layout order
	private static Map<Long, Byte> _dalayout_order_map = new HashMap<>();
	// map: new dashboard id and list of new layout id
	private static Map<Long, List<Long>> _dashboard_dalayout_id_key_map = new HashMap<>();
	// map: new layout id and list of old AhDashboardWidget bo
	private static Map<Long, List<AhDashboardWidget>> _dalayout_lost_widgets = new HashMap<>();

	private static List<AhDashboard> getAllAhDashboard() throws AhRestoreColNotExistException,AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of HM_DASHBOARD.xml
		 */
		String tableName="HM_DASHBOARD";
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AhDashboard> profileListInfo = new ArrayList<AhDashboard>();
		boolean isColPresent;
		String colName;
		AhDashboard profileDTO;

		for (int i = 0; i < rowCount; i++) {
			profileDTO = new AhDashboard();

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			profileDTO.setId(Long.valueOf(id));

			colName = "defaultFlag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String defaultFlag = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setDefaultFlag(AhRestoreCommons.convertStringToBoolean(defaultFlag));

			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			profileDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			colName = "pdfHeader";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String pdfHeader = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setPdfHeader(pdfHeader);

			colName = "pdfFooter";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String pdfFooter = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setPdfFooter(pdfFooter);

			colName = "pdfSummary";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String pdfSummary = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setPdfSummary(pdfSummary);
			
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setDescription(description);

			colName = "userName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String userName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setUserName(userName);

			colName = "active";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String active = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setActive(AhRestoreCommons.convertStringToBoolean(active));

			colName = "dashName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String dashName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setDashName(dashName);

			colName = "daType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String daType = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			profileDTO.setDaType(AhRestoreCommons.convertInt(daType));
			
			colName = "position";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String position = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setPosition(AhRestoreCommons.convertInt(position));

			colName = "reportScheduleStatus";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reportScheduleStatus = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setReportScheduleStatus(AhRestoreCommons.convertInt(reportScheduleStatus));

//			colName = "location_id";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
//			String location_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			if (!location_id.equals("") && !location_id.trim().equalsIgnoreCase("null")) {
//				Long newLocationId = AhRestoreNewMapTools.getMapMapContainer(Long.parseLong(location_id.trim()));
//				MapContainerNode mapContainer = AhRestoreNewTools.CreateBoWithId(MapContainerNode.class,newLocationId);
//				profileDTO.setLocation(mapContainer);
//			}

			colName = "objectType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String objectType = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
			profileDTO.setObjectType(AhRestoreCommons.convertString(objectType));

			colName = "objectId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String objectId = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
			if(profileDTO.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY)) {
				if (!objectId.equals(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY))) {
					Long parseId=-1l;
					try {
						parseId =Long.parseLong(objectId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Long tmpID = AhRestoreNewMapTools.getMapConfigTemplate(parseId);
					if(tmpID==null) {
						profileDTO.setObjectId(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
						profileDTO.setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
					} else {
						profileDTO.setObjectId(tmpID.toString());
					}
				} else {
					profileDTO.setObjectId(objectId);
				}
			} else if(profileDTO.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_MAP)) {
				if (!objectId.equals(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_MAP))) {
					Long parseId=-1l;
					try {
						parseId =Long.parseLong(objectId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Long tmpID = AhRestoreNewMapTools.getMapMapContainer(parseId);
					if(tmpID==null) {
						profileDTO.setObjectId(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
						profileDTO.setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
					} else {
						profileDTO.setObjectId(tmpID.toString());
					}
				} else {
					profileDTO.setObjectId(objectId);
				}
			} else {
				profileDTO.setObjectId(objectId);
			}

			colName = "filterObjectType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String filterObjectType = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
			profileDTO.setFilterObjectType(AhRestoreCommons.convertString(filterObjectType));

			colName = "filterObjectId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String filterObjectId = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);

//			if(profileDTO.getFilterObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_USERPROFILE_ALL)) {
//				if (!filterObjectId.equals(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_USERPROFILE_ALL))) {
//					Long parseId=-1l;
//					try {
//						parseId =Long.parseLong(filterObjectId);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					Long tmpID = AhRestoreNewMapTools.getMapUserProfile(parseId);
//					if(tmpID==null) {
//						profileDTO.setFilterObjectId(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
//						profileDTO.setFilterObjectType(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
//					} else {
//						profileDTO.setFilterObjectId(tmpID.toString());
//					}
//				} else {
//					profileDTO.setFilterObjectId(filterObjectId);
//				}
//			} else {
					profileDTO.setFilterObjectId(filterObjectId);
//			}

			colName = "selectTimeType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String selectTimeType = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(AhDashboard.TAB_TIME_LASTDAY);
			profileDTO.setSelectTimeType(AhRestoreCommons.convertInt(selectTimeType));

			colName = "customStartTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String customStartTime = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setCustomStartTime(AhRestoreCommons.convertLong(customStartTime));

			colName = "customEndTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String customEndTime = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setCustomEndTime(AhRestoreCommons.convertLong(customEndTime));

			colName = "enableTimeLocal";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String enableTimeLocal = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setEnableTimeLocal(AhRestoreCommons.convertStringToBoolean(enableTimeLocal));

			colName = "bgRollup";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String bgRollup = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setBgRollup(AhRestoreCommons.convertStringToBoolean(bgRollup));

			colName = "refrequency";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String refrequency = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(AhNewReport.NEW_REPORT_FREQUENCY_DAILY);
			profileDTO.setRefrequency(AhRestoreCommons.convertInt(refrequency));

			colName = "reCustomDay";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reCustomDay = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setReCustomDay(AhRestoreCommons.convertStringToBoolean(reCustomDay));

			colName = "reCustomTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reCustomTime = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setReCustomTime(AhRestoreCommons.convertStringToBoolean(reCustomTime));

			colName = "reCustomDayValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reCustomDayValue = isColPresent ? xmlParser.getColVal(i, colName) : "0111110";
			profileDTO.setReCustomDayValue(reCustomDayValue);

			colName = "reCustomTimeStart";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reCustomTimeStart = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setReCustomTimeStart(AhRestoreCommons.convertInt(reCustomTimeStart));

			colName = "reCustomTimeEnd";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reCustomTimeEnd = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setReCustomTimeEnd(AhRestoreCommons.convertInt(reCustomTimeEnd));

			colName = "reEmailAddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reEmailAddress = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setReEmailAddress(AhRestoreCommons.convertString(reEmailAddress));

			colName = "reWeekStart";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reWeekStart = isColPresent ? xmlParser.getColVal(i, colName) : "7";
			profileDTO.setReWeekStart(AhRestoreCommons.convertInt(reWeekStart));

			colName = "reCmTimeType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reCmTimeType = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			profileDTO.setReCmTimeType(AhRestoreCommons.convertInt(reCmTimeType));

			colName = "reCmTimePeriod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reCmTimePeriod = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			profileDTO.setReCmTimePeriod(AhRestoreCommons.convertInt(reCmTimePeriod));

			colName = "reCmTimeStartDayType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reCmTimeStartDayType = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			profileDTO.setReCmTimeStartDayType(AhRestoreCommons.convertInt(reCmTimeStartDayType));

			colName = "reCmTimeStartDayValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reCmTimeStartDayValue = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			profileDTO.setReCmTimeStartDayValue(AhRestoreCommons.convertInt(reCmTimeStartDayValue));

			colName = "reCmTimeStartMontyYear";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reCmTimeStartMontyYear = isColPresent ? xmlParser.getColVal(i, colName) : "14";
			profileDTO.setReCmTimeStartMontyYear(AhRestoreCommons.convertInt(reCmTimeStartMontyYear));

			colName = "reCmTimeStartSepcYear";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String reCmTimeStartSepcYear = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
			profileDTO.setReCmTimeStartSepcYear(AhRestoreCommons.convertInt(reCmTimeStartSepcYear));

			profileListInfo.add(profileDTO);
		}

		return profileListInfo;
	}

	public static boolean restoreAhDashboard() {
		try {
			List<AhDashboard> allProfile = getAllAhDashboard();
			if(null == allProfile) {
				return false;
			} else {
				List<Long> lOldId = new ArrayList<Long>();

				for (AhDashboard filter : allProfile) {
					lOldId.add(filter.getId());
				}

				QueryUtil.restoreBulkCreateBos(allProfile);

				for(int i=0; i < allProfile.size(); ++i) {
					AhRestoreNewMapTools.setMapAhDashboard(lOldId.get(i), allProfile.get(i).getId());
				}
			}
		} catch(Exception e) {
			log.error(e);
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}


	private static List<AhDashboardLayout> getAllAhDashboardLayout() throws AhRestoreColNotExistException,AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of HM_DASHBOARD_LAYOUT.xml
		 */
		String tableName="HM_DASHBOARD_LAYOUT";
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AhDashboardLayout> profileListInfo = new ArrayList<AhDashboardLayout>();
		boolean isColPresent;
		String colName;
		AhDashboardLayout profileDTO;

		for (int i = 0; i < rowCount; i++) {
			profileDTO = new AhDashboardLayout();

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			profileDTO.setId(Long.valueOf(id));

			colName = "sizeType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String sizeType = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(AhDashboardLayout.SIZE_MEDIUM);
			profileDTO.setSizeType(Byte.valueOf(sizeType));

			colName = "width";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String width = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			profileDTO.setWidth(AhRestoreCommons.convertDouble(width));

			colName = "itemOrder";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String itemOrder = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setItemOrder(Byte.valueOf(itemOrder));

			colName = "dashboard_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String dashboard_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!dashboard_id.equals("") && !dashboard_id.trim().equalsIgnoreCase("null")) {
				Long newdashboard_id = AhRestoreNewMapTools.getMapAhDashboard(Long.parseLong(dashboard_id.trim()));
				AhDashboard ahDashboard =AhRestoreNewTools.CreateBoWithId(AhDashboard.class,newdashboard_id);
				profileDTO.setDashboard(ahDashboard);
			} else {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'dashboard_id' column is not available.");
				continue;
			}
			
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			profileDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
			
			// not need set tabId, will be set when set dashboard_id
//			colName = "tabId";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
//			String tabId = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			profileDTO.setTabId(tabId);

			profileListInfo.add(profileDTO);
		}

		return profileListInfo;
	}

	public static boolean restoreAhDashboardLayout() {
		try {
			List<AhDashboardLayout> allProfile = getAllAhDashboardLayout();
			if(null == allProfile) {
				return false;
			} else {
				List<Long> lOldId = new ArrayList<Long>();

				for (AhDashboardLayout filter : allProfile) {
					lOldId.add(filter.getId());
				}

				QueryUtil.restoreBulkCreateBos(allProfile);

				for(int i=0; i < allProfile.size(); ++i) {
					AhRestoreNewMapTools.setMapAhDashboardLayout(lOldId.get(i), allProfile.get(i).getId());
				}
				if (blnAdditionalLayoutAdjust) {
					for(int i=0; i < allProfile.size(); ++i) {
						Long daId = Long.valueOf(allProfile.get(i).getTabId());
						_dalayout_dashboard_id_key_map.put(allProfile.get(i).getId(), daId);
						if (!_dashboard_dalayout_id_key_map.containsKey(daId)) {
							_dashboard_dalayout_id_key_map.put(daId, new ArrayList<Long>());
						}
						_dashboard_dalayout_id_key_map.get(daId).add(allProfile.get(i).getId());
						_dalayout_order_map.put(allProfile.get(i).getId(), allProfile.get(i).getItemOrder());
					}
				}
			}
		} catch(Exception e) {
			log.error(e);
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<AhDashboardWidget> getAllAhDashboardWidget() throws AhRestoreColNotExistException,AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of HM_DASHBOARD_WIDGET.xml
		 */
		String tableName="HM_DASHBOARD_WIDGET";
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AhDashboardWidget> profileListInfo = new ArrayList<AhDashboardWidget>();
		boolean isColPresent;
		String colName;
		AhDashboardWidget profileDTO;

		for (int i = 0; i < rowCount; i++) {
			profileDTO = new AhDashboardWidget();

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			profileDTO.setId(Long.valueOf(id));

			colName = "mainTitle";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String mainTitle = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setMainTitle(mainTitle);

			colName = "sizeType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String sizeType = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(AhDashboardWidget.SIZE_LARGE);
			profileDTO.setSizeType(Byte.valueOf(sizeType));

			colName = "width";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String width = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			profileDTO.setWidth(AhRestoreCommons.convertDouble(width));

			colName = "chartHeight";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String chartHeight = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			profileDTO.setChartHeight(AhRestoreCommons.convertDouble(chartHeight));

			colName = "itemOrder";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String itemOrder = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setItemOrder(Byte.valueOf(itemOrder));

			colName = "da_layout_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String da_layout_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!da_layout_id.equals("") && !da_layout_id.trim().equalsIgnoreCase("null")) {
				Long newda_layout_id = AhRestoreNewMapTools.getMapAhDashboardLayout(Long.parseLong(da_layout_id.trim()));
				AhDashboardLayout ahDashboardLayout =AhRestoreNewTools.CreateBoWithId(AhDashboardLayout.class,newda_layout_id);
				if(ahDashboardLayout==null) {
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'da_layout_id' column is not available.");
					continue;
				}
				profileDTO.setDaLayout(ahDashboardLayout);
			} else {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'da_layout_id' column is not available.");
				continue;
			}

//			colName = "location_id";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
//			String location_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			if (!location_id.equals("") && !location_id.trim().equalsIgnoreCase("null")) {
//				Long newLocationId = AhRestoreNewMapTools.getMapMapContainer(Long.parseLong(location_id.trim()));
//				MapContainerNode mapContainer = AhRestoreNewTools.CreateBoWithId(MapContainerNode.class,newLocationId);
//				profileDTO.setLocation(mapContainer);
//			}

			colName = "objectType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String objectType = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
			profileDTO.setObjectType(AhRestoreCommons.convertString(objectType));

			colName = "objectId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String objectId = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
			if(profileDTO.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY)) {
				if (!objectId.equals(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY))) {
					Long parseId=-1l;
					try {
						parseId =Long.parseLong(objectId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Long tmpID = AhRestoreNewMapTools.getMapConfigTemplate(parseId);
					if(tmpID==null) {
						profileDTO.setObjectId(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
						profileDTO.setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
					} else {
						profileDTO.setObjectId(tmpID.toString());
					}
				} else {
					profileDTO.setObjectId(objectId);
				}
			} else if(profileDTO.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_MAP)) {
				if (!objectId.equals(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_MAP))) {
					Long parseId=-1l;
					try {
						parseId =Long.parseLong(objectId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Long tmpID = AhRestoreNewMapTools.getMapMapContainer(parseId);
					if(tmpID==null) {
						profileDTO.setObjectId(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
						profileDTO.setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
					} else {
						profileDTO.setObjectId(tmpID.toString());
					}
				} else {
					profileDTO.setObjectId(objectId);
				}
			} else {
				profileDTO.setObjectId(objectId);
			}

			colName = "filterObjectType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String filterObjectType = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
			profileDTO.setFilterObjectType(AhRestoreCommons.convertString(filterObjectType));

			colName = "filterObjectId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String filterObjectId = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);

//			if(profileDTO.getFilterObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_USERPROFILE_ALL)) {
//				if (!filterObjectId.equals(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_USERPROFILE_ALL))) {
//					Long parseId=-1l;
//					try {
//						parseId =Long.parseLong(filterObjectId);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					Long tmpID = AhRestoreNewMapTools.getMapUserProfile(parseId);
//					if(tmpID==null) {
//						profileDTO.setFilterObjectId(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
//						profileDTO.setFilterObjectType(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
//					} else {
//						profileDTO.setFilterObjectId(tmpID.toString());
//					}
//				} else {
//					profileDTO.setFilterObjectId(filterObjectId);
//				}
//			} else {
					profileDTO.setFilterObjectId(filterObjectId);
//			}

			colName = "selectTimeType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String selectTimeType = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(AhDashboard.TAB_TIME_LASTDAY);
			profileDTO.setSelectTimeType(AhRestoreCommons.convertInt(selectTimeType));

			colName = "customStartTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String customStartTime = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setCustomStartTime(AhRestoreCommons.convertLong(customStartTime));

			colName = "customEndTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String customEndTime = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setCustomEndTime(AhRestoreCommons.convertLong(customEndTime));

			colName = "enableTimeLocal";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String enableTimeLocal = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setEnableTimeLocal(AhRestoreCommons.convertStringToBoolean(enableTimeLocal));

			colName = "blnChecked";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String blnChecked = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setBlnChecked(AhRestoreCommons.convertStringToBoolean(blnChecked));

			colName = "blnDdSpecialType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String blnDdSpecialType = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setBlnDdSpecialType(AhRestoreCommons.convertStringToBoolean(blnDdSpecialType));
			
			colName = "specifyType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String specifyType = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(DashboardComponent.WIDGET_SPECIFY_TYPE_NONE);
			profileDTO.setSpecifyType(AhRestoreCommons.convertInt(specifyType));

			colName = "specifyName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String specifyName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setSpecifyName(specifyName);
			
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			profileDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
			
			
			/**
			 * Please put this logic at the end, it's very important!! Otherwise, some errors will be imported
			 */
			// include reportId
			colName = "widget_config_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String widget_config_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!widget_config_id.equals("") && !widget_config_id.trim().equalsIgnoreCase("null")) {
				
				profileDTO.setOldDashboardComponentId(Long.parseLong(widget_config_id.trim()));
				
				Long newwidget_config_id = AhRestoreNewMapTools.getMapAhDashboardComponent(Long.parseLong(widget_config_id.trim()));
				DashboardComponent dashboardComponent =AhRestoreNewTools.CreateBoWithId(DashboardComponent.class,newwidget_config_id);
				if(dashboardComponent==null) {
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'widget_config_id' column is not available.");
					
					if (blnAdditionalLayoutAdjust) {
						Long daLayoutId = profileDTO.getDaLayout().getId();
						if (!_dalayout_lost_widgets.containsKey(daLayoutId)) {
							_dalayout_lost_widgets.put(daLayoutId, new ArrayList<AhDashboardWidget>());
						}
						_dalayout_lost_widgets.get(daLayoutId).add(profileDTO);
					}
					
					continue;
				}
				profileDTO.setWidgetConfig(dashboardComponent);
			} else {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'widget_config_id' column is not available.");
				continue;
			}
			/**
			 * Please do not put xml data getting logic here, just put before!!!!!
			 */
			
			profileListInfo.add(profileDTO);
		}
		
		return profileListInfo;
	}

	public static boolean restoreAhDashboardWidget() {
		try {
			List<AhDashboardWidget> allProfile = getAllAhDashboardWidget();
			if(null == allProfile) {
				return false;
			} else {
				List<Long> lOldId = new ArrayList<Long>();

				adjustDashboardWidgetOrderIfNeeded(allProfile);
				
				for (AhDashboardWidget filter : allProfile) {
					lOldId.add(filter.getId());
				}

				QueryUtil.restoreBulkCreateBos(allProfile);

				for(int i=0; i < allProfile.size(); ++i) {
					AhRestoreNewMapTools.setMapAhDashboardWidget(lOldId.get(i), allProfile.get(i).getId());
				}
			}
		} catch(Exception e) {
			log.error(e);
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	private static Set<Long> _widget_ids_in_lost_widgets = null;
	private static boolean isWidgetInLostWidgets(AhDashboardWidget widget) {
		if (_widget_ids_in_lost_widgets == null) {
			_widget_ids_in_lost_widgets = new HashSet<>();
			for (Long idTmp : _dalayout_lost_widgets.keySet()) {
				List<AhDashboardWidget> widgets = _dalayout_lost_widgets.get(idTmp);
				if (widgets != null
						&& !widgets.isEmpty()) {
					for (AhDashboardWidget widgetTmp : widgets) {
						_widget_ids_in_lost_widgets.add(widgetTmp.getId());
					}
				}
			}
		}
		return _widget_ids_in_lost_widgets.contains(widget.getId());
	}
	private static boolean adjustDashboardWidgetOrderIfNeeded(List<AhDashboardWidget> widgetProfiles) {
		// can returned from here if once do not need this function in some situations
		if (!blnAdditionalLayoutAdjust) {
			return true;
		}
		if (widgetProfiles == null
				|| widgetProfiles.isEmpty()) {
			return true;
		}
		
		HmDomain owner = widgetProfiles.get(0).getOwner();
		DashboardComponent deviceClientStatusComponent = QueryUtil.findBoByAttribute(DashboardComponent.class, 
				"key", 
				DaHelper.DEVICE_STATUS_KEY,
				owner.getId());
		
		if (widgetProfiles != null
				&& !widgetProfiles.isEmpty()) {
			Set<Long> _has_old_client_status_set = new HashSet<>();
			Map<Long, List<AhDashboardWidget>> _layout_widgets_map = new HashMap<>();
			for (AhDashboardWidget widget : widgetProfiles) {
				Long layoutId = widget.getDaLayout().getId();
				if (!_layout_widgets_map.containsKey(layoutId)) {
					_layout_widgets_map.put(layoutId, new ArrayList<AhDashboardWidget>());
				}
				_layout_widgets_map.get(layoutId).add(widget);
				
				if (widget.getOldDashboardComponentId() != null
						&& _dacomponent_id_key_map.containsKey(widget.getOldDashboardComponentId())
						&& _dacomponent_id_key_map.get(widget.getOldDashboardComponentId()) == DaHelper.CLIENT_STATUS_KEY) {
					_has_old_client_status_set.add(layoutId);
				}
			}
			
			for (Long idTmp : _dalayout_lost_widgets.keySet()) {
				List<AhDashboardWidget> widgets = _dalayout_lost_widgets.get(idTmp);
				if (widgets != null
						&& !widgets.isEmpty()) {
					for (AhDashboardWidget widget : widgets) {
						if (widget.getOldDashboardComponentId() != null
								&& _dacomponent_id_key_map.containsKey(widget.getOldDashboardComponentId())
								&& _dacomponent_id_key_map.get(widget.getOldDashboardComponentId()) == DaHelper.CLIENT_STATUS_KEY) {
							_has_old_client_status_set.add(idTmp);
						}
					}
				}
			}
			
			if (!_has_old_client_status_set.isEmpty()) {
				for (Long layoutId : _has_old_client_status_set) {
					List<Long> allLayouts = _dashboard_dalayout_id_key_map.get(_dalayout_dashboard_id_key_map.get(layoutId));
					List<AhDashboardWidget> widgets = new ArrayList<>();
					for (Long layoutId1 : allLayouts) {
						if (_layout_widgets_map.containsKey(layoutId1)
								&& _layout_widgets_map.get(layoutId1) != null) {
							widgets.addAll(_layout_widgets_map.get(layoutId1));
						}
						if (_dalayout_lost_widgets.containsKey(layoutId1)
								&& _dalayout_lost_widgets.get(layoutId1) != null) {
							widgets.addAll(_dalayout_lost_widgets.get(layoutId1));
						}
					}
					Collections.sort(widgets, new Comparator<AhDashboardWidget>() {

						@Override
						public int compare(AhDashboardWidget o1,
								AhDashboardWidget o2) {
							byte column1 = _dalayout_order_map.get(o1.getDaLayout().getId()),
								column2 = _dalayout_order_map.get(o2.getDaLayout().getId());
							if (o1.getItemOrder() > o2.getItemOrder()) {
								return 1;
							} else if (o1.getItemOrder() < o2.getItemOrder()) {
								return -1;
							} else {
								if (column1 > column2) {
									return 1;
								} else {
									return -1;
								}
							}
						}
						
					});
					
					boolean blnHasDealtDeviceClientCombine = false;
					List<AhDashboardWidget> widgets2Remove = new ArrayList<>();
					for (AhDashboardWidget widget : widgets) {
						int widgetKey = _dacomponent_id_key_map.get(widget.getOldDashboardComponentId());
						boolean blnHasAdded2Remove = false;
						if (widgetKey == DaHelper.CLIENT_STATUS_KEY) {
							if (blnHasDealtDeviceClientCombine) {
								blnHasAdded2Remove = true;
								widgets2Remove.add(widget);
							} else {
								widget.setWidgetConfig(deviceClientStatusComponent);
								blnHasDealtDeviceClientCombine = true;
								if (isWidgetInLostWidgets(widget)) {
									widgetProfiles.add(widget);
								}
							}
						} else if (widgetKey == DaHelper.DEVICE_STATUS_KEY) {
							if (blnHasDealtDeviceClientCombine) {
								blnHasAdded2Remove = true;
								widgets2Remove.add(widget);
								widgetProfiles.remove(widget);
							} else {
								blnHasDealtDeviceClientCombine = true;
							}
						}
						
						if (widget.getWidgetConfig() == null
								&& !blnHasAdded2Remove) {
							blnHasAdded2Remove = true;
							widgets2Remove.add(widget);
						}
					}
					
					if (!widgets2Remove.isEmpty()) {
						widgets.removeAll(widgets2Remove);
					}
					
					List<Long> orderedLayouts = new ArrayList<Long>(allLayouts);
					Collections.sort(orderedLayouts, new Comparator<Long>() {
						@Override
						public int compare(Long o1, Long o2) {
							Byte order1 = _dalayout_order_map.get(o1),
									order2 = _dalayout_order_map.get(o2);
							return order1.compareTo(order2);
						}
					});
					List<AhDashboardLayout> orderedLayoutBos = new ArrayList<>(orderedLayouts.size());
					for (Long idTmp : orderedLayouts) {
						orderedLayoutBos.add(AhRestoreNewTools.CreateBoWithId(AhDashboardLayout.class, idTmp));
					}
					
					int widgetLen = widgets.size();
					if (widgetLen > 0
							&& orderedLayoutBos.size() > 0) {
						int curWidgetPos = 0;
						byte curOrder = 0;
						do {
							for (AhDashboardLayout layoutBo : orderedLayoutBos) {
								AhDashboardWidget widgetTmp = widgets.get(curWidgetPos++);
								widgetTmp.setItemOrder(curOrder);
								widgetTmp.setDaLayout(layoutBo);
								if (curWidgetPos >= widgetLen) {
									break;
								}
							}
							curOrder++;
						} while (curWidgetPos < widgetLen);
					}
				}
			}
		}
		
		return true;
	}

	private static Map<String, List<DashboardComponentData>> getAllDashboardComponentData() throws AhRestoreColNotExistException,AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of DASHBOARD_COMPONENT_DATA.xml
		 */
		String tableName="DASHBOARD_COMPONENT_DATA";
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<DashboardComponentData>> profileListInfo = new HashMap<String, List<DashboardComponentData>>();
		boolean isColPresent;
		String colName;
		DashboardComponentData profileDTO;

		for (int i = 0; i < rowCount; i++) {
			profileDTO = new DashboardComponentData();

			colName = "component_metric_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String component_metric_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (component_metric_id == null || component_metric_id.trim().equals("")
					|| component_metric_id.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'component_metric_id' column is not available.");
					continue;
			}

			colName = "groupIndex";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String groupIndex = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setGroupIndex(AhRestoreCommons.convertInt(groupIndex));

			colName = "positionIndex";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String positionIndex = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setPositionIndex(AhRestoreCommons.convertInt(positionIndex));

			colName = "sourceData";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String sourceData = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (sourceData == null || sourceData.trim().equals("")
					|| sourceData.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'sourceData' column is not available.");
				continue;
			}
			profileDTO.setSourceData(sourceData);

			colName = "displayName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String displayName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setDisplayName(displayName);

			colName = "displayValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String displayValue = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setDisplayValue(displayValue);

			colName = "displayValueKey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String displayValueKey = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setDisplayValueKey(displayValueKey);

			colName = "levelBreakDown";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String levelBreakDown = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setLevelBreakDown(Short.valueOf(levelBreakDown));

			colName = "enableBreakdown";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String enableBreakdown = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setEnableBreakdown(AhRestoreCommons.convertStringToBoolean(enableBreakdown));

			colName = "validBreakdown";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String validBreakdown = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setValidBreakdown(AhRestoreCommons.convertStringToBoolean(validBreakdown));

			colName = "enableDisplayTotal";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String enableDisplayTotal = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setEnableDisplayTotal(AhRestoreCommons.convertStringToBoolean(enableDisplayTotal));
			
			colName = "drillDownType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String drillDownType = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setDrillDownType(AhRestoreCommons.convertString(drillDownType));
			
			colName = "drillDownValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String drillDownValue = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setDrillDownValue(AhRestoreCommons.convertString(drillDownValue));

			if (profileListInfo.get(component_metric_id) == null) {
				List<DashboardComponentData> profileList= new ArrayList<DashboardComponentData>();
				profileList.add(profileDTO);
				profileListInfo.put(component_metric_id, profileList);
			} else {
				profileListInfo.get(component_metric_id).add(profileDTO);
			}
		}

		return profileListInfo;
	}

	private static List<DashboardComponentMetric> getAllAhDashboardMetric() throws AhRestoreColNotExistException,AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of DASHBOARD_COMPONENT_METRIC.xml
		 */
		String tableName="DASHBOARD_COMPONENT_METRIC";
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<DashboardComponentMetric> profileListInfo = new ArrayList<DashboardComponentMetric>();
		boolean isColPresent;
		String colName;
		DashboardComponentMetric profileDTO;

		for (int i = 0; i < rowCount; i++) {
			profileDTO = new DashboardComponentMetric();

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			profileDTO.setId(Long.valueOf(id));

			colName = "key";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String key = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setKey(AhRestoreCommons.convertInt(key));

			colName = "metricName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String metricName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setMetricName(metricName);

			colName = "defaultFlag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String defaultFlag = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setDefaultFlag(AhRestoreCommons.convertStringToBoolean(defaultFlag));

			if(profileDTO.isDefaultFlag()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("key", Integer.valueOf(key));
				DashboardComponentMetric newBo = HmBeParaUtil.getDefaultProfile(DashboardComponentMetric.class, map);
				if (null != newBo) {
					AhRestoreNewMapTools.setMapAhDashboardMetric(AhRestoreCommons.convertLong(id), newBo.getId());
				}
				continue;
			}

			colName = "sourceType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String sourceType = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setSourceType(sourceType);

			colName = "displayName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String displayName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setDisplayName(displayName);

			colName = "displayValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String displayValue = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setDisplayValue(displayValue);

			colName = "displayValueKey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String displayValueKey = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setDisplayValueKey(displayValueKey);

			colName = "orderByMetric";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String orderByMetric = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setOrderByMetric(orderByMetric);

			colName = "orderByDesc";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String orderByDesc = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setOrderByDesc(AhRestoreCommons.convertStringToBoolean(orderByDesc));

			colName = "groupBy";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String groupBy = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setGroupBy(AhRestoreCommons.convertInt(groupBy));

			colName = "topNumber";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String topNumber = isColPresent ? xmlParser.getColVal(i, colName) : "10";
			profileDTO.setTopNumber(AhRestoreCommons.convertInt(topNumber));

			colName = "filterDataType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String filterDataType = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setFilterDataType(AhRestoreCommons.convertInt(filterDataType));

			colName = "createWidget";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String createWidget = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setCreateWidget(AhRestoreCommons.convertStringToBoolean(createWidget));

			colName = "specifyType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String specifyType = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setSpecifyType(AhRestoreCommons.convertInt(specifyType));

			colName = "createTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String createTime = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setCreateTime(AhRestoreCommons.convertLong(createTime));

			colName = "homeonly";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String homeonly = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setHomeonly(AhRestoreCommons.convertStringToBoolean(homeonly));
			
			colName = "valueRange";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String valueRange = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setValueRange(AhRestoreCommons.convertString(valueRange));
			
			colName = "componentGroup";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String componentGroup = isColPresent ? xmlParser.getColVal(i, colName) : "8";
			profileDTO.setComponentGroup(AhRestoreCommons.convertInt(componentGroup));

			colName = "chartType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String chartType = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setChartType(chartType);

			colName = "chartInverted";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String chartInverted = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setChartInverted(AhRestoreCommons.convertStringToBoolean(chartInverted));

			colName = "blnOverTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String blnOverTime = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setBlnOverTime(AhRestoreCommons.convertStringToBoolean(blnOverTime));
			
			colName = "drillDownType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String drillDownType = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setDrillDownType(AhRestoreCommons.convertString(drillDownType));
			
			colName = "drillDownValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String drillDownValue = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setDrillDownValue(AhRestoreCommons.convertString(drillDownValue));
			
			colName = "enabledHtml";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String enabledHtml = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setEnabledHtml(AhRestoreCommons.convertStringToBoolean(enabledHtml));

			colName = "customHtml";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String customHtml = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setCustomHtml(customHtml);

			colName = "enableExampleData";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String enableExampleData = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setEnableExampleData(AhRestoreCommons.convertStringToBoolean(enableExampleData));

			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			profileDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
			
			profileListInfo.add(profileDTO);
		}

		return profileListInfo;
	}

	public static boolean restoreAhDashboardMetric() {
		try {
			List<DashboardComponentMetric> allProfile = getAllAhDashboardMetric();
			Map<String, List<DashboardComponentData>> profileListInfo = getAllDashboardComponentData();
			if(null == allProfile) {
				return false;
			} else {
				for (DashboardComponentMetric tempProfile : allProfile) {
					List<DashboardComponentData> componentData = profileListInfo.get(tempProfile.getId().toString());
					if(componentData!=null && !componentData.isEmpty()) {
						tempProfile.setComponentData(componentData);
					}
				}

				List<Long> lOldId = new ArrayList<Long>();

				for (DashboardComponentMetric filter : allProfile) {
					lOldId.add(filter.getId());
				}

				QueryUtil.restoreBulkCreateBos(allProfile);

				for(int i=0; i < allProfile.size(); ++i) {
					AhRestoreNewMapTools.setMapAhDashboardMetric(lOldId.get(i), allProfile.get(i).getId());
				}
			}
		} catch(Exception e) {
			log.error(e);
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<DashboardComponent> getAllAhDashboardComponent() throws AhRestoreColNotExistException,AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of HM_DASHBOARD_COMPONENT.xml
		 */
		String tableName="HM_DASHBOARD_COMPONENT";
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<DashboardComponent> profileListInfo = new ArrayList<DashboardComponent>();
		boolean isColPresent;
		String colName;
		DashboardComponent profileDTO;

		for (int i = 0; i < rowCount; i++) {
			profileDTO = new DashboardComponent();

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			profileDTO.setId(Long.valueOf(id));

			colName = "key";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String key = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setKey(AhRestoreCommons.convertInt(key));

			colName = "componentName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String componentName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setComponentName(componentName);

			colName = "defaultFlag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String defaultFlag = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setDefaultFlag(AhRestoreCommons.convertStringToBoolean(defaultFlag));

			if (blnAdditionalLayoutAdjust) {
				_dacomponent_id_key_map.put(AhRestoreCommons.convertLong(id), profileDTO.getKey());
			}
			
			if(profileDTO.isDefaultFlag()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("key", Integer.valueOf(key));
				DashboardComponent newBo = HmBeParaUtil.getDefaultProfile(DashboardComponent.class, map);
				if (null != newBo) {
					AhRestoreNewMapTools.setMapAhDashboardComponent(AhRestoreCommons.convertLong(id), newBo.getId());
				}
				continue;
			}

			colName = "componentType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String componentType = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setComponentType(componentType);

			colName = "componentGroup";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String componentGroup = isColPresent ? xmlParser.getColVal(i, colName) : "8";
			profileDTO.setComponentGroup(AhRestoreCommons.convertInt(componentGroup));

			colName = "sourceType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String sourceType = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setSourceType(sourceType);

			colName = "metric_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String metric_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (metric_id!=null && !metric_id.equals("") && !metric_id.trim().equalsIgnoreCase("null")) {
				Long newmetric_id = AhRestoreNewMapTools.getMapAhDashboardMetric(Long.parseLong(metric_id.trim()));
				DashboardComponentMetric dashboardComponentMetric =AhRestoreNewTools.CreateBoWithId(DashboardComponentMetric.class,newmetric_id);
				if(dashboardComponentMetric!=null) {
					profileDTO.setComponentMetric(dashboardComponentMetric);
				}
			}

			colName = "createTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String createTime = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			profileDTO.setCreateTime(AhRestoreCommons.convertLong(createTime));

			colName = "specifyType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String specifyType = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(DashboardComponent.WIDGET_SPECIFY_TYPE_NONE);
			profileDTO.setSpecifyType(AhRestoreCommons.convertInt(specifyType));

			colName = "specifyName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String specifyName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			profileDTO.setSpecifyName(specifyName);

			colName = "homeonly";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String homeonly = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			profileDTO.setHomeonly(AhRestoreCommons.convertStringToBoolean(homeonly));

			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			profileDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
			
			profileListInfo.add(profileDTO);
		}

		return profileListInfo;
	}

	public static boolean restoreAhDashboardComponent() {
		try {
			List<DashboardComponent> allProfile = getAllAhDashboardComponent();
			if(null == allProfile) {
				return false;
			} else {
				List<Long> lOldId = new ArrayList<Long>();
				for (DashboardComponent filter : allProfile) {
					lOldId.add(filter.getId());
				}
				QueryUtil.restoreBulkCreateBos(allProfile);
				for(int i=0; i < allProfile.size(); ++i) {
					AhRestoreNewMapTools.setMapAhDashboardComponent(lOldId.get(i), allProfile.get(i).getId());
				}
			}
		} catch(Exception e) {
			log.error(e);
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

}