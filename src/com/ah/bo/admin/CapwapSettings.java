package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.bo.HmBo;

@Entity
@Table(name = "CAPWAPSETTINGS")
public class CapwapSettings implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long				id;

	private int					udpPort;

	private short				timeOut;

	private short				neighborDeadInterval;

	/*
	 * DTLS Capability
	 */
	public static final byte	DTLS_NODTLS		= 0;

	public static final byte	DTLS_DTLSONLY	= 1;

	public static final byte	DTLS_AUTO		= 2;

	private byte				dtlsCapability;

	@Column(length = 32)
	private String				bootStrap;

	private short				trapFilterInterval;

	private String				primaryCapwapIP;

	private String				backupCapwapIP;
	
	private boolean				enableRollback = true;

	public String getBootStrap() {
		return bootStrap;
	}

	public void setBootStrap(String bootStrap) {
		this.bootStrap = bootStrap;
	}

	public short getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(short timeOut) {
		this.timeOut = timeOut;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	// ------------implement interface
	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return "CAPWAP Settings";
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain	owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;

	@Override
	public Timestamp getVersion() {
		return version;
	}

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

	public short getNeighborDeadInterval() {
		return neighborDeadInterval;
	}

	public void setNeighborDeadInterval(short neighborDeadInterval) {
		this.neighborDeadInterval = neighborDeadInterval;
	}

	public byte getDtlsCapability() {
		return dtlsCapability;
	}

	public void setDtlsCapability(byte dtlsCapability) {
		this.dtlsCapability = dtlsCapability;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public short getTrapFilterInterval() {
		return trapFilterInterval;
	}

	public void setTrapFilterInterval(short trapFilterInterval) {
		this.trapFilterInterval = trapFilterInterval;
	}

	public String getBackupCapwapIP() {
		return backupCapwapIP;
	}

	public void setBackupCapwapIP(String backupCapwapIP) {
		this.backupCapwapIP = backupCapwapIP;
	}

	public String getPrimaryCapwapIP() {
		return primaryCapwapIP;
	}

	public void setPrimaryCapwapIP(String primaryCapwapIP) {
		this.primaryCapwapIP = primaryCapwapIP;
	}

	public boolean isEnableRollback() {
		return enableRollback;
	}

	public void setEnableRollback(boolean enableRollback) {
		this.enableRollback = enableRollback;
	}

}