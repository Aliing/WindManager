/**
 *@filename		LicenseHistoryInfo.java
 *@version
 *@author		Fiona
 *@createtime	2008-6-13 PM 03:01:03
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 * 2009-01-19
 * this table will contain all license information not only the history
 * remove the field of owner for HA
 *
 */
package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.bo.HmBo;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "LICENSE_HISTORY_INFO")
public class LicenseHistoryInfo implements HmBo {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String systemId;
	
	private String licenseString;
	
	private boolean active;
	
	public static final short LICENSE_TYPE_HIVEMANAGER = 1;
	
	public static final short LICENSE_TYPE_GM_LITE = 2;
	
	private short type = LICENSE_TYPE_HIVEMANAGER;
	
	private String hoursUsed;

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getLicenseString() {
		return licenseString;
	}

	public void setLicenseString(String licenseString) {
		this.licenseString = licenseString;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getHoursUsed() {
		return hoursUsed;
	}

	public void setHoursUsed(String hoursUsed) {
		this.hoursUsed = hoursUsed;
	}

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
		return "LicenseHistoryInfo";
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

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

}