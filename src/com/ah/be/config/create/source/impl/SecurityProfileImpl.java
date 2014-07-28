package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.be.config.create.source.SecurityProfileInt;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.IdsPolicySsidProfile;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacFilterInfo;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.Vlan;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.xml.be.config.AhPermitDenyValue;

/**
 * 
 * @author zhang
 * 
 */
public class SecurityProfileImpl implements SecurityProfileInt {

	private static final Tracer logger = new Tracer(SecurityProfileImpl.class
			.getSimpleName());

	private final HiveAp hiveAp;

	private final List<MacFilterBind> macFilterBindList;
	private IdsPolicy idsPolicy;
	private List<Vlan> vlanList;
	private List<MacOrOui> macOrOuiList;
	private List<IdsPolicySsidProfile> idsSsids;

	public SecurityProfileImpl(HiveAp hiveAp) throws CreateXMLException {
		this.hiveAp = hiveAp;
		macFilterBindList = new ArrayList<MacFilterBind>();
		init();
//		idsPolicy = hiveAp.getConfigTemplate().getIdsPolicy();
		if(hiveAp.getConfigTemplate().getIdsPolicy() != null){
			idsPolicy =  hiveAp.getConfigTemplate().getIdsPolicy();
		}
		if (idsPolicy != null) {
			vlanList = new ArrayList<Vlan>(idsPolicy.getVlans());
			macOrOuiList = new ArrayList<MacOrOui>(idsPolicy.getMacOrOuis());
			idsSsids = idsPolicy.getIdsSsids();
		}
	}

	private void init() throws CreateXMLException {
		// mac-filter from hive
		HiveProfile hiveProfile = hiveAp.getConfigTemplate().getHiveProfile();

		if (hiveProfile != null) {
			SecurityProfileImpl.MacFilterBind macFilterBind = new SecurityProfileImpl.MacFilterBind();
			String hiveName = hiveProfile.getHiveName();
//			hiveName = hiveName.replace(" ", "");
//			hiveName = hiveName.replace("\\?", "");
			macFilterBind.setName(hiveName);
			macFilterBind.setDefaultAction(hiveProfile.getDefaultAction());

			if (hiveProfile.getMacFilters() != null && hiveProfile.getMacFilters().size() > 0) {
				for (MacFilter macFilter : hiveProfile.getMacFilters()) {
					if (macFilter.getFilterInfo() != null) {
						for (MacFilterInfo macInfo : macFilter.getFilterInfo()) {
							MacOrOui macOrOui = macInfo.getMacOrOui();
							if (macOrOui != null) {
								if (macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_ADDRESS) {
									macFilterBind.addMacFilterAddressList(macInfo);
								} else if (macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_OUI) {
									macFilterBind.addMacFilterOuiList(macInfo);
								}
							} else {
								logger.info("SecurityProfileImpl.inite()",
										"mac-filter from hive hiveAp: ["
												+ hiveAp.getHostName()
												+ "] cannot found suited "
												+ "mac address or mac OUI]");
							}
						}
					}
				}
			}
			macFilterBindList.add(macFilterBind);
		}

		// mac-filter from ssid
		Collection<ConfigTemplateSsid> ssidList = hiveAp.getConfigTemplate()
				.getSsidInterfaces().values();
		if (ssidList != null) {
			for (ConfigTemplateSsid ssidTemplate : ssidList) {
				if (!ssidTemplate.getInterfaceName().equalsIgnoreCase(InterfaceProfileInt.InterType.eth0.name()) && 
						!ssidTemplate.getInterfaceName().equalsIgnoreCase("wireless mesh") &&
						!ssidTemplate.getInterfaceName().equalsIgnoreCase(InterfaceProfileInt.InterType.eth1.name()) &&
						!ssidTemplate.getInterfaceName().equalsIgnoreCase(InterfaceProfileInt.InterType.eth2.name()) &&
						!ssidTemplate.getInterfaceName().equalsIgnoreCase(InterfaceProfileInt.InterType.eth3.name()) &&
						!ssidTemplate.getInterfaceName().equalsIgnoreCase(InterfaceProfileInt.InterType.eth4.name()) &&
						!ssidTemplate.getInterfaceName().equalsIgnoreCase(InterfaceProfileInt.InterType.agg0.name()) &&
						!ssidTemplate.getInterfaceName().equalsIgnoreCase(InterfaceProfileInt.InterType.red0.name())
					) {
					SsidProfile ssidProfile = ssidTemplate.getSsidProfile();
					if (ssidProfile != null) {
						SecurityProfileImpl.MacFilterBind macFilterBind = new SecurityProfileImpl.MacFilterBind();
						String ssidName = ssidProfile.getSsid();
//						ssidName = ssidName.replace(" ", "");
//						ssidName = ssidName.replace("\\?", "");
						macFilterBind.setName(ssidName);
						macFilterBind.setDefaultAction(ssidProfile
								.getDefaultAction());
						
						if (ssidProfile.getMacFilters() != null && ssidProfile.getMacFilters().size() > 0){
							for (MacFilter macFilter : ssidProfile.getMacFilters()) {
								if (macFilter.getFilterInfo() != null) {
									for (MacFilterInfo macInfo : macFilter
											.getFilterInfo()) {
										MacOrOui macOrOui = macInfo.getMacOrOui();
										if (macOrOui != null) {
											if (macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_ADDRESS) {
												macFilterBind
														.addMacFilterAddressList(macInfo);
											} else {
												macFilterBind
														.addMacFilterOuiList(macInfo);
											}
										} else {
											logger
													.info(
															"SecurityProfileImpl.inite()",
															"mac-filter from ssid:["
																	+ ssidProfile
																			.getSsidName()
																	+ "] hiveAp: ["
																	+ hiveAp
																			.getHostName()
																	+ "] cannot found suited mac address or mac OUI]");
										}

									}
								}
							}
						}
						macFilterBindList.add(macFilterBind);
					}
				}
			}
		}
		
		// mac-filter from Access-Console
		AccessConsole accessConsoleObj = hiveAp.getConfigTemplate().getAccessConsole();
		if(accessConsoleObj != null){
			if (accessConsoleObj.getMacFilters() != null
					&& accessConsoleObj.getMacFilters().size() > 0) {
				SecurityProfileImpl.MacFilterBind macFilterBind = new SecurityProfileImpl.MacFilterBind();
				macFilterBind.setName("AC_" + hiveAp.getMacAddress());
				macFilterBind.setDefaultAction(accessConsoleObj.getDefaultAction());

				for (MacFilter macFilter : accessConsoleObj.getMacFilters()) {
					if (macFilter.getFilterInfo() != null) {
						for (MacFilterInfo macInfo : macFilter.getFilterInfo()) {
							MacOrOui macOrOui = macInfo.getMacOrOui();
							if (macOrOui != null) {
								if (macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_ADDRESS) {
									macFilterBind.addMacFilterAddressList(macInfo);
								} else if (macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_OUI) {
									macFilterBind.addMacFilterOuiList(macInfo);
								}
							} else {
								logger.info("SecurityProfileImpl.inite()",
										"mac-filter from AccessConsole hiveAp: ["
												+ hiveAp.getHostName()
												+ "] cannot found suited "
												+ "mac address or mac OUI]");
							}
						}
					}
				}
				macFilterBindList.add(macFilterBind);
			}
		}
	}
	
	public String getMacFilterGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.macFilters");
	}
	
	public String getIdsGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.idsPolicies");
	}

	public String getApVersion() {
		return hiveAp.getSoftVer();
	}

	public HiveAp getHiveAp() {
		return this.hiveAp;
	}

	public boolean isConfigureSecurity() {
		return this.getMacFilterBindSize() > 0 || this.isConfigureWlanIdp();
	}

	public String getUpdateTime() {
		List<Object> securityIdpTime = new ArrayList<Object>();
		securityIdpTime.add(idsPolicy);
		if (idsPolicy != null) {
			if (idsPolicy.getMacOrOuis() != null) {
				securityIdpTime.addAll(idsPolicy.getMacOrOuis());
			}
			if (idsPolicy.getVlans() != null) {
				securityIdpTime.addAll(idsPolicy.getVlans());
			}
		}
		if (macFilterBindList != null) {
			for (MacFilterBind filterBind : macFilterBindList) {
				if (filterBind != null) {
					for (MacFilterInfo macAddrInfo : filterBind
							.getMacFilterList()) {
						if (macAddrInfo != null) {
							securityIdpTime.add(macAddrInfo.getMacOrOui());
						}
					}
					for (MacFilterInfo macOuiInfo : filterBind
							.getMacFilterOuiList()) {
						if (macOuiInfo != null) {
							securityIdpTime.add(macOuiInfo.getMacOrOui());
						}
					}
				}
			}
		}
		return CLICommonFunc.getLastUpdateTime(securityIdpTime);
	}

	public String getWlanIdpUpdateTime() {
		List<Object> wlanIdpTime = new ArrayList<Object>();
		wlanIdpTime.add(idsPolicy);
		if (idsPolicy != null) {
			if (idsPolicy.getMacOrOuis() != null) {
				wlanIdpTime.addAll(idsPolicy.getMacOrOuis());
			}
			if (idsPolicy.getVlans() != null) {
				wlanIdpTime.addAll(idsPolicy.getVlans());
			}
		}
		return CLICommonFunc.getLastUpdateTime(wlanIdpTime);
	}

	public int getMacFilterBindSize() {
		return macFilterBindList.size();
	}

	public String getMacFilterName(int index) {
		return macFilterBindList.get(index).getName();
	}

	public int getMacFilterAddressSize(int index) {
		return macFilterBindList.get(index).getMacFilterList().size();
	}

	public int getMacFilterOuiSize(int i) {
		return macFilterBindList.get(i).getMacFilterOuiList().size();
	}

	public String getMacAddress(int i, int j) throws CreateXMLException {
		SingleTableItem macOui = CLICommonFunc.getMacAddressOrOui(
				macFilterBindList.get(i).getMacFilterList().get(j)
						.getMacOrOui(), hiveAp);
		return CLICommonFunc.transFormMacAddrOrOui(macOui.getMacEntry());
	}

	public String getMacOui(int i, int j) throws CreateXMLException {
		SingleTableItem macOui = CLICommonFunc.getMacAddressOrOui(
				macFilterBindList.get(i).getMacFilterOuiList().get(j)
						.getMacOrOui(), hiveAp);
		return CLICommonFunc.transFormMacAddrOrOui(macOui.getMacEntry());
	}

	public boolean isConfigureWlanIdp() {
		return idsPolicy != null;
	}

	public String getWlanIdpProfileName() {
		return idsPolicy.getPolicyName();
	}

	public boolean isEnableWlanIdpAdhoc() {
		return idsPolicy.isNetworkDetectionEnable();
	}

//	public boolean isConfigureApDetection() {
//		return idsPolicy.isInNetworkEnable();
//	}

	public boolean isEnableShortBeacon() {
		return idsPolicy.isShortBeanchIntervalEnable();
	}

	public boolean isEnableShortPreamble() {
		return idsPolicy.isShortPreambleEnable();
	}

	public boolean isEnableWmm() {
		return idsPolicy.isWmmEnable();
	}

	public boolean isEnableConnected() {
		return idsPolicy.isInNetworkEnable();
	}

	public int getConnectedVlanSize() {
		return vlanList.size();
	}

	public int getConnectedVlanName(int index) throws Exception {
		SingleTableItem vlanObj = CLICommonFunc.getVlan(vlanList.get(index),
				hiveAp);
		return vlanObj.getVlanId();
	}

	public boolean isEnableApOui() {
		return idsPolicy.isOuiEnable();
	}

	public int getApOuiSize() {
		return macOrOuiList.size();
	}

	public String getApOuiAddress(int index) throws Exception {
		SingleTableItem macObj = CLICommonFunc.getMacAddressOrOui(macOrOuiList
				.get(index), hiveAp);
		return CLICommonFunc.transFormMacAddrOrOui(macObj.getMacEntry());
	}

	public boolean isEnableApPolicySsid() {
		return idsPolicy.isSsidEnable();
	}

	public int getApPolicySsidSize() {
		return idsSsids.size();
	}

	public String getApPolicySsidName(int index) {
		return idsSsids.get(index).getSsidProfile().getSsid();
	}

	public boolean isEnableEncryption(int index) {
		return idsSsids.get(index).isEncryptionEnable();
	}

	public boolean isConfigureEncryptionType(int index, String type) {
		int encryptionType = turnEncryptionType(type);
		return idsSsids.get(index).getEncryptionType() == encryptionType;
	}

	public boolean isAddressActionIsPermit(int i, int j) {
		return macFilterBindList.get(i).getMacFilterList().get(j)
				.getFilterAction() == SecurityProfileInt.MAC_FILTER_ACTION_PERMIT;
	}

	public boolean isOuiActionIsPermit(int i, int j) {
		return macFilterBindList.get(i).getMacFilterOuiList().get(j)
				.getFilterAction() == SecurityProfileInt.MAC_FILTER_ACTION_PERMIT;
	}

	private short turnEncryptionType(String type) {
		if (SecurityProfileInt.ENCRYPTION_TYPE_OPEN.equals(type)) {
			return IdsPolicySsidProfile.ENCRYPTION_TYPE_OPEN;
		} else if (SecurityProfileInt.ENCRYPTION_TYPE_WEP.equals(type)) {
			return IdsPolicySsidProfile.ENCRYPTION_TYPE_WEP;
		} else {
			return IdsPolicySsidProfile.ENCRYPTION_TYPE_WPA_WPA2;
		}
	}

	public AhPermitDenyValue getDefaultActionValue(int i) {
		if (macFilterBindList.get(i).getDefaultAction() == SecurityProfileInt.MAC_FILTER_ACTION_PERMIT) {
			return AhPermitDenyValue.PERMIT;
		}else if (macFilterBindList.get(i).getDefaultAction() == SecurityProfileInt.MAC_FILTER_ACTION_DENY) {
			return AhPermitDenyValue.DENY;
		} else {
			return AhPermitDenyValue.PERMIT;
		}
	}

	/**
	 * Inner class
	 * 
	 * @author zhang
	 * 
	 */
	private class MacFilterBind {

		private String name;
		private List<MacFilterInfo> macFilterList;
		private List<MacFilterInfo> macFilterOuiList;
		private int defaultAction;

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public List<MacFilterInfo> getMacFilterList() {
			if (macFilterList == null) {
				macFilterList = new ArrayList<MacFilterInfo>();
			}
			return macFilterList;
		}

		public List<MacFilterInfo> getMacFilterOuiList() {
			if (macFilterOuiList == null) {
				macFilterOuiList = new ArrayList<MacFilterInfo>();
			}
			return macFilterOuiList;
		}

		public void setDefaultAction(int defaultAction) {
			this.defaultAction = (short) defaultAction;
		}

		public int getDefaultAction() {
			return this.defaultAction;
		}

		public void addMacFilterAddressList(MacFilterInfo info)
				throws CreateXMLException {
			boolean isFound = false;
			String macAddValue = CLICommonFunc.getMacAddressOrOui(
					info.getMacOrOui(), hiveAp).getMacEntry();
			for (MacFilterInfo addrInfo : getMacFilterList()) {
				String macListValue = CLICommonFunc.getMacAddressOrOui(
						addrInfo.getMacOrOui(), hiveAp).getMacEntry();
				if (macAddValue.equals(macListValue)) {
					isFound = true;
					break;
				}
			}
			if (!isFound) {
				getMacFilterList().add(info);
			} else {
				logger.error("Profile [" + name
						+ "] already use the mac address ["
						+ info.getMacOrOui().getMacOrOuiName() + "]");
			}
		}

		public void addMacFilterOuiList(MacFilterInfo info)
				throws CreateXMLException {
			boolean isFound = false;
			String ouiAddValue = CLICommonFunc.getMacAddressOrOui(
					info.getMacOrOui(), hiveAp).getMacEntry();
			for (MacFilterInfo addrInfo : getMacFilterOuiList()) {
				String ouiListValue = CLICommonFunc.getMacAddressOrOui(
						addrInfo.getMacOrOui(), hiveAp).getMacEntry();
				if (ouiAddValue.equals(ouiListValue)) {
					isFound = true;
					break;
				}
			}
			if (!isFound) {
				getMacFilterOuiList().add(info);
			} else {
				logger.error("Profile [" + name
						+ "] already use the mac oui ["
						+ info.getMacOrOui().getMacOrOuiName() + "]");
			}
		}

//		public String getKey() {
//			return this.name;
//		}
	}
	
	public boolean isEnableApPolicy(){
		return idsPolicy.isRogueDetectionEnable();
	}
	
	public int getIDSPolicyPeriod(){
		return idsPolicy.getMitigatePeriod();
	}
	
	public int getIDSPolicyDuration(){
		return idsPolicy.getMitigateDuration();
	}
	
	public int getIDSPolicyQuietTime(){
		return idsPolicy.getMitigateQuiet();
	}
	
	public boolean isStaReportEnable(){
		return idsPolicy.isStaReportEnabled();
	}
	
	public int getStaReportDuration(){
		return idsPolicy.getStaReportDuration();
	}
	
	public int getStaReportInterval(){
		return idsPolicy.getStaReportInterval();
	}
	
	public int getStaReportAgeout(){
		return idsPolicy.getStaReportAgeout();
	}
	
	public int getMitigateDeauthTime(){
		return idsPolicy.getDeAuthTime();
	}

	public int getStaReportAgeTime(){
		return idsPolicy.getStaReportAgeTime();
	}
}