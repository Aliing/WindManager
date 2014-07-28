/**
 *@filename		RadiusUserProfileRule.java
 *@version
 *@author		Joseph Chen
 *@since		05/12/2008
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
package com.ah.bo.useraccess;

import java.sql.Timestamp;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

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
import org.hibernate.validator.constraints.Range;

/**
 * Business Object for RADIUS user profile rules
 * 
 */
@Entity
@Table(name = "RADIUS_USER_PROFILE_RULE")
@org.hibernate.annotations.Table(appliesTo = "RADIUS_USER_PROFILE_RULE", indexes = {
		@Index(name = "RADIUS_USER_PROFILE_RULE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class RadiusUserProfileRule implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String radiusUserProfileRuleName;
	
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	@Version
	private Timestamp version;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	public static final short		DENY_ACTION_BAN				= 1;
	
	public static final short		DENY_ACTION_BAN_FOREVER		= 2;
	
	public static final short		DENY_ACTION_DISCONNECT		= 3;
	
	public static EnumItem[] DENY_ACTION = MgrUtil.enumItems(
			"enum.denyAction.", new int[] { DENY_ACTION_BAN, DENY_ACTION_BAN_FOREVER,
					DENY_ACTION_DISCONNECT });

	private short denyAction;
	
	public static final long		ACTION_TIME_DEFAULT			= 60;
	
	@Range(min=1, max=100000000)
	private long actionTime;
	
	private boolean allUserProfilesPermitted;
	
	/**
	 *   Set the behavior to deauthenticate all connected stations
                whenever a user profile bound to the SSID changes (Note: When
                stations reauthenticate, the user profile changes take
                effect.)
	 */
	private boolean strict;
	
	private boolean defaultFlag;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "RADIUS_RULE_USER_PROFILE", joinColumns = { @JoinColumn(name = "RADIUS_USER_PROFILE_RULE_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_PROFILE_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<UserProfile> permittedUserProfiles = new HashSet<UserProfile>();

	@Transient
	private boolean selected;
	
	public RadiusUserProfileRule() {
		denyAction = DENY_ACTION_DISCONNECT;
		actionTime = ACTION_TIME_DEFAULT;
		allUserProfilesPermitted = true;
	}
	
	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#getId()
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getRadiusUserProfileRuleName() {
		return this.radiusUserProfileRuleName;
	}

	public void setRadiusUserProfileRuleName(String radiusUserProfileRuleName) {
		this.radiusUserProfileRuleName = radiusUserProfileRuleName;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#getVersion()
	 */
	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#isSelected()
	 */
	@Override
	public boolean isSelected() {
		return selected;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;

	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#getLabel()
	 */
	@Override
	public String getLabel() {
		return radiusUserProfileRuleName;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#getOwner()
	 */
	@Override
	public HmDomain getOwner() {
		return owner;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#setOwner(com.ah.bo.admin.HmDomain)
	 */
	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}
	
	public short getDenyAction() {
		return this.denyAction;
	}

	public void setDenyAction(short denyAction) {
		this.denyAction = denyAction;
	}
	
	public long getActionTime() {
		return this.actionTime;
	}
	
	public void setActionTime(long actionTime) {
		this.actionTime = actionTime;
	}
	
	public Set<UserProfile> getPermittedUserProfiles() {
		return this.permittedUserProfiles;
	}
	
	public void setPermittedUserProfiles(Set<UserProfile> permittedUserProfiles) {
		this.permittedUserProfiles = permittedUserProfiles;
	}
	
	public boolean getAllUserProfilesPermitted() {
		return this.allUserProfilesPermitted;
	}
	
	public void setAllUserProfilesPermitted(boolean allUserProfilesPermitted) {
		this.allUserProfilesPermitted = allUserProfilesPermitted;
	}
	
	public boolean getStrict() {
		return this.strict;
	}
	
	public void setStrict(boolean strict) {
		this.strict = strict;
	}
	
	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

}