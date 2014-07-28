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
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.util.CheckItem2;
import com.ah.util.MgrUtil;

public class PortUserProfileSelectionImpl extends UserProfileSelectionTemplate
        implements UserProfileSelection {
    
    private static final String EMPTY_STR = "";
    private static final String JOIN_SYMBOL = ":  ";
    private boolean supportPhone;
    
    @SuppressWarnings("all")
    private PortUserProfileSelectionImpl() {
    }
    
    public PortUserProfileSelectionImpl(Object boToDeal) {
        this.setBoToDeal(boToDeal);
    }
    
    public PortUserProfileSelectionImpl(Object boToDeal, Long domainId) {
        super(boToDeal, domainId, false);
        this.doDataPrepare();
    }
    
    public PortUserProfileSelectionImpl(Long id, Class<? extends HmBo> c, Long domainId, boolean supportPhone) {
        super(id, c, new LazyUserProfileObjLoader(), domainId, false);
        this.supportPhone = supportPhone;
        this.doDataPrepare();
    }
    
    private PortAccessProfile accessProfile = null;
    
    @Override
    @SuppressWarnings("unchecked")
    protected PortAccessProfile getMyObject() {
        if (this.accessProfile == null
                && this.getBoToDeal() instanceof PortAccessProfile) {
            this.accessProfile = (PortAccessProfile)this.getBoToDeal();
        }
        
        return this.accessProfile;
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
        } else if (this.getMyObject().getDefUserProfile() != null) {
            userProfiles.put(this.getMyObject().getDefUserProfile().getId(),
                    this.encapUpToCheckItem(this.getMyObject().getDefUserProfile(), true));
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
        } else if (this.getMyObject().getSelfRegUserProfile() != null) {
            userProfiles.put(this.getMyObject().getSelfRegUserProfile().getId(),
                    this.encapUpToCheckItem(this.getMyObject().getSelfRegUserProfile(), true));
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
        if (this.getMyObject().getAuthOkUserProfile() != null
                && this.getMyObject().getAuthOkUserProfile().size() > 0) {
            for (UserProfile userProfile : this.getMyObject().getAuthOkUserProfile()) {
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
    public List<CheckItem2> getAuthDataUserProfiles() {
        return this.authDataUserProfiles;
    }
    private List<CheckItem2> prepareAuthDataUserProfiles() {
        if (this.getMyObject() == null || isSupport4LAN()) {
            return null;
        }
        
        Map<Long, CheckItem2> userProfiles = 
                new HashMap<Long, CheckItem2>(getAvailableUserProfiles());
        
        if (this.getUpType() == UserProfileSelection.USER_PROFILE_TYPE_AUTH_DATA
                && this.getNewlyAddedUserProfile() != null) {
            userProfiles.put(this.getNewlyAddedUserProfile().getId(),
                    this.encapUpToCheckItem(this.getNewlyAddedUserProfile(), true));
        } 
        if (this.getMyObject().getAuthOkDataUserProfile() != null
                && this.getMyObject().getAuthOkDataUserProfile().size() > 0) {
            for (UserProfile userProfile : this.getMyObject().getAuthOkDataUserProfile()) {
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
    public List<CheckItem2> getAuthFailUserProfiles() {
        return this.authFailUserProfiles;
    }
    private List<CheckItem2> prepareAuthFailUserProfiles() {
        if (this.getMyObject() == null) {
            return null;
        }
        
        Map<Long, CheckItem2> userProfiles = 
                new HashMap<Long, CheckItem2>(getAvailableUserProfiles());
        
        if (this.getUpType() == UserProfileSelection.USER_PROFILE_TYPE_AUTH_FAIL
                && this.getNewlyAddedUserProfile() != null) {
            userProfiles.put(this.getNewlyAddedUserProfile().getId(),
                    this.encapUpToCheckItem(this.getNewlyAddedUserProfile(), true));
        } 
        if (this.getMyObject().getAuthFailUserProfile() != null
                && this.getMyObject().getAuthFailUserProfile().size() > 0) {
            for (UserProfile userProfile : this.getMyObject().getAuthFailUserProfile()) {
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
        
        boolean isAuthEnabled = isAuthEnabled();
        
        if (this.isDefaultUserProfileSupport()) {
            if(this.getMyObject().getPortType() == PortAccessProfile.PORT_TYPE_ACCESS) {
                return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.default")
                        + JOIN_SYMBOL
                        + MgrUtil.getUserMessage(isAuthEnabled ? 
                                "config.configTemplate.wizard.step4.subtitle.access.auth.default"
                                        : "config.configTemplate.wizard.step4.subtitle.access.default");
            } else if(this.getMyObject().getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA) {
                return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.default")
                        + JOIN_SYMBOL 
                        + MgrUtil.getUserMessage(isAuthEnabled? 
                                "config.configTemplate.wizard.step4.subtitle.phone.auth.default" 
                                : "config.configTemplate.wizard.step4.subtitle.phone.default.voice");
            }
        }
        
        return EMPTY_STR;
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
        
        if (this.getMyObject().getPortType() == PortAccessProfile.PORT_TYPE_ACCESS) {
            return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.authok")
                    + JOIN_SYMBOL
                    + MgrUtil.getUserMessage("config.configTemplate.wizard.step4.subtitle.access.authOk");
        } else if (this.getMyObject().getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA) {
            return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.authok")
                    + " (Voice)"
                    + JOIN_SYMBOL
                    + MgrUtil.getUserMessage("config.configTemplate.wizard.step4.subtitle.phone.authOk.voice");
        } else {
            return EMPTY_STR;
        }
    }
    
    @Override
    public String getUpSelectionTipOfAuthData() {
        return this.upSelectionTipOfAuthData;
    }
    private String prepareUpSelectionTipOfAuthData() {
        if (this.getMyObject() == null) {
            return null;
        }
        
        return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.authok")
                + " (Data)"
                + JOIN_SYMBOL 
                + MgrUtil.getUserMessage("config.configTemplate.wizard.step4.subtitle.phone.authOk.data");
    }

    @Override
    public String getUpSelectionTipOfAuthFail() {
        return this.upSelectionTipOfAuthFail;
    }
    private String prepareUpSelectionTipOfAuthFail() {
        if (this.getMyObject() == null) {
            return null;
        }
        
        return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.authfail")
                + JOIN_SYMBOL 
                + MgrUtil.getUserMessage("config.configTemplate.wizard.step4.subtitle.access.authFail");
    }


    @Override
    public boolean isDefaultUserProfileSupport() {
        return this.defaultUserProfileSupport;
    }
    private boolean prepareDefaultUserProfileSupport() {
        if (this.getMyObject() == null) {
            return false;
        }
        
        if (this.getMyObject().isEnabledCWP()) {
            if (!this.getMyObject().isEnabledMAC()
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
        
        if (this.getMyObject().isEnabledCWP()) {
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
        
        if (isAuthEnabled()) {
            return true;
        }

        return false;
    }
    
    public boolean isAuthDataUserProfileSupport() {
        return this.authDataUserProfileSupport;
    }
    private boolean prepareAuthDataUserProfileSupport() {
        if (this.getMyObject() == null
                || this.getMyObject().getPortType() != PortAccessProfile.PORT_TYPE_PHONEDATA) {
            return false;
        }
        
        if (isAuthEnabled()) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean isAuthFailUserProfileSupport() {
        return this.authFailUserProfileSupport;
    }
    private boolean prepareAuthFailUserProfileSupport() {
        if (this.getMyObject() == null) {
            return false;
        }
        if (this.getMyObject().getPortType() == PortAccessProfile.PORT_TYPE_ACCESS) {
            if(this.getMyObject().isEnabledSameVlan()) {
                return false;
            } else if (isAuthEnabled()) {
                if(this.getMyObject().isEnabledCWP()) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public String getChkUserOnlyStyle() {
        if (this.isChkUserOnlyEnabled()) {
            return EMPTY_STR;
        }
        
        return "none";
    }
    
    public boolean isChkUserOnlyEnabled() {
        if (this.getMyObject() != null) {
            if (this.getMyObject().isEnabled8021X()) {
                return true;
            }
            if (this.getMyObject().isEnabledMAC()) {
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
                availableUserProfiles = getBoCheckItems("userProfileName||'('||attributeValue||')'", 
                        UserProfile.class, 
                        null);
        }
        
        return availableUserProfiles;
    }
    
    @Override
    protected void doDataPrepare() {
        this.defaultUserProfileSupport = prepareDefaultUserProfileSupport();
        this.authUserProfileSupport = prepareAuthUserProfileSupport();
        this.guestUserProfileSupport = prepareGuestUserProfileSupport();// AirWatch Non-Compliance
        
        this.defaultUserProfiles = prepareDefaultUserProfiles();
        this.authUserProfiles = prepareAuthUserProfiles();
        this.guestUserProfiles = prepareGuestUserProfiles(); // AirWatch Non-Compliance
        
        sortListByalpha(this.defaultUserProfiles);
        sortListByalpha(this.authUserProfiles);
        sortListByalpha(this.guestUserProfiles); // AirWatch Non-Compliance
        
        this.upSelectionTipOfDefault = prepareUpSelectionTipOfDefault();
        this.upSelectionTipOfAuth = prepareUpSelectionTipOfAuth();
        this.upSelectionTipOfGuest = prepareUpSelectionTipOfGuest(); // AirWatch Non-Compliance
        
        if(this.supportPhone){
            this.authDataUserProfileSupport = prepareAuthDataUserProfileSupport();
            
            this.authDataUserProfiles = prepareAuthDataUserProfiles();
            
            sortListByalpha(this.authDataUserProfiles);
            
            this.upSelectionTipOfAuthData = prepareUpSelectionTipOfAuthData();
        } else {
            this.selfUserProfileSupport = prepareSelfUserProfileSupport();
            this.authFailUserProfileSupport = prepareAuthFailUserProfileSupport();
            
            this.selfUserProfiles = prepareSelfRegUserProfiles();
            this.authFailUserProfiles = prepareAuthFailUserProfiles();
            
            sortListByalpha(this.selfUserProfiles);
            sortListByalpha(this.authFailUserProfiles);
            
            this.upSelectionTipOfReg = prepareUpSelectionTipOfReg();
            this.upSelectionTipOfAuthFail = prepareUpSelectionTipOfAuthFail();
        }
    }

    public void doUpSupportDataPrepare() {
        this.defaultUserProfileSupport = prepareDefaultUserProfileSupport();
        this.selfUserProfileSupport = prepareSelfUserProfileSupport();
        this.authUserProfileSupport = prepareAuthUserProfileSupport();
        this.authDataUserProfileSupport = prepareAuthDataUserProfileSupport();
        this.authFailUserProfileSupport = prepareAuthFailUserProfileSupport();
        this.guestUserProfileSupport = prepareGuestUserProfileSupport(); // AirWatch Non-Compliance
    }
    
    private boolean isSupport4LAN() {
        if(this.getMyObject() != null) {
            if(this.getMyObject().getProduct() != PortAccessProfile.CHESAPEAKE) {
                return true;
            } 
        }
        return false;
    }
    
    public static class LazyUserProfileObjLoader implements QueryBo {
        @Override
        public Collection<HmBo> load(HmBo bo) {
            if(bo instanceof PortAccessProfile) {
                PortAccessProfile profile = (PortAccessProfile)bo;
                if(null != profile.getCwp()) {
                    profile.getCwp().getId();
                }
                if(null != profile.getDefUserProfile()) {
                    profile.getDefUserProfile().getId();
                }
                if(null != profile.getSelfRegUserProfile()) {
                    profile.getSelfRegUserProfile().getId();
                }
                if(null != profile.getGuestUserProfile()) {
                    profile.getGuestUserProfile().getId();
                }
                if(!profile.getAuthOkUserProfile().isEmpty()) {
                    profile.getAuthOkUserProfile().size();
                }
                if(!profile.getAuthFailUserProfile().isEmpty()) {
                    profile.getAuthFailUserProfile().size();
                }
                if(!profile.getAuthOkDataUserProfile().isEmpty()) {
                    profile.getAuthOkDataUserProfile().size();
                }
            }
            return null;
        }
    }

    private boolean isAuthEnabled() {
        return this.getMyObject().isIDMAuthEnabled() || this.getMyObject().isRadiusAuthEnable();
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
                    + JOIN_SYMBOL
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
            } else if (this.getMyObject().getGuestUserProfile() != null) {
                userProfiles.put(this.getMyObject().getGuestUserProfile().getId(),
                        this.encapUpToCheckItem(this.getMyObject().getGuestUserProfile(), true));
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
                || null == this.getMyObject().getConfigtempleMdm()
                || !(this.getMyObject().getConfigtempleMdm().getMdmType() == ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH
                && this.getMyObject().getConfigtempleMdm().getAwNonCompliance().isEnabledNonCompliance())) {
            return false;
        } else {
            //FIXME AirWatch NonCompliance not support wired right now
            //return true;
            return false;
        }
    }
    //---------------AirWatch Non-Compliance--------------------end
}
