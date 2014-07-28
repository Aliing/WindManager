package com.ah.ui.actions.monitor;

/*
 * @author Chris Scheers
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.ah.bo.mgmt.FilterParams;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.Tracer;

public class AhTrapsAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(AhTrapsAction.class
			.getSimpleName());

	protected void saveFilter() {
		if (severity > 0) {
			log.info("execute", "Filter by severity: " + severity);
			if (apId != null && apId.length() > 0) {
				log.info("execute", "Filter by AP ID: " + apId);
				filterParams = new FilterParams(
						"severity = :s1 and lower(apId) like :s2", new Object[] {
								severity, '%'+ apId.toLowerCase() + '%' });
			} else {
				filterParams = new FilterParams("severity", severity);
			}
		} else if (apId != null && apId.length() > 0) {
			log.info("execute", "Filter by AP ID: " + apId);
			filterParams = new FilterParams("lower(apId) like :s1",
					new Object[] {'%'+ apId.toLowerCase() + '%' });
		}
		setSessionFiltering();
	}

	protected void restoreFilter() {
		if (filterParams == null) {
			return;
		}
		if (filterParams.getValue() != null) {
			severity = (Short) filterParams.getValue();
		} else if (filterParams.getWhere() != null) {
			if (filterParams.getBindings().length == 2) {
				severity = (Short) filterParams.getBindings()[0];
				apId = (String) filterParams.getBindings()[1];
			} else {
				apId = (String) filterParams.getBindings()[0];
			}
			apId = apId.substring(1, apId.length() - 1); // remove '%'
		}
	}

	protected int cacheId;

	protected short severity = -1;
	
	protected String filterVHM;

protected String apId = "";
	
	protected String component = "";
	
	protected String beginDate = "";
	
	protected String endDate = "";
	
	protected String beginTimeH = "00";
	
	protected String endTimeH = "23";
	
	protected String filterName = "";
	
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
	
	private SimpleDateFormat dFormatD = new SimpleDateFormat("yyyy-MM-dd");
	
	private SimpleDateFormat dFormatH = new SimpleDateFormat("HH");
	
	public static EnumItem[] ENUM_HOURS = enumItems("enum.hours.", 24, "hr");

	private static EnumItem[] enumItems(String prefix, int len, String type) {
		EnumItem[] enumItems = new EnumItem[len];
		for (int i = 0; i < len; i++) {
			String tmp = String.valueOf(i);
			if (tmp.length() == 1)
				tmp = "0" + tmp;
			tmp = tmp + type;
			enumItems[i] = new EnumItem(i, tmp);
		}
		return enumItems;
	}

	public EnumItem[] getEnumHours() {
		return ENUM_HOURS;
	}

	public void setSeverity(short severity) {
		this.severity = severity;
	}

	public short getSeverity() {
		return severity;
	}

	public String getApId() {
		return apId;
	}

	public void setApId(String apId) {
		this.apId = apId;
	}

	public int getCacheId() {
		return cacheId;
	}

	public void setCacheId(int cacheId) {
		this.cacheId = cacheId;
	}

	public String getFilterVHM()
	{
		return filterVHM;
	}

	public void setFilterVHM(String filterVHM)
	{
		this.filterVHM = filterVHM;
	}
	
	public String getComponent(){
		return this.component;
	}
	
	public void setComponent(String component){
		this.component = component;
	}
	
	public String getBeginDate(){
		return this.beginDate;
	}
	
	public void setBeginDate(String beginDate){
		this.beginDate = beginDate;
	}
	
	public String getBeginTimeH(){
		return this.beginTimeH;
	}
	
	public void setBeginTimeH(String beginTimeH){
		this.beginTimeH = beginTimeH;
	}
	
	public String getEndDate(){
		return this.endDate;
	}
	
	public void setEndDate(String endDate){
		this.endDate = endDate;
	}
	
	public String getEndTimeH(){
		return this.endTimeH;
	}
	
	public void setEndTimeH(String endTimeH){
		this.endTimeH = endTimeH;
	}
	
	public long getStartTimeS(){
		if(beginDate == null || "".equals(beginDate)){
			return -1;
		}
		String startDateStr = beginDate + "/" + 
			((beginTimeH.length()<2) ? "0"+beginTimeH: beginTimeH) + ":00:00";
		try{
			return dFormat.parse(startDateStr).getTime()/1000;
		}catch(ParseException ex){
			log.info("search", "Error start date format: " + startDateStr);
			return -1;
		}
	}
	
	public long getStartTime(){
		long time = this.getStartTimeS();
		return time < 0 ? -1 : time * 1000;
	}
	
	public long getEndTimeS(){
		if(endDate == null || "".equals(endDate)){
			return -1;
		}
		String endDateStr = endDate + "/" + 
			((endTimeH.length()<2) ? "0"+endTimeH : endTimeH) + ":00:00";
		try{
			return dFormat.parse(endDateStr).getTime()/1000;
		}catch(ParseException ex){
			log.info("search", "Error start date format: " + endDateStr);
			return -1;
		}
	}
	
	public long getEndTime(){
		long time = this.getEndTimeS();
		return time < 0 ? -1 : time * 1000;
	}
	
	public String getFilterName(){
		return this.filterName;
	}
	
	public void setFilterName(String filterName){
		this.filterName = filterName;
	}

	public boolean getHide4Setting() {
		if (getIsInHomeDomain()) {
			if (writePermission) {
				return false;
			}
		}

		return true;
	}
	
	public String getDateStr(long time){
		if(time < 0){
			return "";
		}
		return dFormatD.format(time);
	}
	
	public String getHourStr(long time){
		if(time < 0){
			return "00";
		}
		return dFormatH.format(time);
	}

}