package com.ah.bo.admin;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "HM_USER_GROUP")
@org.hibernate.annotations.Table(appliesTo = "HM_USER_GROUP", indexes = {
		@Index(name = "HM_USER_GROUP_OWNER", columnNames = { "OWNER" }),
		@Index(name = "HM_USER_GROUP_ATTRIBUTE", columnNames = { "GROUPATTRIBUTE" }),
		@Index(name = "HM_USER_GROUP_NAME", columnNames = { "GROUPNAME" })
		})
public class HmUserGroup implements HmBo {

	private static final long	serialVersionUID	= 1L;

	@Id
	@GeneratedValue
	private Long				id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain			owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp			version;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String				groupName;

	public static final String	ADMINISTRATOR	= "Super User";

	public static final String	MONITOR			= "Monitoring Only";

	public static final String	CONFIG			= "Configuration and Monitoring";

	public static final String	PLANNING		= "RF Planning";

	public static final String	GM_ADMIN		= "User Manager Admin";

	public static final String	GM_OPERATOR		= "User Manager Operator";

	public static final String	TEACHER			= "Teacher";

	public static final String	VAD				= "Partner";

	public static final String	STANDALONE_HM	= "Standalone HM";

//	@OneToMany(mappedBy = "userGroup")
//	private Set<HmUser>			users			= new HashSet<HmUser>();

	public static final String	VAD_MONITOR			= "Monitoring Only(Partner)";

	public static final String	VAD_CONFIG_MONITOR 	= "Configuration and Monitoring(Partner)";

	private boolean				defaultFlag;

	public boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	private String	helpURL;

//	public Set<HmUser> getUsers() {
//		return users;
//	}
//
//	public void setUsers(Set<HmUser> users) {
//		this.users = users;
//	}

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name = "mapkey")
	@CollectionTable(name = "HM_INSTANCE_PERMISSION", joinColumns = @JoinColumn(name = "HM_USER_GROUP_ID", nullable = true))
	private Map<Long, HmPermission>	instancePermissions	= new HashMap<Long, HmPermission>();

	public Map<Long, HmPermission> getInstancePermissions() {
		return instancePermissions;
	}

	public void setInstancePermissions(Map<Long, HmPermission> instancePermissions) {
		this.instancePermissions = instancePermissions;
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name = "mapkey")
	@CollectionTable(name = "HM_FEATURE_PERMISSION", joinColumns = @JoinColumn(name = "HM_USER_GROUP_ID", nullable = true))
	private Map<String, HmPermission>	featurePermissions	= new HashMap<String, HmPermission>();

	public Map<String, HmPermission> getFeaturePermissions() {
		return featurePermissions;
	}

	public void setFeaturePermissions(Map<String, HmPermission> featurePermissions) {
		this.featurePermissions = featurePermissions;
	}

	public static final int	ADMINISTRATOR_ATTRIBUTE	= 1;

	public static final int	MONITOR_ATTRIBUTE		= 0;

	public static final int	CONFIG_ATTRIBUTE		= 2;

	public static final int	GM_ADMIN_ATTRIBUTE		= 3;

	public static final int	GM_OPERATOR_ATTRIBUTE	= 4;

	public static final int	PLANNING_ATTRIBUTE		= 5;

	public static final int	TEACHER_ATTRIBUTE		= 6;

	public static final int	VAD_ATTRIBUTE			= 7;

	public static final int	STANDALONE_HM_ATTRIBUTE	= 8;

	public static final int	VAD_MONITOR_ATTRIBUTE			= 9;

	public static final int	VAD_CONFIG_MONITOR_ATTRIBUTE 	= 10;

	private int				groupAttribute			= MONITOR_ATTRIBUTE;

	public boolean isDefaultGroupAttribute() {
		if (groupAttribute == HmUserGroup.ADMINISTRATOR_ATTRIBUTE
				|| groupAttribute == HmUserGroup.CONFIG_ATTRIBUTE
				|| groupAttribute == HmUserGroup.MONITOR_ATTRIBUTE
				|| groupAttribute == HmUserGroup.GM_ADMIN_ATTRIBUTE
				|| groupAttribute == HmUserGroup.GM_OPERATOR_ATTRIBUTE
				|| groupAttribute == HmUserGroup.PLANNING_ATTRIBUTE
				|| groupAttribute == HmUserGroup.TEACHER_ATTRIBUTE
				|| groupAttribute == HmUserGroup.VAD_ATTRIBUTE
				|| groupAttribute == HmUserGroup.STANDALONE_HM_ATTRIBUTE) {
			return true;
		}

		return false;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean isAdministrator() {
		return groupName != null && ADMINISTRATOR.equals(groupName);
	}

	public boolean isGMUserGroup() {
		return groupName != null && (groupName.equals(GM_ADMIN) || groupName.equals(GM_OPERATOR));
	}

	public boolean isTcUserGroup() {
		return groupName != null && groupName.equals(TEACHER);
	}

	public boolean isPlUserGroup() {
		return groupName != null && groupName.equals(PLANNING);
	}

	@Transient
	private boolean	selected;
	
	@Transient
	private boolean	GMUserGroup;

	public void setGMUserGroup(boolean gMUserGroup) {
		GMUserGroup = gMUserGroup;
	}

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
		return groupName;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public int getGroupAttribute() {
		return groupAttribute;
	}

	public void setGroupAttribute(int groupAttribute) {
		this.groupAttribute = groupAttribute;
	}

	public String getHelpURL() {
		return helpURL;
	}

	public void setHelpURL(String helpURL) {
		this.helpURL = helpURL;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}		
	
	@Transient
	public static final String[] defaultVhmGroupNames = new String[] { HmUserGroup.MONITOR,
			HmUserGroup.CONFIG, HmUserGroup.PLANNING, HmUserGroup.GM_ADMIN,
			HmUserGroup.GM_OPERATOR, HmUserGroup.TEACHER };

}