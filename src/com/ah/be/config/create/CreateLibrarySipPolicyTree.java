package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.LibrarySipPolicyInt;
import com.ah.xml.be.config.AhInt;
import com.ah.xml.be.config.LibrarySipPolicyAction;
import com.ah.xml.be.config.LibrarySipPolicyDefault;
import com.ah.xml.be.config.LibrarySipPolicyField;
import com.ah.xml.be.config.LibrarySipPolicyId;
import com.ah.xml.be.config.LibrarySipPolicyMatch;
import com.ah.xml.be.config.LibrarySipPolicyObj;
import com.ah.xml.be.config.LibrarySipPolicySeq;
import com.ah.xml.be.config.LibrarySipPolicyUserGroup;

public class CreateLibrarySipPolicyTree {
	
	private GenerateXMLDebug oDebug;
	private LibrarySipPolicyInt libSipImpl;
	
	private LibrarySipPolicyObj libSipObj;
	
	private List<Object> sipPolicyChildLevel_1 = new ArrayList<Object>();
	private List<Object> sipPolicyChildLevel_2 = new ArrayList<Object>();
	private List<Object> sipPolicyChildLevel_3 = new ArrayList<Object>();
	
	private List<Object> idChildLevel_1 = new ArrayList<Object>();
	
	private List<Object> userGroupChildLevel_1 = new ArrayList<Object>();
	private List<Object> userGroupChildLevel_2 = new ArrayList<Object>();

	public CreateLibrarySipPolicyTree(LibrarySipPolicyInt libSipImpl, GenerateXMLDebug oDebug){
		this.libSipImpl = libSipImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		libSipObj = new LibrarySipPolicyObj();
		generateSipPolicyLevel_1();
	}
	
	public LibrarySipPolicyObj getLibrarySipPolicyObj(){
		return this.libSipObj;
	}
	
	private void generateSipPolicyLevel_1() throws Exception{
		/**
		 * <library-sip-policy>			LibrarySipPolicyObj
		 */
		
		/** attribute: name */
		oDebug.debug("/configuration", 
				"library-sip-policy", GenerateXMLDebug.SET_NAME,
				libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
		libSipObj.setName(libSipImpl.getIpPolicyName());
		
		/** attribute: operation */
		libSipObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <library-sip-policy>.<default> */
		LibrarySipPolicyDefault defaultObj = new LibrarySipPolicyDefault();
		sipPolicyChildLevel_1.add(defaultObj);
		libSipObj.setDefault(defaultObj);
		
		/** element: <library-sip-policy>.<id> */
		for(int i=0; i<libSipImpl.getLibrarySipPolicySize(); i++){
			libSipObj.getId().add(this.createLibrarySipPolicyId(i));
		}
		
		generateSipPolicyLevel_2();
	}
	
	private void generateSipPolicyLevel_2(){
		/**
		 * <library-sip-policy>.<default>					LibrarySipPolicyDefault
		 */
		for(Object childObj : sipPolicyChildLevel_1){
			
			/** element: <library-sip-policy>.<default> */
			if(childObj instanceof LibrarySipPolicyDefault){
				LibrarySipPolicyDefault defaultObj = (LibrarySipPolicyDefault)childObj;
				
				/** attribute: operation */
				defaultObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <library-sip-policy>.<default>.<user-group> */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/default", 
						"user-group", GenerateXMLDebug.SET_VALUE,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				LibrarySipPolicyUserGroup defGroupObj = new LibrarySipPolicyUserGroup();
				sipPolicyChildLevel_2.add(defGroupObj);
				defaultObj.setUserGroup(defGroupObj);
			}
		}
		sipPolicyChildLevel_1.clear();
		
		generateSipPolicyLevel_3();
	}
	
	private void generateSipPolicyLevel_3(){
		/**
		 * <library-sip-policy>.<default>.<user-group>					LibrarySipPolicyUserGroup
		 */
		for(Object childObj : sipPolicyChildLevel_2){
			
			/** element: <library-sip-policy>.<default>.<user-group> */
			if(childObj instanceof LibrarySipPolicyUserGroup){
				LibrarySipPolicyUserGroup userGroupObj = (LibrarySipPolicyUserGroup)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/default", 
						"user-group", GenerateXMLDebug.SET_VALUE,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				userGroupObj.setValue(libSipImpl.getDefaultGroupName());
				
				/** element: <library-sip-policy>.<default>.<user-group>.<action> */
				LibrarySipPolicyAction actionObj = new LibrarySipPolicyAction();
				sipPolicyChildLevel_3.add(actionObj);
				userGroupObj.setAction(actionObj);
			}
		}
		sipPolicyChildLevel_2.clear();
		generateSipPolicyLevel_4();
	}
	
	private void generateSipPolicyLevel_4(){
		/**
		 * <library-sip-policy>.<default>.<user-group>.<action>						LibrarySipPolicyAction
		 */
		for(Object childObj : sipPolicyChildLevel_3){
			
			if(childObj instanceof LibrarySipPolicyAction){
				LibrarySipPolicyAction actionObj = (LibrarySipPolicyAction)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/default/user-group", 
						"action", GenerateXMLDebug.SET_VALUE,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				actionObj.setValue(libSipImpl.getDefUserGroupAction());
				
				/** element: <library-sip-policy>.<default>.<user-group>.<action>.<additional-display-message> */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/default/user-group/action", 
						"additional-display-message", GenerateXMLDebug.SET_VALUE,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				actionObj.setAdditionalDisplayMessage(
						CLICommonFunc.createAhStringObj(libSipImpl.getDefUserGroupMessage()));
			}
		}
		sipPolicyChildLevel_3.clear();
	}
	
	private LibrarySipPolicyId createLibrarySipPolicyId(int index) throws Exception{
		LibrarySipPolicyId sipPolicyObj = new LibrarySipPolicyId();
		
		/** attribute: operation */
		sipPolicyObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		sipPolicyObj.setName(libSipImpl.getLibrarySipPolicyId(index));
		
		/** element: <library-sip-policy>.<id>.<before> */
		if(libSipImpl.isConfigBefore(index)){
			LibrarySipPolicySeq beforeObj = new LibrarySipPolicySeq();
			idChildLevel_1.add(beforeObj);
			sipPolicyObj.setBefore(beforeObj);
		}
		
		/** element: <library-sip-policy>.<id>.<field> */
		LibrarySipPolicyField fieldObj = new LibrarySipPolicyField();
		idChildLevel_1.add(fieldObj);
		sipPolicyObj.setField(fieldObj);
		
		generateSipPolicyIdLevel_1(index);
		return sipPolicyObj;
	}
	
	private void generateSipPolicyIdLevel_1(int index) throws Exception{
		/**
		 * <library-sip-policy>.<id>.<before>				LibrarySipPolicySeq
		 * <library-sip-policy>.<id>.<field>				LibrarySipPolicyField
		 */
		for(Object childObj : idChildLevel_1){
			
			/** element: <library-sip-policy>.<id>.<before> */
			if(childObj instanceof LibrarySipPolicySeq){
				LibrarySipPolicySeq beforeObj = (LibrarySipPolicySeq)childObj;
				
				/** attribute: operation */
				beforeObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				/** attribute: quoteProhibited */
				beforeObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/id[@name='"+libSipImpl.getLibrarySipPolicyId(index)+"']", 
						"before", GenerateXMLDebug.SET_VALUE,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				beforeObj.setValue(libSipImpl.getBeforeValue(index));
				
				/** element: <library-sip-policy>.<id>.<before>.<id> */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/id[@name='"+libSipImpl.getLibrarySipPolicyId(index)+"']/before", 
						"id", GenerateXMLDebug.SET_VALUE,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				Object[][] idParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, libSipImpl.getPolicyBeforeIdValue(index) }
				};
				beforeObj.setId(
						(AhInt)CLICommonFunc.createObjectWithName(AhInt.class, idParm)
				);
			}
			
			/** element: <library-sip-policy>.<id>.<field> */
			if(childObj instanceof LibrarySipPolicyField){
				LibrarySipPolicyField fieldObj = (LibrarySipPolicyField)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/id[@name='"+libSipImpl.getLibrarySipPolicyId(index)+"']", 
						"field", GenerateXMLDebug.SET_VALUE,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				fieldObj.setValue(libSipImpl.getFieldValue(index));
				
				/** element: <library-sip-policy>.<id>.<field>.<contains> */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/id[@name='"+libSipImpl.getLibrarySipPolicyId(index)+"']/field", 
						"contains", GenerateXMLDebug.CONFIG_ELEMENT,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				if(libSipImpl.isConfigContains(index)){
					fieldObj.setContains(this.createLibrarySipPolicyMatch(index));
				}
				
				/** element: <library-sip-policy>.<id>.<field>.<differs-from> */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/id[@name='"+libSipImpl.getLibrarySipPolicyId(index)+"']/field", 
						"differs-from", GenerateXMLDebug.CONFIG_ELEMENT,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				if(libSipImpl.isConfigDiffersFrom(index)){
					fieldObj.setDiffersFrom(this.createLibrarySipPolicyMatch(index));
				}
				
				/** element: <library-sip-policy>.<id>.<field>.<matches> */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/id[@name='"+libSipImpl.getLibrarySipPolicyId(index)+"']/field", 
						"matches", GenerateXMLDebug.CONFIG_ELEMENT,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				if(libSipImpl.isConfigMatches(index)){
					fieldObj.setMatches(this.createLibrarySipPolicyMatch(index));
				}
				
				/** element: <library-sip-policy>.<id>.<field>.<occurs-after> */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/id[@name='"+libSipImpl.getLibrarySipPolicyId(index)+"']/field", 
						"occurs-after", GenerateXMLDebug.CONFIG_ELEMENT,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				if(libSipImpl.isConfigOccursAfter(index)){
					fieldObj.setOccursAfter(this.createLibrarySipPolicyMatch(index));
				}
				
				/** element: <library-sip-policy>.<id>.<field>.<occurs-before> */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/id[@name='"+libSipImpl.getLibrarySipPolicyId(index)+"']/field", 
						"occurs-before", GenerateXMLDebug.CONFIG_ELEMENT,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				if(libSipImpl.isConfigOccursBefore(index)){
					fieldObj.setOccursBefore(this.createLibrarySipPolicyMatch(index));
				}
				
				/** element: <library-sip-policy>.<id>.<field>.<starts-with> */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/id[@name='"+libSipImpl.getLibrarySipPolicyId(index)+"']/field", 
						"starts-with", GenerateXMLDebug.CONFIG_ELEMENT,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				if(libSipImpl.isConfigStartsWith(index)){
					fieldObj.setStartsWith(this.createLibrarySipPolicyMatch(index));
				}
				
				/** element: <library-sip-policy>.<id>.<field>.<equal> */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/id[@name='"+libSipImpl.getLibrarySipPolicyId(index)+"']/field", 
						"equal", GenerateXMLDebug.CONFIG_ELEMENT,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				if(libSipImpl.isConfigEqual(index)){
					fieldObj.setEqual(this.createLibrarySipPolicyMatch(index));
				}
				
				/** element: <library-sip-policy>.<id>.<field>.<greater-than> */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/id[@name='"+libSipImpl.getLibrarySipPolicyId(index)+"']/field", 
						"greater-than", GenerateXMLDebug.CONFIG_ELEMENT,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				if(libSipImpl.isConfigGreaterThan(index)){
					fieldObj.setGreaterThan(this.createLibrarySipPolicyMatch(index));
				}
				
				/** element: <library-sip-policy>.<id>.<field>.<less-than> */
				oDebug.debug("/configuration/library-sip-policy[@name='"+libSipImpl.getIpPolicyName()+"']/id[@name='"+libSipImpl.getLibrarySipPolicyId(index)+"']/field", 
						"less-than", GenerateXMLDebug.CONFIG_ELEMENT,
						libSipImpl.getIpPolicyGuiName(), libSipImpl.getIpPolicyName());
				if(libSipImpl.isConfigLessThan(index)){
					fieldObj.setLessThan(this.createLibrarySipPolicyMatch(index));
				}
				
			}
		}
		idChildLevel_1.clear();
	}
	
	private LibrarySipPolicyMatch createLibrarySipPolicyMatch(int index){
		LibrarySipPolicyMatch actionObj = new LibrarySipPolicyMatch();
		
		/** attribute; value */
		actionObj.setValue(libSipImpl.getLibrarySipPolicyValue(index));
		
		/** element: <user-group> */
		LibrarySipPolicyUserGroup userGroupObj = new LibrarySipPolicyUserGroup();
		userGroupChildLevel_1.add(userGroupObj);
		actionObj.setUserGroup(userGroupObj);
		
		createLibrarySipPolicyMatch_1(index);
		
		return actionObj;
	}
	
	private void createLibrarySipPolicyMatch_1(int index){
		/**
		 * <user-group>										LibrarySipPolicyUserGroup
		 */
		for(Object childObj : userGroupChildLevel_1){
			
			/** element: <user-group> */
			if(childObj instanceof LibrarySipPolicyUserGroup){
				LibrarySipPolicyUserGroup userGroupObj = (LibrarySipPolicyUserGroup)childObj;
				
				/** attribute: value */
				userGroupObj.setValue(libSipImpl.getLibrarySipPolicyGroup(index));
				
				/** element: <user-group>.<action> */
				LibrarySipPolicyAction actionObj = new LibrarySipPolicyAction();
				userGroupChildLevel_2.add(actionObj);
				userGroupObj.setAction(actionObj);
			}
		}
		userGroupChildLevel_1.clear();
		
		createLibrarySipPolicyMatch_2(index);
	}
	
	private void createLibrarySipPolicyMatch_2(int index){
		/**
		 * <user-group>.<action>					LibrarySipPolicyAction
		 */
		for(Object childObj : userGroupChildLevel_2){
			
			/** element: <user-group>.<action> */
			if(childObj instanceof LibrarySipPolicyAction){
				LibrarySipPolicyAction actionObj = (LibrarySipPolicyAction)childObj;
				
				/** attribute: value */
				actionObj.setValue(libSipImpl.getUserGroupAction(index));
				
				/** element: <user-group>.<action>.<additional-display-message> */
				actionObj.setAdditionalDisplayMessage(
						CLICommonFunc.createAhStringObj(libSipImpl.getUserGroupMessage(index)));
			}
		}
		userGroupChildLevel_2.clear();
	}
	
}
