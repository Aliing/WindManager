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

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "hm_device_stats")
@org.hibernate.annotations.Table(appliesTo = "hm_device_stats", indexes = {
		@Index(name = "IDX_INTERFACESTATS_TIMESTAMP", columnNames = {"timeStamp","apMac","OWNER"}),
		@Index(name = "IDX_INTERFACESTATS_OWNER", columnNames = {"OWNER"})
		})
public class AhDeviceStats implements HmBo{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long			id;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String				apName;

	@Column(length = MAC_ADDRESS_LENGTH)
	private String				apMac;

	private long				timeStamp;

	private short				collectPeriod;

	private byte 				averageCpu;
	private byte 				maxCpu;
	private byte 				averageMem;
	private byte 				maxMem;
	
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
		return "interface_stats";
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

	public short getCollectPeriod() {
		return collectPeriod;
	}

	public void setCollectPeriod(short collectPeriod) {
		this.collectPeriod = collectPeriod;
	}

	public String getTimeStampString() {
		return AhDateTimeUtil.getSpecifyDateTimeReport(timeStamp, tz);
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



	public byte getAverageMem() {
		return averageMem;
	}

	public void setAverageMem(byte averageMem) {
		this.averageMem = averageMem;
	}

	public byte getMaxMem() {
		return maxMem;
	}

	public void setMaxMem(byte maxMem) {
		this.maxMem = maxMem;
	}

	public byte getAverageCpu() {
		return averageCpu;
	}

	public void setAverageCpu(byte averageCpu) {
		this.averageCpu = averageCpu;
	}

	public byte getMaxCpu() {
		return maxCpu;
	}

	public void setMaxCpu(byte maxCpu) {
		this.maxCpu = maxCpu;
	}

}