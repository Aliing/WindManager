package com.ah.ui.actions.monitor;

/*
 * @author Fisher
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.dashboard.AhDashBoardGroup;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.dashboard.AhDashboardLayout;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.dashboard.DashboardComponentData;
import com.ah.bo.dashboard.DashboardComponentMetric;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.MapMgmtImpl;
import com.ah.bo.performance.AhNewReport;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.admin.DeviceTagUtil;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.HmException;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.bo.dashboard.ReportDataRequestUtil;

public abstract class DashboardAbstractAction extends BaseAction implements QueryBo {

	private static final Tracer log = new Tracer(DashboardAbstractAction.class.getSimpleName());

	private static final long serialVersionUID = 1L;
	public static final String MONITOR_TAB_ID_USERAPPLICATION = "monitor_tab_id_userapplication";

	@Override
	public AhDashboard getDataSource() {
		return (AhDashboard) dataSource;
	}

	@SuppressWarnings("unchecked")
	public void monitorPageSessionManager(boolean addFlg, String did, String value){
		Map<String,String> mon;
		if(MgrUtil.getSessionAttribute(MONITOR_TAB_ID_USERAPPLICATION)==null){
			mon = new HashMap<String, String>();
		} else {
			mon = (HashMap<String,String>)MgrUtil.getSessionAttribute(MONITOR_TAB_ID_USERAPPLICATION);
		}
		if(addFlg) {
			mon.put(did, value);
		} else {
			mon.remove(did);
		}
		MgrUtil.setSessionAttribute(MONITOR_TAB_ID_USERAPPLICATION, mon);
	}

	public boolean getBooleanSuperUser(){
		return userContext.isSuperUser() && getIsInHomeDomain();
	}

	public void prepareJsonGUIData() throws JSONException{
//		if(getDataSource().getLocation()==null) {
//			jsonObject.put("map", String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_MAP));
//		} else {
//			jsonObject.put("map", getDataSource().getLocation().getId().toString());
//		}
		jsonObject.put("obType", getDataSource().getObjectType());
		jsonObject.put("obId", getDataSource().getObjectId());
		initDeviceGroup();
		initFilterGroup();
		String v = filterList.toString();
		jsonObject.put("v", v);
		String v3 = secondFilterList.toString();
		jsonObject.put("v3", v3);
		jsonObject.put("fobType", getDataSource().getFilterObjectType());
		jsonObject.put("fobId", getDataSource().getFilterObjectId());
		jsonObject.put("dId", getDataSource().getId());
		jsonObject.put("daType", getDataSource().getDaType());
		jsonObject.put("t", true);
	}

//	public void updateDataSourceValue() throws Exception {
//		dataSource = QueryUtil.updateBo(dataSource);
//		setSessionDataSource(findBoById(AhDashboard.class, dataSource.getId(),this));
//		getDataSource().setTz(tz);
//	}

	protected JSONObject removeDashboardComponent(Long componentId)
			throws JSONException {
		JSONObject jsonObject = new JSONObject();
		if(null == componentId){
			jsonObject.put("m", MgrUtil.getUserMessage("error.common.invalid", "id"));
		}
		DashboardComponent component = QueryUtil.findBoById(
				DashboardComponent.class, componentId);
		if (null != component && component.isDefaultFlag()) {
			jsonObject
					.put("m", MgrUtil.getUserMessage("error.objectIsDefault"));
		} else {
			try {
				QueryUtil.bulkRemoveBos(DashboardComponent.class,
						new FilterParams("id", componentId), getDomainId());
				jsonObject.put("s", true);
			} catch (HmException e) {
				jsonObject.put("m", MgrUtil.getUserMessage(e));
			} catch (Exception e) {
				jsonObject.put("m", MgrUtil.getUserMessage(
						"bringIntoManagedListerror.unknown", e.getMessage()));
				log.error("remove dashboard component error", e);
			}
		}
		return jsonObject;
	}

	protected JSONObject fetchScheduleSetting() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("f", getRefrequency());
			jsonObject.put("cuE",
					getDataSource().getReEmailAddress() == null ? ""
							: getDataSource().getReEmailAddress());
			jsonObject.put("lo", getDataSource().isEnableTimeLocal());
			if (getDataSource().getRefrequency() == getRefrequency()) {
				if (getRefrequency() == AhNewReport.NEW_REPORT_FREQUENCY_DAILY) {
					jsonObject.put("cuD", getDataSource().isReCustomDay());
					jsonObject.put("cuDv", getDataSource()
							.getReCustomDayValue());
					jsonObject.put("cuT", getDataSource().isReCustomTime());
					jsonObject.put("cuTs", getDataSource()
							.getReCustomTimeStart());
					jsonObject
							.put("cuTe", getDataSource().getReCustomTimeEnd());
				} else if (getRefrequency() == AhNewReport.NEW_REPORT_FREQUENCY_WEEKLY) {
					jsonObject.put("cuW", getDataSource().getReWeekStart());
				} else if (getRefrequency() == AhNewReport.NEW_REPORT_FREQUENCY_CUSTOM) {
					jsonObject.put("cmcutp", getDataSource().getReCmTimeType());
					jsonObject.put("cmcuperiod", getDataSource()
							.getReCmTimePeriod());
					jsonObject.put("cmcusd", getDataSource()
							.getReCmTimeStartDayType());
					jsonObject.put("cmcusdvalue", getDataSource()
							.getReCmTimeStartDayValue());
					jsonObject.put("cmcusm", getDataSource()
							.getReCmTimeStartMontyYear());
					jsonObject.put("cmcussm", getDataSource()
							.getReCmTimeStartSepcYear());
				}
			} else {
				jsonObject.put("cuD", false);
				jsonObject.put("cuDv", "0111110");
				jsonObject.put("cuT", false);
				jsonObject.put("cuTs", 0);
				jsonObject.put("cuTe", 0);
				jsonObject.put("cuW", 0);
				// for custom
				jsonObject.put("cmcutp", 1);
				jsonObject.put("cmcuperiod", 1);
				jsonObject.put("cmcusd", 1);
				jsonObject.put("cmcusdvalue", 1);
				jsonObject.put("cmcusm", 14);
				jsonObject.put("cmcussm",
						Calendar.getInstance().get(Calendar.YEAR));
			}
			jsonObject.put("t", true);

		} catch (Exception e) {
			jsonObject.put("t", false);
			jsonObject.put("m", e.getMessage());
		}
		return jsonObject;
	}

	protected void fillReportScheduleSettings(AhDashboard report) {
		report.setRefrequency(refrequency);
		report.setReEmailAddress(reEmailAddress);
		report.setEnableTimeLocal(enableTimeLocal);
		if (refrequency == AhNewReport.NEW_REPORT_FREQUENCY_DAILY) {
			report.setReCustomDay(reCustomDay);
			report.setReCustomTime(reCustomTime);
			report.setReCustomDayValue(reCustomDayValue);
			report.setReCustomTimeStart(reCustomTimeStart);
			report.setReCustomTimeEnd(reCustomTimeEnd);
		} else if (refrequency == AhNewReport.NEW_REPORT_FREQUENCY_WEEKLY) {
			report.setReWeekStart(reWeekStart);
		} else if (refrequency == AhNewReport.NEW_REPORT_FREQUENCY_CUSTOM) {
			report.setReCmTimeType(reCmTimeType);
			report.setReCmTimePeriod(reCmTimePeriod);
			report.setReCmTimeStartDayType(reCmTimeStartDayType);
			report.setReCmTimeStartDayValue(reCmTimeStartDayValue);
			report.setReCmTimeStartMontyYear(reCmTimeStartMontyYear);
			report.setReCmTimeStartSepcYear(reCmTimeStartSepcYear);
		}
	}

//	protected List<AhDashBoardGroup> topyList;
	protected JSONArray filterList;
	protected JSONArray secondFilterList;

	protected String treeId;
	protected String treeType;
//	protected String widgetLocationId;
	protected String filterObjectType;
	protected String filterObjectId;
	protected int offset;

	protected Long widgetId;
	protected String componentType;
	protected Long preWidgetId;
	protected String sourceType;
	protected Long preMetricId;
	protected boolean blnCloneWidget;

	protected int componentGroupType;
	protected String widgetName;

	protected int specifyType;
	protected String specifyName;

	protected String displayName;
	protected String displayValue;
	protected String displayValueKey;

	// for dashboard
	protected int timeType;
	protected String startTime;
	protected int startHour;
	protected String endTime;
	protected int endHour;
	// for dashboard

	// for report
	protected String reportName;
	protected String reportDescription;
	protected String reportHeader;
	protected String reportFooter;
	protected String reportSummary;
	protected int refrequency;
	protected boolean reCustomDay=false;
	protected boolean reCustomTime=false;
	protected String reCustomDayValue="0111110";
	protected int reCustomTimeStart;
	protected int reCustomTimeEnd;
	protected String reEmailAddress;
	protected int reWeekStart=0;

	protected int reCmTimeType=1;//1 means day,2: month
	protected int reCmTimePeriod=1;
	protected int reCmTimeStartDayType=1; //1 means day,2: week
	protected int reCmTimeStartDayValue=1;
	protected int reCmTimeStartMontyYear=1; //1:january, 2:febuary, ... 13:everymonth, 14, everyyear.
	protected int reCmTimeStartSepcYear=Calendar.getInstance().get(Calendar.YEAR);

	protected boolean reEnabledScheduleCheckbox;
	// for report

	protected boolean enableTimeLocal;

	protected String dafaultTabId;
	protected String cloneTabId;

	protected int monitorType;
	protected String monitorEl;
	protected String deviceMonitorType;
	protected Long monitorId;

	public List<CheckItem> getLstCuMonthYear(){
		List<CheckItem> lstMonthYear = new ArrayList<CheckItem>();
		lstMonthYear.add(new CheckItem((long) (1), "January"));
		lstMonthYear.add(new CheckItem((long) (2), "February"));
		lstMonthYear.add(new CheckItem((long) (3), "March"));
		lstMonthYear.add(new CheckItem((long) (4), "April"));
		lstMonthYear.add(new CheckItem((long) (5), "May"));
		lstMonthYear.add(new CheckItem((long) (6), "June"));
		lstMonthYear.add(new CheckItem((long) (7), "July"));
		lstMonthYear.add(new CheckItem((long) (8), "August"));
		lstMonthYear.add(new CheckItem((long) (9), "September"));
		lstMonthYear.add(new CheckItem((long) (10), "October"));
		lstMonthYear.add(new CheckItem((long) (11), "November"));
		lstMonthYear.add(new CheckItem((long) (12), "December"));
		lstMonthYear.add(new CheckItem((long) (13), "Every Month"));
		lstMonthYear.add(new CheckItem((long) (14), "Every Year"));
		lstMonthYear.add(new CheckItem((long) (15), "Specify Year"));
		return lstMonthYear;
	}


	public List<CheckItem> getLstHours() {
		List<CheckItem> lstHour = new ArrayList<CheckItem>();
		for (int i = 0; i < 12; i++) {
			if (i>9) {
				lstHour.add(new CheckItem((long) i, String.valueOf(i + ":00"+ " AM")));
			} else {
				if (i==0) {
					lstHour.add(new CheckItem((long) i, String.valueOf("12:00"+ " AM")));
				} else {
					lstHour.add(new CheckItem((long) i, String.valueOf("0" + i  + ":00"+ " AM")));
				}
			}
		}
		for (int i = 0; i < 12; i++) {
			if (i>9) {
				lstHour.add(new CheckItem((long) (i + 12), String.valueOf(i + ":00"+ " PM")));
			} else {
				if (i==0) {
					lstHour.add(new CheckItem((long) (i + 12), String.valueOf("12:00"+ " PM")));
				} else {
					lstHour.add(new CheckItem((long) (i + 12), String.valueOf("0" + i  + ":00"+ " PM")));
				}
			}
		}
		return lstHour;
	}

	public List<JSONObject> fetchWidgetSelectInfoList(int st) throws JSONException {

		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONObject keyValue = new JSONObject();
		keyValue.put("k", "Premade widget...");
		keyValue.put("v", -1);
		list.add(keyValue);

		String sql = "SELECT bo.id, bo.componentName FROM "
				+ DashboardComponent.class.getSimpleName() + " bo";
		FilterParams filter = new FilterParams("componentName is not null and componentName!=:s1 and specifyType=:s2", new Object[]{"", st});
		if (!getIsInHomeDomain()) {
			filter = new FilterParams("componentName is not null and componentName!=:s1 and specifyType=:s2 and homeonly=:s3", new Object[]{"", st,false});
		}
		List<?> items = QueryUtil.executeQuery(sql, new SortParams("componentName"),
				filter, getDomain().getId());

		for(Object it: items){
			Object[] item = (Object[]) it;
			keyValue = new JSONObject();
			keyValue.put("k", item[1]);
			keyValue.put("v", item[0]);
			list.add(keyValue);
		}

		return list;
	}

	public List<JSONObject> fetchMetricSelectInfoList(int groupType, int widgetTp) throws JSONException {

		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONObject keyValue = new JSONObject();

		String sql = "SELECT bo.id, bo.metricName FROM "
				+ DashboardComponentMetric.class.getSimpleName() + " bo";
		FilterParams filter = new FilterParams("metricName is not null and metricName!=:s1 and componentGroup=:s2 and specifyType=:s3",
				new Object[]{"",groupType, widgetTp});
		if (!getIsInHomeDomain()) {
			filter = new FilterParams("metricName is not null and metricName!=:s1 and componentGroup=:s2 and specifyType=:s3 and homeonly=:s4",
					new Object[]{"",groupType, widgetTp, false});
		}

		List<?> items = QueryUtil.executeQuery(sql, new SortParams("metricName"),
				filter, getDomain().getId());

		for(Object it: items){
			Object[] item = (Object[]) it;
			keyValue = new JSONObject();
			keyValue.put("k", item[1]);
			keyValue.put("v", item[0]);
			list.add(keyValue);
		}
		if (list.isEmpty()) {
			keyValue.put("k", "None Avaliable");
			keyValue.put("v", -1);
			list.add(keyValue);
		}

		return list;
	}

	public List<JSONObject> fetchTabList(List<TextItem> ls) throws JSONException {
		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONObject keyValue;
		for(TextItem ti: ls){
			keyValue = new JSONObject();
			keyValue.put("k", ti.getKey());
			keyValue.put("v", ti.getValue());
			list.add(keyValue);
		}
		return list;
	}

	public void initGroup() throws JSONException{
		initDeviceGroup();
		initFilterGroup();
		prepareDependencyObjects();
	}
	
	public void initDeviceGroup() throws JSONException {
		initTopyGroup();
//		fetchDevicesList();
		if (!isEasyMode()) {
			fetchNetworkPolicyList();
		}
		fetchTagList();
	}

	private void prepareDependencyObjects() {
		if (getDataSource() != null) {
			bgRollup = getDataSource().isBgRollup();
		}
	}

//	public String getTopyGroupJson(){
//		return getTreeData(topyList, false);
//	}

	public String getFilterGroupJson(){
		return filterList.toString();
	}

	public String getFilterGroupSecondJson(){
		return secondFilterList.toString();
	}

	public void initFilterGroup() throws JSONException {
		StringBuilder sql = fetchFilterMapSql();
		fetchSsidList(sql);
		if (!isEasyMode()) {
			fetchUserProfileList(sql);
		}
		
	}

	private void initTopyGroup() throws JSONException{
		List<CheckItem> items = MapMgmtImpl.getInstance().getMapListView(getDomain().getId(), null, true);
		for(CheckItem it: items) {
			System.out.println(it.getValue() + "----------" + it.getId() );
		}
		
		filterList =  new JSONArray();
		JSONObject jo = setTreeJosnObj(getText("hm.dashboard.tree.item.allgroup"),
				AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL,AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL,0);
		filterList.put(jo);
		
		if (!items.isEmpty()) {
			JSONObject joa = setTreeJosnObj(getText("hm.dashboard.tree.item.alltopo"),
					AhDashBoardGroup.DA_GROUP_TYPE_MAP,AhDashBoardGroup.DA_GROUP_TYPE_MAP,1);
			((JSONArray)jo.get("items")).put(joa);
			fetchMapAsJson(joa,items,0,2);
		}
	}
	
	public int fetchMapAsJson(JSONObject joa,List<CheckItem> items, int index, int currentLevel) throws JSONException{
		int retIndex=index;
		JSONObject joas=null;
		for(int i=index; i<items.size();i++) {
			int subindex = 2;
			String matChar = "|_";
			while (items.get(i).getValue().contains(matChar)) {
				matChar = matChar + " _";
				subindex++;
				if (subindex >30) {
					break;
				}
			}
			
			if (currentLevel==subindex) {
				if (subindex==2) {
					joas = setTreeJosnObj(items.get(i).getValue(),
							items.get(i).getId().toString(),AhDashBoardGroup.DA_GROUP_TYPE_MAP,index);
					((JSONArray)joa.get("items")).put(joas);
					
				} else {
					joas = setTreeJosnObj(items.get(i).getValue().replaceAll("\\" + matChar.substring(0, matChar.length()-2), ""),
							items.get(i).getId().toString(),AhDashBoardGroup.DA_GROUP_TYPE_MAP,index);
					((JSONArray)joa.get("items")).put(joas);
				}
				retIndex=i;
			} else if (currentLevel>subindex) {
				retIndex=i-1;
				break;
			} else {
				i= retIndex =fetchMapAsJson(joas,items,retIndex + 1,subindex);
			}
		}
		return retIndex;
	}

	public StringBuilder fetchFilterMapSql() {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct s.ssid, s.id from hive_ap a, config_template_ssid c, ssid_profile s");
		sql.append(" where a.owner=").append(getDomain().getId());
		sql.append(" and a.managestatus=").append(HiveAp.STATUS_MANAGED);
		sql.append(" and a.devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY);
		sql.append(" and a.template_id = c.config_template_id");
		sql.append(" and c.ssid_profile_id = s.id");

		String[] params = getSecondFilterParam();
		if (params != null && params[0]!=null) {
			sql.append(" and a.map_container_id in (").append(params[0]).append(")");
		}
		if (params != null
				&& params.length > 1
				&& params[1]!=null) {
			String obTy= params[1];
			String obId = params[2];
			if(obTy.endsWith(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH)){
				sql.append(" and a.macAddress = '").append(obId).append("'");
			} else {
				switch (obTy) {
				case AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY:
					if(!obId.equals(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY))){
						sql.append(" and a.template_id=").append(obId);
					}
					break;
				case AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE:
					if(obId.equals(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE))){
						sql.append(" and a.classificationTag1 is not null ");
						sql.append(" and a.classificationTag1!=''");
					} else {
						sql.append(" and a.classificationTag1='").append(NmsUtil.convertSqlStr(obId)).append("'");
					}
					break;
				case AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO:
					if(obId.equals(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO))){
						sql.append(" and a.classificationTag2 is not null ");
						sql.append(" and a.classificationTag2!=''");
					} else {
						sql.append(" and a.classificationTag2='").append(NmsUtil.convertSqlStr(obId)).append("'");
					}
					break;
				case AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE:
					if(obId.equals(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE))){
						sql.append(" and a.classificationTag3 is not null ");
						sql.append(" and a.classificationTag3!=''");
					} else {
						sql.append(" and a.classificationTag3='").append(NmsUtil.convertSqlStr(obId)).append("'");
					}
					break;
				case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ALL:
					if(!obId.equals(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ALL))){
						if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP)){
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_HIVEAP);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR)){
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_BRANCH_ROUTER);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_20)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_20);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_28)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_28);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_110)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_110);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_120)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_120);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_121)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_121);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_141)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_141);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_170)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_170);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_320)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_320);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_340)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_340);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_380)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_380);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_330)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_330);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_HIVEAP);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_350)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_350);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_HIVEAP);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_370)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_370);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_HIVEAP);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_BR100)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_BR100);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_HIVEAP);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_100)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_BR100);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_BRANCH_ROUTER);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_200)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_BR200);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_BRANCH_ROUTER);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_200WP)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_BR200_WP);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_BRANCH_ROUTER);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_200LTEVZ)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_BR200_LTE_VZ);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_BRANCH_ROUTER);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_AP330)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_330);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_BRANCH_ROUTER);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_AP350)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_350);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_BRANCH_ROUTER);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_24)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_SR24);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_SWITCH);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_2124P)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_SR2124P);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_SWITCH);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_2148P)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_SR2148P);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_SWITCH);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_2024P)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_SR2024P);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_SWITCH);
						} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_48)){
							sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_SR48);
							sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_SWITCH);
						}
					}
					break;
				}
			}
		}

		return sql;
	}

	public void fetchSsidList(StringBuilder sql) throws JSONException {
		secondFilterList = new JSONArray();
		sql.append(" order by s.ssid");
		List<?> lstSsid = QueryUtil.executeNativeQuery(sql.toString());

		JSONObject jo = setTreeJosnObj(getText("hm.dashboard.tree.item.allFilters"),
				AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL,AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL,0);
		secondFilterList.put(jo);

		JSONObject joss= new JSONObject();

		if (!lstSsid.isEmpty()) {
			joss = setTreeJosnObj(getText("hm.dashboard.tree.item.allssid"),
					AhDashBoardGroup.DA_GROUP_TYPE_SSID,AhDashBoardGroup.DA_GROUP_TYPE_SSID,1);
			((JSONArray)jo.get("items")).put(joss);
		}
		List<Long> ssidIds = new ArrayList<Long>();
		Set<String> ssidSet = new HashSet<String>();
		for(Object oneSSID: lstSsid) {
			Object[] oneObj = (Object[]) oneSSID;
			ssidSet.add(oneObj[0].toString());
			ssidIds.add(Long.valueOf(oneObj[1].toString()));
		}
		for (String oneSSid : ssidSet) {
			JSONObject jod= setTreeJosnObj(oneSSid,
					oneSSid,AhDashBoardGroup.DA_GROUP_TYPE_SSID,2);
			((JSONArray)joss.get("items")).put(jod);
		}
		if (!ssidIds.isEmpty()){
			List<?> openSsidNames = QueryUtil.executeQuery("select ppskOpenSsid from " + SsidProfile.class.getSimpleName(), null, 
					new FilterParams("id in (:s1) and accessMode=:s2 and enablePpskSelfReg=:s3",
							new Object[]{ssidIds, SsidProfile.ACCESS_MODE_PSK, true}));
			
			for(Object oneObj: openSsidNames){
				if (oneObj!=null && !oneObj.toString().isEmpty() && !ssidSet.contains(oneObj.toString())) {
					JSONObject jod= setTreeJosnObj(oneObj.toString(),
							oneObj.toString(),AhDashBoardGroup.DA_GROUP_TYPE_SSID,2);
					((JSONArray)joss.get("items")).put(jod);
				}
			}
		}
	}

	public String[] getSecondFilterParam(){
		String ret[] = new String[3];
		Long cuMapId=null;

		if ("changeFilterTree".equals(operation) || "changeWidgetFilterTree".equals(operation)) {
			if (treeType.equals(AhDashBoardGroup.DA_GROUP_TYPE_MAP)){
				cuMapId = Long.valueOf(treeId);
			} else {
				ret[1] =  treeType;
				ret[2] =  treeId;
			}
		} else {
			if (getDataSource().getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_MAP)) {
				cuMapId = Long.valueOf(getDataSource().getObjectId());
			} else {
				ret[1] =  getDataSource().getObjectType();
				ret[2] =  getDataSource().getObjectId();
			}
		}

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
				ret[0] =  tmpId.toString();
			}
		}

		return ret;
	}

	public void fetchUserProfileList(StringBuilder sql) throws JSONException{

		List<?> lstSsid = QueryUtil.executeNativeQuery(sql.toString());
		List<Long> ssidIdList = new ArrayList<Long>();
		for(Object obj: lstSsid){
			Object[] oneObj = (Object[]) obj;
			ssidIdList.add(Long.parseLong(oneObj[1].toString()));
		}
		TreeSet<String> ts = new TreeSet<String>();
//		Map<String,String> ms = new HashMap<String,String>();
		if(!ssidIdList.isEmpty()) {
			List<SsidProfile> lstssidp = QueryUtil.executeQuery(SsidProfile.class, null, new FilterParams("id",ssidIdList), getDomain().getId(), this);
			if(!lstssidp.isEmpty()) {
				for(SsidProfile s :lstssidp) {
					if(s.getUserProfileDefault()!=null){
						ts.add(s.getUserProfileDefault().getUserProfileName());
//						ms.put(s.getUserProfileDefault().getUserProfileName(), s.getUserProfileDefault().getId().toString());
					}
					if(s.getUserProfileSelfReg()!=null){
						ts.add(s.getUserProfileSelfReg().getUserProfileName());
//						ms.put(s.getUserProfileSelfReg().getUserProfileName(), s.getUserProfileSelfReg().getId().toString());
					}
					if(s.getRadiusUserProfile()!=null){
						for(UserProfile up: s.getRadiusUserProfile()) {
							ts.add(up.getUserProfileName());
//							ms.put(up.getUserProfileName(), up.getId().toString());
						}
					}
				}
			}
		}


		if(!ts.isEmpty()) {
			JSONArray items = (JSONArray)((JSONObject)secondFilterList.get(0)).get("items");
			JSONObject joa = new JSONObject();

			joa = setTreeJosnObj(getText("hm.dashboard.tree.item.alluserprofile"),
					AhDashBoardGroup.DA_GROUP_TYPE_USERPROFILE_ALL,
					AhDashBoardGroup.DA_GROUP_TYPE_USERPROFILE_ALL ,1);
			items.put(joa);

			for(String oneUs: ts) {
				JSONObject jod = setTreeJosnObj(oneUs,
						oneUs,
						AhDashBoardGroup.DA_GROUP_TYPE_USERPROFILE_ALL ,2);
				((JSONArray)joa.get("items")).put(jod);
			}
		}
	}

	private JSONObject setTreeJosnObj(String label, String id, String ty, int lv) throws JSONException{
		JSONObject jso = new JSONObject();
		jso.put("label", label);
		jso.put("title", label);
		jso.put("id", id);
		jso.put("tp",ty);
		jso.put("lvl",lv);
		jso.put("expanded",true);
		jso.put("items", new JSONArray());
		return jso;
	}

	private void fetchTagList() throws JSONException{
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct classificationTag1,classificationTag2,classificationTag3 from hive_ap a");
		sql.append(" where a.owner=").append(getDomain().getId());
		sql.append(" and a.managestatus=").append(HiveAp.STATUS_MANAGED);
		sql.append(" and a.devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY);
//		String[] params = getSecondFilterParam();
//		if (params != null
//				&& params.length > 0
//				&& params[0]!=null) {
//			sql.append(" and a.map_container_id in (").append(params[0]).append(")");
//		}

		List<?> lst = QueryUtil.executeNativeQuery(sql.toString());
		TreeSet<String> ts1 = new TreeSet<String>();
		TreeSet<String> ts2 = new TreeSet<String>();
		TreeSet<String> ts3 = new TreeSet<String>();
		if(!lst.isEmpty()) {
			for(Object obj: lst){
				Object[] oneObj = (Object[])obj;
				if(oneObj[0]!=null && !oneObj[0].toString().equals("")) {
					ts1.add(oneObj[0].toString());
				}
				if(oneObj[1]!=null && !oneObj[1].toString().equals("")) {
					ts2.add(oneObj[1].toString());
				}
				if(oneObj[2]!=null && !oneObj[2].toString().equals("")) {
					ts3.add(oneObj[2].toString());
				}
			}
		}

		JSONArray items = (JSONArray)((JSONObject)filterList.get(0)).get("items");
		JSONObject joall = new JSONObject();
		JSONObject joa1 = new JSONObject();
		JSONObject joa2 = new JSONObject();
		JSONObject joa3 = new JSONObject();

		if (!ts1.isEmpty() || !ts2.isEmpty() || !ts3.isEmpty()) {
			joall = setTreeJosnObj(getText("hm.dashboard.tree.item.alltab"),
					AhDashBoardGroup.DA_GROUP_TYPE_TAG_ALL,
					AhDashBoardGroup.DA_GROUP_TYPE_TAG_ALL ,1);
			items.put(joall);
		}
		Map<String,String> tagMaps =  DeviceTagUtil.getInstance().getClassifierCustomTag(getDomain().getId());
		if(!ts1.isEmpty()) {
			joa1 = setTreeJosnObj(tagMaps.get(DeviceTagUtil.CUSTOM_TAG1),
					AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE,
					AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE ,2);
			((JSONArray)joall.get("items")).put(joa1);

			for(String oneTag: ts1) {
				JSONObject jobd = setTreeJosnObj(oneTag,
						oneTag,
						AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE ,3);
				((JSONArray)joa1.get("items")).put(jobd);
			}
		}

		if(!ts2.isEmpty()) {
			joa2 = setTreeJosnObj(tagMaps.get(DeviceTagUtil.CUSTOM_TAG2),
					AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO,
					AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO ,2);
			((JSONArray)joall.get("items")).put(joa2);

			for(String oneTag: ts2) {
				JSONObject jobd = setTreeJosnObj(oneTag,
						oneTag,
						AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO ,3);
				((JSONArray)joa2.get("items")).put(jobd);
			}
		}

		if(!ts3.isEmpty()) {
			joa3 = setTreeJosnObj(tagMaps.get(DeviceTagUtil.CUSTOM_TAG3),
					AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE,
					AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE ,2);
			((JSONArray)joall.get("items")).put(joa3);

			for(String oneTag: ts3) {
				JSONObject jobd = setTreeJosnObj(oneTag,
						oneTag,
						AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE ,3);
				((JSONArray)joa3.get("items")).put(jobd);
			}
		}
	}

	private void fetchNetworkPolicyList() throws JSONException {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct c.configname,c.id from hive_ap a, config_template c");
		sql.append(" where a.owner=").append(getDomain().getId());
		sql.append(" and a.managestatus=").append(HiveAp.STATUS_MANAGED);
		sql.append(" and a.devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY);
		sql.append(" and a.template_id = c.id");
//		String[] params = getSecondFilterParam();
//		if (params != null
//				&& params.length > 0
//				&& params[0]!=null) {
//			sql.append(" and a.map_container_id in (").append(params[0]).append(")");
//		}
		sql.append(" order by c.configname");

		List<?> lst = QueryUtil.executeNativeQuery(sql.toString());

		JSONArray items = (JSONArray)((JSONObject)filterList.get(0)).get("items");
		JSONObject joa = new JSONObject();
		if (!lst.isEmpty()) {
			joa = setTreeJosnObj(getText("hm.dashboard.tree.item.allpolicy"),
					AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY,
					AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY ,1);
			items.put(joa);
		}

		for(Object oneObj: lst) {
			Object[] oneItem = (Object[])oneObj;
			JSONObject jod = setTreeJosnObj(oneItem[0].toString(),
					oneItem[1].toString(),
					AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY ,2);
			((JSONArray)joa.get("items")).put(jod);
		}
	}

//	private void fetchDevicesList() throws JSONException {
//		filterList =  new JSONArray();
//		StringBuilder serSql = new StringBuilder();
//		serSql.append("select distinct hiveApModel,devicetype from hive_ap where owner=")
//		.append(getDomain().getId())
//		.append(" and managestatus=").append(HiveAp.STATUS_MANAGED)
//		.append(" and devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY);
//		String[] params = getSecondFilterParam();
//		if (params != null
//				&& params.length > 0
//				&& params[0]!=null) {
//			serSql.append(" and map_container_id in (").append(params[0]).append(")");
//		}
//
//		serSql.append(" order by devicetype,hiveApModel");
//
//		List<?> deviceList = QueryUtil.executeNativeQuery(serSql.toString());
//
//		JSONObject jo = setTreeJosnObj(getText("hm.dashboard.tree.item.allOthers"),
//				AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL,AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL,0);
//		filterList.put(jo);
//
//		if (!deviceList.isEmpty()) {
//			boolean apModel = false;
//			boolean brModel = false;
//			boolean swModel = false;
//			JSONObject joa = setTreeJosnObj(getText("hm.dashboard.tree.item.alldevice"),
//					AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ALL,AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ALL,1);
//			((JSONArray)jo.get("items")).put(joa);
//
//			JSONObject joap =new JSONObject();
//			JSONObject jobr =new JSONObject();
//			JSONObject josw =new JSONObject();
//			JSONObject jodet;
//
//			for(Object oneObj: deviceList){
//				Object[] oneRec = (Object[]) oneObj;
//				short dyModel = (Short)oneRec[0];
//				short dyty = (Short)oneRec[1];
//				if (dyty==HiveAp.Device_TYPE_HIVEAP && !apModel) {
//					joap = setTreeJosnObj(getText("hm.dashboard.tree.item.accesspoint"),
//							AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP,AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ALL,2);
//					((JSONArray)joa.get("items")).put(joap);
//					apModel = true;
//				}
//
//				if (dyty==HiveAp.Device_TYPE_BRANCH_ROUTER && !brModel) {
//					jobr = setTreeJosnObj(getText("hm.dashboard.tree.item.branchrouter"),
//							AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR,AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ALL,2);
//					((JSONArray)joa.get("items")).put(jobr);
//					brModel = true;
//				}
//				if (dyty==HiveAp.Device_TYPE_SWITCH && !swModel) {
//					josw = setTreeJosnObj(getText("hm.dashboard.tree.item.switch"),
//							AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR,AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ALL,2);
//					((JSONArray)joa.get("items")).put(josw);
//					swModel = true;
//				}
//				String[] tmp = getDeviceTypeAndText(dyty, dyModel);
//				if (tmp==null) continue;
//				jodet = setTreeJosnObj(tmp[1],tmp[0],AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ALL,3);
//				if (dyty==HiveAp.Device_TYPE_HIVEAP){
//					((JSONArray)joap.get("items")).put(jodet);
//				} else if (dyty==HiveAp.Device_TYPE_BRANCH_ROUTER){
//					((JSONArray)jobr.get("items")).put(jodet);
//				} else if (dyty==HiveAp.Device_TYPE_SWITCH) {
//					((JSONArray)josw.get("items")).put(jodet);
//				}
//
//				if (params != null
//						&& params.length > 2
//						&& params[1]!=null) {
//					if(params[1].equals(tmp[0] + AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH)){
//						List<?> hName = QueryUtil.executeQuery("select hostName from " + HiveAp.class.getSimpleName(),
//								null, new FilterParams("macAddress", params[2]));
//						JSONObject jode;
//						if (!hName.isEmpty()) {
//							jode = setTreeJosnObj(hName.get(0).toString(),
//									params[2],tmp[0] + AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH,4);
//						} else {
//							jode = setTreeJosnObj(params[2],
//									params[2],tmp[0] + AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH,4);
//						}
//						((JSONArray)jodet.get("items")).put(jode);
//					}
//				}
//
//				JSONObject jode =  setTreeJosnObj("...",
//						tmp[0] + AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_FETCHMORE,
//						AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SINGLE,4);
//				((JSONArray)jodet.get("items")).put(jode);
//			}
//		}
//	}

	public Short[] fetchParamFilter(String deviceType) {
		Short[] tmp = new Short[2];
		switch (deviceType) {
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_20:
				tmp[0] = HiveAp.HIVEAP_MODEL_20;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_28:
				tmp[0] = HiveAp.HIVEAP_MODEL_28;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_110:
				tmp[0] = HiveAp.HIVEAP_MODEL_110;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_120:
				tmp[0] = HiveAp.HIVEAP_MODEL_120;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_121:
				tmp[0] = HiveAp.HIVEAP_MODEL_121;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_141:
				tmp[0] = HiveAp.HIVEAP_MODEL_141;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_170:
				tmp[0] = HiveAp.HIVEAP_MODEL_170;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_320:
				tmp[0] = HiveAp.HIVEAP_MODEL_320;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_330:
				tmp[0] = HiveAp.HIVEAP_MODEL_330;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_340:
				tmp[0] = HiveAp.HIVEAP_MODEL_340;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_350:
				tmp[0] = HiveAp.HIVEAP_MODEL_350;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_370:
				tmp[0] = HiveAp.HIVEAP_MODEL_370;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_380:
				tmp[0] = HiveAp.HIVEAP_MODEL_380;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_BR100:
				tmp[0] = HiveAp.HIVEAP_MODEL_BR100;
				tmp[1] = HiveAp.Device_TYPE_HIVEAP;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_100:
				tmp[0] = HiveAp.HIVEAP_MODEL_BR100;
				tmp[1] = HiveAp.Device_TYPE_BRANCH_ROUTER;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_200:
				tmp[0] = HiveAp.HIVEAP_MODEL_BR200;
				tmp[1] = HiveAp.Device_TYPE_BRANCH_ROUTER;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_200WP:
				tmp[0] = HiveAp.HIVEAP_MODEL_BR200_WP;
				tmp[1] = HiveAp.Device_TYPE_BRANCH_ROUTER;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_200LTEVZ:
				tmp[0] = HiveAp.HIVEAP_MODEL_BR200_LTE_VZ;
				tmp[1] = HiveAp.Device_TYPE_BRANCH_ROUTER;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_AP330:
				tmp[0] = HiveAp.HIVEAP_MODEL_330;
				tmp[1] = HiveAp.Device_TYPE_BRANCH_ROUTER;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_AP350:
				tmp[0] = HiveAp.HIVEAP_MODEL_350;
				tmp[1] = HiveAp.Device_TYPE_BRANCH_ROUTER;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_24:
				tmp[0] = HiveAp.HIVEAP_MODEL_SR24;
				tmp[1] = HiveAp.Device_TYPE_SWITCH;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_2124P:
				tmp[0] = HiveAp.HIVEAP_MODEL_SR2124P;
				tmp[1] = HiveAp.Device_TYPE_SWITCH;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_2024P:
				tmp[0] = HiveAp.HIVEAP_MODEL_SR2024P;
				tmp[1] = HiveAp.Device_TYPE_SWITCH;
				return tmp;				
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_2148P:
				tmp[0] = HiveAp.HIVEAP_MODEL_SR2148P;
				tmp[1] = HiveAp.Device_TYPE_SWITCH;
				return tmp;
			case AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_48:
				tmp[0] = HiveAp.HIVEAP_MODEL_SR48;
				tmp[1] = HiveAp.Device_TYPE_SWITCH;
				return tmp;
			}

		return null;
	}

	private String[] getDeviceTypeAndText(short type, short model){
		String[] tmp = new String[2];
		switch (type) {
		case HiveAp.Device_TYPE_HIVEAP:
			switch (model) {
				case HiveAp.HIVEAP_MODEL_20:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_20;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_20;
					return tmp;
				case HiveAp.HIVEAP_MODEL_28:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_28;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_28;
					return tmp;
				case HiveAp.HIVEAP_MODEL_110:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_110;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_110;
					return tmp;
				case HiveAp.HIVEAP_MODEL_120:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_120;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_120;
					return tmp;
				case HiveAp.HIVEAP_MODEL_121:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_121;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_121;
					return tmp;
				case HiveAp.HIVEAP_MODEL_141:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_141;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_141;
					return tmp;
				case HiveAp.HIVEAP_MODEL_170:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_170;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_170;
					return tmp;
				case HiveAp.HIVEAP_MODEL_320:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_320;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_320;
					return tmp;
				case HiveAp.HIVEAP_MODEL_330:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_330;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_330;
					return tmp;
				case HiveAp.HIVEAP_MODEL_340:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_340;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_340;
					return tmp;
				case HiveAp.HIVEAP_MODEL_350:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_350;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_350;
					return tmp;
				case HiveAp.HIVEAP_MODEL_370:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_370;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_370;
					return tmp;
				case HiveAp.HIVEAP_MODEL_380:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_380;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_380;
					return tmp;
				case HiveAp.HIVEAP_MODEL_BR100:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_BR100;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_100;
					return tmp;
				}
		case HiveAp.Device_TYPE_BRANCH_ROUTER:
			switch (model) {
				case HiveAp.HIVEAP_MODEL_BR100:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_100;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_100;
					return tmp;
				case HiveAp.HIVEAP_MODEL_BR200:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_200;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_200;
					return tmp;
				case HiveAp.HIVEAP_MODEL_BR200_WP:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_200WP;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_200WP;
					return tmp;
				case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_200LTEVZ;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_200LTEVZ;
					return tmp;
				case HiveAp.HIVEAP_MODEL_330:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_AP330;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_330;
					return tmp;
				case HiveAp.HIVEAP_MODEL_350:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_AP350;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_350;
					return tmp;
			}

		case HiveAp.Device_TYPE_SWITCH:
			switch (model) {
				case HiveAp.HIVEAP_MODEL_SR24:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_24;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_24;
					return tmp;
				case HiveAp.HIVEAP_MODEL_SR2124P:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_2124P;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_2124P;
					return tmp;
				case HiveAp.HIVEAP_MODEL_SR2024P:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_2024P;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_2024P;
					return tmp;					
				case HiveAp.HIVEAP_MODEL_SR2148P:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_2148P;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_2148P;
					return tmp;	
				case HiveAp.HIVEAP_MODEL_SR48:
					tmp[0] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_48;
					tmp[1] = AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR_48;
					return tmp;
			}
		}
		return null;
	}


	private String getTreeData(List<AhDashBoardGroup> lst, boolean isJson){
		StringBuilder results = new StringBuilder();
		int level=0;
		results.append("[");
		for(AhDashBoardGroup gp: lst){
			if (level>gp.getLevel()) {
				for(int i=0; i<(level-gp.getLevel()); i++) {
					results.append("]} , ");
				}
			}
			results.append("{ label:'").append(gp.getTextConvert(isJson)).append("', title:'").append(gp.getTextConvert(isJson)).append("', id:'").append(gp.getId()).append("', tp:'").append(gp.getType()).append("', lvl: ").append(gp.getLevel()).append(", expanded:").append(gp.isHasChild());
			results.append(", items: [");
			if (!gp.isHasChild()) {
				results.append("]}");
				if (gp.isNeedCombo()) {
					results.append(" , ");
				} else {
					results.append("");
				}
			}

			level= gp.getLevel();
		}
		for(int i=0; i<level; i++) {
			results.append("]}");
		}
		results.append("]");
		System.out.println(results.toString().replaceAll("]} , ]}", "]}]}").replaceAll("]} , ]}", "]}]}"));

		return results.toString().replaceAll("]} , ]}", "]}]}").replaceAll("]} , ]}", "]}]}");
	}

	public long getConvertCustomStartTime() {
		return getConvertCustomTime(this.startTime, this.startHour);
	}

	public long getConvertCustomEndTime() {
		return getConvertCustomTime(this.endTime, this.endHour);
	}

	private long getConvertCustomTime(String timeArg, int hourArg) {
		if (timeArg != null && !timeArg.equals("")) {
			String datetime[] = timeArg.split("-");
			Calendar calendar = Calendar.getInstance(tz);
			calendar.clear(Calendar.MINUTE);
			calendar.clear(Calendar.SECOND);
			calendar.clear(Calendar.MILLISECOND);
			calendar.set(Calendar.YEAR, Integer.parseInt(datetime[0]));
			calendar.set(Calendar.MONTH, Integer.parseInt(datetime[1]) - 1);
			calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datetime[2]));
			calendar.set(Calendar.HOUR_OF_DAY, hourArg);
			return calendar.getTimeInMillis();
		} else {
			return 0;
		}
	}

	public List<EnumItem> getLstComponentGroupType(){
		List<EnumItem> tmp = new ArrayList<EnumItem>();
		tmp.add(new EnumItem(DashboardComponent.COMPONENT_GROUP_APPLICATION,
			MgrUtil.getEnumString("enum.da.widget.grouptype." + DashboardComponent.COMPONENT_GROUP_APPLICATION)));
		//tmp.add(new EnumItem(DashboardComponent.COMPONENT_GROUP_USERS,
		//		MgrUtil.getEnumString("enum.da.widget.grouptype." + DashboardComponent.COMPONENT_GROUP_USERS)));
		tmp.add(new EnumItem(DashboardComponent.COMPONENT_GROUP_CLIENTS,
				MgrUtil.getEnumString("enum.da.widget.grouptype." + DashboardComponent.COMPONENT_GROUP_CLIENTS)));
		tmp.add(new EnumItem(DashboardComponent.COMPONENT_GROUP_AEROHIVEDEVICE,
				MgrUtil.getEnumString("enum.da.widget.grouptype." + DashboardComponent.COMPONENT_GROUP_AEROHIVEDEVICE)));
		tmp.add(new EnumItem(DashboardComponent.COMPONENT_GROUP_NETWORK,
				MgrUtil.getEnumString("enum.da.widget.grouptype." + DashboardComponent.COMPONENT_GROUP_NETWORK)));
		return tmp;
	}

	public static final int COMPONENT_GROUP_SUB_TYPE_DEFAULT = 1;
	public static final int COMPONENT_GROUP_SUB_TYPE_USER_DEFINED = 2;

	public List<EnumItem> getLstComponentGroupSubType() {
		List<EnumItem> tmp = new ArrayList<EnumItem>();
		tmp.add(new EnumItem(COMPONENT_GROUP_SUB_TYPE_DEFAULT, MgrUtil
				.getEnumString("enum.da.widget.group.sub.type."
						+ COMPONENT_GROUP_SUB_TYPE_DEFAULT)));
		tmp.add(new EnumItem(COMPONENT_GROUP_SUB_TYPE_USER_DEFINED, MgrUtil
				.getEnumString("enum.da.widget.group.sub.type."
						+ COMPONENT_GROUP_SUB_TYPE_USER_DEFINED)));
		return tmp;
	}

	private JSONArray getDashbordComponents() {
		JSONArray jsonArray = new JSONArray();
		try {
			String where;
			Object[] bindings;
			if (getIsInHomeDomain()) {
				where = "componentName is not null and componentName != :s1 and specifyType = :s2";
				bindings = new Object[] { "",
						DashboardComponent.WIDGET_SPECIFY_TYPE_NONE };
			} else {
				where = "componentName is not null and componentName != :s1 and specifyType = :s2 and homeonly = :s3";
				bindings = new Object[] { "",
						DashboardComponent.WIDGET_SPECIFY_TYPE_NONE, false };
			}
			List<DashboardComponent> list = QueryUtil.executeQuery(
					DashboardComponent.class, new SortParams("componentName"),
					new FilterParams(where, bindings), getDomainId());
			for (DashboardComponent component : list) {
				JSONObject jsonObject = getDashbordComponent(component);
				jsonArray.put(jsonObject);
			}
		} catch (Exception e) {
			log.error("getDashbordComponents error", e);
		}
		return jsonArray;
	}

	public JSONObject getDashbordComponent(DashboardComponent component)
			throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", component.getId());
		jsonObject.put("t", component.getComponentGroup());
		String name = component.getComponentName();
		if (name.length() > 23) {
			String fullName = name;
			name = name.substring(0, 20) + "...";
			jsonObject.put("fn", fullName);
		}
		jsonObject.put("n", name);
		boolean isDefault = component.isDefaultFlag();
		jsonObject.put("st", isDefault ? COMPONENT_GROUP_SUB_TYPE_DEFAULT
				: COMPONENT_GROUP_SUB_TYPE_USER_DEFINED);
		jsonObject.put("del", isDefault ? false : true);
		return jsonObject;
	}

	public String getDashbordComponentsJsonString() {
		return getDashbordComponents().toString();
	}

//	public List<EnumItem> getLstSpecifyType(){
//		List<EnumItem> tmp = new ArrayList<EnumItem>();
//		tmp.add(new EnumItem(DashboardComponent.WIDGET_SPECIFY_TYPE_NONE, "None of client/user/application"));
//		tmp.add(new EnumItem(DashboardComponent.WIDGET_SPECIFY_TYPE_CLIENT, "Client w/ specified MAC"));
//		tmp.add(new EnumItem(DashboardComponent.WIDGET_SPECIFY_TYPE_USER, "User w/ specified name"));
//		tmp.add(new EnumItem(DashboardComponent.WIDGET_SPECIFY_TYPE_APP, "Application w/ specified name"));
//		return tmp;
//	}
//
//	public List<EnumItem> getLstSpecifyApplicationName(){
//		List<EnumItem> tmp = new ArrayList<EnumItem>();
//		List<?> appList = QueryUtil.executeQuery("select appCode, appName from " + Application.class.getSimpleName(),
//				new SortParams("appName"), null);
//		for(Object oneob: appList){
//			Object[] oneItem = (Object[]) oneob;
//			tmp.add(new EnumItem(Integer.parseInt(oneItem[0].toString()), oneItem[1].toString()));
//		}
//		return tmp;
//	}

	public String removeWidgetConfigById(Long remId) throws Exception {
		DashboardComponent com = QueryUtil.findBoById(DashboardComponent.class, remId, this);
		if(com==null) return "";
		if(com.isDefaultFlag()) {
			return MgrUtil.getUserMessage("error.da.component.item.default");
		} else {
			List<?> usedLst = QueryUtil.executeQuery("select id from " + DashboardComponent.class.getSimpleName(),
					null,new FilterParams("daComponent.id", remId));
			if (!usedLst.isEmpty()) {
				return MgrUtil.getUserMessage("error.da.remove.fail.byuser", new String[]{"Widget"});
			}
			usedLst = QueryUtil.executeQuery("select id from " + AhDashboardWidget.class.getSimpleName(),
					null,new FilterParams("widgetConfig.id", remId));
			boolean ret;
			if (usedLst.isEmpty()) {
				ret = QueryUtil.removeBo(DashboardComponent.class, remId);
			} else {
				return MgrUtil.getUserMessage("error.da.remove.fail.byuser", new String[]{"Widget"});
			}

			if(ret) {
				if (com.getComponentMetric()!=null && (com.getComponentMetric().getMetricName()==null ||
						com.getComponentMetric().getMetricName().equals(""))) {
					QueryUtil.removeBo(DashboardComponentMetric.class, com.getComponentMetric().getId());
				}
			} else {
				return MgrUtil.getUserMessage("error.da.remove.fail.byuser", new String[]{"Widget"});
			}
		}
		return "";
	}

	public String removeMetricById(Long remId) throws Exception{
		DashboardComponentMetric com = QueryUtil.findBoById(DashboardComponentMetric.class, remId, this);
		if(com==null) return "";
		if(com.isDefaultFlag()) {
			return MgrUtil.getUserMessage("error.da.component.item.default");
		} else {
			List<?> usedLst = QueryUtil.executeQuery("select id from " + DashboardComponent.class.getSimpleName(),
					null,new FilterParams("componentMetric.id", remId));
			if (!usedLst.isEmpty()) {
				return MgrUtil.getUserMessage("error.da.remove.fail.byuser", new String[]{"Metric"});
			}
			boolean ret = QueryUtil.removeBo(DashboardComponentMetric.class, remId);
			if(!ret) {
				return MgrUtil.getUserMessage("error.da.remove.fail.byuser", new String[]{"Metric"});
			}
		}
		return "";
	}



	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof AhDashboard) {
			AhDashboard da = (AhDashboard)bo;
			if (da.getDaLayouts() != null) {
				da.getDaLayouts().size();
				loadLazyWidgets(da.getDaLayouts(), da.getId().toString());
			}
		}
		if (bo instanceof AhDashboardLayout) {
			loadLazySingleWidget((AhDashboardLayout)bo, null);
		}
		if (bo instanceof AhDashboardWidget) {
			AhDashboardWidget daWidget = (AhDashboardWidget)bo;
			if (daWidget.getWidgetConfig() != null) {
				daWidget.getWidgetConfig().getId();
				if (daWidget.getWidgetConfig().getComponentMetric() != null) {
					daWidget.getWidgetConfig().getComponentMetric().getId();
					if (daWidget.getWidgetConfig().getComponentMetric().getComponentData() != null) {
						daWidget.getWidgetConfig().getComponentMetric().getComponentData().size();
					}
				}
			}
		}
		if (bo instanceof DashboardComponent) {
			DashboardComponent da = (DashboardComponent)bo;
			if (da.getComponentMetric()!=null) {
				da.getComponentMetric().getId();
				if (da.getComponentMetric().getComponentData() != null) {
					da.getComponentMetric().getComponentData().size();
				}
			}
		}

		if(bo instanceof SsidProfile) {
			SsidProfile ssid = (SsidProfile) bo;
			if(ssid.getUserProfileDefault()!=null) {
				ssid.getUserProfileDefault().getId();
			}
			if(ssid.getUserProfileSelfReg()!=null) {
				ssid.getUserProfileSelfReg().getId();
			}
			if(ssid.getRadiusUserProfile()!=null) {
				ssid.getRadiusUserProfile().size();
			}
		}

		return null;
	}

	private void loadLazyWidgets(Set<AhDashboardLayout> layouts, String tabIdArg) {
		if (layouts != null
				&& !layouts.isEmpty()) {
			for (AhDashboardLayout layout : layouts) {
				loadLazySingleWidget(layout, tabIdArg);
			}
		}
	}
	private void loadLazySingleWidget(AhDashboardLayout layout, String tabIdArg) {
		if (layout.getDaWidgets() != null
				&& (tabIdArg == null
						|| tabIdArg.equals(layout.getTabId()))
				) {
			layout.getDaWidgets().size();
			for (AhDashboardWidget widget : layout.getDaWidgets()) {
				widget.getId();
				if (widget.getWidgetConfig() != null) {
					widget.getWidgetConfig().getId();

					if (widget.getWidgetConfig().getComponentMetric() != null) {
						widget.getWidgetConfig().getComponentMetric().getId();
						if (widget.getWidgetConfig().getComponentMetric().getComponentData() != null) {
							widget.getWidgetConfig().getComponentMetric().getComponentData().size();
						}
					}
				}
			}
		}
	}

	public TimeZone tz;

	/**
	 * @return the tz
	 */
	public TimeZone getTz() {
		return tz;
	}



//	public List<AhDashBoardGroup> getTopyList() {
//		return topyList;
//	}

	public JSONArray getFilterList() {
		return filterList;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public String getTreeId() {
		return treeId;
	}

	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}

	public String getTreeType() {
		return treeType;
	}

	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}

	public int getTimeType() {
		return timeType;
	}

	public void setTimeType(int timeType) {
		this.timeType = timeType;
	}

	//for json, you can pass arguments in array
	protected String[] columnsConfig;
	protected String[] columnCharts;
	protected Map<Integer, List<WidgetConfig>> widgetConfigs;
	protected boolean bgRollup;
	protected boolean bgRollup2;
	protected Long boardId;

	protected String curDealTabId;
	protected boolean widgetChecked;
	protected Long widgetConfigId;

	protected class WidgetConfig {
		private Long reportId;
		private String title;
		private boolean blnOverTime;
		private boolean blnCloned;
		private Long preWidgetId = -1L;
//		private Long mapId;
		private String objectType = AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL;
		private String objectId = String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
		private String filterObjectType = AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL;
		private String filterObjectId = String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);

		protected int selectTimeType;
		protected boolean enableTimeLocal;
		protected String startTime;
		protected int startHour;
		protected String endTime;
		protected int endHour;

		protected boolean checked;

		public Long getPreWidgetId() {
			return preWidgetId;
		}
		public void setPreWidgetId(Long preWidgetId) {
			this.preWidgetId = preWidgetId;
		}
		public Long getReportId() {
			return reportId;
		}
		public void setReportId(Long reportId) {
			this.reportId = reportId;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public boolean isBlnOverTime() {
			return blnOverTime;
		}
		public void setBlnOverTime(boolean blnOverTime) {
			this.blnOverTime = blnOverTime;
		}
		public boolean isBlnCloned() {
			return blnCloned;
		}
		public void setBlnCloned(boolean blnCloned) {
			this.blnCloned = blnCloned;
		}
//		public Long getMapId() {
//			return mapId;
//		}
//		public void setMapId(Long mapId) {
//			this.mapId = mapId;
//		}
		public String getObjectType() {
			return objectType;
		}
		public void setObjectType(String objectType) {
			this.objectType = objectType;
		}
		public String getObjectId() {
			return objectId;
		}
		public void setObjectId(String objectId) {
			this.objectId = objectId;
		}
		public String getFilterObjectType() {
			return filterObjectType;
		}
		public void setFilterObjectType(String filterObjectType) {
			this.filterObjectType = filterObjectType;
		}
		public String getFilterObjectId() {
			return filterObjectId;
		}
		public void setFilterObjectId(String filterObjectId) {
			this.filterObjectId = filterObjectId;
		}
	}
	public String[] getColumnsConfig() {
		return columnsConfig;
	}

	public void setColumnsConfig(String[] columnsConfig) {
		this.columnsConfig = columnsConfig;
	}

	public String[] getColumnCharts() {
		return columnCharts;
	}

	public void setColumnCharts(String[] columnCharts) {
		this.columnCharts = columnCharts;
	}


	private Map<Long, DashboardComponent> getAllWidgetConfigs() {
		return this.getAllWidgetConfigs(false);
	}
	private Map<Long, DashboardComponent> getAllWidgetConfigs(boolean blnLazyLoad) {
		Map<Long, DashboardComponent> result = new HashMap<Long, DashboardComponent>();

		if (widgetConfigs != null
				&& !widgetConfigs.isEmpty()) {
			Set<Long> configIds = new HashSet<Long>();
			for (List<WidgetConfig> wconfigs : widgetConfigs.values()) {
				if (wconfigs != null
						&& !wconfigs.isEmpty()) {
					for (WidgetConfig wconfig : wconfigs) {
						configIds.add(wconfig.getReportId());
					}
				}
			}

			if (!configIds.isEmpty()) {
				List<DashboardComponent> configs = null;
				if (blnLazyLoad) {
					configs = QueryUtil.executeQuery(DashboardComponent.class,
						null,
						new FilterParams("id", configIds),
						getDomain().getId(),
						this);
				} else {
					configs = QueryUtil.executeQuery(DashboardComponent.class,
							null,
							new FilterParams("id", configIds),
							getDomain().getId());
				}
				if (configs != null
						&& !configs.isEmpty()) {
					for (DashboardComponent config : configs) {
						result.put(config.getId(), config);
					}
				}
			}
		}

		return result;
	}

	public void createNewWidgetConfigForDefaultClonedWidget() {
		if (widgetConfigs != null
				&& !widgetConfigs.isEmpty()) {
			List<Long> clonedIds = new ArrayList<Long>();
			for (List<WidgetConfig> widgets : widgetConfigs.values()) {
				if (widgets != null
						&& !widgets.isEmpty()) {
					for (WidgetConfig widget : widgets) {
						if (widget.isBlnCloned()) {
							clonedIds.add(widget.getReportId());
						}
					}
				}
			}

			if (!clonedIds.isEmpty()) {
				List<DashboardComponent> components = QueryUtil.executeQuery(DashboardComponent.class,
						null,
						new FilterParams("id", clonedIds),
						getDomain().getId());
				if (components != null
						&& !components.isEmpty()) {
					Map<Long, Long> idMap = new HashMap<Long, Long>();
					List<Long> oldIds = new ArrayList<Long>();
					for (DashboardComponent component : components) {
						oldIds.add(component.getId());
						component.setId(null);
					}
					try {
						QueryUtil.bulkCreateBos(components);
						for (int i = 0; i < components.size(); i++) {
							idMap.put(oldIds.get(i), components.get(i).getId());
						}
						for (List<WidgetConfig> widgets : widgetConfigs.values()) {
							if (widgets != null
									&& !widgets.isEmpty()) {
								for (WidgetConfig widget : widgets) {
									if (widget.isBlnCloned()
											&& idMap.containsKey(widget.getReportId())) {
										widget.setReportId(idMap.get(widget.getReportId()));
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						log.error("Failed to create dashboard component for cloned widget.", e);
					}
				}
			}
		}
	}

	protected class ComponentConfigs {
		private Long id;
		private String xAxisKey;
		private JSONObject configObj;

		public ComponentConfigs(Long id) {
			this.id = id;
		}
		public ComponentConfigs(Long id, String xAxisKey) {
			this(id);
			this.xAxisKey = xAxisKey;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getxAxisKey() {
			return xAxisKey;
		}

		public void setxAxisKey(String xAxisKey) {
			this.xAxisKey = xAxisKey;
		}
		public JSONObject getConfigObj() {
			return configObj;
		}
		public void setConfigObj(JSONObject configObj) {
			this.configObj = configObj;
		}
	}

	public Map<Long, ComponentConfigs> saveDaLayouts(AhDashboard daArg) {
		return saveDaLayouts(daArg, true);
	}

	public void prepareWidgetConfigs() {

	}
	public Map<Long, ComponentConfigs> saveDaLayouts(AhDashboard daArg, boolean blnDataSource) {
		if (daArg == null) {
			return null;
		}
		this.curDealTabId = String.valueOf(daArg.getId());
		prepareWidgetConfigs();

		//clear widget settings for current dashboard
		if (widgetConfigs == null
				|| widgetConfigs.isEmpty()) {
			removeDaLayouts(this.curDealTabId, null);
			if (blnDataSource
					&& this.getDataSource() != null) {
				this.getDataSource().setDaLayouts(null);
			}
			return null;
		}

//		if (blnDataSource && getDataSource() != null
//				&& getDataSource().getId() == null) {
//			try {
//				updateDataSourceValue();
//			} catch(Exception e) {
//				e.printStackTrace();
//				log.error("Failed to save a unsaved dashboard while saving layouts for it.", e);
//			}
//		}

		Map<Long, AhDashboardWidget> existedWidgetsMap = new HashMap<Long, AhDashboardWidget>();
		List<Long> idsTmp = new ArrayList<Long>();
		for (Integer key : widgetConfigs.keySet()) {
			if (widgetConfigs.get(key) != null) {
				for (WidgetConfig wconfig : widgetConfigs.get(key)) {
					if (wconfig.getPreWidgetId().compareTo(0L) > 0) {
						idsTmp.add(wconfig.getPreWidgetId());
					}
				}
			}
		}
		if (!idsTmp.isEmpty()) {
			List<AhDashboardWidget> existedWidgets = QueryUtil.executeQuery(AhDashboardWidget.class,
					null,
					new FilterParams("id", idsTmp),
					getDomain().getId(),
					this);
			if (existedWidgets != null
					&& !existedWidgets.isEmpty()) {
				for (AhDashboardWidget widget : existedWidgets) {
					existedWidgetsMap.put(widget.getId(), widget);
				}
			}
		}

		String tabIdTmp = daArg.getId().toString();
		Map<Long, DashboardComponent> configs = getAllWidgetConfigs();
		Set<AhDashboardLayout> layouts = new HashSet<AhDashboardLayout>();
		List<Long> componentIdsInUse = new ArrayList<Long>();
		for (Integer key : widgetConfigs.keySet()) {
			AhDashboardLayout daLayout = new AhDashboardLayout();
			daLayout.setSizeType(convertToSizeType(columnsConfig[key]));
			daLayout.setItemOrder(key.byteValue());
			daLayout.setTabId(tabIdTmp);
			daLayout.setDashboard(daArg);
			daLayout.setOwner(this.getDomain());
			if (widgetConfigs.get(key) != null) {
				daLayout.setDaWidgets(new HashSet<AhDashboardWidget>());
				int chartOrder = 0;
				for (WidgetConfig wconfig : widgetConfigs.get(key)) {
					AhDashboardWidget widget = new AhDashboardWidget();
					widget.setItemOrder(chartOrder++);
					widget.setReportId(wconfig.getReportId());
					widget.setMainTitle(wconfig.getTitle());
					widget.setOwner(this.getDomain());
					widget.setDaLayout(daLayout);
					widget.setWidgetConfig(configs.get(wconfig.getReportId()));
					//widget.setBlnOverTime(wconfig.isBlnOverTime());

					if (existedWidgetsMap.get(wconfig.getPreWidgetId()) != null) {
						existedWidgetsMap.get(wconfig.getPreWidgetId()).cloneConfigsToNew(widget);
					}

					widget.setBlnChecked(wconfig.checked);
					if (widget.isBlnChecked()) {
//						if (wconfig.getMapId() != null) {
//							widget.setLocation(QueryUtil.findBoById(MapContainerNode.class, wconfig.getMapId()));
//						}
						widget.setObjectType(wconfig.getObjectType());
						widget.setObjectId(wconfig.getObjectId());
						widget.setFilterObjectType(wconfig.getFilterObjectType());
						widget.setFilterObjectId(wconfig.getFilterObjectId());
	
						widget.setSelectTimeType(wconfig.selectTimeType);
						widget.setCustomStartTime(this.getConvertCustomTime(wconfig.startTime, wconfig.startHour));
						widget.setCustomEndTime(this.getConvertCustomTime(wconfig.endTime, wconfig.endHour));
						widget.setEnableTimeLocal(wconfig.enableTimeLocal);
					}

					daLayout.getDaWidgets().add(widget);
					componentIdsInUse.add(wconfig.getReportId());
				}
			}
			layouts.add(daLayout);
		}

		try {
			removeDaLayouts(this.curDealTabId, componentIdsInUse);
			QueryUtil.bulkCreateBos(layouts);
			List<Long> lids = new ArrayList<Long>();
			if (layouts != null
					&& !layouts.isEmpty()) {
				for (AhDashboardLayout layoutTmp : layouts) {
					lids.add(layoutTmp.getId());
				}
			}
			layouts = new HashSet<AhDashboardLayout>(
					QueryUtil.executeQuery(AhDashboardLayout.class,
							null,
							new FilterParams("id", lids),
							getDomain().getId(),
							this));

			if (blnDataSource) {
				if (getDataSource().getDaLayouts() != null
						&& !getDataSource().getDaLayouts().isEmpty()) {
					for (AhDashboardLayout layout : getDataSource().getDaLayouts()) {
						if (layout != null
								&& layout.getTabId() != null
								&& !layout.getTabId().equals(tabIdTmp)) {
							layouts.add(layout);
						}
					}
				}
				getDataSource().setDaLayouts(layouts);
			}

			Map<Long, ComponentConfigs> result = new HashMap<Long, ComponentConfigs>(layouts.size());
			for (AhDashboardLayout layout : layouts) {
				if (layout != null
						&& layout.getDaWidgets() != null) {
					for (AhDashboardWidget widget : layout.getDaWidgets()) {
						result.put(widget.getReportId(), new ComponentConfigs(widget.getId()));
						if (widget.getWidgetConfig() != null
								&& widget.getWidgetConfig().getComponentMetric() != null) {
							result.get(widget.getReportId()).setxAxisKey(widget.getWidgetConfig().getComponentMetric().getSourceType());
							JSONObject configTmp = new JSONObject();
							configTmp.put("widgetConfig", widget.getWidgetConfigJSONObject(tz, daArg));
							result.get(widget.getReportId()).setConfigObj(configTmp);
						}
					}
				}
			}
			return result;
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}

		return null;
	}
	
	protected void saveDaLayoutsToDashboardSession(AhDashboard daArg) {
		if (daArg == null) {
			return;
		}
		prepareWidgetConfigs();

		daArg.setDaLayouts(null);
		if (widgetConfigs == null
				|| widgetConfigs.isEmpty()) {
			return;
		}

		Map<Long, DashboardComponent> configs = getAllWidgetConfigs(true);
		Set<AhDashboardLayout> layouts = new HashSet<AhDashboardLayout>();
		for (Integer key : widgetConfigs.keySet()) {
			AhDashboardLayout daLayout = new AhDashboardLayout();
			daLayout.setSizeType(convertToSizeType(columnsConfig[key]));
			daLayout.setItemOrder(key.byteValue());
			daLayout.setTabId("-1");
			daLayout.setDashboard(daArg);
			daLayout.setOwner(this.getDomain());
			if (widgetConfigs.get(key) != null) {
				daLayout.setDaWidgets(new HashSet<AhDashboardWidget>());
				int chartOrder = 0;
				for (WidgetConfig wconfig : widgetConfigs.get(key)) {
					AhDashboardWidget widget = new AhDashboardWidget();
					widget.setItemOrder(chartOrder++);
					widget.setReportId(wconfig.getReportId());
					widget.setMainTitle(wconfig.getTitle());
					widget.setOwner(this.getDomain());
					widget.setDaLayout(daLayout);
					widget.setWidgetConfig(configs.get(wconfig.getReportId()));
					//widget.setBlnOverTime(wconfig.isBlnOverTime());

					widget.setBlnChecked(wconfig.checked);
					if (widget.isBlnChecked()) {
//						if (wconfig.getMapId() != null) {
//							widget.setLocation(QueryUtil.findBoById(MapContainerNode.class, wconfig.getMapId()));
//						}
						widget.setObjectType(wconfig.getObjectType());
						widget.setObjectId(wconfig.getObjectId());
						widget.setFilterObjectType(wconfig.getFilterObjectType());
						widget.setFilterObjectId(wconfig.getFilterObjectId());
	
						widget.setSelectTimeType(wconfig.selectTimeType);
						widget.setCustomStartTime(this.getConvertCustomTime(wconfig.startTime, wconfig.startHour));
						widget.setCustomEndTime(this.getConvertCustomTime(wconfig.endTime, wconfig.endHour));
						widget.setEnableTimeLocal(wconfig.enableTimeLocal);
					}

					daLayout.getDaWidgets().add(widget);
				}
			}
			layouts.add(daLayout);
		}

		daArg.setDaLayouts(layouts);
	}
	
	public Map<Byte, List<AhDashboardWidget>> getCurrentDaWidgets() {
		Map<Byte, List<AhDashboardWidget>> result =
			new HashMap<Byte, List<AhDashboardWidget>>();

		if (this.getDataSource() != null
				&& this.getDataSource().getDaLayouts() != null) {
//			String curTabIdTmp = this.getDataSource().getId().toString();
			for (AhDashboardLayout layout : this.getDataSource().getDaLayouts()) {
//				if (curTabIdTmp.equals(layout.getTabId())) {
					List<AhDashboardWidget> widgetLst = new ArrayList<AhDashboardWidget>(layout.getDaWidgets());
					Collections.sort(widgetLst, new Comparator<AhDashboardWidget>() {
						@Override
			 			public int compare(AhDashboardWidget o1, AhDashboardWidget o2) {
			 				return o1.getItemOrder() - o2.getItemOrder();
			 			}
					});
					result.put(layout.getItemOrder(), widgetLst);
//				}
			}
		}

		return result;
	}
	public List<?> removeDaLayouts(String daIdArg, List<Long> componentIdsInUse) {
		if (StringUtils.isBlank(daIdArg)) {
			return null;
		}

		try {
			List<AhDashboardLayout> daLayouts = QueryUtil.executeQuery(AhDashboardLayout.class,
					null,
					new FilterParams("dashboard.id=:s1", new Object[]{Long.valueOf(daIdArg)}),
					getDomain().getId(), this);
			List<Long> layoutIds = new ArrayList<Long>();
			if (daLayouts != null
					&& !daLayouts.isEmpty()) {
				List<Long> widgetIds = new ArrayList<Long>();
				List<Long> componentIds = new ArrayList<Long>();
				for (AhDashboardLayout layout : daLayouts) {
					layoutIds.add(layout.getId());
					if (layout.getDaWidgets() != null
							&& !layout.getDaWidgets().isEmpty()) {
						for (AhDashboardWidget widget : layout.getDaWidgets()) {
							widgetIds.add(widget.getId());
							if (widget.getWidgetConfig() != null) {
								componentIds.add(widget.getWidgetConfig().getId());
							}
						}
					}
				}
				if (!widgetIds.isEmpty()) {
					QueryUtil.bulkRemoveBos(AhDashboardWidget.class,
							new FilterParams("id", widgetIds), getDomain().getId());
				}

				if (componentIdsInUse != null
						&& !componentIdsInUse.isEmpty()) {
					componentIds.removeAll(componentIdsInUse);
				}

				/*if (!componentIds.isEmpty()) {
					for (Long idTmp : componentIds) {
						try {
							removeWidgetConfigById(idTmp);
						} catch (Exception e1) {
							e1.printStackTrace();
							log.error("Failed to remove dashboard component", e1);
						}
					}
				}*/
				QueryUtil.bulkRemoveBos(AhDashboardLayout.class, new FilterParams("id", layoutIds), getDomain().getId());
			}

			return layoutIds;
		} catch (Exception e) {
			log.error("Failed to remove dashboard layouts.", e);
		}

		return null;
	}
	private byte convertToSizeType(String type) {
		byte result = AhDashboardLayout.SIZE_MEDIUM;

		if ("small".equals(type)) {
			result = AhDashboardLayout.SIZE_SMALL;
		} else if ("large".equals(type)) {
			result = AhDashboardLayout.SIZE_LARGE;
		} else if ("custom".equals(type)) {
			result = AhDashboardLayout.SIZE_CUSTOM;
		}

		return result;
	}
	public JSONObject getWidgetConfigJSONObject(Long widgetIdArg) {
		if (widgetIdArg == null) {
			return new JSONObject();
		}
		List<DashboardComponent> widgets = QueryUtil.executeQuery(DashboardComponent.class,
				null,
				new FilterParams("id", widgetIdArg),
				getDomain().getId(),
				this);
		if (widgets == null
				|| widgets.isEmpty()
				|| widgets.get(0) == null) {
			return new JSONObject();
		}
		return widgets.get(0).getJSONObject();
	}

	public String getCurWidgetCustomTimeString(AhDashboardWidget daWidget) {
		if (daWidget == null
				|| daWidget.getSelectTimeType() != AhDashboard.TAB_TIME_CUSTOM) {
			return "";
		}
		StringBuilder bd = new StringBuilder();
		SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
		TimeZone tzTmp = getUserTimeZone();
		if (getDataSource() != null
				&& getDataSource().getTz() != null) {
			tzTmp = getDataSource().getTz();
		}
		l_sdf.setTimeZone(tzTmp);
		Calendar ca = Calendar.getInstance(tzTmp);
		ca.setTimeInMillis(daWidget.getCustomStartTime());
		bd.append(l_sdf.format(ca.getTime()));
		ca.setTimeInMillis(daWidget.getCustomEndTime());
		bd.append(" -- ");
		bd.append(l_sdf.format(ca.getTime()));
		return bd.toString();
	}

	public AhDashboard getCertainTemplateDa(AhDashboard daArg) {
		if (daArg == null
				|| !daArg.isMonitorKindOfDa()) {
			return null;
		}

		List<AhDashboard> lst = QueryUtil.executeQuery(AhDashboard.class, null,
				new FilterParams("daType=:s1 and defaultFlag=:s2",
						new Object[]{daArg.getDaType(), true}),getDomain().getId(), this);
		if (lst != null
				&& !lst.isEmpty()) {
			return lst.get(0);
		}

		return null;
	}

	public boolean cloneDashboardLayouts(AhDashboard daTemplateArg, AhDashboard descDaArg, boolean persist) {
		if (daTemplateArg == null
				|| daTemplateArg.getId() == null
				|| descDaArg == null
				|| descDaArg.getId() == null) {
			return false;
		}

		try {
			List<AhDashboardLayout> layouts = QueryUtil.executeQuery(AhDashboardLayout.class,
					null,
					new FilterParams("dashboard.id", daTemplateArg.getId()),
					getDomain().getId(),
					this);
			if (layouts != null
					&& !layouts.isEmpty()) {
				/*Set<Long> reportIds = new HashSet<>();
				for (AhDashboardLayout layout : layouts) {
					if (layout.getDaWidgets() != null
							&& !layout.getDaWidgets().isEmpty()) {
						for (AhDashboardWidget widget : layout.getDaWidgets()) {
							reportIds.add(widget.getReportId());
						}
					}
				}
				Map<Long, DashboardComponent> daConfigsMap = new HashMap<>();
				if (reportIds != null
						&& !reportIds.isEmpty()) {
					daConfigsMap = cloneDashboardConfigs(reportIds, false);
				}
				if (null == daConfigsMap) {
					daConfigsMap = new HashMap<>();
				}*/

				Map<Long, DashboardComponent> daConfigsMap = new HashMap<>();
				Set<AhDashboardLayout> layoutsTmp = new HashSet<>();
				AhDashboardLayout layoutTmp;
				AhDashboardWidget widgetTmp;
				Set<AhDashboardWidget> widgetsTmp;
				DashboardComponent configTmp;
				for (AhDashboardLayout layout : layouts) {
					layoutTmp = layout.clone();
					layoutTmp.setId(null);
					layoutTmp.setDashboard(descDaArg);
					layoutTmp.setTabId(String.valueOf(descDaArg.getId()));

					if (layoutTmp.getDaWidgets() != null
							&& !layoutTmp.getDaWidgets().isEmpty()) {
						widgetsTmp = new HashSet<AhDashboardWidget>();
						for (AhDashboardWidget widget : layoutTmp.getDaWidgets()) {
							widgetTmp = widget.clone();
							widgetTmp.setId(null);
							widgetTmp.setDaLayout(layoutTmp);
							configTmp = daConfigsMap.get(widgetTmp.getReportId());
							if (configTmp != null) {
								widgetTmp.setReportId(configTmp.getId());
								widgetTmp.setWidgetConfig(configTmp);
							}
							widgetsTmp.add(widgetTmp);
						}
						layoutTmp.setDaWidgets(widgetsTmp);
					}
					layoutsTmp.add(layoutTmp);
				}
				if(persist){
					QueryUtil.bulkCreateBos(layoutsTmp);
				}else{
					descDaArg.setDaLayouts(layoutsTmp);
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*-
	private Map<Long, DashboardComponent> cloneDashboardConfigs(Set<Long> reportIds, boolean blnDeepClone) throws Exception {
		return cloneDashboardConfigs(reportIds, blnDeepClone, null);
	}*/
	private Map<Long, DashboardComponent> cloneDashboardConfigs(Set<Long> reportIds, boolean blnDeepClone,
			DashboardComponentModifier modifier) throws Exception {
		Map<Long, DashboardComponent> result = new HashMap<>();

		List<DashboardComponentMetric> maCm = new ArrayList<>();

		List<DashboardComponent> lsDaCo = QueryUtil.executeQuery(DashboardComponent.class, null,
				new FilterParams("id",reportIds), getDomain().getId(), this);
		if (!lsDaCo.isEmpty()) {
			for(DashboardComponent dac : lsDaCo){
				DashboardComponent dc = dac.clone();
				dc.setId(null);
				dc.setKey(0);
				dc.setDefaultFlag(false);
				dc.setVersion(null);

				DashboardComponentMetric dm = null;
				/*if(dac.getComponentMetric()!=null &&
						(dac.getComponentMetric().getMetricName()==null ||
						dac.getComponentMetric().getMetricName().equals(""))) {
					dm = this.cloneDaComponentMetric(dac.getComponentMetric());
				} else */
				if (blnDeepClone) {
					if (dac.getComponentMetric() != null) {
						dm = this.cloneDaComponentMetric(dac.getComponentMetric());
						dc.setComponentType(DashboardComponent.COMPONENT_TYPE_COUSTM);
					}
					if (dm != null) {
						maCm.add(dm);
						dc.setComponentMetric(dm);
					}
				}

				if (modifier != null) {
					modifier.modify(dc);
				}

				result.put(dac.getId(), dc);
			}
			if(!maCm.isEmpty()) {
				QueryUtil.bulkCreateBos(maCm);
				List<Long> maNewIds = new ArrayList<>();
				for (DashboardComponentMetric dmTmp : maCm) {
					maNewIds.add(dmTmp.getId());
				}

				List<DashboardComponentMetric> dmsTmp =
						QueryUtil.executeQuery(
								DashboardComponentMetric.class,
								null,
								new FilterParams("id", maNewIds),
								getDomain().getId());

				if (dmsTmp != null
						&& !dmsTmp.isEmpty()) {
					for (DashboardComponentMetric dmTmp : dmsTmp) {
						for (DashboardComponent dc : result.values()) {
							if (dmTmp.getId().equals(dc.getComponentMetric().getId())) {
								dc.setComponentMetric(dmTmp);
							}
						}
					}
				}
			}

			if(!result.values().isEmpty()) {
				QueryUtil.bulkCreateBos(result.values());
				List<Long> dcNewIds = new ArrayList<>();
				for (DashboardComponent dc : result.values()) {
					dcNewIds.add(dc.getId());
				}
				List<DashboardComponent> dcsTmp =
						QueryUtil.executeQuery(
								DashboardComponent.class,
								null,
								new FilterParams("id", dcNewIds),
								getDomain().getId());
				if (dcsTmp != null
						&& !dcsTmp.isEmpty()) {
					for (DashboardComponent dcTmp : dcsTmp) {
						for (Long rKey : result.keySet()) {
							if (result.get(rKey).getId().equals(dcTmp.getId())) {
								result.put(rKey, dcTmp);
							}
						}
					}
				}
			}
		}
		return result;
	}
	private DashboardComponentMetric cloneDaComponentMetric(DashboardComponentMetric srcDm) {
		if (srcDm == null) {
			return null;
		}
		DashboardComponentMetric dm = srcDm.clone();
		dm.setId(null);
		dm.setDefaultFlag(false);
		dm.setKey(0);
		dm.setVersion(null);
		dm.setMetricName(null);

		List<DashboardComponentData> cloneData = new ArrayList<DashboardComponentData>();
		for (DashboardComponentData tempClass : srcDm.getComponentData()) {
			cloneData.add(tempClass);
		}
		dm.setComponentData(cloneData);

		return dm;
	}

	protected abstract class DashboardComponentModifier {
		private String chartType;
		private byte ddLevel;
		private boolean inverted;
		public abstract void modify(DashboardComponent dc);

		public void setChartType(String chartType) {
			this.chartType = chartType;
		}
		public String getChartType() {
			return chartType;
		}

		public byte getDdLevel() {
			return ddLevel;
		}

		public void setDdLevel(byte ddLevel) {
			this.ddLevel = ddLevel;
		}

		public boolean isInverted() {
			return inverted;
		}

		public void setInverted(boolean inverted) {
			this.inverted = inverted;
		}
	}

	protected abstract class DashboardWidgetModifier {
//		protected MapContainerNode location;
		protected Long ctime;
		public DashboardWidgetModifier() {
		}
		public DashboardWidgetModifier(Long ctime) {
			this.ctime = ctime;
		}
//		public DashboardWidgetModifier(MapContainerNode location) {
//			this.location = location;
//		}
//		public DashboardWidgetModifier(Long ctime, MapContainerNode location) {
//			this.ctime = ctime;
//			this.location = location;
//		}
		protected void prepareDrilldownTime(AhDashboard da, AhDashboardWidget widget) {
			if (ctime != null
					&& ctime.compareTo(0L) > 0
					&& widget != null) {
				int diffTime = ReportDataRequestUtil.getDaWidgetSample(da, widget, tz, "da");
				widget.setBlnChecked(true);
				widget.setSelectTimeType(AhDashboard.TAB_TIME_CUSTOM);
				widget.setCustomEndTime(ctime);
				widget.setCustomStartTime(ctime-diffTime*1000);
			}
		}
		protected void prepareDrilldownScope(AhDashboardWidget widget) {
			switch(bkType) {
				case BK_TYPE_DEVICE_TYPE:
					widget.setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ALL);
					break;
				case BK_TYPE_NETWORK_POLICY:
					widget.setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY);
					break;
				case BK_TYPE_TAG1:
					widget.setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE);
					break;
				case BK_TYPE_TAG2:
					widget.setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO);
					break;
				case BK_TYPE_TAG3:
					widget.setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE);
					break;
				case BK_TYPE_MAC:
					widget.setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SINGLE);
					break;
				default:
					widget.setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
					break;
			}
			if (bkType != BK_TYPE_TOPO) {
				widget.setObjectId(bkValue);
			}
		}
		public abstract void modify(AhDashboardWidget widget);
	}
	private static enum DrillDownWidgetType {
		TOPO_FULL_TIME, //break down topology with same time range
		PART_TIME, //no break down for topology, with selected time range
		TOPO_PART_TIME, //break down topology with selected time range
		COUNT_PART_TIME, //special drill down for count, list devices in selected time range
		COUNT_FULL_TIME //special drill down for count, list devices in same time range
	}
	private static final Map<Short, List<DrillDownWidgetType>> DRILLDOWN_WIDGET_TYPES = new HashMap<>();
	private static final short DRILL_DOWN_TYPE_1 = 1;
	private static final short DRILL_DOWN_TYPE_2 = 2;
	private static final short DRILL_DOWN_TYPE_3 = 3;
	private static final short DRILL_DOWN_TYPE_4 = 4;
	private static final short DRILL_DOWN_TYPE_5 = 5;
	private static final short DRILL_DOWN_TYPE_6 = 6;
	private static final short DRILL_DOWN_TYPE_7 = 7;
	static {
		DRILLDOWN_WIDGET_TYPES.put(DRILL_DOWN_TYPE_1, new ArrayList<DrillDownWidgetType>());
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_1).add(DrillDownWidgetType.TOPO_FULL_TIME);

		DRILLDOWN_WIDGET_TYPES.put(DRILL_DOWN_TYPE_2, new ArrayList<DrillDownWidgetType>());
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_2).add(DrillDownWidgetType.PART_TIME);

		DRILLDOWN_WIDGET_TYPES.put(DRILL_DOWN_TYPE_3, new ArrayList<DrillDownWidgetType>());
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_3).add(DrillDownWidgetType.TOPO_FULL_TIME);
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_3).add(DrillDownWidgetType.PART_TIME);
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_3).add(DrillDownWidgetType.TOPO_PART_TIME);

		DRILLDOWN_WIDGET_TYPES.put(DRILL_DOWN_TYPE_4, new ArrayList<DrillDownWidgetType>());
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_4).add(DrillDownWidgetType.COUNT_FULL_TIME);

		DRILLDOWN_WIDGET_TYPES.put(DRILL_DOWN_TYPE_5, new ArrayList<DrillDownWidgetType>());
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_5).add(DrillDownWidgetType.TOPO_FULL_TIME);
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_5).add(DrillDownWidgetType.COUNT_FULL_TIME);

		DRILLDOWN_WIDGET_TYPES.put(DRILL_DOWN_TYPE_6, new ArrayList<DrillDownWidgetType>());
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_6).add(DrillDownWidgetType.PART_TIME);
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_6).add(DrillDownWidgetType.COUNT_PART_TIME);
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_6).add(DrillDownWidgetType.COUNT_FULL_TIME);

		DRILLDOWN_WIDGET_TYPES.put(DRILL_DOWN_TYPE_7, new ArrayList<DrillDownWidgetType>());
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_7).add(DrillDownWidgetType.TOPO_FULL_TIME);
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_7).add(DrillDownWidgetType.PART_TIME);
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_7).add(DrillDownWidgetType.TOPO_PART_TIME);
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_7).add(DrillDownWidgetType.COUNT_PART_TIME);
		DRILLDOWN_WIDGET_TYPES.get(DRILL_DOWN_TYPE_7).add(DrillDownWidgetType.COUNT_FULL_TIME);
	}
	private boolean isNodeMacAddress(String nodeStr) {
		return StringUtils.isNotBlank(nodeStr)
				&& nodeStr.length() == 12;
	}

	public void createDrillDownWidgets(final AhDashboard daArg, Long widgetIdArg) throws Exception {
		/* args should be prepared
		 	1, short drilldownType;
		 	2, String drilldownNode;
		 	3, boolean blnDrilldownOvertime;
		*/
		AhDashboardWidget srcWidget = QueryUtil.findBoByAttribute(
				AhDashboardWidget.class, "id", widgetIdArg, this.getDomain().getId(), this);
		if (srcWidget == null) {
			return;
		}
		List<DrillDownWidgetType> ddWidgetTypes = DRILLDOWN_WIDGET_TYPES.get(this.drilldownType);
		if (ddWidgetTypes == null
				|| ddWidgetTypes.isEmpty()) {
			return;
		}

		DashboardComponentModifier dcModifier = new DashboardComponentModifier() {
			public void modify(DashboardComponent dc) {
				if (dc == null
						|| StringUtils.isBlank(ddMetricKey)) {
					return;
				}
				if(dc.getComponentMetric() != null) {
					List<DashboardComponentData> cloneData = new ArrayList<DashboardComponentData>();
					for (DashboardComponentData tempClass : dc.getComponentMetric().getComponentData()) {
						if (ddMetricKey.equals(tempClass.getSourceData())) {
							if (this.getDdLevel() != (byte)0) {
								tempClass.setEnableBreakdown(true);
								tempClass.setLevelBreakDown(this.getDdLevel());
								//tempClass.setEnableDisplayTotal(true);
							} else {
								tempClass.setEnableBreakdown(false);
								tempClass.setLevelBreakDown((byte)0);
								tempClass.setEnableDisplayTotal(false);
							}
							cloneData.add(tempClass);
							if (StringUtils.isBlank(ddNamePostfix)) {
								ddNamePostfix = tempClass.getDisplayName();
							}
						}
					}
					dc.getComponentMetric().setComponentData(cloneData);
//					if (StringUtils.isNotBlank(this.getChartType())) {
//						dc.setChartType(this.getChartType());
//					}
//					dc.setChartInverted(this.isInverted());
				}
			}
		};

		List<AhDashboardWidget> widgets = new ArrayList<>();
		AhDashboardWidget widgetTmp;
		Set<Long> reportIds = new HashSet<>();
		reportIds.add(srcWidget.getReportId());
		Map<Long, DashboardComponent> ctDcs;
		DashboardComponent dcTmp;
		DashboardWidgetModifier widgetModifier = null;
		// TODO begin
//		MapContainerNode locationTmp = srcWidget.getLocation();
//		if (StringUtils.isNotBlank(drilldownNode)
//				&& !isNodeMacAddress(drilldownNode)) {
//			Long nId;
//			try {
//				nId = Long.valueOf(this.drilldownNode);
//			} catch (Exception e) {
//				nId = 0L;
//			}
//			locationTmp = QueryUtil.findBoByAttribute(MapContainerNode.class, "id", nId, this.getDomain().getId());
//		}
		byte ddLevel;
		for (DrillDownWidgetType ddType : ddWidgetTypes) {
			ddLevel = 0;
			if (ddType == DrillDownWidgetType.TOPO_FULL_TIME) {
				if (!this.checkCanDrilldownOnScope()) {
					continue;
				}
				ddLevel = 1;
				//widgetModifier = new DashboardWidgetModifier(locationTmp) {
					widgetModifier = new DashboardWidgetModifier() {
					public void modify(AhDashboardWidget widget) {
//						widget.setLocation(this.location);
						this.prepareDrilldownScope(widget);
					}
				};
			} else if (ddType == DrillDownWidgetType.PART_TIME) {
				if (!this.checkCanDrilldownOnTime(daArg, srcWidget)) {
					continue;
				}
				widgetModifier = new DashboardWidgetModifier(this.drilldownTime) {
					public void modify(AhDashboardWidget widget) {
						this.prepareDrilldownTime(daArg, widget);
						this.prepareDrilldownScope(widget);
					}
				};
			} else if (ddType == DrillDownWidgetType.TOPO_PART_TIME) {
				final boolean blnCanDdOnTime = this.checkCanDrilldownOnTime(daArg, srcWidget);
				if (!blnCanDdOnTime) {
					continue;
				}
				ddLevel = 1;
				widgetModifier = new DashboardWidgetModifier(this.drilldownTime) {
//				widgetModifier = new DashboardWidgetModifier(this.drilldownTime, locationTmp) {
					public void modify(AhDashboardWidget widget) {
//						widget.setLocation(this.location);
						if (blnCanDdOnTime) {
							this.prepareDrilldownTime(daArg, widget);
						}
						this.prepareDrilldownScope(widget);
					}
				};
			} else if (ddType == DrillDownWidgetType.COUNT_PART_TIME) {
				if (!this.checkCanDrilldownOnTime(daArg, srcWidget)) {
					continue;
				}
				dcModifier.setChartType("table");
				dcModifier.setInverted(true);
				ddLevel = -1;
				widgetModifier = new DashboardWidgetModifier(this.drilldownTime) {
					public void modify(AhDashboardWidget widget) {
						widget.setBlnDdSpecialType(true);
						this.prepareDrilldownTime(daArg, widget);
						this.prepareDrilldownScope(widget);
					}
				};
			} else if (ddType == DrillDownWidgetType.COUNT_FULL_TIME) {
				dcModifier.setChartType("table");
				dcModifier.setInverted(true);
				ddLevel = -1;
				widgetModifier = new DashboardWidgetModifier() {
					public void modify(AhDashboardWidget widget) {
						widget.setBlnDdSpecialType(true);
						this.prepareDrilldownScope(widget);
					}
				};
			}

			dcModifier.setDdLevel(ddLevel);
			ctDcs = cloneDashboardConfigs(reportIds, true, dcModifier);
			dcTmp = null;
			if (ctDcs != null
					&& !ctDcs.isEmpty()) {
				for (Long key : ctDcs.keySet()) {
					dcTmp = ctDcs.get(key);
					break;
				}
			}
			widgetTmp = this.createDrillDownWidget(daArg, srcWidget, widgetModifier);
			widgetTmp.setWidgetConfig(dcTmp);

			if (StringUtils.isNotBlank(ddNamePostfix)) {
				ddNamePostfix = ddNamePostfix.replaceAll("<br>", " ");
				ddNamePostfix = ddNamePostfix.replaceAll("<br/>", " ");
			}

			if (widgetTmp != null) {
				if (StringUtils.isNotBlank(ddNamePostfix)) {
					if (StringUtils.isBlank(widgetTmp.getMainTitle())
							|| !widgetTmp.getMainTitle().contains(ddNamePostfix)) {
						widgetTmp.setMainTitle(widgetTmp.getMainTitle() + " - " + ddNamePostfix);
					}
				}
				widgets.add(widgetTmp);
			}
		}

		List<AhDashboardLayout> layouts = createDefDrillDownLayouts(daArg, widgets);
		if (layouts != null
				&& !layouts.isEmpty()) {
			try {
				QueryUtil.bulkCreateBos(layouts);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Failed to create drill down widgets", e);
			}
		}
	}
	private boolean checkCanDrilldownOnTime(AhDashboard daArg, AhDashboardWidget widget) {
		if (widget == null
				||!widget.isBlnChecked()) {
			if (daArg.getSelectTimeType() == AhDashboard.TAB_TIME_LASTHOUR) {
				return false;
			} else if (daArg.getSelectTimeType() == AhDashboard.TAB_TIME_CUSTOM
					&& daArg.getCustomEndTime() - daArg.getCustomStartTime() <= 3600000) {
				return false;
			}
			return true;
		} else if (widget != null && widget.isBlnChecked()) {
			if (widget.getSelectTimeType() == AhDashboard.TAB_TIME_LASTHOUR) {
				return false;
			} else if (widget.getSelectTimeType() == AhDashboard.TAB_TIME_CUSTOM
					&& widget.getCustomEndTime() - widget.getCustomStartTime() <= 3600000) {
				return false;
			}
			return true;
		}

		return false;
	}
	private boolean checkCanDrilldownOnScope() {
		if (this.bkType == BK_TYPE_MAC) {
			return false;
		}
		return true;
	}
	private void mergeConfigsFromDaForWidget(AhDashboard daArg, AhDashboardWidget widget) {
		if (daArg == null
				|| widget == null) {
			return;
		}
		widget.setSelectTimeType(daArg.getSelectTimeType());
		widget.setCustomStartTime(daArg.getCustomStartTime());
		widget.setCustomEndTime(daArg.getCustomEndTime());
		widget.setObjectId(daArg.getObjectId());
		widget.setObjectType(daArg.getObjectType());
//		widget.setLocation(daArg.getLocation());
		widget.setFilterObjectId(daArg.getFilterObjectId());
		widget.setFilterObjectType(daArg.getFilterObjectType());
	}
	private AhDashboardWidget createDrillDownWidget(AhDashboard daArg, AhDashboardWidget srcWidget, DashboardWidgetModifier modifier) {
		if (srcWidget == null) {
			return null;
		}
		AhDashboardWidget result = srcWidget.clone();
		result.setId(null);

		if (!result.isBlnChecked()
				&& daArg != null) {
			mergeConfigsFromDaForWidget(daArg, result);
			result.setBlnChecked(true);
		}

		if (modifier != null) {
			modifier.modify(result);
		}

		return result;
	}

	public List<AhDashboardLayout> createDefDrillDownLayouts(AhDashboard daArg, List<AhDashboardWidget> widgetsArg) {
		if (daArg == null) {
			return null;
		}

		List<AhDashboardLayout> result = new ArrayList<>();
		result.add(createDefLayoutWithOrder(daArg, (byte)0));
		result.add(createDefLayoutWithOrder(daArg, (byte)1));

		int layoutsLen = result.size();
		int curPos = 0;
		AhDashboardLayout layoutTmp;
		for (AhDashboardWidget widget : widgetsArg) {
			layoutTmp = result.get(curPos%layoutsLen);
			widget.setItemOrder(layoutTmp.getDaWidgets().size());
			widget.setDaLayout(layoutTmp);
			widget.setOwner(layoutTmp.getOwner());
			layoutTmp.getDaWidgets().add(widget);
			curPos++;
		}

		return result;
	}
	private AhDashboardLayout createDefLayoutWithOrder(AhDashboard daArg, byte orderArg) {
		AhDashboardLayout result = new AhDashboardLayout();

		result.setItemOrder(orderArg);
		result.setDashboard(daArg);
		result.setSizeType(AhDashboardLayout.SIZE_LARGE);
		result.setOwner(daArg.getOwner());

		return result;
	}

	public boolean isWidgetChecked() {
		return widgetChecked;
	}

	public void setWidgetChecked(boolean widgetChecked) {
		this.widgetChecked = widgetChecked;
	}

	public String getCurDealTabId() {
		return curDealTabId;
	}

	public void setCurDealTabId(String curDealTabId) {
		this.curDealTabId = curDealTabId;
	}

	public Long getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(Long widgetId) {
		this.widgetId = widgetId;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public Long getPreWidgetId() {
		return preWidgetId;
	}

	public void setPreWidgetId(Long preWidgetId) {
		this.preWidgetId = preWidgetId;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public Long getPreMetricId() {
		return preMetricId;
	}

	public void setPreMetricId(Long preMetricId) {
		this.preMetricId = preMetricId;
	}

	public Map<Integer, List<WidgetConfig>> getWidgetConfigs() {
		return widgetConfigs;
	}

	public void setWidgetConfigs(Map<Integer, List<WidgetConfig>> widgetConfigs) {
		this.widgetConfigs = widgetConfigs;
	}

	public boolean isBlnCloneWidget() {
		return blnCloneWidget;
	}

	public void setBlnCloneWidget(boolean blnCloneWidget) {
		this.blnCloneWidget = blnCloneWidget;
	}

	public boolean isBgRollup() {
		return bgRollup;
	}

	public void setBgRollup(boolean bgRollup) {
		this.bgRollup = bgRollup;
	}

	public boolean isBgRollup2() {
		return bgRollup2;
	}

	public void setBgRollup2(boolean bgRollup2) {
		this.bgRollup2 = bgRollup2;
	}

	public String getCloneTabId() {
		return cloneTabId;
	}

	public void setCloneTabId(String cloneTabId) {
		this.cloneTabId = cloneTabId;
	}

//	public String getWidgetLocationId() {
//		return widgetLocationId;
//	}
//
//	public void setWidgetLocationId(String widgetLocationId) {
//		this.widgetLocationId = widgetLocationId;
//	}

	public String getDafaultTabId() {
		return dafaultTabId;
	}

	public void setDafaultTabId(String dafaultTabId) {
		this.dafaultTabId = dafaultTabId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getSpecifyName() {
		return specifyName;
	}

	public void setSpecifyName(String specifyName) {
		this.specifyName = specifyName;
	}

	public int getSpecifyType() {
		return specifyType;
	}

	public void setSpecifyType(int specifyType) {
		this.specifyType = specifyType;
	}

	public int getMonitorType() {
		return monitorType;
	}

	public void setMonitorType(int monitorType) {
		this.monitorType = monitorType;
	}

	public Long getBoardId() {
		return boardId;
	}

	public void setBoardId(Long boardId) {
		this.boardId = boardId;
	}

	public String getMonitorEl() {
		return monitorEl;
	}

	public void setMonitorEl(String monitorEl) {
		this.monitorEl = monitorEl;
	}

	public boolean isEnableTimeLocal() {
		return enableTimeLocal;
	}

	public void setEnableTimeLocal(boolean enableTimeLocal) {
		this.enableTimeLocal = enableTimeLocal;
	}

	public String getDeviceMonitorType() {
		return deviceMonitorType;
	}

	public void setDeviceMonitorType(String deviceMonitorType) {
		this.deviceMonitorType = deviceMonitorType;
	}

	public Long getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(Long monitorId) {
		this.monitorId = monitorId;
	}

	public String getDisplayValueKey() {
		return displayValueKey;
	}

	public void setDisplayValueKey(String displayValueKey) {
		this.displayValueKey = displayValueKey;
	}

	public Long getWidgetConfigId() {
		return widgetConfigId;
	}

	public void setWidgetConfigId(Long widgetConfigId) {
		this.widgetConfigId = widgetConfigId;
	}

	/**
	 * for drill down fields
	 */
	private short drilldownType;
	private String drilldownNode;
	private boolean blnDrilldownOvertime;
	private String ddMetricKey;
	private Long drilldownTime;
	private String ddNamePostfix = "";

	public short getDrilldownType() {
		return drilldownType;
	}

	public void setDrilldownType(short drilldownType) {
		this.drilldownType = drilldownType;
	}

	public String getDrilldownNode() {
		return drilldownNode;
	}

	public void setDrilldownNode(String drilldownNode) {
		this.drilldownNode = drilldownNode;
	}

	public boolean isBlnDrilldownOvertime() {
		return blnDrilldownOvertime;
	}

	public void setBlnDrilldownOvertime(boolean blnDrilldownOvertime) {
		this.blnDrilldownOvertime = blnDrilldownOvertime;
	}

	public String getDdMetricKey() {
		return ddMetricKey;
	}

	public void setDdMetricKey(String ddMetricKey) {
		this.ddMetricKey = ddMetricKey;
	}

	public Long getDrilldownTime() {
		return drilldownTime;
	}

	public void setDrilldownTime(Long drilldownTime) {
		this.drilldownTime = drilldownTime;
	}


	public JSONArray getSecondFilterList() {
		return secondFilterList;
	}

	public String getFilterObjectType() {
		return filterObjectType;
	}

	public void setFilterObjectType(String filterObjectType) {
		this.filterObjectType = filterObjectType;
	}

	public String getFilterObjectId() {
		return filterObjectId;
	}

	public void setFilterObjectId(String filterObjectId) {
		this.filterObjectId = filterObjectId;
	}

	//for download section start
	private String localFileName;
	private InputStream inputStream;
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public InputStream getErrorInputStream() {
		return new ByteArrayInputStream("Errors occurred while generating exported chart.".getBytes());
	}

	public String getLocalFileName() {
		return localFileName;
	}

	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}
	//for download section end

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	private static final short BK_TYPE_TOPO = 1;
	private static final short BK_TYPE_DEVICE_TYPE = 2;
	private static final short BK_TYPE_NETWORK_POLICY = 3;
	private static final short BK_TYPE_TAG1 = 4;
	private static final short BK_TYPE_TAG2 = 5;
	private static final short BK_TYPE_TAG3 = 6;
	private static final short BK_TYPE_MAC = 7;
	private short bkType;
	private String bkValue;
	private boolean blnSwitch;

	public String getBkValue() {
		return bkValue;
	}

	public void setBkValue(String bkValue) {
		this.bkValue = bkValue;
	}

	public boolean isBlnSwitch() {
		return blnSwitch;
	}

	public void setBlnSwitch(boolean blnSwitch) {
		this.blnSwitch = blnSwitch;
	}

	public short getBkType() {
		return bkType;
	}

	public void setBkType(short bkType) {
		this.bkType = bkType;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportDescription() {
		return reportDescription;
	}

	public void setReportDescription(String reportDescription) {
		this.reportDescription = reportDescription;
	}

	public String getReportHeader() {
		return reportHeader;
	}

	public void setReportHeader(String reportHeader) {
		this.reportHeader = reportHeader;
	}

	public String getReportFooter() {
		return reportFooter;
	}

	public void setReportFooter(String reportFooter) {
		this.reportFooter = reportFooter;
	}

	public String getReportSummary() {
		return reportSummary;
	}

	public void setReportSummary(String reportSummary) {
		this.reportSummary = reportSummary;
	}

	public int getRefrequency() {
		return refrequency;
	}

	public void setRefrequency(int refrequency) {
		this.refrequency = refrequency;
	}

	public boolean isReCustomDay() {
		return reCustomDay;
	}

	public void setReCustomDay(boolean reCustomDay) {
		this.reCustomDay = reCustomDay;
	}

	public boolean isReCustomTime() {
		return reCustomTime;
	}

	public void setReCustomTime(boolean reCustomTime) {
		this.reCustomTime = reCustomTime;
	}

	public String getReCustomDayValue() {
		return reCustomDayValue;
	}

	public void setReCustomDayValue(String reCustomDayValue) {
		this.reCustomDayValue = reCustomDayValue;
	}

	public int getReCustomTimeStart() {
		return reCustomTimeStart;
	}

	public void setReCustomTimeStart(int reCustomTimeStart) {
		this.reCustomTimeStart = reCustomTimeStart;
	}

	public int getReCustomTimeEnd() {
		return reCustomTimeEnd;
	}

	public void setReCustomTimeEnd(int reCustomTimeEnd) {
		this.reCustomTimeEnd = reCustomTimeEnd;
	}

	public String getReEmailAddress() {
		return reEmailAddress;
	}

	public void setReEmailAddress(String reEmailAddress) {
		this.reEmailAddress = reEmailAddress;
	}

	public int getReWeekStart() {
		return reWeekStart;
	}

	public void setReWeekStart(int reWeekStart) {
		this.reWeekStart = reWeekStart;
	}

	public int getReCmTimeType() {
		return reCmTimeType;
	}

	public void setReCmTimeType(int reCmTimeType) {
		this.reCmTimeType = reCmTimeType;
	}

	public int getReCmTimePeriod() {
		return reCmTimePeriod;
	}

	public void setReCmTimePeriod(int reCmTimePeriod) {
		this.reCmTimePeriod = reCmTimePeriod;
	}

	public int getReCmTimeStartDayType() {
		return reCmTimeStartDayType;
	}

	public void setReCmTimeStartDayType(int reCmTimeStartDayType) {
		this.reCmTimeStartDayType = reCmTimeStartDayType;
	}

	public int getReCmTimeStartDayValue() {
		return reCmTimeStartDayValue;
	}

	public void setReCmTimeStartDayValue(int reCmTimeStartDayValue) {
		this.reCmTimeStartDayValue = reCmTimeStartDayValue;
	}

	public int getReCmTimeStartMontyYear() {
		return reCmTimeStartMontyYear;
	}

	public void setReCmTimeStartMontyYear(int reCmTimeStartMontyYear) {
		this.reCmTimeStartMontyYear = reCmTimeStartMontyYear;
	}

	public int getReCmTimeStartSepcYear() {
		return reCmTimeStartSepcYear;
	}

	public void setReCmTimeStartSepcYear(int reCmTimeStartSepcYear) {
		this.reCmTimeStartSepcYear = reCmTimeStartSepcYear;
	}

	public boolean isReEnabledScheduleCheckbox() {
		return reEnabledScheduleCheckbox;
	}

	public void setReEnabledScheduleCheckbox(boolean reEnabledScheduleCheckbox) {
		this.reEnabledScheduleCheckbox = reEnabledScheduleCheckbox;
	}

	public String getDdNamePostfix() {
		return ddNamePostfix;
	}

	public void setDdNamePostfix(String ddNamePostfix) {
		this.ddNamePostfix = ddNamePostfix;
	}

	public int getComponentGroupType() {
		return componentGroupType;
	}

	public void setComponentGroupType(int componentGroupType) {
		this.componentGroupType = componentGroupType;
	}

	public String getWidgetName() {
		return widgetName;
	}

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}
	
	protected void setTopoSelectionInfoToWidget(AhDashboardWidget daWidget) {
		daWidget.setObjectType(this.getTreeType());
		daWidget.setObjectId(this.getTreeId());
		daWidget.setFilterObjectType(this.getFilterObjectType());
		daWidget.setFilterObjectId(this.getFilterObjectId());
	}

	protected void setPeriodSelectionInfoToWidget(AhDashboardWidget daWidget) {
		daWidget.setSelectTimeType(this.getTimeType());
		if(daWidget.getSelectTimeType() == AhDashboard.TAB_TIME_CUSTOM) {
			daWidget.setCustomStartTime(this.getConvertCustomStartTime());
			daWidget.setCustomEndTime(this.getConvertCustomEndTime());
		} else {
			daWidget.setCustomStartTime(0L);
			daWidget.setCustomEndTime(0L);
		}
		daWidget.setEnableTimeLocal(this.isEnableTimeLocal());
	}
	
	protected void cloneConfigsFromDashboardToWidget(AhDashboard da, AhDashboardWidget widget) {
		if (da == null
				|| widget == null) {
			return;
		}
		widget.setObjectId(da.getObjectId());
		widget.setObjectType(da.getObjectType());
		widget.setFilterObjectId(da.getFilterObjectId());
		widget.setFilterObjectType(da.getFilterObjectType());
		
		widget.setSelectTimeType(da.getSelectTimeType());
		widget.setCustomStartTime(da.getCustomStartTime());
		widget.setCustomEndTime(da.getCustomEndTime());
	}
	
}