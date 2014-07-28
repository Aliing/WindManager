package com.ah.bo.performance;

import java.sql.Timestamp;

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

import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "HM_INTERFERENCESTATS")
@org.hibernate.annotations.Table(appliesTo = "HM_INTERFERENCESTATS", indexes = {
		@Index(name = "INTERFERENCE_STATS_OWNER", columnNames = { "OWNER" })
		})
public class AhInterferenceStats implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long	id;

	private String	apMac;

	private String  apName;
	
	private String  ifName;
	
	private int		ifIndex;

	private short	channelNumber;

	private byte	interferenceCUThreshold;

	private byte	crcErrorRateThreshold;

	private byte	severity;

	private byte	averageTXCU;

	private byte	averageRXCU;

	private byte	averageInterferenceCU;

	private byte	averageNoiseFloor;

	private byte	shortTermTXCU;

	private byte	shortTermRXCU;

	private byte	shortTermInterferenceCU;

	private byte	shortTermNoiseFloor;

	private byte	snapShotTXCU;

	private byte	snapShotRXCU;

	private byte	snapShotInterferenceCU;

	private byte	snapShotNoiseFloor;

	private byte	crcError;

//	private Date	statTime;
	
	private HmTimeStamp	timeStamp = HmTimeStamp.CURRENT_TIMESTAMP;

	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return apMac + "_ahInterferenceStats";
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;

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

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public short getChannelNumber() {
		return channelNumber;
	}

	public void setChannelNumber(short channelNumber) {
		this.channelNumber = channelNumber;
	}

	public byte getCrcError() {
		return crcError;
	}

	public void setCrcError(byte crcError) {
		this.crcError = crcError;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	public byte getAverageInterferenceCU() {
		return averageInterferenceCU;
	}

	public void setAverageInterferenceCU(byte interferenceChannelUtil) {
		this.averageInterferenceCU = interferenceChannelUtil;
	}

	public byte getAverageNoiseFloor() {
		return averageNoiseFloor;
	}

	public void setAverageNoiseFloor(byte noiseFloor) {
		this.averageNoiseFloor = noiseFloor;
	}

	public byte getAverageRXCU() {
		return averageRXCU;
	}

	public void setAverageRXCU(byte rxChannelUtil) {
		this.averageRXCU = rxChannelUtil;
	}

//	public Date getStatTime() {
//		return statTime;
//	}
//	
	public String getStatTimeString() { 
		String sts = AhDateTimeUtil.getFormattedDateTimeReport(timeStamp);
		return sts == null ? "" : sts;
	}
//
//	public void setStatTime(Date statTime) {
//		this.statTime = statTime;
//	}

	public byte getAverageTXCU() {
		return averageTXCU;
	}

	public void setAverageTXCU(byte txChannelUtil) {
		this.averageTXCU = txChannelUtil;
	}

	public byte getShortTermInterferenceCU() {
		return shortTermInterferenceCU;
	}

	public void setShortTermInterferenceCU(byte shortTermInterferenceCU) {
		this.shortTermInterferenceCU = shortTermInterferenceCU;
	}

	public byte getShortTermNoiseFloor() {
		return shortTermNoiseFloor;
	}

	public void setShortTermNoiseFloor(byte shortTermNoiseFloor) {
		this.shortTermNoiseFloor = shortTermNoiseFloor;
	}

	public byte getShortTermRXCU() {
		return shortTermRXCU;
	}

	public void setShortTermRXCU(byte shortTermRXCU) {
		this.shortTermRXCU = shortTermRXCU;
	}

	public byte getShortTermTXCU() {
		return shortTermTXCU;
	}

	public void setShortTermTXCU(byte shortTermTXCU) {
		this.shortTermTXCU = shortTermTXCU;
	}

	public byte getSnapShotInterferenceCU() {
		return snapShotInterferenceCU;
	}

	public void setSnapShotInterferenceCU(byte snapShotInterferenceCU) {
		this.snapShotInterferenceCU = snapShotInterferenceCU;
	}

	public byte getSnapShotNoiseFloor() {
		return snapShotNoiseFloor;
	}

	public void setSnapShotNoiseFloor(byte snapShotNoiseFloor) {
		this.snapShotNoiseFloor = snapShotNoiseFloor;
	}

	public byte getSnapShotRXCU() {
		return snapShotRXCU;
	}

	public void setSnapShotRXCU(byte snapShotRXCU) {
		this.snapShotRXCU = snapShotRXCU;
	}

	public byte getSnapShotTXCU() {
		return snapShotTXCU;
	}

	public void setSnapShotTXCU(byte snapShotTXCU) {
		this.snapShotTXCU = snapShotTXCU;
	}

	public byte getCrcErrorRateThreshold() {
		return crcErrorRateThreshold;
	}

	public void setCrcErrorRateThreshold(byte crcErrorRateThreshold) {
		this.crcErrorRateThreshold = crcErrorRateThreshold;
	}

	public byte getInterferenceCUThreshold() {
		return interferenceCUThreshold;
	}

	public void setInterferenceCUThreshold(byte interferenceCUThreshold) {
		this.interferenceCUThreshold = interferenceCUThreshold;
	}

	public byte getSeverity() {
		return severity;
	}

	public void setSeverity(byte severity) {
		this.severity = severity;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public String getIfName() {
		return ifName;
	}

	public void setIfName(String ifName) {
		this.ifName = ifName;
	}

	public HmTimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(HmTimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}

}