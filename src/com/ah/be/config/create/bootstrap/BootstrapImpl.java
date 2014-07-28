package com.ah.be.config.create.bootstrap;

import java.util.Collection;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.event.AhBootstrapGeneratedEvent;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.network.SingleTableItem;
import com.ah.util.MgrUtil;

@SuppressWarnings("static-access")
public class BootstrapImpl implements BootstrapInt, QueryBo {

	private final HiveAp hiveAp;
	private final HiveProfile hiveProfile;
	private final AhBootstrapGeneratedEvent bootStrapEvent;

	public BootstrapImpl(AhBootstrapGeneratedEvent event) {
		bootStrapEvent = event;
		hiveAp = MgrUtil.getQueryEntity().findBoById(HiveAp.class, event.getHiveAp()
				.getId(), this);
		hiveProfile = hiveAp.getConfigTemplate().getHiveProfile();
		event.setHiveAp(hiveAp);
		
//		//for BR100 limit 8 SSIDs
//		ConfigureProfileFunction.br100SsidLimit(hiveAp);
	}

	public String getApVersion() {
		return hiveAp.getSoftVer();
	}

	public String getHiveApHostName() {
		return hiveAp.getHostName();
	}

	public boolean isConfigureHiveProfile() {
		return hiveProfile != null && !hiveProfile.getDefaultFlag();
	}

	public String getHiveId() {
		return hiveAp.getConfigTemplate().getHiveProfile().getHiveName();
	}

	public int getHiveApNativeVlanId() throws CreateXMLException {
		int nativeVlan = hiveAp.getNativeVlan();
		if(nativeVlan > 0){
			return nativeVlan;
		}else{
			return CLICommonFunc.getVlan(hiveAp.getConfigTemplate().getVlanNative(), hiveAp).getVlanId();
		}
	}

	public boolean isConfigureMgtVlan() {
		return hiveAp.getConfigTemplate().getVlan() != null;
	}

	public int getHiveApMgtVlanId() throws CreateXMLException {
		SingleTableItem vlan = CLICommonFunc.getVlan(hiveAp.getConfigTemplate()
				.getVlan(), hiveAp);
		return vlan.getVlanId();
	}

	public boolean isConfigureHivePassword() {
		return hiveProfile.isEnabledPassword()
				&& hiveProfile.getHivePassword() != null
				&& hiveProfile.getHivePassword().equals("");
	}

//	public boolean isConfigCwp() {
//		return !CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())
//				&& CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion());
//	}

	public String getHivePassword() {
		return hiveProfile.getHivePassword();
	}
	
	public boolean isConfigSnmp(){
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
			return false;
		}
		String locationStr = getSnmpLocation();
		return locationStr != null && !"".equals(locationStr);
	}

	public String getSnmpLocation() {
		
		String location = "";
		if(hiveAp.getLocation() != null){
			location = hiveAp.getLocation().replace("@", "");
		}
		
		if(!hiveAp.isIncludeTopologyInfo()){
			return location + "@";
		}
		
		String topoMap;
		if (hiveAp.getMapContainer() != null
				&& hiveAp.getMapContainerName() != null
				&& !"".equals(hiveAp.getMapContainerName())) {
			topoMap = hiveAp.getMapContainerName();
		} else {
			topoMap = "";
		}

		return topoMap.startsWith("@") ? location + topoMap : location + "@"
				+ topoMap;
	}

	public String getRootAdminUser() {
		return bootStrapEvent.getAdminUser();
	}

	public String getRootAdminPassword() {
		return AhConfigUtil.hiveApUserPwdEncrypt(bootStrapEvent.getAdminPwd());
	}

	public boolean isConfigureRootAdmin() {
		return bootStrapEvent.getAdminUser() != null;
	}

	public boolean isConfigureRootPassWord() {
		return bootStrapEvent.getAdminPwd() != null;
	}

	public boolean isEnableCwpDtls() {
		return bootStrapEvent.isEnableDtls();
	}

	public boolean isConfigCwpDtlsBootPassPhrase() {
		return bootStrapEvent.getDtlsPassWord() != null
				&& !bootStrapEvent.getDtlsPassWord().equals("");
	}

	public String getCwpDtlsBootPassPhrase() {
		return bootStrapEvent.getDtlsPassWord();
	}

	public int getCwpHeartbeatInterval() {
		return bootStrapEvent.getEchoTimeOut();
	}

	public int getCwpServerPort() {
		return bootStrapEvent.getCwpUdpPort();
	}

//	public boolean isConfigHiveNativeVlan() {
//		if (CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())) {
//			return true;
//		} else if (CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())) {
//			return false;
//		} else {
//			return false;
//		}
//	}

//	public boolean isConfigMgtNativeVlan() {
//		return !CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())
//				&& CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion());
//	}

//	public int getMgtNativeVlan() {
//		return hiveAp.getNativeVlan();
//	}

	public int getCwpDeadInterval() {
		return bootStrapEvent.getDeadInterval();
	}

	public boolean isConfigCwpServerName() {
		return bootStrapEvent.getCapwapServer() != null
				&& !"".equals(bootStrapEvent.getCapwapServer()) &&
				NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.4.0.0") < 0;
	}
	
	public boolean isConfigCwpServerPrimary(){
		return bootStrapEvent.getCapwapServer() != null
				&& !"".equals(bootStrapEvent.getCapwapServer()) &&
				NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.4.0.0") >= 0;
	}
	
	public boolean isConfigCwpServerSecond(){
		return bootStrapEvent.getCapwapServerBackup() != null
				&& !"".equals(bootStrapEvent.getCapwapServerBackup()) &&
				NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.4.0.0") >= 0;
	}

	public String getCwpServerName() {
		return bootStrapEvent.getCapwapServer();
	}
	
	public String getCwpServerNameSecond(){
		return bootStrapEvent.getCapwapServerBackup();
	}
	
	public String getVhmName(){
		String vhmName = bootStrapEvent.getVhmName();
		if(vhmName == null || "".equals(vhmName)){
			vhmName = hiveAp.getOwner().getDomainName();
		}
		return vhmName;
	}
	
	public boolean isConfigBootParam(){
		return bootStrapEvent.isEnableNetdump();
	}
	
	public String getNetdumpServer(){
		return bootStrapEvent.getNetdumpServer();
	}
	
	public boolean isEnableNetdump(){
		return bootStrapEvent.isEnableNetdump();
	}
	
	private void loadConfigTemplate(HmBo bo){
		/** ConfigTemplate */
		if (bo instanceof ConfigTemplate) {
			ConfigTemplate configTemplate = (ConfigTemplate) bo;

			if (configTemplate.getHiveProfile() != null) {
				configTemplate.getHiveProfile().getId();
			}

			if (configTemplate.getVlan() != null) {
				configTemplate.getVlan().getId();
				if(null != configTemplate.getVlan().getItems())
					configTemplate.getVlan().getItems().size();
			}
			
			if (configTemplate.getVlanNative() != null)
				configTemplate.getVlanNative().getId();
			if(null != configTemplate.getVlanNative().getItems())
				configTemplate.getVlanNative().getItems().size();
			if (configTemplate.getHiveProfile() != null)
				configTemplate.getHiveProfile().getId();
		}
	}

	public Collection<HmBo> load(HmBo bo) {
		/** HiveAp */
		if (bo instanceof HiveAp) {
			HiveAp hiveApBo = (HiveAp) bo;
			if (hiveApBo.getConfigTemplate() != null)
				loadConfigTemplate(hiveApBo.getConfigTemplate());
			if (hiveApBo.getDownloadInfo() != null)
				hiveApBo.getDownloadInfo().getId();
			if (hiveApBo.getOwner() != null)
				hiveApBo.getOwner().getId();
		}

		return null;
	}

}