package com.ah.be.config.create;

import com.ah.be.config.create.source.ClientModeInt;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.wlan.SsidProfile;
import com.ah.xml.be.config.ClientModeObj;
import com.ah.xml.be.config.ClientModeSsid;
import com.ah.xml.be.config.ClientModeSsidProtocolSuite;


public class CreateClientModeTree {
	private ClientModeInt clientModeImpl;

	private ClientModeObj clientModeObj;

	public CreateClientModeTree(ClientModeInt clientModeImpl) throws Exception{
		this.clientModeImpl = clientModeImpl;
	}

	public void generate() throws Exception{
		if(clientModeImpl.isEnableClientMode()){
			clientModeObj = new ClientModeObj();
			this.generateClientModeLevel_1();
		}
	}

	private void generateClientModeLevel_1() throws Exception{
		/***
		 * <client-mode>.<ssid>
		 */
		for(int index=0; index<clientModeImpl.getClientModeSsidSize(); index++){
			clientModeObj.getSsid().add(this.createClientModeSsid(index));
		}

        clientModeObj.setBandMode(CLICommonFunc.createAhStringActObj(
            clientModeImpl.isDynamicBandMode(AhInterface.DEVICE_IF_TYPE_WIFI0) ? "dynamic" : "static", true));
	}

	/***
	 * <client-mode>.<ssid> [passphrase | wep-key   {ascii | hex} <string | hex-digits>]   
	 */
	private ClientModeSsid createClientModeSsid(int index) throws Exception{
		ClientModeSsid clientModeSsid = new ClientModeSsid();
		clientModeSsid.setName(clientModeImpl.getSsidName(index));
		if(SsidProfile.ACCESS_MODE_OPEN == clientModeImpl.getAccessMode(index)){
			clientModeSsid.setCr("");
		}else if(SsidProfile.ACCESS_MODE_WEP == clientModeImpl.getAccessMode(index)){
			clientModeSsid.setCr("");
			clientModeSsid.setWepKey(this.createPassphraseSsidProtocolSuite(index));
		}else if(SsidProfile.ACCESS_MODE_WPA == clientModeImpl.getAccessMode(index)){
			clientModeSsid.setCr("");
			clientModeSsid.setPassphrase(this.createPassphraseSsidProtocolSuite(index));
		}else{
			return null;
		}
		/**
		 * <client-mode>.<ssid>.<priority>
		 */
		clientModeSsid.setPriority(CLICommonFunc.createAhIntActObj(clientModeImpl.getPriority(index), CLICommonFunc.getYesDefault()));
		return clientModeSsid;
	}

	private ClientModeSsidProtocolSuite createPassphraseSsidProtocolSuite(int index) throws Exception{
		ClientModeSsidProtocolSuite clientModeSsidProtocolSuite = new ClientModeSsidProtocolSuite();
		if(clientModeImpl.getKeyType(index) == 0){
			clientModeSsidProtocolSuite.setAscii(CLICommonFunc.createAhEncryptedString(clientModeImpl.getKeyValue(index)));
		}else if(clientModeImpl.getKeyType(index) == 1){
			clientModeSsidProtocolSuite.setHex(CLICommonFunc.createAhEncryptedString(clientModeImpl.getKeyValue(index)));
		}else{
			return null;
		}
		return clientModeSsidProtocolSuite;
	}

	public ClientModeObj getClientModeObj() {
		return clientModeObj;
	}

	public void setClientModeObj(ClientModeObj clientModeObj) {
		this.clientModeObj = clientModeObj;
	}
}
