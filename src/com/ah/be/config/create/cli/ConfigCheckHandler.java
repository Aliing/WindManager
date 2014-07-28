package com.ah.be.config.create.cli;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CreateXMLException;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.Tracer;
import com.ah.xml.be.config.Configuration;
import com.ah.xml.be.config.InterfaceObj;
import com.ah.xml.be.config.RadioObj;
import com.ah.xml.be.config.Wifi;

public class ConfigCheckHandler {
	
	private static final Tracer log = new Tracer(ConfigCheckHandler.class.getSimpleName());
	
	public static final String ERROR_CLI_NOT_SUPPORT_SENSOR = "Sensor mode only be supported for HiveOS 6.1r1 or later.";
	
	public void execute(Configuration configure, HiveAp hiveAp) throws CreateXMLException {
		if (!checkConfigForSensorMode(configure, hiveAp)) {
			throw new CreateXMLException(ERROR_CLI_NOT_SUPPORT_SENSOR);
		}
	}
	
	//pre 6.1r1 device does not support sensor mode 
	private boolean checkConfigForSensorMode(Configuration configure, HiveAp hiveAp) {
		try {
			Wifi wifi0 = configure.getInterface().getWifi0();
			Wifi wifi1 = configure.getInterface().getWifi1();
			if (wifi0 != null && wifi0.getMode() != null && wifi0.getMode().getSensor() != null) {
				if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.1.0") < 0){
					return false;
				}
			}
			if (wifi1 != null && wifi1.getMode() != null && wifi1.getMode().getSensor() != null) {
				if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.1.0") < 0){
					return false;
				}
			}
			
		} catch(Exception e) {
			log.error("[ConfigCheckHandler] checkConfigForSensorMode error.", e);
		}
		return true;
	}
	
	 
	public static void main(String[] args) {
		ConfigCheckHandler obj = new ConfigCheckHandler();
		Configuration configure = new Configuration();

		InterfaceObj wifi0Interface = new InterfaceObj();
		InterfaceObj wifi1Interface = new InterfaceObj();
		Wifi wifi0 = new Wifi();
		Wifi wifi1 = new Wifi();
		wifi0Interface.setWifi0(wifi0);
		wifi0Interface.setWifi0(wifi1);
		
		configure.setInterface(wifi0Interface);
		RadioObj radioObj = new RadioObj();
		RadioObj.Profile wifi0Profile = new RadioObj.Profile();
		RadioObj.Profile wifi1Profile = new RadioObj.Profile();
		radioObj.getProfile().add(wifi0Profile);
		radioObj.getProfile().add(wifi1Profile);
		configure.setRadio(radioObj);
		
		
	}

}
