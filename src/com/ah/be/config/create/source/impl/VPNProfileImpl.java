package com.ah.be.config.create.source.impl;

import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.VPNProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.network.VpnService;
import com.ah.bo.network.VpnServiceCredential;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.coder.AhEncoder;
import com.ah.xml.be.config.IkeAuthMethodValue;
import com.ah.xml.be.config.IkeDhGroupValue;
import com.ah.xml.be.config.IkeHashValue;
import com.ah.xml.be.config.IkePfsGroupValue;
import com.ah.xml.be.config.VpnModeValue;

/**
 * @author zhang
 * @version 2009-4-28 10:13:59
 */

@SuppressWarnings("static-access")
public class VPNProfileImpl implements VPNProfileInt {
	
	private static final Tracer log = new Tracer(VPNProfileImpl.class.getSimpleName());
	
	public static final String IPSEC_1_SUFIX = "_client_ipsec_1";
	public static final String IPSEC_2_SUFIX = "_client_ipsec_2";
	
	public static String VPN_SERVICE_CREDENTIAL = "VPN_SERVICE_CREDENTIAL";
	
	public static final int L2_VPN_MAX_USER_FOR_AP = 128;
	
	protected VpnService vpnProfile;
	protected VpnServiceCredential vpnCredential = null;
	protected HiveAp hiveAp;
	
	public static final short VPN_SERVER_ONE = 1;
	public static final short VPN_SERVER_TWO = 2;
	protected short vpnServerIndex;
	
	protected List<VpnServiceCredential> vpnServerUserList;
	
	public static final String VPN_TUNNEL_POLICY_NAME = "vpn_tunnel_policy";
	
	public static final String clientListSuffix="_clientList";

	public VPNProfileImpl(HiveAp hiveAp, boolean view) throws Exception{
		this.hiveAp = hiveAp;
		vpnProfile = hiveAp.getConfigTemplate().getVpnService();
		
		if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP && hiveAp.isVpnServer() && vpnProfile != null){
			String ip1 = vpnProfile.getServerPrivateIp1();
			String ip2 = vpnProfile.getServerPrivateIp2();
			String mgtIp = hiveAp.getCfgIpAddress();
			if(mgtIp != null && mgtIp.equals(ip1)){
//				vpnServerIndex = VPN_SERVER_ONE;
				if(vpnProfile.getServerPublicIp1() != null && !"".equals(vpnProfile.getServerPublicIp1())){
					vpnServerIndex = VPN_SERVER_ONE;
				}else{
					String errMsg = NmsUtil.getUserMessage("error.be.config.create.noVpnServerPrimary", new String[]{vpnProfile.getProfileName()});
					log.error("VPNProfileImpl", errMsg);
					throw new CreateXMLException(errMsg);
				}
			}
			if(mgtIp != null && mgtIp.equals(ip2)){
//				vpnServerIndex = VPN_SERVER_TWO;
				if(vpnProfile.getServerPublicIp2() != null && !"".equals(vpnProfile.getServerPublicIp2())){
					vpnServerIndex = VPN_SERVER_TWO;
				}else{
					String errMsg = NmsUtil.getUserMessage("error.be.config.create.noVpnServerBackup", new String[]{vpnProfile.getProfileName()});
					log.error("VPNProfileImpl", errMsg);
					throw new CreateXMLException(errMsg);
				}
			}
			if(vpnServerIndex != VPN_SERVER_ONE && vpnServerIndex != VPN_SERVER_TWO){
				String errMsg = NmsUtil.getUserMessage("error.be.config.create.noVPNServer", new String[]{vpnProfile.getProfileName()});
				log.error("VPNProfileImpl", errMsg);
				throw new CreateXMLException(errMsg);
			}
			if(mgtIp != null && isL2Vpn()){
				long mgtAddr=AhEncoder.ip2Long(mgtIp);
				if(vpnServerIndex == VPN_SERVER_ONE){
					long ipStart=AhEncoder.ip2Long(vpnProfile.getClientIpPoolStart1());
					long ipEnd=AhEncoder.ip2Long(vpnProfile.getClientIpPoolEnd1());
					if(ipStart<=mgtAddr && ipEnd>=mgtAddr){
						String errMsg = NmsUtil.getUserMessage("error.be.config.create.sameHiveApMgt0", new String[]{vpnProfile.getProfileName()});
						log.error("VPNProfileImpl", errMsg);
						throw new CreateXMLException(errMsg);
					}
				}
				if(vpnServerIndex == VPN_SERVER_TWO){
					long ipStart=AhEncoder.ip2Long(vpnProfile.getClientIpPoolStart2());
					long ipEnd=AhEncoder.ip2Long(vpnProfile.getClientIpPoolEnd2());
					if(ipStart<=mgtAddr && ipEnd>=mgtAddr){
						String errMsg = NmsUtil.getUserMessage("error.be.config.create.sameHiveApMgt0", new String[]{vpnProfile.getProfileName()});
						log.error("VPNProfileImpl", errMsg);
						throw new CreateXMLException(errMsg);
					}
				}
			}
		}
		
		if(isVPNClient()){
			vpnCredential = initVpnCredential(vpnProfile, hiveAp.getMacAddress(), view);
		}else if(!view){
			removeVpnCredential(hiveAp.getMacAddress());
		}
	}
	
	protected synchronized VpnServiceCredential initVpnCredential(VpnService vpnProfile, String macAddress, boolean view) throws Exception{
		VpnServiceCredential resultObj = null;
		
		/** matching with mac address */
		for(VpnServiceCredential vpnCre : vpnProfile.getVpnCredentials()){
			if(vpnCre.isAllocated() && macAddress.equals(vpnCre.getAssignedClient())){
				resultObj = vpnCre;
				break;
			}
		}
		
		/** password is free, and there's no mac address */
		if(resultObj == null){
			if(view){
				for(VpnServiceCredential vpnCre : vpnProfile.getVpnCredentials()){
					if(vpnCre.isFree()){
						resultObj = vpnCre;
						break;
					}
				}
			}else{
				String updateSql = "update "+VPN_SERVICE_CREDENTIAL +
						" set allocatedStatus = :s1, assignedClient = :s2 " +
						" where vpn_service_id = :s3 and position in (select v2.position from "+VPN_SERVICE_CREDENTIAL+" as v2 " +
								"where vpn_service_id = :s3 and v2.allocatedStatus = :s4 limit 1)";
				
				String selectSql = "select c.credential, c.clientName, " +
						"c.allocatedStatus, c.assignedClient, " +
						"c.primaryRole, c.backupRole " +
						"from "+VpnService.class.getSimpleName()+" as v1 inner join v1.vpnCredentials as c";
				
				MgrUtil.getQueryEntity().executeNativeUpdate(updateSql, 
						new Object[]{VpnServiceCredential.ALLOCATED_STATUS_PRE_USE, macAddress, 
							vpnProfile.getId(), VpnServiceCredential.ALLOCATED_STATUS_FREE} );
				
				List<?> lists = MgrUtil.getQueryEntity().executeQuery(selectSql, null, 
						new FilterParams("v1.id = :s1 and c.assignedClient = :s2 and c.allocatedStatus = :s3",
								new Object[]{vpnProfile.getId(), macAddress, VpnServiceCredential.ALLOCATED_STATUS_PRE_USE}));
				
				if(!lists.isEmpty()){
					resultObj = new VpnServiceCredential();
					Object[] argRest = (Object[])lists.get(0);
					
					resultObj.setCredential((String)argRest[0]);
					resultObj.setClientName((String)argRest[1]);
					resultObj.setAllocatedStatus((short)argRest[2]);
					resultObj.setAssignedClient((String)argRest[3]);
					resultObj.setPrimaryRole((short)argRest[4]);
					resultObj.setBackupRole((short)argRest[5]);
				}
			}
		}
		
		if(resultObj == null){
			String[] errParams = {vpnProfile.getProfileName()};
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.noVpnUsers", errParams);
			log.error("getClientUserName", errMsg);
			throw new CreateXMLException(errMsg);
		}
		
		return resultObj;
		
	}
	
	protected void removeVpnCredential(String macAddress) throws Exception{
		String updateSql = "update "+VPN_SERVICE_CREDENTIAL+
				" set allocatedStatus = :s1 " +
				" where assignedClient = :s2 ";
		
		MgrUtil.getQueryEntity().executeNativeUpdate(updateSql, 
				new Object[]{VpnServiceCredential.ALLOCATED_STATUS_PRE_REMOVE, macAddress});
	}
	
	private boolean isL2Vpn(){
		return vpnProfile != null && vpnProfile.getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_2;
	}
	
	public boolean isVPNServer(){
		return hiveAp.isVpnServer() && (vpnServerIndex == VPN_SERVER_ONE || vpnServerIndex == VPN_SERVER_TWO) && 
				isL2Vpn();
	}
	
	public boolean isVPNClient(){
		return vpnProfile != null && hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT && isL2Vpn();
	}
	
	public boolean isConfigVpn(){
		return isVPNServer() || isVPNClient();
	}
	
	public String getVpnProfileGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.vpnServices");
	}
	
	public String getVpnProfileName(){
		if(vpnProfile != null){
			return vpnProfile.getProfileName();
		}else {
			return null;
		}
	}
	
	public String getClientPoolName(){
		return hiveAp.getMacAddress() + "_ipPool";
	}
	
	public String getClientPoolIpRange(){
		if(vpnServerIndex == VPN_SERVER_ONE){
			return vpnProfile.getClientIpPoolStart1() + " " + vpnProfile.getClientIpPoolEnd1();
		}else if(vpnServerIndex == VPN_SERVER_TWO){
			return vpnProfile.getClientIpPoolStart2() + " " + vpnProfile.getClientIpPoolEnd2();
		}else{
			return null;
		}
	}
	
	public String getClientPoolNetMask(){
		if(vpnServerIndex == VPN_SERVER_ONE){
			return vpnProfile.getClientIpPoolNetmask1();
		}else if(vpnServerIndex == VPN_SERVER_TWO){
			return vpnProfile.getClientIpPoolNetmask2();
		}else{
			return null;
		}
	}
	
	public String getXauthClientListName(){
		return hiveAp.getMacAddress() + clientListSuffix;
	}
	
	public int getXauthClientUserSize(){
		if(vpnServerUserList == null){
			vpnServerUserList = vpnProfile.getVpnCredentials();
			limitUserForL2VPN(vpnServerUserList);
		}
		
		if(vpnServerUserList == null){
			return 0;
		}else{
			return vpnServerUserList.size();
		}
	}
	
	private void limitUserForL2VPN(List<?> list){
		if(list == null || list.isEmpty()){
			return;
		}
		if(hiveAp.isCVGAppliance()){
			return;
		}
		while(list.size() > L2_VPN_MAX_USER_FOR_AP){
			list.remove(list.size() - 1);
		}
	}
	
	public String getXauthClientUserName(int index){
		return vpnServerUserList.get(index).getClientName();
	}
	
	public String getXauthClientPassword(int index){
		return vpnServerUserList.get(index).getCredential();
	}
	
	public String getVPNServerName(){
		if(vpnServerIndex == VPN_SERVER_ONE){
			return hiveAp.getMacAddress() + "_primaryServer";
		}else if(vpnServerIndex == VPN_SERVER_TWO){
			return hiveAp.getMacAddress() + "_backupServer";
		}else{
			return null;
		}
	}
	
	public int getVPNClientIpsecSize(){
		int rst = 0;
		if(vpnProfile.getServerPublicIp1() != null && !"".equals(vpnProfile.getServerPublicIp1())){
			rst++;
		}
		if(vpnProfile.getServerPublicIp2() != null && !"".equals(vpnProfile.getServerPublicIp2())){
			rst++;
		}
		return rst;
	}
	
	public int getVpnIpsecTunnelSize(){
		if(this.isVPNServer()){
			return 1;
		}else{
			return this.getVPNClientIpsecSize();
		}
	}
	
	public String getVPNClientIpsecName(int index){
		return hiveAp.getMacAddress()+"_client_ipsec_" + (index+1);
	}
	
	public String getPhase1Encryption(){
		if(vpnProfile.getPhase1EncrypAlg() == VpnService.PHASE1_ENCRYP_ALG_3DES){
			return "3des";
		}else if(vpnProfile.getPhase1EncrypAlg() == VpnService.PHASE1_ENCRYP_ALG_AES128){
			return "aes128";
		}else if(vpnProfile.getPhase1EncrypAlg() == VpnService.PHASE1_ENCRYP_ALG_AES192){
			return "aes192";
		}else{
			return "aes256";
		}
	}
	
	public IkeHashValue getPhase1Hash(){
		if(vpnProfile.getPhase1Hash() == VpnService.PHASE1_HASH_SHA1){
			return IkeHashValue.SHA_1;
		}else{
			return IkeHashValue.MD_5;
		}
	}
	
	public boolean isConfigPhase1Lifetime(){
		return vpnProfile.getPhase1LifeTime() != VpnService.DEFAULT_PHASE1_LIFE_TIME;
	}
	
	public int getPhase1Lifetime(){
		return vpnProfile.getPhase1LifeTime();
	}
	
	public IkeDhGroupValue getPhase1Dhgroup(){
		if(vpnProfile.getPhase1DhGroup() == VpnService.PHASE1_DH_GROUP_1){
			return IkeDhGroupValue.GROUP_1;
		}else if(vpnProfile.getPhase1DhGroup() == VpnService.PHASE1_DH_GROUP_5){
			return IkeDhGroupValue.GROUP_5;
		}else{
			return IkeDhGroupValue.GROUP_2;
		}
	}
	
	public IkeAuthMethodValue getPhase1Auth(){
		if(vpnProfile.getPhase1AuthMethod() == VpnService.PHASE1_AUTH_METHOD_HYBRID){
			return IkeAuthMethodValue.HYBRID;
		}else{
			return IkeAuthMethodValue.RSA_SIG;
		}
	}
	
	public String getPhase2Encryption(){
		if(vpnProfile.getPhase2EncrypAlg() == VpnService.PHASE2_ENCRYP_ALG_3DES){
			return "3des";
		}else if(vpnProfile.getPhase2EncrypAlg() == VpnService.PHASE2_ENCRYP_ALG_AES128){
			return "aes128";
		}else if(vpnProfile.getPhase2EncrypAlg() == VpnService.PHASE2_ENCRYP_ALG_AES192){
			return "aes192";
		}else{
			return "aes256";
		}
	}
	
	public IkeHashValue getPhase2Hash(){
		if(vpnProfile.getPhase2Hash() == VpnService.PHASE2_HASH_SHA1){
			return IkeHashValue.SHA_1;
		}else{
			return IkeHashValue.MD_5;
		}
	}
	
	public int getPhase2Lifetime(){
		return vpnProfile.getPhase2LifeTime();
	}
	
	public IkePfsGroupValue getPhase2PfsGroup(){
		if(vpnProfile.getPhase2PfsGroup() == VpnService.PHASE2_PFS_GROUP_0){
			return IkePfsGroupValue.NO_PFS;
		}else if(vpnProfile.getPhase2PfsGroup() == VpnService.PHASE2_PFS_GROUP_1){
			return IkePfsGroupValue.GROUP_1;
		}else if(vpnProfile.getPhase2PfsGroup() == VpnService.PHASE2_PFS_GROUP_5){
			return IkePfsGroupValue.GROUP_5;
		}else{
			return IkePfsGroupValue.GROUP_2;
		}
	}
	
	public String getDNSServerAddress() throws CreateXMLException{
		if(vpnProfile.getDnsIp() != null){
			return CLICommonFunc.getIpAddress(vpnProfile.getDnsIp(), this.hiveAp).getIpAddress();
		}else{
			return "192.168.0.1";
		}
		
	}
	
	public String getVpnClientGateWay(int index){
		if(index == 0 ){
			return vpnProfile.getServerPublicIp1();
		}else{
			return vpnProfile.getServerPublicIp2();
		}
	}
	
	public boolean isNatTraversalEnable(){
		return vpnProfile.isNatTraversal();
	}
	
	public String getIpSecTunnelName(int index){
		if(this.isVPNServer()){
			return this.getVPNServerName();
		}else{
			return this.getVPNClientIpsecName(index);
		}
	}
	
	public String getClientUserName(){
		return vpnCredential.getClientName();
	}
	
	public String getClientUserPassword(){
		if(vpnCredential != null){
			return vpnCredential.getCredential();
		}else{
			return null;
		}
	}
	
	public String getTunnlePolicyName(){
		return VPN_TUNNEL_POLICY_NAME;
	}
	
	public String getIpsecTunnelName(){
		if(vpnServerIndex == VPNProfileImpl.VPN_SERVER_ONE){
			return hiveAp.getMacAddress() + "_primaryServer";
		}else if(vpnServerIndex == VPNProfileImpl.VPN_SERVER_TWO){
			return hiveAp.getMacAddress() + "_backupServer";
		}else{
			return null;
		}
	}
	
	public String getIpPoolName(){
		return hiveAp.getMacAddress()+"_ipPool";
	}
	
	public int getClientTunnelSize(){
		int rst = 0;
		if(vpnProfile.getServerPublicIp1() != null && !"".equals(vpnProfile.getServerPublicIp1())){
			rst++;
		}
		if(vpnProfile.getServerPublicIp2() != null && !"".equals(vpnProfile.getServerPublicIp2())){
			rst++;
		}
		return rst;
	}
	
	public String getIpsecName(int index){
		if(index == 0){
			if(vpnProfile.getServerPublicIp1() != null && !"".equals(vpnProfile.getServerPublicIp1())){
				return hiveAp.getMacAddress()+IPSEC_1_SUFIX;
			}else{
				return hiveAp.getMacAddress()+IPSEC_2_SUFIX;
			}
		}else{
			return hiveAp.getMacAddress()+IPSEC_2_SUFIX;
		}
	}
	
	public boolean isPrimary(int index){
		if(vpnProfile.getServerPublicIp2() == null || "".equals(vpnProfile.getServerPublicIp2())){
			return true;
		}
		if(!vpnProfile.isLoadBalance()){
			return index == 0 && vpnProfile.getServerPublicIp1() != null && !"".equals(vpnProfile.getServerPublicIp1());
		}
		int indexUser = 0;
		for(int i=0; i<vpnProfile.getVpnCredentials().size(); i++){
			if(vpnCredential.getCredential().equals(vpnProfile.getVpnCredentials().get(i).getCredential())){
				indexUser = i + 1;
				break;
			}
		}
		indexUser = indexUser%2;
		
		return (index + indexUser)%2 == 1;
		
//		return index == 0 && vpnProfile.getServerPublicIp1() != null && !"".equals(vpnProfile.getServerPublicIp1());
	}
	
	public int getIdleInterval(){
		return vpnProfile.getDpdIdelInterval();
	}
	
	public int getRetry(){
		return vpnProfile.getDpdRetry();
	}
	
	public int getRetryInterval(){
		return vpnProfile.getDpdRetryInterval();
	}
	
	public boolean isConfigLocalIkeId(){
		return this.isVPNServer() && vpnProfile.isIkeValidation();
	}
	
	public boolean isConfigPeerIkeId(){
		return this.isVPNClient() && vpnProfile.isIkeValidation();
	}
	
	public boolean isConfigIkeAddress(){
		return vpnProfile.getServerIkeId() == VpnService.IKE_ID_ADDRESS;
	}
	
	public boolean isConfigIkeAsn1dn(){
		return vpnProfile.getServerIkeId() == VpnService.IKE_ID_ASN1DN;
	}
	
	public boolean isConfigIkeFqdn(){
		return vpnProfile.getServerIkeId() == VpnService.IKE_ID_FQDN;
	}
	
	public boolean isConfigIkeUfqdn(){
		return vpnProfile.getServerIkeId() == VpnService.IKE_ID_UFQDN;
	}
	
	public String getIkeAddressValue(){
		return vpnProfile.getIkeIdValue(VpnService.IKE_ID_ADDRESS);
	}
	
	public String getIkeAsn1dnValue(){
		return vpnProfile.getIkeIdValue(VpnService.IKE_ID_ASN1DN);
	}
	
	public String getIkeFqdnValue(){
		return vpnProfile.getIkeIdValue(VpnService.IKE_ID_FQDN);
	}
	
	public String getIkeUfqdnValue(){
		return vpnProfile.getIkeIdValue(VpnService.IKE_ID_UFQDN);
	}
	
	public int getL3TunnelExceptionSize(){
		return 0;
	}
	
	public String getL3TunnelException(int index){
		return null;
	}
	
	public VpnModeValue getVpnMode(){
		if(vpnProfile.getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_3){
			return VpnModeValue.LAYER_3;
		}else{
			return VpnModeValue.LAYER_2;
		}
	}

	@Override
	public int getNatPolicySize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getNaPolicyName(int index) {
		// TODO Auto-generated method stub
		return null;
	}

}