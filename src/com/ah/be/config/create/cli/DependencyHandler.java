package com.ah.be.config.create.cli;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.Tracer;
import com.ah.util.bo.report.ApplicationUtil;
import com.ah.xml.be.config.AhEnumAct;
import com.ah.xml.be.config.Configuration;
import com.ah.xml.be.config.InterfaceObj;
import com.ah.xml.be.config.IpPolicyTo;
import com.ah.xml.be.config.PseObj;
import com.ah.xml.be.config.PseProfile;
import com.ah.xml.be.config.PseProfilePowerMode;
import com.ah.xml.be.config.QosMapService;
import com.ah.xml.be.config.QosObj;
import com.ah.xml.be.config.RadioObj;
import com.ah.xml.be.config.RadioObj.Profile;
import com.ah.xml.be.config.ServiceObj;
import com.ah.xml.be.config.UsbmodemObj;
import com.ah.xml.be.config.Usbnet;
import com.ah.xml.be.config.Wifi;
import com.ah.xml.be.config.WifixMode;

public class DependencyHandler {
	
	private static final Tracer log = new Tracer(DependencyHandler.class
			.getSimpleName());
	
	private static final int SR2024_MAXPOWERSOURCE_DEFAULT = 195;
	
	private static final int SR2124P_MAXPOWERSOURCE_DEFAULT = 408;
	
	private static final int SR2148P_MAXPOWERSOURCE_DEFAULT = 779;
	
	public void execute(Configuration configure, HiveAp hiveAp) {
		try {
			specialOperateForTree(configure);
			filterObjForSensorMode(configure, hiveAp);
			filterPseMaxPowerForSwitch(configure, hiveAp);
			filterBonjourGatewayForSwitchMode(configure, hiveAp);
			filterDefaultPseProfileValue(configure, hiveAp);
			filterNotSupportedAppId(configure, hiveAp);
			filterUsbWanPriority(configure, hiveAp);
		} catch (Exception e) {
			log.error("DependencyHandler Error:", e);
		}

	}
	
	//usbmodem mode primary-wan
	//interface usbnet0 mode wan priority <number>
	private void filterUsbWanPriority(Configuration configure, HiveAp hiveAp) {
		UsbmodemObj usbmodemObj = configure.getUsbmodem();
		if (usbmodemObj != null && usbmodemObj.getMode() != null && usbmodemObj.getMode().getPrimaryWan() != null) {
			Usbnet usbnet0 = configure.getInterface().getUsbnet0();
			if (usbnet0 != null && usbnet0.getMode() != null && usbnet0.getMode().getWan() != null) {
				usbnet0.getMode().getWan().setPriority(null);
			}
		}
	}
	
	//application service can config in Qos and Firewall, and AP's supported appid is depend on AVC siginature version since 6.1r1.
	//service <string> app-id <number>
	//ip-policy firewall1 id 2 from 0.0.0.0 0.0.0.0 to 0.0.0.0 0.0.0.0 service L7-LYNC action deny
	private void filterNotSupportedAppId(Configuration configure, HiveAp hiveAp) {
		if (configure.getService() != null && configure.getService().size() == 0) {
			return;
		}
		int maxAppId = ApplicationUtil.getMaxSupportedAppCode(hiveAp);
		List<String> deletedServiceName = new ArrayList<String>();
		for (Iterator<ServiceObj> iter = configure.getService().iterator(); iter.hasNext();) {
			ServiceObj service = iter.next();
			if (service.getAppId() != null && StringUtils.isNotBlank(service.getAppId().getValue())) {
				int value = Integer.parseInt(service.getAppId().getValue());
				if (value >= ApplicationUtil.getMinCustomAppCode()) {
					continue;
				}
				if (value > maxAppId) {
					deletedServiceName.add(service.getName());
					iter.remove();
				}
			}
		}
		
//		List<IpPolicyObj> ipPolicyList = configure.getIpPolicy();
//		if (deletedServiceName.size() == 0) {
//			return;
//		}
//		if (ipPolicyList == null) {
//			return;
//		}
//		
//		for (Iterator<IpPolicyObj> iter = ipPolicyList.iterator(); iter.hasNext();) {
//			IpPolicyObj policy = iter.next();
//			boolean hasServiceDeleted = false;
//			if (policy.getId() != null) {
//				for (IpPolicyId policyId : policy.getId()) {
//					if (policyId.getFrom() != null && policyId.getFrom().getTo() != null && policyId.getFrom().getTo().getService() != null
//							&& hasFindServiceName(deletedServiceName, policyId.getFrom().getTo().getService())) {
//						hasServiceDeleted = true;
//						break;
//					}
//				}
//			}
//			if (hasServiceDeleted) {
//				iter.remove();
//			}
//		}
	}
	
	private boolean hasFindServiceName(List<String> nameList, IpPolicyTo.Service service) {
		for (String serviceName : nameList) {
			if (serviceName.equals(service.getName())) {
				return true;
			}
		}
		return false;
	}
	
	//br mode support bonjour gateway while switch mode do not
	private void filterBonjourGatewayForSwitchMode(Configuration configure, HiveAp hiveAp) {
		if (configure.getBonjourGateway() == null || hiveAp.isSwitchProduct() == false) {
			return;
		}
		if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_SWITCH) {
			configure.setBonjourGateway(null);	
		}
	}
	
	//pse profile default-pse power-mode 802.3at power-limit 32000 is default cli for 6.1r3+ device
	private void filterDefaultPseProfileValue(Configuration configure, HiveAp hiveAp) {
		if (configure.getPse() == null || configure.getPse().getProfile() == null) {
			return;
		}
		if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.3.0") < 0){
			return;
		}
		for (Iterator<PseProfile> iter = configure.getPse().getProfile().iterator(); iter.hasNext();) {
			PseProfile profile = iter.next();
			if (profile.getPowerMode() == null || profile.getPowerMode().getPowerLimit() == null) {
				continue;
			}
			String powerModeValue = profile.getPowerMode().getValue();
			int powerLimitValue = profile.getPowerMode().getPowerLimit().getValue();
			if ("802.3at".equals(powerModeValue) && (32000 == powerLimitValue)) {
				//iter.remove();
				profile.setPowerMode(null);
			}
		}
	}
	
	//pse max-power-source default values for sr24 sr48 and sr2124p are different, current implementation we use same version/default xml
	private void filterPseMaxPowerForSwitch(Configuration configure, HiveAp hiveAp) {
		if (configure.getPse() == null || configure.getPse().getMaxPowerSource() == null) {
			return;
		}
		int maxPowerSource = configure.getPse().getMaxPowerSource().getValue();
		if ((hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR24 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR2024P)&& maxPowerSource == SR2024_MAXPOWERSOURCE_DEFAULT) {
			configure.getPse().setMaxPowerSource(null);
		}
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR2124P && maxPowerSource == SR2124P_MAXPOWERSOURCE_DEFAULT) {
			configure.getPse().setMaxPowerSource(null);
		}
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR2148P && maxPowerSource == SR2148P_MAXPOWERSOURCE_DEFAULT) {
			configure.getPse().setMaxPowerSource(null);
		}
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR48 && maxPowerSource == SR2148P_MAXPOWERSOURCE_DEFAULT) {
			configure.getPse().setMaxPowerSource(null);
		}
	}
	
	//no usbmodem power enable
	//usbmodem.......
	//interface usbnet0 ......
	//routing route-map aaa via usbnet0
	//track-wan aaa interface usbnet0
	private void filterUsb0ForLteBR(Configuration configure, HiveAp hiveAp) {
		UsbmodemObj usbmodemObj = configure.getUsbmodem();
		if (usbmodemObj == null || usbmodemObj.getPower() == null || usbmodemObj.getPower().getEnable() != null 
			|| usbmodemObj.getPower().getEnable().getOperation() == AhEnumAct.YES) {
			return;
		}
		if (configure.getInterface() != null) {
			configure.getInterface().setUsbnet0(null);
		}
		
	}
	
	private Profile findRadioProfileByName(List<Profile> radioProfileList, String name) {
		if (radioProfileList == null || radioProfileList.size() == 0 || name == null) {
			return null;
		}
		for (Profile profile : radioProfileList) {
			if (name.equals(profile.getName())) {
				return profile;
			}
		}
		return null;
	}
	
	private void setRadioProfileForSensorMode(HiveAp hiveAp, Profile radioProfile, Wifi wifi) {
		radioProfile.setHighDensity(null);
		radioProfile.setSafetyNet(null);
		radioProfile.setBandSteering(null);
		radioProfile.setWeakSnrSuppress(null);
		radioProfile.setTransmitChain(null);
		radioProfile.setAmpdu(null);
		radioProfile.setAcsp(null);
		radioProfile.setDfs(null);
		radioProfile.setScan(null);
		radioProfile.setWmm(null);
		radioProfile.setBackhaul(null);
		radioProfile.setBeaconPeriod(null);
		radioProfile.setBenchmark(null);
		radioProfile.setClientLoadBalance(null);
		radioProfile.setDetectBssidSpoofing(null);
		radioProfile.setMaxClient(null);
		radioProfile.setShortGuardInterval(null);
		radioProfile.setShortPreamble(null);
		radioProfile.setLoadBalance(null);
		radioProfile.setDenyClient(null);
		if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.3.0") < 0){
			wifi.setWlanIdp(null);
		}
	}
	
	//interface wifi0|wifi1 mode sensor 
	//radio profile <string> high-density|safety-net|band-steering|weak-snr-suppress|transmit-chain|ampdu|acsp|dfs|scan|wmm
	//radio profile <string> backhaul|beacon-period|benchmark|client-load-balance|detect-bssid-spoofing|max-client|short-guard-interval
	//radio profile <string> short-preamble|tx-beamforming|load-balance|deny-client
	//for BR100: when not sensor mode, should filter presence clis
	//6.1r3 devices supports "interface wifix wlan-idp profile <string>"
	//for all device: when not sensor mode, should filter sensor related clis
	private void filterObjForSensorMode(Configuration configure, HiveAp hiveAp) {
		if (configure.getRadio() == null) {
			return;
		}
		List<Profile> radioProfileList = configure.getRadio().getProfile();
		if (radioProfileList == null || radioProfileList.size() == 0) {
			return;
		}
		Wifi wifi0 = configure.getInterface().getWifi0();
		Wifi wifi1 = configure.getInterface().getWifi1();
		Profile wifi0RadioProfile = null;
		Profile wifi1RadioProfile = null;
		if (wifi0 != null && wifi0.getMode() != null) {
			wifi0RadioProfile = findRadioProfileByName(radioProfileList, wifi0.getRadio().getProfile().getName());
		}
		if (wifi1 != null && wifi1.getMode() != null) {
			wifi1RadioProfile = findRadioProfileByName(radioProfileList, wifi1.getRadio().getProfile().getName());
		}
		
		if (wifi0RadioProfile != null && wifi0.getMode().getSensor() != null) {
			setRadioProfileForSensorMode(hiveAp, wifi0RadioProfile, wifi0);
		}
		if (wifi1RadioProfile != null && wifi1.getMode().getSensor() != null) {
			setRadioProfileForSensorMode(hiveAp, wifi1RadioProfile, wifi1);
		}
		
		//when not sensor mode, should filter below clis
		if (wifi0RadioProfile != null && wifi0.getMode().getSensor() == null) {
			wifi0RadioProfile.setSensor(null);
			if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100) {
				wifi0RadioProfile.setPresence(null);
			}
		}
		
		if (wifi1RadioProfile != null && wifi1.getMode().getSensor() == null) {
			wifi1RadioProfile.setSensor(null);
		}
		
	}
	
	private void specialOperateForTree(Configuration configure) {
		List<ServiceObj> serviceList = configure.getService();
		if (serviceList == null || serviceList.isEmpty()) {
			return;
		}
		if (configure.getQos() == null) {
			configure.setQos(new QosObj());
		}
		if (configure.getQos().getClassifierMap() == null) {
			configure.getQos().setClassifierMap(new QosObj.ClassifierMap());
		}
		List<QosMapService> qosServiceList = configure.getQos()
				.getClassifierMap().getService();
		for (ServiceObj serviceObj : serviceList) {
			boolean isFound = false;
			for (QosMapService qosServiceObj : qosServiceList) {
				if (serviceObj.getName() != null
						&& serviceObj.getName().equals(qosServiceObj.getName())) {
					isFound = true;
				}
			}
			if (!isFound) {
				QosMapService qosServiceObj_2 = new QosMapService();
				qosServiceObj_2.setName(serviceObj.getName());
				qosServiceObj_2.setOperation(CLICommonFunc
						.getAhEnumActValue(CLICommonFunc.getYesDefault()));
				configure.getQos().getClassifierMap().getService().add(
						qosServiceObj_2);
			}
		}
	}

	
	
	
	
	 
	public static void main(String[] args) {
		DependencyHandler obj = new DependencyHandler();
		Configuration configure = new Configuration();
		HiveAp hiveAp = new HiveAp();
		hiveAp.setSoftVer("6.1.3.0");
		
		PseObj pse = new PseObj();
		PseProfile profile = new PseProfile();
		PseProfilePowerMode powerMode = new PseProfilePowerMode();
		powerMode.setValue("802.3at");
		powerMode.setPowerLimit(CLICommonFunc.createAhIntActObj(32000, true));
		
		profile.setName("aaa");
		profile.setPowerMode(powerMode);
		pse.getProfile().add(profile);
		
		configure.setPse(pse);
		System.out.println(pse.getProfile().get(0).getPowerMode());
		obj.filterDefaultPseProfileValue(configure, hiveAp);
		System.out.println(pse.getProfile().get(0).getPowerMode());

		//test filter sensor mode
//		InterfaceObj interfaceObj = new InterfaceObj();
//		
//		Wifi wifi0 = new Wifi();
//		Wifi wifi1 = new Wifi();
//		WifixMode wifixMode0 = new WifixMode();
//		wifixMode0.setAccess("1");
//		wifi0.setMode(wifixMode0);
//		wifi0.setWlanIdp(new Wifi.WlanIdp());
//		Wifi.Radio radio0 = new Wifi.Radio();
//		radio0.setProfile(CLICommonFunc.createAhNameActObj("radio0", true));
//		wifi0.setRadio(radio0);
//		
//		
//		WifixMode wifixMode1 = new WifixMode();
//		wifixMode1.setSensor("1");
//		wifi1.setMode(wifixMode1);
//		wifi1.setWlanIdp(new Wifi.WlanIdp());
//		Wifi.Radio radio1 = new Wifi.Radio();
//		radio1.setProfile(CLICommonFunc.createAhNameActObj("radio1", true));
//		wifi1.setRadio(radio1);
//		
//		interfaceObj.setWifi0(wifi0);
//		interfaceObj.setWifi1(wifi1);
//		
//		configure.setInterface(interfaceObj);
//		
//		RadioObj radioObj = new RadioObj();
//		RadioObj.Profile wifi0Profile = new RadioObj.Profile();
//		wifi0Profile.setName("radio1");
//		RadioObj.Profile wifi1Profile = new RadioObj.Profile();
//		wifi1Profile.setName("radio2");
//		radioObj.getProfile().add(wifi0Profile);
//		radioObj.getProfile().add(wifi1Profile);
//		configure.setRadio(radioObj);
//		
//
//		obj.filterObjForSensorMode(configure, hiveAp);
//		System.out.println(configure.getInterface().getWifi0().getWlanIdp());
//		System.out.println(configure.getInterface().getWifi1().getWlanIdp());
	}

}
