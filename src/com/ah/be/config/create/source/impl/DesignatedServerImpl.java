package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.DesignatedServerInt;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;

@SuppressWarnings("static-access")
@Deprecated
public class DesignatedServerImpl implements DesignatedServerInt {
	
	private HiveAp hiveAp;
	
	private boolean isProxyNetwork = false;
	
	public DesignatedServerImpl(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
		
		if(hiveAp.getConfigTemplate() != null){
			if(hiveAp.getConfigTemplate().getSsidInterfaces() != null && !isProxyNetwork){
				for(ConfigTemplateSsid ssidTemp : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
					if(ssidTemp.getSsidProfile() == null){
						continue;
					}
					if(ssidTemp.getSsidProfile().isEnabledIDM()){
						isProxyNetwork = true;
						break;
					}
				}
			}
			
			PortGroupProfile portGroup = hiveAp.getPortGroup();
			if(portGroup != null && portGroup.getBasicProfiles() != null && !isProxyNetwork){
				for(PortBasicProfile baseProfile : portGroup.getBasicProfiles()){
					if(baseProfile == null || baseProfile.getAccessProfile() == null){
						continue;
					}
					if(baseProfile.getAccessProfile().isEnabledIDM()){
						isProxyNetwork = true;
						break;
					}
				}
			}
		}
	}

//	public boolean isEnableIdmProxyAnnounce() {
//		return isProxyNetwork && hiveAp.isIDMProxy();
//	}
//
//	public boolean isEnableIdmProxyDynamic() {
//		return isProxyNetwork;
//	}

}
