/**
 *@filename		RadiusUserProfileRuleAction.java
 *@version
 *@author		Joseph Chen
 *@since		05/12/2008
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.useraccess.RadiusUserProfileRule;
import com.ah.bo.useraccess.UserProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * this action handles requests for radius user profile rule
 */
public class RadiusUserProfileRuleAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {
		String fw = globalForward();
		
		if (fw != null) {
			return fw;
		}
		
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.radiusUserProfileRule"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new RadiusUserProfileRule());
				initValues();
				return INPUT;
			} else if("create".equals(operation)) {
				if (checkNameExists("radiusUserProfileRuleName", getDataSource().getRadiusUserProfileRuleName())
						|| !setSelectedUserProfile()) {
					initValues();
					return INPUT;
				}
				
				return createBo();
			} else if ("edit".equals(operation)) {
				String strForward = editBo(this);
				if (getDataSource().getId() != null) {
					findBoById(RadiusUserProfileRule.class, getDataSource()
							.getId(), this);
				}
				
				addLstTitle(getSelectedL2Feature().getDescription()
					+ " > Edit '" + getDisplayName() + "'");
				if (dataSource != null) {
					initValues();
				}
				return strForward;
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				RadiusUserProfileRule rule = (RadiusUserProfileRule) findBoById(boClass, cloneId, this);
				rule.setId(null);
				rule.setRadiusUserProfileRuleName("");
				rule.setDefaultFlag(false);
				rule.setOwner(null);
				rule.setVersion(null); // joseph chen 06/17/2008
				Set<UserProfile> cloneUserProfile = new HashSet<UserProfile>();
				
				for(UserProfile profile : rule.getPermittedUserProfiles()) {
					cloneUserProfile.add(profile);
				}
				
				rule.setPermittedUserProfiles(cloneUserProfile);
				setSessionDataSource(rule);
				initValues();
				addLstTitle(getText("config.title.radiusUserProfileRule"));
				return INPUT;
			} else if ("update".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				
				if (!setSelectedUserProfile()) {
					initValues();
					return INPUT;
				}
				
				return updateBo();
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					initValues();
					setId(dataSource.getId());
					if (getUpdateContext()) {
						removeLstTitle();
						removeLstForward();
						setUpdateContext(false);
					}
					return INPUT;
				}
			} else if ("newUserProfile".equals(operation)) {
				setSelectedUserProfile();
				clearErrorsAndMessages();
				addLstForward("radiusUserProfileRule");
				return operation;
			} else if(("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("radiusUserProfileRuleName", getDataSource().getRadiusUserProfileRuleName())
						|| !setSelectedUserProfile()) {
					initValues();
					return INPUT;
				}
				id = createBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}

	}
	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_RADIUS_USER_PROFILE_RULE);
		setDataSource(RadiusUserProfileRule.class);
	}
	
	public RadiusUserProfileRule getDataSource() {
		return (RadiusUserProfileRule) dataSource;
	}
	
	public String getDisplayName() {
		return getDataSource().getRadiusUserProfileRuleName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}
	
	public int getNameLength() {
		return getAttributeLength("radiusUserProfileRuleName");
	}
	
	public int getDescriptionLength() {
		return getAttributeLength("description");
	}
	
	protected OptionsTransfer userProfileOptions;

	public OptionsTransfer getUserProfileOptions() {
		return userProfileOptions;
	}

	public void setUserProfileOptions(OptionsTransfer userProfileOptions) {
		this.userProfileOptions = userProfileOptions;
	}
	
	public EnumItem[] getEnumDenyAction() {
		return RadiusUserProfileRule.DENY_ACTION;
	}
	
	public Range getActionTimeRange() {
		return getAttributeRange("actionTime");
	}
	
	public boolean getActionTimeDisabled() {
		return getDataSource().getDenyAction() != 1;
	}
	
	protected List<Long> selectUserProfile;
	
	public List<Long> getSelectUserProfile() {
		return selectUserProfile;
	}

	public void setSelectUserProfile(List<Long> selectUserProfile) {
		this.selectUserProfile = selectUserProfile;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ah.bo.mgmt.QueryBo#load(com.ah.bo.HmBo)
	 */
	@Override
	public Collection<HmBo> load(HmBo bo) {
		
		if(bo instanceof RadiusUserProfileRule) {
			RadiusUserProfileRule rule = (RadiusUserProfileRule)bo;
			if(rule.getPermittedUserProfiles() != null) {
				rule.getPermittedUserProfiles().size();
			}
		}
		return null;
	}
	
	public boolean setSelectedUserProfile() throws Exception {
		Set<UserProfile> permitUserProfile = getDataSource().getPermittedUserProfiles();
		permitUserProfile.clear();
		
		if(getDataSource().getAllUserProfilesPermitted()) {
			return true;
		}
		
		if (null != selectUserProfile) {
			for (Long up_id : selectUserProfile) {
				UserProfile up = findBoById(UserProfile.class, up_id);
				if (up != null) {
					permitUserProfile.add(up);
				}
			}
		}
		
		if (permitUserProfile.size() > 0) {
			getDataSource().setPermittedUserProfiles(permitUserProfile);
			return true;
		} else {
			addActionError(getText("error.radiusUserProfileRule.mustSelectPermitUserProfile"));
			return false;
		}
			
	}
	
	public String getSelectPermitted() {
		if (getDataSource().getAllUserProfilesPermitted()) {
			return "none";
		} else {
			return "";
		}
	}
	
	public String getSelectStrict() {
		if(getDataSource().getStrict()) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().isDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}
	
	private void initValues() throws Exception {
		prepareUserProfile();
	}
	
	private void prepareUserProfile() throws Exception {
		List<CheckItem> removeList = new ArrayList<CheckItem>();
		
		// get user profile from database
		List<CheckItem> availableUserProfile = this.getBoCheckItems("userProfileName", UserProfile.class, null);
		
		if (availableUserProfile.size() == 0) {
			availableUserProfile.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		
		for (CheckItem oneItem : availableUserProfile) {
			for (UserProfile userProfile : getDataSource().getPermittedUserProfiles()) {
				if (userProfile.getUserProfileName().equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		
		availableUserProfile.removeAll(removeList);
		
		userProfileOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.radiusUserProfileRule.availableUserProfile"),
				MgrUtil.getUserMessage("config.radiusUserProfileRule.selectedUserProfile"),
				availableUserProfile, getDataSource().getPermittedUserProfiles(), "id", "value",
				"selectUserProfile", 128);
	}

}