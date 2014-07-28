/**
 *@filename		EthernetAccess.java
 *@version
 *@author		Fiona
 *@createtime	2007-12-10 PM 02:54:38
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.wlan;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.useraccess.UserProfile;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "ETHERNET_ACCESS")
@org.hibernate.annotations.Table(appliesTo = "ETHERNET_ACCESS", indexes = {
		@Index(name = "ETHERNET_ACCESS_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class EthernetAccess implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

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

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String ethernetName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	@Version
	private Timestamp version;

	private boolean macLearning = true;

	private boolean enableIdle = true;

	private short idleTimeout = 180;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_PROFILE_ID")
	private UserProfile userProfile;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "ETHERNET_ACCESS_MAC", joinColumns = { @JoinColumn(name = "ETHERNET_ACCESS_ID") }, inverseJoinColumns = { @JoinColumn(name = "MAC_OR_OUI_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<MacOrOui> macAddress = new HashSet<MacOrOui>();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Long getId() {
		return id;
	}

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

	@Override
	public String getLabel() {
		return ethernetName;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getEthernetName() {
		return ethernetName;
	}

	public void setEthernetName(String ethernetName) {
		this.ethernetName = ethernetName;
	}

	public Set<MacOrOui> getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(Set<MacOrOui> macAddress) {
		this.macAddress = macAddress;
	}

	public boolean isMacLearning() {
		return macLearning;
	}

	public void setMacLearning(boolean macLearning) {
		this.macLearning = macLearning;
	}

	public boolean isEnableIdle() {
		return enableIdle;
	}

	public void setEnableIdle(boolean enableIdle) {
		this.enableIdle = enableIdle;
	}

	public short getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(short idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Transient
	public String getStringMacLearn() {
		return macLearning ? "Enabled" : "Disabled";
	}

	@Transient
	public String getStringIdleTimeout() {
		return enableIdle ? "Enabled (" + idleTimeout + ")" : "Disabled";
	}
	
	@Transient
	public String getUserProfileName() {
		if (null == getUserProfile())
			return "";
		return getUserProfile().getUserProfileName();
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

}