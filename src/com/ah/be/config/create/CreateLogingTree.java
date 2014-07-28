package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.LogingProfileInt;
import com.ah.xml.be.config.*;

/**
 * 
 * @author zhang
 *
 */
public class CreateLogingTree {
	
	private LogingProfileInt logingImpl;
	
	private LoggingObj logingObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> logingChildLevel_1 = new ArrayList<Object>();
	
	public CreateLogingTree(LogingProfileInt logingImpl, GenerateXMLDebug oDebug) {
		this.logingImpl = logingImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		if(logingImpl.isConfigureLoging()){
			logingObj = new LoggingObj();
			generateLoggingLevel_1();
		}
	}
	
	public LoggingObj getLoggingObj(){
		return this.logingObj;
	}
	
	private void generateLoggingLevel_1() throws Exception{
		/**
		 * <logging>
		 */
		
		/** attribute: updateTime */
		logingObj.setUpdateTime(logingImpl.getUpdateTime());
		
		/** element: <logging>.<facility> */
		LoggingObj.Facility facilityObj = new LoggingObj.Facility();
		logingChildLevel_1.add(facilityObj);
		logingObj.setFacility(facilityObj);
		
		/** element: <logging>.<server> */
		oDebug.debug("/configuration/logging", 
				"server", GenerateXMLDebug.CONFIG_ELEMENT,
				logingImpl.getLoggingGuiName(), logingImpl.getLoggingName());
		for(int i=0; i<logingImpl.getLoggingServerSize(); i++){
			logingObj.getServer().add(createLoggingServer(i));
		}
		
		generateLoggingLevel_2();
	}
	
	private void generateLoggingLevel_2() throws Exception{
		/**
		 * <logging>.<facility>					LoggingObj.Facility
		 */
		for(Object childObj : logingChildLevel_1){
			
			/** element: <logging>.<facility> */
			if(childObj instanceof LoggingObj.Facility){
				LoggingObj.Facility facilityObj = (LoggingObj.Facility)childObj;
				
				/** attribute: operation */
				facilityObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/logging", 
						"facility", GenerateXMLDebug.SET_VALUE,
						logingImpl.getLoggingGuiName(), logingImpl.getLoggingName());
				facilityObj.setValue(logingImpl.getLoggingFacilityValue());
			}
		}
	}
	
	private LoggingObj.Server createLoggingServer(int index) throws CreateXMLException{
		LoggingObj.Server serverObj = new LoggingObj.Server();
		
		/** attribute: name */
		oDebug.debug("/configuration/logging", 
				"server", GenerateXMLDebug.SET_NAME,
				logingImpl.getLoggingGuiName(), logingImpl.getLoggingName());
		serverObj.setName(logingImpl.getLoggingServerName(index));
		
		/** attribute: operation */
		serverObj.setOperation(
				CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault())
		);
		
		/** element: <logging>.<server>.<level> */
		LoggingLevel levelObj = new LoggingLevel();
		serverObj.setLevel(levelObj);
			//set LoggingLevel
			/** attribute: value */
			oDebug.debug("/configuration/logging/server[@name='"+serverObj.getName()+"']", 
				"level", GenerateXMLDebug.SET_VALUE,
				logingImpl.getLoggingGuiName(), logingImpl.getLoggingName());
			levelObj.setValue(turnServerLevel(logingImpl.getLoggingServerLevel(index)));
			
			/** attribute: operation */
			levelObj.setOperation(
					CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault())
			);
			
		/** element: <logging>.<server>.<via-vpn-tunnel> */
		oDebug.debug("/configuration/logging/server[@name='"+logingImpl.getLoggingServerName(index)+"']",
				"via-vpn-tunnel", GenerateXMLDebug.CONFIG_ELEMENT,
				logingImpl.getLoggingGuiName(), logingImpl.getLoggingName());
		if(logingImpl.isEnableVpnTunnel(index)){
			serverObj.setViaVpnTunnel(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		return serverObj;
	}
	
	private LoggingLevelValue turnServerLevel(String level){
		if(LogingProfileInt.SERVER_LEVEL_ALERT.equals(level)){
			return LoggingLevelValue.ALERT;
		}else if(LogingProfileInt.SERVER_LEVEL_CRITICAL.equals(level)){
			return LoggingLevelValue.CRITICAL;
		}else if(LogingProfileInt.SERVER_LEVEL_DEBUG.equals(level)){
			return LoggingLevelValue.DEBUG;
		}else if(LogingProfileInt.SERVER_LEVEL_EMERGENCY.equals(level)){
			return LoggingLevelValue.EMERGENCY;
		}else if(LogingProfileInt.SERVER_LEVEL_ERROR.equals(level)){
			return LoggingLevelValue.ERROR;
		}else if(LogingProfileInt.SERVER_LEVEL_INFO.equals(level)){
			return LoggingLevelValue.INFO;
		}else if(LogingProfileInt.SERVER_LEVEL_NOTIFICATION.equals(level)){
			return LoggingLevelValue.NOTIFICATION;
		}else if(LogingProfileInt.SERVER_LEVEL_WARNING.equals(level)){
			return LoggingLevelValue.WARNING;
		}else{
			return null;
		}
	}
}
