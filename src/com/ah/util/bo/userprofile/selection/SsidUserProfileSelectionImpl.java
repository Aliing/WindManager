package com.ah.util.bo.userprofile.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.HmBo;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.CheckItem2;
import com.ah.util.MgrUtil;

public class SsidUserProfileSelectionImpl extends UserProfileSelectionTemplate
		implements UserProfileSelection {
	
	@SuppressWarnings("all")
	private SsidUserProfileSelectionImpl(){
	}
	
	public SsidUserProfileSelectionImpl(Object boToDeal) {
		this.setBoToDeal(boToDeal);
	}
	
	public SsidUserProfileSelectionImpl(Object boToDeal, Long domainId) {
		super(boToDeal, domainId, false);
		this.doDataPrepare();
	}
	
	public SsidUserProfileSelectionImpl(Long id, Class<? extends HmBo> c, Long domainId) {
		super(id, c, new LazyUserProfileObjLoader(), domainId, false);
		this.doDataPrepare();
	}

	private SsidProfile ssidProfile = null;
	
	@Override
	@SuppressWarnings("unchecked")
	protected SsidProfile getMyObject() {
		if (this.ssidProfile == null
				&& this.getBoToDeal() instanceof SsidProfile) {
			this.ssidProfile = (SsidProfile)this.getBoToDeal();
		}
		
		return this.ssidProfile;
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

	@Override
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
					+ MgrUtil.getUserMessage("config.configTemplate.wizard.step4.subtitle5");
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
		
		if (SsidProfile.ACCESS_MODE_PSK == this.getMyObject().getAccessMode()
				&& !this.getMyObject().getEnabledUseGuestManager()) {
			return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.auth")
					+ ":  " 
					+ MgrUtil.getUserMessage("config.ssid.pskUserInfo");
		}
		
		return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.auth")
					+ ":  " 
					+ MgrUtil.getUserMessage("config.configTemplate.wizard.step4.subtitle4");
	}

	@Override
	public boolean isDefaultUserProfileSupport() {
		return this.defaultUserProfileSupport;
	}
	// so you should call prepareAuthUserProfileSupport() before calling this function
	private boolean prepareDefaultUserProfileSupport() {
		if (this.getMyObject() == null) {
			return false;
		}
		
		if (isAuthUserProfileSupport()) {
			return true;
		}
		if (!this.getMyObject().isCwpSelectEnabled() 
				|| (this.getMyObject().isCwpSelectEnabled() && this.getMyObject().getCwp() == null)
				|| (this.getMyObject().isCwpSelectEnabled() 
						&& Cwp.REGISTRATION_TYPE_EULA == this.getMyObject().getCwp().getRegistrationType() 
						&& !this.getMyObject().isMacAuthEnabled())
				) {
			// WEP, key management is WEP
			if (SsidProfile.ACCESS_MODE_WEP == this.getMyObject().getAccessMode()
					&& SsidProfile.KEY_MGMT_WEP_PSK == this.getMyObject().getMgmtKey()) {
				return true;
			}
			// open
			if (SsidProfile.ACCESS_MODE_OPEN == this.getMyObject().getAccessMode()) {
				return true;
			}
			// WPA/WPA2 PSK, and do enable CWP
			if (SsidProfile.ACCESS_MODE_WPA == this.getMyObject().getAccessMode()) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean isSelfUserProfileSupport() {
		return this.selfUserProfileSupport;
	}
	private boolean prepareSelfUserProfileSupport() {
		if (this.getMyObject() == null) {
			return false;
		}
		
		// enable CWP, the type is: auth, external, both
		if (SsidProfile.ACCESS_MODE_WPA == this.getMyObject().getAccessMode()
				|| SsidProfile.ACCESS_MODE_OPEN == this.getMyObject().getAccessMode()
				|| (SsidProfile.ACCESS_MODE_WEP == this.getMyObject().getAccessMode()
						&& SsidProfile.KEY_MGMT_WEP_PSK == this.getMyObject().getMgmtKey())) {
			if (this.getMyObject().isCwpSelectEnabled()
					&& this.getMyObject().getCwp() != null
					&& (
							Cwp.REGISTRATION_TYPE_REGISTERED == this.getMyObject().getCwp().getRegistrationType()
							||Cwp.REGISTRATION_TYPE_BOTH == this.getMyObject().getCwp().getRegistrationType()
							
					)) {
				return true;
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
		
		if (this.getMyObject().isBlnDisplayIDM() && !isEnabledIDMSelfReg()) {
			return true;
		}
		
		// the following situation can lead to multiple selection
		// WPA/WPA2 802.1X, WEP, key management is WEP 802.1X
		if (SsidProfile.ACCESS_MODE_8021X == this.getMyObject().getAccessMode()) {
			return true;
		}
		if ((SsidProfile.ACCESS_MODE_WEP == this.getMyObject().getAccessMode()
						&& SsidProfile.KEY_MGMT_DYNAMIC_WEP == this.getMyObject().getMgmtKey())) {
			return true;
		}
		// Private PSK
		if (SsidProfile.ACCESS_MODE_PSK == this.getMyObject().getAccessMode()) {
			return true;
		}
		// enable MAC
		if (this.getMyObject().isMacAuthEnabled()) {
			return true;
		}
		// used as Guest Manager
		if (this.getMyObject().getEnabledUseGuestManager()) {
			return true;
		}
		// enable CWP, the type is: auth, external, both
		if (SsidProfile.ACCESS_MODE_WPA == this.getMyObject().getAccessMode()
				|| SsidProfile.ACCESS_MODE_OPEN == this.getMyObject().getAccessMode()
				|| (SsidProfile.ACCESS_MODE_WEP == this.getMyObject().getAccessMode()
						&& SsidProfile.KEY_MGMT_WEP_PSK == this.getMyObject().getMgmtKey())) {
			if (this.getMyObject().isCwpSelectEnabled()
					&& this.getMyObject().getCwp() != null
					&& (
							Cwp.REGISTRATION_TYPE_AUTHENTICATED == this.getMyObject().getCwp().getRegistrationType()
							||Cwp.REGISTRATION_TYPE_EXTERNAL == this.getMyObject().getCwp().getRegistrationType()
							||Cwp.REGISTRATION_TYPE_BOTH == this.getMyObject().getCwp().getRegistrationType()
							
					)) {
				return true;
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
			if (this.getMyObject().isBlnDisplayIDM()) {
                if (isEnabledIDMSelfReg()
                        && null != this.getMyObject().getCwp()
                        && this.getMyObject().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED) {
                    return false;
                } else {
                    return true;
                }
			}
			// the following situation can lead to show "Only the selected user profiles..." checkbox
			if (SsidProfile.ACCESS_MODE_8021X == this.getMyObject().getAccessMode()) {
				return true;
			}
			if (this.getMyObject().isMacAuthEnabled()) {
				return true;
			}
			if (this.getMyObject().getEnabledUseGuestManager()) {
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

	@Override
	protected void doDataPrepare() {
		this.authUserProfileSupport = prepareAuthUserProfileSupport();
		this.defaultUserProfileSupport = prepareDefaultUserProfileSupport();
		this.selfUserProfileSupport = prepareSelfUserProfileSupport();
		this.guestUserProfileSupport = prepareGuestUserProfileSupport();// AirWatch Non-Compliance
		
		this.defaultUserProfiles = prepareDefaultUserProfiles();
		this.selfUserProfiles = prepareSelfRegUserProfiles();
		this.authUserProfiles = prepareAuthUserProfiles();
		this.guestUserProfiles = prepareGuestUserProfiles(); // AirWatch Non-Compliance
		sortListByalpha(this.defaultUserProfiles);
		sortListByalpha(this.selfUserProfiles);
		sortListByalpha(this.authUserProfiles);
		sortListByalpha(this.guestUserProfiles); // AirWatch Non-Compliance
		
		this.upSelectionTipOfDefault = prepareUpSelectionTipOfDefault();
		this.upSelectionTipOfReg = prepareUpSelectionTipOfReg();
		this.upSelectionTipOfAuth = prepareUpSelectionTipOfAuth();
		this.upSelectionTipOfGuest = prepareUpSelectionTipOfGuest(); // AirWatch Non-Compliance
	}


    public void doUpSupportDataPrepare() {
		this.authUserProfileSupport = prepareAuthUserProfileSupport();
		this.defaultUserProfileSupport = prepareDefaultUserProfileSupport();
		this.selfUserProfileSupport = prepareSelfUserProfileSupport();
		this.guestUserProfileSupport = prepareGuestUserProfileSupport(); // AirWatch Non-Compliance
	}
	
    private boolean isEnabledIDMSelfReg() {
        if(this.getMyObject().isEnabledIDM() && this.getMyObject().isCwpSelectEnabled()
                    && this.getMyObject().getCwp() != null
                    && (Cwp.REGISTRATION_TYPE_BOTH == this.getMyObject().getCwp().getRegistrationType()
                    || Cwp.REGISTRATION_TYPE_REGISTERED == this.getMyObject().getCwp().getRegistrationType())) {
            return true;
        }
        return false;
    }
	
	/**
	 * 
	 * @date Feb 7, 2012
	 * @author wx
	 *
	 * used for: lazy load user profile&cwp of ssidProfile
	 */
	public static class LazyUserProfileObjLoader implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if(bo instanceof SsidProfile) {
				SsidProfile ssidProfile = (SsidProfile)bo;
				if (ssidProfile.getUserProfileDefault() != null) {
					ssidProfile.getUserProfileDefault().getId();
				}
				if (ssidProfile.getUserProfileSelfReg() != null) {
					ssidProfile.getUserProfileSelfReg().getId();
				}
				if (ssidProfile.getUserProfileGuest() != null) {
				    ssidProfile.getUserProfileGuest().getId();
				}
				if (ssidProfile.getRadiusUserProfile() != null) {
					ssidProfile.getRadiusUserProfile().size();
				}
				if (ssidProfile.isCwpSelectEnabled()) {
					if (ssidProfile.getCwp() != null) {
						ssidProfile.getCwp().getId();
					}
				}
			}
			return null;
		}
	}
	
    //---------------AirWatch Non-Compliance--------------------start
    @Override
    public List<CheckItem2> getGuestUserProfiles() {
        return this.guestUserProfiles;
    }

    @Override
    public String getUpSelectionTipOfGuest() {
        return this.upSelectionTipOfGuest;
    }

    @Override
    public boolean isGuestUserProfileSupport() {
        return this.guestUserProfileSupport;
    }

    private String prepareUpSelectionTipOfGuest() {
        if (prepareGuestUserProfileSupport()) {
            return MgrUtil.getUserMessage("glasgow_10.config.v2.select.user.profile.popup.tab.guest")
                    + ":  "
                    + MgrUtil.getUserMessage("glasgow_10.config.configTemplate.wizard.step4.subtitle.guest");
        }
        return null;
    }

    private List<CheckItem2> prepareGuestUserProfiles() {
        if (prepareGuestUserProfileSupport()) {
            Map<Long, CheckItem2> userProfiles = 
                    new HashMap<Long, CheckItem2>(getAvailableUserProfiles());
            
            if (this.getUpType() == UserProfileSelection.USER_PROFILE_TYPE_GUEST
                    && this.getNewlyAddedUserProfile() != null) {
                userProfiles.put(this.getNewlyAddedUserProfile().getId(),
                        this.encapUpToCheckItem(this.getNewlyAddedUserProfile(), true));
            } else if (this.getMyObject().getUserProfileGuest() != null) {
                userProfiles.put(this.getMyObject().getUserProfileGuest().getId(),
                        this.encapUpToCheckItem(this.getMyObject().getUserProfileGuest(), true));
            }
            
            List<CheckItem2> userProfileList = new ArrayList<CheckItem2>(userProfiles.values());
            Collections.sort(userProfileList, 
                    new CheckItem2ComparatorWithId());
            
            return userProfileList;
        }
        return null;
    }

    private boolean prepareGuestUserProfileSupport() {
        if(this.getMyObject() == null
                || !this.getMyObject().isEnableMDM()
                || null == this.getMyObject().getConfigmdmId()
                || !(this.getMyObject().getConfigmdmId().getMdmType() == ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH
                && this.getMyObject().getConfigmdmId().getAwNonCompliance().isEnabledNonCompliance())) {
            return false;
        } else {
            return true;
        }
    }
    //---------------AirWatch Non-Compliance--------------------end

    @Override
    public List<CheckItem2> getAuthFailUserProfiles() {
        return null;
    }

    @Override
    public String getUpSelectionTipOfAuthFail() {
        return null;
    }

    @Override
    public boolean isAuthFailUserProfileSupport() {
        return false;
    }

    @Override
    public List<CheckItem2> getAuthDataUserProfiles() {
        return null;
    }

    @Override
    public String getUpSelectionTipOfAuthData() {
        return null;
    }

    @Override
    public boolean isAuthDataUserProfileSupport() {
        return false;
    }

}
