package com.ah.util.bo.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.dashboard.AhDashBoardGroup;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;

public class DaHelper {
	public static String getDrillDownTypeString(String drillDownType) {
		String result = null;
		switch(drillDownType) {
			case "01":
				result = "user";
				break;
			case "02":
				result = "app";
				break;
			case "03":
				result = "client";
				break;
			case "04":
				result = "device";
				break;
			case "05":
				result = "dd";
				break;
			case "06":
				result = "link";
				break;
			case "07":
				result = "port";
				break;
			case "08":
				result = "deviceDetail";
				break;
			case "09":
				result = "appclient";
				break;
			default:
				break;
		}
		
		return result;
	}
	
	private static List<Integer> APPLICATION_WIDGET_KEYS = new ArrayList<>();
	static {
		APPLICATION_WIDGET_KEYS.add(53);
		APPLICATION_WIDGET_KEYS.add(54);
		APPLICATION_WIDGET_KEYS.add(55);
		APPLICATION_WIDGET_KEYS.add(56);
		APPLICATION_WIDGET_KEYS.add(67);
		APPLICATION_WIDGET_KEYS.add(68);
		APPLICATION_WIDGET_KEYS.add(75);
		//APPLICATION_WIDGET_KEYS.add(81);
		APPLICATION_WIDGET_KEYS.add(93);
		APPLICATION_WIDGET_KEYS.add(94);
		APPLICATION_WIDGET_KEYS.add(95);
		APPLICATION_WIDGET_KEYS.add(96);
		APPLICATION_WIDGET_KEYS.add(98);
	}
	
	private static List<Integer> APPLICATION_WATCHLIST_WIDGET_KEYS = new ArrayList<>();
	static {
		APPLICATION_WATCHLIST_WIDGET_KEYS.add(93);
		APPLICATION_WATCHLIST_WIDGET_KEYS.add(94);
		APPLICATION_WATCHLIST_WIDGET_KEYS.add(95);
		APPLICATION_WATCHLIST_WIDGET_KEYS.add(96);
	}
	
	private static List<Integer> LASTHOUR_WIDGET_KEYS = new ArrayList<>();
	static {
		LASTHOUR_WIDGET_KEYS.add(3);
		LASTHOUR_WIDGET_KEYS.add(4);
		LASTHOUR_WIDGET_KEYS.add(5);
		LASTHOUR_WIDGET_KEYS.add(6);
		LASTHOUR_WIDGET_KEYS.add(9);
	}
	
	private static List<Integer> CURRENTTIME_WIDGET_KEYS = new ArrayList<>();
	static {
		CURRENTTIME_WIDGET_KEYS.add(1);
		CURRENTTIME_WIDGET_KEYS.add(2);
		CURRENTTIME_WIDGET_KEYS.add(7);
		CURRENTTIME_WIDGET_KEYS.add(8);
//		CURRENTTIME_WIDGET_KEYS.add(11);
		CURRENTTIME_WIDGET_KEYS.add(12);
		CURRENTTIME_WIDGET_KEYS.add(13);
		CURRENTTIME_WIDGET_KEYS.add(14);
		CURRENTTIME_WIDGET_KEYS.add(18);
		CURRENTTIME_WIDGET_KEYS.add(19);
		CURRENTTIME_WIDGET_KEYS.add(22);
		CURRENTTIME_WIDGET_KEYS.add(23);
		CURRENTTIME_WIDGET_KEYS.add(51);
		CURRENTTIME_WIDGET_KEYS.add(81);
	}
	
	private static List<Integer> APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE = new ArrayList<>();
	static {
		
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(53);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(54);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(55);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(67);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(81);
		
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(57);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(58);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(59);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(60);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(61);
		
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(62);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(63);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(64);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(66);
		
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(93);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(94);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(95);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(97);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(98);
		APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.add(99);
	}
	
	private static List<Integer> BK_NODATA_NEED_TOTAL_WIDGET_KEYS = new ArrayList<>();
	static {
		BK_NODATA_NEED_TOTAL_WIDGET_KEYS.add(44);
		BK_NODATA_NEED_TOTAL_WIDGET_KEYS.add(46);
		BK_NODATA_NEED_TOTAL_WIDGET_KEYS.add(75);
	}
	
	public static List<Integer> getApplicationWidgetKeys() {
		return APPLICATION_WIDGET_KEYS;
	}
	public static boolean isApplicationWidget(Integer key) {
		return APPLICATION_WIDGET_KEYS.contains(key);
	}
	
	public static List<Integer> getLastHourWidgetKeys() {
		return LASTHOUR_WIDGET_KEYS;
	}
	
	public static boolean isLastHourWidget(Integer key) {
		return LASTHOUR_WIDGET_KEYS.contains(key);
	}
	
	public static List<Integer> getCurrentTimeWidgetKeys() {
		return CURRENTTIME_WIDGET_KEYS;
	}
	
	public static boolean isCurrentTimeWidget(Integer key) {
		return CURRENTTIME_WIDGET_KEYS.contains(key);
	}
	
	public static List<Integer> getBkNoDataNeedTotalWidgetKeys() {
		return BK_NODATA_NEED_TOTAL_WIDGET_KEYS;
	}
	public static boolean isBkNoDataNeedTotalWidget(Integer key) {
		return BK_NODATA_NEED_TOTAL_WIDGET_KEYS.contains(key);
	}
	
	public static List<Integer> getApplicationChangeTimeModeWidgetKeys() {
		return APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE;
	}
	
	public static boolean isApplicationChangeTimeModeWidgetKey(Integer key) {
		return APPLICATION_WIDGETKEYS_CHANGE_TIMEMODE.contains(key);
	}
	
	public static boolean isApplicationWatchlistWidgetKey(Integer key) {
		return APPLICATION_WATCHLIST_WIDGET_KEYS.contains(key);
	}
	
	public static int getApplicationReportIntervalMinute() {
		LogSettings logSetting = QueryUtil.findBoByAttribute(LogSettings.class, "owner.domainName", HmDomain.HOME_DOMAIN);
		if (logSetting == null) {
			logSetting = new LogSettings();
		}
		return logSetting.getReportIntervalMinute();
	}
	
	public static boolean isDataReturnedFromBackendNull(Object data) {
		return data == null
				|| StringUtils.isBlank(data.toString())
				|| "[]".equals(data.toString());
	}
	
	/**
	 * this field is used to hold key&order map for back end, so that we can request data for widgets with certain order
	 */
	public static Map<Integer, Integer> REPORT_KEY_SEQUENCE_MAP = new HashMap<>();
	static {
		REPORT_KEY_SEQUENCE_MAP.put(1,	1010);
		REPORT_KEY_SEQUENCE_MAP.put(2,	1020);
		REPORT_KEY_SEQUENCE_MAP.put(3,	1030);
		REPORT_KEY_SEQUENCE_MAP.put(4,	1040);
		REPORT_KEY_SEQUENCE_MAP.put(5,	1050);
		REPORT_KEY_SEQUENCE_MAP.put(6,	1060);
		REPORT_KEY_SEQUENCE_MAP.put(7,	1070);
		REPORT_KEY_SEQUENCE_MAP.put(8,	1080);
		REPORT_KEY_SEQUENCE_MAP.put(9,	1090);
		REPORT_KEY_SEQUENCE_MAP.put(10,	1100);
		REPORT_KEY_SEQUENCE_MAP.put(11,	1110);
		REPORT_KEY_SEQUENCE_MAP.put(12,	1120);
		REPORT_KEY_SEQUENCE_MAP.put(13,	1121);
		REPORT_KEY_SEQUENCE_MAP.put(14,	1122);
		REPORT_KEY_SEQUENCE_MAP.put(15,	5000);
		REPORT_KEY_SEQUENCE_MAP.put(16,	5001);
		REPORT_KEY_SEQUENCE_MAP.put(17,	5002);
		REPORT_KEY_SEQUENCE_MAP.put(18,	1130);
		REPORT_KEY_SEQUENCE_MAP.put(19,	1140);
		REPORT_KEY_SEQUENCE_MAP.put(20,	1150);
		REPORT_KEY_SEQUENCE_MAP.put(21,	1160);
		REPORT_KEY_SEQUENCE_MAP.put(22,	1170);
		REPORT_KEY_SEQUENCE_MAP.put(23,	1180);
		REPORT_KEY_SEQUENCE_MAP.put(24,	1190);
		REPORT_KEY_SEQUENCE_MAP.put(25,	6000);
		REPORT_KEY_SEQUENCE_MAP.put(26,	1200);
		REPORT_KEY_SEQUENCE_MAP.put(27,	1210);
		REPORT_KEY_SEQUENCE_MAP.put(28,	5003);
		REPORT_KEY_SEQUENCE_MAP.put(29,	5004);
		REPORT_KEY_SEQUENCE_MAP.put(30,	6001);
		REPORT_KEY_SEQUENCE_MAP.put(31,	6002);
		REPORT_KEY_SEQUENCE_MAP.put(32,	5005);
		REPORT_KEY_SEQUENCE_MAP.put(33,	5006);
		REPORT_KEY_SEQUENCE_MAP.put(34,	1220);
		REPORT_KEY_SEQUENCE_MAP.put(35,	5007);
		REPORT_KEY_SEQUENCE_MAP.put(36,	5008);
		REPORT_KEY_SEQUENCE_MAP.put(37,	5009);
		REPORT_KEY_SEQUENCE_MAP.put(38,	5010);
		REPORT_KEY_SEQUENCE_MAP.put(39,	5011);
		REPORT_KEY_SEQUENCE_MAP.put(40,	6003);
		REPORT_KEY_SEQUENCE_MAP.put(41,	6004);
		REPORT_KEY_SEQUENCE_MAP.put(42,	5012);
		REPORT_KEY_SEQUENCE_MAP.put(43,	5013);
		REPORT_KEY_SEQUENCE_MAP.put(44,	5014);
		REPORT_KEY_SEQUENCE_MAP.put(45,	5015);
		REPORT_KEY_SEQUENCE_MAP.put(46,	6005);
		REPORT_KEY_SEQUENCE_MAP.put(47,	6006);
		REPORT_KEY_SEQUENCE_MAP.put(48,	6007);
		REPORT_KEY_SEQUENCE_MAP.put(49,	6008);
		REPORT_KEY_SEQUENCE_MAP.put(50,	5016);
		REPORT_KEY_SEQUENCE_MAP.put(51,	1230);
		REPORT_KEY_SEQUENCE_MAP.put(52,	1240);
		REPORT_KEY_SEQUENCE_MAP.put(53,	7001);
		REPORT_KEY_SEQUENCE_MAP.put(54,	7002);
		REPORT_KEY_SEQUENCE_MAP.put(55,	7003);
		REPORT_KEY_SEQUENCE_MAP.put(56,	7004);
		REPORT_KEY_SEQUENCE_MAP.put(57,	7100);
		REPORT_KEY_SEQUENCE_MAP.put(58,	7101);
		REPORT_KEY_SEQUENCE_MAP.put(59,	7102);
		REPORT_KEY_SEQUENCE_MAP.put(60,	7103);
		REPORT_KEY_SEQUENCE_MAP.put(61,	7104);
		REPORT_KEY_SEQUENCE_MAP.put(62,	7200);
		REPORT_KEY_SEQUENCE_MAP.put(63,	7201);
		REPORT_KEY_SEQUENCE_MAP.put(64,	7202);
		REPORT_KEY_SEQUENCE_MAP.put(65,	7203);
		REPORT_KEY_SEQUENCE_MAP.put(66,	7204);
		REPORT_KEY_SEQUENCE_MAP.put(67,	7105);
		REPORT_KEY_SEQUENCE_MAP.put(68,	7106);
		REPORT_KEY_SEQUENCE_MAP.put(69,	1250);
		REPORT_KEY_SEQUENCE_MAP.put(70,	5017);
		REPORT_KEY_SEQUENCE_MAP.put(71,	5018);
		REPORT_KEY_SEQUENCE_MAP.put(72,	5019);
		REPORT_KEY_SEQUENCE_MAP.put(73,	5020);
		REPORT_KEY_SEQUENCE_MAP.put(74,	5021);
		REPORT_KEY_SEQUENCE_MAP.put(75,	7005);
		REPORT_KEY_SEQUENCE_MAP.put(76,	6009);
		REPORT_KEY_SEQUENCE_MAP.put(77,	6010);
		REPORT_KEY_SEQUENCE_MAP.put(78,	9000);
		REPORT_KEY_SEQUENCE_MAP.put(79,	9001);
		REPORT_KEY_SEQUENCE_MAP.put(80,	9002);
		REPORT_KEY_SEQUENCE_MAP.put(81,	7005);
		REPORT_KEY_SEQUENCE_MAP.put(82,	7107);
		REPORT_KEY_SEQUENCE_MAP.put(83,	7006);
		REPORT_KEY_SEQUENCE_MAP.put(84,	5022);
		REPORT_KEY_SEQUENCE_MAP.put(85,	5023);
		REPORT_KEY_SEQUENCE_MAP.put(86,	5024);
		REPORT_KEY_SEQUENCE_MAP.put(87,	6011);
		REPORT_KEY_SEQUENCE_MAP.put(88,	6012);
		REPORT_KEY_SEQUENCE_MAP.put(89,	6013);
		REPORT_KEY_SEQUENCE_MAP.put(90,	1260);
		REPORT_KEY_SEQUENCE_MAP.put(91,	1270);
		REPORT_KEY_SEQUENCE_MAP.put(92,	5025);
		REPORT_KEY_SEQUENCE_MAP.put(93,	7007);
		REPORT_KEY_SEQUENCE_MAP.put(94,	7008);
		REPORT_KEY_SEQUENCE_MAP.put(95,	7009);
		REPORT_KEY_SEQUENCE_MAP.put(96,	7010);
		REPORT_KEY_SEQUENCE_MAP.put(97,	7108);
		REPORT_KEY_SEQUENCE_MAP.put(98,	7011);
		REPORT_KEY_SEQUENCE_MAP.put(99,	7301);
		REPORT_KEY_SEQUENCE_MAP.put(100,7302);
		REPORT_KEY_SEQUENCE_MAP.put(101,7303);
		REPORT_KEY_SEQUENCE_MAP.put(102,7304);
	}
	
	public static Integer getReportKeySequence(Integer key) {
		if (REPORT_KEY_SEQUENCE_MAP.containsKey(key)) {
			return REPORT_KEY_SEQUENCE_MAP.get(key);
		} else {
			return 0;
		}
	}
	
	/**
	 * Attention: key 11 is removed, key 1 is used as device&client, please do not refer to below two fields.
	 * These two keys are used for restore
	 */
	public static final int CLIENT_STATUS_KEY = 11;
	public static final int DEVICE_STATUS_KEY = 1;
	
	private static StringBuilder fetchDeviceCountSql(AhDashboard da, Long domainId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(a.id) from hive_ap a");
		sql.append(" where a.owner=").append(domainId);
		sql.append(" and a.managestatus=").append(HiveAp.STATUS_MANAGED);
		sql.append(" and a.devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY);
		Long cuMapId = null;
		String obTy = da.getObjectType();
		String obId = da.getObjectId();
		if (obTy.equals(AhDashBoardGroup.DA_GROUP_TYPE_MAP)) {
			cuMapId = Long.valueOf(obId);
			if(cuMapId!=null && cuMapId>0) {
				Set<Long> mapIds =  BoMgmt.getMapMgmt().getContainerDownIds(cuMapId);
				StringBuilder tmpId = new StringBuilder();
				for(Long it: mapIds){
					if (tmpId.length()>0) {
						tmpId.append(",");
					}
					tmpId.append(it);
				}
				if (tmpId.length()>0) {
					sql.append(" and a.map_container_id in (").append(tmpId.toString()).append(")");
				}
			}
		} else {
			switch (obTy) {
			case AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY:
				if(!obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY)){
					sql.append(" and a.template_id=").append(obId);
				}
				break;
			case AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE:
				if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE)){
					sql.append(" and a.classificationTag1 is not null ");
					sql.append(" and a.classificationTag1!=''");
				} else {
					sql.append(" and a.classificationTag1='").append(NmsUtil.convertSqlStr(obId)).append("'");
				}
				break;
			case AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO:
				if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO)){
					sql.append(" and a.classificationTag2 is not null ");
					sql.append(" and a.classificationTag2!=''");
				} else {
					sql.append(" and a.classificationTag2='").append(NmsUtil.convertSqlStr(obId)).append("'");
				}
				break;
			case AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE:
				if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE)){
					sql.append(" and a.classificationTag3 is not null ");
					sql.append(" and a.classificationTag3!=''");
				} else {
					sql.append(" and a.classificationTag3='").append(NmsUtil.convertSqlStr(obId)).append("'");
				}
				break;
			}
		}
		
		return sql;
	}
	
	public static int getDeviceCount(AhDashboard da, Long domainId) {
		int result = 0;
		try {
			List<?> counts = QueryUtil.executeNativeQuery(fetchDeviceCountSql(da, domainId).toString());
			if (counts != null
					&& !counts.isEmpty()) {
				result = Integer.parseInt(counts.get(0).toString());
			}
		} catch (Exception e) {
		}
		
		return result;
	}
	
	public static JSONObject getJSONWidgetCommonInfo(WidgetCommonInfo widgetInfo) throws JSONException {
		JSONObject jObject = new JSONObject();
		
		jObject.put("starttime", widgetInfo.getStartTime());
		jObject.put("rqtime", widgetInfo.getRequestTime());
		
		return jObject;
	}
	public static WidgetCommonInfo prepareWidgetCommonInfo(AhDashboardWidget widget, AhDashboard da, TimeZone tz) {
		Calendar ca = Calendar.getInstance(tz);
		long currentTime = System.currentTimeMillis();
		
		WidgetCommonInfo widgetInfo = new WidgetCommonInfo(currentTime);
		WidgetConfigMeta configMeta = null;
		if (widget.isBlnChecked()) {
			configMeta = new WidgetConfigMeta(widget);
		} else {
			configMeta = new WidgetConfigMeta(da);
		}
		
		if(configMeta.getSelectTimeType() == WidgetConfigMeta.TIME_LASTHOUR) {
			ca.setTimeInMillis(currentTime-3600000);
			ca.clear(Calendar.SECOND);
			ca.clear(Calendar.MILLISECOND);
			widgetInfo.setStartTime(ca.getTimeInMillis());
			widgetInfo.setEndTime(widgetInfo.getStartTime()+3600000);
		} else if (configMeta.getSelectTimeType() == WidgetConfigMeta.TIME_LASTDAY) {
			ca.setTimeInMillis(currentTime -3600000L * 24);
			widgetInfo.setStartTime(ca.getTimeInMillis()/3600000L*3600000L);
			widgetInfo.setEndTime(currentTime/3600000L*3600000L);
		} else if (configMeta.getSelectTimeType() == WidgetConfigMeta.TIME_LASTWEEK) {
			ca.setTimeInMillis(currentTime-3600000L * 24 * 7);
			ca.clear(Calendar.MINUTE);
			ca.clear(Calendar.SECOND);
			ca.clear(Calendar.MILLISECOND);
			ca.set(Calendar.HOUR_OF_DAY,0);
			widgetInfo.setStartTime(ca.getTimeInMillis());
			
			ca.setTimeInMillis(currentTime);
			ca.clear(Calendar.MINUTE);
			ca.clear(Calendar.SECOND);
			ca.clear(Calendar.MILLISECOND);
			ca.set(Calendar.HOUR_OF_DAY,0);
			widgetInfo.setEndTime(ca.getTimeInMillis());
		} else {
			widgetInfo.setStartTime(configMeta.getCustomStartTime());
			widgetInfo.setEndTime(configMeta.getCustomEndTime());
		}
		
		return widgetInfo;
	}
	
	protected static class WidgetConfigMeta {
		public static final int TIME_LASTHOUR = 1;
		public static final int TIME_LASTDAY = 2;
		public static final int TIME_LASTWEEK = 3;
		public static final int TIME_CUSTOM = 4;
		private int selectTimeType=TIME_LASTDAY;
		private long customStartTime;
		private long customEndTime;
		
		public WidgetConfigMeta(AhDashboardWidget widget) {
			this.setSelectTimeType(widget.getSelectTimeType());
			this.setCustomStartTime(widget.getCustomStartTime());
			this.setCustomEndTime(widget.getCustomEndTime());
		}
		
		public WidgetConfigMeta(AhDashboard da) {
			this.setSelectTimeType(da.getSelectTimeType());
			this.setCustomStartTime(da.getCustomStartTime());
			this.setCustomEndTime(da.getCustomEndTime());
		}
		
		public int getSelectTimeType() {
			return selectTimeType;
		}
		public void setSelectTimeType(int selectTimeType) {
			this.selectTimeType = selectTimeType;
		}
		public long getCustomStartTime() {
			return customStartTime;
		}
		public void setCustomStartTime(long customStartTime) {
			this.customStartTime = customStartTime;
		}
		public long getCustomEndTime() {
			return customEndTime;
		}
		public void setCustomEndTime(long customEndTime) {
			this.customEndTime = customEndTime;
		}
	}
	
	public static class WidgetCommonInfo {
		private long startTime;
		private long requestTime;
		private long endTime;
		
		public WidgetCommonInfo() {
		}
		public WidgetCommonInfo(long requestTime) {
			this.setRequestTime(requestTime);
		}
		
		public long getStartTime() {
			return startTime;
		}
		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}
		public long getRequestTime() {
			return requestTime;
		}
		public void setRequestTime(long requestTime) {
			this.requestTime = requestTime;
		}
		public long getEndTime() {
			return endTime;
		}
		public void setEndTime(long endTime) {
			this.endTime = endTime;
		}
	}
	
}
