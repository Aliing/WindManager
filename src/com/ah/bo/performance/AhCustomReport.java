package com.ah.bo.performance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.ui.actions.monitor.CustomReportListAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "HM_CUSTOM_REPORT")
@org.hibernate.annotations.Table(appliesTo = "HM_CUSTOM_REPORT", indexes = {
		@Index(name = "CUSTOM_REPORT_OWNER", columnNames = { "OWNER" })
		})
public class AhCustomReport implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Column(length = DEFAULT_STRING_LENGTH,nullable = false)
	private String name;

	private String apName;

	private String ssidName;
	
	public static final int REPORT_TYPE_HIVEAP = 1;
	public static final int REPORT_TYPE_CLIENT = 2;
	public static final int REPORT_TYPE_SSID = 3;
	public static EnumItem[] REPORT_TYPE_ENUM = MgrUtil.enumItems(
			"enum.customReport.type.", new int[] {
					REPORT_TYPE_HIVEAP, REPORT_TYPE_CLIENT,
					REPORT_TYPE_SSID});
	private int reportType=REPORT_TYPE_HIVEAP;
	
	public static final int REPORT_DETAILTYPE_UNIQUE = 1;
	public static final int REPORT_DETAILTYPE_COUNT = 2;
	public static final int REPORT_DETAILTYPE_VALUE = 3;
	public static final int REPORT_DETAILTYPE_AVERAGE = 4;
	public static EnumItem[] REPORT_DETAILTYPE_ENUM = MgrUtil.enumItems(
			"enum.customReport.detailType.", new int[] {
					REPORT_DETAILTYPE_UNIQUE, REPORT_DETAILTYPE_COUNT,
					REPORT_DETAILTYPE_VALUE, REPORT_DETAILTYPE_AVERAGE});
	public static EnumItem[] REPORT_DETAILTYPE_ENUM_SSID = MgrUtil.enumItems(
			"enum.customReport.detailType.", new int[] {
					REPORT_DETAILTYPE_COUNT,
					REPORT_DETAILTYPE_VALUE, REPORT_DETAILTYPE_AVERAGE});
	private int reportDetailType=REPORT_DETAILTYPE_VALUE;

	public static final int REPORT_INTERFACE_WIFI0 = 1;
	public static final int REPORT_INTERFACE_WIFI1 = 2;
	public static final int REPORT_INTERFACE_BOTH = 3;
	public static EnumItem[] REPORT_INTERFACE_TYPE_ENUM = MgrUtil.enumItems(
			"enum.customReport.interface.", new int[] {
					REPORT_INTERFACE_WIFI0, REPORT_INTERFACE_WIFI1,
					REPORT_INTERFACE_BOTH});
	private int interfaceRole=REPORT_INTERFACE_BOTH;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "LOCATION_ID")
	private MapContainerNode location;

	public static final int REPORT_PERIOD_LASTONEDAY = 1;
	public static final int REPORT_PERIOD_LASTTWODAYS = 2;
	public static final int REPORT_PERIOD_LASTTHREEDAYS = 3;
	public static final int REPORT_PERIOD_LASTONEWEEK = 4;
	public static final int REPORT_PERIOD_LASTTWOWEEKS = 5;
	public static final int REPORT_PERIOD_LASTTHREEWEEKS = 6;
	public static final int REPORT_PERIOD_LASTONEMONTH = 7;
	public static final int REPORT_PERIOD_LASTTWOMONTHs = 8;
	public static EnumItem[] REPORT_PERIOD_TYPE = MgrUtil.enumItems(
			"enum.report.reportPeriod.", new int[] {
					REPORT_PERIOD_LASTONEDAY, REPORT_PERIOD_LASTTWODAYS,
					REPORT_PERIOD_LASTTHREEDAYS, REPORT_PERIOD_LASTONEWEEK,
					REPORT_PERIOD_LASTTWOWEEKS, REPORT_PERIOD_LASTTHREEWEEKS,
					REPORT_PERIOD_LASTONEMONTH, REPORT_PERIOD_LASTTWOMONTHs});
	private int reportPeriod=REPORT_PERIOD_LASTONEDAY;
	
	private String authMac,authHostName,authUserName,authIp;

	private Long longSortBy;
	
	// 1: asc 2 desc
	public static final int REPORT_SORTBY_TYPE_ASC=1;
	public static final int REPORT_SORTBY_TYPE_DESC=2;
	public static EnumItem[] REPORT_SORTBY_TYPE = MgrUtil.enumItems(
			"enum.customReport.sortBy.", new int[] {
					REPORT_SORTBY_TYPE_ASC, REPORT_SORTBY_TYPE_DESC});
	private int sortByType=REPORT_SORTBY_TYPE_ASC;
	
	private String description;

	private boolean defaultFlag;

	@ManyToMany(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@JoinTable(name = "HM_CUSTOM_REPORT_FIELD_TABLE", joinColumns = { @JoinColumn(name = "CUSTOM_REPORT_ID") }, inverseJoinColumns = { @JoinColumn(name = "CUSTOM_REPORT_FIELD_ID") })
	private List<AhCustomReportField> customFields = new ArrayList<AhCustomReportField>();
	
	public boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public String getAuthMac() {
		return authMac;
	}

	public void setAuthMac(String authMac) {
		this.authMac = authMac;
	}

	public String getAuthHostName() {
		return authHostName;
	}
	public String getHostNameForSQL(){
		if (authHostName==null) {
			return "";
		}
		return authHostName.toLowerCase().replace("\\", "\\\\\\\\").replace("'", "''");
	}

	public void setAuthHostName(String authHostName) {
		this.authHostName = authHostName;
	}

	public String getAuthUserName() {
		return authUserName;
	}
	
	public String getUserNameForSQL(){
		if (authUserName==null) {
			return "";
		}
		return authUserName.toLowerCase().replace("\\", "\\\\\\\\").replace("'", "''");
	}


	public void setAuthUserName(String authUserName) {
		this.authUserName = authUserName;
	}

	public String getAuthIp() {
		return authIp;
	}
	
	public String getAuthIpForSQL(){
		if (authIp==null) {
			return "";
		}
		return authIp.toLowerCase().replace("\\", "\\\\\\\\").replace("'", "''");
	}

	public void setAuthIp(String authIp) {
		this.authIp = authIp;
	}

	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return name;
	}

	@Transient
	private boolean selected;
	
	@Transient
	private TimeZone tz;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
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
	public Timestamp getVersion() {
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApName() {
		return apName;
	}
	public String getApNameForSQL(){
		if (apName==null) {
			return "";
		}
		return apName.toLowerCase().replace("\\", "\\\\\\\\").replace("'", "''");
	}
	public String getClientMacForSQL(){
		if (authMac==null) {
			return "";
		}
		return authMac.toLowerCase().replace("\\", "\\\\\\\\").replace("'", "''");
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public int getReportPeriod() {
		return reportPeriod;
	}

	public void setReportPeriod(int reportPeriod) {
		this.reportPeriod = reportPeriod;
	}

	public MapContainerNode getLocation() {
		return location;
	}

	public void setLocation(MapContainerNode location) {
		this.location = location;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getSsidName() {
		return ssidName;
	}
	
	public String getSsidNameForSQL(){
		if (ssidName==null) {
			return "";
		}
		return ssidName.toLowerCase().replace("\\", "\\\\\\\\").replace("'", "''");
	}

	public void setSsidName(String ssidName) {
		this.ssidName = ssidName;
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	public TimeZone getTz() {
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}

	public int getReportType() {
		return reportType;
	}
	
	public String getReportTypeString() {
		return MgrUtil.getEnumString("enum.customReport.type." + reportType);
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public String getReportDetailTypeString() {
		return MgrUtil.getEnumString("enum.customReport.detailType." + reportDetailType);
	}

	public void setReportDetailType(int reportDetailType) {
		this.reportDetailType = reportDetailType;
	}

	public int getInterfaceRole() {
		return interfaceRole;
	}

	public void setInterfaceRole(int interfaceRole) {
		this.interfaceRole = interfaceRole;
	}

	public void setLongSortBy(Long longSortBy) {
		this.longSortBy = longSortBy;
	}
	
	public Long getLongSortBy() {
		return longSortBy;
	}
	
	public String getLongSortByString() {
		return CustomReportListAction.mapKeyFieldString.get(longSortBy);
	}
	
	public String getLongSortByTableField() {
		return CustomReportListAction.mapKeyTableField.get(longSortBy);
	}

	public int getSortByType() {
		return sortByType;
	}
	
	public String getSortByTypeString() {
		if (sortByType==1){
			return "ascending";
		} else {
			return "descending";
		}
	}

	public void setSortByType(int sortByType) {
		this.sortByType = sortByType;
	}

	public List<AhCustomReportField> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<AhCustomReportField> customFields) {
		this.customFields = customFields;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReportPeriodString() {
		return MgrUtil.getEnumString("enum.report.reportPeriod." + getReportPeriod());
	}
	
	public int getReportDetailType() {
		return reportDetailType;
	}
	
	// for page index
	@Transient
	private int cuPageSize = 30;
	@Transient
	private int cuPageCount = 0;
	@Transient
	private int cuPageIndex = 0;

	public int getCuPageSize() {
		return cuPageSize;
	}

	public void setCuPageSize(int cuPageSize) {
		this.cuPageSize = cuPageSize;
	}

	public int getCuPageCount() {
		return cuPageCount;
	}

	public void setCuPageCount(int cuPageCount) {
		this.cuPageCount = cuPageCount;
	}

	public int getCuPageIndex() {
		return cuPageIndex;
	}

	public void setCuPageIndex(int cuPageIndex) {
		this.cuPageIndex = cuPageIndex;
	}

	// for page index
}