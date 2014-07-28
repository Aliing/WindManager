/**
 *@filename		ActivationKeyInfo.java
 *@version
 *@author		Fiona
 *@createtime	2009-04-08 PM 03:01:03
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 */
package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.be.license.LicenseOperationTool;
import com.ah.bo.HmBo;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "ACTIVATION_KEY_INFO")
public class ActivationKeyInfo implements HmBo {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;

	@Column(length = 35)
	private String activationKey = "";
		
	// it is the grace period if the activation key is null, the unit is day
	private int queryPeriod;
	
	private byte queryRetryTime = 3;
	
	// the unit is hour
	private int queryInterval = 3;
	
	private boolean startRetryTimer;
	
	private byte hasRetryTime = 0;
	
	private boolean activateSuccess;
	
	@Column(length = 39)
	private String systemId;
	
	private String hoursUsed;
	
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
		return "ActivationKeyInfo";
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

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}	

	public int getQueryInterval() {
		return queryInterval;
	}
	
	public void setQueryInterval(int queryInterval) {
		this.queryInterval = queryInterval;
	}
	
	@Transient
	public int getQueryPeriodLeft() {
		return queryPeriod*24-getDecriptInt(hoursUsed);
	}
	
	@Transient
	public int getRetryIntervalLeft() {
		return queryInterval-getDecriptInt(hoursUsed);
	}
	
	@Transient
	public int getDecriptInt(String encript) {
		if (null == hoursUsed || "".equals(hoursUsed)) {
			return 0;
		} else {
			return LicenseOperationTool.getDecryptedHours(encript, systemId);
		}
	}
	
	@Transient
	public String getEncriptString(int descript) {
		return LicenseOperationTool.getEncryptedHours(descript, systemId);
	}

	public byte getQueryRetryTime() {
		return queryRetryTime;
	}

	public void setQueryRetryTime(byte queryRetryTime) {
		this.queryRetryTime = queryRetryTime;
	}

	public boolean isStartRetryTimer() {
		return startRetryTimer;
	}

	public void setStartRetryTimer(boolean startRetryTimer) {
		this.startRetryTimer = startRetryTimer;
	}

	public byte getHasRetryTime() {
		return hasRetryTime;
	}

	public void setHasRetryTime(byte hasRetryTime) {
		this.hasRetryTime = hasRetryTime;
	}

	public boolean isActivateSuccess() {
		return activateSuccess;
	}

	public void setActivateSuccess(boolean activateSuccess) {
		this.activateSuccess = activateSuccess;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getHoursUsed() {
		return hoursUsed;
	}

	public void setHoursUsed(String hoursUsed) {
		this.hoursUsed = hoursUsed;
	}
	
	/**
	 * Increase the used hours after an hour for activation key grace period or query.
	 */
	public void changeTheUsedHours() {
		// the hour used does not exist
		if (null == hoursUsed || "".equals(hoursUsed)) {
			setHoursUsed(getEncriptString(1));
			
			// the hour used exists, increase the hours
		} else {
			setHoursUsed(getEncriptString(getDecriptInt(hoursUsed)+1));
		}
	}
	
	/**
	 * Init the used hours after input one new activation key or one period query.
	 */
	public void initTheUsedHours() {
		setHoursUsed(getEncriptString(0));
	}

	public int getQueryPeriod() {
		return queryPeriod;
	}

	public void setQueryPeriod(int queryPeriod) {
		this.queryPeriod = queryPeriod;
	}
	
}