package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.bo.HmBo;

@Entity
@Table(name = "hm_capwapclient")
public class CapwapClient implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long				id;

	public static final byte	SERVERTYPE_PORTAL	= 1;

	private byte				serverType			= SERVERTYPE_PORTAL;

	private boolean				capwapEnable;

	private String				primaryCapwapIP		= "";

	private String				backupCapwapIP		= "";

	private int					udpPort;

	private short				timeOut;

	private short				neighborDeadInterval;

	private boolean				dtlsEnable = true;

	@Column(length = 32)
	private String				passphrase;
	
	private byte				transportMode = 1;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
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

	@Version
	private Timestamp version;

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return (serverType == SERVERTYPE_PORTAL ? "Portal" : "") + " Capwap Settings";
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
	}

	public String getBackupCapwapIP() {
		return backupCapwapIP;
	}

	public void setBackupCapwapIP(String backupCapwapIP) {
		this.backupCapwapIP = backupCapwapIP;
	}

	public boolean isCapwapEnable() {
		return capwapEnable;
	}

	public void setCapwapEnable(boolean capwapEnable) {
		this.capwapEnable = capwapEnable;
	}

	public boolean isDtlsEnable() {
		return dtlsEnable;
	}

	public void setDtlsEnable(boolean dtlsEnable) {
		this.dtlsEnable = dtlsEnable;
	}

	public short getNeighborDeadInterval() {
		return neighborDeadInterval;
	}

	public void setNeighborDeadInterval(short neighborDeadInterval) {
		this.neighborDeadInterval = neighborDeadInterval;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public String getPrimaryCapwapIP() {
		return primaryCapwapIP;
	}

	public void setPrimaryCapwapIP(String primaryCapwapIP) {
		this.primaryCapwapIP = primaryCapwapIP;
	}

	public byte getServerType() {
		return serverType;
	}

	public void setServerType(byte serverType) {
		this.serverType = serverType;
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

	public byte getTransportMode() {
		return transportMode;
	}

	public void setTransportMode(byte transportMode) {
		this.transportMode = transportMode;
	}

}