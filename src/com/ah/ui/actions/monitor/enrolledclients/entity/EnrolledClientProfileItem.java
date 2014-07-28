package com.ah.ui.actions.monitor.enrolledclients.entity;


import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@Embeddable
@XStreamAlias("Profile")
public class EnrolledClientProfileItem
{	
	@Transient
	@XStreamAlias("Identifier")
	public String identifier;
	@Transient
	@XStreamAlias("Version")
	public String version;
	@Transient
	@XStreamAlias("DisplayName")
	public String displayName;
	@Transient
	@XStreamAlias("Desc")
	public String desc;
	@Transient
	@XStreamAlias("Orgnization")
	public String orgnization;
	@Transient
	@XStreamAlias("IsManaged")
	public String isManaged;
	@Transient
	@XStreamAlias("IsEncrypted")
	public String isEncrypted;
	@Transient
	@XStreamAlias("ConfigedItems")
	public String configedItems;
	
	public EnrolledClientProfileItem()
	{
		super();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getOrgnization() {
		return orgnization;
	}

	public void setOrgnization(String orgnization) {
		this.orgnization = orgnization;
	}

	public String getIsManaged() {
		return isManaged;
	}

	public void setIsManaged(String isManaged) {
		this.isManaged = isManaged;
	}

	public String getIsEncrypted() {
		return isEncrypted;
	}

	public void setIsEncrypted(String isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	public String getConfigedItems() {
		return configedItems;
	}

	public void setConfigedItems(String configedItems) {
		this.configedItems = configedItems;
	}

}
