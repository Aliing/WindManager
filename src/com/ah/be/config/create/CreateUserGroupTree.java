package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.source.UserGroupProfileInt;
import com.ah.xml.be.config.AhOnlyAct;
import com.ah.xml.be.config.UserGroupObj;

/**
 * @author zhang
 * @version 2009-2-17 14:37:42
 */

public class CreateUserGroupTree {
	
	private UserGroupProfileInt userGroupImpl;
	private UserGroupObj userGroupObj;
	
	private List<Object> userGroupChildList_1 = new ArrayList<Object>();
	private List<Object> userGroupChildList_2 = new ArrayList<Object>();

	public CreateUserGroupTree(UserGroupProfileInt userGroupImpl) throws Exception{
		this.userGroupImpl = userGroupImpl;
		
		/** element: <user-group> */
		this.userGroupObj = new UserGroupObj();
		
		generateUserGroupLevel_1();
	}
	
	public UserGroupObj getUserGroupObj(){
		return this.userGroupObj;
	}
	
	private void generateUserGroupLevel_1() throws Exception{
		/**
		 * <user-group>			UserGroupObj
		 */
		
		/** attribute: name */
		userGroupObj.setName(userGroupImpl.getUserGroupName());
		
		/** attribute: operation */
		userGroupObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <user-group>.<cr> */
		userGroupObj.setCr("");
		
		/** element: <user-group>.<user-attribute> */
		if(userGroupImpl.isConfigUserGroupAttribute()){
			Object[][] userAttributeParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, userGroupImpl.getUserGroupAttribute()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			userGroupObj.setUserAttribute((UserGroupObj.UserAttribute)CLICommonFunc.createObjectWithName(
					UserGroupObj.UserAttribute.class, userAttributeParm)
			);
		}
		
		/** element: <user-group>.<vlan-id> */
		if(userGroupImpl.isConfigUserGroupVlan()){
			Object[][] vlanParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, userGroupImpl.getUserGroupVlan()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			userGroupObj.setVlanId(
					(UserGroupObj.VlanId)CLICommonFunc.createObjectWithName(UserGroupObj.VlanId.class, vlanParm)
			);
		}
		
		/** element: <user-group>.<reauth-interval> */
		Object[][] reauthParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, userGroupImpl.getUserGroupReauthInterval()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		userGroupObj.setReauthInterval(
				(UserGroupObj.ReauthInterval)CLICommonFunc.createObjectWithName(UserGroupObj.ReauthInterval.class, reauthParm)
		);
		
		/** element: <user-group>.<cache-mode> */
		UserGroupObj.CacheMode cacheModeObj = new UserGroupObj.CacheMode();
		userGroupChildList_1.add(cacheModeObj);
		userGroupObj.setCacheMode(cacheModeObj);
		
		/** element: <user-group>.<voice-device> */
		userGroupObj.setVoiceDevice(CLICommonFunc.getAhOnlyAct(userGroupImpl.isVoiceDevice()));
		
		if(userGroupImpl.isPskGroup()){
			
			/** element: <user-group>.<psk-generation-method> */
			UserGroupObj.PskGenerationMethod pskMethod = new UserGroupObj.PskGenerationMethod();
			userGroupChildList_1.add(pskMethod);
			userGroupObj.setPskGenerationMethod(pskMethod);
			
			/** element: <user-group>.<start-time> */
			if(userGroupImpl.isConfigPskStartTime()){
				userGroupObj.setStartTime(CLICommonFunc.createAhStringActObj(
						userGroupImpl.getPskGroupStartTime(), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <user-group>.<expired-time> */
			if(userGroupImpl.isConfigPskExpiredTime()){
				userGroupObj.setExpiredTime(CLICommonFunc.createAhStringActObj(
						userGroupImpl.getPskGroupExpiredTime(), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <user-group>.<psk-format> */
			UserGroupObj.PskFormat pskFormatObj = new UserGroupObj.PskFormat();
			userGroupChildList_1.add(pskFormatObj);
			userGroupObj.setPskFormat(pskFormatObj);
			
			/** element: <user-group>.<password-generation-method> */
			UserGroupObj.PasswordGenerationMethod passwordGenerationMethodObj = new UserGroupObj.PasswordGenerationMethod();
			userGroupChildList_1.add(passwordGenerationMethodObj);
			userGroupObj.setPasswordGenerationMethod(passwordGenerationMethodObj);
			
			/** element: <user-group>.<auto-generation> */
			if(userGroupImpl.isPskGroupAuto()){
				UserGroupObj.AutoGeneration autoGenerationObj = new UserGroupObj.AutoGeneration();
				userGroupChildList_1.add(autoGenerationObj);
				userGroupObj.setAutoGeneration(autoGenerationObj);
			}
		}
		
		generateUserGroupLevel_2();
	}
	
	private void generateUserGroupLevel_2() throws Exception{
		/**
		 * <user-group>.<cache-mode>								UserGroupObj.CacheMode
		 * <user-group>.<psk-generation-method>						UserGroupObj.PskGenerationMethod
		 * <user-group>.<psk-format>								UserGroupObj.PskFormat
		 * <user-group>.<auto-generation>							UserGroupObj.AutoGeneration
		 * <user-group>.<password-generation-method>				UserGroupObj.PasswordGenerationMethod
		 */
		for(Object childObj : userGroupChildList_1){
			
			/** element: <user-group>.<cache-mode> */
			if(childObj instanceof UserGroupObj.CacheMode){
				UserGroupObj.CacheMode cacheModeObj = (UserGroupObj.CacheMode)childObj;
				
				/** attribute: value */
				cacheModeObj.setValue(userGroupImpl.getCacheModeValue());
				
				/** attribute: operation */
				cacheModeObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <user-group>.<psk-generation-method> */
			if(childObj instanceof UserGroupObj.PskGenerationMethod){
				UserGroupObj.PskGenerationMethod pskMethod = (UserGroupObj.PskGenerationMethod)childObj;
				
				/** element: <user-group>.<psk-generation-method>.<password-only> */
				if(userGroupImpl.isPskPasswordOnly()){
					pskMethod.setPasswordOnly(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <user-group>.<psk-generation-method>.<username-and-password> */
				if(userGroupImpl.isPskUserAndPassword()){
					UserGroupObj.PskGenerationMethod.UsernameAndPassword usernameAndPasswordObj = new UserGroupObj.PskGenerationMethod.UsernameAndPassword();
					userGroupChildList_2.add(usernameAndPasswordObj);
					pskMethod.setUsernameAndPassword(usernameAndPasswordObj);
				}
			}
			
			/** element: <user-group>.<psk-format> */
			if(childObj instanceof UserGroupObj.PskFormat){
				UserGroupObj.PskFormat pskFormatObj = (UserGroupObj.PskFormat)childObj;
				
				/** element: <user-group>.<psk-format>.<character-pattern> */
				UserGroupObj.PskFormat.CharacterPattern characterPatternObj = new UserGroupObj.PskFormat.CharacterPattern();
				userGroupChildList_2.add(characterPatternObj);
				pskFormatObj.setCharacterPattern(characterPatternObj);
				
				/** element: <user-group>.<psk-format>.<combo-pattern> */
				UserGroupObj.PskFormat.ComboPattern comboPatternObj = new UserGroupObj.PskFormat.ComboPattern();
				userGroupChildList_2.add(comboPatternObj);
				pskFormatObj.setComboPattern(comboPatternObj);
			}
			
			/** element: <user-group>.<auto-generation> */
			if(childObj instanceof UserGroupObj.AutoGeneration){
				UserGroupObj.AutoGeneration autoGenerationObj = (UserGroupObj.AutoGeneration)childObj;
				
				/** element: <user-group>.<auto-generation>.<prefix> */
				autoGenerationObj.setPrefix(CLICommonFunc.createAhStringActObj(
						userGroupImpl.getPskUserPrefix(), CLICommonFunc.getYesDefault()));
				
				/** element: <user-group>.<auto-generation>.<shared-secret> */
				if(userGroupImpl.isConfigPskUserSecret()){
					autoGenerationObj.setSharedSecret(CLICommonFunc.createAhEncryptedStringAct(
							userGroupImpl.getPskUserSecret(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <user-group>.<auto-generation>.<location> */
				if(userGroupImpl.isConfigPskUserLocation()){
					autoGenerationObj.setLocation(CLICommonFunc.createAhStringActObj(
							userGroupImpl.getPskUserLocation(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <user-group>.<auto-generation>.<password-length> */
				Object[][] passLengthParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, userGroupImpl.getPskPasswordLength()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				autoGenerationObj.setPasswordLength((UserGroupObj.AutoGeneration.PasswordLength)
						CLICommonFunc.createObjectWithName(UserGroupObj.AutoGeneration.PasswordLength.class, passLengthParm));
				
				/** element: <user-group>.<auto-generation>.<schedule> */
				if(userGroupImpl.isConfigPskSchedule()){
					autoGenerationObj.setSchedule(CLICommonFunc.createAhNameActObj(
							userGroupImpl.getPskSchedule(), CLICommonFunc.getYesDefault()));
				}
				
//				/** element: <user-group>.<auto-generation>.<index-range> */
//				if(userGroupImpl.isConfigIndexRange()){
//					autoGenerationObj.setIndexRange(CLICommonFunc.createAhStringActQuoteProhibited(
//							userGroupImpl.getAutoPskIndexRange(), CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault()));
//				}
//				
//				/** element: <user-group>.<auto-generation>.<revoke-user> */
//				for(int i=0; i<userGroupImpl.getRevokeUserSize(); i++){
//					autoGenerationObj.getRevokeUser().add(
//							CLICommonFunc.createAhNameActValueQuoteProhibited(userGroupImpl.getRevokeUserValue(i), CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
//					);
//				}
			}
			
			/** element: <user-group>.<password-generation-method> */
			if(childObj instanceof UserGroupObj.PasswordGenerationMethod){
				UserGroupObj.PasswordGenerationMethod passwordMethodObj = (UserGroupObj.PasswordGenerationMethod)childObj;
				
				/** attribute: value */
				passwordMethodObj.setValue(userGroupImpl.getPasswordMethod());
				
				/** attribute: operation */
				passwordMethodObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			}
		}
		userGroupChildList_1.clear();
		generateUserGroupLevel_3();
	}
	
	private void generateUserGroupLevel_3(){
		/**
		 * <user-group>.<psk-generation-method>.<username-and-password>			UserGroupObj.PskGenerationMethod.UsernameAndPassword
		 * <user-group>.<psk-format>.<character-pattern>						UserGroupObj.PskFormat.CharacterPattern
		 * <user-group>.<psk-format>.<combo-pattern>							UserGroupObj.PskFormat.ComboPattern
		 */
		for(Object childObj : userGroupChildList_2){
			
			/** element: <user-group>.<psk-generation-method>.<username-and-password> */
			if(childObj instanceof UserGroupObj.PskGenerationMethod.UsernameAndPassword){
				UserGroupObj.PskGenerationMethod.UsernameAndPassword userAndPassword = (UserGroupObj.PskGenerationMethod.UsernameAndPassword)childObj;
				
				/** element: <user-group>.<psk-generation-method>.<username-and-password>.<cr> */
				userAndPassword.setCr(CLICommonFunc.createAhActShow(userGroupImpl.isPskUserAndPassword()));
				
				/** element: <user-group>.<psk-generation-method>.<username-and-password>.<concatenated-characters> */
				if(userGroupImpl.isConfigConcateCharacters()){
					userAndPassword.setConcatenatedCharacters(CLICommonFunc.createAhStringActObj(
							userGroupImpl.getConcateCharacters(), CLICommonFunc.getYesDefault())
					);
				}
			}
			
			/** element: <user-group>.<psk-format>.<character-pattern> */
			if(childObj instanceof UserGroupObj.PskFormat.CharacterPattern){
				UserGroupObj.PskFormat.CharacterPattern characterPatternObj = (UserGroupObj.PskFormat.CharacterPattern)childObj;
				
				/** element: <user-group>.<psk-format>.<character-pattern>.<digits> */
				characterPatternObj.setDigits(CLICommonFunc.getAhOnlyAct(userGroupImpl.isDigits()));
				
				/** element: <user-group>.<psk-format>.<character-pattern>.<letters> */
				characterPatternObj.setLetters(CLICommonFunc.getAhOnlyAct(userGroupImpl.isLetters()));
				
				/** element: <user-group>.<psk-format>.<character-pattern>.<special-characters> */
				characterPatternObj.setSpecialCharacters(CLICommonFunc.getAhOnlyAct(userGroupImpl.isSpecialCharacters()));
			}
			
			/** element: <user-group>.<psk-format>.<combo-pattern> */
			if(childObj instanceof UserGroupObj.PskFormat.ComboPattern){
				UserGroupObj.PskFormat.ComboPattern comboPatternObj = (UserGroupObj.PskFormat.ComboPattern)childObj;
				
				/** attribute: value */
				comboPatternObj.setValue(userGroupImpl.getComboPatternValue());
				
				/** attribute: operation */
				comboPatternObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			}
			
		}
		userGroupChildList_2.clear();
	}
}
