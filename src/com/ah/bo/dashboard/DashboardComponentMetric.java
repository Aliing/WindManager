package com.ah.bo.dashboard;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "DASHBOARD_COMPONENT_METRIC")
@org.hibernate.annotations.Table(appliesTo = "DASHBOARD_COMPONENT_METRIC", indexes = {
		@Index(name = "DASHBOARD_COMPONENT_METRIC_OWNER", columnNames = { "OWNER" })
		})
public class DashboardComponentMetric implements HmBo {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	// for restore and name change when PLM has requirement
	private int key;
	
	@Column(length = DEFAULT_LENGTH_255)
	private String metricName="";
	
	private String sourceType="";
	
	@Column(length = DEFAULT_LENGTH_255)
	private String displayName="";
	
	@Column(length = DEFAULT_LENGTH_255)
	private String displayValue="";
	
	private String displayValueKey="";
	
	private String orderByMetric="";
	
	private boolean orderByDesc;
	
	private int topNumber=10;
	
	private String drillDownType="";
	
	private String drillDownValue="";
	
	// 0, all, 1, ap only, 2, switch only
	private int filterDataType=0;
	
	private boolean createWidget=false;
	
	private int specifyType;
	
	private boolean defaultFlag;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "DASHBOARD_COMPONENT_DATA", joinColumns = @JoinColumn(name = "COMPONENT_METRIC_ID", nullable = false))
	private List<DashboardComponentData> componentData = new ArrayList<DashboardComponentData>();

	private long createTime = System.currentTimeMillis();
	
	private boolean homeonly=false;
	
	private int componentGroup=DashboardComponent.COMPONENT_GROUP_AEROHIVEDEVICE;
	
	private String chartType="";
	private boolean chartInverted;
	private boolean blnOverTime;
	
	private int groupBy=0;
	
	private String valueRange="";
	
	private boolean enabledHtml;
	
	private boolean enableExampleData;
	
	@Column(length = 5120)
	private String customHtml="";

	@Version
	private Timestamp version;
	
	public List<DashboardComponentData> getComponentData() {
		return componentData;
	}

	public void setComponentData(List<DashboardComponentData> componentData) {
		this.componentData = componentData;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
		
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = (metricName==null?"":metricName);
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = (sourceType==null?"":sourceType);
	}

	public int getSpecifyType() {
		return specifyType;
	}

	public void setSpecifyType(int specifyType) {
		this.specifyType = specifyType;
	}
	
    @Override
    public DashboardComponentMetric clone() {
       try {
           return (DashboardComponentMetric) super.clone();
       } catch (CloneNotSupportedException e) {
           return null;
       }
    }

	public boolean isHomeonly() {
		return homeonly;
	}

	public void setHomeonly(boolean homeonly) {
		this.homeonly = homeonly;
	}

	public int getComponentGroup() {
		return componentGroup;
	}

	public void setComponentGroup(int componentGroup) {
		this.componentGroup = componentGroup;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public boolean isChartInverted() {
		return chartInverted;
	}

	public void setChartInverted(boolean chartInverted) {
		this.chartInverted = chartInverted;
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

	public String getDisplayValueKey() {
		return displayValueKey;
	}

	public void setDisplayValueKey(String displayValueKey) {
		this.displayValueKey = displayValueKey;
	}
	
	public boolean isEnableExampleData() {
		return enableExampleData;
	}
	
	public void setEnableExampleData(boolean enableExampleData) {
		this.enableExampleData = enableExampleData;
	}

	public boolean isEnabledHtml() {
		return enabledHtml;
	}

	public void setEnabledHtml(boolean enabledHtml) {
		this.enabledHtml = enabledHtml;
	}

	public String getCustomHtml() {
		return customHtml;
	}

	public void setCustomHtml(String customHtml) {
		this.customHtml = customHtml;
	}

	public String getOrderByMetric() {
		return orderByMetric;
	}

	public void setOrderByMetric(String orderByMetric) {
		this.orderByMetric = orderByMetric;
	}

	public boolean isOrderByDesc() {
		return orderByDesc;
	}

	public void setOrderByDesc(boolean orderByDesc) {
		this.orderByDesc = orderByDesc;
	}

	public int getTopNumber() {
		return topNumber;
	}

	public void setTopNumber(int topNumber) {
		this.topNumber = topNumber;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public boolean isCreateWidget() {
		return createWidget;
	}

	public void setCreateWidget(boolean createWidget) {
		this.createWidget = createWidget;
	}

	public int getFilterDataType() {
		return filterDataType;
	}

	public void setFilterDataType(int filterDataType) {
		this.filterDataType = filterDataType;
	}

	public boolean isBlnOverTime() {
		return blnOverTime;
	}

	public void setBlnOverTime(boolean blnOverTime) {
		this.blnOverTime = blnOverTime;
	}

	public String getValueRange() {
		return valueRange;
	}

	public void setValueRange(String valueRange) {
		this.valueRange = valueRange;
	}

	public int getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(int groupBy) {
		this.groupBy = groupBy;
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