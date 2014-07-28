package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.RadiusBindGroupInt;
import com.ah.xml.be.config.AaaObj;

/**
 * @author zhang
 * @version 2009-2-25 15:16:32
 */

public class CreateRadiusBindGroupTree {
	
	private RadiusBindGroupInt radiusBindGroupImpl;
	private AaaObj aaaObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> aaaChildList_1 = new ArrayList<Object>();
	private List<Object> aaaChildList_2 = new ArrayList<Object>();

	public CreateRadiusBindGroupTree(RadiusBindGroupInt radiusBindGroupImpl, GenerateXMLDebug oDebug){
		this.radiusBindGroupImpl = radiusBindGroupImpl;
		this.oDebug = oDebug;
	}
	
	public void generate(){
		
		if(radiusBindGroupImpl.isExistLocalUserGroup()){
			aaaObj = new AaaObj();
			generateAAALevel_1();
		}
	}
	
	public AaaObj getAaaObj(){
		return this.aaaObj;
	}
	
	private void generateAAALevel_1(){
		/**
		 * <aaa>			AaaObj
		 */
		
		/** element: <aaa>.<radius-server> */
		AaaObj.RadiusServer radiusServerObj = new AaaObj.RadiusServer();
		aaaChildList_1.add(radiusServerObj);
		aaaObj.setRadiusServer(radiusServerObj);
		
		generateAAALevel_2();
	}
	
	private void generateAAALevel_2(){
		/**
		 * <aaa>.<radius-server>				AaaObj.RadiusServer
		 */
		for(Object childObj : aaaChildList_1){
			
			/** element: <aaa>.<radius-server> */
			if(childObj instanceof AaaObj.RadiusServer){
				AaaObj.RadiusServer radiusServerObj = (AaaObj.RadiusServer)childObj;
				
				/** element: <aaa>.<radius-server>.<local> */
				AaaObj.RadiusServer.Local localObj = new AaaObj.RadiusServer.Local();
				aaaChildList_2.add(localObj);
				radiusServerObj.setLocal(localObj);
			}
		}
		aaaChildList_1.clear();
		generateAAALevel_3();
	}
	
	private void generateAAALevel_3(){
		/**
		 * <aaa>.<radius-server>.<local>				AaaObj.RadiusServer.Local
		 */
		for(Object childObj : aaaChildList_2){
			
			/** element: <aaa>.<radius-server>.<local> */
			if(childObj instanceof AaaObj.RadiusServer.Local){
				AaaObj.RadiusServer.Local localObj = (AaaObj.RadiusServer.Local)childObj;
				
				/** element: <aaa>.<radius-server>.<local>.<user-group> */
				for(int i=0; i<radiusBindGroupImpl.getUserGroupSize(); i++){
					localObj.getUserGroup().add(this.createUserGroup(radiusBindGroupImpl.getUserGroupName(i)));
				}
			}
		}
		aaaChildList_2.clear();
	}
	
	private AaaObj.RadiusServer.Local.UserGroup createUserGroup(String groupName){
		AaaObj.RadiusServer.Local.UserGroup userGroupObj = new AaaObj.RadiusServer.Local.UserGroup();
		
		/** attribute: name */
		oDebug.debug("/configuration/aaa/radius-server/local", 
				"user-group", GenerateXMLDebug.SET_NAME,
				radiusBindGroupImpl.getRadiusGuiName(), radiusBindGroupImpl.getRadiusName());
		userGroupObj.setName(groupName);
		
		/** attribute: operation */
		userGroupObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		return userGroupObj;
	}
}
