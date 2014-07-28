package com.ah.be.config.create.source.impl;

import java.io.IOException;

import com.ah.be.common.NmsUtil;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.CapwapProfileInt;
import com.ah.be.config.create.source.common.HmServiceSettingService;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.bo.admin.CapwapSettings;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.VpnService;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author zhang
 * @version 2008-1-3 02:15:06
 */
@SuppressWarnings("static-access")
public class CapwapProfileImpl implements CapwapProfileInt {
	
	private static final Tracer log = new Tracer(CapwapProfileImpl.class
			.getSimpleName());

	private final HiveAp hiveAp;
	private final CapwapSettings capwapSetingObj;
	private boolean enableAutoDiscovery;

	public CapwapProfileImpl(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
		capwapSetingObj = MgrUtil.getQueryEntity().executeQuery(
				CapwapSettings.class, null, null).get(0);
		enableAutoDiscovery = new HmServiceSettingService().getHmStartConfig(hiveAp.getOwner().getId()).isEnableAutoDiscovery();
	}
	
	public String getCapwapGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.hmServices");
	}

	public String getUpdateTime() {
		return CLICommonFunc.getLastUpdateTime(null);
	}

//	public boolean isConfigCapwap() {
//		return !CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())
//				&& CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion());
//	}

	public String getApVersion() {
		return hiveAp.getSoftVer();
	}

	public boolean isConfigCapwapPrimary() {
		String ipaddress = getCapwapIpPrimary();
		return ipaddress != null
				&& !"".equals(ipaddress);
	}
	
	public boolean isConfigCapwapBackup() {
		String ipaddress = getCapwapIpBackup();
		return ipaddress != null
				&& !"".equals(ipaddress);
	}

	public String getCapwapIpPrimary() {
		return NmsUtil.getCapwapServer(hiveAp, true);
	}
	
	public String getCapwapIpBackup() {
		return NmsUtil.getCapwapServer(hiveAp, false);
	}
	
//	public boolean isConfigCapwapPort(){
//		if(NmsUtil.compareSoftwareVersion("3.5.3.0", hiveAp.getSoftVer()) > 0){
//			return false;
//		}
//		int port = this.getServerPort();
//		if(port == 1222 || port == 80){
//			return false;
//		}
//		return true;
//	}
	
	public boolean isConfigCapwapPort(){
		if(hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_TCP){
			return false;
		}else{
			return true;
		}
	}

	public int getServerPort() {
		if(hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_TCP && hiveAp.getTransferPort() > 0){
			return hiveAp.getTransferPort();
		}else{
			return capwapSetingObj.getUdpPort();
		}
	}

	public int getCwpHeartbeatInterval() {
		return capwapSetingObj.getTimeOut();
	}

	public int getCwpDeadInterval() {
		return capwapSetingObj.getNeighborDeadInterval();
	}

	public boolean isEnableCwpDtls() {
		return capwapSetingObj.getDtlsCapability() != CapwapSettings.DTLS_NODTLS;
	}

	public boolean isConfigCwpDtlsBootPassPhrase() {
		return (capwapSetingObj.getBootStrap() != null && !capwapSetingObj
				.getBootStrap().equals(""));
	}

	public boolean isConfigHmDefinedPassphrase() {
		return hiveAp.getPassPhrase() != null
				&& !"".equals(hiveAp.getPassPhrase());
	}

	public String getHmDefinedPassphraseValue() throws IOException {
		return AhConfigUtil.hiveApCommonEncrypt(hiveAp.getPassPhrase());
	}

	public int getHmDefinedPassphraseKey() {
		return hiveAp.getKeyId();
	}

	public String getCwpDtlsBootPassPhrase() {
		return capwapSetingObj.getBootStrap();
	}
	
	public boolean isEnableVpnTunnel(String serverAddr) throws CreateXMLException{
		if(hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT){
			VpnService vpnClient = hiveAp.getConfigTemplate().getVpnService();
			if(vpnClient == null){
				return false;
			}else{
				if(vpnClient.isCapwapThroughTunnel()){
					if(!CLICommonFunc.isIpAddress(serverAddr)){
						String errMsg = NmsUtil.getUserMessage("error.be.config.create.VPNPermitIp", new String[]{"Capwap"});
						log.error("CapwapProfileImpl.isEnableVpnTunnel", errMsg);
						throw new CreateXMLException(errMsg);
					}
					return true;
				}else{
					return false;
				}
			}
		}else{
			return false;
		}
	}
	
	public String getVhmValue(){
		return hiveAp.getOwner().getDomainName();
	}
	
	public boolean isConfigPCI(){
		return hiveAp.getConfigTemplate().getMgmtServiceOption() != null;
	}
	
	public boolean isEnablePCI(){
		return hiveAp.getConfigTemplate().getMgmtServiceOption().isEnablePCIData();
	}

	@Override
	public boolean isEnableAutoDiscovery() {
		return enableAutoDiscovery;
	}

}