package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.PskAutoUserGroupInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.util.MgrUtil;

/**
 * @author zhang
 * @version 2009-3-12 11:21:38
 */

@SuppressWarnings("static-access")
public class PskAutoUserGroupImpl implements PskAutoUserGroupInt {
	
	private final LocalUserGroup userGroupObj;
	private final HiveAp hiveAp;
	private List<String> revokeList;
	private List<String> autoUserList;
	
	public static final int AUTO_USER_COUNT = 10020;
	
	public PskAutoUserGroupImpl(LocalUserGroup userGroup, HiveAp hiveAp){
		this.userGroupObj = userGroup;
		this.hiveAp = hiveAp;
		
		if(userGroup.getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
			
			List<Integer> statusList = new ArrayList<Integer>();
			
			/** load revoke users */
			String where = "localUserGroup = :s1 AND (revoked = :s2 OR status in (:s3))";
			Object[] revokeArg = new Object[3];
			statusList.clear();
			statusList.add(LocalUser.STATUS_REVOKED);
			statusList.add(LocalUser.STATUS_PARTIAL_REVOKED);
			revokeArg[0] = userGroup;
			revokeArg[1] = true;
			revokeArg[2] = statusList;
			List<LocalUser> userList = MgrUtil.getQueryEntity().executeQuery(LocalUser.class, null,
					new FilterParams(where, revokeArg), hiveAp.getOwner().getId());
			
			if(!userList.isEmpty()){
				boolean[] argIndex = new boolean[AUTO_USER_COUNT];
				for(LocalUser localUser : userList){
					String userName = localUser.getUserName();
					int index = Integer.valueOf(userName.substring(userName.length()-4));
					argIndex[index] = true;
				}
				revokeList = generateRange(argIndex);
			}
			
			where = "localUserGroup = :s1 AND revoked = :s2 AND status in (:s3)";
			Object[] normalArg = new Object[3];
			statusList.clear();
			statusList.add(LocalUser.STATUS_FREE);
			statusList.add(LocalUser.STATUS_ALLOCATED);
			statusList.add(LocalUser.STATUS_EXPIRED);
			normalArg[0] = userGroup;
			normalArg[1] = false;
			normalArg[2] = statusList;
			userList = MgrUtil.getQueryEntity().executeQuery(LocalUser.class, null,
					new FilterParams(where, normalArg), hiveAp.getOwner().getId());
			if(!userList.isEmpty()){
				boolean[] argIndex = new boolean[AUTO_USER_COUNT];
				for(LocalUser localUser : userList){
					String userName = localUser.getUserName();
					int index = Integer.valueOf(userName.substring(userName.length()-4));
					argIndex[index] = true;
				}
				autoUserList = generateRange(argIndex);
			}
		}
	}
	
	public String getUserGroupGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.localUserGroups");
	}
	
	public static List<String> generateRange(boolean[] argUser){
		if(argUser == null){
			return null;
		}
		
		List<String> userRange = new ArrayList<String>();
		int start = -1, end = -1;
		boolean searchStart = true, searchEnd = false;
		for(int index=0; index<argUser.length; index++){
			if(searchStart && argUser[index]){
				start = index;
				searchStart = false;
				searchEnd = true;
			}
			if(searchEnd && !argUser[index]){
				end = index - 1;
				searchStart = true;
				searchEnd = false;
			}
			if(start > 0 && end > 0){
				if(start == end){
					userRange.add(String.valueOf(start));
				}else{
					userRange.add(start + " " + end);
				}
				start = -1;
				end = -1;
			}
		}
		return userRange;
	}
	
	private long getCountOfPskUser(){
		String sqlStr = "select count(id) from " + LocalUser.class.getSimpleName();
		List<?> counts = MgrUtil.getQueryEntity().executeQuery(sqlStr, null, 
				new FilterParams("localUserGroup", this.userGroupObj), this.hiveAp.getOwner().getId());
		if(counts != null && !counts.isEmpty()){
			return (Long)counts.get(0);
		}else{
			return 0;
		}
	}

	public String getUserGroupName(){
		return userGroupObj.getGroupName();
	}
	
	public boolean isConfigOldIndexRange(){
		if(userGroupObj.getValidTimeType() ==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE && userGroupObj.isBlnBulkType()){
			return userGroupObj.getIndexRange() > 0;
		}else{
			return this.getCountOfPskUser() > 0;
		}
	}
	
	public String getIndexRangeValue(){
		if(userGroupObj.getValidTimeType() ==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE && userGroupObj.isBlnBulkType()){
			return "1" + " " + userGroupObj.getIndexRange();
		}else{
			return "1" + " " + this.getCountOfPskUser();
		}
	}
	
	public int getRevokeUserSize(){
		if(revokeList == null){
			return 0;
		}else{
			return revokeList.size();
		}
	}
	
	public int getAutoUserSize(){
		if(userGroupObj.getValidTimeType() ==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE && userGroupObj.isBlnBulkType()){
			return 1;
		}
		
		if(autoUserList == null){
			return 0;
		}else{
			return autoUserList.size();
		}
	}
	
	public String getRevokeUserName(int index){
		return revokeList.get(index);
	}
	
	public String getAutoUserName(int index){
		if(userGroupObj.getValidTimeType() ==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE && userGroupObj.isBlnBulkType()){
			return "1" + " " + userGroupObj.getIndexRange();
		}
		return autoUserList.get(index);
	}
	
	public boolean isConfigAutoGeneration(){
		return userGroupObj.getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK;
	}
	
	public boolean isConfigPpskBulk(){
		return userGroupObj.getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK && 
		userGroupObj.getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_SCHEDULE && 
		userGroupObj.isBlnBulkType();
	}
	
	public int getPpskBulkNumber() throws CreateXMLException{
		int bulkNumber = userGroupObj.getBulkNumber();
		if(bulkNumber > 500 && NmsUtil.compareSoftwareVersion("4.1.2.0", hiveAp.getSoftVer()) > 0){
			throw new CreateXMLException(NmsUtil.getUserMessage("error.be.config.create.tooMorePpskUser"));
		}
		return bulkNumber;
	}
	
	public String getPpskBulkInterval(){
		userGroupObj.getIntervalDay();
		String hour = String.valueOf(userGroupObj.getIntervalHour());
		String time = String.valueOf(userGroupObj.getIntervalMin());
		return String.valueOf(userGroupObj.getIntervalDay()) + " " +
			(hour.length()>1? hour : "0"+hour) + ":" +
			(time.length()>1? time : "0"+time);
	}

}