package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.SystemProfileInt;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.util.MgrUtil;

/**
 * @author zhang
 * @version 2008-4-15 15:23:28
 */

@SuppressWarnings("static-access")
public class SystemProfileImpl implements SystemProfileInt {
	
	private HiveAp hiveAp;
	private MgmtServiceOption mgmtOpt;
	private final HmStartConfig hmStart;
	
	public SystemProfileImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		mgmtOpt = hiveAp.getConfigTemplate().getMgmtServiceOption();
		hmStart = MgrUtil.getQueryEntity().findBoByAttribute(HmStartConfig.class, "owner", hiveAp.getOwner());
	}
	
	public String getMgmtOptionGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.mgmtServiceOption");
	}
	
	public String getMgmtOptionName(){
		if(mgmtOpt != null){
			return mgmtOpt.getMgmtName();
		}else{
			return null;
		}
	}
	
	public boolean isHiveAp11n(){
		return hiveAp.is11nHiveAP();
	}
	
	public boolean isConfigMgmtOpt(){
		return mgmtOpt != null;
	}
	
	public boolean isConfigHighThreshold(){
		return this.hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_330 && 
			this.hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_350;
	}

	public int getHighThreshold(){
		return mgmtOpt.getTempAlarmThreshold();
	}
	
	public int getUnderSpeedThreshold(){
		return mgmtOpt.getFansUnderSpeedAlarmThreshold();
	}
	
	//For the 11n APs, 330/350/370/390/230 not support smart poe
	public boolean isConfigSmartPoe(){
		return this.hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_330 && 
				this.hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_350 && 
				this.hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_370 &&
				this.hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_390 &&
				this.hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_230;
	}
	
	public boolean getSmartPoeEnable(){
		return mgmtOpt.getEnableSmartPoe();
	}
	
	public boolean isSystemLedBright(){
		if(hmStart.getModeType() == HmStartConfig.HM_MODE_EASY){
			return hmStart.getLedBrightness() == MgmtServiceOption.SYSTEM_LED_BRIGHT;
		}
		return mgmtOpt.getSystemLedBrightness() == MgmtServiceOption.SYSTEM_LED_BRIGHT;
	}
	
	public boolean isSystemLedSoft(){
		if(hmStart.getModeType() == HmStartConfig.HM_MODE_EASY){
			return hmStart.getLedBrightness() == MgmtServiceOption.SYSTEM_LED_SOFT;
		}
		return mgmtOpt.getSystemLedBrightness() == MgmtServiceOption.SYSTEM_LED_SOFT;
	}
	
	public boolean isSystemLedDim(){
		if(hmStart.getModeType() == HmStartConfig.HM_MODE_EASY){
			return hmStart.getLedBrightness() == MgmtServiceOption.SYSTEM_LED_DIM;
		}
		return mgmtOpt.getSystemLedBrightness() == MgmtServiceOption.SYSTEM_LED_DIM;
	}
	
	public boolean isSystemLedOff(){
		if(hmStart.getModeType() == HmStartConfig.HM_MODE_EASY){
			return hmStart.getLedBrightness() == MgmtServiceOption.SYSTEM_LED_OFF;
		}
		return mgmtOpt.getSystemLedBrightness() == MgmtServiceOption.SYSTEM_LED_OFF;
	}
	
	public boolean isIcmpRedirectEnable(){
		return mgmtOpt.isEnableIcmpRedirect();
	}
	
	public boolean isWebServerEnable(){
		return hiveAp.getConfigTemplate().isEnableHttpServer();
	}
	
	public boolean isConfigSystemLed(){
		return hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_121 || 
			hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_141;
	}
	
	//Only AP370/AP390/AP230 support PoE mode
	public boolean isSupportPoEMode(){
		return hiveAp.isSupportPoEMode();
	}

	@Override
	public short getPoEPowerMode() {
		return hiveAp.getPoeMode();
	}

	@Override
	public String getPoEPrimaryEth() {
		return hiveAp.getPoePrimaryEthName();
	}
}
