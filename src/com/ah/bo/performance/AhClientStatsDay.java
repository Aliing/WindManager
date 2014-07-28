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
@Table(name = "hm_client_stats_day")
@org.hibernate.annotations.Table(appliesTo = "hm_client_stats_day", indexes = {
		@Index(name = "IDX_CLIENT_STATS_DAY_TIMESTAMP", columnNames = {"TIMESTAMP", "APMAC", "OWNER"}),
		@Index(name = "IDX_CLIENT_STATS_DAY_OWNER", columnNames = {"OWNER"})
		})
public class AhClientStatsDay implements AhClientStatsInterf {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String				apName;

	@Column(length = MAC_ADDRESS_LENGTH)
	private String				apMac;

	private long		timeStamp;
	
	@Column(length = MAC_ADDRESS_LENGTH)
	private String		clientMac;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String		ssidName;

	private int			bandWidthUsage;

	private int			txFrameDropped;

	private int			rxFrameDropped;

	private long		txFrameByteCount;

	private long		rxFrameByteCount;

	private byte		txAirTime;

	private byte		rxAirTime;

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
		return "client_stats_day";
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

	public String getClientMac() {
		return clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

	public byte getRxAirTime() {
		return rxAirTime;
	}

	public void setRxAirTime(byte rxAirTime) {
		this.rxAirTime = rxAirTime;
	}

	public int getRxFrameDropped() {
		return rxFrameDropped;
	}

	public void setRxFrameDropped(int rxFrameDropped) {
		this.rxFrameDropped = rxFrameDropped;
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
		return AhDateTimeUtil.getSpecifyDateTimeReport(timeStamp, tz);
	}

	public byte getTxAirTime() {
		return txAirTime;
	}

	public void setTxAirTime(byte txAirTime) {
		this.txAirTime = txAirTime;
	}

	public int getTxFrameDropped() {
		return txFrameDropped;
	}

	public void setTxFrameDropped(int txFrameDropped) {
		this.txFrameDropped = txFrameDropped;
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

}