package com.ah.bo.dashboard;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

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
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.Tracer;

@Entity
@Table(name = "HM_DASHBOARD_WIDGET")
@org.hibernate.annotations.Table(appliesTo = "HM_DASHBOARD_WIDGET", indexes = {
		@Index(name = "DASHBOARD_WIDGET_OWNER", columnNames = { "OWNER" })
		})
public class AhDashboardWidget implements HmBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(AhDashboardWidget.class.getSimpleName());
	
//	public static final int TIME_LASTHOUR = 1;
//	public static final int TIME_LASTDAY = 2;
//	public static final int TIME_LASTWEEK = 3;
//	public static final int TIME_CUSTOM = 4;
	
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Transient
	private boolean selected;

	private Long reportId;
	
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String mainTitle="";
	
	public static final byte SIZE_SMALL = 1;
	public static final byte SIZE_MEDIUM = 2;
	public static final byte SIZE_LARGE = 3;
	private byte sizeType = SIZE_LARGE;
	
	private double width = -1;
	private double chartHeight = -1;
	
	private int itemOrder;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "da_layout_id")
	private AhDashboardLayout daLayout;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "widget_config_id")
	private DashboardComponent widgetConfig;
	
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "LOCATION_ID")
//	private MapContainerNode location;
	// tree node type
	private String objectType=AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL;
	//tree node id
	private String objectId = String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
	
	// tree filter node type
	private String filterObjectType=AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL;
	//tree filter node id
	private String filterObjectId = String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
	
	private int selectTimeType=AhDashboard.TAB_TIME_LASTDAY;
	private long customStartTime;
	private long customEndTime;
	private boolean enableTimeLocal;
	
	private boolean blnChecked;
	
	// a flag for special type of drill down
	private boolean blnDdSpecialType;
	
	private int specifyType = DashboardComponent.WIDGET_SPECIFY_TYPE_NONE;
	private String specifyName = "";
	
	@Version
	private Timestamp version;
	
	/**
	 * only used for restore
	 */
	@Transient
	private Long oldDashboardComponentId;
	
	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public byte getSizeType() {
		return sizeType;
	}

	public void setSizeType(byte sizeType) {
		this.sizeType = sizeType;
	}
	
	public double getChartHeight() {
		if (chartHeight < 0) {
			chartHeight = getChartHeightFromSizeType();
		}
		return chartHeight;
	}

	public void setChartHeight(double chartHeight) {
		this.chartHeight = chartHeight;
	}

	public double getWidth() {
		if (width < 0) {
			width = getWidthFromSizeType();
		}
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	@Transient
	public double getWidthFromSizeType() {
		double result = -1;
		switch(this.sizeType) {
			case SIZE_SMALL:
				result = -1;
				break;
			case SIZE_LARGE:
				result = -1;
				break;
			default:
				break;
		}
		return result;
	}
	
	@Transient
	public double getChartHeightFromSizeType() {
		double result = 275;
		switch(this.sizeType) {
			case SIZE_SMALL:
				result = 275;
				break;
			case SIZE_LARGE:
				result = 275;
				break;
			default:
				break;
		}
		return result;
	}
	
	public JSONObject getWidgetConfigJSONObject(TimeZone tz) throws JSONException {
		return this.getWidgetConfigJSONObject(tz, null);
	}
	public JSONObject getWidgetConfigJSONObject(TimeZone tz, AhDashboard da) throws JSONException {
		if (this.isBlnChecked() || da == null) {
			return this.getOnlyWidgetConfigJSONObject(tz);
		} else {
			return da.getDashboardAsWidgetConfigJSONObject(tz);
		}
	}
	
	private JSONObject getOnlyWidgetConfigJSONObject(TimeZone tz) throws JSONException {
		JSONObject jObjInfo = new JSONObject();
		
		jObjInfo.put("checked", this.isBlnChecked());
//		jObjInfo.put("lid", this.location == null?Long.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_MAP):this.location.getId());
		jObjInfo.put("obType", StringUtils.isBlank(this.getObjectType()) ? AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL :this.getObjectType());
		jObjInfo.put("obId", StringUtils.isBlank(this.getObjectId()) ? String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL) : this.getObjectId());
		jObjInfo.put("fobType", StringUtils.isBlank(this.getFilterObjectType()) ? String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL) : this.getFilterObjectType());
		jObjInfo.put("fobId", StringUtils.isBlank(this.getFilterObjectId()) ? String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL) : this.getFilterObjectId());
		jObjInfo.put("timeType", this.getSelectTimeType() <= 0 ? AhDashboard.TAB_TIME_LASTDAY : this.getSelectTimeType());
		jObjInfo.put("enableTimeLocal", this.isEnableTimeLocal());

		Calendar calendar;
		if (tz != null) {
			calendar = Calendar.getInstance(tz);
		} else {
			calendar = Calendar.getInstance();
		}
		SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (tz != null) {
			l_sdf.setTimeZone(tz);
		}
		calendar.setTimeInMillis(this.getCustomStartTime());
		jObjInfo.put("startDate", l_sdf.format(calendar.getTime()));
		jObjInfo.put("startHour", calendar.get(Calendar.HOUR_OF_DAY));
		calendar.setTimeInMillis(this.getCustomEndTime());
		jObjInfo.put("endDate", l_sdf.format(calendar.getTime()));
		jObjInfo.put("endHour", calendar.get(Calendar.HOUR_OF_DAY));
		
		return jObjInfo;
	}
	
	
	public JSONObject getJSONObject() {
		return getJSONObject(null, null);
	}
	
	public JSONObject getJSONObject(TimeZone tz) {
		return getJSONObject(tz, null);
	}
	
	public JSONObject getJSONObject(TimeZone tz, AhDashboard da) {
		JSONObject jObjInfo = new JSONObject();
		
		try {
			jObjInfo.put("reportId", this.getReportId());
			jObjInfo.put("width", this.getWidth());
			jObjInfo.put("chartHeight", this.getChartHeight());
			jObjInfo.put("title", this.getMainTitle());
			jObjInfo.put("oWidgetId", this.getId());
			jObjInfo.put("checked", this.isBlnChecked());
			
			if (widgetConfig != null) {
				if (widgetConfig.getComponentMetric().isBlnOverTime()) {
					jObjInfo.put("xAxisType", "datetime");
				}
				if (widgetConfig.getComponentMetric().isEnabledHtml()) {
					jObjInfo.put("renderType", "html");
				}
				if (widgetConfig.getComponentMetric() != null) {
					jObjInfo.put("xk", widgetConfig.getComponentMetric().getSourceType());
				}
				
				JSONObject jWidgetConfigInfo = widgetConfig.getDataConfigJSONObject();
				if (jWidgetConfigInfo == null) {
					jWidgetConfigInfo = new JSONObject();
				}
				//jWidgetConfigInfo.put("desc", DaHelper.getJSONWidgetCommonInfo(DaHelper.prepareWidgetCommonInfo(this, da, tz)));
				
				JSONObject wJsonObj = new JSONObject();
				jWidgetConfigInfo.put("w", wJsonObj);
				wJsonObj.put("blnDds", this.isBlnDdSpecialType());
				
				jWidgetConfigInfo.put("widgetConfig", this.getWidgetConfigJSONObject(tz, da));
				jObjInfo.put("config", jWidgetConfigInfo);
			}
		} catch (JSONException e) {
			log.error("getJSONObject", "Failed to encapsulate JSON object for dashboard widget", e);
		}
		
		return jObjInfo;
	}

	@Transient
	public void cloneConfigsToNew(AhDashboardWidget another) {
		if (another == null) {
			return;
		}
		
		another.setCustomEndTime(this.getCustomEndTime());
		another.setCustomStartTime(this.getCustomStartTime());
//		another.setLocation(this.getLocation());
		another.setObjectId(this.getObjectId());
		another.setObjectType(this.getObjectType());
		another.setFilterObjectId(this.getFilterObjectId());
		another.setFilterObjectType(this.getFilterObjectType());
		another.setSelectTimeType(this.getSelectTimeType());
		another.setBlnChecked(this.isBlnChecked());
		another.setEnableTimeLocal(this.isEnableTimeLocal());
	}
	
	@Transient
	public void setDefaultTopoFilter() {
//		this.location = null;
		this.objectType = AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL;
		this.objectId = String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
		this.filterObjectType=AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL;
		this.filterObjectId = String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
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
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public AhDashboardLayout getDaLayout() {
		return daLayout;
	}

	public void setDaLayout(AhDashboardLayout daLayout) {
		this.daLayout = daLayout;
	}

	public int getItemOrder() {
		return itemOrder;
	}

	public void setItemOrder(int itemOrder) {
		this.itemOrder = itemOrder;
	}

	public DashboardComponent getWidgetConfig() {
		return widgetConfig;
	}

	public void setWidgetConfig(DashboardComponent widgetConfig) {
		this.widgetConfig = widgetConfig;
		if (widgetConfig != null) {
			this.reportId = widgetConfig.getId();
		}
	}

	public String getMainTitle() {
		return mainTitle;
	}

	public void setMainTitle(String mainTitle) {
		this.mainTitle = (mainTitle==null?"":mainTitle);
	}

//	public MapContainerNode getLocation() {
//		return location;
//	}
//
//	public void setLocation(MapContainerNode location) {
//		this.location = location;
//	}

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

	public int getSelectTimeType() {
		return selectTimeType;
	}

	public void setSelectTimeType(int selectTimeType) {
		this.selectTimeType = selectTimeType;
	}

	public long getCustomStartTime() {
		return customStartTime;
	}

	public void setCustomStartTime(long customStartTime) {
		this.customStartTime = customStartTime;
	}

	public long getCustomEndTime() {
		return customEndTime;
	}

	public void setCustomEndTime(long customEndTime) {
		this.customEndTime = customEndTime;
	}

	public boolean isBlnChecked() {
		return blnChecked;
	}

	public void setBlnChecked(boolean blnChecked) {
		this.blnChecked = blnChecked;
	}

	public boolean isEnableTimeLocal() {
		return enableTimeLocal;
	}

	public void setEnableTimeLocal(boolean enableTimeLocal) {
		this.enableTimeLocal = enableTimeLocal;
	}

	@Override
	public AhDashboardWidget clone() {
		try {
			return (AhDashboardWidget) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public boolean isBlnDdSpecialType() {
		return blnDdSpecialType;
	}

	public void setBlnDdSpecialType(boolean blnDdSpecialType) {
		this.blnDdSpecialType = blnDdSpecialType;
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
		this.specifyName = specifyName;
	}

	public Long getOldDashboardComponentId() {
		return oldDashboardComponentId;
	}

	public void setOldDashboardComponentId(Long oldDashboardComponentId) {
		this.oldDashboardComponentId = oldDashboardComponentId;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AhDashboardWidget)) {
			return false;
		}
		return id.equals(((AhDashboardWidget)other).getId());
	}

}