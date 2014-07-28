package com.ah.be.config.create.source.impl.branchRouter;

import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.be.config.create.source.impl.ConfigureProfileFunction;
import com.ah.be.config.create.source.impl.VPNProfileImpl;
import com.ah.be.config.create.source.impl.cvg.VPNProfileCVGImpl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.RoutingPolicy;
import com.ah.bo.network.VpnGatewaySetting;
import com.ah.bo.network.VpnService;

public class VPNProfileBRImpl extends VPNProfileCVGImpl {
	
	private RoutingPolicy policyBo;
	private InterfaceProfileInt interfaceProfileInt;

	public VPNProfileBRImpl(HiveAp hiveAp, ConfigureProfileFunction function, boolean view) throws Exception{
		super(hiveAp, view);
		
		this.interfaceProfileInt = function.getInterfaceProfileImpl();
		policyBo = hiveAp.getRoutingPolicy();
		if(policyBo == null){
			policyBo = hiveAp.getConfigTemplate().getRoutingPolicy();
		}
	}
	
	public boolean isVPNServer(){
		return hiveAp.isVpnGateway() && 
				vpnProfile != null &&
				vpnProfile.getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_3;
	}
	
	public boolean isConfigVpn(){
		return super.isConfigVpn() || getL3TunnelExceptionSize() > 0;
	}
	
	public int getL3TunnelExceptionSize(){
		if(policyBo == null || policyBo.getDomainObjectForDesList() == null || 
				policyBo.getDomainObjectForDesList().getItems() == null){
			return 0;
		}else{
			return policyBo.getDomainObjectForDesList().getItems().size();
		}
	}
	
	public String getL3TunnelException(int index){
		return policyBo.getDomainObjectForDesList().getItems().get(index).getDomainName();
	}
	
	public int getNatPolicySize() {
		return interfaceProfileInt.getNatPolicyNameForSubNetworkSize();
	}
	
	public String getNaPolicyName(int index) {
		return interfaceProfileInt.getNatPolicyNameForSubNetwork(index);
	}
}
