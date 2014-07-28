package com.ah.be.config.cli.generate.impl;

import org.apache.commons.lang3.StringUtils;

import com.ah.be.cloudauth.HmCloudAuthCertMgmtImpl;
import com.ah.be.cloudauth.IDMConfig;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.cli.generate.AbstractAutoAdaptiveCLIGenerate;
import com.ah.be.config.cli.generate.CLIConfig;
import com.ah.be.config.cli.generate.CLIGenResult;
import com.ah.be.config.cli.generate.CLIGenerateException;
import com.ah.be.config.cli.util.ConstraintCheckUtil;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.SsidProfile;

public class AAAImpl extends AbstractAutoAdaptiveCLIGenerate {
	
	private IDMConfig idmConfig;

	@Override
	public void init() throws CLIGenerateException {
		idmConfig = new HmCloudAuthCertMgmtImpl().getRadSecConfig(hiveAp.getOwner().getId());
	}
	
	@CLIConfig
	public CLIGenResult getIDMProxyClis(){
		if(!needIDMProxyClis()){
			return null;
		}
		
		CLIGenResult cliRes = new CLIGenResult();
		
		String serverName = "idmAuthServer";
		String idmServer = idmConfig.getIdmGatewayServer();
		int tlsPort = idmConfig.getTlsPort();
		
		if(!StringUtils.isEmpty(idmServer)){
			cliRes.add(AAA_RADIUS_SERVER_NAME_TLS, new Object[]{serverName, idmServer});
			
			Object[] realmArrays = new Object[]{"DEFAULT", "primary", serverName};
			if(ConstraintCheckUtil.checkConstraint("<6.1.2.0", hiveAp.getSoftVer())){
				cliRes.add(AAA_RADIUS_SERVER_PROXY_REALM, realmArrays);
			}else{
				cliRes.add(AAA_RADIUS_SERVER_PROXY_RADSEC_REALM, realmArrays);
			}
		}
		if(tlsPort >= 0){
			cliRes.add(AAA_RADIUS_SERVER_NAME_TLS_PORT, new Object[]{serverName, tlsPort});	
		}
		
		cliRes.add(AAA_RADIUS_SERVER_PROXY_RADSEC_DYNAMIC_AUTH_EXTENSION, new Object[]{true});
		cliRes.add(AAA_RADIUS_SERVER_PROXY_RADSEC_ENABLE, new Object[]{true});
		cliRes.add(DESIGNATED_SERVER_IDM_PROXY_ANNOUNCE, new Object[]{true});
		
		return cliRes;
	}
	
	@CLIConfig
	public CLIGenResult getIDMClientClis() {
		if(!isIDMNetworkPolicy()){
			return null;
		}
		
		CLIGenResult cliRes = new CLIGenResult();
		cliRes.add(DESIGNATED_SERVER_IDM_PROXY_DYNAMIC, new Object[]{true});
		if(isIdmWithOpenSsid()){
			cliRes.add(AAA_RADIUS_SERVER_PROXY_RADSEC_DYNAMIC_AUTH_EXTENSION, new Object[]{true});
		}
		return cliRes;
	}
	
	@CLIConfig
	public CLIGenResult getIDMAuthProxyClis() {
		if(!isIDMAuthProxy()){
			return null;
		}
		
		CLIGenResult cliRes = new CLIGenResult();
		
		String idmServer = idmConfig.getIdmGatewayServer();
		cliRes.add(AAA_RADIUS_SERVER_LOCAL_NAS_TLS, new Object[]{idmServer});
		cliRes.add(AAA_RADIUS_SERVER_PROXY_RADSEC_ENABLE, new Object[]{true});
		
		if(ConstraintCheckUtil.checkConstraint("<6.1.2.0", hiveAp.getSoftVer())){
			String localServerName = "local_radius_server";
			cliRes.add(AAA_RADIUS_SERVER_NAME, new Object[]{localServerName, hiveAp.getCfgIpAddress(), "aerohive"});
			cliRes.add(AAA_RADIUS_SERVER_PROXY_RADSEC_REALM, new Object[]{"DEFAULT", "primary", localServerName});
		}
		return cliRes;
	}
	
	/****************************************************************************************************************************/
	
	private boolean isIDMAuthProxy(){
		return NmsUtil.isVhmEnableIdm(hiveAp.getOwner().getId()) && 
				hiveAp.getRadiusServerProfile() != null && 
				hiveAp.isEnableIDMAuthProxy();
	}
	
	private boolean needIDMProxyClis(){
		return isIDMNetworkPolicy() && hiveAp.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_IDM_PROXY) &&
				(hiveAp.isIDMProxy() || ConstraintCheckUtil.checkConstraint(">=6.2.1.0", hiveAp.getSoftVer()) );
	}
	
	private boolean isIdmWithOpenSsid(){
		if(hiveAp.getConfigTemplate() != null && hiveAp.getConfigTemplate().getSsidInterfaces() != null){
			for(ConfigTemplateSsid tempSsid : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
				if(tempSsid.getSsidProfile() == null){
					continue;
				}
				SsidProfile ssidObj = tempSsid.getSsidProfile();
				if(ssidObj.getAccessMode() == SsidProfile.ACCESS_MODE_OPEN && 
						ssidObj.isEnabledIDM() && 
						ssidObj.getCwp() != null && 
						ssidObj.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isIDMNetworkPolicy() {
		boolean vhmEnable = NmsUtil.isVhmEnableIdm(hiveAp.getOwner().getId());
		if(!vhmEnable){
			return false;
		}
		
		boolean ssidEnable = false;
		if(hiveAp.getConfigTemplate() != null && hiveAp.getConfigTemplate().getSsidInterfaces() != null){
			for(ConfigTemplateSsid ssidTemp : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
				if(ssidTemp.getSsidProfile() == null){
					continue;
				}
				if(ssidTemp.getSsidProfile().isEnabledIDM()){
					ssidEnable = true;
					break;
				}
			}
		}
		
		boolean portEnable = false;
		PortGroupProfile portGroup = hiveAp.getPortGroup();
		if(portGroup != null && portGroup.getBasicProfiles() != null){
			for(PortBasicProfile baseProfile : portGroup.getBasicProfiles()){
				if(baseProfile == null || baseProfile.getAccessProfile() == null){
					continue;
				}
				if(baseProfile.getAccessProfile().isEnabledIDM()){
					portEnable = true;
					break;
				}
			}
		}
		
		return vhmEnable && (ssidEnable || portEnable);
	}
}
