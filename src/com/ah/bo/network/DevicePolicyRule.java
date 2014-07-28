package com.ah.bo.network;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Range;

import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;

@Embeddable
public class DevicePolicyRule implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int MAX_RULE_ID = 16320/64;
	public final static short ANY_TYPE=1;
	public final static short CID_TYPE=2;
	public final static short BYOD_TYPE=3;
	@Transient
	private String ownershipName;

	@Range(min = 1)
	private short ruleId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MAC_OBJ_ID")
	private MacOrOui macObj;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OS_OBJ_ID")
	private OsObject osObj;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "DOMAIN_OBJ_ID")
	private DomainObject domObj;
	
	private String userProfileName;
	
	private Long userProfileId;
	private int ownership;
	public int getOwnership() {
		return ownership;
	}
	public void setOwnership(int ownership) {
		this.ownership = ownership;
	}
	public String getOwnershipName() {
		return getOwnershipName(ownership);
	}
	public static String getOwnershipName(int type) {
		switch (type){
		case ANY_TYPE:
			return "[-any-]";
		case CID_TYPE:
			return "CID";
		case BYOD_TYPE:
			return "BYOD";
		}
		return "[-any-]";
	}

	public short getRuleId() {
		return ruleId;
	}

	public void setRuleId(short ruleId) {
		this.ruleId = ruleId;
	}

	public MacOrOui getMacObj() {
		return macObj;
	}

	public void setMacObj(MacOrOui macObj) {
		this.macObj = macObj;
	}

	public OsObject getOsObj() {
		return osObj;
	}

	public void setOsObj(OsObject osObj) {
		this.osObj = osObj;
	}
	
	public String getIdentifier() {
		return getIdentifierString(macObj, osObj, domObj, userProfileName,ownership);
	}
	
	public static String getIdentifierString(MacOrOui macObj, OsObject osObj,
		DomainObject domain, String userProfileName,int ownership) {
		StringBuffer sb = new StringBuffer();
		if (macObj != null) {
			sb.append(macObj.getMacOrOuiName());
		}
		if (osObj != null) {
			sb.append(osObj.getOsName());
		}
		if (domain != null) {
			sb.append(domain);
		}
		if (userProfileName != null) {
			sb.append(userProfileName);
		}
		sb.append(getOwnershipName(ownership));
		return sb.toString();
	}

	public String getUserProfileName()
	{
		return userProfileName;
	}

	public void setUserProfileName(String userProfileName)
	{
		this.userProfileName = userProfileName;
	}
	
	public String getUserProfileNameSubstr() {
		if (userProfileName==null) {
			return "";
		}
		if (userProfileName.length()> BaseAction.DISPLAY_LENGTH_IN_GUI_OK) {
			return userProfileName.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI) + "...";
		}
		
		return userProfileName;
	}

	public Long getUserProfileId()
	{
		return userProfileId;
	}

	public void setUserProfileId(Long userProfileId)
	{
		this.userProfileId = userProfileId;
	}
	
	public String getUsVlanName(){
		if (userProfileId==null) {
			return "";
		}
		List<?> vlans = QueryUtil.executeNativeQuery("select vlanName from USER_PROFILE u, VLAN v where u.VLAN_ID=v.id and u.id=" + userProfileId);
		if (vlans!=null && !vlans.isEmpty()) {
			return vlans.get(0).toString();
		}
		return "";
	
	}
	
	public Long getUsVlanId(){
		if (userProfileId==null) {
			return -1L;
		}
		List<?> vlans = QueryUtil.executeNativeQuery("select v.id from USER_PROFILE u, VLAN v where u.VLAN_ID=v.id and u.id=" + userProfileId);
		if (vlans!=null && !vlans.isEmpty()) {
			return Long.valueOf(vlans.get(0).toString());
		}
		return -1L;
	
	}

	public DomainObject getDomObj()
	{
		return domObj;
	}

	public void setDomObj(DomainObject domObj)
	{
		this.domObj = domObj;
	}
	
	@Transient
	private String description;

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
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
