package com.ah.util.bo.userprofile.selection;

import java.util.List;

import com.ah.util.CheckItem2;

public interface UserProfileSelection {
	//used to indicate which kind of user profile is dealt
	public static final short USER_PROFILE_TYPE_DEFAULT = 0;
	public static final short USER_PROFILE_TYPE_REGISTRATION = 1;
	public static final short USER_PROFILE_TYPE_AUTH = 2;
	public static final short USER_PROFILE_TYPE_AUTH_FAIL = 3;
	public static final short USER_PROFILE_TYPE_NONE = -1;
	
	// for PHONE & DATA - DATA
	public static final short USER_PROFILE_TYPE_DEFAULT_DATA = USER_PROFILE_TYPE_REGISTRATION; // 4
	public static final short USER_PROFILE_TYPE_AUTH_DATA = USER_PROFILE_TYPE_AUTH_FAIL; // 5
	
	// for AirWatch
	public static final short USER_PROFILE_TYPE_GUEST = 4; 
	
	/**
	 * fetch out all user profiles can be used as default user profile
	 * @return
	 */
	public List<CheckItem2> getDefaultUserProfiles();

	/**
	 * fetch out all user profiles can be used as self registration user profile
	 * @return
	 */
	public List<CheckItem2> getSelfRegUserProfiles();

	/**
	 * fetch out all user profiles can be used as authentication user profile
	 * @return
	 */
	public List<CheckItem2> getAuthUserProfiles();
	
	/**
	 * fetch out all user profiles can be used as authentication fail user profile
	 * @return
	 */
	public List<CheckItem2> getAuthFailUserProfiles();

	/**
	 * used to add a newly added user profile
	 * @param addedId :id of newly added user profile
	 * @param type :default/self-reg/auth
	 */
	public void setAddedUserProfile(Long addedId, short type);

	/**
	 * get the tip shown while selecting default user profiles
	 * @return
	 */
	public String getUpSelectionTipOfDefault();

	/**
	 * get the tip shown while selecting self-registration user profiles
	 * @return
	 */
	public String getUpSelectionTipOfReg();

	/**
	 * get the tip shown while selecting authentication user profiles
	 * @return
	 */
	public String getUpSelectionTipOfAuth();
	
	/**
	 * get the tip shown while selecting authentication user profiles
	 * @return
	 */
	public String getUpSelectionTipOfAuthFail();

	/**
	 * whether default user profile can be selected in this case
	 * @return
	 */
	public boolean isDefaultUserProfileSupport();

	/**
	 * whether self-registration user profile can be selected in this case
	 * @return
	 */
	public boolean isSelfUserProfileSupport();

	/**
	 * whether authentication user profile can be selected in this case
	 * @return
	 */
	public boolean isAuthUserProfileSupport();
	
	/**
	 * whether authentication user profile can be selected in this case
	 * @return
	 */
	public boolean isAuthFailUserProfileSupport();
	
	/**
	 * whether check user only check box should be shown
	 * @return
	 */
	public String getChkUserOnlyStyle();
	
	/**
	 * default/self-reg/auth/authFail
	 * @return
	 */
	public short getAddedUserProfileType();
	

	/**
	 * Phone & Data - Data
	 */
    public List<CheckItem2> getAuthDataUserProfiles();
    public String getUpSelectionTipOfAuthData();
    public boolean isAuthDataUserProfileSupport();
    
    /**
     * AirWatch - Non-Compliance
     */
    public List<CheckItem2> getGuestUserProfiles();
    public String getUpSelectionTipOfGuest();
    public boolean isGuestUserProfileSupport();
}
