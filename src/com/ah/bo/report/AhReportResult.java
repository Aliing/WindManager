package com.ah.bo.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.util.bo.report.AhReportRequest;

public class AhReportResult {
	private AhReportElement reportEl;
	private AhReportRequest request;
	private JSONObject userMessage;
	private Map<String, AhReportElement> groupReportEls;

	
	public JSONArray getJSONData() throws JSONException {
		JSONArray jsonArray = new JSONArray();
		
		// add the main/default/total one
		jsonArray.put(getSingleJSONData(this.reportEl));
		
		return jsonArray;
	}
	
	public List<JSONArray> getGroupJSONData()  throws JSONException {
		List<JSONArray> jsonArrayLst = new ArrayList<JSONArray>();
		// add other group reports, for example, each kind of radios
		if (this.groupReportEls != null
				&& !this.groupReportEls.isEmpty()) {
			for (AhReportElement rpElTmp : this.groupReportEls.values()) {
				JSONArray jsonArray = new JSONArray();
				jsonArray.put(getSingleJSONData(rpElTmp));
				jsonArrayLst.add(jsonArray);
			}
		}
		
		return jsonArrayLst;
	}
	
	public JSONObject getSingleJSONData(AhReportElement rpElTmp) throws JSONException {
		JSONObject jObjInfo = new JSONObject();
		
		if (rpElTmp != null
				&& this.getRequest() != null) {
			// encap report data here
			JSONObject jObjGroupInfo = new JSONObject();
			jObjInfo.put("groupInfo", jObjGroupInfo);
			jObjGroupInfo.put("reportId", rpElTmp.getId());
			jObjGroupInfo.put("grpMark", rpElTmp.getGrpMark());
			
			JSONObject jObjDesc = new JSONObject();
			jObjInfo.put("desc", jObjDesc);
			jObjDesc.put("id", rpElTmp.getId());
			jObjDesc.put("title", rpElTmp.getTitle());
			if (rpElTmp.getStartTime() != null
					&& rpElTmp.getEndTime() != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy, h:mma");
				dateFormat.setTimeZone(this.getRequest().getTimeZone());
				String startTimeStr = dateFormat.format(new Date(rpElTmp.getStartTime()));
				String endTimeStr = dateFormat.format(new Date(rpElTmp.getEndTime()));
				Calendar ca = Calendar.getInstance(this.getRequest().getTimeZone());
				jObjDesc.put("timeOffset", ca.get(Calendar.ZONE_OFFSET) + ca.get(Calendar.DST_OFFSET));
				jObjDesc.put("subTitle", "Report Period: " + startTimeStr + " - " + endTimeStr);
				jObjDesc.put("starttime", rpElTmp.getStartTime());
				jObjDesc.put("endtime", rpElTmp.getEndTime());
			} else {
				jObjDesc.put("subTitle", rpElTmp.getSubTitle());
			}
			jObjDesc.put("summary", getFormattedSummary(rpElTmp.getSummary()));
			if (!StringUtils.isBlank(rpElTmp.getValueTitle())) {
				jObjDesc.put("yTitle", rpElTmp.getValueTitle());
			}
			
			
			boolean blnPairDataSeries = false;
			// encap series data information
			if (rpElTmp.getSeries() != null
					&& !rpElTmp.getSeries().isEmpty()) {
				JSONArray jaSeries = new JSONArray();
				jObjInfo.put("series", jaSeries);
				int curColorIdx = 0;
				int colorsLen = AhReportProperties.SERIES_ALL_COLORS.length;
				for (AhSeries aSeries : rpElTmp.getSeries()) {
					if (!blnPairDataSeries 
							&& aSeries.getPairData() != null) {
						blnPairDataSeries = true;
					}
					JSONObject jObjDataTmp = new JSONObject();
					jObjDataTmp.put("id", aSeries.getId());
					jObjDataTmp.put("name", aSeries.getName());
					jObjDataTmp.put("type", aSeries.getShowType());
					if (!StringUtils.isBlank(aSeries.getCustomColor())) {
						jObjDataTmp.put("color", aSeries.getCustomColor());
					} else {
						jObjDataTmp.put("color", AhReportProperties.SERIES_ALL_COLORS[curColorIdx++%colorsLen]);
					}
					jObjDataTmp.put("summary", aSeries.getSummarys());
					if (!StringUtils.isBlank(aSeries.getStackGroupString())) {
						jObjDataTmp.put("stack", aSeries.getStackGroupString());
					}
					if (blnPairDataSeries) {
						JSONArray jaDtData = new JSONArray();
						jObjDataTmp.put("data", jaDtData);
						List<AhSeriesData> seriesDataTmp = aSeries.getPairData();
						if (seriesDataTmp != null
								&& !seriesDataTmp.isEmpty()) {
							for (AhSeriesData dataTmp : seriesDataTmp) {
								JSONArray jaDtDataTmp = new JSONArray();
								jaDtData.put(jaDtDataTmp);
								jaDtDataTmp.put(dataTmp.getName());
								jaDtDataTmp.put(dataTmp.getValue());
							}
						}
						
					} else {
						jObjDataTmp.put("data", aSeries.getSimpleData());
					}
					jaSeries.put(jObjDataTmp);
				}
			}
			
			if (rpElTmp.isBlnCategoriesSet()) {
				if (rpElTmp.getCategories() != null
						&& !rpElTmp.getCategories().isEmpty()) {
					JSONObject jObjCategories = new JSONObject();
					jObjInfo.put("categories", jObjCategories);
					jObjCategories.put("data", rpElTmp.getJudgedCategories());
				}
			}
			
		}
		
		if (this.userMessage != null) {
			jObjInfo.put("userMessage", this.userMessage);
		}
		
		return jObjInfo;
	}
	
	public JSONArray getFailedJSONData() throws JSONException {
		return getFailedJSONData(null);
	}
	
	public JSONArray getFailedJSONData(String errorInfo) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		JSONObject jObjInfo = new JSONObject();
		jsonArray.put(jObjInfo);
		
		if (this.getReportEl() != null
				&& this.getRequest() != null) {
			AhReportElement rpElTmp = this.getReportEl();
			JSONObject jObjResultStatus = new JSONObject();
			jObjInfo.put("resultStatus", jObjResultStatus);
			jObjResultStatus.put("status", false);
			String errInfoTmp = "Failed to calculate this report.";
			if (!StringUtils.isBlank(errorInfo)) {
				errInfoTmp = errorInfo;
			}
			jObjResultStatus.put("errInfo", errInfoTmp);
			jObjResultStatus.put("errCode", AhReportProperties.ERROR_CODE_CALCULATE_EXCEPTION);
			
			// encap report data here
			JSONObject jObjGroupInfo = new JSONObject();
			jObjInfo.put("groupInfo", jObjGroupInfo);
			jObjGroupInfo.put("reportId", rpElTmp.getId());
			jObjGroupInfo.put("grpMark", rpElTmp.getGrpMark());
			
			JSONObject jObjDesc = new JSONObject();
			jObjInfo.put("desc", jObjDesc);
			jObjDesc.put("id", rpElTmp.getId());
			jObjDesc.put("title", rpElTmp.getTitle());
			if (rpElTmp.getStartTime() != null
					&& rpElTmp.getEndTime() != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy, h:mma");
				dateFormat.setTimeZone(this.getRequest().getTimeZone());
				String startTimeStr = dateFormat.format(new Date(rpElTmp.getStartTime()));
				String endTimeStr = dateFormat.format(new Date(rpElTmp.getEndTime()));
				Calendar ca = Calendar.getInstance(this.getRequest().getTimeZone());
				jObjDesc.put("timeOffset", ca.get(Calendar.ZONE_OFFSET) + ca.get(Calendar.DST_OFFSET));
				jObjDesc.put("subTitle", "Report Period: " + startTimeStr + " - " + endTimeStr);
			} else {
				jObjDesc.put("subTitle", rpElTmp.getSubTitle());
			}
			jObjDesc.put("summary", getFormattedSummary(rpElTmp.getSummary()));
			if (!StringUtils.isBlank(rpElTmp.getValueTitle())) {
				jObjDesc.put("yTitle", rpElTmp.getValueTitle());
			}
		}
		
		return jsonArray;
	}
	
	private String getFormattedSummary(String str) {
		if (!StringUtils.isBlank(str)) {
			return "<b>Summary</b><br>" + str;
		}
		
		return "";
	}
	
	public AhReportElement getReportEl() {
		return reportEl;
	}
	public void setReportEl(AhReportElement reportEl) {
		this.reportEl = reportEl;
	}
	public AhReportRequest getRequest() {
		return request;
	}
	public void setRequest(AhReportRequest request) {
		this.request = request;
	}

	public JSONObject getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(JSONObject userMessage) {
		this.userMessage = userMessage;
	}


	public Map<String, AhReportElement> getGroupReportEls() {
		return groupReportEls;
	}


	public void setGroupReportEls(Map<String, AhReportElement> groupReportEls) {
		this.groupReportEls = groupReportEls;
	}
	
}
