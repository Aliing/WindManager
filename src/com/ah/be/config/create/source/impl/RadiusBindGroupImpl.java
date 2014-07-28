package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.source.RadiusBindGroupInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.util.MgrUtil;

/**
 * @author zhang
 * @version 2009-2-25 15:17:55
 */

public class RadiusBindGroupImpl implements RadiusBindGroupInt {
	
	private RadiusOnHiveap radiusServer;
	private List<LocalUserGroup> localUserGroupList;

	public RadiusBindGroupImpl(HiveAp hiveAp){
		radiusServer = hiveAp.getRadiusServerProfile();
		localUserGroupList = new ArrayList<LocalUserGroup>();
		if(radiusServer != null && radiusServer.getLocalUserGroup() != null){
			localUserGroupList.addAll(radiusServer.getLocalUserGroup());
		}
	}
	
	public String getRadiusGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.radiusService");
	}
	
	public String getRadiusName(){
		if(radiusServer != null){
			return radiusServer.getRadiusName();
		}else{
			return null;
		}
	}
	
	public boolean isExistLocalUserGroup(){
		if(radiusServer == null){
			return false;
		}else{
			short databaseType = radiusServer.getDatabaseType();
			boolean localmode = databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL
					|| databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE
					|| databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN;
			
			return localmode && localUserGroupList.size() >0;
		}
	}
	
	public int getUserGroupSize(){
		return localUserGroupList.size();
	}
	
	public String getUserGroupName(int index){
		return localUserGroupList.get(index).getGroupName();
	}
}
