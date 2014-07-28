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
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "HM_SPECTRAL_ANALYSIS")
@org.hibernate.annotations.Table(appliesTo = "HM_SPECTRAL_ANALYSIS", indexes = {
		@Index(name = "SPECTRAL_ANALYSIS_OWNER", columnNames = { "OWNER" }),
		@Index(name = "SPECTRAL_ANALYSIS_AP_MAC_TIMESTAMP", columnNames = { "APMAC", "TIMESTAMP"})
		})
public class AhSpectralAnalysis implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;

	@Column(nullable = false, length=12)
	private String		apMac;
	
	private String		apName;
	
	private long		timeStamp;
	
	private String		timeZone;
	
	private byte 		interf;
	
	private String 		channel0;
	
	private String 		channel1;
	
	private short		interval;
	
	private long		runTime;
	
	private String		dataFile;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable=false)
	private HmDomain	owner;

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
		return "hm_spectral_analysis";
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

	public byte getInterf() {
		return interf;
	}

	public void setInterf(byte interf) {
		this.interf = interf;
	}

	public short getInterval() {
		return interval;
	}

	public void setInterval(short interval) {
		this.interval = interval;
	}

	public long getRunTime() {
		return runTime;
	}

	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}

	public String getDataFile() {
		return dataFile;
	}

	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getChannel0() {
		return channel0;
	}

	public void setChannel0(String channel0) {
		this.channel0 = channel0;
	}

	public String getChannel1() {
		return channel1;
	}

	public void setChannel1(String channel1) {
		this.channel1 = channel1;
	}

}