package com.ah.ui.actions.monitor;

/*
 * @author Fisher
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.AhAppContainer;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.dashboard.AhDashBoardGroup;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.dashboard.AhDashboardAppAp;
import com.ah.bo.dashboard.AhDashboardLayout;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.dashboard.DashboardComponentMetric;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.performance.AhClientSession;
import com.ah.ui.actions.monitor.NewReportExporting.ReportDataHolder;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.UserSettingsUtil;
import com.ah.util.bo.dashboard.DaExportPdfUtil.ExportPdfFilePathResponse;
import com.ah.util.bo.dashboard.DaExportPdfUtil.ExportPdfStreamResponse;
import com.ah.util.bo.dashboard.DaHelper;

public class DashboardAction extends DashboardAbstractAction {

	private static final Tracer log = new Tracer(DashboardAction.class.getSimpleName());

	private static final long serialVersionUID = 1L;
	
	public static int SUPPORT_APPLICATION_OTHER_WARN=Integer.valueOf(MgrUtil.getResourceString("hm.dashboard.config.apptab.othertime.warnNum"));
	public static int SUPPORT_APPLICATION_OTHER_ERROR=Integer.valueOf(MgrUtil.getResourceString("hm.dashboard.config.apptab.othertime.errorNum"));
	public static int SUPPORT_APPLICATION_CALENDAR_WARN=Integer.valueOf(MgrUtil.getResourceString("hm.dashboard.config.apptab.lastcaletime.warnNum"));
	public static int SUPPORT_APPLICATION_CALENDAR_ERROR=Integer.valueOf(MgrUtil.getResourceString("hm.dashboard.config.apptab.lastcaletime.errorNum"));
	public static int SUPPORT_APPLICATION_ONEUSER_SESSION_APS=Integer.valueOf(MgrUtil.getResourceString("hm.dashboard.config.apptab.customtime.oneSession.user.apNum"));
	
	public static int DA_TAB_POSITION_APPLICATION=3;
	
	public static int[] DA_TAB_APPLICATION=new int[]{81,67,53,54};

	@Override
	public String execute() throws Exception {
		tz = getUserTimeZone();

		try {
			if ("removeTab".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					long tabRemoveId = Long.parseLong(getTabRemoveId());
					boolean isApp = isApplicationPerspective(tabRemoveId);
					if (isApp) {// application perspective cannot be removed
						jsonObject.put("t", false);
						jsonObject.put("m", MgrUtil.getUserMessage("info.da.application.remove.deny"));
					} else {
						if (tabRemoveId > 0) {
							removeDaLayouts(getTabRemoveId(), null);
							QueryUtil.removeBo(AhDashboard.class, tabRemoveId);
							clearAppAp(tabRemoveId);
						}
						monitorPageSessionManager(false, getTabRemoveId(), null);

						jsonObject.put("rid", getTabRemoveId());
						jsonObject.put("m", MgrUtil.getUserMessage(
								"error.da.remove.success",
								new String[] { "perspective" }));
						jsonObject.put("t", true);
					}
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("removeTabActive".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					long tabRemoveId = Long.parseLong(getTabRemoveId());
					boolean isApp = isApplicationPerspective(tabRemoveId);
					if (isApp) {// application perspective cannot be removed
						jsonObject.put("t", false);
						jsonObject.put("m", MgrUtil.getUserMessage("info.da.application.remove.deny"));
					} else {
						if (tabRemoveId > 0) {
							removeDaLayouts(getTabRemoveId(), null);
							QueryUtil.removeBo(AhDashboard.class, tabRemoveId);
							clearAppAp(tabRemoveId);
						}
						monitorPageSessionManager(false, getTabRemoveId(), null);

						AhDashboard da;
						List<AhDashboard> lst = QueryUtil
								.executeQuery(
										AhDashboard.class,
										null,
										new FilterParams(
												"daType=:s1 and userName=:s2 and active=:s3",
												new Object[] {
														AhDashboard.DASHBOARD_TYPE_DASH,
														getUserContext()
																.getUserName(),
														true }), getDomain()
												.getId());
						if (lst.isEmpty()) {
							lst = QueryUtil
									.executeQuery(
											AhDashboard.class,
											null,
											new FilterParams(
													"defaultFlag=:s1 and daType=:s2 and userName=:s3",
													new Object[] {
															true,
															AhDashboard.DASHBOARD_TYPE_DASH,
															getUserContext()
																	.getUserName() }),
											getDomain().getId());
							lst.get(0).setActive(true);
							da = QueryUtil.updateBo(lst.get(0));
						} else {
							da = lst.get(0);
						}
						setSessionDataSource(QueryUtil.findBoById(
								AhDashboard.class, da.getId(), this));
						getDataSource().setTz(tz);
						jsonObject.put("op", true);
						jsonObject.put("rid", getTabRemoveId());
						prepareJsonGUIData();
						jsonObject.put("m", MgrUtil.getUserMessage(
								"error.da.remove.success",
								new String[] { "perspective" }));
					}
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("changeTabName".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					if (getTabName().contains("\"")) {
						jsonObject.put("t", false);
						jsonObject.put("n", "Perspective name cannot contain char (\").");
						return "json";
					}
					if (getTabName().contains(">")) {
						jsonObject.put("t", false);
						jsonObject.put("n", "Perspective name cannot contain char (>).");
						return "json";
					}
					if (getTabName().contains("<")) {
						jsonObject.put("t", false);
						jsonObject.put("n", "Perspective name cannot contain char (<).");
						return "json";
					}
					if (getTabName().contains("&")) {
						jsonObject.put("t", false);
						jsonObject.put("n", "Perspective name cannot contain char (&).");
						return "json";
					}
					List<?> nameList = QueryUtil.executeQuery(AhDashboard.class, null,
							new FilterParams("daType=:s1 and userName=:s2 and dashName=:s3 and id!=:s4",
									new Object[]{AhDashboard.DASHBOARD_TYPE_DASH, getUserContext().getUserName(),
									getTabName(),Long.parseLong(getTabChangeId())}),
									getDomain().getId());
					if (!nameList.isEmpty()) {
						jsonObject.put("t", false);
						jsonObject.put("n", "Perspective name (" + getTabName() + " ) already exist. ");
						return "json";
					}

					AhDashboard da = QueryUtil.findBoById(AhDashboard.class, Long.parseLong(getTabChangeId()));
					if(da.getDaType()==AhDashboard.DASHBOARD_TYPE_DASH) {
						da.setDashName(getTabName());
						da = QueryUtil.updateBo(da);
					}
					setSessionDataSource(QueryUtil.findBoById(AhDashboard.class, da.getId(), this));
					getDataSource().setTz(tz);
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("checkTabSize".equals(operation)){
				jsonObject = new JSONObject();
				try {
					long tabCount = QueryUtil.findRowCount(AhDashboard.class,
							new FilterParams("daType=:s1 and userName=:s2 and owner.id=:s3",
									new Object[]{AhDashboard.DASHBOARD_TYPE_DASH,getUserContext().getUserName(),
									getDomain().getId()}));
					if (tabCount>=6) {
						jsonObject.put("t", false);
					} else {
						jsonObject.put("t", true);
					}
				} catch (Exception e) {
					jsonObject.put("t", false);
				}
				return "json";
			} else if ("changeTab".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					AhDashboard da = QueryUtil.findBoById(AhDashboard.class, Long.parseLong(getTabChangeId()));

					if(da.getDaType()==AhDashboard.DASHBOARD_TYPE_DASH) {
						clearActiveFlgForBo();
						da = QueryUtil.findBoById(AhDashboard.class, Long.parseLong(getTabChangeId()));
						String[] retArray = checkToChangeAppTimeSelectPeriod(da, getDomain().getId());
						if (retArray[0].equals("true")) {
							da.setSelectTimeType(AhDashboard.TAB_TIME_LASTCALEDAY);
						}
						if (!retArray[1].isEmpty()) {
							jsonObject.put("dailog", retArray[1]);
						}

						da.setActive(true);
						da = QueryUtil.updateBo(da);
					}
					setSessionDataSource(QueryUtil.findBoById(AhDashboard.class, da.getId(), this));
					if(getDataSource().isMonitorKindOfDa()) {
						jsonObject.put("dId", getTabChangeId());
						jsonObject.put("drill", true);
						jsonObject.put("daType", getDataSource().getDaType());
						jsonObject.put("t", true);
					} else {
						prepareJsonGUIData();
						
						setHasHighIntervalApFlg(false);
						String ret =getAllHiveApByApp(getDataSource().getId());
						if(!ret.equals("")) {
							jsonObject.put("dailog", ret);
						}
						if(isHasHighIntervalApFlg()){
							jsonObject.put("timeo", true);
							jsonObject.put("name", getDataSource().getDashName());
						}
					}
					
					getDataSource().setTz(tz);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if("newTab".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					AhDashboard da;
					if (getCloneTabId()!=null) {
						da=getDataSource().clone();
						if (da == null) {
							da = new AhDashboard();
						}
					} else {
						da = new AhDashboard();
					}
					da.setActive(true);
					da.setPosition(0);
					da.setOwner(getDomain());
					da.setUserName(getUserContext().getUserName());
					da.setDaType(AhDashboard.DASHBOARD_TYPE_DASH);
					List<String> nameList=new ArrayList<String>();
					for(int i=0; i<6; i++) {
						if (i==0) {
							nameList.add("My Perspective");
						} else {
							nameList.add("My Perspective" + i);
						}
					}
					List<?> nameExList = QueryUtil.executeQuery("select dashName from " + AhDashboard.class.getSimpleName(), null,
							new FilterParams("daType=:s1 and userName=:s2 and dashName in(:s3)",
									new Object[]{AhDashboard.DASHBOARD_TYPE_DASH, getUserContext().getUserName(),
									nameList}),
									getDomain().getId());
					if (nameExList.isEmpty()) {
					da.setDashName(getTabName());
					} else {
						for (Object str: nameExList){
							nameList.remove(str.toString());
						}
						da.setDashName(nameList.isEmpty()?"My Perspective6" :nameList.get(0));
					}
					da.setDefaultFlag(false);
					da.setBgRollup(isBgRollup2());
					da.setId(null);

					clearActiveFlgForBo();

					Long createId = QueryUtil.createBo(da);
					da= QueryUtil.findBoById(AhDashboard.class, createId, this);
					setSessionDataSource(da);
					getDataSource().setTz(tz);
					jsonObject.put("name", getDataSource().getDashName());
					prepareJsonGUIData();

					Map<Long, ComponentConfigs> savedLayouts = saveDaLayouts(this.getDataSource());
					if (savedLayouts != null
							&& !savedLayouts.isEmpty()) {
						JSONObject layouts = new JSONObject();
						jsonObject.put("d", layouts);
						for (Long key : savedLayouts.keySet()) {
							JSONObject jTmp = new JSONObject();
							jTmp.put("xk", savedLayouts.get(key).getxAxisKey());
							jTmp.put("id", savedLayouts.get(key).getId());
							jTmp.put("config", savedLayouts.get(key).getConfigObj());
							layouts.put(String.valueOf(key), jTmp);
						}
						//add another information for dashboard, just hold here for previous implementation
						JSONObject jOpt = new JSONObject();
						layouts.put("additionalInfo", jOpt);
						jOpt.put("appInterval", DaHelper.getApplicationReportIntervalMinute()*60000);
					}
					setHasHighIntervalApFlg(false);
					String ret =getAllHiveApByApp(getDataSource().getId());
					if(!ret.equals("")) {
						jsonObject.put("dailog", ret);
					}
					if(isHasHighIntervalApFlg()){
						jsonObject.put("timeo", true);
					}
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if("newMonitorTab".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					List<AhDashboard> lstActive = QueryUtil.executeQuery(AhDashboard.class, null,
							new FilterParams("daType=:s1 and active=:s2 and userName=:s3",
									new Object[]{AhDashboard.DASHBOARD_TYPE_DASH, true, getUserContext().getUserName()}),getDomain().getId());
					AhDashboard daAc = null;
					if(!lstActive.isEmpty()) {
						daAc = lstActive.get(0);
					}
					if (daAc == null) {
						jsonObject.put("t", false);
						jsonObject.put("m", "Invalid dashboard.");
						return "json";
					}
					final AhDashboardWidget oriWidget = QueryUtil.findBoByAttribute(AhDashboardWidget.class, "id", this.getWidgetId(), getDomain().getId(), this);
					if (oriWidget == null) {
						jsonObject.put("t", false);
						jsonObject.put("m", "Invalid widget.");
						return "json";
					}
					
					if (this.getMonitorType() != AhDashboard.DASHBOARD_TYPE_APP
							&& this.getMonitorType() != AhDashboard.DASHBOARD_TYPE_USER
							&& this.getMonitorType() != AhDashboard.DASHBOARD_TYPE_CLIENT
							&& this.getMonitorType() != AhDashboard.DASHBOARD_TYPE_DEVICE
							&& this.getMonitorType() != AhDashboard.DASHBOARD_TYPE_PORT
							&& this.getMonitorType() != AhDashboard.DASHBOARD_TYPE_APPCLIENT) {
						jsonObject.put("t", false);
						jsonObject.put("m", "Monitor type is not supported now.");
						return "json";
					}
					final int mtype = this.getMonitorType();
					final String ddValue = this.getDrilldownNode();
					daAc.setOwner(getDomain());
					daAc.setUserName(getUserContext().getUserName());
					daAc.setDashName(this.getTabName());
					daAc.setActive(true);
					daAc.setDefaultFlag(false);
					daAc.setPosition(0);
					daAc.setDaType(mtype);
					AhDashboard newDa = createDefaultMonitorDashBoard(daAc, this.widgetConfigId, this.getDdMetricKey(),
							new DashboardWidgetModifier() {
								@Override
								public void modify(AhDashboardWidget widget) {
									if (oriWidget.isBlnChecked()) {
										oriWidget.cloneConfigsToNew(widget);
									} 
									if (mtype == AhDashboard.DASHBOARD_TYPE_APP) {
										widget.setSpecifyType(DashboardComponent.WIDGET_SPECIFY_TYPE_APP);
									} else if (mtype == AhDashboard.DASHBOARD_TYPE_USER) {
										widget.setSpecifyType(DashboardComponent.WIDGET_SPECIFY_TYPE_USER);
									} else if (mtype == AhDashboard.DASHBOARD_TYPE_CLIENT) {
										widget.setSpecifyType(DashboardComponent.WIDGET_SPECIFY_TYPE_CLIENT);
									} else if (mtype == AhDashboard.DASHBOARD_TYPE_DEVICE) {
										widget.setSpecifyType(DashboardComponent.WIDGET_SPECIFY_TYPE_DEVICE);
									} else if (mtype == AhDashboard.DASHBOARD_TYPE_PORT) {
										widget.setSpecifyType(DashboardComponent.WIDGET_SPECIFY_TYPE_PORT);
									} else if (mtype == AhDashboard.DASHBOARD_TYPE_APPCLIENT) {
										widget.setSpecifyType(DashboardComponent.WIDGET_SPECIFY_TYPE_APPCLIENT);
									}
									widget.setSpecifyName(ddValue);
								}
							});
					if (newDa == null) {
						jsonObject.put("t", false);
						jsonObject.put("m", "Failed to create monitor tab.");
						return "json";
					}
					
					setSessionDataSource(newDa);
					getDataSource().setTz(tz);
					getDataSource().setActive(true);
					jsonObject.put("name", newDa.getDashName());
					jsonObject.put("dId", newDa.getId());
					monitorPageSessionManager(true, newDa.getId().toString(), newDa.getDashName());
					prepareJsonGUIData();
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if("newDrillTab".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					AhDashboard da = new AhDashboard();
					da.setOwner(getDomain());
					da.setUserName("");
					da.setDaType(getMonitorType());
					da.setDashName(getTabName());
					da.setActive(true);
					da.setDefaultFlag(false);
					da.setPosition(0);

					List<AhDashboard> lstActive = QueryUtil.executeQuery(AhDashboard.class, null,
							new FilterParams("daType=:s1 and active=:s2",
									new Object[]{AhDashboard.DASHBOARD_TYPE_DASH, true}),getDomain().getId());
					if(!lstActive.isEmpty()) {
						AhDashboard daAc = lstActive.get(0);
//						da.setLocation(daAc.getLocation());
						da.setObjectType(daAc.getObjectType());
						da.setObjectId(daAc.getObjectId());
						da.setFilterObjectType(daAc.getFilterObjectType());
						da.setFilterObjectId(daAc.getFilterObjectId());

						da.setSelectTimeType(daAc.getSelectTimeType());
						da.setCustomStartTime(daAc.getCustomStartTime());
						da.setCustomEndTime(daAc.getCustomEndTime());
						da.setEnableTimeLocal(daAc.isEnableTimeLocal());
					}

					Long nid = QueryUtil.createBo(da);
					da.setId(nid);
					createDrillDownWidgets(da, getWidgetId());
					setSessionDataSource(QueryUtil.findBoById(AhDashboard.class, nid, this));
					getDataSource().setTz(tz);
					jsonObject.put("name", getTabName());
					jsonObject.put("dId", nid);
					monitorPageSessionManager(true, nid.toString(), getTabName());
					jsonObject.put("daType", getDataSource().getDaType());
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
//			} else if ("changeTopyTree".equals(operation)){
//				jsonObject = new JSONObject();
//				try {
//					MapContainerNode node = QueryUtil.findBoById(MapContainerNode.class, Long.parseLong(getTreeId()));
//					AhDashboard da= QueryUtil.findBoById(AhDashboard.class, getDataSource().getId());
//					da.setLocation(node);
//					da.setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
//					da.setObjectId(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
//					da = QueryUtil.updateBo(da);
//					setSessionDataSource(QueryUtil.findBoById(AhDashboard.class, da.getId(), this));
//					getDataSource().setTz(tz);
//					initFilterGroup();
//					String v = getFilterList().toString();
//					String v3 = getSecondFilterList().toString();
//					jsonObject.put("t", true);
//					jsonObject.put("v", v);
//					jsonObject.put("v3", v3);
//				} catch (Exception e) {
//					jsonObject.put("t", false);
//					jsonObject.put("m", e.getMessage());
//				}
//				return "json";
//			} else if ("changeWidgetTopyTree".equals(operation)){
//				jsonObject = new JSONObject();
//				try {
//					initFilterGroup();
//					String v = getFilterList().toString();
//					String v3 =  getSecondFilterList().toString();
//					jsonObject.put("t", true);
//					jsonObject.put("v", v);
//					jsonObject.put("v3", v3);
//				} catch (Exception e) {
//					jsonObject.put("t", false);
//					jsonObject.put("m", e.getMessage());
//				}
//				return "json";
			} else if ("changeWidgetFilterTree".equals(operation)){
				jsonObject = new JSONObject();
				try {
					StringBuilder sql = fetchFilterMapSql();
					fetchSsidList(sql);
					if (!isEasyMode()) {
						fetchUserProfileList(sql);
					}
					String v3 = getSecondFilterList().toString();
					jsonObject.put("t", true);
					jsonObject.put("v3", v3);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if("saveWidgetGroupPanel".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					AhDashboardWidget wd = QueryUtil.findBoById(AhDashboardWidget.class, getWidgetId(), this);
//					MapContainerNode node = QueryUtil.findBoById(MapContainerNode.class, Long.parseLong(getWidgetLocationId()));
//					wd.setLocation(node);
					wd.setObjectType(getTreeType());
					wd.setObjectId(getTreeId());
					wd.setFilterObjectType(getFilterObjectType());
					wd.setFilterObjectId(getFilterObjectId());
					QueryUtil.updateBo(wd);
					jsonObject.put("v", wd.getWidgetConfig().getId());
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("changeFilterTree".equals(operation)){
				jsonObject = new JSONObject();
				try {
					AhDashboard da = QueryUtil.findBoById(AhDashboard.class, getDataSource().getId());
					da.setObjectType(getTreeType());
					da.setObjectId(getTreeId());
					da.setFilterObjectType(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
					da.setFilterObjectId(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
					da = QueryUtil.updateBo(da);
					setSessionDataSource(QueryUtil.findBoById(AhDashboard.class, da.getId(), this));
					getDataSource().setTz(tz);
					StringBuilder sql = fetchFilterMapSql();
					fetchSsidList(sql);
					if (!isEasyMode()) {
						fetchUserProfileList(sql);
					}
					String v3 = getSecondFilterList().toString();
					jsonObject.put("v3", v3);
					
					setHasHighIntervalApFlg(false);
					String ret =getAllHiveApByApp(getDataSource().getId());
					if(!ret.equals("")) {
						jsonObject.put("dailog", ret);
					}
					if(isHasHighIntervalApFlg()){
						jsonObject.put("timeo", true);
						jsonObject.put("name", getDataSource().getDashName());
						jsonObject.put("dId", getDataSource().getId());	
					} else {
						jsonObject.put("dId", getDataSource().getId());	
					}
					
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("changeFilterUpTree".equals(operation)){
				jsonObject = new JSONObject();
				try {
					AhDashboard da = QueryUtil.findBoById(AhDashboard.class, getDataSource().getId());
					da.setFilterObjectType(getTreeType());
					da.setFilterObjectId(getTreeId());
					da = QueryUtil.updateBo(da);
					setSessionDataSource(QueryUtil.findBoById(AhDashboard.class, da.getId(), this));
					getDataSource().setTz(tz);
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("fetchDetailDevice".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					StringBuilder serSql = new StringBuilder();
					serSql.append("select distinct hostName,macAddress from hive_ap where owner=")
					.append(getDomain().getId())
					.append(" and managestatus=").append(HiveAp.STATUS_MANAGED);
					Short[] modelType = fetchParamFilter(getTreeId());
					if(modelType!=null) {
						serSql.append(" and hiveApModel=").append(modelType[0]);
						serSql.append(" and devicetype=").append(modelType[1]);
					}
					String[] params = getSecondFilterParam();
					if (params != null
							&& params.length > 0
							&& params[0]!=null) {
						serSql.append(" and map_container_id in (").append(params[0]).append(")");
					}
					if (params != null
							&& params.length > 1
							&& params[1]!=null) {
//						String obTy= params[1];
						String obId = params[2];
						if(getFilterObjectType().endsWith(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH)){
							serSql.append(" and macAddress!='").append(getFilterObjectId()).append("'");
						}
						serSql.append(" order by hostName");
						if(getFilterObjectType().endsWith(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH)
								&& getFilterObjectType().equals(obId + AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH)){
							setOffset(getOffset()-1);
							if (getOffset()<0) {setOffset(0);}
						}
					}
					List<?> deviceList = QueryUtil.executeNativeQuery(serSql.toString(), 10, getOffset());
					if (!deviceList.isEmpty()) {
						JSONArray list = new JSONArray();
						for(Object obj: deviceList){
							Object[] oneObj = (Object[]) obj;
							JSONObject jsObj = new JSONObject();
							jsObj.put("label", oneObj[0].toString());
							jsObj.put("title", oneObj[0].toString());
							jsObj.put("id", oneObj[1].toString());
							jsObj.put("tp", getTreeId() + AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH);
							jsObj.put("expanded", false);
							list.put(jsObj);
						}
						jsonObject.put("v", list);
					}
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("checkCustomTimeRange".equals(operation)) {
				jsonObject = new JSONObject();
				long startTimeck = getConvertCustomStartTime();
				long endTimeck = getConvertCustomEndTime();
				LogSettings sets = MgrUtil.fetchHomeDomainLogSettings();
				long hourlyTime = sets.getMaxHourValue() * 24L * 3600000L;
				long dailyTime = sets.getMaxDayValue() * 7 * 24L * 3600000L;
				long weeklyTime = sets.getMaxWeekValue() * 30 * 24L * 3600000L;
				
				Calendar ca = Calendar.getInstance();
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				long currentTime =ca.getTimeInMillis();
				
				if (endTimeck-startTimeck<=7200000) {
					// error need more than 2 hour
					jsonObject.put("e", MgrUtil.getUserMessage("error.da.customtime.morethan2Hours"));
				} else if (endTimeck-startTimeck<=3600000L * 24 *2){
					if (endTimeck<currentTime-hourlyTime) {
						jsonObject.put("e", MgrUtil.getUserMessage("error.da.customtime.invalid", new String[]{sets.getMaxHourValue() + " days"}));
						// error no data
					} else if (startTimeck<currentTime-hourlyTime){
						// warning not complete data
						jsonObject.put("w", MgrUtil.getUserMessage("error.da.customtime.incomplete", new String[]{sets.getMaxHourValue() + " days"}));
					}
				} else if (endTimeck-startTimeck<=3600000L * 24 * 35){
					if (endTimeck<currentTime-dailyTime) {
						jsonObject.put("e", MgrUtil.getUserMessage("error.da.customtime.invalid", new String[]{sets.getMaxDayValue() + " weeks"}));
						// error no data
					} else if (startTimeck<currentTime-dailyTime){
						// warning not complete data
						jsonObject.put("w", MgrUtil.getUserMessage("error.da.customtime.incomplete", new String[]{sets.getMaxDayValue() + " weeks"}));
					}
				} else {
//				} else if (endTimeck-startTimeck<=3600000L * 24 * 30 * 12) {
					if (endTimeck<currentTime-weeklyTime) {
						jsonObject.put("e", MgrUtil.getUserMessage("error.da.customtime.invalid", new String[]{sets.getMaxWeekValue() + " months"}));
						// error no data
					} else if (startTimeck<currentTime-weeklyTime){
						// warning not complete data
						jsonObject.put("w", MgrUtil.getUserMessage("error.da.customtime.incomplete", new String[]{sets.getMaxWeekValue() + " months"}));
					}
				}
//				} else {
//					if (endTimeck-startTimeck<=730 * 24 * 3600000L) {
//						if (endTimeck<currentTime-730 * 24 * 3600000L) {
//							jsonObject.put("e", MgrUtil.getUserMessage("error.da.customtime.invalid", new String[]{"2 years"}));
//							// error no data
//						} else if (startTimeck<currentTime-730 * 24 * 3600000L){
//							// warning not complete data
//							jsonObject.put("w", MgrUtil.getUserMessage("error.da.customtime.incomplete", new String[]{"2 years"}));
//						}
//					} else {
//						if (startTimeck<currentTime-730 * 24 * 3600000L){
//							// warning not complete data
//							jsonObject.put("w", MgrUtil.getUserMessage("error.da.customtime.incomplete", new String[]{"2 years"}));
//						} else {
//							jsonObject.put("e", MgrUtil.getUserMessage("error.da.customtime.invalid", new String[]{"2 years"}));
//							// error no data
//						}
//					}
//				}
				jsonObject.put("t", true);
				return "json";
			} else if ("changeCustomTimeType".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					AhDashboard da = QueryUtil.findBoById(AhDashboard.class, getDataSource().getId());
					da.setSelectTimeType(getTimeType());
					if(getTimeType()==AhDashboard.TAB_TIME_CUSTOM) {
						da.setCustomStartTime(getConvertCustomStartTime());
						da.setCustomEndTime(getConvertCustomEndTime());
						da.setEnableTimeLocal(isEnableTimeLocal());
					} else {
						da.setCustomStartTime(0);
						da.setCustomEndTime(0);
						da.setEnableTimeLocal(false);
					}
					da = QueryUtil.updateBo(da);
					setSessionDataSource(QueryUtil.findBoById(AhDashboard.class, da.getId(), this));
					getDataSource().setTz(tz);

					if(getTimeType()==AhDashboard.TAB_TIME_CUSTOM || 
						getTimeType() == AhDashboard.TAB_TIME_LASTCALEWEEK ||
						getTimeType() == AhDashboard.TAB_TIME_LASTDAY || 
						getTimeType() == AhDashboard.TAB_TIME_LASTWEEK) {
						jsonObject.put("v", getDataSource().getCurrentDashCustomTimeString());
					}
					
					setHasHighIntervalApFlg(false);
					String ret =getAllHiveApByApp(getDataSource().getId());
					if(!ret.equals("")) {
						jsonObject.put("dailog", ret);
					}
					if(isHasHighIntervalApFlg()){
						jsonObject.put("timeo", true);
						jsonObject.put("name", getDataSource().getDashName());
						jsonObject.put("dId", getDataSource().getId());	
					} else {
						jsonObject.put("dId", getDataSource().getId());	
					}
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("fetchCustomTime".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					if(getDataSource().getSelectTimeType()==AhDashboard.TAB_TIME_CUSTOM){
						Calendar calendar = Calendar.getInstance(tz);
						calendar.setTimeInMillis(getDataSource().getCustomStartTime());
						SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd");
						l_sdf.setTimeZone(tz);
						jsonObject.put("s", l_sdf.format(calendar.getTime()));
						jsonObject.put("sh", calendar.get(Calendar.HOUR_OF_DAY));
						calendar.setTimeInMillis(getDataSource().getCustomEndTime());
						jsonObject.put("e", l_sdf.format(calendar.getTime()));
						jsonObject.put("eh", calendar.get(Calendar.HOUR_OF_DAY));
						jsonObject.put("c", getDataSource().isEnableTimeLocal());
						jsonObject.put("t", true);
					} else {
						jsonObject.put("t", false);
					}
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("initWidgetCopyDiv".equals(operation)){
				jsonObject = new JSONObject();
				long mmV=-1;
				int tp;
				try {
					if(null==getWidgetId() || getWidgetId()<0) {
						tp = DashboardComponent.COMPONENT_GROUP_AEROHIVEDEVICE;
						jsonObject.put("tp", DashboardComponent.COMPONENT_GROUP_AEROHIVEDEVICE);
					} else {
						DashboardComponent dc = QueryUtil.findBoById(DashboardComponent.class, getWidgetId(), this);
						tp=dc.getComponentGroup();
						mmV=dc.getComponentMetric().getId();
						jsonObject.put("tp", dc.getComponentGroup());
						jsonObject.put("mm_v", dc.getComponentMetric().getId());
						jsonObject.put("mm_n", dc.getComponentName());
					}
					List<JSONObject> jsA = fetchMetricSelectInfoList(tp,DashboardComponent.WIDGET_SPECIFY_TYPE_NONE);
					if (mmV<0) {
						jsonObject.put("mm_v", jsA.get(0).get("v"));
						if (jsA.get(0).get("v").toString().equals("-1")){
							jsonObject.put("mm_n", "");
						} else {
							jsonObject.put("mm_n", jsA.get(0).get("k"));
						}
					}
					jsonObject.put("mm", new JSONArray(jsA));
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("changeComponentGroup".equals(operation)){
				jsonObject = new JSONObject();
				try {
					List<JSONObject> jsA = fetchMetricSelectInfoList(getComponentGroupType(),DashboardComponent.WIDGET_SPECIFY_TYPE_NONE);
					jsonObject.put("mm", new JSONArray(jsA));
					jsonObject.put("mm_v", jsA.get(0).get("v"));
					if (jsA.get(0).get("v").toString().equals("-1")){
						jsonObject.put("mm_n", "");
					} else {
						jsonObject.put("mm_n", jsA.get(0).get("k"));
					}
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
			return "json";
//			} else if ("changeSpecifyType".equals(operation)){
//				jsonObject = new JSONObject();
//				String maV="-1";
//				try {
//					jsonObject.put("wi", new JSONArray(fetchWidgetSelectInfoList(getSpecifyType())));
//					jsonObject.put("mm", new JSONArray(fetchMetricSelectInfoList(getSpecifyType(),maV)));
//					jsonObject.put("t", true);
//				} catch (Exception e) {
//					jsonObject.put("t", false);
//					jsonObject.put("m", e.getMessage());
//				}
//				return "json";
//			} else if ("changeMetricAxis".equals(operation)) {
//				jsonObject = new JSONObject();
//				try {
//					jsonObject.put("mm", new JSONArray(fetchMetricSelectInfoList(getSpecifyType(),getSourceType())));
//					jsonObject.put("t", true);
//				} catch (Exception e) {
//					jsonObject.put("t", false);
//					jsonObject.put("m", e.getMessage());
//				}
//				return "json";
			} else if ("saveWidgetCopyDiv".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					DashboardComponent dc  = QueryUtil.findBoByAttribute(DashboardComponent.class,
							"componentName", getWidgetName(), getDomain().getId());
					if (dc!=null) {
						jsonObject.put("t", false);
						jsonObject.put("m", MgrUtil.getUserMessage("error.objectExists", getWidgetName()));
						return "json";
					}
					if(null==getWidgetId()) {
						dc = new DashboardComponent();
						dc.setOwner(getDomain());
					} else {
						dc = findBoById(DashboardComponent.class, getWidgetId(), this);
					}

					dc.setComponentGroup(getComponentGroupType());
					dc.setComponentName(getWidgetName());
					dc.setComponentType(DashboardComponent.COMPONENT_TYPE_METRIC);
					dc.setComponentMetric(findBoById(DashboardComponentMetric.class, preMetricId));
					dc.setSourceType(dc.getComponentMetric().getSourceType());
					dc.setHomeonly(dc.getComponentMetric().isHomeonly());
					dc.setSpecifyType(DashboardComponent.WIDGET_SPECIFY_TYPE_NONE);

					Long createId;
					if(dc.getId()!=null) {
						createId = QueryUtil.updateBo(dc).getId();
					} else {
						createId = QueryUtil.createBo(dc);
					}
					jsonObject.put("t", true);
					jsonObject.put("v", createId);
					jsonObject.put("dcc", getDashbordComponent(QueryUtil.findBoById(DashboardComponent.class, createId)));
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if("removeMetricAtCopyDiv".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					String ret = removeMetricById(getPreMetricId());
					if(ret.equals("")) {
						jsonObject.put("t", true);
						jsonObject.put("m", MgrUtil.getUserMessage("error.da.remove.success", new String[]{"metric"}));
					} else {
						jsonObject.put("t", false);
						jsonObject.put("m", ret);
					}
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("removeWidgetAtCopyDiv".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					String ret = removeWidgetConfigById(getPreWidgetId());
					if(ret.equals("")) {
						jsonObject.put("t", true);
						jsonObject.put("m", MgrUtil.getUserMessage("error.da.remove.success", new String[]{"widget"}));
					} else {
						jsonObject.put("t", false);
						jsonObject.put("m", ret);
					}
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if("dashboardSetting".equals(operation)) {
				jsonArray = new JSONArray();
				JSONObject jObjInfo = new JSONObject();
				jsonArray.put(jObjInfo);

				JSONObject jResultInfo = new JSONObject();
				jObjInfo.put("resultInfo", jResultInfo);
				jResultInfo.put("resultStatus", true);

				JSONObject jDaSettingsInfo = new JSONObject();
				jObjInfo.put("daSetting", jDaSettingsInfo);
				
				JSONObject jDaCustomInfo = new JSONObject();
				jDaCustomInfo.put("appInterval", DaHelper.getApplicationReportIntervalMinute()*60000);
				jDaCustomInfo.put("timeType", this.getDataSource().getSelectTimeType());
				jDaCustomInfo.put("blnAppPerspective", this.getDataSource().isApplicationPerspective());
				jObjInfo.put("customInfo", jDaCustomInfo);

				JSONObject jDaReportsInfo = new JSONObject();
				jObjInfo.put("daReports", jDaReportsInfo);

				TimeZone tzTmp = this.tz;
				Map<Byte, List<AhDashboardWidget>> widgets = getCurrentDaWidgets();
				if (widgets != null
						&& !widgets.isEmpty()) {
					for (Byte key : widgets.keySet()) {
						JSONArray colArrayTmp = new JSONArray();
						jDaReportsInfo.put("order"+key, colArrayTmp);
						List<AhDashboardWidget> widgetLst = widgets.get(key);
						if (widgetLst != null
								&& !widgetLst.isEmpty()) {
							for (AhDashboardWidget daWidgetTmp : widgetLst) {
								colArrayTmp.put(daWidgetTmp.getJSONObject(tzTmp, this.getDataSource()));
							}
						}
					}
				}

				return "json";
			} else if("saveLayout".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", true);

//				if (getDataSource() != null) {
//					getDataSource().setBgRollup(isBgRollup());
//				}
				Map<Long, ComponentConfigs> savedLayouts = saveDaLayouts(this.getDataSource());

				if (this.getDataSource().isMonitorKindOfDa()) {
					saveDaLayouts(getCertainTemplateDa(this.getDataSource()), false);
				}

				if (savedLayouts != null
						&& !savedLayouts.isEmpty()) {
					JSONObject layouts = new JSONObject();
					jsonObject.put("d", layouts);
					for (Long key : savedLayouts.keySet()) {
						JSONObject jTmp = new JSONObject();
						jTmp.put("xk", savedLayouts.get(key).getxAxisKey());
						jTmp.put("id", savedLayouts.get(key).getId());
						jTmp.put("config", savedLayouts.get(key).getConfigObj());
						layouts.put(String.valueOf(key), jTmp);
					}
					//add another information for dashboard, just hold here for previous implementation
					JSONObject jOpt = new JSONObject();
					layouts.put("additionalInfo", jOpt);
					jOpt.put("appInterval", DaHelper.getApplicationReportIntervalMinute()*60000);
				}
				setHasHighIntervalApFlg(false);
				String ret =getAllHiveApByApp(getDataSource().getId());
				if(!ret.equals("")) {
					jsonObject.put("dailog", ret);
				}
				if(isHasHighIntervalApFlg()){
					jsonObject.put("timeo", true);
					jsonObject.put("name", getDataSource().getDashName());
					jsonObject.put("dId", getDataSource().getId());
				} else {
					jsonObject.put("dId", getDataSource().getId());	
				}
				return "json";
			} else if ("changeAppApHighInterval".equals(operation)){
				// true mean update the record time
				try {
					if(isRemoveHighIntervalFlg()) {
						QueryUtil.updateBos(AhDashboardAppAp.class, "timestamp=:s1", "dashId=:s2",
								new Object[]{System.currentTimeMillis(),Long.parseLong(getRemoveHighIntervalId())});
					} else {
						// remove all AP for this dash
						clearAppAp(Long.parseLong(getRemoveHighIntervalId()));
					}
				} catch (Exception e) {
					log.error(e);
				}
				return "json";
			} else if("getConfig4Widget".equals(operation)) {
				jsonObject = this.getWidgetConfigJSONObject(getWidgetId());
				return "json";
			} else if ("getHtmlContent".equals(operation)) {
				jsonArray = new JSONArray();
				JSONObject objHtml = new JSONObject();
				jsonArray.put(objHtml);
				objHtml.put("resultStatus", true);
				DashboardComponent daComponent = QueryUtil.findBoByAttribute(DashboardComponent.class, "id", getWidgetConfigId(), getDomain().getId(), this);
				if (daComponent != null
						&& daComponent.getComponentMetric() != null) {
					objHtml.put("htmlText", daComponent.getComponentMetric().getCustomHtml());
				}

				return "json";
			} else if ("topoSelect".equals(operation)) {
				jsonObject = new JSONObject();
				boolean blnWidgetChecked = false;
				try {
					if (getWidgetId() != null
							&& getWidgetId() > 0) {
						AhDashboardWidget wd = QueryUtil.findBoById(AhDashboardWidget.class, getWidgetId());
						if(wd==null) {
							jsonObject.put("t", false);
							jsonObject.put("m", "The widget is not exist.");
							return "json";
						}
						if (wd.isBlnChecked()) {
							blnWidgetChecked = true;
							jsonObject.put("obType", wd.getObjectType());
							jsonObject.put("obId", wd.getObjectId());
							jsonObject.put("fobType", wd.getFilterObjectType());
							jsonObject.put("fobId", wd.getFilterObjectId());
						}
					}
					
					if (!blnWidgetChecked) {
						if (this.getDataSource() != null) {
							jsonObject.put("obType", this.getDataSource().getObjectType());
							jsonObject.put("obId", this.getDataSource().getObjectId());
							jsonObject.put("fobType", this.getDataSource().getFilterObjectType());
							jsonObject.put("fobId", this.getDataSource().getFilterObjectId());
						} else {
							jsonObject.put("obType", AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
							jsonObject.put("obId", String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
							jsonObject.put("fobType", String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
							jsonObject.put("fobId", String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
						}
					}
					initGroup();
					String v = getFilterList().toString();
					jsonObject.put("v", v);
					String v3 = getSecondFilterList().toString();
					jsonObject.put("v3", v3);
					//jsonObject.put("wId", widgetId);
					jsonObject.put("t", true);
				} catch (Exception e) {
					e.printStackTrace();
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if("saveTopoSelect".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("rs", true);
				if (getWidgetId() != null
						&& getWidgetId() > 0) {
					AhDashboardWidget wd = QueryUtil.findBoById(AhDashboardWidget.class, getWidgetId());
					if(wd==null) {
						jsonObject.put("rs", false);
						jsonObject.put("m", "The widget is not exist.");
						return "json";
					}
					this.setTopoSelectionInfoToWidget(wd);
					QueryUtil.updateBo(wd);
				}
				return "json";
			} else if ("periodSelect".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", true);
				//jsonObject.put("wId", widgetId);

				boolean blnWidgetChecked = false;
				if (getWidgetId() != null) {
					AhDashboardWidget daWidget = QueryUtil.findBoByAttribute(AhDashboardWidget.class, "id", getWidgetId(), getDomain().getId());
					if (daWidget != null) {
						setTimeType(daWidget.getSelectTimeType());
						if (daWidget.isBlnChecked()) {
							blnWidgetChecked = true;
							if(getTimeType()==AhDashboard.TAB_TIME_CUSTOM) {
								jsonObject.put("custstr", getCurWidgetCustomTimeString(daWidget));
	
								JSONObject cusTime = new JSONObject();
								jsonObject.put("ct", cusTime);
								Calendar calendar = Calendar.getInstance(tz);
								calendar.setTimeInMillis(daWidget.getCustomStartTime());
								SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd");
								l_sdf.setTimeZone(tz);
								cusTime.put("s", l_sdf.format(calendar.getTime()));
								cusTime.put("sh", calendar.get(Calendar.HOUR_OF_DAY));
								calendar.setTimeInMillis(daWidget.getCustomEndTime());
								cusTime.put("e", l_sdf.format(calendar.getTime()));
								cusTime.put("eh", calendar.get(Calendar.HOUR_OF_DAY));
								cusTime.put("c", daWidget.isEnableTimeLocal());
							}
						}
					}
				}
				
				if (!blnWidgetChecked 
						&& this.getDataSource() != null) {
					setTimeType(this.getDataSource().getSelectTimeType());
					if(getTimeType()==AhDashboard.TAB_TIME_CUSTOM) {
						jsonObject.put("custstr", this.getDataSource().getCurrentDashCustomTimeString());

						JSONObject cusTime = new JSONObject();
						jsonObject.put("ct", cusTime);
						Calendar calendar = Calendar.getInstance(tz);
						calendar.setTimeInMillis(this.getDataSource().getCustomStartTime());
						SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd");
						l_sdf.setTimeZone(tz);
						cusTime.put("s", l_sdf.format(calendar.getTime()));
						cusTime.put("sh", calendar.get(Calendar.HOUR_OF_DAY));
						calendar.setTimeInMillis(this.getDataSource().getCustomEndTime());
						cusTime.put("e", l_sdf.format(calendar.getTime()));
						cusTime.put("eh", calendar.get(Calendar.HOUR_OF_DAY));
						cusTime.put("c", this.getDataSource().isEnableTimeLocal());
					}
				}
				jsonObject.put("tt", getTimeType());
				
				return "json";
			} else if ("dontShowMessageAgain".equals(operation)) {
				jsonObject = new JSONObject();
				try {
/*					if (getUserContext()!=null && getUserContext().getId()!=null) {
						HmUser aUser = QueryUtil.findBoById(HmUser.class, getUserContext().getId());
						if (aUser!=null) {
							aUser.setDontShowMessageInDashboard(true);
							QueryUtil.updateBo(aUser);
							getUserContext().setDontShowMessageInDashboard(true);
							setSessionUserContext(getUserContext());
						}
					}*/
					// changed in Geneva, for user setting columns separated from hm_user
					UserSettingsUtil.updateDontShowMessageInDashboard(getUserContext().getEmailAddress(), true);
					getUserContext().setDontShowMessageInDashboard(true);
					setSessionUserContext(getUserContext());
					
				} catch (Exception e) {
					log.error(e);
				}
				return "json";
			} else if ("saveTimePeriodChart".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", true);

				if (getWidgetId() != null) {
					AhDashboardWidget daWidget = QueryUtil.findBoByAttribute(AhDashboardWidget.class, "id", getWidgetId(), getDomain().getId());
					if (daWidget != null) {
						this.setPeriodSelectionInfoToWidget(daWidget);
						try {
							QueryUtil.updateBo(daWidget);
						} catch (Exception e1) {
							jsonObject.put("resultStatus", false);
							log.error("Failed to save time period for widget.", e1);
						}
					}
				}
				return "json";
			} else if ("checkWidget".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", true);

				if (getWidgetId() != null) {
					AhDashboardWidget daWidget = QueryUtil.findBoByAttribute(AhDashboardWidget.class, "id", getWidgetId(), getDomain().getId());
					if (daWidget != null) {
						daWidget.setBlnChecked(isWidgetChecked());
						this.cloneConfigsFromDashboardToWidget(this.getDataSource(), daWidget);

						try {
							QueryUtil.updateBo(daWidget);
						} catch (Exception e1) {
							jsonObject.put("resultStatus", false);
							log.error("Failed to save check status for widget.", e1);
						}
					}
				}

				return "json";
			} else if ("checkDeviceMonitor".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("rs", false);
				if ("client".equals(getDeviceMonitorType())) {
					List<AhClientSession> clients = DBOperationUtil.executeQuery(AhClientSession.class,
							null,
							new FilterParams("clientMac", getMonitorEl()),
							this.getDomain().getId());
					if (clients != null
							&& !clients.isEmpty()) {
						jsonObject.put("rs", true);
						jsonObject.put("id", clients.get(0).getId());
					}
				} else {
					List<HiveAp> devices = QueryUtil.executeQuery(HiveAp.class,
							null,
							new FilterParams("macAddress", getMonitorEl()),
							this.getDomain().getId());
					if (devices != null
							&& !devices.isEmpty()) {
						jsonObject.put("rs", true);
						jsonObject.put("id", devices.get(0).getId());
					}
				}
				return "json";
			}else if("saveAsReport".equals(operation)){
				log.info("save as report: " + reportName);
				jsonObject = new JSONObject();

				try {
					AhDashboard da = getDataSource();
					AhDashboard report = da.clone();

					report.setActive(false);
					report.setOwner(getDomain());
					report.setUserName("");
					report.setDaType(AhDashboard.DASHBOARD_TYPE_REPORT);
					report.setDefaultFlag(false);
					report.setBgRollup(false);
					report.setId(null);
					report.setPosition(0);

					String where = "dashName = :s1 and daType = :s2";
					Object[] values = new Object[] { this.reportName,
							AhDashboard.DASHBOARD_TYPE_REPORT };
					List<?> boIds = QueryUtil.executeQuery("select id from "
							+ AhDashboard.class.getSimpleName(), null,
							new FilterParams(where, values), domainId);
					if (!boIds.isEmpty()) {
						jsonObject.put("m", MgrUtil.getUserMessage(
								"error.objectExists", this.reportName));
					} else {
						report.setDashName(this.reportName);
						report.setDescription(this.reportDescription);
						report.setPdfHeader(this.reportHeader);
						report.setPdfFooter(this.reportFooter);
						report.setPdfSummary(this.reportSummary);
						fillReportScheduleSettings(report);
						report.setReportScheduleStatus(AhDashboard.REPORT_STATUS_SCHEDULED);
						Long id = QueryUtil.createBo(report);
						jsonObject.put("id", id);
						jsonObject.put("t", this.reportName);
						report.setId(id);
						if (!cloneDashboardLayouts(da, report, true)) {
							jsonObject.put("m", "clone widgets for report error.");
						}
					}
				} catch (Exception e) {
					jsonObject.put("m", "save as report error.");
					log.error("save as report error", e);
				}
				return "json";
			} else if ("checkApNumberForApp".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					int currentTimeType = getDataSource().getSelectTimeType();
					String currentObId = getDataSource().getObjectId();
					String currentObTp = getDataSource().getObjectType();
					if (timeType!=0) {
						currentTimeType = timeType;
					} else {
						currentObId = treeId;
						currentObTp = treeType;
					}

					if (getDataSource().getPosition() == DA_TAB_POSITION_APPLICATION && 
							(currentTimeType==AhDashboard.TAB_TIME_LAST8HOUR
							|| currentTimeType==AhDashboard.TAB_TIME_LASTDAY
							|| currentTimeType==AhDashboard.TAB_TIME_LASTWEEK 
							|| currentTimeType==AhDashboard.TAB_TIME_LASTCALEDAY
							|| currentTimeType==AhDashboard.TAB_TIME_LASTCALEWEEK
							|| currentTimeType==AhDashboard.TAB_TIME_CUSTOM)) {
						int currentApNum = calculateAppDevicesNumber(currentObTp,currentObId, getDomain().getId());
						if ((currentTimeType==AhDashboard.TAB_TIME_LASTCALEWEEK ||currentTimeType==AhDashboard.TAB_TIME_LASTCALEDAY)
								&& currentApNum >SUPPORT_APPLICATION_CALENDAR_WARN && currentApNum <=SUPPORT_APPLICATION_CALENDAR_ERROR) {
							jsonObject.put("w", MgrUtil.getUserMessage("warn.da.app.time.support.maxap", new String[]{String.valueOf(SUPPORT_APPLICATION_CALENDAR_WARN)}));
							jsonObject.put("t", true);
						} else if ((currentTimeType!=AhDashboard.TAB_TIME_LASTCALEWEEK && currentTimeType!=AhDashboard.TAB_TIME_LASTCALEDAY)
								&& currentApNum >SUPPORT_APPLICATION_OTHER_WARN && currentApNum <=SUPPORT_APPLICATION_OTHER_ERROR) {
							jsonObject.put("w", MgrUtil.getUserMessage("warn.da.app.time.support.maxap", new String[]{String.valueOf(SUPPORT_APPLICATION_OTHER_WARN)}));
							jsonObject.put("t", true);
						} else if ((currentTimeType==AhDashboard.TAB_TIME_LASTCALEWEEK ||currentTimeType==AhDashboard.TAB_TIME_LASTCALEDAY)
								&& currentApNum >SUPPORT_APPLICATION_CALENDAR_ERROR) {
							jsonObject.put("m", MgrUtil.getUserMessage("error.da.app.time.support.maxap", new String[]{String.valueOf(SUPPORT_APPLICATION_CALENDAR_ERROR)}));
							jsonObject.put("t", false);
						} else if ((currentTimeType!=AhDashboard.TAB_TIME_LASTCALEWEEK && currentTimeType!=AhDashboard.TAB_TIME_LASTCALEDAY)
								&&  currentApNum >SUPPORT_APPLICATION_OTHER_ERROR) {
							jsonObject.put("m", MgrUtil.getUserMessage("error.da.app.time.support.maxap", new String[]{String.valueOf(SUPPORT_APPLICATION_OTHER_ERROR)}));
							jsonObject.put("t", false);
						} else {
							jsonObject.put("t", true);
						}
					} else if (getDataSource().getPosition() == DA_TAB_POSITION_APPLICATION && 
								currentTimeType==AhDashboard.TAB_TIME_LASTHOUR) {
						int currentApNum = calculateAppDevicesNumber(currentObTp,currentObId, getDomain().getId());
						LogSettings homeLogSetting = MgrUtil.fetchHomeDomainLogSettings();
						int vhmCheckCount = homeLogSetting.getReportMaxApCount();
						if (currentApNum>vhmCheckCount) {
							jsonObject.put("m", MgrUtil.getUserMessage("error.da.support.ap.vhm", new String[]{String.valueOf(vhmCheckCount)}));
							jsonObject.put("t", false);
						} else {
							jsonObject.put("t", true);
						}
					} else {
						jsonObject.put("t", true);
					}
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", "Change Time Error.");
				}
				return "json";
				
			} else if ("deviceMonitor".equals(operation)) {
				if ("client".equals(getDeviceMonitorType())) {
					List<AhClientSession> clients = DBOperationUtil.executeQuery(AhClientSession.class,
							null,
							new FilterParams("id", Long.valueOf(getMonitorEl())),
							getDomain().getId());
					if (clients != null
							&& !clients.isEmpty()) {
						setMonitorId(clients.get(0).getId());
						return "clientDetail";
					}

					return "clientList";
				} else {
					List<HiveAp> devices = QueryUtil.executeQuery(HiveAp.class,
							null,
							new FilterParams("id", Long.valueOf(getMonitorEl())),
							getDomain().getId());
					if (devices != null
							&& !devices.isEmpty()) {
						setMonitorId(devices.get(0).getId());
						return "deviceDetail";
					}

					return "deviceList";
				}
			} else if ("export".equals(operation)) {
				NewReportExporting exportProxy = null;
				List<ReportDataHolder> reportsData = preparePostReportsData();
				if (reportsData == null
						|| reportsData.isEmpty()) {
					exportProxy = new NewReportExporting(false, this.getDataSource(), this.getDataSource().getOwner(), tz);
				} else {
					exportProxy = new NewReportExporting(false, reportsData, this.getDataSource(), this.getDataSource().getOwner(), tz);
				}
				exportProxy.run(new ExportPdfStreamResponse(){
					public void respond() {
						if (this.getResponse() != null
								&& this.getResponse() instanceof ByteArrayOutputStream) {
							setInputStream(new ByteArrayInputStream(((ByteArrayOutputStream)this.getResponse()).toByteArray()));
							setLocalFileName(this.getFileName());
						} else {
							setInputStream(getErrorInputStream());
							setLocalFileName("error.file");
						}
						this.closeStream();
					}
				});

				return "download";
			}
			else if("sendEmail".equals(operation))
			{
				jsonObject=new JSONObject();
				if(emailAddrs!=null&&!emailAddrs.equals(""))
				{
					try{
							List<MailNotification> list = QueryUtil.executeQuery(MailNotification.class, null,
																		new FilterParams("owner.domainName=:s1",new Object[]{this.getDataSource().getOwner().getDomainName()}));
							if(list!=null&&!list.isEmpty())
							{
								MailNotification mailSettings=list.get(0);
								if(mailSettings.getServerName()!=null&&!"".equals(mailSettings.getServerName()))
								{
									NewReportExporting  exportProxy=new NewReportExporting(false,this.getDataSource(),this.getDataSource().getOwner(),tz);
									exportProxy.setEmailAddress(emailAddrs);
									exportProxy.run(new ExportPdfFilePathResponse(){
											public void respond(){
												try {
													String filePath=this.getResponse();
													if(filePath!=null&&!"".equals(filePath))
													{
														File pdfFile=new File(filePath);
														if(pdfFile.exists())
														{
															jsonObject.put("resultStatus",true);
														}
														else{
															jsonObject.put("resultStatus",false);
															jsonObject.put("returnMsg","PDF file generation failed.");
															log.error("DashBoardAction.sendMail(): Failed send mail! ,"+filePath+" does't exist");
														}
													}

												} catch (Exception e) {
													log.error(
												       "DashBoardAction.sendMail(): Failed send mail! ",e);
												}}
											});
								}
								else{
									jsonObject.put("resultStatus",false);
									jsonObject.put("returnMsg","Email service is not configured correctly.");
									log.error("DashBoardAction.sendMail(): Failed send mail:Smtp server hasn't been set properly!");
								}
							}
							else{
								jsonObject.put("resultStatus",false);
								jsonObject.put("returnMsg","Email service is not configured correctly.");
								log.error("DashBoardAction.sendMail(): Failed send mail:Email service setting is not set!");
							}

					}catch(Exception e)
					{
						jsonObject.put("resultStatus",false);
						jsonObject.put("returnMsg","Failed to send this email.");
						log.error("DashBoardAction.sendMail(): Failed to send mail",e);
					}
				}
				else
				{
					jsonObject.put("resultStatus",false);
					jsonObject.put("returnMsg","Email address is required.");
					log.error("DashBoardAction.sendMail(): Failed send mail:email address is not set!");
				}
				return "json";
			}
			else {
				removeUnusedMonitorPage();
				List<AhDashboard> lst = QueryUtil.executeQuery(AhDashboard.class, null,
						new FilterParams("daType=:s1 and userName=:s2",
								new Object[]{AhDashboard.DASHBOARD_TYPE_DASH, getUserContext().getUserName()}),getDomain().getId(), this);
				if (!lst.isEmpty()) {
					boolean hasDefault= false;
					boolean hasActive= false;
					boolean hasApplication= false;
					for(AhDashboard da: lst){
						if(da.getDefaultFlag()){
							setDafaultTabId(da.getId().toString());
							hasDefault=true;
						}
						if(da.isActive()) {
							hasActive=true;
						}
						if (da.getPosition()==DA_TAB_POSITION_APPLICATION) {
							hasApplication = true;
						}
					}
					if (!hasDefault) {
						AhDashboard da = createDefaultDashBoard();
						setDafaultTabId(da.getId().toString());
						setSessionDataSource(da);
						getDataSource().setTz(tz);
					} else {
						if (!hasApplication) {
							insertDefaultDashBoard(DA_TAB_APPLICATION,"Applications",false,false,DA_TAB_POSITION_APPLICATION);
						}
						if (hasActive) {
							for(AhDashboard da: lst){
								if(da.isActive()) {
									setSessionDataSource(da);
									getDataSource().setTz(tz);
								}
							}
						} else {
							for(AhDashboard da: lst){
								if(da.getDefaultFlag()) {
									setSessionDataSource(da);
									getDataSource().setTz(tz);
								}
							}
						}
					}
				} else {
					AhDashboard da = createDefaultDashBoard();
					setDafaultTabId(da.getId().toString());
					setSessionDataSource(da);
					getDataSource().setTz(tz);
				}
				initGroup();
				String[] retArray = checkToChangeAppTimeSelectPeriod(getDataSource(), getDomain().getId());
				if (retArray[0].equals("true")) {
						QueryUtil.updateBo(AhDashboard.class, "selectTimeType=:s1", 
								new FilterParams("id=:s2", new Object[]{AhDashboard.TAB_TIME_LASTCALEDAY,getDataSource().getId()}));
						setSessionDataSource(findBoById(AhDashboard.class, getDataSource().getId(), this));
						getDataSource().setTz(tz);
				}
				if (!retArray[1].isEmpty()) {
					onloadErrorMessage = retArray[1];
				}
				
				String ret =getAllHiveApByApp(getDataSource().getId());
				if(!ret.equals("")) {
					onloadErrorMessage = ret;
				}
				
				return SUCCESS;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			e.printStackTrace();
			log.info("excute",e);
			return SUCCESS;
		}
	}
	
	private String onloadErrorMessage;
	public String getVhmDeviceCount(){
		long count = QueryUtil.findRowCount(HiveAp.class, new FilterParams("manageStatus=:s1 and deviceType!=:s2 and owner.id=:s3",
				new Object[]{HiveAp.STATUS_MANAGED, HiveAp.Device_TYPE_VPN_GATEWAY, getDomain().getId()}));
		if (count==0 && !getUserContext().isDontShowMessageInDashboard()) {
			return getText("hm.dashboard.noDevice.info"); 
		}
		return "";
	}
	
	public static String[] checkToChangeAppTimeSelectPeriod(AhDashboard myDa, long sqlDomainId){
		String ret[]={"false",""};
		if (myDa.getPosition() == DA_TAB_POSITION_APPLICATION && 
				(myDa.getSelectTimeType()==AhDashboard.TAB_TIME_LAST8HOUR
				|| myDa.getSelectTimeType()==AhDashboard.TAB_TIME_LASTDAY
				|| myDa.getSelectTimeType()==AhDashboard.TAB_TIME_LASTWEEK 
				|| myDa.getSelectTimeType()==AhDashboard.TAB_TIME_LASTCALEWEEK
				|| myDa.getSelectTimeType()==AhDashboard.TAB_TIME_CUSTOM)) {
			
			int currentApNum = calculateAppDevicesNumber(myDa.getObjectType(),myDa.getObjectId(), sqlDomainId);
			if ((myDa.getSelectTimeType()==AhDashboard.TAB_TIME_LASTCALEWEEK && currentApNum >SUPPORT_APPLICATION_CALENDAR_WARN)
					|| (myDa.getSelectTimeType()!=AhDashboard.TAB_TIME_LASTCALEWEEK && currentApNum >SUPPORT_APPLICATION_OTHER_WARN)) {
				ret[0] = "true";
			}
			if (currentApNum>SUPPORT_APPLICATION_CALENDAR_ERROR) {
				ret[1]= MgrUtil.getUserMessage("error.da.app.time.support.maxap", new String[]{String.valueOf(SUPPORT_APPLICATION_CALENDAR_ERROR)});
			} 
		} else if (myDa.getPosition() == DA_TAB_POSITION_APPLICATION && 
				myDa.getSelectTimeType()==AhDashboard.TAB_TIME_LASTHOUR) {
			int currentApNum = calculateAppDevicesNumber(myDa.getObjectType(),myDa.getObjectId(), sqlDomainId);
			LogSettings homeLogSetting = MgrUtil.fetchHomeDomainLogSettings();
			int vhmCheckCount = homeLogSetting.getReportMaxApCount();
			if (currentApNum>vhmCheckCount) {
				ret[1] =  MgrUtil.getUserMessage("error.da.support.ap.vhm", new String[]{String.valueOf(vhmCheckCount)});
			}
		}

		return ret;
	}
	
	public String getDisplayAppTimeStyle(){
		if (getDataSource()!=null && getDataSource().getPosition()==DA_TAB_POSITION_APPLICATION) {
			return "";
		}
		return "none";
	}
	public String getDisplayCommonTimeStyle(){
		if (getDataSource()!=null && getDataSource().getPosition()==DA_TAB_POSITION_APPLICATION) {
			return "none";
		}
		return "";
	}
	
	private boolean isApplicationPerspective(Long perspectiveId) {
		String query = "select position from "
				+ AhDashboard.class.getCanonicalName();
		List<?> list = QueryUtil.executeQuery(query, null, new FilterParams(
				"id", perspectiveId));
		if (!list.isEmpty()) {
			return (Integer) list.get(0) == DA_TAB_POSITION_APPLICATION;
		}
		return false;
	}
	
	public Long getApplicationPerspectiveId() {
		String query = "select id from " + AhDashboard.class.getCanonicalName();
		List<?> list = QueryUtil
				.executeQuery(query, null, new FilterParams(
						"userName=:s1 and position =:s2 and daType=:s3 ",
						new Object[] { getUserContext().getUserName(), DA_TAB_POSITION_APPLICATION,
								AhDashboard.DASHBOARD_TYPE_DASH }), getDomain()
						.getId());
		if (list.isEmpty()) {
			return 0L;
		} else {
			return (Long) list.get(0);
		}
	}
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setDataSource(AhDashboard.class);
		setSelectedL2Feature(L2_FEATURE_DASHBOARD);
	}

	public void prepareJsonGUIData() throws JSONException{
		super.prepareJsonGUIData();
		jsonObject.put("tmtp", getDataSource().getSelectTimeType());
		jsonObject.put("tmval", getDataSource().getCurrentDashCustomTimeString());
	}

	public void clearActiveFlgForBo() throws Exception{
		List<AhDashboard> lst = QueryUtil.executeQuery(AhDashboard.class, null,
				new FilterParams("active=:s1 and daType=:s2 and userName=:s3",
						new Object[]{true,AhDashboard.DASHBOARD_TYPE_DASH, getUserContext().getUserName()}),getDomain().getId());
		if(!lst.isEmpty()) {
			for(AhDashboard adb: lst) {
				adb.setActive(false);
			}
			QueryUtil.bulkUpdateBos(lst);
		}
	}

	public List<TextItem> getTabItemArray(){
		List<TextItem> aa = new ArrayList<TextItem>();
		//List<Long> cuDaIds= new ArrayList<Long>();
		Map<Long, String> cuDaIds= new HashMap<Long, String>();
		List<AhDashboard> lst = QueryUtil.executeQuery(AhDashboard.class, new SortParams("position desc, id"),
				new FilterParams("daType=:s1 and userName=:s2",
						new Object[]{AhDashboard.DASHBOARD_TYPE_DASH,getUserContext().getUserName()}),getDomain().getId());
		for(AhDashboard da:  lst) {
			if (getDataSource().getId()!=null && getDataSource().getId().equals(da.getId())) {
				TextItem tmp = new TextItem(da.getId().toString(), da.getDashName(), "1");
				aa.add(tmp);
			} else {
				TextItem tmp = new TextItem(da.getId().toString(), da.getDashName(), null);
				aa.add(tmp);
			}
			cuDaIds.put(da.getId(), da.getDashName());
		}
		if (aa.isEmpty()) {
			TextItem tmp = new TextItem(getDataSource().getId().toString(), getDataSource().getDashName(), "1");
			aa.add(tmp);
		}

		if(!cuDaIds.isEmpty()) {
			List<?> lstAp = QueryUtil.executeQuery("select distinct dashId,timestamp from "
			+ AhDashboardAppAp.class.getSimpleName(), null,
			new FilterParams("dashId", cuDaIds.keySet()),getDomain().getId());
			if(!lstAp.isEmpty()) {
				setTabItemHighInterval(new ArrayList<TextItem>());
				long cutime = System.currentTimeMillis();
				for(Object ob: lstAp){
					Object[] oneOb = (Object[])ob;
					long insertTime = Long.parseLong(oneOb[1].toString());
					long popUpTime = 600000-(cutime-insertTime);
					if(popUpTime<0 || popUpTime > 600000) {
						popUpTime=1000;
					}
					getTabItemHighInterval().add(
							new TextItem(oneOb[0].toString(), String.valueOf(popUpTime), cuDaIds.get(Long.parseLong(oneOb[0].toString()))));
				}
			}
		}

		return aa;
	}
	public AhDashboard createDefaultDashBoard() throws Exception{
		int[] c_keys=null;
		c_keys= new int[]{42,28,47,70,74,1};
		String c_createDashName="Network Summary";
		boolean c_actived=true;
		boolean c_defaultFlag=true;
		long createId = insertDefaultDashBoard(c_keys,c_createDashName,c_actived,c_defaultFlag,5);
		
		c_keys= new int[]{8,19,20,21};
		c_createDashName="System Summary";
		c_actived=false;
		c_defaultFlag=false;
		insertDefaultDashBoard(c_keys,c_createDashName,c_actived,c_defaultFlag,4);
		
		//c_keys= new int[]{81,67,68,56,54};
		c_createDashName="Applications";
		c_actived=false;
		c_defaultFlag=false;
		insertDefaultDashBoard(DA_TAB_APPLICATION,c_createDashName,c_actived,c_defaultFlag,DA_TAB_POSITION_APPLICATION);

		c_keys= new int[]{41,40,48,49,16,17,27,26};
		c_createDashName="Troubleshooting";
		c_actived=false;
		c_defaultFlag=false;
		insertDefaultDashBoard(c_keys,c_createDashName,c_actived,c_defaultFlag,2);
		
		return QueryUtil.findBoById(AhDashboard.class, createId, this);
	}

	public long insertDefaultDashBoard(int[] c_keys, String c_createDashName, boolean c_actived,boolean c_defaultFlag, int position) throws Exception{
		AhDashboard da = new AhDashboard();
		da.setOwner(getDomain());
		da.setUserName(getUserContext().getUserName());
		da.setPosition(position);
		if (position==DA_TAB_POSITION_APPLICATION) {
			da.setSelectTimeType(AhDashboard.TAB_TIME_LAST8HOUR);
		}
		da.setDaType(AhDashboard.DASHBOARD_TYPE_DASH);
		da.setDashName(c_createDashName);
		da.setActive(c_actived);
		da.setDefaultFlag(c_defaultFlag);
		return this.insertDefaultDashBoard(da, c_keys, null);
	}
	public long insertDefaultDashBoard(AhDashboard da, int[] c_keys, DashboardWidgetModifier widgetModifier) throws Exception{
		da.setId(null);
		Long createId = QueryUtil.createBo(da);
		da = QueryUtil.findBoById(AhDashboard.class, createId, this);

		List<AhDashboardWidget> lstWidget = new ArrayList<AhDashboardWidget>();
		for(int i=0; i<c_keys.length; i++) {
			lstWidget = creDefaultWidget(c_keys[i],lstWidget, AhDashboard.DASHBOARD_TYPE_DASH);
		}
		if (widgetModifier != null) {
			for (AhDashboardWidget widget : lstWidget) {
				widgetModifier.modify(widget);
			}
		}
		if (!lstWidget.isEmpty()) {
			List<AhDashboardLayout> layouts = createDefDrillDownLayouts(da,lstWidget);
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

		return createId;
	}

	public List<AhDashboardWidget> creDefaultWidget(int key, List<AhDashboardWidget> lstWidget, int mtype){
		try {
			List<DashboardComponent> bb = QueryUtil.executeQuery(DashboardComponent.class, null,
					new FilterParams("defaultFlag=:s1 and key=:s2",
							new Object[]{true,key}));
			if (!bb.isEmpty()) {
				AhDashboardWidget aa = new AhDashboardWidget();
				aa.setOwner(getDomain());
				aa.setMainTitle(bb.get(0).getComponentName());
				aa.setWidgetConfig(bb.get(0));
				lstWidget.add(aa);
			}
		} catch (Exception e) {
			log.error(e);
		}
		return lstWidget;
	}

	protected List<Integer> getMonitorWidgetsForCertainWidget(Long reportId, String metric) {
		List<?> objs = null;
		if (StringUtils.isBlank(metric)) {
			objs = QueryUtil.executeNativeQuery("select b.drilldownvalue from hm_dashboard_component a, "
							+ "dashboard_component_metric b where a.metric_id=b.id and a.id=" + reportId, 1);
		} else {
			objs = QueryUtil.executeNativeQuery("select c.drilldownvalue from hm_dashboard_component a, dashboard_component_metric b, "
					+ "dashboard_component_data c where a.metric_id=b.id and c.component_metric_id = b.id and a.id=" + reportId, 1);
		}
		if (objs != null
				&& !objs.isEmpty()
				&& objs.get(0) != null) {
			String keys = objs.get(0).toString();
			if (StringUtils.isNotBlank(keys)) {
				String[] keySets = keys.split("-");
				if (keySets != null
						&& keySets.length > 0) {
					List<Integer> result = new ArrayList<>();
					for (String singleKey : keySets) {
						if (StringUtils.isNotBlank(singleKey)) {
							result.add(Integer.valueOf(singleKey));
						}
					}
					return result;
				}
			}
		}
		return null;
	}
	
	protected AhDashboard createDefaultMonitorDashBoard(AhDashboard da, Long reportId, String metric, DashboardWidgetModifier widgetModifier) throws Exception {
		List<Integer> widgets = this.getMonitorWidgetsForCertainWidget(reportId, metric);
		if (widgets == null
				|| widgets.isEmpty()) {
			return null;
		}
		int[] c_keys = new int[widgets.size()];
		for (int i = 0; i < widgets.size(); i++) {
			c_keys[i] = widgets.get(i);
		}
		Long createId = this.insertDefaultDashBoard(da, c_keys, widgetModifier);
		
		return QueryUtil.findBoById(AhDashboard.class, createId, this);
	}

	public List<Integer> getApplicationKeys() {
		return DaHelper.getApplicationWidgetKeys();
	}
	
	@SuppressWarnings("unchecked")
	public String getAllHiveApByApp(Long daId){
		try {
			StringBuilder sql = new StringBuilder();
			List<Integer> keys = getApplicationKeys();
			List<?> daClist = QueryUtil.executeQuery("select id from " + DashboardComponent.class.getSimpleName(),
					null, new FilterParams("key", keys));

			sql.append("select distinct d.objectType, d.objectId, d.selectTimeType from hm_dashboard d,hm_dashboard_widget w,hm_dashboard_layout l ");
			sql.append(" where d.id=").append(daId).append(" and l.id=w.da_layout_id and d.id=l.dashboard_id ")
				.append(" and d.selectTimeType=1").append(" and d.owner=").append(getDomain().getId());
			sql.append(" and w.widget_config_id in (");
			for(int i=0; i< daClist.size();i++){
				if (i==daClist.size()-1) {
					sql.append(daClist.get(i).toString()).append(")");
				} else {
					sql.append(daClist.get(i).toString()).append(",");
				}
			}
			
			List<?> wIds = QueryUtil.executeNativeQuery(sql.toString());
			
			Set<String> apMacList = new HashSet<String>();
			if (!wIds.isEmpty()) {
				Object[] oneOb = (Object[])wIds.get(0);
				calculateAppDevicesList(
						oneOb[0].toString(),
						oneOb[1].toString(),
						apMacList);
			}

			System.out.println(apMacList);
			//Set<String> addIntervalList = new HashSet<String>();
			Set<String> removeIntervalList = new HashSet<String>();
			List<AhDashboardAppAp> appAplist = QueryUtil.executeQuery(AhDashboardAppAp.class, null, new FilterParams("dashId",daId));

			if(apMacList.isEmpty()) {
				setHasHighIntervalApFlg(false);
				if(!appAplist.isEmpty()) {
					for(AhDashboardAppAp ap: appAplist){
						removeIntervalList.add(ap.getApMac());
					}
				}
			} else {
				setHasHighIntervalApFlg(true);
				//addIntervalList.addAll(apMacList);
				if(!appAplist.isEmpty()) {
					//Set<String> existMac = new HashSet<String>();
					for(AhDashboardAppAp oriAp: appAplist){
						if(!apMacList.contains(oriAp.getApMac())) {
							removeIntervalList.add(oriAp.getApMac());
						//} else {
						//	existMac.add(oriAp.getApMac());
						}
					}
					//addIntervalList.removeAll(existMac);
				}
			}
			if(!removeIntervalList.isEmpty()) {
				List<String> remlist = (List<String>)QueryUtil.executeQuery("select distinct apMac from " + AhDashboardAppAp.class.getSimpleName(), null,
					new FilterParams("dashId!=:s1 and apMac in (:s2)",new Object[]{daId, removeIntervalList}));

				removeIntervalList.removeAll(remlist);
			}
			//if(!addIntervalList.isEmpty()) {
			//	List<String> remlist = (List<String>)QueryUtil.executeQuery("select distinct apMac from " + AhDashboardAppAp.class.getSimpleName(), null,
			//		new FilterParams("dashId!=:s1 and apMac in (:s2)",new Object[]{daId, addIntervalList}));
			//		addIntervalList.removeAll(remlist);
			//}

			List<String> totalList = (List<String>)QueryUtil.executeQuery("select distinct apMac from " + AhDashboardAppAp.class.getSimpleName(), null,
					new FilterParams("dashId!=:s1",new Object[]{daId}));
			List<String> totalVhmList = (List<String>)QueryUtil.executeQuery("select distinct apMac from " + AhDashboardAppAp.class.getSimpleName(), null,
					new FilterParams("dashId!=:s1",new Object[]{daId}), getDomain().getId());

			String msg = checkIntervalApLimit(apMacList,removeIntervalList,totalList, totalVhmList);
			if(msg.equals("")) {
				long cutime = System.currentTimeMillis();
				if(!apMacList.isEmpty()) {
					List<AhDashboardAppAp> insertList = new ArrayList<AhDashboardAppAp>();
					for(String mac: apMacList){
						AhDashboardAppAp tmp = new AhDashboardAppAp();
						tmp.setOwner(getDomain());
						tmp.setApMac(mac);
						tmp.setDashId(daId);
						tmp.setTimestamp(cutime);
						insertList.add(tmp);
					}
					QueryUtil.bulkCreateBos(insertList);
				}
				QueryUtil.bulkRemoveBos(AhDashboardAppAp.class, new FilterParams("dashId=:s1 and timestamp!=:s2",
						new Object[]{daId,cutime}), getDomain().getId());
				//send CLI to get data from ap
				//send CLI to stop app 10 min collect
				if(!apMacList.isEmpty()) {
					AhAppContainer.HmBe.getPerformModule().getBeAppReportCollectionProcessor().startAppReportCollect(apMacList.toArray(new String[apMacList.size()]));
				}
				if (!removeIntervalList.isEmpty()) {
					AhAppContainer.HmBe.getPerformModule().getBeAppReportCollectionProcessor().stopAppReportCollect(removeIntervalList.toArray(new String[removeIntervalList.size()]));
				}
			} else {
				return msg;
			}

		} catch (Exception e) {
			log.error(e);
			return e.getMessage();
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public static void clearAppAp(List<Long> ids){
		try {
			if(ids.isEmpty()) {
				return;
			}
			Set<String> removeIntervalList = new HashSet<String>();
			List<String> appAplist = (List<String>)QueryUtil.executeQuery("select distinct apMac from "
					+ AhDashboardAppAp.class.getSimpleName(), null, new FilterParams("dashId",ids));
			removeIntervalList.addAll(appAplist);
			List<?> remlist = QueryUtil.executeQuery("select distinct apMac from " + AhDashboardAppAp.class.getSimpleName(), null,
					new FilterParams("dashId not in (:s1)",new Object[]{ids}));
			removeIntervalList.removeAll(remlist);

			QueryUtil.bulkRemoveBos(AhDashboardAppAp.class, new FilterParams("dashId",ids));
			//send CLI to stop app 10 min collect
			if (!removeIntervalList.isEmpty()) {
				AhAppContainer.HmBe.getPerformModule().getBeAppReportCollectionProcessor().stopAppReportCollect(removeIntervalList.toArray(new String[removeIntervalList.size()]));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void clearAppAp(Long ids){
		try {
			Set<String> removeIntervalList = new HashSet<String>();
			List<String> appAplist = (List<String>)QueryUtil.executeQuery("select distinct apMac from "
					+ AhDashboardAppAp.class.getSimpleName(), null, new FilterParams("dashId",ids));
			removeIntervalList.addAll(appAplist);
			List<?> remlist = QueryUtil.executeQuery("select distinct apMac from " + AhDashboardAppAp.class.getSimpleName(), null,
					new FilterParams("dashId not in (:s1)",new Object[]{ids}));
			removeIntervalList.removeAll(remlist);

			QueryUtil.bulkRemoveBos(AhDashboardAppAp.class, new FilterParams("dashId",ids));
			//send CLI to stop app 10 min collect
			if (!removeIntervalList.isEmpty()) {
				AhAppContainer.HmBe.getPerformModule().getBeAppReportCollectionProcessor().stopAppReportCollect(removeIntervalList.toArray(new String[removeIntervalList.size()]));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	private String checkIntervalApLimit(Set<String> addSet, Set<String> removeSet, List<String> totalSet, List<String> totalVhmSet) {
		// check number of support ap limit
		if(addSet.isEmpty()) {
			return "";
		} else {
			LogSettings homeLogSetting = MgrUtil.fetchHomeDomainLogSettings();
			int vhmCheckCount = homeLogSetting.getReportMaxApCount();
			int systemCheckCount= homeLogSetting.getReportMaxApForSystem();

			Set<String> totalVhm= new HashSet<String>();
			totalVhm.addAll(totalVhmSet);
			totalVhm.addAll(addSet);
			totalVhm.removeAll(removeSet);
			if(totalVhm.size()>vhmCheckCount) {
				return MgrUtil.getUserMessage("error.da.support.ap.vhm", new String[]{String.valueOf(vhmCheckCount)});
			}
			
			Set<String> total= new HashSet<String>();
			total.addAll(totalSet);
			total.addAll(addSet);
			total.removeAll(removeSet);
			
			if(total.size() > systemCheckCount) {
				return MgrUtil.getUserMessage("error.da.support.ap.hm");
			}

		}
		return "";
	}
	
	public static int calculateAppDevicesNumber(String obType, String obId, long sqlDomainId) {
		String serSql = fetchAppApSql(obType, obId, sqlDomainId);
		List<?> deviceList = QueryUtil.executeNativeQuery(serSql);
		return deviceList.size();
	}

	private void calculateAppDevicesList(String obType, String obId, Set<String> apMacList) {
		String serSql = fetchAppApSql(obType, obId, getDomain().getId());
		List<?> deviceList = QueryUtil.executeNativeQuery(serSql);
		for(Object ob: deviceList) {
			apMacList.add(ob.toString());
		}
	}

	public static String fetchAppApSql(String obType, String obId, long sqlDomainId){
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct a.macAddress from hive_ap a");
		sql.append(" where a.owner=").append(sqlDomainId);
		sql.append(" and a.managestatus=").append(HiveAp.STATUS_MANAGED);
		sql.append(" and a.simulated=false");
		sql.append(" and a.devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY);
		sql.append(" and a.devicetype!=").append(HiveAp.Device_TYPE_SWITCH);
		sql.append(" and a.softVer>'6.0.0.0'");
		//sql.append(" and a.template_id = c.config_template_id");

		String mapSql=null;
		if (obType.equals(AhDashBoardGroup.DA_GROUP_TYPE_MAP) && obId!=null && Long.valueOf(obId)>0) {

			Set<Long> mapIds =  BoMgmt.getMapMgmt().getContainerDownIds(Long.valueOf(obId));
			StringBuilder tmpId = new StringBuilder();
			for(Long it: mapIds){
				if (tmpId.length()>0) {
					tmpId.append(",");
				}
				tmpId.append(it);
			}
			if (tmpId.length()>0) {
				mapSql =  tmpId.toString();
			}
		}

		if (mapSql!=null) {
			sql.append(" and a.map_container_id in (").append(mapSql).append(")");
		}
		if(obType.endsWith(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH)){
			sql.append(" and a.macAddress = '").append(obId).append("'");
		} else {
			switch (obType) {
//			case AhDashBoardGroup.DA_GROUP_TYPE_SSID:
//				if(!obId.equals(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_SSID))) {
//					sql.append(" and s.ssid='").append(obId).append("'");
//				}
//				break;
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
					}  else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_370)){
						sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_370);
						sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_HIVEAP);
					} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_390)){
						sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_390);
						sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_HIVEAP);
					} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP_230)){
						sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_230);
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
					} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_AP330)){
						sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_330);
						sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_BRANCH_ROUTER);
					} else if(obId.equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR_AP350)){
						sql.append(" and a.hiveApModel=").append(HiveAp.HIVEAP_MODEL_350);
						sql.append(" and a.deviceType=").append(HiveAp.Device_TYPE_BRANCH_ROUTER);
					}
				}
				break;
			}
		}
		return sql.toString();
	}

	@SuppressWarnings("unchecked")
	private void removeUnusedMonitorPage(){
		try {
			if(MgrUtil.getSessionAttribute(MONITOR_TAB_ID_USERAPPLICATION)==null){
			} else {
				Map<String,String> mon = (Map<String,String>)MgrUtil.getSessionAttribute(MONITOR_TAB_ID_USERAPPLICATION);
				List<Long> ids = new ArrayList<Long>();
				for(String key : mon.keySet()){
					ids.add(Long.parseLong(key));
				}
				if(!ids.isEmpty()) {

					List<Long> layoutIds = (List<Long>)QueryUtil.executeQuery("select id from "  + AhDashboardLayout.class.getSimpleName()
							, null, new FilterParams("dashboard.id",ids));
					if(!layoutIds.isEmpty()) {
						QueryUtil.removeBos(AhDashboardWidget.class, new FilterParams("daLayout.id",layoutIds));
						QueryUtil.removeBos(AhDashboardLayout.class, layoutIds);
					}
					QueryUtil.removeBos(AhDashboard.class, ids);
				}
				clearAppAp(ids);
				MgrUtil.setSessionAttribute(MONITOR_TAB_ID_USERAPPLICATION, null);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}


	public void prepareWidgetConfigs() {
		setWidgetConfigs(new HashMap<Integer, List<WidgetConfig>>());
		if (getColumnsConfig() != null
				&& getColumnsConfig().length > 0) {
			int chartLen = 0;
			if (getColumnCharts() != null) chartLen = getColumnCharts().length;
			Integer columnOrder = 0;
			int paramPos;
			for (int i = 0; i < getColumnsConfig().length; i++) {
				if (i < chartLen) {
					String chartsTmpStr = getColumnCharts()[i];
					if (!StringUtils.isBlank(chartsTmpStr)) {
						String[] chartsTmp = chartsTmpStr.split(",");
						if (chartsTmp != null
								&& chartsTmp.length > 0) {
							getWidgetConfigs().put(columnOrder, new ArrayList<WidgetConfig>());
							for (String chart : chartsTmp) {
								if (StringUtils.isNotBlank(chart)) {
									String[] configsTmp = chart.split(";");
									WidgetConfig widgetConfig = new WidgetConfig();
									paramPos = 0;
									if (configsTmp.length > paramPos) {
										widgetConfig.setReportId(Long.valueOf(configsTmp[paramPos]));
									}
									paramPos++;
									if (configsTmp.length > paramPos) {
										widgetConfig.setTitle(configsTmp[paramPos]);
									}
									paramPos++;
									/*if (configsTmp.length > paramPos) {
										widgetConfig.setBlnOverTime(Boolean.valueOf(configsTmp[paramPos]));
									}*/
									paramPos++;
									if (configsTmp.length > paramPos) {
										if ("1".equals(configsTmp[paramPos])) {
											widgetConfig.setBlnCloned(true);
										} else {
											widgetConfig.setBlnCloned(false);
										}
									}
									paramPos++;
									if (configsTmp.length > paramPos) {
										Long wId = Long.valueOf(configsTmp[paramPos]);
										if (wId.compareTo(0L) > 0) {
											widgetConfig.setPreWidgetId(wId);
										}
									}
//									paramPos++;
//									if (configsTmp.length > paramPos
//											&& StringUtils.isNotBlank(configsTmp[paramPos])) {
//										Long mId = Long.valueOf(configsTmp[paramPos]);
//											widgetConfig.setMapId(mId);
//										}
									paramPos++;
									if (configsTmp.length > paramPos
											&& StringUtils.isNotBlank(configsTmp[paramPos])) {
										widgetConfig.setObjectType(configsTmp[paramPos]);
									}
									paramPos++;
									if (configsTmp.length > paramPos
											&& StringUtils.isNotBlank(configsTmp[paramPos])) {
										widgetConfig.setObjectId(configsTmp[paramPos]);
									}
									paramPos++;
									if (configsTmp.length > paramPos
											&& StringUtils.isNotBlank(configsTmp[paramPos])) {
										widgetConfig.setFilterObjectType(configsTmp[paramPos]);
									}
									paramPos++;
									if (configsTmp.length > paramPos
											&& StringUtils.isNotBlank(configsTmp[paramPos])) {
										widgetConfig.setFilterObjectId(configsTmp[paramPos]);
									}
									paramPos++;
									if (configsTmp.length > paramPos
											&& StringUtils.isNotBlank(configsTmp[paramPos])) {
										widgetConfig.selectTimeType = Integer.valueOf(configsTmp[paramPos]);
									}
									paramPos++;
									if (configsTmp.length > paramPos) {
										widgetConfig.startTime = configsTmp[paramPos];
									}
									paramPos++;
									if (configsTmp.length > paramPos
											&& StringUtils.isNotBlank(configsTmp[paramPos])) {
										widgetConfig.startHour = Integer.valueOf(configsTmp[paramPos]);
									}
									paramPos++;
									if (configsTmp.length > paramPos) {
										widgetConfig.endTime = configsTmp[paramPos];
									}
									paramPos++;
									if (configsTmp.length > paramPos
											&& StringUtils.isNotBlank(configsTmp[paramPos])) {
										widgetConfig.endHour = Integer.valueOf(configsTmp[paramPos]);
									}
									paramPos++;
									if (configsTmp.length > paramPos
											&& StringUtils.isNotBlank(configsTmp[paramPos])) {
										widgetConfig.enableTimeLocal = Boolean.valueOf(configsTmp[paramPos]);
									}
									paramPos++;
									if (configsTmp.length > paramPos
											&& StringUtils.isNotBlank(configsTmp[paramPos])) {
										widgetConfig.checked = Boolean.valueOf(configsTmp[paramPos]);
									}
									getWidgetConfigs().get(columnOrder).add(widgetConfig);
								}
							}
						}
					}
					columnOrder++;
				}
			}
		}
		createNewWidgetConfigForDefaultClonedWidget();
	}

	private String tabName;
	private String tabRemoveId;
	private String tabChangeId;


	private  boolean hasHighIntervalApFlg = false;
	private boolean removeHighIntervalFlg = false;
	private String removeHighIntervalId;
	private List<TextItem> tabItemHighInterval;

	public boolean isHasHighIntervalApFlg() {
		return hasHighIntervalApFlg;
	}

	public void setHasHighIntervalApFlg(boolean hasHighIntervalApFlg) {
		this.hasHighIntervalApFlg = hasHighIntervalApFlg;
	}

	public boolean isRemoveHighIntervalFlg() {
		return removeHighIntervalFlg;
	}

	public void setRemoveHighIntervalFlg(boolean removeHighIntervalFlg) {
		this.removeHighIntervalFlg = removeHighIntervalFlg;
	}

	public String getRemoveHighIntervalId() {
		return removeHighIntervalId;
	}

	public void setRemoveHighIntervalId(String removeHighIntervalId) {
		this.removeHighIntervalId = removeHighIntervalId;
	}

	public List<TextItem> getTabItemHighInterval(){
		if(tabItemHighInterval==null) {
			return new ArrayList<TextItem>();
		}
		return tabItemHighInterval;
	}

	public void setTabItemHighInterval(List<TextItem> tabItemHighInterval) {
		this.tabItemHighInterval = tabItemHighInterval;
	}


	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getTabRemoveId() {
		return tabRemoveId;
	}

	public void setTabRemoveId(String tabRemoveId) {
		this.tabRemoveId = tabRemoveId;
	}

	public String getTabChangeId() {
		return tabChangeId;
	}

	public void setTabChangeId(String tabChangeId) {
		this.tabChangeId = tabChangeId;
	}


	//for send pdf email
	private String emailAddrs;

	public String getEmailAddrs() {
		return emailAddrs;
	}

	public void setEmailAddrs(String emailAddrs) {
		this.emailAddrs = emailAddrs;
	}

	private List<ReportDataHolder> preparePostReportsData() {
		List<ReportDataHolder> result = new ArrayList<>();

		if (this.rd_data != null
				&& !rd_data.isEmpty()) {
			for (int i = 0; i < this.rd_data.size(); i++) {
				ReportDataHolder dataHolder = new ReportDataHolder();
				dataHolder.setData(rd_data.get(i));
				dataHolder.setResult(rd_result.get(i));
				dataHolder.setException(rd_exception.get(i));
				dataHolder.setColumn(rd_column.get(i));
				dataHolder.setOrder(rd_order.get(i));
				dataHolder.setWidgetId(rd_wIds.get(i));
				dataHolder.setXaxis(rd_xaxis.get(i));
				dataHolder.setSample(rd_samples.get(i));
				result.add(dataHolder);
			}
		}

		return result;
	}

	private List<String> rd_data;
	private List<String> rd_result;
	private List<String> rd_exception;
	private List<Integer> rd_column;
	private List<Integer> rd_order;
	private List<Long> rd_wIds;
	private List<String> rd_xaxis;
	private List<Integer> rd_samples;

	public List<Integer> getRd_samples() {
		return rd_samples;
	}

	public void setRd_samples(List<Integer> rd_samples) {
		this.rd_samples = rd_samples;
	}

	public List<String> getRd_data() {
		return rd_data;
	}

	public void setRd_data(List<String> rd_data) {
		this.rd_data = rd_data;
	}

	public List<String> getRd_result() {
		return rd_result;
	}

	public void setRd_result(List<String> rd_result) {
		this.rd_result = rd_result;
	}

	public List<String> getRd_exception() {
		return rd_exception;
	}

	public void setRd_exception(List<String> rd_exception) {
		this.rd_exception = rd_exception;
	}

	public List<String> getRd_xaxis() {
		return rd_xaxis;
	}

	public void setRd_xaxis(List<String> rd_xaxis) {
		this.rd_xaxis = rd_xaxis;
	}

	public List<Integer> getRd_column() {
		return rd_column;
	}

	public void setRd_column(List<Integer> rd_column) {
		this.rd_column = rd_column;
	}

	public List<Integer> getRd_order() {
		return rd_order;
	}

	public void setRd_order(List<Integer> rd_order) {
		this.rd_order = rd_order;
	}

	public List<Long> getRd_wIds() {
		return rd_wIds;
	}

	public void setRd_wIds(List<Long> rd_wIds) {
		this.rd_wIds = rd_wIds;
	}

	public String getOnloadErrorMessage() {
		if(onloadErrorMessage!=null && !onloadErrorMessage.isEmpty()) {
			return onloadErrorMessage;
		}
		return "";
	}

}