package com.ah.ui.actions.monitor;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.service.ApplicationService;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.Application;
import com.ah.common.async.Response;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.bo.dashboard.DaHelper;
import com.ah.util.bo.dashboard.ReportDataRequestUtil;
import com.ah.util.bo.dashboard.ReportDataRequestUtil.RequestDataModifier;
import com.ah.util.bo.dashboard.ReportTestDataGen;
import com.ah.util.bo.dashboard.RequestData;

public class ReportDataAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	private static final Tracer	log	= new Tracer(ReportDataAction.class.getSimpleName());
	
	@SuppressWarnings("rawtypes")
	public String execute() throws Exception {
		tz = this.getUserTimeZone();
		
		try {
			if ("data".equals(operation)) {
				jsonArray = new JSONArray();
				JSONObject jobj = new JSONObject();
				jsonArray.put(jobj);
				JSONObject jrs = new JSONObject();
				jobj.put("rs", jrs);
				jrs.put("resultStatus", true);
				
				RequestData data = checkReportRequest();
				if (data == null) {
					jrs.put("resultStatus", false);
					jrs.put("errMsg", this.errMsg);
					return "json";
				} else {
					String dataError = data.checkErrors();
					if (dataError != null) {
						jrs.put("resultStatus", false);
						jrs.put("errMsg", dataError);
						return "json";
					}
				}
				prepareAdditionalRequestDataField(data);
				prepareAdditionalInfoForDataReturned(jobj, data);
				//prepare additional info for html widget
				if ("html".equals(data.getRenderType())) {
					prepareHtmlAdditionalFields(data, jobj);
				}
				
				String checkErrorStr = null;
				if (!forTest) {
					checkErrorStr = checkWhetherToRequestForBackend(data, this.getDomain().getId());
					if (checkErrorStr == null) {
						try {
							ReportDataRequestUtil.requestForDashboard(data.formatData().getData(), 
									new Response.Handler(){
										public void responded (Response response) {
											blnDataReturned = true;
											curResponse = response;
										}
									}
							);
						} catch (Exception e) {
							log.error("ReportDataAction: Failed to request report data from back end.", e);
							jrs.put("resultStatus", false);
							jrs.put("errMsg", "Failed to request data.");
							return "json";
						}
						int iCount = 0;
						while (!blnDataReturned && iCount++ < 120) {
							Thread.sleep(500);
							continue;
						}
					}
				} else {
					jobj.put("result", 0);
					jobj.put("exception", "");
					jobj.put("data", getFakeDataForTestReturn(data));
					return "json";
				}
				
				if (curResponse != null) {
					log.warn("response from report back end(result): ", String.valueOf(curResponse.result));
					log.warn("response from report back end(exception): ", curResponse.exception);
					String additionalException = curResponse.exception;
					if (!DaHelper.isDataReturnedFromBackendNull(curResponse.data)) {
						log.warn("response from report back end(data): ", curResponse.data.toString());
					} else {
						additionalException = checkWhenNoDataReturned(data);
						log.warn("no data of response returned from report back end");
					}
					jobj.put("result", curResponse.result);
					jobj.put("exception", getCertainExceptionMessageForBackend(curResponse.exception));
					jobj.put("exception1", additionalException);
					jobj.put("data", curResponse.data);
				} else {
					jrs.put("resultStatus", false);
					jrs.put("errMsg", "No response for data request.");
				}
				
				return "json";
			}
			return null;
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			e.printStackTrace();
			log.error("ReportDataAction: Failed to request report data for charts.", e);
			jsonObject = new JSONObject();
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", e.getMessage());
			return "json";
		}
	}
	
	private String checkWhetherToRequestForBackend(RequestData data, Long domainId) {
		AhDashboard daTmp = this.currentDa;
		if (daTmp == null) {
			daTmp = QueryUtil.findBoById(AhDashboard.class, data.getDaId());
		}
		if (daTmp != null && daTmp.getDaType()!= AhDashboard.DASHBOARD_TYPE_REPORT) {
			String[] ret = DashboardAction.checkToChangeAppTimeSelectPeriod(daTmp, domainId);
			if (StringUtils.isNotBlank(ret[1])) {
				return ret[1];
			}
		}
		
		return null;
	}
	
	private String getCertainExceptionMessageForBackend(String exception) {
		if (StringUtils.isNotBlank(exception)) {
			return MgrUtil.getUserMessage("exception.report.back.end." + exception);
		}
		return exception;
	}
	
	private void prepareAdditionalInfoForDataReturned(JSONObject jobj, RequestData data) throws JSONException {
		if (jobj == null
				|| data == null) {
			return;
		}
		JSONObject jObjDesc = null;
		if (!jobj.has("desc") 
				|| jobj.getJSONObject("desc") == null) {
			jObjDesc = new JSONObject();
			jobj.put("desc", jObjDesc);
		} else {
			jObjDesc = jobj.getJSONObject("desc");
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy, h:mma");
		dateFormat.setTimeZone(this.tz);
		long endTimeTmp;
		long startTimeTmp;
		boolean blnSpecialPeriod = false;
		if (data.isBlnAlwaysLastHour()) {
			endTimeTmp = System.currentTimeMillis();
			startTimeTmp = endTimeTmp - 3600000; 
			blnSpecialPeriod = true;
		} else {
			endTimeTmp = data.getEndTime();
			startTimeTmp = data.getStartTime();
		}
		String startTimeStr = dateFormat.format(new Date(startTimeTmp));
		String endTimeStr = dateFormat.format(new Date(endTimeTmp));
		Calendar ca = Calendar.getInstance(this.tz);
		jObjDesc.put("timeOffset", ca.get(Calendar.ZONE_OFFSET) + ca.get(Calendar.DST_OFFSET));
		if (DaHelper.isCurrentTimeWidget(data.getComponentKey())) {
			blnSpecialPeriod = true;
			jObjDesc.put("subTitle", " ");
		} else {
			jObjDesc.put("subTitle", "Report Period: " + startTimeStr + " - " + endTimeStr);
		}
		jObjDesc.put("starttime", data.getStartTime());
		jObjDesc.put("endtime", data.getEndTime());
		jObjDesc.put("rqtime", System.currentTimeMillis());
		jObjDesc.put("dc", data.getCurrentDeviceCount());
		jObjDesc.put("sample", data.getSample());
		jObjDesc.put("specialPeriod", blnSpecialPeriod);
	}
	
	private void prepareHtmlAdditionalFields(RequestData data, JSONObject jobj) throws JSONException {
		if (data == null
				|| jobj == null) {
			return;
		}
		
		if (data.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_CLIENT) {
			jobj.put("ClientDeviceMAC", data.getClientDeviceMAC());
		} else if  (data.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_APP) {
			jobj.put("application", data.getApplication());
		} else if  (data.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_USER) {
			jobj.put("UserName", data.getUserName());
		}
		
		jobj.put("htmlText", data.getHtmlContent());
	}
	
	private void prepareAdditionalRequestDataField(RequestData data) {
		ReportDataRequestUtil.encapRealRequestDataRequestId(data, this.getGrpTimestamp());
	}
	
	public static String getFakeDataForTestReturn_static(RequestData data, String testCode) {
		ReportDataAction report = new ReportDataAction();
		report.setTestCode(testCode);
		return report.getFakeDataForTestReturn(data);
	}
	
	private String getFakeDataForTestReturn(RequestData data) {
		return new ReportTestDataGen(this.testCode).getFakeDataForTestReturn(data);
	}
	
	private AhDashboard currentDa = null;
	private RequestData checkReportRequest() {
		if (this.widgetId == null && this.reportId == null) {
			errMsg = "Illegal request arguments.";
			return null;
		}
		
		if (this.daId <= 0) {
			this.daId = null;
		}
		
		RequestDataModifier modifier = new RequestDataModifier() {
			public void modify(AhDashboardWidget widget, AhDashboard da) {
				// do not override the configuration of drill down kind of dashboard
				if (da != null
						&& da.getDaType() == AhDashboard.DASHBOARD_TYPE_DRILLDAWN) {
					return;
				}
				if (StringUtils.isNotBlank(getWidgetChecked())) {
					widget.setBlnChecked(Boolean.valueOf(getWidgetChecked()));
				}
				// only when check for widget, set those arguments for widget
				if (!widget.isBlnChecked()) {
					return;
				}
//				if (StringUtils.isNotBlank(getLocationId())) {
//					widget.setLocation(QueryUtil.findBoById(MapContainerNode.class, Long.parseLong(getLocationId())));
//				}
				if (StringUtils.isNotBlank(getObId())) {
					widget.setObjectId(getObId());
				}
				if (StringUtils.isNotBlank(getObType())) {
					widget.setObjectType(getObType());
				}
				if (StringUtils.isNotBlank(getFobId())) {
					widget.setFilterObjectId(getFobId());
				}
				if (StringUtils.isNotBlank(getFobType())) {
					widget.setFilterObjectType(getFobType());
				}
				if (StringUtils.isNotBlank(getTimeType())) {
					widget.setSelectTimeType(Integer.parseInt(getTimeType()));
					
					if (AhDashboard.TAB_TIME_CUSTOM == widget.getSelectTimeType()) {
						if (StringUtils.isNotBlank(getStartTime())
								&& StringUtils.isNotBlank(getStartHour())) {
							widget.setCustomStartTime(getConvertCustomTime(getStartTime(), Integer.parseInt(getStartHour())));
						}
						if (StringUtils.isNotBlank(getEndTime())
								&& StringUtils.isNotBlank(getEndHour())) {
							widget.setCustomEndTime(getConvertCustomTime(getEndTime(), Integer.parseInt(getEndHour())));
						}
						if (StringUtils.isNotBlank(getEnableTimeLocal())) {
							widget.setEnableTimeLocal(Boolean.valueOf(getEnableTimeLocal()));
						}
					}
				}
			}
			
			public void modify(AhDashboard da, boolean blnAnyway) {
				// if check for widget, do not set for dashboard
				if (!blnAnyway) {
					return;
				}
				if (da.isBlnNullNewDa()) {
					if ("da".equals(daType)) {
						da.setDaType(AhDashboard.DASHBOARD_TYPE_DASH);
					} else if ("report".equals(daType)) {
						da.setDaType(AhDashboard.DASHBOARD_TYPE_REPORT);
					}
				}
				if (!(StringUtils.isNotBlank(getWidgetChecked())
						&& Boolean.valueOf(getWidgetChecked()))) {
					return;
				}
//				if (StringUtils.isNotBlank(getLocationId())) {
//					da.setLocation(QueryUtil.findBoById(MapContainerNode.class, Long.parseLong(getLocationId())));
//				}
				if (StringUtils.isNotBlank(getObId())) {
					da.setObjectId(getObId());
				}
				if (StringUtils.isNotBlank(getObType())) {
					da.setObjectType(getObType());
				}
				if (StringUtils.isNotBlank(getFobId())) {
					da.setFilterObjectId(getFobId());
				}
				if (StringUtils.isNotBlank(getFobType())) {
					da.setFilterObjectType(getFobType());
				}
				if (StringUtils.isNotBlank(getTimeType())) {
					da.setSelectTimeType(Integer.parseInt(getTimeType()));
					
					if (AhDashboard.TAB_TIME_CUSTOM == da.getSelectTimeType()) {
						if (StringUtils.isNotBlank(getStartTime())
								&& StringUtils.isNotBlank(getStartHour())) {
							da.setCustomStartTime(getConvertCustomTime(getStartTime(), Integer.parseInt(getStartHour())));
						}
						if (StringUtils.isNotBlank(getEndTime())
								&& StringUtils.isNotBlank(getEndHour())) {
							da.setCustomEndTime(getConvertCustomTime(getEndTime(), Integer.parseInt(getEndHour())));
						}
						if (StringUtils.isNotBlank(getEnableTimeLocal())) {
							da.setEnableTimeLocal(Boolean.valueOf(getEnableTimeLocal()));
						}
					}
				}
			}
		};
		
		RequestData data = null;
		AhDashboard daTmp = null;
		if ("report".equals(daType)) {
			daTmp = getSessionRecurReport();
		}
		if (daTmp == null) {
			if (this.widgetId == null) {
				data = ReportDataRequestUtil.prepareDataWithConfigID(this.daId, this.reportId, this.getDomain().getTimeZone(), modifier);
			} else {
				data = ReportDataRequestUtil.prepareDataWithWidgetID(this.daId, this.widgetId, this.getDomain().getTimeZone(), modifier);
			}
		} else {
			if (this.widgetId == null) {
				data = ReportDataRequestUtil.prepareDataWithConfigID(daTmp, this.reportId, this.getDomain().getTimeZone(), modifier);
			} else {
				data = ReportDataRequestUtil.prepareDataWithWidgetID(daTmp, this.widgetId, this.getDomain().getTimeZone(), modifier);
			}
		}
		this.currentDa = daTmp;
		
		if (data == null) {
			errMsg = "Bad configurations.";
			return null;
		}
		
		data.mergeRequestData(this.rpdata);
		
		return data;
	}
	
	private AhDashboard getSessionRecurReport() {
		return (AhDashboard)MgrUtil.getSessionAttribute(AhDashboard.class.getSimpleName() + "Source");
	}
	
	private String checkWhenNoDataReturned(RequestData data) {
		String result = "";
		
		if (DaHelper.isApplicationWatchlistWidgetKey(data.getComponentKey())) {
			List<Application> selectedApp = new ApplicationService()
												.initApplicationMap(this.getUserContext().getDomain())
												.get(ApplicationService.SELECTED_APPLICATION_LIST);
			if (selectedApp == null
					|| selectedApp.size() == 0) {
				return MgrUtil.getUserMessage("hm.dashboard.application.no.monitor.selected");
			}
		}
		
		return result;
	}
	
	private boolean blnDataReturned = false;
	@SuppressWarnings("rawtypes")
	private Response curResponse;
	
	private String errMsg;
	
	private RequestData rpdata;
	private Long widgetId;
	private Long reportId;
	private Long daId;

	public RequestData getRpdata() {
		if (rpdata == null) {
			rpdata = new RequestData();
		}
		return rpdata;
	}

	public void setRpdata(RequestData rpdata) {
		this.rpdata = rpdata;
	}

	public Long getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(Long widgetId) {
		this.widgetId = widgetId;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	
	public Long getDaId() {
		return daId;
	}

	public void setDaId(Long daId) {
		this.daId = daId;
	}
	
	/**
	 * for test start
	 */
	// for test
	// 1: common request
	// 2: return with fixed values
	private String testCode = "1";
	public String getTestCode() {
		return testCode;
	}
	public void setTestCode(String testCode) {
		this.testCode = testCode;
	}
	
	// used to indicate whether to call real report data back end
	private boolean forTest;
	public boolean isForTest() {
		return forTest;
	}
	public void setForTest(boolean forTest) {
		this.forTest = forTest;
	}
	/**
	 * for test end
	 */

	
	private String obId;
	private String obType; //int
	private String locationId;
	private String fobId;
	private String fobType; //int
	
	private String timeType; //int
	private String startTime;
	private String startHour; //int
	private String endTime;
	private String endHour; //int
	private String enableTimeLocal; //boolean
	
	private String widgetChecked;
	
	//values: da, report
	private String daType = "da";
	
	private long grpTimestamp;
	
	private TimeZone tz;
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

	public String getObId() {
		return obId;
	}

	public void setObId(String obId) {
		this.obId = obId;
	}

	public String getObType() {
		return obType;
	}

	public void setObType(String obType) {
		this.obType = obType;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getTimeType() {
		return timeType;
	}

	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStartHour() {
		return startHour;
	}

	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getEndHour() {
		return endHour;
	}

	public void setEndHour(String endHour) {
		this.endHour = endHour;
	}

	public String getEnableTimeLocal() {
		return enableTimeLocal;
	}

	public void setEnableTimeLocal(String enableTimeLocal) {
		this.enableTimeLocal = enableTimeLocal;
	}

	public String getWidgetChecked() {
		return widgetChecked;
	}

	public void setWidgetChecked(String widgetChecked) {
		this.widgetChecked = widgetChecked;
	}

	public String getFobId() {
		return fobId;
	}

	public void setFobId(String fobId) {
		this.fobId = fobId;
	}

	public String getFobType() {
		return fobType;
	}

	public void setFobType(String fobType) {
		this.fobType = fobType;
	}

	public void setDaType(String daType) {
		this.daType = daType;
	}

	public long getGrpTimestamp() {
		return grpTimestamp;
	}

	public void setGrpTimestamp(long grpTimestamp) {
		this.grpTimestamp = grpTimestamp;
	}
	
}
