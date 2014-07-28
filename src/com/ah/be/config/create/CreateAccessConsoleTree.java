package com.ah.be.config.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.AccessConsoleInt;
import com.ah.be.config.create.source.AccessConsoleInt.ProtocolSuitType;
import com.ah.xml.be.config.AcProtocolSuiteWpa;
import com.ah.xml.be.config.AccessConsoleObj;

/**
 * @author zhang
 * @version 2008-9-16 16:52:19
 */

public class CreateAccessConsoleTree {
	
	private AccessConsoleInt accessConsoleImp;
	
	private AccessConsoleObj accessConsoleObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> accessConsoleChildLevel_1 = new ArrayList<Object>();
	private List<Object> accessConsoleChildLevel_2 = new ArrayList<Object>();

	public CreateAccessConsoleTree(AccessConsoleInt accessConsoleImp, GenerateXMLDebug oDebug) throws Exception{
		this.accessConsoleImp = accessConsoleImp;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		/** element: <access-console> */
		oDebug.debug("/configuration", 
				"access-console", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		if(accessConsoleImp.isConfigAccessConsole()){
			accessConsoleObj = new AccessConsoleObj();
			
			generateAccessConsoleLevel_1();
		}
	}
	
	public AccessConsoleObj getAccessConsoleObj(){
		return this.accessConsoleObj;
	}
	
	private void generateAccessConsoleLevel_1() throws Exception{
		/**
		 * <access-console>				AccessConsoleObj
		 */
		
		/** element: <access-console>.<mode> */
		oDebug.debug("/configuration/access-console", 
				"mode", GenerateXMLDebug.SET_VALUE,
				accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
		Object[][] modeParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, accessConsoleImp.getConsoleMode()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		accessConsoleObj.setMode(
				(AccessConsoleObj.Mode)CLICommonFunc.createObjectWithName(AccessConsoleObj.Mode.class, modeParm)
		);
		
		/** element: <access-console>.<security> */
		AccessConsoleObj.Security securityObj = new AccessConsoleObj.Security();
		accessConsoleChildLevel_1.add(securityObj);
		accessConsoleObj.setSecurity(securityObj);
		
		/** element: <access-console>.<max-client> */
		oDebug.debug("/configuration/access-console", 
				"max-client", GenerateXMLDebug.SET_VALUE,
				accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
		Object[][] maxClientParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, accessConsoleImp.getMaxClient()},
				{CLICommonFunc.ATTRIBUTE_OPERATION,CLICommonFunc.getYesDefault()}
		};
		accessConsoleObj.setMaxClient(
				(AccessConsoleObj.MaxClient)CLICommonFunc.createObjectWithName(AccessConsoleObj.MaxClient.class, maxClientParm)
		);
		
		/** element: <access-console>.<hide-ssid> */
		oDebug.debug("/configuration/access-console", 
				"hide-ssid", GenerateXMLDebug.SET_VALUE,
				accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
		accessConsoleObj.setHideSsid(CLICommonFunc.getAhOnlyAct(accessConsoleImp.isHideSsid()));
		
		/** element: <access-console>.<telnet> */
		oDebug.debug("/configuration/access-console", 
				"telnet", GenerateXMLDebug.SET_OPERATION,
				accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
		accessConsoleObj.setTelnet(CLICommonFunc.getAhOnlyAct(accessConsoleImp.isTelentEnable()));
		
		generateAccessConsoleLevel_2();
	}
	
	private void generateAccessConsoleLevel_2() throws IOException{
		/**
		 * <access-console>.<security>		AccessConsoleObj.Security
		 */
		for(Object childObj : accessConsoleChildLevel_1){
			
			/** element: <access-console>.<security> */
			if(childObj instanceof AccessConsoleObj.Security){
				AccessConsoleObj.Security securityObj = (AccessConsoleObj.Security)childObj;
				
				/** element: <access-console>.<security>.<protocol-suite> */
				AccessConsoleObj.Security.ProtocolSuite protocolSuiteObj = new AccessConsoleObj.Security.ProtocolSuite();
				accessConsoleChildLevel_2.add(protocolSuiteObj);
				securityObj.setProtocolSuite(protocolSuiteObj);
				
				/** element: <access-console>.<security>.<mac-filter> */
				oDebug.debug("/configuration/access-console/security", 
						"mac-filter", GenerateXMLDebug.CONFIG_ELEMENT,
						accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
				if(accessConsoleImp.isConfigMacFilter()){
					
					oDebug.debug("/configuration/access-console/security", 
							"mac-filter", GenerateXMLDebug.SET_VALUE,
							accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
					securityObj.setMacFilter(CLICommonFunc.createAhNameActObj(
							accessConsoleImp.getMacFilterName(), CLICommonFunc.getYesDefault())
					);
				}
			}
		}
		accessConsoleChildLevel_1.clear();
		generateAccessConsoleLevel_3();
	}
	
	private void generateAccessConsoleLevel_3() throws IOException{
		/**
		 * <access-console>.<security>.<protocol-suite> 	AccessConsoleObj.Security.ProtocolSuite
		 */
		for(Object childObj : accessConsoleChildLevel_2){
			
			/** element: <access-console>.<security>.<protocol-suite> */
			if(childObj instanceof AccessConsoleObj.Security.ProtocolSuite){
				AccessConsoleObj.Security.ProtocolSuite protocolSuiteObj = (AccessConsoleObj.Security.ProtocolSuite)childObj;
				
				/** attribute: operation */
				protocolSuiteObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <access-console>.<security>.<protocol-suite>.<open> */
				oDebug.debug("/configuration/access-console/security/protocol-suite", 
						"open", GenerateXMLDebug.CONFIG_ELEMENT,
						accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
				if(accessConsoleImp.isConfigProtocolSuitWithType(ProtocolSuitType.open)){
					protocolSuiteObj.setOpen("");
				}
				
				/** element: <access-console>.<security>.<protocol-suite>.<wpa-auto-psk> */
				oDebug.debug("/configuration/access-console/security/protocol-suite", 
						"wpa-auto-psk", GenerateXMLDebug.CONFIG_ELEMENT,
						accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
				if(accessConsoleImp.isConfigProtocolSuitWithType(ProtocolSuitType.wpa_auto_psk)){
					
					oDebug.debug("/configuration/access-console/security/protocol-suite", 
							"wpa-auto-psk", GenerateXMLDebug.NULL,
							accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
					protocolSuiteObj.setWpaAutoPsk(createAcProtocolSuiteWpa(accessConsoleImp.getAscIIkey()));
				}
				
				/** element: <access-console>.<security>.<protocol-suite>.<wpa-tkip-psk> */
				oDebug.debug("/configuration/access-console/security/protocol-suite", 
						"wpa-tkip-psk", GenerateXMLDebug.CONFIG_ELEMENT,
						accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
				if(accessConsoleImp.isConfigProtocolSuitWithType(ProtocolSuitType.wpa_tkip_psk)){
					
					oDebug.debug("/configuration/access-console/security/protocol-suite", 
							"wpa-tkip-psk", GenerateXMLDebug.NULL,
							accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
					protocolSuiteObj.setWpaTkipPsk(createAcProtocolSuiteWpa(accessConsoleImp.getAscIIkey()));
				}
				
				/** element: <access-console>.<security>.<protocol-suite>.<wpa-aes-psk> */
				oDebug.debug("/configuration/access-console/security/protocol-suite", 
						"wpa-aes-psk", GenerateXMLDebug.CONFIG_ELEMENT,
						accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
				if(accessConsoleImp.isConfigProtocolSuitWithType(ProtocolSuitType.wpa_aes_psk)){
					
					oDebug.debug("/configuration/access-console/security/protocol-suite", 
							"wpa-aes-psk", GenerateXMLDebug.NULL,
							accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
					protocolSuiteObj.setWpaAesPsk(createAcProtocolSuiteWpa(accessConsoleImp.getAscIIkey()));
				}
				
				/** element: <access-console>.<security>.<protocol-suite>.<wpa2-tkip-psk> */
				oDebug.debug("/configuration/access-console/security/protocol-suite", 
						"wpa2-tkip-psk", GenerateXMLDebug.CONFIG_ELEMENT,
						accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
				if(accessConsoleImp.isConfigProtocolSuitWithType(ProtocolSuitType.wpa2_tkip_psk)){
					
					oDebug.debug("/configuration/access-console/security/protocol-suite", 
							"wpa2-tkip-psk", GenerateXMLDebug.NULL,
							accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
					protocolSuiteObj.setWpa2TkipPsk(createAcProtocolSuiteWpa(accessConsoleImp.getAscIIkey()));
				}
				
				/** element: <access-console>.<security>.<protocol-suite>.<wpa2-aes-psk> */
				oDebug.debug("/configuration/access-console/security/protocol-suite", 
						"wpa2-aes-psk", GenerateXMLDebug.CONFIG_ELEMENT,
						accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
				if(accessConsoleImp.isConfigProtocolSuitWithType(ProtocolSuitType.wpa2_aes_psk)){
					
					oDebug.debug("/configuration/access-console/security/protocol-suite", 
							"wpa2-aes-psk", GenerateXMLDebug.NULL,
							accessConsoleImp.getAccessConsoleGuiName(), accessConsoleImp.getAccessConsoleName());
					protocolSuiteObj.setWpa2AesPsk(createAcProtocolSuiteWpa(accessConsoleImp.getAscIIkey()));
				}
			}
		}
		accessConsoleChildLevel_2.clear();
	}
	
	private AcProtocolSuiteWpa createAcProtocolSuiteWpa(String asciiKey) throws IOException{
		AcProtocolSuiteWpa protocolObj = new AcProtocolSuiteWpa();
		protocolObj.setAsciiKey(CLICommonFunc.createAhEncryptedString(accessConsoleImp.getAscIIkey()));
		return protocolObj;
	}
}
