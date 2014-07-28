package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.MobileDevicePolicyInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.useraccess.UserProfile;
import com.ah.util.MgrUtil;

@SuppressWarnings("static-access")
public class MobileDevicePolicyImpl implements MobileDevicePolicyInt {
	
	private List<DevicePolicyRule> devicePolicyRules;
	private HiveAp hiveAp;
	
	public static Map<Long, UserProfile[]> HIS_USERPROFILE_MAP = new HashMap<Long, UserProfile[]>();
	private UserProfile[] currentUPArray = new UserProfile[64];

	public MobileDevicePolicyImpl(List<DevicePolicyRule> devicePolicyRules, List<UserProfile> userProfileList, HiveAp hiveAp) throws CreateXMLException{
		this.devicePolicyRules = devicePolicyRules;
		this.hiveAp = hiveAp;
		initCurrentUPArray(userProfileList, hiveAp);
	}
	
	public List<DevicePolicyRule> getDevicePolicyRules(){
		return this.devicePolicyRules;
	}
	
	private void initCurrentUPArray(List<UserProfile> userProfileList, HiveAp hiveAp) throws CreateXMLException{
		List<UserProfile> upNewAddList = new ArrayList<UserProfile>();
		int index=0;
		boolean exists = false;
		UserProfile[] hisUPArray = HIS_USERPROFILE_MAP.get(hiveAp.getId());
		if(hisUPArray == null || hisUPArray.length == 0){
			try{
				for(UserProfile userProfile : userProfileList){
					currentUPArray[index] = userProfile;
					index++;
				}
			}catch(Exception ex){
				throw new CreateXMLException("Max allow 64 UserProfiles.");
			}
		}else{
			for(UserProfile userProfile : userProfileList){
				exists = false;
				for(int i=0; i<hisUPArray.length; i++){
					if(hisUPArray[i] != null && userProfile.getUserProfileName() == hisUPArray[i].getUserProfileName()){
						currentUPArray[i] = userProfile;
						exists = true;
						break;
					}
				}
				if(!exists){
					upNewAddList.add(userProfile);
				}
			}
			for(UserProfile upNewAdd : upNewAddList){
				for(int j=0; j<currentUPArray.length; j++){
					if(currentUPArray[j] == null){
						currentUPArray[j] = upNewAdd;
						break;
					}
				}
			}
		}
		HIS_USERPROFILE_MAP.put(hiveAp.getId(), currentUPArray);
	}
	
	public String getDevicePolicyGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.devicePolicy");
	}
	
	public boolean isConfigDeviceGroup(){
		return devicePolicyRules != null && !devicePolicyRules.isEmpty();
	}
	
	public String getDevicePolicyName(){
		return hiveAp.getMacAddress() + SecurityObjectProfileImpl.DEVICE_POLICY_SUFFIX;
	}
	
	public boolean isConfigApplyOnce(){
		return false;
	}
	
	public boolean isConfigMultiple(){
		return true;
	}
	
	public boolean isClassificationMac(){
		return true;
	}
	
	public boolean isClassificationOs(){
		return true;
	}
	
	public boolean isClassificationDomain(){
		return true;
	}
	
	public int getRuleSize(){
		if(devicePolicyRules == null){
			return 0;
		}
		return devicePolicyRules.size();
	}
	
	public String getOriginalAttribute(int index){
		index = getReverseIndex(index);
		return devicePolicyRules.get(index).getDescription();
	}
	
	public String getDeviceGroupName(int index){
		index = getReverseIndex(index);
		DevicePolicyRule rule = devicePolicyRules.get(index);
		return DeviceGroupImpl.DeviceGroup.getDeviceGroupName(
					rule.getMacObj(), rule.getOsObj(), rule.getDomObj(),rule.getOwnership());
	}
	
	public int getMappedAttribute(int index){
		index = getReverseIndex(index);
		UserProfile userProfile = MgrUtil.getQueryEntity().findBoById(
				UserProfile.class, devicePolicyRules.get(index).getUserProfileId());
		if(userProfile != null){
			return userProfile.getAttributeValue();
		}else{
			return -1;
		}
	}
	
	private int getReverseIndex(int index){
		return devicePolicyRules.size() - index -1;
	}
	
	public int getPolicyRuleId(int index) throws CreateXMLException{
		index = getReverseIndex(index);
		return getRuleId(index);
	}
	
	private int getRuleId(int index) throws CreateXMLException{
		DevicePolicyRule rule = devicePolicyRules.get(index);
		String upName = rule.getDescription();
		int upIndex = 0;
		for(int i=0; i<currentUPArray.length; i++){
			if(currentUPArray[i] != null && currentUPArray[i].getUserProfileName().equals(upName)){
				upIndex = i + 1;
				break;
			}
		}
		if(upIndex == 0){
			throw new CreateXMLException("Cannot get UserProfiles ID for Device Policy.");
		}
		return upIndex * 1000 + rule.getRuleId();
	}
	
	public boolean isConfigBefore(int index){
		return index != 0;
	}
	
	public int getBeforeValue(int index){
		return devicePolicyRules.size() - index;
	}
	
	public int getBeforeRuleId(int index) throws CreateXMLException{
		index = devicePolicyRules.size()-index;
		return getRuleId(index);
	}
}
