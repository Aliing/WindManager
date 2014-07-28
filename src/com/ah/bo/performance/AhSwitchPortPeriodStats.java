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
@Table(name = "HM_SWITCH_PORT_PERIOD_STATS")
@org.hibernate.annotations.Table(appliesTo = "HM_SWITCH_PORT_PERIOD_STATS", indexes = {
		@Index(name = "SWITCH_PORT_PERIOD_STATS_OWNER", columnNames = { "OWNER"}),
		@Index(name = "SWITCH_PORT_PERIOD_STATS_TIMESTAMP", columnNames = { "timestamp"})
		})
public class AhSwitchPortPeriodStats implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String apmac;
	
	private String hostname;
	
	private long timestamp;
	
	//unit is seconds
	private long collectPeriod;

	@Column(length = 128)
	private String portName;

	private long txPacketCount;

	private long rxPacketCount;

	private long txBytesCount;
	
	private long rxBytesCount;

	private long txUnicastPackets;

	private long rxUnicastPackets;
	
	private long txMuticastPackets;
	
	private long rxMuticastPackets;

	private long txBroadcastPackets;
	
	private long rxBroadcastPackets;
	
	private float utilization;


	// --------implement interface function--------
	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return "AhSwitchPortStats";
	}

	// For multi page selection
	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/*
	 * statistic objects have no owner or version,because it created by system,
	 * it can't be updated and 'statTime' keep track of when create it.
	 */
	/**
	 * modify mark: add owner field for VHM
	 */

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
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public void setId(Long id)
	{
		this.id=id;
	}

	@Override
	public void setVersion(Timestamp version) {
	}
	
	public String getApmac() {
		return apmac;
	}

	public void setApmac(String apmac) {
		this.apmac = apmac;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getCollectPeriod() {
		return collectPeriod;
	}

	public void setCollectPeriod(long collectPeriod) {
		this.collectPeriod = collectPeriod;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public long getTxPacketCount() {
		return txPacketCount;
	}

	public void setTxPacketCount(long txPacketCount) {
		this.txPacketCount = txPacketCount;
	}

	public long getTxBytesCount() {
		return txBytesCount;
	}

	public void setTxBytesCount(long txBytesCount) {
		this.txBytesCount = txBytesCount;
	}

	public long getRxPacketCount() {
		return rxPacketCount;
	}

	public void setRxPacketCount(long rxPacketCount) {
		this.rxPacketCount = rxPacketCount;
	}

	public long getRxBytesCount() {
		return rxBytesCount;
	}

	public void setRxBytesCount(long rxBytesCount) {
		this.rxBytesCount = rxBytesCount;
	}

	public long getTxUnicastPackets() {
		return txUnicastPackets;
	}

	public void setTxUnicastPackets(long txUnicastPackets) {
		this.txUnicastPackets = txUnicastPackets;
	}

	public long getRxUnicastPackets() {
		return rxUnicastPackets;
	}

	public void setRxUnicastPackets(long rxUnicastPackets) {
		this.rxUnicastPackets = rxUnicastPackets;
	}

	public long getTxMuticastPackets() {
		return txMuticastPackets;
	}

	public void setTxMuticastPackets(long txMuticastPackets) {
		this.txMuticastPackets = txMuticastPackets;
	}

	public long getRxMuticastPackets() {
		return rxMuticastPackets;
	}

	public void setRxMuticastPackets(long rxMuticastPackets) {
		this.rxMuticastPackets = rxMuticastPackets;
	}

	public long getTxBroadcastPackets() {
		return txBroadcastPackets;
	}

	public void setTxBroadcastPackets(long txBroadcastPackets) {
		this.txBroadcastPackets = txBroadcastPackets;
	}

	public long getRxBroadcastPackets() {
		return rxBroadcastPackets;
	}

	public void setRxBroadcastPackets(long rxBroadcastPackets) {
		this.rxBroadcastPackets = rxBroadcastPackets;
	}

	public float getUtilization() {
		return utilization;
	}

	public void setUtilization(float utilization) {
		this.utilization = utilization;
	}
	
}