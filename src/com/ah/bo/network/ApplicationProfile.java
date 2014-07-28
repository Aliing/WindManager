package com.ah.bo.network;

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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "APPLICATION_PROFILE")
@org.hibernate.annotations.Table(appliesTo = "APPLICATION_PROFILE", indexes = {
		@Index(name = "IDX_APPLICATION_PROFILE_OWNER", columnNames = {"OWNER"})
	    })
public class ApplicationProfile implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String profileName;
	
	@Column 
	private boolean defaultFlag;

	@Version
	private Timestamp version;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "APPPROFILE_APP", joinColumns = { @JoinColumn(name = "PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "APP_ID") })
	private Set<Application> applicationList = new HashSet<Application>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "APPPROFILE_CUSTOM_APP", joinColumns = { @JoinColumn(name = "PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "CUSTOMAPP_ID") })
	private Set<CustomApplication> customApplicationList = new HashSet<CustomApplication>();
	
	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public Set<Application> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(Set<Application> applicationList) {
		this.applicationList = applicationList;
	}
	
	public Set<CustomApplication> getCustomApplicationList() {
		return customApplicationList;
	}

	public void setCustomApplicationList(
			Set<CustomApplication> customApplicationList) {
		this.customApplicationList = customApplicationList;
	}

	@Override
	public boolean equals(Object osObject) {
		if (!(osObject instanceof ApplicationProfile)) {
			return false;
		}
		return null == id ? super.equals(osObject) : id.equals(((ApplicationProfile) osObject).getId());
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
		return profileName;
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
	public ApplicationProfile clone() {
		try {
			return (ApplicationProfile) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}