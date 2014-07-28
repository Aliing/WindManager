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
@Table(name = "AH_STATS_LATENCY_HIGH")
@org.hibernate.annotations.Table(appliesTo = "AH_STATS_LATENCY_HIGH", indexes = {
		@Index(name = "LATENCY_HIGH_OWNER", columnNames = { "OWNER" }),
		@Index(name = "LATENCY_HIGH_MAC", columnNames = { "MAC" })
		})
public class AhStatsLatencyHigh implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;
	
	@Column(length = 12, nullable = false)
	private String mac;
	
	private String hostName;
	
	private String sid;
	
	private long time;

	private byte interfType;
	
	private String interfName;
	
	private String interfServer;
	
	private String name;
	
	private double rtt;                                                                       
	
	public static byte TARGET_STATUS_UP=0;
	public static byte TARGET_STATUS_DOWN=1;
	private byte targetStatus;
	
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

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

	public byte getInterfType() {
		return interfType;
	}

	public void setInterfType(byte interfType) {
		this.interfType = interfType;
	}

	public String getInterfName() {
		return interfName;
	}

	public void setInterfName(String interfName) {
		this.interfName = interfName;
	}

	public String getInterfServer() {
		return interfServer;
	}

	public void setInterfServer(String interfServer) {
		this.interfServer = interfServer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRtt() {
		return rtt;
	}

	public void setRtt(double rtt) {
		this.rtt = rtt;
	}

	public byte getTargetStatus() {
		return targetStatus;
	}

	public void setTargetStatus(byte targetStatus) {
		this.targetStatus = targetStatus;
	}

	public String getKey() {
		return time + "_" + interfType + "_" + interfName + "_" + interfServer + "_" + name;
	}
}
