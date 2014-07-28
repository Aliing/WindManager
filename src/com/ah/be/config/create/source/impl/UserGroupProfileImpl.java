package com.ah.be.config.create.source.impl;

import java.text.SimpleDateFormat;

import com.ah.be.config.create.source.UserGroupProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.xml.be.config.CacheModeValue;
import com.ah.xml.be.config.ComboPatternValue;
import com.ah.xml.be.config.PasswordMethodValue;

/**
 * @author zhang
 * @version 2009-2-17 14:39:27
 */

public class UserGroupProfileImpl implements UserGroupProfileInt {
	
	private LocalUserGroup userGroup;
//	private HiveAp hiveAp;
//	private List<String> revokeList;

	public UserGroupProfileImpl(LocalUserGroup userGroup, HiveAp hiveAp){
		this.userGroup = userGroup;
	}
	
//	private List<String> generateRange(boolean[] argUser){
//		if(argUser == null){
//			return null;
//		}
//		
//		List<String> userRange = new ArrayList<String>();
//		int start = -1, end = -1;
//		boolean searchStart = true, searchEnd = false;
//		for(int index=0; index<argUser.length; index++){
//			if(searchStart && argUser[index]){
//				start = index;
//				searchStart = false;
//				searchEnd = true;
//			}
//			if(searchEnd && !argUser[index]){
//				end = index - 1;
//				searchStart = true;
//				searchEnd = false;
//			}
//			if(start > 0 && end > 0){
//				if(start == end){
//					userRange.add(String.valueOf(start));
//				}else{
//					userRange.add(start + " " + end);
//				}
//				start = -1;
//				end = -1;
//			}
//		}
//		return userRange;
//	}
	
	public LocalUserGroup getLocalUserGroup(){
		return this.userGroup;
	}
	
	public String getUserGroupName(){
		return userGroup.getGroupName();
	}
	
	public int getUserGroupAttribute(){
		return userGroup.getUserProfileId();
	}
	
	public int getUserGroupVlan(){
		return userGroup.getVlanId();
	}
	
	public int getUserGroupReauthInterval(){
		return userGroup.getReauthTime();
	}
	
	public boolean isConfigUserGroupAttribute(){
		return userGroup.getUserProfileId() > -1;
	}
	
	public boolean isConfigUserGroupVlan(){
		return userGroup.getVlanId() > -1;
	}
	
	public CacheModeValue getCacheModeValue(){
		if(userGroup.getCredentialType() == LocalUserGroup.USERGROUP_CREDENTIAL_FLASH){
			return CacheModeValue.MANDATORY;
		}else{
			return CacheModeValue.TEMPORARY;
		}
	}
	
	public boolean isPskPasswordOnly(){
		return userGroup.getPskGenerateMethod() == LocalUserGroup.PSK_METHOD_PASSWORD_ONLY;
	}
	
	public boolean isPskUserAndPassword(){
		return userGroup.getPskGenerateMethod() == LocalUserGroup.PSK_METHOD_PASSWORD_USERNAME;
	}
	
	public boolean isConfigConcateCharacters(){
		return userGroup.getConcatenateString() != null && !"".equals(userGroup.getConcatenateString());
	}
	
	public String getConcateCharacters(){
		return userGroup.getConcatenateString();
	}
	
	public boolean isPskGroup(){
		return userGroup.getUserType() != LocalUserGroup.USERGROUP_USERTYPE_RADIUS;
	}
	
	public boolean isConfigPskStartTime(){
		return (userGroup.getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_ONCE || 
				userGroup.getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_SCHEDULE) &&
			userGroup.getStartTime() != null;
	}
	
	public String getPskGroupStartTime(){
		return new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss").format(userGroup.getStartTime());
	}
	
	public boolean isConfigPskExpiredTime(){
		return (userGroup.getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_ONCE || 
				userGroup.getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_SCHEDULE) &&
			userGroup.getExpiredTime() != null;
	}
	
	public String getPskGroupExpiredTime(){
		return new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss").format(userGroup.getExpiredTime());
	}
	
	public boolean isDigits(){
		return userGroup.getBlnCharDigits();
	}
	
	public boolean isLetters(){
		return userGroup.getBlnCharLetters();
	}
	
	public boolean isSpecialCharacters(){
		return userGroup.getBlnCharSpecial();
	}
	
	public ComboPatternValue getComboPatternValue(){
		if(userGroup.getPersonPskCombo() == LocalUserGroup.PSKFORMAT_COMBO_OR){
			return ComboPatternValue.OR;
		}else if(userGroup.getPersonPskCombo() == LocalUserGroup.PSKFORMAT_COMBO_AND){
			return ComboPatternValue.AND;
		}else{
			return ComboPatternValue.NO;
		}
	}
	
	public boolean isPskGroupAuto(){
		return userGroup.getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK;
	}
	
	public String getPskUserPrefix(){
		return userGroup.getUserNamePrefix();
	}
	
	public boolean isConfigPskUserSecret(){
		return userGroup.getPskSecret() != null && !"".equals(userGroup.getPskSecret());
	}
	
	public String getPskUserSecret(){
		return userGroup.getPskSecret();
	}
	
	public boolean isConfigPskUserLocation(){
		return userGroup.getPskLocation() != null && !"".equals(userGroup.getPskLocation());
	}
	
	public String getPskUserLocation(){
		return userGroup.getPskLocation();
	}
	
	public int getPskPasswordLength(){
		return userGroup.getPskLength();
	}
	
	public boolean isConfigPskSchedule(){
		return userGroup.getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_SCHEDULE &&
			userGroup.getSchedule() != null;
	}
	
	public String getPskSchedule(){
		return userGroup.getSchedule().getSchedulerName();
	}
	
	public PasswordMethodValue getPasswordMethod(){
		if(this.isPskGroupAuto()){
			return PasswordMethodValue.AUTO;
		}else{
			return PasswordMethodValue.MANUAL;
		}
	}
	
//	public int getRevokeUserSize(){
//		if(revokeList == null){
//			return 0;
//		}else{
//			return revokeList.size();
//		}
//	}
//	
//	public String getRevokeUserValue(int index){
//		return revokeList.get(index);
//	}
	
	public boolean isVoiceDevice() {
		return userGroup.isVoiceDevice();
	}
}
