package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.ServiceProfileInt;
import com.ah.xml.be.config.ProtocolPort;
import com.ah.xml.be.config.ProtocolTimeout;
import com.ah.xml.be.config.ServiceAppId;
import com.ah.xml.be.config.ServiceObj;
import com.ah.xml.be.config.ServiceProtocol;

/**
 * 
 * @author zhang
 *
 */
@Deprecated
public class CreateServiceTree {
	
	private ServiceProfileInt serviceImpl;
	private ServiceObj serviceObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> serviceChildLevel_1 = new ArrayList<Object>();

	public CreateServiceTree(ServiceProfileInt serviceImpl, GenerateXMLDebug oDebug) {
		this.serviceImpl = serviceImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		if(serviceImpl.isConfigServices()){
			serviceObj = new ServiceObj();
			generateServiceLevel_1();
		}
	}
	
	public ServiceObj getServiceObj(){
		return this.serviceObj;
	}
	
	private void generateServiceLevel_1() throws Exception {
		/**
		 * <service>	ServiceObj
		 */
		
		/** attribute: updateTime */
		serviceObj.setUpdateTime(serviceImpl.getUpdateTime());
		
		/** attribute: operation */
		serviceObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		oDebug.debug("/configuration",
				"service", GenerateXMLDebug.SET_NAME,
				serviceImpl.getServiceGuiName(), serviceImpl.getServiceName());
		serviceObj.setName(serviceImpl.getServiceName());
		
		/** elemnet: <service>.<protocol> */
		oDebug.debug("/configuration/service[@name='"+serviceImpl.getServiceName()+"']",
				"protocol", GenerateXMLDebug.CONFIG_ELEMENT,
				serviceImpl.getServiceGuiName(), serviceImpl.getServiceName());
		if(serviceImpl.isConfigServiceProtocol()){
			ServiceProtocol protocolObj = new ServiceProtocol();
			serviceChildLevel_1.add(protocolObj);
			serviceObj.setProtocol(protocolObj);
		}
		
		/** elemnet: <service>.<app-id> */
		oDebug.debug("/configuration/service[@name='"+serviceImpl.getServiceName()+"']",
				"app-id", GenerateXMLDebug.CONFIG_ELEMENT,
				serviceImpl.getServiceGuiName(), serviceImpl.getServiceName());
		if(serviceImpl.isConfigAppid()){
			ServiceAppId appId = new ServiceAppId();
			serviceChildLevel_1.add(appId);
			serviceObj.setAppId(appId);
		}
		
		/** elemnet: <service>.<alg> */
		oDebug.debug("/configuration/service[@name='"+serviceImpl.getServiceName()+"']",
				"alg", GenerateXMLDebug.CONFIG_ELEMENT,
				serviceImpl.getServiceGuiName(), serviceImpl.getServiceName());
		if(serviceImpl.isConfigAlg()){
			ServiceObj.Alg algObj = new ServiceObj.Alg();
			serviceChildLevel_1.add(algObj);
			serviceObj.setAlg(algObj);
		}
		
		generateServiceLevel_2();
	}
	
	private void generateServiceLevel_2() throws Exception {
		/**
		 * <service>.<protocol>			ServiceProtocol
		 * <service>.<alg>				ServiceObj.Alg
		 * <service>.<app-id>			ServiceAppId
		 */
		for(Object childObj : serviceChildLevel_1){
			
			/** element : <service>.<protocol> */
			if(childObj instanceof ServiceProtocol){
				ServiceProtocol protocolObj = (ServiceProtocol)childObj;
				
				
				/** attribute: value */
				oDebug.debug("/configuration/service[@name='"+serviceImpl.getServiceName()+"']",
						"protocol", GenerateXMLDebug.SET_VALUE,
						serviceImpl.getServiceGuiName(), serviceImpl.getServiceName());
				protocolObj.setValue(serviceImpl.getProtocolValue());
				
				/** attribute: operation */
				protocolObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				/** element : <service>.<protocol>.<port> */
				oDebug.debug("/configuration/service[@name='"+serviceImpl.getServiceName()+"']/protocol",
						"port", GenerateXMLDebug.CONFIG_ELEMENT,
						serviceImpl.getServiceGuiName(), serviceImpl.getServiceName());
				if(serviceImpl.isConfigPort()){
					
					oDebug.debug("/configuration/service[@name='"+serviceImpl.getServiceName()+"']/protocol",
							"port", GenerateXMLDebug.SET_VALUE,
							serviceImpl.getServiceGuiName(), serviceImpl.getServiceName());
					Object[][] portParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, serviceImpl.getServicePort()}
					};
					protocolObj.setPort(
							(ProtocolPort)CLICommonFunc.createObjectWithName(ProtocolPort.class, portParm)
					);
				}
				
				/** element : <service>.<protocol>.<timeout> */
				oDebug.debug("/configuration/service[@name='"+serviceImpl.getServiceName()+"']/protocol",
						"timeout", GenerateXMLDebug.CONFIG_ELEMENT,
						serviceImpl.getServiceGuiName(), serviceImpl.getServiceName());
				if(!serviceImpl.isSpecialDefValue()){
					
					oDebug.debug("/configuration/service[@name='"+serviceImpl.getServiceName()+"']/protocol",
							"timeout", GenerateXMLDebug.SET_VALUE,
							serviceImpl.getServiceGuiName(), serviceImpl.getServiceName());
					Object[][] timeOutParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, serviceImpl.getServiceTimeOut()}
					};
					protocolObj.setTimeout(
							(ProtocolTimeout)CLICommonFunc.createObjectWithName(ProtocolTimeout.class, timeOutParm)
					);
				}
				
			}
			
			/** element : <service>.<alg> */
			if(childObj instanceof ServiceObj.Alg){
				ServiceObj.Alg algObj = (ServiceObj.Alg)childObj;
				
				/** attribute: operation */
				algObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/service[@name='"+serviceImpl.getServiceName()+"']",
						"alg", GenerateXMLDebug.SET_VALUE,
						serviceImpl.getServiceGuiName(), serviceImpl.getServiceName());
				algObj.setValue(serviceImpl.getServiceAlgType());
			}
			
			/** element: <service>.<app-id> */
			if(childObj instanceof ServiceAppId){
				ServiceAppId appId = (ServiceAppId)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/service[@name='"+serviceImpl.getServiceName()+"']",
						"app-id", GenerateXMLDebug.SET_VALUE,
						serviceImpl.getServiceGuiName(), serviceImpl.getServiceName());
				appId.setValue(serviceImpl.getAppid());
				
				/** attribute: operation */
				appId.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				/** element: <service>.<app-id>.<timeout> */
				oDebug.debug("/configuration/service[@name='"+serviceImpl.getServiceName()+"']/app-id",
						"timeout", GenerateXMLDebug.SET_VALUE,
						serviceImpl.getServiceGuiName(), serviceImpl.getServiceName());
				Object[][] timeOutParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, serviceImpl.getServiceTimeOut()}
				};
				appId.setTimeout(
						(ProtocolTimeout)CLICommonFunc.createObjectWithName(ProtocolTimeout.class, timeOutParm)
				);
			}
			
		}
	}
}
