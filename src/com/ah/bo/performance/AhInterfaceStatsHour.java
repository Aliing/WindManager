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
@Table(name = "hm_interface_stats_hour")
@org.hibernate.annotations.Table(appliesTo = "hm_interface_stats_hour", indexes = {
		@Index(name = "IDX_INTERFACESTATS_HOUR_TIMESTAMP", columnNames = {"timeStamp","APMAC","OWNER"}),
		@Index(name = "IDX_INTERFACESTATS_HOUR_OWNER", columnNames = {"OWNER"})
		})
public class AhInterfaceStatsHour implements AhInterfaceStatsInterf {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long			id;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String				apName;

	@Column(length = MAC_ADDRESS_LENGTH)
	private String				apMac;

	private long			timeStamp;

	private long			txDrops;

	private long			rxDrops;
	
	private long			uniTxFrameCount;

	private long			uniRxFrameCount;

	private byte			crcErrorRate;

	private byte			txRetryRate;

	private byte			rxRetryRate;

	private byte			totalChannelUtilization;

	private byte			interferenceUtilization;

	private byte			txUtilization;

	private byte			rxUtilization;

	private short			noiseFloor;

	private byte			txAirTime;

	private byte			rxAirTime;

	private long 			txByteCount;
	
	private long 			rxByteCount;
	
	//0:wifi0, 1: wifi1
	private String 			ifName;
	
	private int 			radioType;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain		owner;

	@Transient
	private TimeZone		tz;

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
		return "interface_stats_hour";
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

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getTimeStampString() {
		return AhDateTimeUtil.getSpecifyDateTimeReport(timeStamp, tz);
	}

	public byte getCrcErrorRate() {
		return crcErrorRate;
	}

	public void setCrcErrorRate(byte crcErrorRate) {
		this.crcErrorRate = crcErrorRate;
	}

	public byte getInterferenceUtilization() {
		return interferenceUtilization;
	}

	public void setInterferenceUtilization(byte interferenceUtilization) {
		this.interferenceUtilization = interferenceUtilization;
	}

	public short getNoiseFloor() {
		return noiseFloor;
	}

	public void setNoiseFloor(short noiseFloor) {
		this.noiseFloor = noiseFloor;
	}

	public byte getRxAirTime() {
		return rxAirTime;
	}

	public void setRxAirTime(byte rxAirTime) {
		this.rxAirTime = rxAirTime;
	}

	public long getRxDrops() {
		return rxDrops;
	}

	public void setRxDrops(long rxDrops) {
		this.rxDrops = rxDrops;
	}

	public byte getRxRetryRate() {
		return rxRetryRate;
	}

	public void setRxRetryRate(byte rxRetryRate) {
		this.rxRetryRate = rxRetryRate;
	}

	public byte getRxUtilization() {
		return rxUtilization;
	}

	public void setRxUtilization(byte rxUtilization) {
		this.rxUtilization = rxUtilization;
	}

	public byte getTotalChannelUtilization() {
		return totalChannelUtilization;
	}

	public void setTotalChannelUtilization(byte totalChannelUtilization) {
		this.totalChannelUtilization = totalChannelUtilization;
	}

	public byte getTxAirTime() {
		return txAirTime;
	}

	public void setTxAirTime(byte txAirTime) {
		this.txAirTime = txAirTime;
	}

	public long getTxDrops() {
		return txDrops;
	}

	public void setTxDrops(long txDrops) {
		this.txDrops = txDrops;
	}

	public byte getTxRetryRate() {
		return txRetryRate;
	}

	public void setTxRetryRate(byte txRetryRate) {
		this.txRetryRate = txRetryRate;
	}

	public byte getTxUtilization() {
		return txUtilization;
	}

	public void setTxUtilization(byte txUtilization) {
		this.txUtilization = txUtilization;
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

	public TimeZone getTz() {
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}

	public long getTxByteCount() {
		return txByteCount;
	}

	public void setTxByteCount(long txByteCount) {
		this.txByteCount = txByteCount;
	}

	public long getRxByteCount() {
		return rxByteCount;
	}

	public void setRxByteCount(long rxByteCount) {
		this.rxByteCount = rxByteCount;
	}

	public String getIfName() {
		return ifName;
	}

	public void setIfName(String ifName) {
		this.ifName = ifName;
	}
	
	public long getUniTxFrameCount() {
		return uniTxFrameCount;
	}

	public void setUniTxFrameCount(long uniTxFrameCount) {
		this.uniTxFrameCount = uniTxFrameCount;
	}

	public long getUniRxFrameCount() {
		return uniRxFrameCount;
	}

	public void setUniRxFrameCount(long uniRxFrameCount) {
		this.uniRxFrameCount = uniRxFrameCount;
	}

	public int getRadioType() {
		return radioType;
	}

	public void setRadioType(int radioType) {
		this.radioType = radioType;
	}

}