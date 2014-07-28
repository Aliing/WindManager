package com.ah.ui.actions.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.CompliancePolicy;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.performance.ComplianceResult;
import com.ah.bo.performance.ComplianceSsidListInfo;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.MgrUtil;

public class ReportServiceFilter {
	public static final String KET_ETH0 = "key_eth0";
	public static final String KET_ETH0_BACK = "key_eth0_back";
	public static final String KET_ETH1 = "key_eth1";
	public static final String KET_ETH1_BACK = "key_eth1_back";
	public static final String KET_ETH2 = "key_eth2";
	public static final String KET_ETH2_BACK = "key_eth2_back";
	public static final String KET_ETH3 = "key_eth3";
	public static final String KET_ETH3_BACK = "key_eth3_back";
	public static final String KET_ETH4 = "key_eth4";
	public static final String KET_ETH4_BACK = "key_eth4_back";
	public static final String KET_WIRE_BACK = "key_wire_back";
	public static final String KET_RED0 = "key_red0";
	public static final String KET_RED0_BACK = "key_red0_back";
	public static final String KET_AGG0 = "key_agg0";
	public static final String KET_AGG0_BACK = "key_agg0_back";

	private final HiveAp hiveAp;
	private final Map<String, ServiceFilter> mapServiceFilterMap;
	private CompliancePolicy compliancePolicy;
	private List<ComplianceSsidListInfo> retList;

	public ReportServiceFilter(HiveAp ap, CompliancePolicy compliancePolicy) {
		this.hiveAp = ap;
		
		if (compliancePolicy==null) {
			this.compliancePolicy = new CompliancePolicy();
		} else {
			this.compliancePolicy = compliancePolicy;
		}
		this.mapServiceFilterMap = new HashMap<String, ServiceFilter>();
		retList = new ArrayList<ComplianceSsidListInfo>();
	}

	public HiveAp getHiveAp() {
		return hiveAp;
	}

	public Map<String, ServiceFilter> getMapServiceFilterMap() {
		return mapServiceFilterMap;
	}
	
	private ComplianceSsidListInfo getComplianceSsidListInfo(
			ServiceFilter serviceFilter, ServiceFilter serviceFilterBack,
			CompliancePolicy compliancePolicy,String name, int accessType){
		ComplianceSsidListInfo ssidListInfo = new ComplianceSsidListInfo();
		ssidListInfo.setSsidName(name);
		if (accessType>=0) {
			ssidListInfo.setSsidMethod(accessType);
		}
		ssidListInfo.setRating(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		if (serviceFilter==null && serviceFilterBack == null) {
			ssidListInfo.setBlnSsh(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
			ssidListInfo.setBlnPing(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
			ssidListInfo.setBlnTelnet(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
			ssidListInfo.setBlnSnmp(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
			return ssidListInfo;
		}
		
		if (serviceFilter.getEnableSSH()|| serviceFilterBack.getEnableSSH()){
			ssidListInfo.setBlnSsh(compliancePolicy.getHiveApSsh());
		} else {
			ssidListInfo.setBlnSsh(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		if (serviceFilter.getEnablePing()|| serviceFilterBack.getEnablePing()){
			ssidListInfo.setBlnPing(compliancePolicy.getHiveApPing());
		} else {
			ssidListInfo.setBlnPing(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		if (serviceFilter.getEnableTelnet()|| serviceFilterBack.getEnableTelnet()){
			ssidListInfo.setBlnTelnet(compliancePolicy.getHiveApTelnet());
		} else {
			ssidListInfo.setBlnTelnet(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		if (serviceFilter.getEnableSNMP() || serviceFilterBack.getEnableSNMP()){
			ssidListInfo.setBlnSnmp(compliancePolicy.getHiveApSnmp());
		} else {
			ssidListInfo.setBlnSnmp(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		return ssidListInfo;
	}

	public List<ComplianceSsidListInfo> initServiceFilter() {
		if (!getHiveAp().getConfigTemplate().isOverrideTF4IndividualAPs()
				&& getHiveAp().getConfigTemplate().getDeviceServiceFilter() != null) {
			ServiceFilter defFilter = getHiveAp().getConfigTemplate()
					.getDeviceServiceFilter();
			getHiveAp().getConfigTemplate().setEth0ServiceFilter(defFilter);
			getHiveAp().getConfigTemplate().setEth0BackServiceFilter(defFilter);
			getHiveAp().getConfigTemplate().setWireServiceFilter(defFilter);
			getHiveAp().getConfigTemplate().setEth1ServiceFilter(defFilter);
			getHiveAp().getConfigTemplate().setEth1BackServiceFilter(defFilter);
			getHiveAp().getConfigTemplate().setRed0ServiceFilter(defFilter);
			getHiveAp().getConfigTemplate().setRed0BackServiceFilter(defFilter);
			getHiveAp().getConfigTemplate().setAgg0ServiceFilter(defFilter);
			getHiveAp().getConfigTemplate().setAgg0BackServiceFilter(defFilter);
		}
		boolean eth1Available= getHiveAp().isEth1Available();
		if (isAp()) {
			ComplianceSsidListInfo ssidListInfo = getComplianceSsidListInfo(
					getHiveAp().getConfigTemplate().getEth0ServiceFilter(),
					getHiveAp().getConfigTemplate().getEth0BackServiceFilter()
					,compliancePolicy,"eth0", -1);
			retList.add(ssidListInfo);
			if (eth1Available) {
				ssidListInfo = getComplianceSsidListInfo(
						getHiveAp().getConfigTemplate().getEth1ServiceFilter(),
						getHiveAp().getConfigTemplate().getEth1BackServiceFilter()
						,compliancePolicy,"eth1", -1);
				retList.add(ssidListInfo);
				if (this.getHiveAp().getDeviceInfo().getIntegerValue(DeviceInfo.SPT_ETHERNET_COUNTS) == 2){
					ssidListInfo = getComplianceSsidListInfo(
							getHiveAp().getConfigTemplate().getRed0ServiceFilter(),
							getHiveAp().getConfigTemplate().getRed0BackServiceFilter()
							,compliancePolicy,"red0", -1);
					retList.add(ssidListInfo);
					ssidListInfo = getComplianceSsidListInfo(
							getHiveAp().getConfigTemplate().getAgg0ServiceFilter(),
							getHiveAp().getConfigTemplate().getAgg0BackServiceFilter()
							,compliancePolicy,"agg0", -1);
					retList.add(ssidListInfo);
				}
			}
		} else if (isApasBr()) {
			ComplianceSsidListInfo ssidListInfo = getComplianceSsidListInfo(
					null,null ,compliancePolicy,"eth0", ComplianceSsidListInfo.ETH_WAN);
			retList.add(ssidListInfo);
			setEthServiceFilter();
			
			ssidListInfo = getComplianceSsidListInfo(
					 mapServiceFilterMap.get(KET_ETH1),mapServiceFilterMap.get(KET_ETH1),
					 compliancePolicy,"eth1", -1);
			retList.add(ssidListInfo);
			// todo  add eth1
		} else if (isBr()) {
			ComplianceSsidListInfo ssidListInfo = getComplianceSsidListInfo(
					null,null ,compliancePolicy,"eth0", ComplianceSsidListInfo.ETH_WAN);
			retList.add(ssidListInfo);
			
			setEthServiceFilter();
			if (mapServiceFilterMap.get(KET_ETH1_BACK)!=null) {
				ssidListInfo = getComplianceSsidListInfo(
						 null,null,
						 compliancePolicy,"eth1", ComplianceSsidListInfo.ETH_WAN);
				retList.add(ssidListInfo);
			} else {
				if (mapServiceFilterMap.get(KET_ETH1)!=null) {
					ssidListInfo = getComplianceSsidListInfo(
							 mapServiceFilterMap.get(KET_ETH1),mapServiceFilterMap.get(KET_ETH1),
							 compliancePolicy,"eth1", -1);
					retList.add(ssidListInfo);
				}
			}
			if (mapServiceFilterMap.get(KET_ETH2_BACK)!=null) {
				ssidListInfo = getComplianceSsidListInfo(
						 null,null,
						 compliancePolicy,"eth2", ComplianceSsidListInfo.ETH_WAN);
				retList.add(ssidListInfo);
			} else {
				if (mapServiceFilterMap.get(KET_ETH2)!=null) {
					ssidListInfo = getComplianceSsidListInfo(
							 mapServiceFilterMap.get(KET_ETH2),mapServiceFilterMap.get(KET_ETH2),
							 compliancePolicy,"eth2", -1);
					retList.add(ssidListInfo);
				}
			}
			
			if (mapServiceFilterMap.get(KET_ETH3_BACK)!=null) {
				ssidListInfo = getComplianceSsidListInfo(
						 null,null,
						 compliancePolicy,"eth3", ComplianceSsidListInfo.ETH_WAN);
				retList.add(ssidListInfo);
			} else {
				if (mapServiceFilterMap.get(KET_ETH3)!=null) {
					ssidListInfo = getComplianceSsidListInfo(
							 mapServiceFilterMap.get(KET_ETH3),mapServiceFilterMap.get(KET_ETH3),
							 compliancePolicy,"eth3", -1);
					retList.add(ssidListInfo);
				}
			}
			
			if (mapServiceFilterMap.get(KET_ETH4_BACK)!=null) {
				ssidListInfo = getComplianceSsidListInfo(
						 null,null,
						 compliancePolicy,"eth4", ComplianceSsidListInfo.ETH_WAN);
				retList.add(ssidListInfo);
			} else {
				if (mapServiceFilterMap.get(KET_ETH4)!=null) {
					ssidListInfo = getComplianceSsidListInfo(
							 mapServiceFilterMap.get(KET_ETH4),mapServiceFilterMap.get(KET_ETH4),
							 compliancePolicy,"eth4", -1);
					retList.add(ssidListInfo);
				}
			}
		} else if (isBrAsAp()) {
			ComplianceSsidListInfo ssidListInfo = getComplianceSsidListInfo(
					getHiveAp().getConfigTemplate().getEth0ServiceFilter(),
					getHiveAp().getConfigTemplate().getEth0BackServiceFilter()
					,compliancePolicy,"eth0", -1);
			retList.add(ssidListInfo);
			
			setEthServiceFilter();
			if (mapServiceFilterMap.get(KET_ETH1_BACK)!=null) {
				ssidListInfo = getComplianceSsidListInfo(
						 null,null,
						 compliancePolicy,"eth1", ComplianceSsidListInfo.ETH_WAN);
				retList.add(ssidListInfo);
			} else {
				if (mapServiceFilterMap.get(KET_ETH1)!=null) {
					ssidListInfo = getComplianceSsidListInfo(
							 mapServiceFilterMap.get(KET_ETH1),mapServiceFilterMap.get(KET_ETH1),
							 compliancePolicy,"eth1", -1);
					retList.add(ssidListInfo);
				}
			}
			if (mapServiceFilterMap.get(KET_ETH2_BACK)!=null) {
				ssidListInfo = getComplianceSsidListInfo(
						 null,null,
						 compliancePolicy,"eth2", ComplianceSsidListInfo.ETH_WAN);
				retList.add(ssidListInfo);
			} else {
				if (mapServiceFilterMap.get(KET_ETH2)!=null) {
					ssidListInfo = getComplianceSsidListInfo(
							 mapServiceFilterMap.get(KET_ETH2),mapServiceFilterMap.get(KET_ETH2),
							 compliancePolicy,"eth2", -1);
					retList.add(ssidListInfo);
				}
			}
			
			if (mapServiceFilterMap.get(KET_ETH3_BACK)!=null) {
				ssidListInfo = getComplianceSsidListInfo(
						 null,null,
						 compliancePolicy,"eth3", ComplianceSsidListInfo.ETH_WAN);
				retList.add(ssidListInfo);
			} else {
				if (mapServiceFilterMap.get(KET_ETH3)!=null) {
					ssidListInfo = getComplianceSsidListInfo(
							 mapServiceFilterMap.get(KET_ETH3),mapServiceFilterMap.get(KET_ETH3),
							 compliancePolicy,"eth3", -1);
					retList.add(ssidListInfo);
				}
			}
			
			if (mapServiceFilterMap.get(KET_ETH4_BACK)!=null) {
				ssidListInfo = getComplianceSsidListInfo(
						 null,null,
						 compliancePolicy,"eth4", ComplianceSsidListInfo.ETH_WAN);
				retList.add(ssidListInfo);
			} else {
				if (mapServiceFilterMap.get(KET_ETH4)!=null) {
					ssidListInfo = getComplianceSsidListInfo(
							 mapServiceFilterMap.get(KET_ETH4),mapServiceFilterMap.get(KET_ETH4),
							 compliancePolicy,"eth4", -1);
					retList.add(ssidListInfo);
				}
			}
			
		}
		if (HiveAp.isWifi0Available(getHiveAp().getHiveApModel())) {
			addSSIDtoList();
		}
		
		return retList;
	}
	
	
	
	private void addSSIDtoList () {
		for(ConfigTemplateSsid configTemplateSsid :getHiveAp().getConfigTemplate().getSsidInterfaces().values()){
			if (configTemplateSsid.getSsidProfile()!=null){
				SsidProfile sp=configTemplateSsid.getSsidProfile();
				ComplianceSsidListInfo ssidListInfo = new ComplianceSsidListInfo();
				ssidListInfo.setSsidName(sp.getSsidName());
				switch (sp.getAccessMode()){
					case SsidProfile.ACCESS_MODE_OPEN:
						if (sp.getMacAuthEnabled()){
							ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_OPEN_AUTH);
							ssidListInfo.setRating(compliancePolicy.getClientOpenAuth());
						} else {
							ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_OPEN);
							ssidListInfo.setRating(compliancePolicy.getClientOpen());
						}
						break;
					case SsidProfile.ACCESS_MODE_WPA:
						ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_PSK);
						ssidListInfo.setRating(compliancePolicy.getClientPsk());
						if (!compliancePolicy.getPasswordSSID()){
							ssidListInfo.setSsidPass(ComplianceResult.PASSWORD_STRENGTH_NA);
						} else {
							int ssidPass = MgrUtil.checkPasswordStrength(sp.getSsidSecurity().getFirstKeyValue());
							ssidListInfo.setSsidPass(ssidPass);
						}
						break;
					case SsidProfile.ACCESS_MODE_PSK:
						ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_PRIVETE_PSK);
						ssidListInfo.setRating(compliancePolicy.getClientPrivatePsk());
						break;
					case SsidProfile.ACCESS_MODE_WEP:
						ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_WEP);
						ssidListInfo.setRating(compliancePolicy.getClientWep());
						break;
					case SsidProfile.ACCESS_MODE_8021X:
						ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_8021X);
						ssidListInfo.setRating(compliancePolicy.getClient8021x());
						break;
				}
				ServiceFilter serviceFilter = sp.getServiceFilter();
				if (serviceFilter.getEnableSSH()){
					ssidListInfo.setBlnSsh(compliancePolicy.getHiveApSsh());
				} else {
					ssidListInfo.setBlnSsh(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
				}
				if (serviceFilter.getEnablePing()){
					ssidListInfo.setBlnPing(compliancePolicy.getHiveApPing());
				} else {
					ssidListInfo.setBlnPing(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
				}
				if (serviceFilter.getEnableTelnet()){
					ssidListInfo.setBlnTelnet(compliancePolicy.getHiveApTelnet());
				} else {
					ssidListInfo.setBlnTelnet(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
				}
				if (serviceFilter.getEnableSNMP()){
					ssidListInfo.setBlnSnmp(compliancePolicy.getHiveApSnmp());
				} else {
					ssidListInfo.setBlnSnmp(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
				}
				retList.add(ssidListInfo);
			}
		}
	}
	

	private void setEthServiceFilter() {
		ServiceFilter firstFilter = null;
		mapServiceFilterMap.clear();
		// TODO Port Template Profiles
		if (hiveAp.getPortGroup() != null
				&& hiveAp.getPortGroup().getBasicProfiles() != null) {
			for (PortBasicProfile portBase : hiveAp.getPortGroup()
					.getBasicProfiles()) {
				boolean wanflag = false;
				PortAccessProfile accessProfile = portBase.getAccessProfile();
				if (accessProfile.getPortType()==PortAccessProfile.PORT_TYPE_WAN) {
					wanflag = true;
				}
				if (accessProfile.getServiceFilter() != null) {
					if (firstFilter == null) {
						firstFilter = accessProfile.getServiceFilter();
					}
					if (portBase.getETHs() != null) {
						for (int i = 0; i < portBase.getETHs().length; i++) {
							if ("1".equals(portBase.getETHs()[i])) {
								if (wanflag) {
									mapServiceFilterMap.put(KET_ETH1_BACK,
											accessProfile.getServiceFilter());
								} else {
									mapServiceFilterMap.put(KET_ETH1,
											accessProfile.getServiceFilter());
								}
							} else if ("2".equals(portBase.getETHs()[i])) {
								if (wanflag) {
									mapServiceFilterMap.put(KET_ETH2_BACK,
											accessProfile.getServiceFilter());
								} else {
									mapServiceFilterMap.put(KET_ETH2,
											accessProfile.getServiceFilter());
								}
							} else if ("3".equals(portBase.getETHs()[i])) {
								if (wanflag) {
									mapServiceFilterMap.put(KET_ETH3_BACK,
											accessProfile.getServiceFilter());
								} else {
									mapServiceFilterMap.put(KET_ETH3,
											accessProfile.getServiceFilter());
								}
							} else if ("4".equals(portBase.getETHs()[i])) {
								if (wanflag) {
									mapServiceFilterMap.put(KET_ETH4_BACK,
											accessProfile.getServiceFilter());
								} else {
									mapServiceFilterMap.put(KET_ETH4,
											accessProfile.getServiceFilter());
								}
							}
						}
					}
				}
			}
			if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100
					|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_330
					|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_350) {
				mapServiceFilterMap.put(KET_ETH1, firstFilter);
			}
		}
	}

	private boolean isApasBr() {
		if (HiveAp.isBR100LikeHiveAP(hiveAp.getHiveApModel())) {
			return false;
		}
		if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER
				|| hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_BR) {
			return true;
		}
		return false;
	}

	private boolean isBrAsAp() {
		return hiveAp.isBr100WorkAsAp();
	}

	private boolean isBr() {
		if (!HiveAp.isBR100LikeHiveAP(hiveAp.getHiveApModel())) {
			return false;
		}
		if (isBrAsAp()) {
			return false;
		}
		return true;
	}

	private boolean isAp() {
		if (HiveAp.isBR100LikeHiveAP(hiveAp.getHiveApModel())) {
			return false;
		}
		if (isApasBr()) {
			return false;
		}
		return true;
	}
}
