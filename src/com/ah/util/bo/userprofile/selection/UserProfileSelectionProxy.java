package com.ah.util.bo.userprofile.selection;

import java.util.List;

import com.ah.util.CheckItem2;

public class UserProfileSelectionProxy implements UserProfileSelection {

	public UserProfileSelectionProxy(UserProfileSelection upProxy) {
		this.upProxy = upProxy;
	}
	
	public void setProxyThis(UserProfileSelection upProxy) {
		this.upProxy = upProxy;
	}
	
	private UserProfileSelection upProxy;
	
	@Override
	public List<CheckItem2> getDefaultUserProfiles() {
		if (this.upProxy == null) {
			return null;
		}
		
		return this.upProxy.getDefaultUserProfiles();
	}
	@Override
	public List<CheckItem2> getSelfRegUserProfiles() {
		if (this.upProxy == null) {
			return null;
		}

		return this.upProxy.getSelfRegUserProfiles();
	}

	@Override
	public List<CheckItem2> getAuthUserProfiles() {
		if (this.upProxy == null) {
			return null;
		}

		return this.upProxy.getAuthUserProfiles();
	}

    @Override
    public List<CheckItem2> getAuthDataUserProfiles() {
        if (this.upProxy == null) {
            return null;
        }

        return this.upProxy.getAuthDataUserProfiles();
    }
    
    @Override
    public List<CheckItem2> getAuthFailUserProfiles() {
        if(this.upProxy == null) {
            return null;
        }
        return this.upProxy.getAuthFailUserProfiles();
    }
    
    @Override
    public List<CheckItem2> getGuestUserProfiles() {
        if(this.upProxy == null) {
            return null;
        }
        return this.upProxy.getGuestUserProfiles();
    }
    
	@Override
	public void setAddedUserProfile(Long addedId, short type) {
		if (this.upProxy == null) {
			return;
		}
		
		this.upProxy.setAddedUserProfile(addedId, type);
	}

	@Override
	public String getUpSelectionTipOfDefault() {
		if (this.upProxy == null) {
			return null;
		}
		
		return this.upProxy.getUpSelectionTipOfDefault();
	}

	@Override
	public String getUpSelectionTipOfReg() {
		if (this.upProxy == null) {
			return null;
		}
		
		return this.upProxy.getUpSelectionTipOfReg();
	}

	@Override
	public String getUpSelectionTipOfAuth() {
		if (this.upProxy == null) {
			return null;
		}
		
		return this.upProxy.getUpSelectionTipOfAuth();
	}
    @Override
    public String getUpSelectionTipOfAuthData() {
        if (this.upProxy == null) {
            return null;
        }
        
        return this.upProxy.getUpSelectionTipOfAuthData();
    }

    @Override
    public String getUpSelectionTipOfAuthFail() {
        if (this.upProxy == null) {
            return null;
        }
        
        return this.upProxy.getUpSelectionTipOfAuthFail();
    }
    
    @Override
    public String getUpSelectionTipOfGuest() {
        if (this.upProxy == null) {
            return null;
        }
        
        return this.upProxy.getUpSelectionTipOfGuest();
    }
    
	@Override
	public boolean isDefaultUserProfileSupport() {
		if (this.upProxy == null) {
			return false;
		}
		
		return this.upProxy.isDefaultUserProfileSupport();
	}
    
	@Override
	public boolean isSelfUserProfileSupport() {
		if (this.upProxy == null) {
			return false;
		}
		
		return this.upProxy.isSelfUserProfileSupport();
	}

	@Override
	public boolean isAuthUserProfileSupport() {
		if (this.upProxy == null) {
			return false;
		}
		
		return this.upProxy.isAuthUserProfileSupport();
	}

    @Override
    public boolean isAuthDataUserProfileSupport() {
        if (this.upProxy == null) {
            return false;
        }
        
        return this.upProxy.isAuthDataUserProfileSupport();
    }
    
    @Override
    public boolean isAuthFailUserProfileSupport() {
        if (this.upProxy == null) {
            return false;
        }
        
        return this.upProxy.isAuthFailUserProfileSupport();
    }
    
    @Override
    public boolean isGuestUserProfileSupport() {
        if(this.upProxy == null) {
            return false;
        }
        return this.upProxy.isGuestUserProfileSupport();
    }
    
	@Override
	public String getChkUserOnlyStyle() {
		if (this.upProxy == null) {
			return "none";
		}
		
		return this.upProxy.getChkUserOnlyStyle();
	}

	public UserProfileSelection getUpProxy() {
		return upProxy;
	}

	public void setUpProxy(UserProfileSelection upProxy) {
		this.upProxy = upProxy;
	}
	
	@Override
	public short getAddedUserProfileType() {
		if (this.upProxy == null) {
			return UserProfileSelection.USER_PROFILE_TYPE_NONE;
		}
		return this.upProxy.getAddedUserProfileType();
	}
	
	public short getUserProfileSubTabId() {
		if (this.upProxy == null) {
			return UserProfileSelection.USER_PROFILE_TYPE_NONE;
		}
		
		short addedUpType = getAddedUserProfileType();
		if (addedUpType != UserProfileSelection.USER_PROFILE_TYPE_NONE) {
			return addedUpType;
		}
		
		if (this.isDefaultUserProfileSupport()) {
			return UserProfileSelection.USER_PROFILE_TYPE_DEFAULT;
		}
		if (this.isSelfUserProfileSupport()) {
			return UserProfileSelection.USER_PROFILE_TYPE_REGISTRATION;
		}
		if (this.isAuthUserProfileSupport()) {
		    return UserProfileSelection.USER_PROFILE_TYPE_AUTH;
		}
		if (this.isAuthFailUserProfileSupport()) {
			return UserProfileSelection.USER_PROFILE_TYPE_AUTH_FAIL;
		}
		if (this.isAuthDataUserProfileSupport()) {
		    return UserProfileSelection.USER_PROFILE_TYPE_AUTH_DATA;
		}
		if(this.isGuestUserProfileSupport()) {
		    return UserProfileSelection.USER_PROFILE_TYPE_GUEST;
		}
		
		return UserProfileSelection.USER_PROFILE_TYPE_NONE;
	}

}
