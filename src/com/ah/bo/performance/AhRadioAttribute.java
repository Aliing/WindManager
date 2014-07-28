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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "HM_RADIOATTRIBUTE",uniqueConstraints = { @UniqueConstraint( columnNames = { "statTimeStamp", "apName", "ifIndex" } ) })
@org.hibernate.annotations.Table(appliesTo = "HM_RADIOATTRIBUTE", indexes = {
		@Index(name = "RADIO_ATTRIBUTE_OWNER", columnNames = { "OWNER"})
		})
public class AhRadioAttribute implements HmBo {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private XIfPK	xifpk;

	@Column(length = 12)
	private String	apMac;

	@Column(length = 14)
	private String	apSerialNumber;

	private long	radioChannel;

	private long	radioTxPower;

	private long	radioNoiseFloor;

	private int		beaconInterval;
	
	@Column(columnDefinition = "real default 0")
	private float	eirp;
	
	public static final short RADIO_TYPE_INVALID = -1;
	/**
	 * 11a/n, 11g/n, etc.
	 */
	private short   radioType = RADIO_TYPE_INVALID;

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

	public XIfPK getXifpk() {
		return xifpk;
	}

	public void setXifpk(XIfPK xifpk) {
		this.xifpk = xifpk;
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

	// --------implement interface function--------
	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return "ahRadioAttribute";
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
		this.id=id;
	}

	@Override
	public void setVersion(Timestamp version) {
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