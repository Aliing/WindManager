package com.ah.mdm.core.profile.service;

public interface BindUserProfileService {
	
	boolean bindUserProfileSuccess(String upid,String profileName,String customId);
	
	boolean unBindUserProfileSuccess(String upid,String profileName,String customId);

}
