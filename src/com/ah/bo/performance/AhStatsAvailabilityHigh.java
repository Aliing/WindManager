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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name="AH_STATS_AVAILABILITY_HIGH")
@org.hibernate.annotations.Table(appliesTo = "AH_STATS_AVAILABILITY_HIGH", indexes = {
		@Index(name = "AVAILABILITY_HIGH_OWNER", columnNames = { "OWNER" }),
		@Index(name = "AVAILABILITY_HIGH_MAC", columnNames = { "MAC" })
		})
public class AhStatsAvailabilityHigh implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;
	
	@Column(length = 12, nullable = false)
	private String mac;
	
	private String hostName;
	
	private String sid;
	
	private long time;
	
	private String interfName;
	
	private byte interfStatus;
	
	private byte interfActive;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getInterfName() {
		return interfName;
	}

	public void setInterfName(String interfName) {
		this.interfName = interfName;
	}

	public byte getInterfStatus() {
		return interfStatus;
	}

	public void setInterfStatus(byte interfStatus) {
		this.interfStatus = interfStatus;
	}

	public byte getInterfActive() {
		return interfActive;
	}

	public void setInterfActive(byte interfActive) {
		this.interfActive = interfActive;
	}
	
	public String getKey() {
		return time + "_" + interfName + "_" + interfStatus + "_" + interfActive;
	}
}