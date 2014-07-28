/**
 *@filename		HmNtpServerAndInterval.java
 *@version		v1.0
 *@author		Fiona
 *@createtime	2008-04-22 AM 10:44:08
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Range;

import com.ah.be.os.BeOsLayerModule;
import com.ah.bo.HmBo;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "HM_NTP_SERVER_INTERVAL")
public class HmNtpServerAndInterval implements HmBo  {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private short timeType = BeOsLayerModule.STOP_NTP_SERVICE;

	public static final String DEFAULT_NTP_SERVER = HmStartConfig.EXPRESS_MODE_DEFAULT_NTP_SERVER;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String ntpServer = "";

	@Range(min = 60, max = 10080)
	private int ntpInterval = 1440;

	// ------------implement interface
	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return "NTP Server and Interval";
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain owner;

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
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public short getTimeType() {
		return timeType;
	}

	public void setTimeType(short timeType) {
		this.timeType = timeType;
	}

	public String getNtpServer() {
		return ntpServer;
	}

	public void setNtpServer(String ntpServer) {
		this.ntpServer = ntpServer;
	}

	public int getNtpInterval() {
		return ntpInterval;
	}

	public void setNtpInterval(int ntpInterval) {
		this.ntpInterval = ntpInterval;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

}