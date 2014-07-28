package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.AccessConsoleInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.MgrUtil;
import com.ah.xml.be.config.AcModeValue;

/**
 * @author zhang
 * @version 2008-9-16 16:53:21
 */

public class AccessConsoleImpl implements AccessConsoleInt {
	
	private final HiveAp hiveAp;
	private AccessConsole accessConsoleDB;

	public AccessConsoleImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		accessConsoleDB = hiveAp.getConfigTemplate().getAccessConsole();
	}
	
	public String getAccessConsoleGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.accessConsole");
	}
	
	public String getAccessConsoleName(){
		if(accessConsoleDB != null){
			return accessConsoleDB.getConsoleName();
		}else{
			return null;
		}
	}
	
	public boolean isConfigAccessConsole(){
		return accessConsoleDB != null;
	}
	
	public AcModeValue getConsoleMode(){
		AcModeValue modevalue = null;
		switch(accessConsoleDB.getConsoleMode()){
			case AccessConsole.ACCESS_CONSOLE_MODE_AUTO: 
				modevalue = AcModeValue.AUTO;
				break;
			case AccessConsole.ACCESS_CONSOLE_MODE_DISABLE:
				modevalue = AcModeValue.DISABLE;
				break;
			case AccessConsole.ACCESS_CONSOLE_MODE_ENABLE:
				modevalue = AcModeValue.ENABLE;
				break;
		}
		return modevalue;
	}
	
	public int getMaxClient(){
		return accessConsoleDB.getMaxClient();
	}
	
	public boolean isHideSsid(){
		return accessConsoleDB.isHideSsid();
	}
	
	public boolean isTelentEnable(){
		return accessConsoleDB.isEnableTelnet();
	}
	
	public boolean isConfigMacFilter(){
		return accessConsoleDB.getMacFilters() != null && !accessConsoleDB.getMacFilters().isEmpty();
	}
	
	public String getMacFilterName(){
		return "AC_" + hiveAp.getMacAddress();
	}
	
	public String getAscIIkey(){
		return accessConsoleDB.getAsciiKey();
	}
	
	private ProtocolSuitType getProtocolSuitType(){
		ProtocolSuitType protocolType = null;
		int mgmtKey, encryption;
		mgmtKey = accessConsoleDB.getMgmtKey();
		encryption = accessConsoleDB.getEncryption();
		if(mgmtKey == SsidProfile.KEY_MGMT_OPEN){
			protocolType = ProtocolSuitType.open;
		}else if(mgmtKey == SsidProfile.KEY_MGMT_WPA2_PSK && encryption == SsidProfile.KEY_ENC_TKIP){
			protocolType = ProtocolSuitType.wpa2_tkip_psk;
		}else if(mgmtKey == SsidProfile.KEY_MGMT_WPA2_PSK && encryption == SsidProfile.KEY_ENC_CCMP){
			protocolType = ProtocolSuitType.wpa2_aes_psk;
		}else if(mgmtKey == SsidProfile.KEY_MGMT_WPA_PSK && encryption == SsidProfile.KEY_ENC_TKIP){
			protocolType = ProtocolSuitType.wpa_tkip_psk;
		}else if(mgmtKey == SsidProfile.KEY_MGMT_WPA_PSK && encryption == SsidProfile.KEY_ENC_CCMP){
			protocolType = ProtocolSuitType.wpa_aes_psk;
		}else if(mgmtKey == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK && encryption == SsidProfile.KEY_ENC_AUTO_TKIP_OR_CCMP){
			protocolType = ProtocolSuitType.wpa_auto_psk;
		}
		return protocolType;
	}
	
	public boolean isConfigProtocolSuitWithType(ProtocolSuitType type){
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_230){
			if(type == ProtocolSuitType.wpa_auto_psk){
				ProtocolSuitType returnType = getProtocolSuitType();
				return returnType == ProtocolSuitType.wpa_auto_psk || 
						returnType == ProtocolSuitType.wpa_aes_psk || 
						returnType == ProtocolSuitType.wpa_tkip_psk;
			}else if(type == ProtocolSuitType.wpa_aes_psk || type == ProtocolSuitType.wpa_tkip_psk){
				return false;
			}
		}
		return type == getProtocolSuitType();
	}

}