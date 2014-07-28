package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.AlgProfileInt;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.AlgConfigurationInfo;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.ui.actions.Navigation;
import com.ah.util.MgrUtil;

/**
 * @author zhang
 * @version 2007-12-19 10:36:56
 */
@SuppressWarnings("static-access")
public class AlgProfileImpl implements AlgProfileInt {

	private HiveAp hiveAp;
	private AlgConfiguration algConfig;

	private AlgConfigurationInfo algFtp;
	private AlgConfigurationInfo algTftp;
	private AlgConfigurationInfo algSip;
	private AlgConfigurationInfo algDns;
	private AlgConfigurationInfo algHttp;
	
	private boolean isEnableOSDetection;

	public AlgProfileImpl(HiveAp hiveAp, boolean isEnableOSDetection) {
		this.hiveAp = hiveAp;
		this.isEnableOSDetection = isEnableOsDetectionAlgHttp(isEnableOSDetection);
		algConfig = hiveAp.getConfigTemplate().getAlgConfiguration();
		if (algConfig != null) {
			algFtp = algConfig.getItems().get(AlgConfigurationInfo.GatewayType.FTP.name());
			algTftp = algConfig.getItems().get(AlgConfigurationInfo.GatewayType.TFTP.name());
			algSip = algConfig.getItems().get(AlgConfigurationInfo.GatewayType.SIP.name());
			algDns = algConfig.getItems().get(AlgConfigurationInfo.GatewayType.DNS.name());
			algHttp = algConfig.getItems().get(AlgConfigurationInfo.GatewayType.HTTP.name());
		}

	}
	// fix bug 17778
	private boolean isEnableOsDetectionAlgHttp(boolean isEnableOSDetection){
		MgmtServiceOption mgmtService = hiveAp.getConfigTemplate().getMgmtServiceOption();
		if(mgmtService != null){
			return mgmtService.getOsDetectionMethod() != MgmtServiceOption.OS_DETECTION_METHOD_DHCP && isEnableOSDetection;
		}
		return false;
	}
	
	public String getWlanGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.networkPolicy");
	}
	
	public String getWlanName(){
		return hiveAp.getConfigTemplate().getConfigName();
	}
	
	public String getAlgGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.algConfiguration");
	}
	
	public String getAlgName(){
		if(algConfig != null){
			return algConfig.getConfigName();
		}else{
			return null;
		}
	}

	public String getApVersion() {
		return hiveAp.getSoftVer();
	}

	public boolean isConfigAlg(){
		return !hiveAp.isBranchRouter() && !hiveAp.isVpnGateway() && 
				(algFtp != null || algTftp != null || algSip != null || algDns != null || algHttp != null);
	}

	public String getUpdateTime() {
		List<Object> algTimeList = new ArrayList<Object>();
		algTimeList.add(algConfig);
		return CLICommonFunc.getLastUpdateTime(algTimeList);
	}

	private AlgConfigurationInfo getAlgConfigurationInfo(AlgType algType) {
		if (algType == AlgProfileInt.AlgType.ftp) {
			return this.algFtp;
		} else if (algType == AlgProfileInt.AlgType.tftp) {
			return this.algTftp;
		} else if (algType == AlgProfileInt.AlgType.sip) {
			return this.algSip;
		} else if (algType == AlgProfileInt.AlgType.dns) {
			return this.algDns;
		} else if (algType == AlgProfileInt.AlgType.http){
			return this.algHttp;
		} else {
			return null;
		}
	}

	public int getInactiveDataTimeout(AlgType algType) {
		return getAlgConfigurationInfo(algType).getTimeout();
	}

	public int getMaxDuration(AlgType algType) {
		return getAlgConfigurationInfo(algType).getDuration();
	}

	public int getQos(AlgType algType) {
		return getAlgConfigurationInfo(algType).getQosClass();
	}
	
	public boolean isAlgEnable(AlgType algType){
		if(algType == AlgType.http){
			//Teacher view must enable alg http
			String query = "select enableTeacher from " + HMServicesSettings.class.getSimpleName() + " bo where bo.owner.id = "+hiveAp.getOwner().getId();
			List<?> hmSettingList = MgrUtil.getQueryEntity().executeQuery(query, 1);
			if(hmSettingList != null && !hmSettingList.isEmpty() && 
					hiveAp.getDeviceInfo().isSptTeacherView() ){
				boolean isEnableTv = (Boolean)hmSettingList.get(0);
				if(isEnableTv){
					return true;
				}
			}
			
			//OS detection must enable alg http
			if(isEnableOSDetection){
				return true;
			}
		}else if(algType == AlgType.dns && isMDMEnable()){
			/** In the past, when MDM(Airwatch/Jamf/CM) was enabled, the DNS ALG was automatically enabled, but from 6.1r6 HM need push DNS ALG enable CLI to AP. */
			return true;
		}
		
		if(Navigation.isExpressHMMode(hiveAp.getOwner()) && 
				QosProfileImpl.isExistsVoiceSsid(hiveAp) && 
				algType != AlgType.dns && algType != AlgType.http){
			return true;
		}else{
			return getAlgConfigurationInfo(algType).isIfEnable();
		}
	}
	
	private boolean isMDMEnable(){
		if(hiveAp == null || hiveAp.getConfigTemplate() == null || 
				hiveAp.getConfigTemplate().getSsidInterfaces() == null || 
				NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.6.0") < 0){
			return false;
		}
		
		for(ConfigTemplateSsid ssidTemp : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			if(ssidTemp.getSsidProfile() == null){
				continue;
			}
			if(ssidTemp.getSsidProfile().isEnableMDM() && 
					ssidTemp.getSsidProfile().getConfigmdmId() != null){
				return true;
			}else if(ssidTemp.getSsidProfile().isEnabledSocialLogin()){
				return true;
			}else if(ssidTemp.getSsidProfile().isEnabledCM()){
				return true;
			}
		}
		return false;
	}

}