package com.ah.ui.actions.monitor;

/*
 * @author Fisher
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.dashboard.AhDashBoardGroup;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.dashboard.AhDashboardLayout;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.dashboard.DashboardComponentMetric;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.admin.NavigationCustomizationUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class RecurReportAction extends DashboardAbstractAction {

	private static final Tracer log = new Tracer(RecurReportAction.class.getSimpleName());

	private static final long serialVersionUID = 1L;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_REPORT_NAME = 1;
	public static final int COLUMN_REPORT_STATUS = 2;
	public static final int COLUMN_REPORT_PERIOD = 3;
	public static final int COLUMN_REPORT_EMAIL = 4;
	public static final int COLUMN_REPORT_DESCRIPTION = 5;

	@Override
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_REPORT_NAME:
			code = "hm.recurreport.list.report.name";
			break;
		case COLUMN_REPORT_STATUS:
			code = "hm.recurreport.list.report.status";
			break;
		case COLUMN_REPORT_PERIOD:
			code = "hm.recurreport.list.report.period";
			break;
		case COLUMN_REPORT_EMAIL:
			code = "hm.recurreport.list.report.email";
			break;
		case COLUMN_REPORT_DESCRIPTION:
			code = "hm.recurreport.list.report.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(7);
		columns.add(new HmTableColumn(COLUMN_REPORT_NAME));
		columns.add(new HmTableColumn(COLUMN_REPORT_STATUS));
		columns.add(new HmTableColumn(COLUMN_REPORT_PERIOD));
		columns.add(new HmTableColumn(COLUMN_REPORT_EMAIL));
		columns.add(new HmTableColumn(COLUMN_REPORT_DESCRIPTION));
		return columns;
	}

	@Override
	public String execute() throws Exception {
		tz = getUserTimeZone();

		try {
			if("new".equals(operation)){
				log.info("operation: " + operation);
				AhDashboard dashboard = new AhDashboard();
				dashboard.setActive(false);
				dashboard.setOwner(getDomain());
				dashboard.setUserName("");
				dashboard.setDaType(AhDashboard.DASHBOARD_TYPE_REPORT);
				dashboard.setDefaultFlag(false);
				dashboard.setBgRollup(false);
				setSessionDataSource(dashboard);
				getDataSource().setTz(tz);
				prepareDependentObjects();
				return INPUT;
			} else if("edit".equals(operation)){
				log.info("operation: " + operation);
				editBo(this);
				getDataSource().setTz(tz);
				prepareDependentObjects();
				return INPUT;
			} else if("create".equals(operation)){
				log.info("operation: " + operation);
				String where = "dashName = :s1 and daType = :s2";
				Object[] values = new Object[] { getDataSource().getDashName(),
						AhDashboard.DASHBOARD_TYPE_REPORT };
				if (checkNameExists(where, values)) {
					prepareDependentObjects();
					saveDaLayoutsToDashboardSession(this.getDataSource());
					return INPUT;
				}
				Long newId = createBo(getDataSource());
				this.getDataSource().setId(newId);
				saveDaLayouts(this.getDataSource());
				return prepareBoList();
			} else if("update".equals(operation)){
				log.info("operation: " + operation);
				updateBo(getDataSource());
				saveDaLayouts(this.getDataSource());
				return prepareBoList();
			} else if("clone".equals(operation)){
				log.info("operation: " + operation);
				long cloneId = getSelectedIds().get(0);
				AhDashboard dashboard = findBoById(AhDashboard.class, cloneId, this);
				if(null != dashboard){
					AhDashboard report = dashboard.clone();
					report.setActive(false);
					report.setOwner(getDomain());
					report.setUserName("");
					report.setDaType(AhDashboard.DASHBOARD_TYPE_REPORT);
					report.setDefaultFlag(false);
					report.setBgRollup(false);
					report.setPosition(0);
					report.setId(null);
					report.setDashName("");
					report.setCloneDash(true);
					cloneDashboardLayouts(dashboard, report, false);
					setSessionDataSource(report);
					getDataSource().setTz(tz);
					prepareDependentObjects();
					return INPUT;
				}else{
					return prepareBoList();
				}
			} else if("enable".equals(operation)){
				log.info("operation: " + operation);
				enableReportOperation(true);
				return prepareBoList();
			} else if("disable".equals(operation)){
				log.info("operation: " + operation);
				enableReportOperation(false);
				return prepareBoList();
//			} else if ("changeTopyTree".equals(operation)){
//				jsonObject = new JSONObject();
//				try {
//					MapContainerNode node = QueryUtil.findBoById(MapContainerNode.class, Long.parseLong(treeId));
//					getDataSource().setLocation(node);
//					getDataSource().setObjectType(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
//					getDataSource().setObjectId(String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
//					initFilterGroup();
//					String v = filterList.toString();
//					String v3 = secondFilterList.toString();
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
//					String v = filterList.toString();
//					String v3 = secondFilterList.toString();
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
					String v3 = secondFilterList.toString();
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
					AhDashboardWidget wd = QueryUtil.findBoById(AhDashboardWidget.class, widgetId, this);
//					MapContainerNode node = QueryUtil.findBoById(MapContainerNode.class, Long.parseLong(widgetLocationId));
//					wd.setLocation(node);
					wd.setObjectType(treeType);
					wd.setObjectId(treeId);
					wd.setFilterObjectType(filterObjectType);
					wd.setFilterObjectId(filterObjectId);
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
					getDataSource().setObjectType(treeType);
					getDataSource().setObjectId(treeId);
					getDataSource().setFilterObjectType(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
					getDataSource().setFilterObjectId(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
					StringBuilder sql = fetchFilterMapSql();
					fetchSsidList(sql);
					if (!isEasyMode()) {
						fetchUserProfileList(sql);
					}
					String v3 = secondFilterList.toString();
					jsonObject.put("v3", v3);
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("changeFilterUpTree".equals(operation)){
				jsonObject = new JSONObject();
				try {
					getDataSource().setFilterObjectType(treeType);
					getDataSource().setFilterObjectId(treeId);
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
//			} else if ("clickEnabledScheduleCheckbox".equals(operation)) {
//				jsonObject = new JSONObject();
//				try {
//					if(getDataSource().getReEmailAddress()==null || getDataSource().getReEmailAddress().equals("")){
//						jsonObject.put("m", "Please click schedule type button and enter the email address.");
//					} else {
//						getDataSource().setReEnabledScheduleCheckbox(reEnabledScheduleCheckbox);
//						updateDataSourceValue();
//					}
//				} catch (Exception e) {
//					jsonObject.put("m", e.getMessage());
//				}
//				return "json";
			} else if ("fetchDetailDevice".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					StringBuilder serSql = new StringBuilder();
					serSql.append("select distinct hostName,macAddress from hive_ap where owner=")
					.append(getDomain().getId())
					.append(" and managestatus=").append(HiveAp.STATUS_MANAGED);
					Short[] modelType = fetchParamFilter(treeId);
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
						if(filterObjectType.endsWith(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH)){
							serSql.append(" and macAddress!='").append(filterObjectId).append("'");
						}
						serSql.append(" order by hostName");
						if(filterObjectType.endsWith(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH)
								&& filterObjectType.equals(obId + AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH)){
							offset--;
							if (offset<0) {offset=0;}
						}
					}
					List<?> deviceList = QueryUtil.executeNativeQuery(serSql.toString(), 10, offset);
					if (!deviceList.isEmpty()) {
						JSONArray list = new JSONArray();
						for(Object obj: deviceList){
							Object[] oneObj = (Object[]) obj;
							JSONObject jsObj = new JSONObject();
							jsObj.put("label", oneObj[0].toString());
							jsObj.put("title", oneObj[0].toString());
							jsObj.put("id", oneObj[1].toString());
							jsObj.put("tp", treeId + AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH);
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
			} else if ("changeCustomTimeType".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					fillReportScheduleSettings(getDataSource());
//					if(timeType==AhDashboard.TAB_TIME_CUSTOM) {
						jsonObject.put("v", getDataSource().getDisplayReportCurrentTimeString());
//					}
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("fetchScheduleSetting".equals(operation)) {
				jsonObject = fetchScheduleSetting();
				return "json";
			} else if ("removeDashboardComponent".equals(operation)) {
				log.info("operation: " + operation + ", id: " + id);
				jsonObject = removeDashboardComponent(id);
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
			} /*else if ("changeSpecifyType".equals(operation)){
				jsonObject = new JSONObject();
				String maV="-1";
				try {
					jsonObject.put("wi", new JSONArray(fetchWidgetSelectInfoList(specifyType)));
//					jsonObject.put("mm", new JSONArray(fetchMetricSelectInfoList(specifyType,maV)));
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("changeMetricAxis".equals(operation)) {
				jsonObject = new JSONObject();
				try {
//					jsonObject.put("mm", new JSONArray(fetchMetricSelectInfoList(specifyType,sourceType)));
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			}*/ else if ("saveWidgetCopyDiv".equals(operation)) {
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
					String ret = removeMetricById(preMetricId);
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
					String ret = removeWidgetConfigById(preWidgetId);
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

				JSONObject jDaReportsInfo = new JSONObject();
				jObjInfo.put("daReports", jDaReportsInfo);

				TimeZone tzTmp = this.tz;
				if (this.getDataSource() != null) {
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
				}

				return "json";
			} else if("saveLayout".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", true);

//				if (getDataSource() != null) {
//					getDataSource().setBgRollup(bgRollup);
//				}
				Map<Long, ComponentConfigs> savedLayouts = saveDaLayouts(this.getDataSource());

				if (this.getDataSource().isMonitorKindOfDa()) {
					saveDaLayouts(getCertainTemplateDa(this.getDataSource()), false);
				}

//				updateDataSourceValue();

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
				}
//				hasHighIntervalApFlg=false;
//				String ret =getAllHiveApByApp(getDataSource().getId());
//				if(!ret.equals("")) {
//					jsonObject.put("dailog", ret);
//				}
//				if(hasHighIntervalApFlg){
//					jsonObject.put("timeo", true);
//					jsonObject.put("dName", getDataSource().getDashName());
//					jsonObject.put("id", getDataSource().getId());
//				}
				return "json";
			} else if("getConfig4Widget".equals(operation)) {
				jsonObject = this.getWidgetConfigJSONObject(widgetId);
				return "json";
			} else if ("getHtmlContent".equals(operation)) {
				jsonArray = new JSONArray();
				JSONObject objHtml = new JSONObject();
				jsonArray.put(objHtml);
				objHtml.put("resultStatus", true);
				DashboardComponent daComponent = QueryUtil.findBoByAttribute(DashboardComponent.class, "id", this.widgetConfigId, getDomain().getId(), this);
				if (daComponent != null
						&& daComponent.getComponentMetric() != null) {
					objHtml.put("htmlText", daComponent.getComponentMetric().getCustomHtml());
				}

				return "json";
			} else if ("topoSelect".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					if (widgetId != null
							&& widgetId > 0) {
						AhDashboardWidget wd = QueryUtil.findBoById(AhDashboardWidget.class, widgetId);
						if(wd==null) {
							jsonObject.put("t", false);
							jsonObject.put("m", "The widget is not exist.");
							return "json";
						}
//						if(wd.getLocation()==null) {
//							jsonObject.put("map", String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_MAP));
//						} else {
//							jsonObject.put("map", wd.getLocation().getId().toString());
//						}
						jsonObject.put("obType", wd.getObjectType());
						jsonObject.put("obId", wd.getObjectId());
						jsonObject.put("fobType", wd.getFilterObjectType());
						jsonObject.put("fobId", wd.getFilterObjectId());
					} else {
//						jsonObject.put("map", String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_MAP));
						jsonObject.put("obType", AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
						jsonObject.put("obId", String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
						jsonObject.put("fobType", String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
						jsonObject.put("fobId", String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL));
					}
					initGroup();
					String v = filterList.toString();
					jsonObject.put("v", v);
					String v3 = secondFilterList.toString();
					jsonObject.put("v3", v3);
					//jsonObject.put("wId", widgetId);
					jsonObject.put("t", true);
				} catch (Exception e) {
					e.printStackTrace();
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("checkWidget".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", true);

				if (widgetId != null) {
					AhDashboardWidget daWidget = QueryUtil.findBoByAttribute(AhDashboardWidget.class, "id", widgetId, getDomain().getId());
					if (daWidget != null) {
						daWidget.setBlnChecked(widgetChecked);

						try {
							QueryUtil.updateBo(daWidget);
						} catch (Exception e1) {
							jsonObject.put("resultStatus", false);
							log.error("Failed to save check status for widget.", e1);
						}
					}
				}
				return "json";
			} else if ("saveReportBasic".equals(operation)) {
				log.info("operation: " + operation);
				jsonObject = new JSONObject();
				if(!StringUtils.isEmpty(this.reportName)){
					getDataSource().setDashName(this.reportName);
				}
				getDataSource().setDescription(this.reportDescription);
				getDataSource().setPdfHeader(this.reportHeader);
				getDataSource().setPdfFooter(this.reportFooter);
				getDataSource().setPdfSummary(this.reportSummary);
				jsonObject.put("suc", true);
				jsonObject.put("n", this.reportName);
				return "json";
			} else if ("saveReportSchedule".equals(operation)) {
				log.info("operation: " + operation);
				jsonObject = new JSONObject();
				fillReportScheduleSettings(getDataSource());
				getDataSource().setReportScheduleStatus(
						AhDashboard.REPORT_STATUS_SCHEDULED);
				jsonObject.put("suc", true);
				jsonObject.put("pd", getDataSource().getDisplayReportCurrentTimeString());
				return "json";
			} else if ("loadReportConfig".equals(operation)) {
				log.info("operation: " + operation);
				jsonObject = new JSONObject();
				jsonObject.put("id", getDataSource().getId());
				jsonObject.put("rn", getDataSource().getDashName());
				jsonObject.put("rd", getDataSource().getDescription());
				jsonObject.put("rh", getDataSource().getPdfHeader());
				jsonObject.put("rf", getDataSource().getPdfFooter());
				jsonObject.put("rs", getDataSource().getPdfSummary());
				jsonObject.put("fr", getDataSource().getRefrequency());
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
			}

			/*- else {
				removeUnusedMonitorPage();
				List<AhDashboard> lst = QueryUtil.executeQuery(AhDashboard.class, null,
						new FilterParams("daType=:s1",
								new Object[]{AhDashboard.DASHBOARD_TYPE_REPORT}),getDomain().getId(), this);
				if (!lst.isEmpty()) {
					for(AhDashboard da: lst){
						if(da.getDefaultFlag()){
							dafaultTabId = da.getId().toString();
						}
						if(da.isActive()) {
							setSessionDataSource(da);
							getDataSource().setTz(tz);
						}
					}
				} else {
					//AhDashboard da = createDefaultDashBoard();
				//					dafaultTabId = da.getId().toString();
				//					setSessionDataSource(da);
				//					getDataSource().setTz(tz);
				}
				initGroup();
				return SUCCESS;
				} */else {
					List<AhDashboard> das = QueryUtil.executeQuery(AhDashboard.class, null, new FilterParams("defaultFlag=:s1 and daType=:s2",
							new Object[]{true, AhDashboard.DASHBOARD_TYPE_REPORT}),getDomain().getId());
					if (!das.isEmpty() && das.size()==1) {
						removeDaLayouts(String.valueOf(das.get(0).getId()), null);
						QueryUtil.removeBo(AhDashboard.class, das.get(0).getId());
					}
					if (das.isEmpty()) {
						createDefaultTemplate();
					}
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	private void createDefaultTemplate(List<Integer> lstWidget, String deDaName, String desc) {
		try {
			AhDashboard da = new AhDashboard();
			da.setOwner(getDomain());
			da.setActive(false);
			da.setDefaultFlag(true);
			da.setDashName(deDaName);
			da.setUserName("");
			da.setDescription(desc);
			da.setDaType(AhDashboard.DASHBOARD_TYPE_REPORT);
			da.setReportScheduleStatus(AhDashboard.REPORT_STATUS_UNSCHEDULED);
			da.setReEmailAddress("");
			QueryUtil.createBo(da);
			
			List<DashboardComponent> daList = QueryUtil.executeQuery(DashboardComponent.class, null, 
					new FilterParams("key in (:s1) and defaultFlag=:s2",new Object[]{lstWidget, true}), null,this);
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
			
			if (!result.isEmpty()) {
				QueryUtil.restoreBulkCreateBos(result);
			}
		} catch (Exception e) {
			log.error(e);
		}
		
	}
	
	private void createDefaultTemplate(){
		List<Integer> lstWidget = new ArrayList<Integer>();
		lstWidget.add(42);
		lstWidget.add(28);
		lstWidget.add(47);
		lstWidget.add(70);
		lstWidget.add(74);
		//lstWidget.add(11);
		lstWidget.add(1);
		String deDaName = "Network Summary";
		String desc = "At-a-glance view of your network activity by user, client, bandwidth, and device";
		createDefaultTemplate(lstWidget,deDaName,desc);

		lstWidget.clear();
		lstWidget.add(8);
		lstWidget.add(19);
		lstWidget.add(20);
		lstWidget.add(21);
		deDaName = "System Summary";
		desc = "At-a-glance view of your system statistics, including version, security, active devices, and audit logs";
		createDefaultTemplate(lstWidget,deDaName,desc);
	}

	private List<Long> getSelectedReportIds() {
		if (allItemsSelected) {
			List<?> ids = QueryUtil.executeQuery("select id from "
					+ AhDashboard.class.getCanonicalName(), null, new FilterParams("daType", AhDashboard.DASHBOARD_TYPE_REPORT), domainId);
			List<Long> selectedIds = new ArrayList<Long>(ids.size());
			for (Object obj : ids) {
				selectedIds.add((Long) obj);
			}
			return selectedIds;
		} else {
			Set<Long> allSelectedIds = getAllSelectedIds();
			List<Long> sIds = new ArrayList<Long>();
			if (null != allSelectedIds) {
				sIds.addAll(allSelectedIds);
			}
			return sIds;
		}
	}

	private void enableReportOperation(boolean enable) throws Exception {
		List<Long> ids = getSelectedReportIds();
		int count = 0;
		if (ids.size() > 0) {
			int status = enable ? AhDashboard.REPORT_STATUS_SCHEDULED
					: AhDashboard.REPORT_STATUS_DISABLED;
			int currentStatus = enable ? AhDashboard.REPORT_STATUS_DISABLED
					: AhDashboard.REPORT_STATUS_SCHEDULED;
			String set = "reportScheduleStatus = :s1";
			String where = "id in (:s2) and reportScheduleStatus = :s3";
			Object[] bindings = new Object[] { status, ids, currentStatus };
//			String where = "id in (:s2) and defaultFlag = :s3 and reportScheduleStatus = :s4";
//			Object[] bindings = new Object[] { status, ids, false, currentStatus };
			count = QueryUtil.updateBos(AhDashboard.class, set, where,
					bindings, domainId);
		}
		if (enable) {
			if (count > 1) {
				addActionMessage(MgrUtil.getUserMessage("info.objectsEnabled",
						NmsUtil.convertNumToEnglish(count,true)));
			} else if (count == 1) {
				addActionMessage(MgrUtil.getUserMessage("info.objectEnabled"));
			} else {
				addActionMessage(MgrUtil
						.getUserMessage("info.noObjectsEnabled"));
			}
		} else {
			if (count > 1) {
				addActionMessage(MgrUtil.getUserMessage("info.objectsDisabled",
						NmsUtil.convertNumToEnglish(count, true)));
			} else if (count == 1) {
				addActionMessage(MgrUtil.getUserMessage("info.objectDisabled"));
			} else {
				addActionMessage(MgrUtil
						.getUserMessage("info.noObjectsDisabled"));
			}
		}
		allItemsSelected = false;
		setAllSelectedIds(null);
	}

	private void prepareDependentObjects() throws Exception {
		initGroup();
		// TODO Auto-generated method stub
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setDataSource(AhDashboard.class);
		setSelectedL2Feature(L2_FEATURE_REPORT_RECURREPORT);
		enableSorting();
		// default sorting
		if (sortParams.getOrderBy().equals("id")) {
			sortParams.setPrimaryOrderBy("defaultFlag");
			sortParams.setPrimaryAscending(false);
		}
		keyColumnId = COLUMN_REPORT_NAME;
		tableId = HmTableColumn.TABLE_REPORT_RECUR;
		filterParams = new FilterParams("daType", AhDashboard.DASHBOARD_TYPE_REPORT);
	}

	protected void updateSortParams() {
		super.updateSortParams();
		sortParams.setPrimaryOrderBy("defaultFlag");
		sortParams.setPrimaryAscending(false);
	}

	@Override
	public AhDashboard getDataSource() {
		return (AhDashboard) dataSource;
	}

	public boolean getHasPredefinedReport() {
		boolean has = false;
		if (null != getPage()) {
			for (Object object : getPage()) {
				AhDashboard item = (AhDashboard) object;
				if (item.getDefaultFlag()) {
					has = true;
					break;
				}
			}
		}
		return has;
	}

	public boolean getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? true : false;
		}
		return true;
	}

	@SuppressWarnings({ "unchecked", "unused" })
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
				MgrUtil.setSessionAttribute(MONITOR_TAB_ID_USERAPPLICATION, null);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	public void prepareJsonGUIData() throws JSONException{
		super.prepareJsonGUIData();
		jsonObject.put("tmtp", getDataSource().getRefrequency());
//		jsonObject.put("tmSchedule", getDataSource().isReEnabledScheduleCheckbox());
	}


	public void prepareWidgetConfigs() {
		widgetConfigs = new HashMap<Integer, List<WidgetConfig>>();
		if (columnsConfig != null
				&& columnsConfig.length > 0) {
			int chartLen = 0;
			if (columnCharts != null) chartLen = columnCharts.length;
			Integer columnOrder = 0;
			int paramPos;
			for (int i = 0; i < columnsConfig.length; i++) {
				if (i < chartLen) {
					String chartsTmpStr = columnCharts[i];
					if (!StringUtils.isBlank(chartsTmpStr)) {
						String[] chartsTmp = chartsTmpStr.split(",");
						if (chartsTmp != null
								&& chartsTmp.length > 0) {
							widgetConfigs.put(columnOrder, new ArrayList<WidgetConfig>());
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
										widgetConfig.checked = Boolean.valueOf(configsTmp[paramPos]);
									}
									widgetConfigs.get(columnOrder).add(widgetConfig);
								}
							}
						}
					}
					columnOrder++;
				}
			}
		}
		//createNewWidgetConfigForDefaultClonedWidget();
	}


	protected boolean removeOperation() throws Exception {
		if (!"remove".equals(operation)) {
			return false;
		}
		int count = -1;
		boolean hasRemoveDefaultValue = false;
		Collection<Long> idsToRemove = new ArrayList<>();
		if (allItemsSelected) {
			setAllSelectedIds(null);
			this.getSessionFiltering();

			idsToRemove = getHomeDataInHomeDomainForRecurReport(this.boClass);
			if (getShowDomain()) {
				addActionMessage(MgrUtil
						.getUserMessage(OBJECT_IS_NONHOME_DOMAIN_VALUE));
			} else {
				Collection<Long> defaultIds = getDefaultIds();
				idsToRemove.removeAll(defaultIds);
				if (null != defaultIds && !defaultIds.isEmpty()) {
					hasRemoveDefaultValue = true;
					addActionMessage(MgrUtil
							.getUserMessage(OBJECT_IS_DEFAULT_VALUE));
				}
			}
		} else if (this.getAllSelectedIds() != null && !this.getAllSelectedIds().isEmpty()) {
			Collection<Long> defaultIds = getDefaultIds();
			if (defaultIds != null && this.getAllSelectedIds().removeAll(defaultIds)) {
				hasRemoveDefaultValue = true;
				addActionMessage(MgrUtil
						.getUserMessage(OBJECT_IS_DEFAULT_VALUE));
			}
			idsToRemove = new ArrayList<Long>(this.getAllSelectedIds());

			setAllSelectedIds(null);
		}

		log.info("removeOperation", "Count: " + count);
		if (!idsToRemove.isEmpty()) {
			for (Long daId : idsToRemove) {
				this.removeDaLayouts(String.valueOf(daId), null);
			}
			count = removeBos(boClass, idsToRemove);
		}

		if (count < 0) {
			addActionMessage(MgrUtil.getUserMessage(SELECT_OBJECT));
		} else if (count == 0) {
			addActionMessage(MgrUtil.getUserMessage(NO_OBJECTS_REMOVED));
		} else if (count == 1) {
			if (hasRemoveDefaultValue) {
				addActionMessage(MgrUtil
						.getUserMessage(OBJECT_REMOVED_WITH_DEFAULT));
			} else {
				addActionMessage(MgrUtil.getUserMessage(OBJECT_REMOVED));
			}
		} else {
			if (hasRemoveDefaultValue) {
				addActionMessage(MgrUtil.getUserMessage(
						OBJECTS_REMOVED_WITH_DEFAULT, count + ""));
			} else {
				addActionMessage(MgrUtil.getUserMessage(OBJECTS_REMOVED, count
						+ ""));
			}
		}

		return true;
	}

	private Collection<Long> getHomeDataInHomeDomainForRecurReport (
			Class<? extends HmBo> hmBoClass) throws Exception {
		domainId = QueryUtil.getDependentDomainFilter(userContext);

		Collection<Long> toRemoveIds = new ArrayList<Long>();
		List<?> boIds = QueryUtil.executeQuery("select id, owner.id from "
				+ hmBoClass.getSimpleName(), null, null);
		for (Object obj : boIds) {
			Object[] item = (Object[]) obj;
			if (domainId.equals(item[1])) {
				toRemoveIds.add((Long) item[0]);
			}
		}
		return toRemoveIds;
	}
	public int getMenuId() {
		return NavigationCustomizationUtil
				.getMenuIdByName(Navigation.L1_FEATURE_REPORT);
	}

	@Override
	public String getSlideStyleStatus() {// collapsed
		if ("new".equals(operation)
				|| "edit".equals(operation)
				|| "clone".equals(operation)) {
			return "collapsed";
		} else {
			return super.getSlideStyleStatus();
		}
	}

}