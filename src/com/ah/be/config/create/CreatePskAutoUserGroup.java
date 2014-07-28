package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.PskAutoUserGroupInt;
import com.ah.xml.be.config.AutoPskUserGroupAutoGeneration;
import com.ah.xml.be.config.AutoPskUserGroupObj;
import com.ah.xml.be.config.UserGroupBulkNumber;

/**
 * @author zhang
 * @version 2009-3-12 11:20:21
 */

public class CreatePskAutoUserGroup {
	
	private PskAutoUserGroupInt pskAutoImpl;
	private AutoPskUserGroupObj autoGroupObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> pskAutoGroupChildList_1 = new ArrayList<Object>();
	private List<Object> pskAutoGroupChildList_2 = new ArrayList<Object>();

	public CreatePskAutoUserGroup(PskAutoUserGroupInt pskAutoImpl, GenerateXMLDebug oDebug){
		this.pskAutoImpl = pskAutoImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws CreateXMLException{
		autoGroupObj = new  AutoPskUserGroupObj();
		generateAutoGroupLevel_1();
	}
	
	public AutoPskUserGroupObj getAutoPskUserGroupObj(){
		return this.autoGroupObj;
	}
	
	private void generateAutoGroupLevel_1() throws CreateXMLException{
		/**
		 * <auto-psk-user-group>.<auto-generation>			AutoUserGroupObj.AutoGeneration
		 */
		
		/** attribute: name */
		oDebug.debug("/configuration", 
				"auto-generation", GenerateXMLDebug.SET_NAME,
				pskAutoImpl.getUserGroupGuiName(), pskAutoImpl.getUserGroupName());
		autoGroupObj.setName(pskAutoImpl.getUserGroupName());
		
		/** attribute: operation */
		autoGroupObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
		
		/** element: <auto-psk-user-group>.<auto-generation> */
		oDebug.debug("/configuration/auto-psk-user-group", 
				"auto-generation", GenerateXMLDebug.CONFIG_ELEMENT,
				pskAutoImpl.getUserGroupGuiName(), pskAutoImpl.getUserGroupName());
		if(pskAutoImpl.isConfigAutoGeneration()){
			AutoPskUserGroupAutoGeneration autoGenObj = new AutoPskUserGroupAutoGeneration();
			pskAutoGroupChildList_1.add(autoGenObj);
			autoGroupObj.setAutoGeneration(autoGenObj);
		}

		generateAutoGroupLevel_2();
	}
	
	private void generateAutoGroupLevel_2() throws CreateXMLException{
		/**
		 * <auto-psk-user-group>.<auto-generation>			AutoPskUserGroupAutoGeneration
		 */	
		
		for(Object childObj : pskAutoGroupChildList_1){
			
			/** element: <auto-psk-user-group>.<auto-generation> */
			if(childObj instanceof AutoPskUserGroupAutoGeneration){
				AutoPskUserGroupAutoGeneration autoObj = (AutoPskUserGroupAutoGeneration)childObj;
	
				/** element: <auto-psk-user-group>.<auto-generation>.<old-index-range> */
				oDebug.debug("/configuration/auto-psk-user-group/auto-generation", 
						"index-range", GenerateXMLDebug.CONFIG_ELEMENT,
						pskAutoImpl.getUserGroupGuiName(), pskAutoImpl.getUserGroupName());
				if(pskAutoImpl.isConfigOldIndexRange()){
					
					oDebug.debug("/configuration/auto-psk-user-group/auto-generation", 
							"old-index-range", GenerateXMLDebug.SET_VALUE,
							pskAutoImpl.getUserGroupGuiName(), pskAutoImpl.getUserGroupName());
					autoObj.setOldIndexRange(CLICommonFunc.createAhStringActQuoteProhibited(
							pskAutoImpl.getIndexRangeValue(), CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <auto-psk-user-group>.<auto-generation>.<revoke-user> */
				oDebug.debug("/configuration/auto-psk-user-group/auto-generation", 
						"revoke-user", GenerateXMLDebug.CONFIG_ELEMENT,
						pskAutoImpl.getUserGroupGuiName(), pskAutoImpl.getUserGroupName());
				for(int index=0; index<pskAutoImpl.getRevokeUserSize(); index++){
					
					oDebug.debug("/configuration/auto-psk-user-group/auto-generation", 
							"revoke-user", GenerateXMLDebug.SET_NAME,
							pskAutoImpl.getUserGroupGuiName(), pskAutoImpl.getUserGroupName());
					autoObj.getRevokeUser().add(
							CLICommonFunc.createAhNameActValueQuoteProhibited(
									pskAutoImpl.getRevokeUserName(index), CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
					);
				}
				
				/** element: <auto-psk-user-group>.<auto-generation>.<index-range> */
				oDebug.debug("/configuration/auto-psk-user-group/auto-generation", 
						"index-range", GenerateXMLDebug.CONFIG_ELEMENT,
						pskAutoImpl.getUserGroupGuiName(), pskAutoImpl.getUserGroupName());
				for(int index=0; index<pskAutoImpl.getAutoUserSize(); index++){
					
					oDebug.debug("/configuration/auto-psk-user-group/auto-generation", 
							"index-range", GenerateXMLDebug.SET_NAME,
							pskAutoImpl.getUserGroupGuiName(), pskAutoImpl.getUserGroupName());
					autoObj.getIndexRange().add(
							CLICommonFunc.createAhNameActValueQuoteProhibited(pskAutoImpl.getAutoUserName(index), true, true));
				}
				
				/** element: <auto-psk-user-group>.<auto-generation>.<bulk-number> */
				oDebug.debug("/configuration/auto-psk-user-group/auto-generation", 
						"bulk-number", GenerateXMLDebug.CONFIG_ELEMENT,
						pskAutoImpl.getUserGroupGuiName(), pskAutoImpl.getUserGroupName());
				if(pskAutoImpl.isConfigPpskBulk()){
					UserGroupBulkNumber bulkObj = new UserGroupBulkNumber();
					pskAutoGroupChildList_2.add(bulkObj);
					autoObj.setBulkNumber(bulkObj);
				}
				
			}
		}
		pskAutoGroupChildList_1.clear();
		generateAutoGroupLevel_3();
	}
	
	private void generateAutoGroupLevel_3() throws CreateXMLException{
		/**
		 * <auto-psk-user-group>.<auto-generation>.<bulk-number>				UserGroupBulkNumber
		 */
		for(Object childObj : pskAutoGroupChildList_2){
			
			/** element: <auto-psk-user-group>.<auto-generation>.<bulk-number> */
			if(childObj instanceof UserGroupBulkNumber){
				UserGroupBulkNumber bulkObj = (UserGroupBulkNumber)childObj;
				
				/** attribute: operation */
				bulkObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/auto-psk-user-group/auto-generation", 
						"bulk-number", GenerateXMLDebug.SET_VALUE,
						pskAutoImpl.getUserGroupGuiName(), pskAutoImpl.getUserGroupName());
				bulkObj.setValue(pskAutoImpl.getPpskBulkNumber());
				
				/** element: <auto-psk-user-group>.<auto-generation>.<bulk-number>.<bulk-interval> */
				oDebug.debug("/configuration/auto-psk-user-group/auto-generation/bulk-number", 
						"bulk-interval", GenerateXMLDebug.SET_VALUE,
						pskAutoImpl.getUserGroupGuiName(), pskAutoImpl.getUserGroupName());
				bulkObj.setBulkInterval(CLICommonFunc.createAhStringQuoteProhibited(pskAutoImpl.getPpskBulkInterval(), true));
			}
		}
		pskAutoGroupChildList_2.clear();
	}
}
