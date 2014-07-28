/**
 *@filename		LicenseServerSetting.java
 *@version
 *@author		Fiona
 *@createtime	Apr 15, 2009 10:13:20 AM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.be.activation.BeActivationModule;
import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "LICENSE_SERVER_SETTING")
public class LicenseServerSetting implements HmBo {

	private static final long serialVersionUID = 1L;
	
	// the default license server url
	public static final String DEFAULT_LICENSE_SERVER_URL = "hmupdates.aerohive.com";
	
	// the default license server port
	public static final int DEFAULT_LICENSE_SERVER_PORT = 443;
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String lserverUrl = NmsUtil.getOEMCustomer().getDefaultLsUrl();
	
	private boolean sendStatistic = true;
	
//	private boolean sendDataCollection = true;
	
	private int hoursUsed = 0;
	
	private boolean availableSoftToUpdate;
	
	// the unit is days
	private int apTimerInterval = 15;
	
	@Version
	private Timestamp version;

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
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return "LicenseServerSetting";
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

	public String getLserverUrl() {
		return lserverUrl;
	}

	public void setLserverUrl(String lserverUrl) {
		this.lserverUrl = lserverUrl;
	}

	public boolean isSendStatistic() {
		return sendStatistic;
	}

	public void setSendStatistic(boolean sendStatistic) {
		this.sendStatistic = sendStatistic;
	}
	
	@Transient
	public int getStatisticPeriodLeft() {
		return BeActivationModule.SEND_VERSION_INFO_DEFAULT_PERIOD-hoursUsed;
	}

	public int getHoursUsed() {
		return hoursUsed;
	}

	public void setHoursUsed(int hoursUsed) {
		this.hoursUsed = hoursUsed;
	}

	public boolean isAvailableSoftToUpdate() {
		return availableSoftToUpdate;
	}

	public void setAvailableSoftToUpdate(boolean availableSoftToUpdate) {
		this.availableSoftToUpdate = availableSoftToUpdate;
	}

	public int getApTimerInterval()
	{
		return apTimerInterval;
	}

	public void setApTimerInterval(int apTimerInterval)
	{
		this.apTimerInterval = apTimerInterval;
	}
	
//	public boolean isSendDataCollection() {
//		return sendDataCollection;
//	}
//
//	public void setSendDataCollection(boolean sendDataCollection) {
//		this.sendDataCollection = sendDataCollection;
//	}

}