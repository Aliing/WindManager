package com.ah.bo.performance;

import java.sql.Timestamp;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import com.ah.bo.admin.HmDomain;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "hm_client_stats")
@org.hibernate.annotations.Table(appliesTo = "hm_client_stats", indexes = {
		@Index(name = "IDX_TIMESTAMP_AND_CLIENTMAC", columnNames = {"TIMESTAMP","CLIENTMAC"}),
		@Index(name = "IDX_TIMESTAMP_AND_OWNER", columnNames = {"TIMESTAMP","OWNER"}),
		@Index(name = "IDX_CLIENT_STATS_OWNER", columnNames = {"OWNER"})
		})
public class AhClientStats implements AhClientStatsInterf {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String				apName;

	@Column(length = MAC_ADDRESS_LENGTH)
	private String				apMac;

	private long		timeStamp;

	private short		collectPeriod;

	private int			ifIndex;
	private Short		ifName;

	@Column(length = MAC_ADDRESS_LENGTH)
	private String		clientMac;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String		ssidName;

	//radio link quality score
	private byte		slaConnectScore=100;
	
	private byte		ipNetworkConnectivityScore=100;

	private byte		applicationHealthScore=100;
	
	private byte		overallClientHealthScore=100;

	private int			bandWidthUsage;

	private int			slaViolationTraps;

	private int			txFrameDropped;

	private int			rxFrameDropped;

	private int			txFrameCount;

	private long		txFrameByteCount;

	private int			rxFrameCount;

	private long		rxFrameByteCount;

	private int			averageSNR;

	private int			powerSaveModeTimes;

	private byte		txAirTime;

	private byte		rxAirTime;

	/**
	 * {Tx rate,Tx bit rate distribution,Tx bit rate success distribution};{Same with previous
	 * one};....
	 */
	@Column(length = 1000)
	private String		txRateInfo;

	@Column(length = 1000)
	private String		rxRateInfo;

	private int			alarmFlag;
	
	private byte 			totalTxBitSuccessRate;
	
	private byte 			totalRxBitSuccessRate;
	
	private int radioType;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String clientosinfo;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String osname;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String vendor;
	
	private int rssi;
	
	private String userName;
	
	private String hostName;
	
	private String userProfileName;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;

	@Transient
	private TimeZone	tz;

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public String getLabel() {
		return "client_stats";
	}

	@Transient
	private boolean	selected;

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public int getAverageSNR() {
		return averageSNR;
	}

	public void setAverageSNR(int averageSNR) {
		this.averageSNR = averageSNR;
	}

	public String getClientMac() {
		return clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

	public short getCollectPeriod() {
		return collectPeriod;
	}

	public void setCollectPeriod(short collectPeriod) {
		this.collectPeriod = collectPeriod;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	public Short getIfName() {
		return ifName;
	}

	public void setIfName(Short port) {
		ifName = port;
	}

	public int getPowerSaveModeTimes() {
		return powerSaveModeTimes;
	}

	public void setPowerSaveModeTimes(int powerSaveModeTimes) {
		this.powerSaveModeTimes = powerSaveModeTimes;
	}

	public byte getRxAirTime() {
		return rxAirTime;
	}

	public void setRxAirTime(byte rxAirTime) {
		this.rxAirTime = rxAirTime;
	}

	public int getRxFrameCount() {
		return rxFrameCount;
	}

	public void setRxFrameCount(int rxFrameCount) {
		this.rxFrameCount = rxFrameCount;
	}

	public int getRxFrameDropped() {
		return rxFrameDropped;
	}

	public void setRxFrameDropped(int rxFrameDropped) {
		this.rxFrameDropped = rxFrameDropped;
	}

	public byte getSlaConnectScore() {
		return slaConnectScore;
	}

	public void setSlaConnectScore(byte slaConnectScore) {
		this.slaConnectScore = slaConnectScore;
	}
	
	public byte getIpNetworkConnectivityScore() {
		return ipNetworkConnectivityScore;
	}

	public void setIpNetworkConnectivityScore(byte ipNetworkConnectivityScore) {
		this.ipNetworkConnectivityScore = ipNetworkConnectivityScore;
	}

	public byte getApplicationHealthScore() {
		return applicationHealthScore;
	}

	public void setApplicationHealthScore(byte applicationHealthScore) {
		this.applicationHealthScore = applicationHealthScore;
	}

	public byte getOverallClientHealthScore() {
		return overallClientHealthScore;
	}

	public void setOverallClientHealthScore(byte overallClientHealthScore) {
		this.overallClientHealthScore = overallClientHealthScore;
	}
	
	public int getSlaViolationTraps() {
		return slaViolationTraps;
	}

	public void setSlaViolationTraps(int slaViolationTraps) {
		this.slaViolationTraps = slaViolationTraps;
	}

	public String getSsidName() {
		return ssidName;
	}

	public void setSsidName(String ssidName) {
		this.ssidName = ssidName;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getTimeStampString() {
		return AhDateTimeUtil.getSpecifyDateTime(timeStamp, tz, owner);
	}

	public byte getTxAirTime() {
		return txAirTime;
	}

	public void setTxAirTime(byte txAirTime) {
		this.txAirTime = txAirTime;
	}

	public int getTxFrameCount() {
		return txFrameCount;
	}

	public void setTxFrameCount(int txFrameCount) {
		this.txFrameCount = txFrameCount;
	}

	public int getTxFrameDropped() {
		return txFrameDropped;
	}

	public void setTxFrameDropped(int txFrameDropped) {
		this.txFrameDropped = txFrameDropped;
	}

	public String getRxRateInfo() {
		return rxRateInfo;
	}

	public void setRxRateInfo(String rxRateInfo) {
		this.rxRateInfo = rxRateInfo;
	}

	public String getTxRateInfo() {
		return txRateInfo;
	}

	public void setTxRateInfo(String txRateInfo) {
		this.txRateInfo = txRateInfo;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public int getBandWidthUsage() {
		return bandWidthUsage;
	}

	public void setBandWidthUsage(int bandWidthUsage) {
		this.bandWidthUsage = bandWidthUsage;
	}

	public TimeZone getTz() {
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}

	public int getAlarmFlag() {
		return alarmFlag;
	}

	public void setAlarmFlag(int alarmFlag) {
		this.alarmFlag = alarmFlag;
	}

	public long getTxFrameByteCount() {
		return txFrameByteCount;
	}

	public void setTxFrameByteCount(long txFrameByteCount) {
		this.txFrameByteCount = txFrameByteCount;
	}

	public long getRxFrameByteCount() {
		return rxFrameByteCount;
	}

	public void setRxFrameByteCount(long rxFrameByteCount) {
		this.rxFrameByteCount = rxFrameByteCount;
	}

	public byte getTotalTxBitSuccessRate() {
		return totalTxBitSuccessRate;
	}

	public void setTotalTxBitSuccessRate(byte totalTxBitSuccessRate) {
		this.totalTxBitSuccessRate = totalTxBitSuccessRate;
	}

	public byte getTotalRxBitSuccessRate() {
		return totalRxBitSuccessRate;
	}

	public void setTotalRxBitSuccessRate(byte totalRxBitSuccessRate) {
		this.totalRxBitSuccessRate = totalRxBitSuccessRate;
	}

	public String getClientosinfo() {
		return clientosinfo;
	}

	public void setClientosinfo(String clientosinfo) {
		this.clientosinfo = clientosinfo;
	}
	
	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public void setUserName(String username) {
		this.userName = username;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public void setHostName(String hostname) {
		this.hostName = hostname;
	}
	
	public int getRadioType() {
		return radioType;
	}

	public void setRadioType(int radioType) {
		this.radioType = radioType;
	}

	public String getOsname() {
		return osname;
	}

	public void setOsname(String osname) {
		this.osname = osname;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getUserProfileName() {
		return userProfileName;
	}

	public void setUserProfileName(String userprofilename) {
		this.userProfileName = userprofilename;
	}	
}