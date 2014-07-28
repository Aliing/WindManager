package com.ah.bo.dashboard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.bo.HmBoBase;
import com.ah.util.TextItem;
import com.ah.util.bo.dashboard.ReportDataRequestUtil;

@Embeddable
public class DashboardComponentData implements Serializable{
	private static final long serialVersionUID = 1L;

	private int groupIndex;
	
	private int positionIndex;
	
	private String sourceData="";
	
	@Column(length = HmBoBase.DEFAULT_LENGTH_255)
	private String displayName="";
	
	@Column(length = HmBoBase.DEFAULT_LENGTH_255)
	private String displayValue="";
	
	@Column(length = HmBoBase.DEFAULT_LENGTH_255)
	private String displayValueKey="";
	
	private short levelBreakDown;
	
	private boolean enableBreakdown;
	
	private boolean validBreakdown;
	
	private boolean enableDisplayTotal;
	
	private String drillDownType="";
	
	private String drillDownValue="";
	
	public static final short DISPLAY_OPTION_DEFAULT = 1;
	public static final short DISPLAY_OPTION_LEVEL_WITH_AGG = 2;
	public static final short DISPLAY_OPTION_LEVEL_WITHOUT_AGG = 3;

	public String getSourceData() {
		return sourceData;
	}

	public void setSourceData(String sourceData) {
		this.sourceData = (sourceData==null?"":sourceData);
	}
	
	public List<TextItem> getSourceDataList(){
		List<TextItem> textItems = new ArrayList<TextItem>();
		/*CheckItem item1 = new CheckItem(1L, "overTime");
		CheckItem item2 = new CheckItem(2L, "bandWidth");
		checkItems.add(item1);
		checkItems.add(item2);*/
		if(sourceData!=null) {
			textItems.add(new TextItem(sourceData, sourceData));
		}
		
		return textItems;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = (displayName==null?"":displayName);
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = (displayValue==null?"":displayValue);
	}
	
	@Transient
	public short getDisplayOption() {
		if(!enableBreakdown
				|| this.levelBreakDown == 0){
			return DISPLAY_OPTION_DEFAULT;
		}else if(enableBreakdown && enableDisplayTotal){
			return DISPLAY_OPTION_LEVEL_WITH_AGG;
		}else if(enableBreakdown && !enableDisplayTotal){
			return DISPLAY_OPTION_LEVEL_WITHOUT_AGG;
		}
		return DISPLAY_OPTION_DEFAULT;
	}

	public short getLevelBreakDown() {
		return levelBreakDown;
	}

	public void setLevelBreakDown(short levelBreakDown) {
		this.levelBreakDown = levelBreakDown;
	}

	public int getGroupIndex() {
		return groupIndex;
	}

	public void setGroupIndex(int groupIndex) {
		this.groupIndex = groupIndex;
	}

	public int getPositionIndex() {
		return positionIndex;
	}

	public void setPositionIndex(int positionIndex) {
		this.positionIndex = positionIndex;
	}
	
	public boolean isEnableBreakdown() {
		return enableBreakdown;
	}

	public void setEnableBreakdown(boolean enableBreakdown) {
		this.enableBreakdown = enableBreakdown;
	}

	public boolean isEnableDisplayTotal() {
		return enableDisplayTotal;
	}

	public void setEnableDisplayTotal(boolean enableDisplayTotal) {
		this.enableDisplayTotal = enableDisplayTotal;
	}

	public String getDisplayValueKey() {
		return displayValueKey;
	}

	public void setDisplayValueKey(String displayValueKey) {
		this.displayValueKey = (displayValueKey==null?"":displayValueKey);
	}

	public boolean isValidBreakdown() {
		return validBreakdown;
	}

	public void setValidBreakdown(boolean validBreakdown) {
		this.validBreakdown = validBreakdown;
	}
	
	@Transient
	public Set<String> getExpMetrics() {
		return ReportDataRequestUtil.getMetricsFromExpression(this.displayValueKey);
	}

	public String getDrillDownType() {
		return drillDownType;
	}

	public void setDrillDownType(String drillDownType) {
		this.drillDownType = drillDownType;
	}

	public String getDrillDownValue() {
		return drillDownValue;
	}

	public void setDrillDownValue(String drillDownValue) {
		this.drillDownValue = drillDownValue;
	}
}
