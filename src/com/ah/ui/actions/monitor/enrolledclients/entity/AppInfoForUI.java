package com.ah.ui.actions.monitor.enrolledclients.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Embeddable
@XStreamAlias("App")
public class AppInfoForUI implements Serializable{
	
	private static final long serialVersionUID = 8906791939391045397L;
	
	@Transient
	@XStreamAlias("Identifier")
	private String identifier;

	@Transient
	@XStreamAlias("Name")
	private String name;
	
	@Transient
	@XStreamAlias("Version")
	private String version;
	
	@Transient
	@XStreamAlias("ShortVersion")
	private String shortVersion;

	@Transient
	@XStreamAlias("BundleSize")
	private String bundleSize;
	
	@Transient
	@XStreamAlias("DynamicSize")
	private String dynamicSize;
	
	@Transient
	@XStreamAlias("IsManaged")
	private String managed;

	@Transient
	@XStreamAlias("Status")
	private String status;
	
	public AppInfoForUI() {
		super();
	}

	public AppInfoForUI(String identifier, String name, String version,
			String shortVersion, String bundleSize, String dynamicSize,
			String managed, String status) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.version = version;
		this.shortVersion = shortVersion;
		this.bundleSize = bundleSize;
		this.dynamicSize = dynamicSize;
		this.managed = managed;
		this.status = status;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getShortVersion() {
		return shortVersion;
	}

	public void setShortVersion(String shortVersion) {
		this.shortVersion = shortVersion;
	}

	public String getBundleSize() {
		return bundleSize == null ? "":bundleSize.trim();
	}

	public void setBundleSize(String bundleSize) {
		this.bundleSize = bundleSize;
	}

	public String getDynamicSize() {
		return dynamicSize == null ? "":dynamicSize.trim();
	}

	public void setDynamicSize(String dynamicSize) {
		this.dynamicSize = dynamicSize;
	}

	public String getManaged() {
		return managed;
	}

	public void setManaged(String managed) {
		this.managed = managed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
