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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.useraccess.UserProfile;

@Entity
@Table(name = "OPENDNS_MAPPING")
@org.hibernate.annotations.Table(appliesTo = "OPENDNS_MAPPING", indexes = {
		@Index(name = "OPENDNS_MAPPING_OWNER", columnNames = { "OWNER" })
		})
public class OpenDNSMapping implements HmBo {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long	id;
	
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
	
	@Version
	private Timestamp version;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OPENDNS_DEVICE_ID")
	private OpenDNSDevice openDNSDevice;
	
	@Column(name="USER_PROFILE_ID", nullable = false)
	private Long userProfileId;

	@Transient
	private UserProfile userProfile;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OPENDNS_ACCOUNT_ID", nullable = false)
	private OpenDNSAccount openDNSAccount;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String getLabel() {
		return "";
	}
	
	public Timestamp getVersion() {
		return version;
	}

	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public OpenDNSDevice getOpenDNSDevice() {
		return openDNSDevice;
	}

	public void setOpenDNSDevice(OpenDNSDevice openDNSDevice) {
		this.openDNSDevice = openDNSDevice;
	}

	public UserProfile getUserProfile() {
		if(userProfile == null){
			userProfile = QueryUtil.findBoById(UserProfile.class, userProfileId);
		}
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		if(userProfile != null){
			this.userProfile = userProfile;
			this.userProfileId = userProfile.getId();
		}
	}

	public OpenDNSAccount getOpenDNSAccount() {
		return openDNSAccount;
	}

	public void setOpenDNSAccount(OpenDNSAccount openDNSAccount) {
		this.openDNSAccount = openDNSAccount;
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

	public Long getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}
}
