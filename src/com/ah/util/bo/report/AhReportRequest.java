package com.ah.util.bo.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.ah.bo.HmBo;

public class AhReportRequest implements Cloneable {
	public static final int PERIOD_TYPE_NOT_DEFINED = -1;
	
	private Long id;
	
	private int periodType = PERIOD_TYPE_NOT_DEFINED;
	private int frequencyType;
	
	private String subType;
	
	private boolean blnCustomPeriod;
	private long customPeriod;
	
	private Map<String, Object> customArgs = null;
	
	private Long domainId;
	
	private TimeZone timeZone;
	
	// this is used when you deal with xAxis area
	private boolean blnResetScaleArea;
	private boolean useScaleArea;
	private Long scaleAreaStart;
	private Long scaleAreaEnd;
	
	
	private long startTime;
	private long endTime;
	
	private HmBo dataSource;
	
	public boolean addCustomArg(String key, Object value) {
		if (this.customArgs == null) {
			this.customArgs = new HashMap<String, Object>();
		}
		this.customArgs.put(key, value);
		
		return true;
	}
	public boolean removeCustomArg(String key) {
		if (this.customArgs != null
				&& this.customArgs.containsKey(key)) {
			this.customArgs.remove(key);
		}
		return true;
	}
	public boolean clearCustomArgs() {
		if (this.customArgs != null) {
			this.customArgs.clear();
		}
		return true;
	}
	
	public boolean isPeriodTypeNotDefined() {
		return this.periodType == PERIOD_TYPE_NOT_DEFINED;
	}
	
	public AhReportRequest clone() throws CloneNotSupportedException {
		AhReportRequest rqResult = (AhReportRequest)super.clone();
		return rqResult;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getPeriodType() {
		return periodType;
	}
	public void setPeriodType(int periodType) {
		this.periodType = periodType;
	}
	public boolean isBlnCustomPeriod() {
		return blnCustomPeriod;
	}
	public void setBlnCustomPeriod(boolean blnCustomPeriod) {
		this.blnCustomPeriod = blnCustomPeriod;
	}
	public long getCustomPeriod() {
		return customPeriod;
	}
	public void setCustomPeriod(long customPeriod) {
		this.customPeriod = customPeriod;
	}
	public Map<String, Object> getCustomArgs() {
		return customArgs;
	}
	public void setCustomArgs(Map<String, Object> customArgs) {
		this.customArgs = customArgs;
	}
	public Long getDomainId() {
		return domainId;
	}
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	public TimeZone getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
	public boolean isBlnResetScaleArea() {
		return blnResetScaleArea;
	}
	public void setBlnResetScaleArea(boolean blnResetScaleArea) {
		this.blnResetScaleArea = blnResetScaleArea;
	}
	public boolean isUseScaleArea() {
		return useScaleArea;
	}
	public void setUseScaleArea(boolean useScaleArea) {
		this.useScaleArea = useScaleArea;
	}
	public Long getScaleAreaStart() {
		return scaleAreaStart;
	}
	public void setScaleAreaStart(Long scaleAreaStart) {
		this.scaleAreaStart = scaleAreaStart;
	}
	public Long getScaleAreaEnd() {
		return scaleAreaEnd;
	}
	public void setScaleAreaEnd(Long scaleAreaEnd) {
		this.scaleAreaEnd = scaleAreaEnd;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public int getFrequencyType() {
		return frequencyType;
	}
	public void setFrequencyType(int frequencyType) {
		this.frequencyType = frequencyType;
	}
	public HmBo getDataSource() {
		return dataSource;
	}
	public void setDataSource(HmBo dataSource) {
		this.dataSource = dataSource;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	
	private List<Long> ids;
	private boolean blnGroupCal;
	private List<String> idsWithSubTypes;

	private Map<Long, List<String>> idsSubTypesMap;
	
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
	public boolean isBlnGroupCal() {
		return blnGroupCal;
	}
	public void setBlnGroupCal(boolean blnGroupCal) {
		this.blnGroupCal = blnGroupCal;
	}
	public List<String> getIdsWithSubTypes() {
		return idsWithSubTypes;
	}
	public void setIdsWithSubTypes(List<String> idsWithSubTypes) {
		this.idsWithSubTypes = idsWithSubTypes;
		this.fetchInfoFromIdsAndSubTypes(idsWithSubTypes);
	}
	public Map<Long, List<String>> getIdsSubTypesMap() {
		return idsSubTypesMap;
	}
	
	private void fetchInfoFromIdsAndSubTypes(List<String> idsWithSubTypes) {
		// fetch ids and subTypes from idsWithSubTypes
		if (idsWithSubTypes != null
				&& !idsWithSubTypes.isEmpty()) {
			if (this.ids == null) {
				this.ids = new ArrayList<Long>(idsWithSubTypes.size());
			} else {
				this.ids.clear();
			}
			if (this.idsSubTypesMap == null) {
				this.idsSubTypesMap = new HashMap<Long, List<String>>(idsWithSubTypes.size());
			} else {
				this.idsSubTypesMap.clear();
			}
			for (String idSubType : idsWithSubTypes) {
				String[] idSubTypes = idSubType.split("_", 2);
				String subTypeTmp = null;
				if (idSubTypes.length > 1) {
					subTypeTmp = idSubTypes[1];
				}
				Long idTmp = Long.valueOf(idSubTypes[0]);
				this.ids.add(idTmp);
				if(this.idsSubTypesMap.containsKey(idTmp)) {
					this.idsSubTypesMap.get(idTmp).add(subTypeTmp);
				} else {
					List<String> subTypesTmp = new ArrayList<String>();
					subTypesTmp.add(subTypeTmp);
					this.idsSubTypesMap.put(idTmp, subTypesTmp);
				}
			}
		}
	}
	
	public List<String> getReqSubTypesOfReport(Long reportId) {
		if (this.idsSubTypesMap != null
				&& this.idsSubTypesMap.containsKey(reportId)) {
			return this.idsSubTypesMap.get(reportId);
		}
		return null;
	}
	
	private boolean blnReqDesc;

	public boolean isBlnReqDesc() {
		return blnReqDesc;
	}
	public void setBlnReqDesc(boolean blnReqDesc) {
		this.blnReqDesc = blnReqDesc;
	}
}
