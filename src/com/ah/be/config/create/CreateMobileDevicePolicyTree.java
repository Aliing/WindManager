package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.MobileDevicePolicyInt;
import com.ah.xml.be.config.AhInt;
import com.ah.xml.be.config.MdpRuleDeviceGroup;
import com.ah.xml.be.config.MdpRuleOriginalUserProfile;
import com.ah.xml.be.config.MdpRuleReassignedUserProfileAttr;
import com.ah.xml.be.config.MobileDevicePolicyApply;
import com.ah.xml.be.config.MobileDevicePolicyClientClassification;
import com.ah.xml.be.config.MobileDevicePolicyObj;
import com.ah.xml.be.config.MobileDevicePolicyRule;
import com.ah.xml.be.config.MobileDevicePolicyRuleSeq;

public class CreateMobileDevicePolicyTree {
	
	private MobileDevicePolicyInt devicePolicyImpl;
	private GenerateXMLDebug oDebug;
	
	private MobileDevicePolicyObj mobileDevicePolicyObj;
	
	private List<Object> devicePolicyChildList_1 = new ArrayList<Object>();
	
	private List<Object> devicePolicyRuleChildList_1 = new ArrayList<Object>();
	private List<Object> devicePolicyRuleChildList_2 = new ArrayList<Object>();

	public CreateMobileDevicePolicyTree(MobileDevicePolicyInt devicePolicyImpl, GenerateXMLDebug oDebug){
		this.devicePolicyImpl = devicePolicyImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		if(devicePolicyImpl.isConfigDeviceGroup()){
			mobileDevicePolicyObj = new MobileDevicePolicyObj();
			generateUserAttributePolicyLevel_1();
		}
	}
	
	public MobileDevicePolicyObj getUserAttributePolicyObj(){
		return this.mobileDevicePolicyObj;
	}
	
	private void generateUserAttributePolicyLevel_1() throws Exception{
		
		/** attribute: operation */
		mobileDevicePolicyObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		oDebug.debug("/configuration", 
				"mobile-device-policy", GenerateXMLDebug.SET_NAME, 
				devicePolicyImpl.getDevicePolicyGuiName(), devicePolicyImpl.getDevicePolicyName());
		mobileDevicePolicyObj.setName(devicePolicyImpl.getDevicePolicyName());
		
		/** element: <mobile-device-policy>.<apply> */
		MobileDevicePolicyApply applyObj = new MobileDevicePolicyApply();
		devicePolicyChildList_1.add(applyObj);
		mobileDevicePolicyObj.setApply(applyObj);
		
		/** element: <mobile-device-policy>.<client-classification> */
		MobileDevicePolicyClientClassification classification = new MobileDevicePolicyClientClassification();
		devicePolicyChildList_1.add(classification);
		mobileDevicePolicyObj.setClientClassification(classification);
		
		/** element: <mobile-device-policy>.<rule> */
		for(int index=0; index<devicePolicyImpl.getRuleSize(); index++){
			mobileDevicePolicyObj.getRule().add(this.createMobileDevicePolicyRule(index));
		}
		
		generateUserAttributePolicyLevel_2();
	}
	
	private void generateUserAttributePolicyLevel_2(){
		/**
		 * <mobile-device-policy>.<apply>										MobileDevicePolicyApply
		 * <mobile-device-policy>.<client-classification>						MobileDevicePolicyClientClassification
		 */
		for(Object childObj : devicePolicyChildList_1){
			
			/** element: <mobile-device-policy>.<apply> */
			if(childObj instanceof MobileDevicePolicyApply){
				MobileDevicePolicyApply applyObj = (MobileDevicePolicyApply)childObj;
				
				/** attribute: operation */
				applyObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <mobile-device-policy>.<apply>.<once> */
				oDebug.debug("/configuration/mobile-device-policy[@name='"+mobileDevicePolicyObj.getName()+"']/apply",
						"once", GenerateXMLDebug.CONFIG_ELEMENT, 
						devicePolicyImpl.getDevicePolicyGuiName(), devicePolicyImpl.getDevicePolicyName());
				if(devicePolicyImpl.isConfigApplyOnce()){
					applyObj.setOnce("");
				}
				
				/** element: <mobile-device-policy>.<apply>.<multiple-times> */
				oDebug.debug("/configuration/mobile-device-policy[@name='"+mobileDevicePolicyObj.getName()+"']/apply",
						"multiple-times", GenerateXMLDebug.CONFIG_ELEMENT, 
						devicePolicyImpl.getDevicePolicyGuiName(), devicePolicyImpl.getDevicePolicyName());
				if(devicePolicyImpl.isConfigMultiple()){
					applyObj.setMultipleTimes("");
				}
			}
			
			/** element: <mobile-device-policy>.<client-classification> */
			if(childObj instanceof MobileDevicePolicyClientClassification){
				MobileDevicePolicyClientClassification classification = (MobileDevicePolicyClientClassification)childObj;
				
				/** attribute: operation */
				classification.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <mobile-device-policy>.<client-classification>.<mac> */
				oDebug.debug("/configuration/mobile-device-policy[@name='"+mobileDevicePolicyObj.getName()+"']/client-classification",
						"mac", GenerateXMLDebug.SET_OPERATION,
						devicePolicyImpl.getDevicePolicyGuiName(), devicePolicyImpl.getDevicePolicyName());
				classification.setMac(CLICommonFunc.getAhOnlyAct(devicePolicyImpl.isClassificationMac()));
				
				/** element: <mobile-device-policy>.<client-classification>.<os> */
				oDebug.debug("/configuration/mobile-device-policy[@name='"+mobileDevicePolicyObj.getName()+"']/client-classification",
						"os", GenerateXMLDebug.SET_OPERATION,
						devicePolicyImpl.getDevicePolicyGuiName(), devicePolicyImpl.getDevicePolicyName());
				classification.setOs(CLICommonFunc.getAhOnlyAct(devicePolicyImpl.isClassificationOs()));
				
				/** element: <mobile-device-policy>.<client-classification>.<domain> */
				oDebug.debug("/configuration/mobile-device-policy[@name='"+mobileDevicePolicyObj.getName()+"']/client-classification",
						"domain", GenerateXMLDebug.SET_OPERATION,
						devicePolicyImpl.getDevicePolicyGuiName(), devicePolicyImpl.getDevicePolicyName());
				classification.setDomain(CLICommonFunc.getAhOnlyAct(devicePolicyImpl.isClassificationDomain()));
			}
		}
		devicePolicyChildList_1.clear();
	}
	
	private MobileDevicePolicyRule createMobileDevicePolicyRule(int index) throws Exception{
		MobileDevicePolicyRule rule = new MobileDevicePolicyRule();
		
		/** attribute: operation */
		rule.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		rule.setName(devicePolicyImpl.getPolicyRuleId(index));
		
		/** element: <original-user-profile> */
		MdpRuleOriginalUserProfile originalObj = new MdpRuleOriginalUserProfile();
		devicePolicyRuleChildList_1.add(originalObj);
		rule.setOriginalUserProfile(originalObj);
		
		/** element: <before> */
		if(devicePolicyImpl.isConfigBefore(index)){
			MobileDevicePolicyRuleSeq beforeObj = new MobileDevicePolicyRuleSeq();
			devicePolicyRuleChildList_1.add(beforeObj);
			rule.setBefore(beforeObj);
		}
		
		generateUserAttributePolicyRuleLevel_1(index);
		
		return rule;
	}
	
	private void generateUserAttributePolicyRuleLevel_1(int index) throws Exception{
		/**
		 * <mobile-device-policy>.<rule>.<original-user-profile>						MdpRuleOriginalUserProfile
		 * <mobile-device-policy>.<rule>.<before>										MobileDevicePolicyRuleSeq
		 */
		for(Object childObj : devicePolicyRuleChildList_1){
			
			if(childObj instanceof MdpRuleOriginalUserProfile){
				MdpRuleOriginalUserProfile originalObj = (MdpRuleOriginalUserProfile)childObj;
				
				/** attribute: value */
				originalObj.setName(devicePolicyImpl.getOriginalAttribute(index));
				
				/** element: <mobile-device-policy>.<rule>.<original-user-profile>.<device-group> */
				MdpRuleDeviceGroup deviceGoup = new MdpRuleDeviceGroup();
				devicePolicyRuleChildList_2.add(deviceGoup);
				originalObj.setDeviceGroup(deviceGoup);
			}
			
			/** element: <mobile-device-policy>.<rule>.<before> */
			if(childObj instanceof MobileDevicePolicyRuleSeq){
				MobileDevicePolicyRuleSeq beforeObj = (MobileDevicePolicyRuleSeq)childObj;
				
				/** attribute: operation */
				beforeObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				beforeObj.setValue(devicePolicyImpl.getBeforeValue(index));
				
				/** element: <mobile-device-policy>.<rule>.<before>.<rule> */
				Object[][] ruleParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, devicePolicyImpl.getBeforeRuleId(index)}
				};
				beforeObj.setRule((AhInt)CLICommonFunc.createObjectWithName(AhInt.class, ruleParm));
			}
		}
		devicePolicyRuleChildList_1.clear();
		generateUserAttributePolicyRuleLevel_2(index);
	}
	
	private void generateUserAttributePolicyRuleLevel_2(int index) throws Exception{
		/**
		 * <mobile-device-policy>.<rule>.<original-attribute>.<device-group>						MdpRuleDeviceGroup
		 */
		for(Object childObj : devicePolicyRuleChildList_2){
			
			if(childObj instanceof MdpRuleDeviceGroup){
				MdpRuleDeviceGroup deviceGroup = (MdpRuleDeviceGroup)childObj;
				
				/** attribute: name */
				deviceGroup.setName(devicePolicyImpl.getDeviceGroupName(index));
				
				/** element: <mobile-device-policy>.<rule>.<original-attribute>.<device-group>.<reassigned-user-profile-attr> */
				Object[][] mapAttrParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, devicePolicyImpl.getMappedAttribute(index)}
				};
				deviceGroup.setReassignedUserProfileAttr((MdpRuleReassignedUserProfileAttr)CLICommonFunc.createObjectWithName(
						MdpRuleReassignedUserProfileAttr.class, mapAttrParm));
				
			}
		}
		devicePolicyRuleChildList_2.clear();
	}
}
