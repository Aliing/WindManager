/**
 * @filename			AirtightSettings.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
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

import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;

/**
 * BO for Airtight integration settings
 */
@Entity
@Table(name = "AIR_TIGHT_SETTINGS")
public class AirtightSettings implements HmBo {

	private static final long	serialVersionUID	= 1L;
	
	private boolean enabled;
	
	@Column(length = 128)
	private String serverURL; 

	@Column(length = DEFAULT_STRING_LENGTH)
	private String userName;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String password;
	
	public static final int DEFAULT_SYNC_INTERVAL = 30; // minute
	
	/*
	 * when application is under RELEASE mode, the range is from 15 to 60.
	 * while under DEBUG mode, the range is from 1 to 60.
	 * 
	 */
	public final static int MIN_INTERVAL_RELEASE = 15;
	
	public final static int MIN_INTERVAL_DEBUG = 1;
	
	public final static int MAX_INTERVAL = 60;
	
	@Range(min = MIN_INTERVAL_DEBUG, max = MAX_INTERVAL)
	private int syncInterval = DEFAULT_SYNC_INTERVAL;
	
	public static final String DEFAULT_CLIENT_IDENTIFIER = "HiveManager";
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Version
	private Timestamp version;
	
	@Transient
	private boolean	selected;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain owner;

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#getVersion()
	 */
	@Override
	public Timestamp getVersion() {
		return version;
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
	 * @see com.ah.bo.HmBo#setVersion(java.util.Date)
	 */
	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#getLabel()
	 */
	@Override
	public String getLabel() {
		return "SGE Integration";
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
	 * getter of enabled
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * setter of enabled
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * getter of serverURL
	 * @return the serverURL
	 */
	public String getServerURL() {
		return serverURL;
	}

	/**
	 * setter of serverURL
	 * @param serverURL the serverURL to set
	 */
	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	/**
	 * getter of userName
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * setter of userName
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * getter of password
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * setter of password
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * getter of syncInterval
	 * @return the syncInterval
	 */
	public int getSyncInterval() {
		return syncInterval;
	}

	/**
	 * setter of syncInterval
	 * @param syncInterval the syncInterval to set
	 */
	public void setSyncInterval(int syncInterval) {
		this.syncInterval = syncInterval;
	}

}