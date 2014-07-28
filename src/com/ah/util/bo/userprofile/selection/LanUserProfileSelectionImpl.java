package com.ah.util.bo.userprofile.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.HmBo;
import com.ah.bo.lan.LanProfile;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.util.CheckItem2;
import com.ah.util.MgrUtil;


public class LanUserProfileSelectionImpl extends UserProfileSelectionTemplate
		implements UserProfileSelection {
	
	@SuppressWarnings("all")
	private LanUserProfileSelectionImpl() {
	}
	
	public LanUserProfileSelectionImpl(Object boToDeal) {
		this.setBoToDeal(boToDeal);
	}
	
	public LanUserProfileSelectionImpl(Object boToDeal, Long domainId) {
		super(boToDeal, domainId, false);
		this.doDataPrepare();
	}
	
	public LanUserProfileSelectionImpl(Long id, Class<? extends HmBo> c, Long domainId) {
		super(id, c, new LazyUserProfileObjLoader(), domainId, false);
		this.doDataPrepare();
	}
	
	private LanProfile lanProfile = null;
	
	@Override
	@SuppressWarnings("unchecked")
	protected LanProfile getMyObject() {
		if (this.lanProfile == null
				&& this.getBoToDeal() instanceof LanProfile) {
			this.lanProfile = (LanProfile)this.getBoToDeal();
		}
		
		return this.lanProfile;
	}
	
	@Override
	public List<CheckItem2> getDefaultUserProfiles() {
		return this.defaultUserProfiles;
	}
	private List<CheckItem2> prepareDefaultUserProfiles() {
		if (this.getMyObject() == null) {
			return null;
		}
		
		Map<Long, CheckItem2> userProfiles = 
			new HashMap<Long, CheckItem2>(getAvailableUserProfiles());
		
		if (this.getUpType() == UserProfileSelection.USER_PROFILE_TYPE_DEFAULT
				&& this.getNewlyAddedUserProfile() != null) {
			userProfiles.put(this.getNewlyAddedUserProfile().getId(),
					this.encapUpToCheckItem(this.getNewlyAddedUserProfile(), true));
		} else if (this.getMyObject().getUserProfileDefault() != null) {
			userProfiles.put(this.getMyObject().getUserProfileDefault().getId(),
					this.encapUpToCheckItem(this.getMyObject().getUserProfileDefault(), true));
		}
		
		List<CheckItem2> userProfileList = new ArrayList<CheckItem2>(userProfiles.values());
		Collections.sort(userProfileList, 
				new CheckItem2ComparatorWithId());
		
		return userProfileList;
	}

	@Override
	public List<CheckItem2> getSelfRegUserProfiles() {
		return this.selfUserProfiles;
	}
	private List<CheckItem2> prepareSelfRegUserProfiles() {
		if (this.getMyObject() == null) {
			return null;
		}
		
		Map<Long, CheckItem2> userProfiles = 
			new HashMap<Long, CheckItem2>(getAvailableUserProfiles());
		
		if (this.getUpType() == UserProfileSelection.USER_PROFILE_TYPE_REGISTRATION
				&& this.getNewlyAddedUserProfile() != null) {
			userProfiles.put(this.getNewlyAddedUserProfile().getId(),
					this.encapUpToCheckItem(this.getNewlyAddedUserProfile(), true));
		} else if (this.getMyObject().getUserProfileSelfReg() != null) {
			userProfiles.put(this.getMyObject().getUserProfileSelfReg().getId(),
					this.encapUpToCheckItem(this.getMyObject().getUserProfileSelfReg(), true));
		}
		
		List<CheckItem2> userProfileList = new ArrayList<CheckItem2>(userProfiles.values());
		Collections.sort(userProfileList, 
				new CheckItem2ComparatorWithId());
		
		return userProfileList;
	}

	@Override
	public List<CheckItem2> getAuthUserProfiles() {
		return this.authUserProfiles;
	}
	private List<CheckItem2> prepareAuthUserProfiles() {
		if (this.getMyObject() == null) {
			return null;
		}
		
		Map<Long, CheckItem2> userProfiles = 
			new HashMap<Long, CheckItem2>(getAvailableUserProfiles());
		
		if (this.getUpType() == UserProfileSelection.USER_PROFILE_TYPE_AUTH
				&& this.getNewlyAddedUserProfile() != null) {
			userProfiles.put(this.getNewlyAddedUserProfile().getId(),
					this.encapUpToCheckItem(this.getNewlyAddedUserProfile(), true));
		} 
		if (this.getMyObject().getRadiusUserProfile() != null
				&& this.getMyObject().getRadiusUserProfile().size() > 0) {
			for (UserProfile userProfile : this.getMyObject().getRadiusUserProfile()) {
				userProfiles.put(userProfile.getId(),
						this.encapUpToCheckItem(userProfile, true));
			}
		}
		
		List<CheckItem2> userProfileList = new ArrayList<CheckItem2>(userProfiles.values());
		Collections.sort(userProfileList, 
				new CheckItem2ComparatorWithId());
		
		return userProfileList;
	}
	
	public void setAddedUserProfile(Long addedId, short type) {
		this.addNewlyAddedUserProfile(addedId, type, true);
	}

	@Override
	public String getUpSelectionTipOfDefault() {
		return this.upSelectionTipOfDefault;
	}
	private String prepareUpSelectionTipOfDefault() {
		if (this.getMyObject() == null) {
			return null;
		}
		
		if (this.isDefaultUserProfileSupport()
				&& this.isAuthUserProfileSupport()) {
			return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.default")
						+ ":  " 
						+ MgrUtil.getUserMessage("config.configTemplate.wizard.step4.subtitle5");
		}
		
		return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.default")
					+ ":  "
					+ MgrUtil.getUserMessage("config.configTemplate.wizard.step4.subtitle5.lan.profile");
	}

	@Override
	public String getUpSelectionTipOfReg() {
		return this.upSelectionTipOfReg;
	}
	private String prepareUpSelectionTipOfReg() {
		if (this.getMyObject() == null) {
			return null;
		}

		return MgrUtil.getUserMessage("config.configTemplate.wizard.step4.subtitle1");
	}

	@Override
	public String getUpSelectionTipOfAuth() {
		return this.upSelectionTipOfAuth;
	}
	private String prepareUpSelectionTipOfAuth() {
		if (this.getMyObject() == null) {
			return null;
		}
		
		return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.auth")
					+ ":  " 
					+ MgrUtil.getUserMessage("config.configTemplate.wizard.step4.subtitle4");
	}

	@Override
	public boolean isDefaultUserProfileSupport() {
		return this.defaultUserProfileSupport;
	}
	private boolean prepareDefaultUserProfileSupport() {
		if (this.getMyObject() == null) {
			return false;
		}
		
		if (this.getMyObject().isCwpSelectEnabled()) {
			if (!this.getMyObject().isMacAuthEnabled()
					&& this.getMyObject().getCwp() != null) {
				if (this.getMyObject().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public boolean isSelfUserProfileSupport() {
		return this.selfUserProfileSupport;
	}
	private boolean prepareSelfUserProfileSupport() {
		if (this.getMyObject() == null) {
			return false;
		}
		
		if (this.getMyObject().isCwpSelectEnabled()) {
			if (this.getMyObject().getCwp() != null) {
				if (this.getMyObject().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED
						|| this.getMyObject().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean isAuthUserProfileSupport() {
		return this.authUserProfileSupport;
	}
	private boolean prepareAuthUserProfileSupport() {
		if (this.getMyObject() == null) {
			return false;
		}
		
		if (this.getMyObject().isEnabled8021X()) {
			return true;
		}
		
		if (this.getMyObject().isCwpSelectEnabled()) {
			if (this.getMyObject().isMacAuthEnabled()) {
				return true;
			}
			if (this.getMyObject().getCwp() != null) {
				if (this.getMyObject().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
						|| this.getMyObject().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL
						|| this.getMyObject().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH) {
					return true;
				}
			}
		}

		return false;
	}
	
	@Override
	public String getChkUserOnlyStyle() {
		if (this.isChkUserOnlyEnabled()) {
			return "";
		}
		
		return "none";
	}
	
	public boolean isChkUserOnlyEnabled() {
		if (this.getMyObject() != null) {
			if (this.getMyObject().isEnabled8021X()) {
				return true;
			}
			if (this.getMyObject().isMacAuthEnabled()) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public short getAddedUserProfileType() {
		return this.getUpType();
	}
	
	Map<Long, CheckItem2> availableUserProfiles = null;
	
	private Map<Long, CheckItem2> getAvailableUserProfiles() {
		if (availableUserProfiles == null) {
			/*if (this.isBlnWirelessRoutingEnable()) {
				Object objNull = null;
				availableUserProfiles = getBoCheckItems("userProfileName||'('||attributeValue||')'", 
						UserProfile.class, 
						new FilterParams("vlan", objNull));
			} else {*/
				availableUserProfiles = getBoCheckItems("userProfileName||'('||attributeValue||')'", 
						UserProfile.class, 
						null);
			//}
		}
		
		return availableUserProfiles;
	}
	
	protected void doDataPrepare() {
		this.defaultUserProfileSupport = prepareDefaultUserProfileSupport();
		this.selfUserProfileSupport = prepareSelfUserProfileSupport();
		this.authUserProfileSupport = prepareAuthUserProfileSupport();
		
		this.defaultUserProfiles = prepareDefaultUserProfiles();
		this.selfUserProfiles = prepareSelfRegUserProfiles();
		this.authUserProfiles = prepareAuthUserProfiles();
		sortListByalpha(this.defaultUserProfiles);
		sortListByalpha(this.selfUserProfiles);
		sortListByalpha(this.authUserProfiles);
		
		this.upSelectionTipOfDefault = prepareUpSelectionTipOfDefault();
		this.upSelectionTipOfReg = prepareUpSelectionTipOfReg();
		this.upSelectionTipOfAuth = prepareUpSelectionTipOfAuth();
	}
	
	public void doUpSupportDataPrepare() {
		this.defaultUserProfileSupport = prepareDefaultUserProfileSupport();
		this.selfUserProfileSupport = prepareSelfUserProfileSupport();
		this.authUserProfileSupport = prepareAuthUserProfileSupport();
	}
	
	public static class LazyUserProfileObjLoader implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if(bo instanceof LanProfile) {
				LanProfile lanProfile = (LanProfile)bo;
				if (lanProfile.getUserProfileDefault() != null) {
					lanProfile.getUserProfileDefault().getId();
				}
				if (lanProfile.getUserProfileSelfReg() != null) {
					lanProfile.getUserProfileSelfReg().getId();
				}
				if (lanProfile.getRadiusUserProfile() != null) {
					lanProfile.getRadiusUserProfile().size();
				}
				if (lanProfile.isCwpSelectEnabled()) {
					if (lanProfile.getCwp() != null) {
						lanProfile.getCwp().getId();
					}
				}
			}
			return null;
		}
	}

    @Override
    public List<CheckItem2> getAuthFailUserProfiles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getUpSelectionTipOfAuthFail() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAuthFailUserProfileSupport() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<CheckItem2> getAuthDataUserProfiles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getUpSelectionTipOfAuthData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAuthDataUserProfileSupport() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<CheckItem2> getGuestUserProfiles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getUpSelectionTipOfGuest() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isGuestUserProfileSupport() {
        // TODO Auto-generated method stub
        return false;
    }

}
