package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.CapwapProfileInt;
import com.ah.xml.be.config.AhEnable;
import com.ah.xml.be.config.CapwapDiscoveryMethod;
import com.ah.xml.be.config.CapwapObj;
import com.ah.xml.be.config.CapwapObj.Client.Discovery;
import com.ah.xml.be.config.CapwapServer;
import com.ah.xml.be.config.CapwapServerName;

/**
 * @author zhang
 * @version 2008-1-3 14:14:22
 */

public class CreateCapwapTree {
	
	private CapwapProfileInt cwpImpl;
	private CapwapObj capwapObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> capwapChildLevel_1 = new ArrayList<Object>();
	private List<Object> capwapChildLevel_2 = new ArrayList<Object>();
	private List<Object> capwapChildLevel_3 = new ArrayList<Object>();

	public CreateCapwapTree(CapwapProfileInt cwpImpl, GenerateXMLDebug oDebug) {
		this.cwpImpl = cwpImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		capwapObj = new CapwapObj();
		generateLevel_1();
	}
	
	public CapwapObj getCapwapObj(){
		return this.capwapObj;
	}
	
	private void generateLevel_1() throws Exception {
		/**
		 * <capwap>					CapwapObj
		 */
		
		/** attribute: updatTime */
		capwapObj.setUpdateTime(cwpImpl.getUpdateTime());
		
		/** element: <capwap>.<client> */
		CapwapObj.Client clientObj = new CapwapObj.Client();
		capwapChildLevel_1.add(clientObj);
		capwapObj.setClient(clientObj);
		
		generateLevel_2();
	}
	
	private void generateLevel_2() throws Exception {
		/***
		 * <capwap>.<client>		CapwapObj.Client
		 */
		for(Object childObj : capwapChildLevel_1){
			
			/** element: <capwap>.<client> */
			if(childObj instanceof CapwapObj.Client){
				CapwapObj.Client clientObj = (CapwapObj.Client)childObj;
				
				/** element: <capwap>.<client>.<server> */
				CapwapObj.Client.Server serverObj = new CapwapObj.Client.Server();
				capwapChildLevel_2.add(serverObj);
				clientObj.setServer(serverObj);
				
				/** element: <capwap>.<client>.<neighbor> */
				CapwapObj.Client.Neighbor neighborObj = new CapwapObj.Client.Neighbor();
				capwapChildLevel_2.add(neighborObj);
				clientObj.setNeighbor(neighborObj);
				
				/** element: <capwap>.<client>.<dtls> */
				CapwapObj.Client.Dtls dtlsObj = new CapwapObj.Client.Dtls();
				capwapChildLevel_2.add(dtlsObj);
				clientObj.setDtls(dtlsObj);
				
				/** element: <capwap>.<client>.<vhm> */
				oDebug.debug("/configuration/capwap/client", 
						"vhm", GenerateXMLDebug.SET_VALUE,
						cwpImpl.getCapwapGuiName(), null);
				clientObj.setVhmName(CLICommonFunc.createAhStringActObj(cwpImpl.getVhmValue(), CLICommonFunc.getYesDefault()));
				
				/** element: <capwap>.<client>.<pci-alert> */
				oDebug.debug("/configuration/capwap/client", 
						"pci-alert", GenerateXMLDebug.CONFIG_ELEMENT,
						cwpImpl.getCapwapGuiName(), null);
				if(cwpImpl.isConfigPCI()){
					AhEnable enableObj = new AhEnable();
					capwapChildLevel_2.add(enableObj);
					clientObj.setPciAlert(enableObj);
				}
				
				/** element: <capwap>.<client>.<discovery> */
				Discovery discovery = new Discovery();
				CapwapDiscoveryMethod method = new CapwapDiscoveryMethod();
				method.setBroadcast(CLICommonFunc.getAhOnlyAct(cwpImpl.isEnableAutoDiscovery()));
				discovery.setMethod(method);
				clientObj.setDiscovery(discovery);
				//capwapChildLevel_2.add(discovery);
			}
		}
		capwapChildLevel_1.clear();
		generateLevel_3();
	}
	
	private void generateLevel_3() throws Exception {
		/**
		 * <capwap>.<client>.<dtls>					CapwapObj.Client.Dtls
		 * <capwap>.<client>.<server>				CapwapObj.Client.Server
		 * <capwap>.<client>.<neighbor>				CapwapObj.Client.Neighbor
		 * <capwap>.<client>.<pci-alert>			AhEnable
		 */
		for(Object childObj : capwapChildLevel_2){
			
			/** element: <capwap>.<client>.<dtls> */
			if(childObj instanceof CapwapObj.Client.Dtls){
				CapwapObj.Client.Dtls dtlsObj = (CapwapObj.Client.Dtls)childObj;
				
				/** element: <capwap>.<client>.<dtls>.<enable> */
				CapwapObj.Client.Dtls.Enable enableObj = new CapwapObj.Client.Dtls.Enable();
				capwapChildLevel_3.add(enableObj);
				dtlsObj.setEnable(enableObj);
				
				/** element: <capwap>.<client>.<dtls>.<bootstrap-passphrase> */
				oDebug.debug("/configuration/capwap/client/dtls", 
						"bootstrap-passphrase", GenerateXMLDebug.CONFIG_ELEMENT,
						cwpImpl.getCapwapGuiName(), null);
				if(cwpImpl.isConfigCwpDtlsBootPassPhrase()){
					
					oDebug.debug("/configuration/capwap/client/dtls", 
							"bootstrap-passphrase", GenerateXMLDebug.SET_VALUE,
							cwpImpl.getCapwapGuiName(), null);
					dtlsObj.setBootstrapPassphrase(
							CLICommonFunc.createAhEncryptedStringAct(cwpImpl.getCwpDtlsBootPassPhrase(), 
							CLICommonFunc.getYesDefault())
					);
				}
				
				/** element: <capwap>.<client>.<dtls>.<hm-defined-passphrase> */
				oDebug.debug("/configuration/capwap/client/dtls", 
						"hm-defined-passphrase", GenerateXMLDebug.CONFIG_ELEMENT,
						cwpImpl.getCapwapGuiName(), null);
				if(cwpImpl.isConfigHmDefinedPassphrase()){
					CapwapObj.Client.Dtls.HmDefinedPassphrase hmDefinedPassphraseObj = new CapwapObj.Client.Dtls.HmDefinedPassphrase();
					capwapChildLevel_3.add(hmDefinedPassphraseObj);
					dtlsObj.setHmDefinedPassphrase(hmDefinedPassphraseObj);
				}
				
				/** element: <capwap>.<client>.<dtls>.<negotiation> */
				CapwapObj.Client.Dtls.Negotiation negotiationObj = new CapwapObj.Client.Dtls.Negotiation();
				capwapChildLevel_3.add(negotiationObj);
				dtlsObj.setNegotiation(negotiationObj);
			}
			
			/** element: <capwap>.<client>.<server> */
			if(childObj instanceof CapwapObj.Client.Server){
				CapwapObj.Client.Server serverObj = (CapwapObj.Client.Server)childObj;
				
				/** element: <capwap>.<client>.<server>.<primary> */
				oDebug.debug("/configuration/capwap/client/server", 
						"primary", GenerateXMLDebug.CONFIG_ELEMENT,
						cwpImpl.getCapwapGuiName(), null);
				if(cwpImpl.isConfigCapwapPrimary()){
					serverObj.setPrimary(this.createCapwapServer(cwpImpl.getCapwapIpPrimary()));
				}
				
				/** element: <capwap>.<client>.<server>.<backup> */
				oDebug.debug("/configuration/capwap/client/server", 
						"backup", GenerateXMLDebug.CONFIG_ELEMENT,
						cwpImpl.getCapwapGuiName(), null);
				if(cwpImpl.isConfigCapwapBackup()){
					serverObj.setBackup(this.createCapwapServer(cwpImpl.getCapwapIpBackup()));
				}
				
				/** element: <capwap>.<client>.<server>.<cr-primary> */
				oDebug.debug("/configuration/capwap/client/server", 
						"cr-primary", GenerateXMLDebug.CONFIG_ELEMENT,
						cwpImpl.getCapwapGuiName(), null);
				if(cwpImpl.isConfigCapwapPrimary()){
					serverObj.setCrPrimary(this.createCapwapServer(cwpImpl.getCapwapIpPrimary()));
				}
				
				/** element: <capwap>.<client>.<server>.<cr-backup> */
				oDebug.debug("/configuration/capwap/client/server", 
						"cr-backup", GenerateXMLDebug.CONFIG_ELEMENT,
						cwpImpl.getCapwapGuiName(), null);
				if(cwpImpl.isConfigCapwapBackup()){
					serverObj.setCrBackup(this.createCapwapServer(cwpImpl.getCapwapIpBackup()));
				}
				
				/** element: <capwap>.<client>.<server>.<port> */
				if(cwpImpl.isConfigCapwapPort()){
					CapwapObj.Client.Server.Port portObj = new CapwapObj.Client.Server.Port();
					capwapChildLevel_3.add(portObj);
					serverObj.setPort(portObj);
				}
			}
			
			/** element: <capwap>.<client>.<neighbor> */
			if(childObj instanceof CapwapObj.Client.Neighbor){
				CapwapObj.Client.Neighbor neighborObj = (CapwapObj.Client.Neighbor)childObj;
				
				/** element: <capwap>.<client>.<neighbor>.<heartbeat> */
				CapwapObj.Client.Neighbor.Heartbeat heartbeatObj = new CapwapObj.Client.Neighbor.Heartbeat();
				capwapChildLevel_3.add(heartbeatObj);
				neighborObj.setHeartbeat(heartbeatObj);
				
				/** element: <capwap>.<client>.<neighbor>.<dead> */
				CapwapObj.Client.Neighbor.Dead deadObj = new CapwapObj.Client.Neighbor.Dead();
				capwapChildLevel_3.add(deadObj);
				neighborObj.setDead(deadObj);
			}
			
			/** element: <capwap>.<client>.<pci-alert> */
			if(childObj instanceof AhEnable){
				AhEnable enableObj = (AhEnable)childObj;
				
				/** element: <capwap>.<client>.<pci-alert>.<enable> */
				oDebug.debug("/configuration/capwap/client/pci-alert",
						"enable", GenerateXMLDebug.SET_OPERATION,
						cwpImpl.getCapwapGuiName(), null);
				enableObj.setEnable(CLICommonFunc.getAhOnlyAct(cwpImpl.isEnablePCI()));
			}
		}
		capwapChildLevel_2.clear();
		generateLevel_4();
	}
	
	private void generateLevel_4() throws Exception {
		/**
		 * <capwap>.<client>.<server>.<port>			CapwapObj.Client.Server.Port
		 * <capwap>.<client>.<server>.<name>			CapwapObj.Client.Server.Name
		 * <capwap>.<client>.<neighbor>.<heartbeat>		CapwapObj.Client.Neighbor.Heartbeat
		 * <capwap>.<client>.<neighbor>.<dead>			CapwapObj.Client.Neighbor.Dead
		 * <capwap>.<client>.<dtls>.<hm-defined-passphrase>		CapwapObj.Client.Dtls.HmDefinedPassphrase
		 * <capwap>.<client>.<dtls>.<enable>			CapwapObj.Client.Dtls.Enable
		 * <capwap>.<client>.<dtls>.<negotiation>		CapwapObj.Client.Dtls.Negotiation
		 */
		for(Object childObj : capwapChildLevel_3){
			
			/** element: <capwap>.<client>.<server>.<port> */
			if(childObj instanceof CapwapObj.Client.Server.Port){
				CapwapObj.Client.Server.Port portObj = (CapwapObj.Client.Server.Port)childObj;
				
				/** attribute: operation */
				portObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/capwap/client/server", 
						"port", GenerateXMLDebug.SET_VALUE,
						cwpImpl.getCapwapGuiName(), null);
				portObj.setValue(cwpImpl.getServerPort());
				
				/** element: <capwap>.<client>.<server>.<port>.<no-disconnect> */
				portObj.setNoDisconnect(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <capwap>.<client>.<neighbor>.<heartbeat>*/
			if(childObj instanceof CapwapObj.Client.Neighbor.Heartbeat){
				CapwapObj.Client.Neighbor.Heartbeat heartbeatObj = (CapwapObj.Client.Neighbor.Heartbeat)childObj;
				
				/** element: <capwap>.<client>.<neighbor>.<heartbeat>.<interval> */
				oDebug.debug("/configuration/capwap/client/neighbor/heartbeat", 
						"interval", GenerateXMLDebug.SET_VALUE,
						cwpImpl.getCapwapGuiName(), null);
				Object[][] intervlParm = {
						{CLICommonFunc.ATTRIBUTE_NAME, cwpImpl.getCwpHeartbeatInterval()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				heartbeatObj.setInterval(
						(CapwapObj.Client.Neighbor.Heartbeat.Interval)CLICommonFunc.createObjectWithName(
								CapwapObj.Client.Neighbor.Heartbeat.Interval.class, intervlParm)
				);
			}
			
			/** element: <capwap>.<client>.<neighbor>.<dead> */
			if(childObj instanceof CapwapObj.Client.Neighbor.Dead){
				CapwapObj.Client.Neighbor.Dead deadObj = (CapwapObj.Client.Neighbor.Dead)childObj;
				
				/** element: <capwap>.<client>.<neighbor>.<dead>.<interval> */
				oDebug.debug("/configuration/capwap/client/neighbor/dead", 
						"interval", GenerateXMLDebug.SET_VALUE,
						cwpImpl.getCapwapGuiName(), null);
				Object[][] deadInterParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, cwpImpl.getCwpDeadInterval()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				deadObj.setInterval(
						(CapwapObj.Client.Neighbor.Dead.Interval)CLICommonFunc.createObjectWithName(
								CapwapObj.Client.Neighbor.Dead.Interval.class, deadInterParm)
				);
				
			}
			
			/** element: <capwap>.<client>.<dtls>.<hm-defined-passphrase> */
			if(childObj instanceof CapwapObj.Client.Dtls.HmDefinedPassphrase){
				CapwapObj.Client.Dtls.HmDefinedPassphrase hmDefinedPassphraseObj = (CapwapObj.Client.Dtls.HmDefinedPassphrase)childObj;
				
				/** attribute: operation */
				hmDefinedPassphraseObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** aattribute: value */
				oDebug.debug("/configuration/capwap/client/dtls", 
						"hm-defined-passphrase", GenerateXMLDebug.SET_VALUE,
						cwpImpl.getCapwapGuiName(), null);
				hmDefinedPassphraseObj.setValue(cwpImpl.getHmDefinedPassphraseValue());
				
				/** aattribute: encrypted */
				hmDefinedPassphraseObj.setEncrypted(hmDefinedPassphraseObj.getEncrypted());
				
				/** element: <capwap>.<client>.<dtls>.<hm-defined-passphrase>.<keyId> */
				oDebug.debug("/configuration/capwap/client/dtls/hm-defined-passphrase", 
						"keyId", GenerateXMLDebug.SET_VALUE,
						cwpImpl.getCapwapGuiName(), null);
				Object[][] keyIdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, cwpImpl.getHmDefinedPassphraseKey()}
				};
				hmDefinedPassphraseObj.setKeyId(
						(CapwapObj.Client.Dtls.HmDefinedPassphrase.KeyId)CLICommonFunc.createObjectWithName(
								CapwapObj.Client.Dtls.HmDefinedPassphrase.KeyId.class, keyIdParm)
				);
			}
			
			/** element: <capwap>.<client>.<dtls>.<enable> */
			if(childObj instanceof CapwapObj.Client.Dtls.Enable){
				CapwapObj.Client.Dtls.Enable enableObj = (CapwapObj.Client.Dtls.Enable)childObj;
				
				/** element: <capwap>.<client>.<dtls>.<enable>.<no-disconnect> */
				oDebug.debug("/configuration/capwap/client/dtls/enable", 
						"no-disconnect", GenerateXMLDebug.SET_OPERATION,
						cwpImpl.getCapwapGuiName(), null);
				enableObj.setNoDisconnect(CLICommonFunc.getAhOnlyAct(cwpImpl.isEnableCwpDtls()));
			}
			
			/** element: <capwap>.<client>.<dtls>.<negotiation> */
			if(childObj instanceof CapwapObj.Client.Dtls.Negotiation){
				CapwapObj.Client.Dtls.Negotiation negotiationObj = (CapwapObj.Client.Dtls.Negotiation)childObj;
				
				/** element: <capwap>.<client>.<dtls>.<negotiation>.<enable> */
				negotiationObj.setEnable(CLICommonFunc.getAhOnlyAct(false));
			}
			
		}
		capwapChildLevel_3.clear();
	}
	
	private CapwapServer createCapwapServer(String ipAddr) throws CreateXMLException{
		CapwapServer server = new CapwapServer();
		CapwapServerName nameObj = new CapwapServerName();
		
		/** attribute: value */
		nameObj.setValue(ipAddr);
		
		/** attribute: operation */
		nameObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <no-disconnect> */
		nameObj.setNoDisconnect("");
		
		/** element: <via-vpn-tunnel> */
		if(cwpImpl.isEnableVpnTunnel(nameObj.getValue())){
			nameObj.setViaVpnTunnel("");
		}
		
		server.setName(nameObj);
		
		return server;
	}
	
}
