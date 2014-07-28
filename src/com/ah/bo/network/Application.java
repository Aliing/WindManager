package com.ah.bo.network;

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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "APPLICATION")
@org.hibernate.annotations.Table(appliesTo = "APPLICATION", indexes = {
		@Index(name = "IDX_APPLICATION_APPCODE", columnNames = {"APPCODE"}),
		@Index(name = "IDX_APPLICATION_OWNER", columnNames = {"OWNER"})
	    })
public class Application implements HmBo {
	
	public static final String[] GROUP_NAMES = new String[] {
		"Web Services", "Mail", "Social Networking", "Networking", "Network Monitoring", 
		"Games", "File Transfer", "VPN & Tunneling", "Messaging", "Proxy",
		"Database", "Collaboration", "Remote Access", "Streaming Media"
	};

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	private Integer appCode;

	@Column(length = 64, nullable = false)
	private String appName;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String shortName;
	
	@Column 
	private boolean defaultFlag = true;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;
	
	@Column 
	private Integer appGroupCode;
	
	@Column(length = 64)
	private String appGroupName;
	
	@Column(length = 1024)
	String description;
	
	@Transient
	private Long lastDayUsage = 0L;
	
	@Transient
	private Long lastMonthUsage = 0L; 
	
	public Application() {
		super();
	}
	
	public Application(Integer appCode, String appName, String shortName, String description, String groupName, HmDomain owner) {
		super();
		this.appCode = appCode;
		this.appName = appName.toUpperCase();
		this.shortName = shortName;
		this.description = description;
		this.owner = owner;
		int groupCode = 0;
		for (int i = 0; i < GROUP_NAMES.length; i++) {
			 if (groupName.equalsIgnoreCase(GROUP_NAMES[i])) {
				 groupCode = i + 1;
				 break;
			 }
		 }
		this.appGroupCode = groupCode;
		this.appGroupName = groupName;
	}
	
	public Long getLastDayUsage() {
		return lastDayUsage;
	}
	
	public String getLastDayUsageStr() {
		if (lastDayUsage == 0) {
			return "0.00 KB";
		}
		double d = lastDayUsage * 1.0;
		if (d >= 1024 * 1024 * 1024) {
			d = d / (1024 * 1024 * 1024);
			d = Math.round(d * 100) / 100.0; 
			return d + " GB";
		}
		else if (d >= 1024 * 1024) {
			d = d / (1024 * 1024);
			d = Math.round(d * 100) / 100.0; 
			return d + " MB";
		}
		if (d >= 1024) {
			d = d / 1024;
			d = Math.round(d * 100) / 100.0; 
			return d + " KB";
		}
		return lastDayUsage + " B";
	}
	
	public String getLastMonthUsageStr() {
		if (lastMonthUsage == 0) {
			return "0.00 KB";
		}
		double d = lastMonthUsage * 1.0;
		if (d >= 1024 * 1024 * 1024) {
			d = d / (1024 * 1024 * 1024);
			d = Math.round(d * 100) / 100.0; 
			return d + " GB";
		}
		else if (d >= 1024 * 1024) {
			d = d / (1024 * 1024);
			d = Math.round(d * 100) / 100.0; 
			return d + " MB";
		}
		if (d >= 1024) {
			d = d / 1024;
			d = Math.round(d * 100) / 100.0; 
			return d + " KB";
		}
		return lastMonthUsage + " B";
	}

	public void setLastDayUsage(Long lastDayUsage) {
		this.lastDayUsage = lastDayUsage;
	}

	public Long getLastMonthUsage() {
		return lastMonthUsage;
	}

	public void setLastMonthUsage(Long lastMonthUsage) {
		this.lastMonthUsage = lastMonthUsage;
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getDescription() {
		if (appCode.intValue() == 712) {
			return description + " (need signature version 3.1.0 or later) ";
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getAppGroupCode() {
		return appGroupCode;
	}

	public String getAppGroupName() {
		return appGroupName;
	}

	public void setAppGroupName(String appGroupName) {
		this.appGroupName = appGroupName;
	}

	public void setAppGroupCode(Integer appGroupCode) {
		this.appGroupCode = appGroupCode;
	}

	public Integer getAppCode() {
		return appCode;
	}

	public void setAppCode(Integer appCode) {
		this.appCode = appCode;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	@Override
	public boolean equals(Object application) {
		if (!(application instanceof Application)) {
			return false;
		}
		return null == id ? super.equals(application) : id.equals(((Application) application).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}
	
	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public String getLabel() {
		return appName;
	}

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
	public Application clone() {
		try {
			return (Application) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	@Transient
	private int appType = 0;

	public int getAppType() {
		return appType;
	}

	public void setAppType(int appType) {
		this.appType = appType;
	}
	
	
}