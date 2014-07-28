package com.ah.be.config.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.VPNProfileInt;
import com.ah.xml.be.config.ClientIpsecTunnel;
import com.ah.xml.be.config.DpdIdleInterval;
import com.ah.xml.be.config.DpdRetry;
import com.ah.xml.be.config.DpdRetryInterval;
import com.ah.xml.be.config.IkeAuthMethod;
import com.ah.xml.be.config.IkeDhGroup;
import com.ah.xml.be.config.IkeEncryptionAlgorithm;
import com.ah.xml.be.config.IkeHash;
import com.ah.xml.be.config.IkePfsGroup;
import com.ah.xml.be.config.IkePhase1Lifetime;
import com.ah.xml.be.config.IkePhase2Lifetime;
import com.ah.xml.be.config.IpsecDpd;
import com.ah.xml.be.config.IpsecLocalIkeId;
import com.ah.xml.be.config.IpsecPeerIkeId;
import com.ah.xml.be.config.ServerClientIpsecTunnel;
import com.ah.xml.be.config.TunnelPolicyClient;
import com.ah.xml.be.config.TunnelPolicyServer;
import com.ah.xml.be.config.VpnIpsecTunnel;
import com.ah.xml.be.config.VpnMode;
import com.ah.xml.be.config.VpnModeValue;
import com.ah.xml.be.config.VpnObj;
import com.ah.xml.be.config.VpnTunnelPolicy;

/**
 * @author zhang
 * @version 2009-4-28 10:12:49
 */

public class CreateVPNTree {
	
	private VPNProfileInt vpnImpl;
	
	private VpnObj vpnObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> oVpnChildList_1 = new ArrayList<Object>();
	private List<Object> oVpnChildList_2 = new ArrayList<Object>();
	
	private List<Object> oIpsecChildList_1 = new ArrayList<Object>();
	private List<Object> oIpsecChildList_2 = new ArrayList<Object>();
	private List<Object> oIpsecChildList_3 = new ArrayList<Object>();

	public CreateVPNTree(VPNProfileInt vpnImpl, GenerateXMLDebug oDebug, boolean isView){
		this.vpnImpl = vpnImpl;
		this.oDebug = oDebug;
	}
	
	public VpnObj getVpnObj(){
		return this.vpnObj;
	}
	
	public void generate() throws Exception{
		
		oDebug.debug("/configuration", 
				"vpn", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		if(vpnImpl.isConfigVpn()){
			vpnObj = new VpnObj();
			
			generateVpnChildLevel_1();
		}
	}
	
	private void generateVpnChildLevel_1() throws Exception{
		/**
		 * <vpn>				VpnObj
		 */
		
		/** element: <vpn>.<client-ip-pool> */
		oDebug.debug("/configuration/vpn", 
				"client-ip-pool", GenerateXMLDebug.CONFIG_ELEMENT,
				vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
		if(vpnImpl.isVPNServer()){
			VpnObj.ClientIpPool ipPool = new VpnObj.ClientIpPool();
			oVpnChildList_1.add(ipPool);
			vpnObj.setClientIpPool(ipPool);
		}
	
		/** element: <vpn>.<xauth-client-list> */
		oDebug.debug("/configuration/vpn", 
				"xauth-client-list", GenerateXMLDebug.CONFIG_ELEMENT,
				vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
		if(vpnImpl.isVPNServer()){
			VpnObj.XauthClientList xauthObj = new VpnObj.XauthClientList();
			oVpnChildList_1.add(xauthObj);
			vpnObj.setXauthClientList(xauthObj);
		}
		
		/** element: <vpn>.<server-ipsec-tunnel> */
		oDebug.debug("/configuration/vpn", 
				"server-ipsec-tunnel", GenerateXMLDebug.CONFIG_ELEMENT,
				vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
		if(vpnImpl.isVPNServer()){
			ServerClientIpsecTunnel ipSecTunnel = new ServerClientIpsecTunnel();
			oVpnChildList_1.add(ipSecTunnel);
			vpnObj.setServerIpsecTunnel(ipSecTunnel);
		}
		
		/** element: <vpn>.<client-ipsec-tunnel> */
		oDebug.debug("/configuration/vpn", 
				"client-ipsec-tunnel", GenerateXMLDebug.CONFIG_ELEMENT,
				vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
		if(vpnImpl.isVPNClient()){
			for(int i=0; i<vpnImpl.getVPNClientIpsecSize(); i++){
				vpnObj.getClientIpsecTunnel().add(this.createServerClientIpsecTunnel(i, vpnImpl.getVPNClientIpsecName(i)));
			}
		}
		
		/** element: <vpn>.<ipsec-tunnel> */
		for(int i=0; i<vpnImpl.getVpnIpsecTunnelSize(); i++){
			vpnObj.getIpsecTunnel().add(this.createVpnIpsecTunnel(i));
		}
		
		/** element: <vpn>.<tunnel-policy> */
		if(vpnImpl.isVPNServer() || vpnImpl.isVPNClient()){
			VpnTunnelPolicy tunnelPolicyObj = new VpnTunnelPolicy();
			oVpnChildList_1.add(tunnelPolicyObj);
			vpnObj.getTunnelPolicy().add(tunnelPolicyObj);
		}
		
		/** element: <vpn>.<l3-tunnel-exception> */
		for(int index=0; index < vpnImpl.getL3TunnelExceptionSize(); index++){
			vpnObj.getL3TunnelException().add(CLICommonFunc.createAhNameActValue(vpnImpl.getL3TunnelException(index), CLICommonFunc.getYesDefault()));
		}
		
		generateVpnChildLevel_2();
	}
	
	private void generateVpnChildLevel_2() throws Exception{
		/**
		 * <vpn>.<client-ip-pool>						VpnObj.ClientIpPool
		 * <vpn>.<xauth-client-list>					VpnObj.XauthClientList
		 * <vpn>.<tunnel-policy>						VpnTunnelPolicy
		 * <vpn>.<server-ipsec-tunnel>					ServerClientIpsecTunnel
		 */
		for(Object childObj : oVpnChildList_1){
			
			/** element: <vpn>.<client-ip-pool> */
			if(childObj instanceof VpnObj.ClientIpPool){
				VpnObj.ClientIpPool ipPoolObj = (VpnObj.ClientIpPool)childObj;
				
				/** attribute: name */
				oDebug.debug("/configuration/vpn", 
						"client-ip-pool", GenerateXMLDebug.SET_NAME,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				ipPoolObj.setName(vpnImpl.getClientPoolName());
				
				/** attribute: operation */
				ipPoolObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
				
				/** element: <vpn>.<client-ip-pool>.<local> */
				VpnObj.ClientIpPool.Local localObj = new VpnObj.ClientIpPool.Local();
				oVpnChildList_2.add(localObj);
				ipPoolObj.setLocal(localObj);
			}
			
			/** element: <vpn>.<xauth-client-list> */
			if(childObj instanceof VpnObj.XauthClientList){
				VpnObj.XauthClientList oXauthClientList = (VpnObj.XauthClientList)childObj;
				
				/** attribute: name */
				oXauthClientList.setName(vpnImpl.getXauthClientListName());
				
				/** attribute: operation */
				oXauthClientList.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
				
				/** element: <vpn>.<xauth-client-list>.<local> */
				oXauthClientList.setLocal("");
				
				/** element: <vpn>.<xauth-client-list>.<clientName> */
				for(int i=0; i<vpnImpl.getXauthClientUserSize(); i++){
					oXauthClientList.getClientName().add(createClientName(i));
				}
			}
			
			/** element: <vpn>.<tunnel-policy> */
			if(childObj instanceof VpnTunnelPolicy){
				VpnTunnelPolicy tunnelObj = (VpnTunnelPolicy)childObj;
				
				/** attribute: name */
				oDebug.debug("/configuration/vpn", 
						"tunnel-policy", GenerateXMLDebug.SET_NAME,
						null, null);
				tunnelObj.setName(vpnImpl.getTunnlePolicyName());
				
				/** attribute: operation */
				tunnelObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
				
				/** element: <vpn>.<tunnel-policy>.<server> */
				oDebug.debug("/configuration/vpn/tunnel-policy[@name='"+vpnImpl.getTunnlePolicyName()+"']", 
						"server", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(vpnImpl.isVPNServer()){
					TunnelPolicyServer serverObj = new TunnelPolicyServer();
					oVpnChildList_2.add(serverObj);
					tunnelObj.setServer(serverObj);
				}
				
				/** element: <vpn>.<tunnel-policy>.<client> */
				oDebug.debug("/configuration/vpn/tunnel-policy[@name='"+vpnImpl.getTunnlePolicyName()+"']", 
						"client", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(vpnImpl.isVPNClient()){
					TunnelPolicyClient clientObj = new TunnelPolicyClient();
					oVpnChildList_2.add(clientObj);
					tunnelObj.setClient(clientObj);
				}
			}
			
			/** element: <vpn>.<server-ipsec-tunnel> */
			if(childObj instanceof ServerClientIpsecTunnel){
				ServerClientIpsecTunnel ipSecTunnel = (ServerClientIpsecTunnel)childObj;
				
				/** attribute: name */
				ipSecTunnel.setName(vpnImpl.getVPNServerName());
				
				/** attribute: operation */
				ipSecTunnel.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
				
				/** element: <vpn>.<server-ipsec-tunnel>.<vpn-mode> */
				ipSecTunnel.setVpnMode(createVPNMode(vpnImpl.getVpnMode()));
			}
		}
		oVpnChildList_1.clear();
		generateVpnChildLevel_3();
	}
	
	private void generateVpnChildLevel_3() throws Exception{
		/**
		 * <vpn>.<client-ip-pool>.<local>			VpnObj.ClientIpPool.Local
		 * <vpn>.<tunnel-policy>.<server>			TunnelPolicyServer
		 * <vpn>.<tunnel-policy>.<client>			TunnelPolicyClient
		 */
		for(Object childObj : oVpnChildList_2){
			
			/** element: <vpn>.<client-ip-pool>.<local>	*/
			if(childObj instanceof VpnObj.ClientIpPool.Local){
				VpnObj.ClientIpPool.Local localObj = (VpnObj.ClientIpPool.Local)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/vpn/client-ip-pool", 
						"local", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				localObj.setValue(vpnImpl.getClientPoolIpRange());
				
				/** attribute: quoteProhibited */
				localObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <vpn>.<client-ip-pool>.<local>.<netmask> */
				oDebug.debug("/configuration/vpn/client-ip-pool/local", 
						"netmask", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				localObj.setNetmask(CLICommonFunc.createAhStringObj(vpnImpl.getClientPoolNetMask()));
			}
			
			/** element: <vpn>.<tunnel-policy>.<server> */
			if(childObj instanceof TunnelPolicyServer){
				TunnelPolicyServer serverObj = (TunnelPolicyServer)childObj;
				
				/** element: <vpn>.<tunnel-policy>.<server>.<ipsec-tunnel> */
				oDebug.debug("/configuration/vpn/tunnel-policy[@name='"+vpnImpl.getTunnlePolicyName()+"']/server", 
						"ipsec-tunnel", GenerateXMLDebug.SET_VALUE,
						null, null);
				serverObj.setIpsecTunnel(CLICommonFunc.createAhNameActObj(
						vpnImpl.getIpsecTunnelName(), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <vpn>.<tunnel-policy>.<client> */
			if(childObj instanceof TunnelPolicyClient){
				TunnelPolicyClient clientObj = (TunnelPolicyClient)childObj;
				
				/** element: <vpn>.<tunnel-policy>.<client>.<ipsec-tunnel> */
				for(int i=0; i<vpnImpl.getClientTunnelSize(); i++){
					clientObj.getIpsecTunnel().add(createClientIpTunnel(i));
				}
			}
		}
		oVpnChildList_2.clear();
	}
	
	private VpnObj.XauthClientList.ClientName createClientName(int index) throws IOException{
		VpnObj.XauthClientList.ClientName clientNameObj = new VpnObj.XauthClientList.ClientName();
		
		/** attribute: name */
		oDebug.debug("/configuration/vpn/xauth-client-list", 
				"client-name", GenerateXMLDebug.SET_NAME,
				vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
		clientNameObj.setName(vpnImpl.getXauthClientUserName(index));
		
		/** attribute: operation */
		clientNameObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <vpn>.<xauth-client-list>.<clientName>.<password> */
		oDebug.debug("/configuration/vpn/xauth-client-list/client-name[@name='"+vpnImpl.getXauthClientUserName(index)+"']", 
				"password", GenerateXMLDebug.SET_VALUE,
				vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
		clientNameObj.setPassword(CLICommonFunc.createAhEncryptedString(vpnImpl.getXauthClientPassword(index)));
		
		return clientNameObj;
	}
	
	private VpnIpsecTunnel createVpnIpsecTunnel(int index) throws Exception{
		VpnIpsecTunnel ipSecObj = new VpnIpsecTunnel();
		
		/** attribute: name */
		oDebug.debug("/configuration/vpn", 
				"ipsec-tunnel", GenerateXMLDebug.SET_NAME,
				vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
		ipSecObj.setName(vpnImpl.getIpSecTunnelName(index));
		
		/** attribute: operation */
		ipSecObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
		
		/** element: <vpn>.<ipsec-tunnel>.<client-list> */
		oDebug.debug("/configuration/vpn/ipsec-tunnel",
				"client-list", GenerateXMLDebug.CONFIG_ELEMENT,
				vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
		if(vpnImpl.isVPNServer()){
			VpnIpsecTunnel.ClientList clientListObj = new VpnIpsecTunnel.ClientList();
			oIpsecChildList_1.add(clientListObj);
			ipSecObj.setClientList(clientListObj);
		}
		
		/** element: <vpn>.<ipsec-tunnel>.<gateway> */
		oDebug.debug("/configuration/vpn/ipsec-tunnel",
				"gateway", GenerateXMLDebug.CONFIG_ELEMENT,
				vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
		if(vpnImpl.isVPNClient()){
			VpnIpsecTunnel.Gateway gatewayObj = new VpnIpsecTunnel.Gateway();
			oIpsecChildList_1.add(gatewayObj);
			ipSecObj.setGateway(gatewayObj);
		}
		
		/** element: <vpn>.<ipsec-tunnel>.<ike> */
		VpnIpsecTunnel.Ike ikeObj = new VpnIpsecTunnel.Ike();
		oIpsecChildList_1.add(ikeObj);
		ipSecObj.setIke(ikeObj);
		
		/** element: <vpn>.<ipsec-tunnel>.<nat-traversal> */
		VpnIpsecTunnel.NatTraversal natTraversalObj = new VpnIpsecTunnel.NatTraversal();
		oIpsecChildList_1.add(natTraversalObj);
		ipSecObj.setNatTraversal(natTraversalObj);
		
		/** element: <vpn>.<ipsec-tunnel>.<dpd> */
		IpsecDpd dpdObj = new IpsecDpd();
		oIpsecChildList_1.add(dpdObj);
		ipSecObj.setDpd(dpdObj);
		
		/** element: <vpn>.<ipsec-tunnel>.<local-ike-id> */
		if(vpnImpl.isConfigLocalIkeId()){
			IpsecLocalIkeId ikeId = new IpsecLocalIkeId();
			oIpsecChildList_1.add(ikeId);
			ipSecObj.setLocalIkeId(ikeId);
		}
		
		/** element: <vpn>.<ipsec-tunnel>.<peer-ike-id> */
		if(vpnImpl.isConfigPeerIkeId()){
			IpsecPeerIkeId peerIkeIdObj = new IpsecPeerIkeId();
			oIpsecChildList_1.add(peerIkeIdObj);
			ipSecObj.setPeerIkeId(peerIkeIdObj);
		}
		
		/** element: <vpn>.<ipsec-tunnel>.<nat-policy> */
		for (int i = 0; i < vpnImpl.getNatPolicySize(); i++) {
			ipSecObj.getNatPolicy().add(CLICommonFunc.createAhNameActValue(vpnImpl.getNaPolicyName(i), true));
		}
		
		generateIpsecTunnelLevel_1(index);
		return ipSecObj;
	}
	
	public void generateIpsecTunnelLevel_1(int index) throws Exception{
		/**
		 * <vpn>.<ipsec-tunnel>.<client-list>				VpnIpsecTunnel.ClientList
		 * <vpn>.<ipsec-tunnel>.<gateway>					VpnIpsecTunnel.Gateway
		 * <vpn>.<ipsec-tunnel>.<ike>						VpnIpsecTunnel.Ike
		 * <vpn>.<ipsec-tunnel>.<nat-traversal>				VpnIpsecTunnel.NatTraversal
		 * <vpn>.<ipsec-tunnel>.<dpd>						IpsecDpd
		 * <vpn>.<ipsec-tunnel>.<local-ike-id>				IpsecLocalIkeId
		 * <vpn>.<ipsec-tunnel>.<peer-ike-id>				IpsecPeerIkeId
		 */
		for(Object childObj : oIpsecChildList_1){
			
			/** element: <vpn>.<ipsec-tunnel>.<client-list> */
			if(childObj instanceof VpnIpsecTunnel.ClientList){
				VpnIpsecTunnel.ClientList clientListObj = (VpnIpsecTunnel.ClientList)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/vpn/ipsec-tunnel",
						"client-list", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				clientListObj.setValue(vpnImpl.getXauthClientListName());
				
				/** element: <vpn>.<ipsec-tunnel>.<client-list>.<client-ip-pool> */
				VpnIpsecTunnel.ClientList.ClientIpPool clientIpPoolObj = new VpnIpsecTunnel.ClientList.ClientIpPool();
				oIpsecChildList_2.add(clientIpPoolObj);
				clientListObj.setClientIpPool(clientIpPoolObj);
			}
			
			/** element: <vpn>.<ipsec-tunnel>.<gateway>	*/
			if(childObj instanceof VpnIpsecTunnel.Gateway){
				VpnIpsecTunnel.Gateway gatewayObj = (VpnIpsecTunnel.Gateway)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/vpn/ipsec-tunnel",
						"gateway", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				gatewayObj.setValue(vpnImpl.getVpnClientGateWay(index));
				
				/** element: <vpn>.<ipsec-tunnel>.<gateway>.<client-name> */
				VpnIpsecTunnel.Gateway.ClientName clientNameObj = new VpnIpsecTunnel.Gateway.ClientName();
				oIpsecChildList_2.add(clientNameObj);
				gatewayObj.setClientName(clientNameObj);
			}
			
			/** element: <vpn>.<ipsec-tunnel>.<ike>	*/
			if(childObj instanceof VpnIpsecTunnel.Ike){
				VpnIpsecTunnel.Ike ikeObj = (VpnIpsecTunnel.Ike)childObj;
				
				/** element: <vpn>.<ipsec-tunnel>.<ike>.<phase1> */
				VpnIpsecTunnel.Ike.Phase1 phase1Obj = new VpnIpsecTunnel.Ike.Phase1();
				oIpsecChildList_2.add(phase1Obj);
				ikeObj.setPhase1(phase1Obj);
				
				/** element: <vpn>.<ipsec-tunnel>.<ike>.<phase2> */
				VpnIpsecTunnel.Ike.Phase2 phase2Obj = new VpnIpsecTunnel.Ike.Phase2();
				oIpsecChildList_2.add(phase2Obj);
				ikeObj.setPhase2(phase2Obj);
			}
			
			/** element: <vpn>.<ipsec-tunnel>.<nat-traversal> */
			if(childObj instanceof VpnIpsecTunnel.NatTraversal){
				VpnIpsecTunnel.NatTraversal natTraversalObj = (VpnIpsecTunnel.NatTraversal)childObj;
				
				/** element: <vpn>.<ipsec-tunnel>.<nat-traversal>.<enable> */
				natTraversalObj.setEnable(CLICommonFunc.getAhOnlyAct(vpnImpl.isNatTraversalEnable()));
			}
			
			/** element: <vpn>.<ipsec-tunnel>.<dpd> */
			if(childObj instanceof IpsecDpd){
				IpsecDpd dpdObj = (IpsecDpd)childObj;
				
				/** attribute: operation */
				dpdObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <vpn>.<ipsec-tunnel>.<dpd>.<idle-interval> */
				DpdIdleInterval idleIntervalObj = new DpdIdleInterval();
				oIpsecChildList_2.add(idleIntervalObj);
				dpdObj.setIdleInterval(idleIntervalObj);
			}
			
			/** element: <vpn>.<ipsec-tunnel>.<local-ike-id> */
			if(childObj instanceof IpsecLocalIkeId){
				IpsecLocalIkeId localIkeIdObj = (IpsecLocalIkeId)childObj;
				
				/** attribute: operation */
				localIkeIdObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <vpn>.<ipsec-tunnel>.<local-ike-id>.<address> */
				if(vpnImpl.isConfigIkeAddress()){
					localIkeIdObj.setAddress(CLICommonFunc.createAhStringObj(vpnImpl.getIkeAddressValue()));
				}
				
				/** element: <vpn>.<ipsec-tunnel>.<local-ike-id>.<asn1dn> */
				if(vpnImpl.isConfigIkeAsn1dn()){
					localIkeIdObj.setAsn1Dn(CLICommonFunc.createAhStringObj(vpnImpl.getIkeAsn1dnValue()));
				}
				
				/** element: <vpn>.<ipsec-tunnel>.<local-ike-id>.<fqdn> */
				if(vpnImpl.isConfigIkeFqdn()){
					localIkeIdObj.setFqdn(CLICommonFunc.createAhStringObj(vpnImpl.getIkeFqdnValue()));
				}
				
				/** element: <vpn>.<ipsec-tunnel>.<local-ike-id>.<ufqdn> */
				if(vpnImpl.isConfigIkeUfqdn()){
					localIkeIdObj.setUfqdn(CLICommonFunc.createAhStringObj(vpnImpl.getIkeUfqdnValue()));
				}
			}
			
			/** element: <vpn>.<ipsec-tunnel>.<peer-ike-id> */
			if(childObj instanceof IpsecPeerIkeId){
				IpsecPeerIkeId peerIkeId = (IpsecPeerIkeId)childObj;
				
				/** element: <vpn>.<ipsec-tunnel>.<peer-ike-id>.<address> */
				if(vpnImpl.isConfigIkeAddress()){
					peerIkeId.getAddress().add(CLICommonFunc.createAhNameActValue(vpnImpl.getIkeAddressValue(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <vpn>.<ipsec-tunnel>.<peer-ike-id>.<asn1dn> */
				if(vpnImpl.isConfigIkeAsn1dn()){
					peerIkeId.getAsn1Dn().add(CLICommonFunc.createAhNameActValue(vpnImpl.getIkeAsn1dnValue(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <vpn>.<ipsec-tunnel>.<peer-ike-id>.<fqdn> */
				if(vpnImpl.isConfigIkeFqdn()){
					peerIkeId.getFqdn().add(CLICommonFunc.createAhNameActValue(vpnImpl.getIkeFqdnValue(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <vpn>.<ipsec-tunnel>.<peer-ike-id>.<ufqdn> */
				if(vpnImpl.isConfigIkeUfqdn()){
					peerIkeId.getUfqdn().add(CLICommonFunc.createAhNameActValue(vpnImpl.getIkeUfqdnValue(), CLICommonFunc.getYesDefault()));
				}
			}
		}
		generateIpsecTunnelLevel_2(index);
		oIpsecChildList_1.clear();
	}
	
	public void generateIpsecTunnelLevel_2(int index) throws Exception{
		/**
		 * <vpn>.<ipsec-tunnel>.<client-list>.<client-ip-pool>		VpnIpsecTunnel.ClientList.ClientIpPool
		 * <vpn>.<ipsec-tunnel>.<gateway>.<client-name>				VpnIpsecTunnel.Gateway.ClientName
		 * <vpn>.<ipsec-tunnel>.<ike>.<phase1>						VpnIpsecTunnel.Ike.Phase1
		 * <vpn>.<ipsec-tunnel>.<ike>.<phase2>						VpnIpsecTunnel.Ike.Phase2
		 * <vpn>.<ipsec-tunnel>.<dpd>.<idle-interval>				DpdIdleInterval
		 */
		for(Object childObj : oIpsecChildList_2){
			
			/** element: <vpn>.<ipsec-tunnel>.<client-list>.<client-ip-pool> */
			if(childObj instanceof VpnIpsecTunnel.ClientList.ClientIpPool){
				VpnIpsecTunnel.ClientList.ClientIpPool clientIpPoolObj = (VpnIpsecTunnel.ClientList.ClientIpPool)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/client-list",
						"client-ip-pool", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				clientIpPoolObj.setValue(vpnImpl.getClientPoolName());
				
				/** element: <vpn>.<ipsec-tunnel>.<client-list>.<client-ip-pool>.<dns-server> */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/client-list/client-ip-pool",
						"dns-server", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				Object[][] dnsParm  = {
						{CLICommonFunc.ATTRIBUTE_VALUE, vpnImpl.getDNSServerAddress()}
				};
				clientIpPoolObj.setDnsServer(
						(VpnIpsecTunnel.ClientList.ClientIpPool.DnsServer)CLICommonFunc.createObjectWithName(
								VpnIpsecTunnel.ClientList.ClientIpPool.DnsServer.class, dnsParm)
				);
			}
			
			/** element: <vpn>.<ipsec-tunnel>.<gateway>.<client-name> */
			if(childObj instanceof VpnIpsecTunnel.Gateway.ClientName){
				VpnIpsecTunnel.Gateway.ClientName clientNameObj = (VpnIpsecTunnel.Gateway.ClientName)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/gateway",
						"client-name", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				clientNameObj.setValue(vpnImpl.getClientUserName());
				
				/** element: <vpn>.<ipsec-tunnel>.<gateway>.<client-name>.<password> */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/gateway/client-name",
						"password", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				clientNameObj.setPassword(CLICommonFunc.createAhEncryptedString(vpnImpl.getClientUserPassword()));
				
			}
			
			/** element: <vpn>.<ipsec-tunnel>.<ike>.<phase1> */
			if(childObj instanceof VpnIpsecTunnel.Ike.Phase1){
				VpnIpsecTunnel.Ike.Phase1 phase1Obj = (VpnIpsecTunnel.Ike.Phase1)childObj;
				
				/** element: <vpn>.<ipsec-tunnel>.<ike>.<phase1>.<encryption-algorithm> */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/ike/phase1",
						"encryption-algorithm", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				Object[][] encryptionParm = new Object[][]{
						{CLICommonFunc.ATTRIBUTE_VALUE, vpnImpl.getPhase1Encryption()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				phase1Obj.setEncryptionAlgorithm(
						(IkeEncryptionAlgorithm)CLICommonFunc.createObjectWithName(IkeEncryptionAlgorithm.class, encryptionParm)
				);
				
				/** element: <vpn>.<ipsec-tunnel>.<ike>.<phase1>.<hash> */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/ike/phase1",
						"hash", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				Object[][] hashParm = new Object[][]{
						{CLICommonFunc.ATTRIBUTE_VALUE, vpnImpl.getPhase1Hash()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				phase1Obj.setHash(
						(IkeHash)CLICommonFunc.createObjectWithName(IkeHash.class, hashParm)
				);
				
				/** element: <vpn>.<ipsec-tunnel>.<ike>.<phase1>.<lifetime> */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/ike/phase1",
						"lifetime", GenerateXMLDebug.CONFIG_ELEMENT,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				if(vpnImpl.isConfigPhase1Lifetime()){
					oDebug.debug("/configuration/vpn/ipsec-tunnel/ike/phase1",
							"lifetime", GenerateXMLDebug.SET_VALUE,
							vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
					Object[][] lifetimeParm = new Object[][]{
							{CLICommonFunc.ATTRIBUTE_VALUE, vpnImpl.getPhase1Lifetime()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					phase1Obj.setLifetime(
							(IkePhase1Lifetime)CLICommonFunc.createObjectWithName(IkePhase1Lifetime.class, lifetimeParm)
					);
				}
				
				/** element: <vpn>.<ipsec-tunnel>.<ike>.<phase1>.<dh-group> */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/ike/phase1",
						"dh-group", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				Object[][] groupParm = new Object[][]{
						{CLICommonFunc.ATTRIBUTE_VALUE, vpnImpl.getPhase1Dhgroup()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				phase1Obj.setDhGroup(
						(IkeDhGroup)CLICommonFunc.createObjectWithName(IkeDhGroup.class, groupParm)
				);
				
				/** element: <vpn>.<ipsec-tunnel>.<ike>.<phase1>.<auth-method> */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/ike/phase1",
						"auth-method", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				Object[][] authParm = new Object[][]{
						{CLICommonFunc.ATTRIBUTE_VALUE, vpnImpl.getPhase1Auth()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				phase1Obj.setAuthMethod(
						(IkeAuthMethod)CLICommonFunc.createObjectWithName(IkeAuthMethod.class, authParm)
				);
			}
			
			/** element: <vpn>.<ipsec-tunnel>.<ike>.<phase2> */
			if(childObj instanceof VpnIpsecTunnel.Ike.Phase2){
				VpnIpsecTunnel.Ike.Phase2 phase2Obj = (VpnIpsecTunnel.Ike.Phase2)childObj;
				
				/** element: <vpn>.<ipsec-tunnel>.<ike>.<phase2>.<encryption-algorithm> */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/ike/phase2",
						"encryption-algorithm", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				Object[][] encryptionParm = new Object[][]{
						{CLICommonFunc.ATTRIBUTE_VALUE, vpnImpl.getPhase2Encryption()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				phase2Obj.setEncryptionAlgorithm(
						(IkeEncryptionAlgorithm)CLICommonFunc.createObjectWithName(IkeEncryptionAlgorithm.class, encryptionParm)
				);
				
				/** element: <vpn>.<ipsec-tunnel>.<ike>.<phase2>.<hash> */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/ike/phase2",
						"hash", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				Object[][] hashParm = new Object[][]{
						{CLICommonFunc.ATTRIBUTE_VALUE, vpnImpl.getPhase2Hash()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				phase2Obj.setHash(
						(IkeHash)CLICommonFunc.createObjectWithName(IkeHash.class, hashParm)
				);
				
				/** element: <vpn>.<ipsec-tunnel>.<ike>.<phase2>.<lifetime> */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/ike/phase2",
						"lifetime", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				Object[][] lifetimeParm = new Object[][]{
						{CLICommonFunc.ATTRIBUTE_VALUE, vpnImpl.getPhase2Lifetime()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				phase2Obj.setLifetime(
						(IkePhase2Lifetime)CLICommonFunc.createObjectWithName(IkePhase2Lifetime.class, lifetimeParm)
				);
				
				
				/** element: <vpn>.<ipsec-tunnel>.<ike>.<phase2>.<pfs-group> */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/ike/phase2",
						"pfs-group", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				Object[][] pfsGroupParm = new Object[][]{
						{CLICommonFunc.ATTRIBUTE_VALUE, vpnImpl.getPhase2PfsGroup()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				phase2Obj.setPfsGroup(
						(IkePfsGroup)CLICommonFunc.createObjectWithName(IkePfsGroup.class, pfsGroupParm)
				);
			}
			
			/** element: <vpn>.<ipsec-tunnel>.<dpd>.<idle-interval>	*/
			if(childObj instanceof DpdIdleInterval){
				DpdIdleInterval idleIntervalObj = (DpdIdleInterval)childObj;
				
				/** attribute:  value */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/dpd",
						"idle-interval", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				idleIntervalObj.setValue(vpnImpl.getIdleInterval());
				
				/** element: <vpn>.<ipsec-tunnel>.<dpd>.<idle-interval>.<retry>	*/
				DpdRetry retryObj = new DpdRetry();
				oIpsecChildList_3.add(retryObj);
				idleIntervalObj.setRetry(retryObj);
			}
		}
		oIpsecChildList_2.clear();
		generateIpsecTunnelLevel_3(index);
	}
	
	public void generateIpsecTunnelLevel_3(int index) throws Exception{
		/**
		 * <vpn>.<ipsec-tunnel>.<dpd>.<idle-interval>.<retry>				DpdRetry
		 */
		for(Object childObj : oIpsecChildList_3){
			
			/** element: <vpn>.<ipsec-tunnel>.<dpd>.<idle-interval>.<retry> */
			if(childObj instanceof DpdRetry){
				DpdRetry retryObj = (DpdRetry)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/dpd/idle-interval",
						"retry", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				retryObj.setValue(vpnImpl.getRetry());
				
				/** element: <vpn>.<ipsec-tunnel>.<dpd>.<idle-interval>.<retry>.<retry-interval> */
				oDebug.debug("/configuration/vpn/ipsec-tunnel/dpd/idle-interval/retry",
						"retry-interval", GenerateXMLDebug.SET_VALUE,
						vpnImpl.getVpnProfileGuiName(), vpnImpl.getVpnProfileName());
				Object[][] retryIntParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, vpnImpl.getRetryInterval()}
				};
				retryObj.setRetryInterval(
						(DpdRetryInterval)CLICommonFunc.createObjectWithName(DpdRetryInterval.class, retryIntParm)
				);
			}
		}
		oIpsecChildList_3.clear();
	}
	
	private ClientIpsecTunnel createClientIpTunnel(int index){
		
		/** element: <vpn>.<tunnel-policy>.<client>.<ipsec-tunnel> */
		ClientIpsecTunnel ipTunnelObj = new ClientIpsecTunnel();
		
		/** attribute: name */
		ipTunnelObj.setName(vpnImpl.getIpsecName(index));
		
		/** attribute: operation */
		ipTunnelObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <vpn>.<tunnel-policy>.<client>.<ipsec-tunnel>.<primary> */
		oDebug.debug("/configuration/vpn/tunnel-policy[@name='"+vpnImpl.getTunnlePolicyName()+"']/client/ipsec-tunnel[@name='"+vpnImpl.getIpsecName(index)+"']", 
				"primary", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		if(vpnImpl.isPrimary(index)){
			ipTunnelObj.setPrimary(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
//		/** element: <vpn>.<tunnel-policy>.<client>.<ipsec-tunnel>.<gre-gateway> */
//		oDebug.debug("/configuration/vpn/tunnel-policy[@name='"+vpnImpl.getTunnlePolicyName()+"']/client/ipsec-tunnel[@name='"+vpnImpl.getIpsecName(index)+"']", 
//				"gre-gateway", GenerateXMLDebug.SET_VALUE,
//				null, null);
//		ipTunnelObj.setGreGateway(CLICommonFunc.createAhStringActValueObj(vpnImpl.getGreGateway(index), CLICommonFunc.getYesDefault()));
		
		
		return ipTunnelObj;
	}
	
	private ServerClientIpsecTunnel createServerClientIpsecTunnel(int index, String serverName){
		ServerClientIpsecTunnel ipSecTunnel = new ServerClientIpsecTunnel();
		
		/** attribute: name */
		ipSecTunnel.setName(serverName);
		
		/** attribute: operation */
		ipSecTunnel.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <vpn-mode> */
		ipSecTunnel.setVpnMode(createVPNMode(vpnImpl.getVpnMode()));
		
		return ipSecTunnel;
	}
	
	private VpnMode createVPNMode(VpnModeValue mode){
		VpnMode modeObj = new VpnMode();
		
		modeObj.setValue(mode);
		
		return modeObj;
	}
}
