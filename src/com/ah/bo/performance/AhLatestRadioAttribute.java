package com.ah.bo.performance;

import java.sql.Timestamp;

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

import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "HM_LATESTRADIOATTRIBUTE")
@org.hibernate.annotations.Table(appliesTo = "HM_LATESTRADIOATTRIBUTE", indexes = {
		@Index(name = "LATEST_RADIO_ATTRIBUTE_OWNER", columnNames = { "OWNER" }),
		@Index(name = "LATEST_RADIO_ATTRIBUTE_AP_MAC", columnNames = { "APMAC" })
		})
public class AhLatestRadioAttribute implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;

	@Column(length = 64, nullable = false)
	private String		apName;

	@Column(nullable = false)
	private int			ifIndex;

	@Column(nullable = false)
	private String		apMac;

	// private Date statTime;

	@Column(length = 14)
	private String		apSerialNumber;

	private long		radioChannel;

	private long		radioTxPower;

	private long		radioNoiseFloor;

	private int			beaconInterval;
	
	private float		eirp;

	private HmTimeStamp	timeStamp	= HmTimeStamp.CURRENT_TIMESTAMP;
	
	/**
	 * 11a/n, 11g/n, etc.
	 */
	private short   radioType = AhRadioAttribute.RADIO_TYPE_INVALID;

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public String getApSerialNumber() {
		return apSerialNumber;
	}

	public void setApSerialNumber(String apSerialNumber) {
		this.apSerialNumber = apSerialNumber;
	}

	public long getRadioChannel() {
		return radioChannel;
	}

	public void setRadioChannel(long radioChannel) {
		this.radioChannel = radioChannel;
	}

	public long getRadioNoiseFloor() {
		return radioNoiseFloor;
	}

	public void setRadioNoiseFloor(long radioNoiseFloor) {
		this.radioNoiseFloor = radioNoiseFloor;
	}

	public long getRadioTxPower() {
		return radioTxPower;
	}

	public void setRadioTxPower(long radioTxPower) {
		this.radioTxPower = radioTxPower;
	}

	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return "AhLatestRadioAttribute";
	}

	// For multi page selection
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

	/*
	 * statistic objects have no owner or version,because it created by system, it can't be updated
	 * and 'statTime' keep track of when create it.
	 */
	/**
	 * modify mark: add owner field for VHM
	 */

	@ManyToOne(fetch = FetchType.LAZY)
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

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	// public Date getStatTime() {
	// return statTime;
	// }
	//
	// public void setStatTime(Date statTime) {
	// this.statTime = statTime;
	// }

	public HmTimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(HmTimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getBeaconInterval() {
		return beaconInterval;
	}

	public void setBeaconInterval(int beaconInterval) {
		this.beaconInterval = beaconInterval;
	}
	
	public float getEirp() {
		return eirp;
	}

	public void setEirp(float eirp) {
		this.eirp = eirp;
	}

	public short getRadioType() {
		return radioType;
	}

	public void setRadioType(short radioType) {
		this.radioType = radioType;
	}
	
}