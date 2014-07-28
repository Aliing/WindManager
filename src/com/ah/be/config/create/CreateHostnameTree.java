package com.ah.be.config.create;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.HostnameProfileInt;
import com.ah.xml.be.config.HostnameObj;

/**
 * @author zhang
 * @version 2008-1-10  04:08:50
 */

public class CreateHostnameTree {
	
	private HostnameProfileInt hostNameImpl;
	private HostnameObj hostnameObj;
	
	private GenerateXMLDebug oDebug;

	public CreateHostnameTree(HostnameProfileInt hostNameImpl, GenerateXMLDebug oDebug){
		this.hostNameImpl = hostNameImpl;
		this.oDebug = oDebug;
	}
	
	public void generate(){
		if(hostNameImpl.isConfigHostName()){
			hostnameObj = new HostnameObj();
			generateChildLevel_1();
		}
	}
	
	public HostnameObj getHostnameObj(){
		return this.hostnameObj;
	}
	
	private void generateChildLevel_1(){
		/**
		 * <hostname>			HostnameObj
		 */
		
		/** attribute: updateTime */
		hostnameObj.setUpdateTime(hostNameImpl.getUpdatTime());
		
		/** attribute: operation */
		hostnameObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** attribute: value */
		oDebug.debug("/configuration", 
				"hostname", GenerateXMLDebug.SET_VALUE,
				hostNameImpl.getHiveApGuiName(), hostNameImpl.getHiveApName());
		hostnameObj.setValue(hostNameImpl.getHostName());
		
	}
	
}
