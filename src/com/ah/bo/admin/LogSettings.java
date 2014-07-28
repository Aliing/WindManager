package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Range;

import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;

@Entity
@Table(name = "LOGSETTINGS")
public class LogSettings implements HmBo {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long	id;

	private int		alarmInterval;
    
	private int     alarmRetainUnclearDays=DEFAULT_ALARM_RETAIN_UNCLEAR_DAYS;
	
	private int     alarmMaxRecords=-1;
	
	private int     alarmReminderDays=DEFAULT_ALARM_REMINDER_DAYS;
	
	private int		eventInterval;

	private int		maxPerfRecord=500000;

	private int		maxHistoryClientRecord=2000000;

	// minutes unit
	private int		interfaceStatsInterval=30;
	
	// system log expiration days
	@Range(min = MIN_EXPIRATIONDAYS_INTERVAL, max = MAX_EXPIRATIONDAYS_INTERVAL)
	private int		syslogExpirationDays = DEFAULT_SYSLOG_EXPIRATIONDAYS;
	
	// audit log expiration days
	@Range(min = MIN_EXPIRATIONDAYS_INTERVAL, max = MAX_EXPIRATIONDAYS_INTERVAL)
	private int		auditlogExpirationDays = DEFAULT_AUDITLOG_EXPIRATIONDAYS;
	
	//l3Firewall log expiration days
	@Range(min = MIN_EXPIRATIONDAYS_INTERVAL, max = MAX_EXPIRATIONDAYS_INTERVAL)
	private int     l3FirewallLogExpirationDays = DEFAULT_L3FIREWALLLOG_EXPIRATIONDAYS;
	
	// default values
	public final static int DEFAULT_ALARM_INTERVAL = 30;
	
	public final static int DEFAULT_EVENT_INTERVAL = 7;
	
	public final static int DEFAULT_MAX_PERFORM_RECORDS = 500000;
	
	public final static int DEFAULT_MAX_HISTORY_CLIENT_RECORDS = 2000000;
	
	public final static int DEFAULT_INTERFACE_STATS_INTERVAL = 30;
	
	public final static int DEFAULT_SYSLOG_EXPIRATIONDAYS = 60;
	
	public final static int DEFAULT_AUDITLOG_EXPIRATIONDAYS = 183;
	
	public final static int DEFAULT_L3FIREWALLLOG_EXPIRATIONDAYS = 60;
	
	public final static int DEFAULT_STATS_START_MINUTE = 0;
	
	public final static int MAX_EXPIRATIONDAYS_INTERVAL = 999;
	
	public final static int MIN_EXPIRATIONDAYS_INTERVAL = 1;
	
	public final static  int DEFAULT_ALARM_RETAIN_UNCLEAR_DAYS=14;
	
	public final static  int DEFAULT_ALARM_REMINDER_DAYS=30;
	
	public final static int ALARM_MIN_RECORDS=500;
	public final static int ALARM_MAX_RECORDS=500000;
	public final static int ALARM_HMOL_MIN_RECORDS=1000;
	public final static int ALARM_HMOL_MAX_RECORDS=100000;
	public final static int DEFAULT_ALARM_HMOL_MAX_RECORDS=30000;
	
	// stats start time
	private int		statsStartMinute;
	
	
	private int maxOriginalCount=24;
	private int maxHourValue=2;
	private int maxDayValue=5;
	private int maxWeekValue=12;
	private int slaPeriod=3;
	private int clientPeriod=10;
	private int maxSupportAp = 200;
	//interval of table partition for performance statistics: days
	private int intervalTablePartPer=1;
	//maximum time of performance table data be saved: days
	private int maxTimeTablePerSave=3;
	//interval of table partition for client records: hours
	private int intervalTablePartCli=8;
	//maximum time of client statistics data be saved: days
	private int maxTimeTableCliSave=3;
	
	private int reportIntervalMinute = 10;
	private int reportMaxApCount = 20;
	private int reportMaxApForSystem = 100;
	
	private int reportDbHourly = 30000000;
	private int reportDbDaily = 30000000;
	private int reportDbWeekly = 30000000;

	
	public int getIntervalTablePartPer() {
		return intervalTablePartPer;
	}

	public void setIntervalTablePartPer(int intervalTablePartPer) {
		this.intervalTablePartPer = intervalTablePartPer;
	}

	public int getMaxTimeTablePerSave() {
		return maxTimeTablePerSave;
	}

	public void setMaxTimeTablePerSave(int maxTimeTablePerSave) {
		this.maxTimeTablePerSave = maxTimeTablePerSave;
	}

	public int getIntervalTablePartCli() {
		return intervalTablePartCli;
	}

	public void setIntervalTablePartCli(int intervalTablePartCli) {
		this.intervalTablePartCli = intervalTablePartCli;
	}

	public int getMaxTimeTableCliSave() {
		return maxTimeTableCliSave;
	}

	public void setMaxTimeTableCliSave(int maxTimeTableCliSave) {
		this.maxTimeTableCliSave = maxTimeTableCliSave;
	}

	public int getReportIntervalMinute() {
		return reportIntervalMinute;
	}

	public void setReportIntervalMinute(int reportIntervalMinute) {
		this.reportIntervalMinute = reportIntervalMinute;
	}

	public int getReportMaxApCount() {
		return reportMaxApCount;
	}

	public void setReportMaxApCount(int reportMaxApCount) {
		this.reportMaxApCount = reportMaxApCount;
	}

	public int getMaxSupportAp() {
		return maxSupportAp;
	}

	public void setMaxSupportAp(int maxSupportAp) {
		this.maxSupportAp = maxSupportAp;
	}

	public int getMaxOriginalCount() {
		return maxOriginalCount;
	}

	public void setMaxOriginalCount(int maxOriginalCount) {
		this.maxOriginalCount = maxOriginalCount;
	}

	public int getMaxHourValue() {
		return maxHourValue;
	}

	public void setMaxHourValue(int maxHourValue) {
		this.maxHourValue = maxHourValue;
	}

	public int getMaxDayValue() {
		return maxDayValue;
	}

	public void setMaxDayValue(int maxDayValue) {
		this.maxDayValue = maxDayValue;
	}

	public int getMaxWeekValue() {
		return maxWeekValue;
	}

	public void setMaxWeekValue(int maxWeekValue) {
		this.maxWeekValue = maxWeekValue;
	}

	public int getSlaPeriod() {
		return slaPeriod;
	}

	public void setSlaPeriod(int slaPeriod) {
		this.slaPeriod = slaPeriod;
	}

	public int getClientPeriod() {
		return clientPeriod;
	}

	public void setClientPeriod(int clientPeriod) {
		this.clientPeriod = clientPeriod;
	}
	
	public int getMaxPerfRecord() {
		return maxPerfRecord;
	}

	public void setMaxPerfRecord(int maxPerfRecord) {
		this.maxPerfRecord = maxPerfRecord;
	}

	public int getAlarmInterval() {
		return alarmInterval;
	}

	public void setAlarmInterval(int alarmInterval) {
		this.alarmInterval = alarmInterval;
	}

	public int getEventInterval() {
		return eventInterval;
	}

	public void setEventInterval(int eventInterval) {
		this.eventInterval = eventInterval;
	}

	// ------------implement interface
	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return "Log Settings";
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain	owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp	version;

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Transient
	private boolean	selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public int getMaxHistoryClientRecord() {
		return maxHistoryClientRecord;
	}

	public void setMaxHistoryClientRecord(int maxHistoryClientRecord) {
		this.maxHistoryClientRecord = maxHistoryClientRecord;
	}

	public int getInterfaceStatsInterval() {
		return interfaceStatsInterval;
	}

	public void setInterfaceStatsInterval(int interfaceStatsInterval) {
		this.interfaceStatsInterval = interfaceStatsInterval;
	}

	public int getSyslogExpirationDays() {
		return syslogExpirationDays;
	}

	public void setSyslogExpirationDays(int syslogExpirationDays) {
		this.syslogExpirationDays = syslogExpirationDays;
	}

	public int getAuditlogExpirationDays() {
		return auditlogExpirationDays;
	}

	public void setAuditlogExpirationDays(int auditlogExpirationDays) {
		this.auditlogExpirationDays = auditlogExpirationDays;
	}

	public int getL3FirewallLogExpirationDays() {
		return l3FirewallLogExpirationDays;
	}

	public void setL3FirewallLogExpirationDays(int l3FirewallLogExpirationDays) {
		this.l3FirewallLogExpirationDays = l3FirewallLogExpirationDays;
	}

	public int getStatsStartMinute() {
		return statsStartMinute;
	}

	public void setStatsStartMinute(int statsStartMinute) {
		this.statsStartMinute = statsStartMinute;
	}
	
	public int getReportMaxApForSystem() {
		return reportMaxApForSystem;
	}

	public void setReportMaxApForSystem(int reportMaxApForSystem) {
		this.reportMaxApForSystem = reportMaxApForSystem;
	}

	public int getReportDbHourly() {
		return reportDbHourly;
	}

	public void setReportDbHourly(int reportDbHourly) {
		this.reportDbHourly = reportDbHourly;
	}

	public int getReportDbDaily() {
		return reportDbDaily;
	}

	public void setReportDbDaily(int reportDbDaily) {
		this.reportDbDaily = reportDbDaily;
	}

	public int getReportDbWeekly() {
		return reportDbWeekly;
	}

	public void setReportDbWeekly(int reportDbWeekly) {
		this.reportDbWeekly = reportDbWeekly;
	}

	public int getAlarmRetainUnclearDays() {
		return alarmRetainUnclearDays;
	}

	public void setAlarmRetainUnclearDays(int alarmRetainUnclearDays) {
		this.alarmRetainUnclearDays = alarmRetainUnclearDays;
	}

	public int getAlarmMaxRecords() {
		return alarmMaxRecords;
	}
	
	@Transient
	public int getShowMaxRecordsValue(){
		if(alarmMaxRecords==-1){
			return getDefaultMaxRecords();
		}
		return alarmMaxRecords;
	}
    @Transient
	public static int getDefaultMaxRecords(){
    	int maxRecords=0;
    	if(NmsUtil.isHostedHMApplication()){
    		maxRecords=DEFAULT_ALARM_HMOL_MAX_RECORDS;
		}else{
			maxRecords=getConvertMaxRecords(10*HmBeLicenseUtil.getLicenseInfo().getHiveAps());
		}
    	return maxRecords;
	}
    @Transient
    public static int getConvertMaxRecords(int maxRecords){
    	if(maxRecords<=ALARM_MIN_RECORDS){
			maxRecords=ALARM_MIN_RECORDS;
		}else if(maxRecords>=ALARM_MAX_RECORDS){
			maxRecords=ALARM_MAX_RECORDS;
		}
    	return maxRecords;
    }
    
	public void setAlarmMaxRecords(int alarmMaxRecords) {
		this.alarmMaxRecords = alarmMaxRecords;
	}

	public int getAlarmReminderDays() {
		return alarmReminderDays;
	}

	public void setAlarmReminderDays(int alarmReminderDays) {
		this.alarmReminderDays = alarmReminderDays;
	}

}