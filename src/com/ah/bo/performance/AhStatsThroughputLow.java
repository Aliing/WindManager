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
@Table(name="AH_STATS_THROUGHPUT_LOW")
@org.hibernate.annotations.Table(appliesTo = "AH_STATS_THROUGHPUT_LOW", indexes = {
		@Index(name = "THROUGHPUT_LOW_OWNER", columnNames = { "OWNER" }),
		@Index(name = "THROUGHPUT_LOW_MAC", columnNames = { "MAC" })
		})
public class AhStatsThroughputLow implements HmBo {

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
	
	private long rxPkts;
	
	private long txPkts;
	
	private long rxBytes;
	
	private long txBytes;
	
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

	public long getRxPkts() {
		return rxPkts;
	}

	public void setRxPkts(long rxPkts) {
		this.rxPkts = rxPkts;
	}

	public long getTxPkts() {
		return txPkts;
	}

	public void setTxPkts(long txPkts) {
		this.txPkts = txPkts;
	}

	public long getRxBytes() {
		return rxBytes;
	}

	public void setRxBytes(long rxBytes) {
		this.rxBytes = rxBytes;
	}

	public long getTxBytes() {
		return txBytes;
	}

	public void setTxBytes(long txBytes) {
		this.txBytes = txBytes;
	}

	public String getKey() {
		return time + "_" + interfType + "_" + interfName + "_" + interfServer;
	}
}
