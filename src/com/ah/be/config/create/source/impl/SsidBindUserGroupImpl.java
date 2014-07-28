package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.source.SsidBindUserGroupInt;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.MgrUtil;

/**
 * @author zhang
 * @version 2009-2-24 15:22:47
 */

public class SsidBindUserGroupImpl implements SsidBindUserGroupInt {
	
	private SsidProfile ssidProfile;
	private List<LocalUserGroup> pskUserGroupList;
	
	public SsidBindUserGroupImpl(SsidProfile ssidProfile){
		this.ssidProfile = ssidProfile;
		pskUserGroupList = new ArrayList<LocalUserGroup>(ssidProfile.getLocalUserGroups());
	}
	
	public String getSsidGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.ssidProfiles");
	}

	public boolean isSsidBindGroup(){
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_PSK &&
			ssidProfile.getLocalUserGroups() != null &&
			ssidProfile.getLocalUserGroups().size() > 0;
	}
	
	public String getSsidName(){
		return ssidProfile.getSsid();
	}
	
	public int getUserGroupSize(){
		return pskUserGroupList.size();
	}
	
	public String getUserGroupName(int index){
		return pskUserGroupList.get(index).getGroupName();
	}
}
