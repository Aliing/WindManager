package com.ah.bo.dashboard;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Index;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.bo.dashboard.DaHelper;
import com.ah.util.bo.dashboard.ReportDataRequestUtil;

@Entity
@Table(name = "HM_DASHBOARD_COMPONENT")
@org.hibernate.annotations.Table(appliesTo = "HM_DASHBOARD_COMPONENT", indexes = {
		@Index(name = "DASHBOARD_COMPONENT_OWNER", columnNames = { "OWNER" })
		})
public class DashboardComponent implements HmBo {
	
	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(DashboardComponent.class.getSimpleName());
	
	@Id
	@GeneratedValue
	private Long id;
	
	// for restore and name change when PLM has requirement
	private int key;
	
	@Column(length = DEFAULT_LENGTH_255)
	private String componentName="";

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	public static final String COMPONENT_TYPE_WIDGET = "widget";
	public static String COMPONENT_TYPE_METRIC="metric";
	public static String COMPONENT_TYPE_COUSTM="custom";
	private String componentType=COMPONENT_TYPE_METRIC;
	
	public static final int COMPONENT_GROUP_APPLICATION=1;
	public static final int COMPONENT_GROUP_CLIENTS=2;
	public static final int COMPONENT_GROUP_USERS=4;
	public static final int COMPONENT_GROUP_AEROHIVEDEVICE=8;
	public static final int COMPONENT_GROUP_NETWORK=16;
	
	private int componentGroup=COMPONENT_GROUP_AEROHIVEDEVICE;
	
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String sourceType="";
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "METRIC_ID", nullable = true)
	private DashboardComponentMetric componentMetric;
	
	private boolean defaultFlag;
	
	private long createTime= System.currentTimeMillis();
//	
//	private String chartType="";
//	private boolean chartInverted;
	
	public static final int WIDGET_SPECIFY_TYPE_NONE=1;
	public static final int WIDGET_SPECIFY_TYPE_CLIENT=2;
	public static final int WIDGET_SPECIFY_TYPE_USER=3;
	public static final int WIDGET_SPECIFY_TYPE_APP=4;
	public static final int WIDGET_SPECIFY_TYPE_DEVICE=5;
	public static final int WIDGET_SPECIFY_TYPE_PORT=6;
	public static final int WIDGET_SPECIFY_TYPE_APPCLIENT=7;
	private int specifyType=WIDGET_SPECIFY_TYPE_NONE;
	private String specifyName="";
	
	private boolean homeonly=false;
	
	@Version
	private Timestamp version;
	
	@Transient
	private boolean selected;

	@Override
	public String getLabel() {
		return null;
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

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = (sourceType==null?"":sourceType);
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = (componentName==null?"":componentName);
	}

	public DashboardComponentMetric getComponentMetric() {
		return componentMetric;
	}

	public void setComponentMetric(DashboardComponentMetric componentMetric) {
		this.componentMetric = componentMetric;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public String getRealComponentType(){
		return componentType;
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public String getProfileSaveAbled(){
		if(isDefaultFlag()) {
			return "disabled";
		}
		return "";
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getSpecifyType() {
		return specifyType;
	}

	public void setSpecifyType(int specifyType) {
		this.specifyType = specifyType;
	}

	public String getSpecifyName() {
		return specifyName;
	}

	public void setSpecifyName(String specifyName) {
		this.specifyName = (specifyName==null?"":specifyName);
	}
	
	
    @Override
    public DashboardComponent clone() {
       try {
           return (DashboardComponent) super.clone();
       } catch (CloneNotSupportedException e) {
           return null;
       }
    }
	
	@Transient
	public Map<Integer, List<DashboardComponentData>> getDaComponentDataGroupMap() {
		Map<Integer, List<DashboardComponentData>> groupMap = new HashMap<>();
		
		if(null != this.getComponentMetric() ){
			DashboardComponentMetric daMetric = this.getComponentMetric();
			
			if(!daMetric.getComponentData().isEmpty()){
				for(DashboardComponentData data : daMetric.getComponentData()){
					if(groupMap.containsKey(data.getGroupIndex())){
						groupMap.get(data.getGroupIndex()).add(data);
					}else{
						List<DashboardComponentData> datalList = new ArrayList<DashboardComponentData>();
						datalList.add(data);
						groupMap.put(data.getGroupIndex(), datalList);
					}
				}
			}
		}
		
		return groupMap;
	}
	
	@Transient
	public JSONObject getDataConfigJSONObject() {
		JSONObject jObjInfo = new JSONObject();
		
		try {
			jObjInfo.put("reportId", this.getId());
			jObjInfo.put("axis", this.getSourceType());
			jObjInfo.put("xname", this.getComponentMetric().getDisplayName());
			jObjInfo.put("xExp", this.getComponentMetric().getDisplayValueKey());
			jObjInfo.put("xKey", this.getComponentMetric().getKey());
			jObjInfo.put("example", this.getComponentMetric().isEnableExampleData());
			if (StringUtils.isNotBlank(this.getComponentMetric().getChartType())) {
				jObjInfo.put("chartType", this.getComponentMetric().getChartType());
			} else if (this.getComponentMetric().isEnabledHtml()) {
				jObjInfo.put("chartType", "html");
			}
			jObjInfo.put("inverted", this.getComponentMetric().isChartInverted());
			jObjInfo.put("overtime", this.getComponentMetric().isBlnOverTime());
			jObjInfo.put("valRange", this.getComponentMetric().getValueRange());
			jObjInfo.put("blnApp", DaHelper.isApplicationWidget(this.getComponentMetric().getKey()));
			jObjInfo.put("blnBkNoDataTotal", DaHelper.isBkNoDataNeedTotalWidget(this.getComponentMetric().getKey()));
			jObjInfo.put("xmt", DaHelper.getDrillDownTypeString(this.getComponentMetric().getDrillDownType()));
			jObjInfo.put("xmv", this.getComponentMetric().getDrillDownValue());
			
			JSONObject jOpt = new JSONObject();
			jObjInfo.put("opt", jOpt);
			jOpt.put("title", this.getComponentName());
			jOpt.put("group", this.getComponentGroupString());
			jOpt.put("metric", this.getComponentMetric().getMetricName());
			
			JSONObject jMetric = new JSONObject();
			jObjInfo.put("m", jMetric);
			
			Map<Integer, List<DashboardComponentData>> dataMap = this.getDaComponentDataGroupMap();
			if (dataMap != null
					&& !dataMap.isEmpty()) {
				for (Integer key : dataMap.keySet()) {
					if (key == null) {
						continue;
					}
					String keyStr = key.toString();
					JSONArray dataArray = new JSONArray();
					jMetric.put(keyStr, dataArray);
					
					for (DashboardComponentData data : dataMap.get(key)) {
						JSONObject jMObj = new JSONObject();
						dataArray.put(jMObj);
						jMObj.put("name", data.getDisplayName());
						jMObj.put("metric", data.getSourceData());
						jMObj.put("exp", data.getDisplayValueKey());
						jMObj.put("bkl", data.getLevelBreakDown());
						jMObj.put("bkOption", data.getDisplayOption());
						jMObj.put("gidx", data.getGroupIndex());
						jMObj.put("idx", data.getPositionIndex());
						jMObj.put("mt", DaHelper.getDrillDownTypeString(data.getDrillDownType()));
						jMObj.put("mv", data.getDrillDownValue());
					}
				}
			}
		} catch (JSONException e) {
			log.error("getJSONObject", "Failed to encapsulate data config JSON object for dashboard component", e);
		}
		
		return jObjInfo;
	}
	
	@Transient
	public JSONObject getJSONObject() {
		JSONObject jObjInfo = new JSONObject();
		
		try {
			if (this.isdaComponentHTML()) {
				jObjInfo.put("renderType", "html");
				return jObjInfo;
			}
			jObjInfo.put("config", getDataConfigJSONObject());
		} catch (JSONException e) {
			log.error("getJSONObject", "Failed to encapsulate JSON object for dashboard component", e);
		}
		
		return jObjInfo;
	}
	
	@Transient
	private boolean isdaComponentHTML() {
		if (this.getComponentMetric().isEnabledHtml()) {
			return true;
		}
		
		return false;
	}

	@Transient
	public Set<String> getExpMetrics() {
		String displayKey = this.getComponentMetric().getDisplayValueKey();
		Set<String> result = ReportDataRequestUtil.getMetricsFromExpression(displayKey);
		
		if (result != null 
				&& !result.isEmpty()
				&& this.getComponentMetric() != null
				&& StringUtils.isNotBlank(this.getComponentMetric().getSourceType())) {
			result.remove(this.getComponentMetric().getSourceType());
		}
		
		return result;
	}
//
//	public String getChartType() {
//		return chartType;
//	}
//
//	public void setChartType(String chartType) {
//		this.chartType = (chartType==null?"":chartType);
//	}
//	
//	public boolean isChartInverted() {
//		return chartInverted;
//	}
//
//	public void setChartInverted(boolean chartInverted) {
//		this.chartInverted = chartInverted;
//	}

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

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
	
	public String getComponentGroupString() {
		return MgrUtil.getEnumString("enum.da.widget.grouptype." + this.componentGroup);
	}
}