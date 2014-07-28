/**
 *@filename		HhmUpgradeVersionInfo.java
 *@version
 *@author		Fiona
 *@createtime	Jul 15, 2009 2:30:23 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.hhm;

import java.sql.Timestamp;

import javax.persistence.Entity;
//import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "HHM_UPGRADE_VERSION_INFO")
public class HhmUpgradeVersionInfo implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long			id;

	private String			ipAddress;

	private String			hmVersion;
	
	private int 			leftApCount;
	
	private int             leftVhmCount;
	
	private String          userName;
	
	private String          password;

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return "HM Upgrade Version Information";
	}

//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "OWNER", nullable = false)
//	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		//return owner;
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
		//this.owner = owner;
	}

	@Version
	private Timestamp	version;

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
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
	public void setId(Long id) {
		this.id = id;
	}

	public String getHmVersion() {
		return hmVersion;
	}

	public void setHmVersion(String hmVersion) {
		this.hmVersion = hmVersion;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getLeftApCount() {
		return leftApCount;
	}

	public void setLeftApCount(int leftApCount) {
		this.leftApCount = leftApCount;
	}

	public int getLeftVhmCount() {
		return leftVhmCount;
	}

	public void setLeftVhmCount(int leftVhmCount) {
		this.leftVhmCount = leftVhmCount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}