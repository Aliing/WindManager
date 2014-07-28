package com.ah.bo.useraccess;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Embeddable
public class LdapServerOuUserProfile implements Serializable {
	private static final long		serialVersionUID	= 1L;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "LOCAL_USER_GROUP_ID", nullable = false)
	private LocalUserGroup localUserGroup;
	
	private String userProfileName;
	
	private Long userProfileId;
	
	private int rowId;
	
	private String groupAttributeValue;
	
	private Long serverId;

	private short typeFlag = ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY;
	
	public String getUserProfileName() {
		return userProfileName;
	}

	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
	}

	public LocalUserGroup getLocalUserGroup() {
		return localUserGroup;
	}

	public void setLocalUserGroup(LocalUserGroup localUserGroup) {
		this.localUserGroup = localUserGroup;
	}

	public short getTypeFlag() {
		return typeFlag;
	}

	public void setTypeFlag(short typeFlag) {
		this.typeFlag = typeFlag;
	}

	public Long getServerId() {
		return serverId;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	public int getRowId() {
		return rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
	}
	
	public String getGroupAttributeValue() {
		return groupAttributeValue;
	}

	public void setGroupAttributeValue(String groupAttributeValue) {
		this.groupAttributeValue = groupAttributeValue;
	}

	public Long getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}

	@Transient
	private short userProfileAttribute = -1;

	public short getUserProfileAttribute() {
		if (userProfileAttribute == -1 && localUserGroup != null) {
			userProfileAttribute = (short)localUserGroup.getUserProfileId();
		}
		return userProfileAttribute;
	}

	public void setUserProfileAttribute(short userProfileAttribute) {
		this.userProfileAttribute = userProfileAttribute;
	}

	@Transient
	private int reorder;

	public int getReorder() {
		return reorder;
	}
	
	public void setReorder(int reorder) {
		this.reorder = reorder;
	}
	
}
