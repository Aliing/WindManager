package com.ah.bo.performance;

import java.sql.Timestamp;
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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.ui.actions.Navigation;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "HM_REPORT")
@org.hibernate.annotations.Table(appliesTo = "HM_REPORT", indexes = {
		@Index(name = "REPORT_OWNER", columnNames = { "OWNER" })
		})
public class AhReport implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	private String reportType;

	@Column(length = DEFAULT_STRING_LENGTH,nullable = false)
	private String name;

	private String apName;

	private String ssidName;

	public static final int REPORT_ROLE_ACCESS = 1;
	public static final int REPORT_ROLE_BACKHAUL = 2;
	public static final int REPORT_ROLE_BOTH = 3;
	public static EnumItem[] REPORT_ROLE_TYPE = MgrUtil.enumItems(
			"enum.report.role.", new int[] {
					REPORT_ROLE_ACCESS, REPORT_ROLE_BACKHAUL,
					REPORT_ROLE_BOTH});
	private int role=3;

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
	
	public static final int REPORT_PERIOD_VPN_ONEHOUR = 1;
	public static final int REPORT_PERIOD_VPN_ONEDAY = 2;
	public static final int REPORT_PERIOD_VPN_TWODAY = 3;
	public static final int REPORT_PERIOD_VPN_THREEDAY = 4;
	public static final int REPORT_PERIOD_VPN_ONEWEEK = 5;
	public static final int REPORT_PERIOD_VPN_TWOWEEK = 6;
	public static final int REPORT_PERIOD_VPN_THREEWEEK = 7;
	public static final int REPORT_PERIOD_VPN_ONEMONTH = 8;
	
	public static EnumItem[] REPORT_PERIOD_VPN_TYPE = MgrUtil.enumItems(
			"enum.report.reportPeriod.vpn.", new int[] {
					REPORT_PERIOD_VPN_ONEHOUR, REPORT_PERIOD_VPN_ONEDAY,
					REPORT_PERIOD_VPN_TWODAY, REPORT_PERIOD_VPN_THREEDAY,
					REPORT_PERIOD_VPN_ONEWEEK, REPORT_PERIOD_VPN_TWOWEEK,
					REPORT_PERIOD_VPN_THREEWEEK, REPORT_PERIOD_VPN_ONEMONTH});
	
	private int reportPeriod=1;

	public static final int TIME_AGGREGATION_FOURHOURS = 1;
	public static final int TIME_AGGREGATION_EIGHTHOURS = 2;
	public static final int TIME_AGGREGATION_ONEDAY = 3;
	public static final int TIME_AGGREGATION_TWODAYS = 4;
	public static final int TIME_AGGREGATION_ONEWEEK = 5;
	public static final int TIME_AGGREGATION_TWOWEEKS = 6;
//	public static final int TIME_AGGREGATION_ONEMONTH = 7;
	public static EnumItem[] TIME_AGGREGATION_TYPE = MgrUtil.enumItems(
			"enum.report.timeAggregation.", new int[] {
					TIME_AGGREGATION_FOURHOURS, TIME_AGGREGATION_EIGHTHOURS,
					TIME_AGGREGATION_ONEDAY, TIME_AGGREGATION_TWODAYS,
					TIME_AGGREGATION_ONEWEEK, TIME_AGGREGATION_TWOWEEKS});
//					,TIME_AGGREGATION_ONEMONTH});

	private int timeAggregation=-1;

	// 1:Immediately
	// 2:Schedule
	private String excuteType="1";

	private long startTime;
	
	private long pciStartTime;
	
	private long pciEndTime;

	private boolean enabledRecurrence;

	// 1:Daily
	// 2:Weekly
	private String recurrenceType="1";


	public static final int REPORT_WEEKDAY_MONDAY = 2;
	public static final int REPORT_WEEKDAY_TUESDAY = 3;
	public static final int REPORT_WEEKDAY_WEDNESDAY = 4;
	public static final int REPORT_WEEKDAY_THURSDAY = 5;
	public static final int REPORT_WEEKDAY_FRIDAY = 6;
	public static final int REPORT_WEEKDAY_SATURDAY = 7;
	public static final int REPORT_WEEKDAY_SUNDAY = 1;
	public static EnumItem[] REPORT_WEEKDAY_TYPE = MgrUtil.enumItems(
			"enum.report.weekday.", new int[] {
					REPORT_WEEKDAY_SUNDAY,REPORT_WEEKDAY_MONDAY,
					REPORT_WEEKDAY_TUESDAY,REPORT_WEEKDAY_WEDNESDAY,
					REPORT_WEEKDAY_THURSDAY,
					REPORT_WEEKDAY_FRIDAY, REPORT_WEEKDAY_SATURDAY});
	private int weekDay=1;

	private boolean enabledEmail;

	private String emailAddress;
	
	private String authMac,authHostName,authUserName,authIp;
	
	public static final int REPORT_CLIENTAUTH_ALL = 1;
	public static final int REPORT_CLIENTAUTH_AUTH = 2;
	public static final int REPORT_CLIENTAUTH_DEAUTH = 3;
	public static final int REPORT_CLIENTAUTH_REJECT = 4;
	public static EnumItem[] REPORT_AUTH_TYPE = MgrUtil.enumItems(
			"enum.report.authType.", new int[] {
					REPORT_CLIENTAUTH_ALL,REPORT_CLIENTAUTH_AUTH, 
					REPORT_CLIENTAUTH_DEAUTH,REPORT_CLIENTAUTH_REJECT});
	private int authType=1;
	
	public static final int REPORT_COMPLIANCEPOLICY_ALL = 0;
	public static final int REPORT_COMPLIANCEPOLICY_POOR = 1;
	public static final int REPORT_COMPLIANCEPOLICY_GOOD = 2;
	public static final int REPORT_COMPLIANCEPOLICY_EXCELLENT = 3;
	public static EnumItem[] REPORT_COMPLIANCEPOLICY_RESULT = MgrUtil.enumItems(
			"enum.report.compliance.result.", new int[] {
					REPORT_COMPLIANCEPOLICY_ALL,REPORT_COMPLIANCEPOLICY_POOR, 
					REPORT_COMPLIANCEPOLICY_GOOD,REPORT_COMPLIANCEPOLICY_EXCELLENT});
	public static EnumItem[] REPORT_SLA_TYPE_RESULT = MgrUtil.enumItems(
			"enum.report.sla.result.", new int[] {
					REPORT_COMPLIANCEPOLICY_ALL,REPORT_COMPLIANCEPOLICY_POOR, 
					REPORT_COMPLIANCEPOLICY_GOOD});
	
	private int complianceType=0;
	
	private String detailDomainName;

	private boolean defaultFlag;
	
	
	public static final int REPORT_NEWOLDTYEP_OLD = 1;
	public static final int REPORT_NEWOLDTYEP_NEW = 2;
	private int newOldFlg=REPORT_NEWOLDTYEP_NEW;
	
	public int getNewOldFlg() {
		return newOldFlg;
	}

	public void setNewOldFlg(int newOldFlg) {
		this.newOldFlg = newOldFlg;
	}

	public boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public int getAuthType() {
		return authType;
	}

	public void setAuthType(int authType) {
		this.authType = authType;
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

	public void setAuthHostName(String authHostName) {
		this.authHostName = authHostName;
	}

	public String getAuthUserName() {
		return authUserName;
	}

	public void setAuthUserName(String authUserName) {
		this.authUserName = authUserName;
	}

	public String getAuthIp() {
		return authIp;
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

	public void setApName(String apName) {
		this.apName = apName;
	}

	public int getReportPeriod() {
		return reportPeriod;
	}

	public void setReportPeriod(int reportPeriod) {
		this.reportPeriod = reportPeriod;
	}

	public int getTimeAggregation() {
		return timeAggregation;
	}

	public void setTimeAggregation(int timeAggregation) {
		this.timeAggregation = timeAggregation;
	}

	public String getExcuteType() {
		return excuteType;
	}

	public String getExcuteTypeString() {
		if (excuteType.equals("2")) {
			return MgrUtil.getUserMessage("report.reportList.schedule");
		}
		return MgrUtil.getUserMessage("report.reportList.immediately");
	}

	public void setExcuteType(String excuteType) {
		this.excuteType = excuteType;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getNextScheduleTime() {
		if (startTime == 0) {
			return 0;
		}
		if (excuteType.equals("2")) {
			Calendar calendarOld = Calendar.getInstance(owner.getTimeZone());
			calendarOld.setTimeInMillis(startTime);
			Calendar calendar = Calendar.getInstance(owner.getTimeZone());
			calendar.clear(Calendar.MINUTE);
			calendar.clear(Calendar.SECOND);
			calendar.clear(Calendar.MILLISECOND);
			long systemTimeInMillis = System.currentTimeMillis();
			if (calendarOld.getTimeInMillis()>systemTimeInMillis) {
				if (!enabledRecurrence) {
					return calendarOld.getTimeInMillis();
				} else {
					if (recurrenceType.equals("1")) {
						return calendarOld.getTimeInMillis();
					} else {
						calendarOld.set(Calendar.DAY_OF_WEEK, weekDay);
						while(calendarOld.getTimeInMillis()<startTime){
							calendarOld.add(Calendar.DAY_OF_MONTH, 7);
						}
						return calendarOld.getTimeInMillis();
					}
				}
			} else {
				if (!enabledRecurrence) {
					return 0;
				}
				calendar.set(Calendar.HOUR_OF_DAY, calendarOld.get(Calendar.HOUR_OF_DAY));
				if (calendar.getTimeInMillis()>systemTimeInMillis){
					if (recurrenceType.equals("1")) {
						return calendar.getTimeInMillis();
					} else {
						calendar.set(Calendar.DAY_OF_WEEK, weekDay);
						if (calendar.getTimeInMillis()<systemTimeInMillis) {
							calendar.add(Calendar.DAY_OF_MONTH, 7);
						}
						return calendar.getTimeInMillis();
					}
				} else {
					if (recurrenceType.equals("1")) {
						calendar.add(Calendar.DAY_OF_MONTH, 1);
					} else {
						calendar.set(Calendar.DAY_OF_WEEK, weekDay);
						if (calendar.getTimeInMillis()<systemTimeInMillis) {
							calendar.add(Calendar.DAY_OF_MONTH, 7);
						}
					}
					return calendar.getTimeInMillis();
				}
			}
		} else {
			return 0;
		}
	}
	
	public long getNextScheduleTimeForSchedule() {
		if (startTime == 0) {
			return 0;
		}
		if (excuteType.equals("2")) {
			Calendar calendarOld = Calendar.getInstance(owner.getTimeZone());
			calendarOld.setTimeInMillis(startTime);
			Calendar calendar = Calendar.getInstance(owner.getTimeZone());
			calendar.clear(Calendar.MINUTE);
			calendar.clear(Calendar.SECOND);
			calendar.clear(Calendar.MILLISECOND);
			long systemTimeInMillis = System.currentTimeMillis();
			if (calendarOld.getTimeInMillis()>systemTimeInMillis) {
				if (!enabledRecurrence) {
					return calendarOld.getTimeInMillis();
				} else {
					if (recurrenceType.equals("1")) {
						return calendarOld.getTimeInMillis();
					} else {
						calendarOld.set(Calendar.DAY_OF_WEEK, weekDay);
						while(calendarOld.getTimeInMillis()<startTime){
							calendarOld.add(Calendar.DAY_OF_MONTH, 7);
						}
						return calendarOld.getTimeInMillis();
					}
				}
			} else {
				if (!enabledRecurrence) {
					if (calendarOld.getTimeInMillis() == calendar.getTimeInMillis()){
						return calendar.getTimeInMillis();
					}
					return 0;
				}
				calendar.set(Calendar.HOUR_OF_DAY, calendarOld.get(Calendar.HOUR_OF_DAY));
				if (calendar.getTimeInMillis()>systemTimeInMillis){
					if (recurrenceType.equals("1")) {
						return calendar.getTimeInMillis();
					} else {
						calendar.set(Calendar.DAY_OF_WEEK, weekDay);
						if (calendar.getTimeInMillis()<systemTimeInMillis) {
							calendar.add(Calendar.DAY_OF_MONTH, 7);
						}
						return calendar.getTimeInMillis();
					}
				} else {
					if (recurrenceType.equals("1")) {
						calendar.add(Calendar.DAY_OF_MONTH, 1);
					} else {
						calendar.set(Calendar.DAY_OF_WEEK, weekDay);
						if (calendar.getTimeInMillis()<systemTimeInMillis) {
							calendar.add(Calendar.DAY_OF_MONTH, 7);
						}
					}
					return calendar.getTimeInMillis();
				}
			}
		} else {
			return 0;
		}
	}

	public boolean getRunScheduleTime(Calendar renCalendar) {
		long runTime= getNextScheduleTimeForSchedule();
		if (runTime==0) {
			return false;
		} else {
			if (runTime==renCalendar.getTimeInMillis()) {
				return true;
			} else {
				Calendar calendarOld = Calendar.getInstance(owner.getTimeZone());
				calendarOld.setTimeInMillis(runTime);
				if (recurrenceType.equals("1")) {
					calendarOld.add(Calendar.DAY_OF_MONTH, -1);
					return calendarOld.getTimeInMillis()==renCalendar.getTimeInMillis();
				} else {
					calendarOld.add(Calendar.DAY_OF_MONTH, -7);
					return calendarOld.getTimeInMillis()==renCalendar.getTimeInMillis();
				}
			}
		}
	}

	public String getNextScheduleTimeString() {
		long tmpDate= getNextScheduleTime();
		if (tmpDate==0) {
			return null;
		}
		return AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate,tz);
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		return formatter.format(getNextScheduleTime());
	}

	public String getStartTimeFormat() {
		if (startTime==0) {
			return "";
		}
		return AhDateTimeUtil.getSpecifyDateTimeReport(startTime,tz);
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		return formatter.format(startTime);
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public boolean getEnabledRecurrence() {
		return enabledRecurrence;
	}

	public void setEnabledRecurrence(boolean enabledRecurrence) {
		this.enabledRecurrence = enabledRecurrence;
	}

	public String getRecurrenceType() {
		return recurrenceType;
	}

	public void setRecurrenceType(String recurrenceType) {
		this.recurrenceType = recurrenceType;
	}

	public int getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(int weekDay) {
		this.weekDay = weekDay;
	}

	public boolean getEnabledEmail() {
		return enabledEmail;
	}

	public void setEnabledEmail(boolean enabledEmail) {
		this.enabledEmail = enabledEmail;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
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

	public void setSsidName(String ssidName) {
		this.ssidName = ssidName;
	}
	
	public String getNoBlankString(String strValue){
		if (strValue==null) {
			return "";
		}
		return strValue.replaceAll(" ", "");
	}
	
	public String getReportTypeShow(){
		if (reportType==null) {
			return "null";
		}
		if (reportType.equals("radioTrafficMetrics")) {
			return "RadioTrafficMetrics";
		} else if (reportType.equals("channelPowerNoise")) {
			if (newOldFlg==REPORT_NEWOLDTYEP_NEW) {
				return "Wi-FiDevices";
			} else {
				return "ChannelPowerNoise";
			}
		} else if (reportType.equals("radioTroubleShooting")) {
			return "RadioTroubleShooting";
		} else if (reportType.equals("radioAirTime")) {
			return "RadioAirTime";
		} else if (reportType.equals("radioInterference")) {
			return "RadioInterference";
		} else if (reportType.equals("ssidAirTime")) {
			return "SSIDAirTime";
		} else if (reportType.equals("ssidTrafficMetrics")) {
			return "SSIDTrafficMetrics";
		} else if (reportType.equals("ssidTroubleShooting")) {
			return "SSIDTroubleShooting";
		} else if (reportType.equals("mostClientsAPs")) {
			return "MostClientsAPs";
		} else if (reportType.equals("clientSession")) {
			if (newOldFlg==REPORT_NEWOLDTYEP_NEW) {
				return "Clients";
			} else {
				return "ClientSessions";
			}
		} else if (reportType.equals("clientCount")) {
			return "ClientRadioMode";
		} else if (reportType.equals("clientAirTime")) {
			return "ClientAirTime";
		} else if (reportType.equals("uniqueClientCount")) {
			return "UniqueClientCount";
		} else if (reportType.equals("clientAuth")) {
			return "ClientAuth";
		} else if (reportType.equals("clientVendor")) {
			return "clientVendorCount";
		} else if (reportType.equals("securityRogueAPs")) {
			return "SecurityRogueAPs";
		} else if (reportType.equals("securityRogueClients")) {
			return "SecurityRogueClients";
		} else if (reportType.equals("securityDetection")) {
			return "SecurityDetection";
		} else if (reportType.equals("meshNeighbors")) {
			return "MeshNeighbors";
		} else if (reportType.equals("inventory")) {
			return "Inventory";
		} else if (reportType.equals("compliance")) {
			return "Compliance";
		} else if (reportType.equals("pciCompliance")) {
			return "PCICompliance";
		} else if (reportType.equals("hiveApNonCompliance")) {
			return "NonComplianceDevice";
		} else if (reportType.equals("clientNonCompliance")) {
			return "NonComplianceClient";
		} else if (reportType.equals("hiveApSla")) {
			return "DeviceSLA";
		} else if (reportType.equals("clientSla")) {
			return "ClientSLA";
		} else if (reportType.equals("maxClient")) {
			return "MaxConcurrentClients";
		} else if (reportType.equals("hiveApConnection")) {
			return "DeviceConnection";
		} else if (reportType.equals("detailUserUsage")) {
			return "DetailedAccountUsage";
		} else if (reportType.equals("summaryUserUsage")) {
			return "SummaryAccountUsage";
		// VPN report
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_VPNAVAILABILITY)){
			return Navigation.L2_FEATURE_REPORT_VPNAVAILABILITY;
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_VPNTHROUGHPUT)){
			return Navigation.L2_FEATURE_REPORT_VPNTHROUGHPUT;
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_VPNLATENCY)){
			return Navigation.L2_FEATURE_REPORT_VPNLATENCY;
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_WANAVAILABILITY)){
			return Navigation.L2_FEATURE_REPORT_WANAVAILABILITY;
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_WANTHROUGHPUT)){
			return Navigation.L2_FEATURE_REPORT_WANTHROUGHPUT;
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_GWVPNAVAILABILITY)){
			return Navigation.L2_FEATURE_REPORT_GWVPNAVAILABILITY;
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_GWWANAVAILABILITY)){
			return Navigation.L2_FEATURE_REPORT_GWWANAVAILABILITY;
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_GWWANTHROUGHPUT)){
			return Navigation.L2_FEATURE_REPORT_GWWANTHROUGHPUT;
		} else {
			return "null";
		}
	}
	public String getReportTypeShowInGUI(){
		if (reportType==null) {
			return "null";
		}
		if (reportType.equals("radioTrafficMetrics")) {
			return "Traffic Metrics";
		} else if (reportType.equals("channelPowerNoise")) {
			if (newOldFlg==REPORT_NEWOLDTYEP_NEW) {
				return "Wi-Fi Devices";
			} else {
				return "Channel/Power/Noise";
			}
		} else if (reportType.equals("radioTroubleShooting")) {
			return "Troubleshooting";
		} else if (reportType.equals("radioAirTime")) {
			return "Device Airtime Usage";
		} else if (reportType.equals("radioInterference")) {
			return "Interference";
		} else if (reportType.equals("ssidAirTime")) {
			return "Airtime Usage";
		} else if (reportType.equals("ssidTrafficMetrics")) {
			return "Traffic Metrics";
		} else if (reportType.equals("ssidTroubleShooting")) {
			return "Troubleshooting";
		} else if (reportType.equals("mostClientsAPs")) {
			return "APs with Most Clients";
		} else if (reportType.equals("clientSession")) {
			if (newOldFlg==REPORT_NEWOLDTYEP_NEW) {
				return "Clients";
			} else {
				return "Client Sessions";
			}
		} else if (reportType.equals("clientCount")) {
			return "Client Radio Mode";
		} else if (reportType.equals("clientAirTime")) {
			return "Airtime Usage";
		} else if (reportType.equals("uniqueClientCount")) {
			return "Unique Client Count";
		} else if (reportType.equals("clientAuth")) {
			return "Client Authentication";
		} else if (reportType.equals("clientVendor")) {
			return "client Vendors";
		} else if (reportType.equals("securityRogueAPs")) {
			return "Rogue APs";
		} else if (reportType.equals("securityRogueClients")) {
			return "Rogue Clients";
		} else if (reportType.equals("securityDetection")) {
			return "SecurityDetection";
		} else if (reportType.equals("meshNeighbors")) {
			return "Mesh Neighbors";
		} else if (reportType.equals("inventory")) {
			return "Inventory";
		} else if (reportType.equals("compliance")) {
			return "Compliance";
		} else if (reportType.equals("pciCompliance")) {
			return "PCI Compliance";
		} else if (reportType.equals("hiveApNonCompliance")) {
			return "NonCompliance Devices";
		} else if (reportType.equals("clientNonCompliance")) {
			return "NonCompliance Clients";
		} else if (reportType.equals("hiveApSla")) {
			return "Device SLA";
		} else if (reportType.equals("clientSla")) {
			return "Client SLA";
		} else if (reportType.equals("maxClient")) {
			return "Max Concurrent Clients";
		} else if (reportType.equals("hiveApConnection")) {
			return "Device Connection Status";
		} else if (reportType.equals("detailUserUsage")) {
			return "Detailed Account Usage";
		} else if (reportType.equals("summaryUserUsage")) {
			return "Summary Account Usage";
			
		// VPN REPORT
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_VPNAVAILABILITY)){
			return "VPN Availablity";
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_VPNTHROUGHPUT)){
			return "VPN Throughput";
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_VPNLATENCY)){
			return "VPN Latency";
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_WANAVAILABILITY)){
			return "WAN Availablity";
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_WANTHROUGHPUT)){
			return "WAN Throughput";
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_GWVPNAVAILABILITY)){
			return "VPN Availablity";
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_GWWANAVAILABILITY)){
			return "WAN Availablity";
		} else if (reportType.equals(Navigation.L2_FEATURE_REPORT_GWWANTHROUGHPUT)){
			return "WAN Throughput";
		} else {
			return "null";
		}
	}
	
	public static String getReportLeftMenuName(String paramType){
		if (paramType==null) {
			return "null";
		}
		if (paramType.equalsIgnoreCase("radioTrafficMetrics")
				||paramType.equalsIgnoreCase("channelPowerNoise")
				||paramType.equalsIgnoreCase("radioTroubleShooting")
				||paramType.equalsIgnoreCase("radioAirTime")
				||paramType.equalsIgnoreCase("radioInterference")
				||paramType.equalsIgnoreCase("uniqueClientCount")
				||paramType.equalsIgnoreCase("hiveApSla")) {
			return "Wi-Fi Devices";
		} else if (paramType.equalsIgnoreCase("ssidAirTime")
				||paramType.equalsIgnoreCase("ssidTrafficMetrics")
				||paramType.equalsIgnoreCase("ssidTroubleShooting")) {
			return "SSIDs";
		} else if (paramType.equalsIgnoreCase("clientSession")
				||paramType.equalsIgnoreCase("clientAirTime")
				||paramType.equalsIgnoreCase("clientSla")) {
			return "Clients";
		} else if (paramType.equalsIgnoreCase("mostClientsAPs")) {
			return "APs with Most Clients";
		} else if (paramType.equalsIgnoreCase("clientCount")) {
			return "Client Radio Mode";
		} else if (paramType.equalsIgnoreCase("clientAuth")) {
			return "Client Authentication";
		} else if (paramType.equalsIgnoreCase("clientVendor")) {
			return "client Vendors";
		} else if (paramType.equalsIgnoreCase("securityRogueAPs")) {
			return "Rogue APs";
		} else if (paramType.equalsIgnoreCase("securityRogueClients")) {
			return "Rogue Clients";
		} else if (paramType.equalsIgnoreCase("securityDetection")) {
			return "SecurityDetection";
		} else if (paramType.equalsIgnoreCase("meshNeighbors")) {
			return "Mesh Neighbors";
		} else if (paramType.equalsIgnoreCase("inventory")) {
			return "Inventory";
		} else if (paramType.equalsIgnoreCase("compliance")) {
			return "Compliance";
		} else if (paramType.equals("pciCompliance")) {
			return "PCI Compliance";
		} else if (paramType.equalsIgnoreCase("hiveApNonCompliance")) {
			return "NonCompliance Devices";
		} else if (paramType.equalsIgnoreCase("clientNonCompliance")) {
			return "NonCompliance Clients";
		} else if (paramType.equalsIgnoreCase("maxClient")) {
			return "Max Concurrent Clients";
		} else if (paramType.equalsIgnoreCase("hiveApConnection")) {
			return "Device Connection Status";
		} else if (paramType.equalsIgnoreCase("detailUserUsage")) {
			return "Detailed Account Usage";
		} else if (paramType.equalsIgnoreCase("summaryUserUsage")) {
			return "Summary Account Usage";
		} else if (paramType.equals(Navigation.L2_FEATURE_REPORT_VPNAVAILABILITY)){
			return "VPN Availablity";
		} else if (paramType.equals(Navigation.L2_FEATURE_REPORT_VPNTHROUGHPUT)){
			return "VPN Throughput";
		} else if (paramType.equals(Navigation.L2_FEATURE_REPORT_VPNLATENCY)){
			return "VPN Latency";
		} else if (paramType.equals(Navigation.L2_FEATURE_REPORT_WANAVAILABILITY)){
			return "WAN Availablity";
		} else if (paramType.equals(Navigation.L2_FEATURE_REPORT_WANTHROUGHPUT)){
			return "WAN Throughput";
		} else if (paramType.equals(Navigation.L2_FEATURE_REPORT_GWVPNAVAILABILITY)){
			return "VPN Availablity";
		} else if (paramType.equals(Navigation.L2_FEATURE_REPORT_GWWANAVAILABILITY)){
			return "WAN Availablity";
		} else if (paramType.equals(Navigation.L2_FEATURE_REPORT_GWWANTHROUGHPUT)){
			return "WAN Throughput";
		} else {
			return "null";
		}
	}
	
	public String getApNameForSQL(){
		if (apName==null) {
			return "";
		}
		return apName.replace("\\", "\\\\\\\\").replace("'", "''");
	}
	public String getApNameForSQLTwo(){
		if (apName==null) {
			return "%%";
		}
		return "%" + apName.toLowerCase().replace("\\", "\\\\") + "%";
	}
	public String getSsidNameForSQL(){
		if (ssidName==null) {
			return "";
		}
		return ssidName.replace("\\", "\\\\\\\\").replace("'", "''");
	}

	public String getAuthMacForSQL(){
		if (authMac==null) {
			return "";
		}
		return authMac.replace("\\", "\\\\");
	}
	public String getAuthMacForSQLTwo(){
		if (authMac==null) {
			return "";
		}
		return authMac.replace("\\", "\\\\\\\\").replace("'", "''");
	}
	public String getAuthHostNameForSQL(){
		if (authHostName==null) {
			return "";
		}
		return authHostName.replace("\\", "\\\\");
	}
	public String getAuthHostNameForSQLTwo(){
		if (authHostName==null) {
			return "";
		}
		return authHostName.replace("\\", "\\\\\\\\").replace("'", "''");
	}
	public String getAuthUserNameForSQL(){
		if (authUserName==null) {
			return "";
		}
		return authUserName.replace("\\", "\\\\");
	}
	public String getAuthUserNameForSQLTwo(){
		if (authUserName==null) {
			return "";
		}
		return authUserName.replace("\\", "\\\\\\\\").replace("'", "''");
	}
	public String getAuthIpForSQL(){
		if (authIp==null) {
			return "";
		}
		return authIp.replace("\\", "\\\\");
	}
	public String getAuthIpForSQLTwo(){
		if (authIp==null) {
			return "";
		}
		return authIp.replace("\\", "\\\\\\\\").replace("'", "''");
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	public int getComplianceType() {
		return complianceType;
	}

	public void setComplianceType(int complianceType) {
		this.complianceType = complianceType;
	}

	public TimeZone getTz() {
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}
	
	// for page index
	@Transient
	private int cuPageSize = 30;
	@Transient
	private int cuPageCount = 0;
	@Transient
	private int cuPageIndex = 0;
	@Transient
	private int cuPageSort = 1;
	@Transient
	private boolean cuPageSortDesc;

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

	public int getCuPageSort() {
		return cuPageSort;
	}

	public void setCuPageSort(int cuPageSort) {
		this.cuPageSort = cuPageSort;
	}

	public boolean getCuPageSortDesc() {
		return cuPageSortDesc;
	}

	public void setCuPageSortDesc(boolean cuPageSortDesc) {
		this.cuPageSortDesc = cuPageSortDesc;
	}

	public String getDetailDomainName() {
		return detailDomainName;
	}

	public void setDetailDomainName(String detailDomainName) {
		this.detailDomainName = detailDomainName;
	}

	// for page index

	public String getReportPeriodString() {
		return MgrUtil.getEnumString("enum.report.reportPeriod." + getReportPeriod());
	}
	
	public String getReportPeriodVpnString() {
		return MgrUtil.getEnumString("enum.report.reportPeriod.vpn." + getReportPeriod());
	}
	
	@Transient
	private long reportStartTime = 0;

	public long getReportStartTime() {
		return reportStartTime;
	}

	public void setReportStartTime(long reportStartTime) {
		this.reportStartTime = reportStartTime;
	}

	public long getPciStartTime() {
		return pciStartTime;
	}

	public void setPciStartTime(long pciStartTime) {
		this.pciStartTime = pciStartTime;
	}

	public long getPciEndTime() {
		return pciEndTime;
	}

	public void setPciEndTime(long pciEndTime) {
		this.pciEndTime = pciEndTime;
	}

}