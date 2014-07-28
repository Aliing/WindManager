/**
 * @filename			LocationRssiReport.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.bo.monitor;

import java.sql.Timestamp;
import java.util.Date;

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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

/**
 * BO of RSSI report in location tracking
 */
@Entity
@Table(name = "LOCATION_RSSI_REPORT")
@org.hibernate.annotations.Table(appliesTo = "LOCATION_RSSI_REPORT", indexes = {
		@Index(name = "LOCATION_RSSI_REPORT_OWNER", columnNames = { "OWNER" }),
		@Index(name = "LOCATION_RSSI_REPORT_CLIENT_REPORTER", columnNames = { "CLIENTMAC", "REPORTERMAC" }) })
public class LocationRssiReport implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	//	private Timestamp version;
	@Transient
	private boolean selected;

	@Column(length = MAC_ADDRESS_LENGTH, nullable = false)
	private String clientMac;

	@Column(length = MAC_ADDRESS_LENGTH, nullable = false)
	private String reporterMac;

	private int channel;

	private byte rssi;

	private Date reportTime;

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#getId()
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#getVersion()
	 */
	@Override
	public Timestamp getVersion() {
		// return this.version;
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#isSelected()
	 */
	@Override
	public boolean isSelected() {
		return this.selected;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#setVersion(java.sql.Timestamp)
	 */
	@Override
	public void setVersion(Timestamp version) {
		//	this.version = version;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#getLabel()
	 */
	@Override
	public String getLabel() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#getOwner()
	 */
	@Override
	public HmDomain getOwner() {
		return this.owner;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#setOwner(com.ah.bo.admin.HmDomain)
	 */
	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	/**
	 * getter of clientMac
	 * @return the clientMac
	 */
	public String getClientMac() {
		return clientMac;
	}

	/**
	 * setter of clientMac
	 * @param clientMac the clientMac to set
	 */
	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

	/**
	 * getter of reporterMac
	 * @return the reporterMac
	 */
	public String getReporterMac() {
		return reporterMac;
	}

	/**
	 * setter of reporterMac
	 * @param reporterMac the reporterMac to set
	 */
	public void setReporterMac(String reporterMac) {
		this.reporterMac = reporterMac;
	}

	/**
	 * getter of channel
	 * @return the channel
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * setter of channel
	 * @param channel the channel to set
	 */
	public void setChannel(int channel) {
		this.channel = channel;
	}

	/**
	 * getter of rssi
	 * @return the rssi
	 */
	public byte getRssi() {
		return rssi;
	}

	/**
	 * setter of rssi
	 * @param rssi the rssi to set
	 */
	public void setRssi(byte rssi) {
		this.rssi = rssi;
	}

	/**
	 * getter of reportTime
	 * @return the reportTime
	 */
	public Date getReportTime() {
		return reportTime;
	}

	/**
	 * setter of reportTime
	 * @param reportTime the reportTime to set
	 */
	public void setReportTime(Date reportTime) {
		this.reportTime = reportTime;
	}

	@Override
	public String toString() {
		return "Reporter: " + reporterMac + "; Client: " + clientMac + "; Channel: " + channel + "; RSSI: " + rssi + "; Report Time: " + reportTime;
	}

}