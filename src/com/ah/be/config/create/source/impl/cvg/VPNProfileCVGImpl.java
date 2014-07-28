package com.ah.be.config.create.source.impl.cvg;

import com.ah.be.config.create.source.impl.VPNProfileImpl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.VpnGatewaySetting;
import com.ah.bo.network.VpnService;

public class VPNProfileCVGImpl extends VPNProfileImpl {
	
	public VPNProfileCVGImpl(HiveAp hiveAp, boolean view) throws Exception{
		super(hiveAp, view);
	}
	
	public boolean isVPNServer(){
		boolean isServer = false;
		if(hiveAp.isVpnGateway() && 
				vpnProfile != null &&
				vpnProfile.getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_3 &&
				vpnProfile.getVpnGateWaysSetting() != null){
			for(VpnGatewaySetting vpnSetting : vpnProfile.getVpnGateWaysSetting()){
				if(hiveAp.getId().equals(vpnSetting.getApId())){
					isServer = true;
					break;
				}
			}
		}
		return isServer;
	}
	
	public boolean isVPNClient(){
		return hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER && 
				vpnProfile != null &&
				vpnProfile.getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_3;
	}
	
	public String getClientPoolIpRange(){
		return null;
	}
	
	public String getClientPoolNetMask(){
		return null;
	}
	
	public String getVPNServerName(){
		int seq = 0;
		for(VpnGatewaySetting cvgObj : vpnProfile.getVpnGateWaysSetting()){
			if(this.hiveAp.getId().equals(cvgObj.getApId())){
				if(seq == 0){
					return hiveAp.getMacAddress() + "_primaryServer";
				}else if(seq == 1){
					return hiveAp.getMacAddress() + "_backupServer";
				}
			}
			seq++;
		}
		return null;
	}
	
	public int getVPNClientIpsecSize(){
		if(vpnProfile == null || vpnProfile.getVpnGateWaysSetting() == null){
			return 0;
		}else{
			return vpnProfile.getVpnGateWaysSetting().size();
		}
	}
	
	public String getVpnClientGateWay(int index){
		int seq = 0;
		for(VpnGatewaySetting cvgObj : vpnProfile.getVpnGateWaysSetting()){
			if(seq == index){
				return cvgObj.getExternalIpAddress();
			}
			seq ++;
		}
		return null;
	}
	
	public String getIpsecTunnelName(){
		int seq = 0;
		for(VpnGatewaySetting cvgObj : vpnProfile.getVpnGateWaysSetting()){
			if(this.hiveAp.getId().equals(cvgObj.getApId())){
				if(seq == 0){
					return hiveAp.getMacAddress() + "_primaryServer";
				}else if(seq == 1){
					return hiveAp.getMacAddress() + "_backupServer";
				}
			}
			seq++;
		}
		return null;
	}
	
	public int getClientTunnelSize(){
		if(vpnProfile.getVpnGateWaysSetting() == null){
			return 0;
		}else{
			return vpnProfile.getVpnGateWaysSetting().size();
		}
	}
	
	public String getIpsecName(int index){
		for(int seq=0; seq<vpnProfile.getVpnGateWaysSetting().size(); seq++){
			if(index == seq && index == 0){
				return hiveAp.getMacAddress()+VPNProfileImpl.IPSEC_1_SUFIX;
			}else if(index == seq && index == 1){
				return hiveAp.getMacAddress()+VPNProfileImpl.IPSEC_2_SUFIX;
			}
		}
		return null;
	}
	
	public boolean isPrimary(int index){
		return index == 0;
	}
}
