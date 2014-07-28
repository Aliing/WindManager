package com.ah.mdm.core.profile.service;

import com.ah.mdm.core.profile.entity.OnBoardUIInfo;

public interface OnBoardUISettingService {
	

	OnBoardUIInfo getOnBoardUIPage(String customId);
	
	boolean customizedOnBoardUIPage(String customId,OnBoardUIInfo uiObject);
	
	boolean resetOnBoardUIPage(String customId);
	
	String previewOnBoardUIPage(String customId,String pageName,OnBoardUIInfo uiObject);
	
	
	
	

}
