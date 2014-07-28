package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.be.config.create.source.WebSecurityProxyInt;
import com.ah.be.config.create.source.impl.branchRouter.InterfaceBRImpl;
import com.ah.be.config.create.source.impl.branchRouter.InterfaceBRImpl.DhcpServerInfo;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.OpenDNSAccount;
import com.ah.bo.admin.OpenDNSDevice;
import com.ah.bo.admin.OpenDNSMapping;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.network.VpnNetwork;
import com.ah.util.MgrUtil;
import com.ah.xml.be.config.AhAllowBlockValue;

@SuppressWarnings("static-access")
public class WebSecurityProxyImpl implements WebSecurityProxyInt {
	
	private HMServicesSettings serviceSettings = null;
	private OpenDNSAccount activeAccount = null;
	private HiveAp hiveAp;
	
	private Map<ProxyType, List<SubAction>> subNetMap = new HashMap<ProxyType, List<SubAction>>();
	
	
	public WebSecurityProxyImpl(HiveAp hiveAp, InterfaceProfileInt interfaceImpl){
		this.hiveAp = hiveAp;
		List<HMServicesSettings> list = MgrUtil.getQueryEntity().executeQuery(
				HMServicesSettings.class, null, null,
						hiveAp.getOwner().getId(), new ConfigLazyQueryBo());
		if(list != null && !list.isEmpty()){
			serviceSettings = list.get(0);
		}
		
		activeAccount = serviceSettings.getOpenDNSAccount();
		
		// get All VpnNetwork from hiveAp
		List<VpnNetwork> allNetworkList = InterfaceBRImpl.getAllVpnNetwork(hiveAp);
		if(allNetworkList != null && !allNetworkList.isEmpty()){
			subNetMap.put(ProxyType.barracuda, new ArrayList<SubAction>());
			subNetMap.put(ProxyType.websense, new ArrayList<SubAction>());
			for(VpnNetwork network : allNetworkList){
				if(network != null && network.getWebSecurity() != VpnNetwork.VPN_NETWORK_WEBSECURITY_NONE){
					AhAllowBlockValue action = (network.getFailConnectionOption() == VpnNetwork.NETWORK_WEBSECURITY_FAIL_PERMIT)? AhAllowBlockValue.ALLOW : AhAllowBlockValue.BLOCK;
					List<SubAction> netList = new ArrayList<SubAction>();
					if(network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_GUEST){
						SubAction subObj = new SubAction();
						subObj.setSubnet(network.getIpAddressSpace());
						subObj.setAction(action);
						netList.add(subObj);
					}else if((network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
							network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT) && interfaceImpl.getMgtSubResourceMap() != null){
						for(DhcpServerInfo dhcpInfo : interfaceImpl.getMgtSubResourceMap().values()){
							SubNetworkResource subRes = dhcpInfo.getSubNetwork();
							if(subRes != null && subRes.getVpnNetwork() != null && subRes.getVpnNetwork().getId().equals(network.getId())){
								SubAction subObj = new SubAction();
								String networkIp = subRes.getNetwork();
								int index = networkIp.indexOf("/");
								networkIp = networkIp.substring(index+1);
								subObj.setSubnet(subRes.getFirstIp() + "/" + networkIp);
								subObj.setAction(action);
								netList.add(subObj);
								break;
							}
						}
					}
					if(network.getWebSecurity() == VpnNetwork.VPN_NETWORK_WEBSECURITY_BARRACUDA){
						subNetMap.get(ProxyType.barracuda).addAll(netList);
					}else if(network.getWebSecurity() == VpnNetwork.VPN_NETWORK_WEBSECURITY_WEBSENSE){
						subNetMap.get(ProxyType.websense).addAll(netList);
					}
				}
			}
		}
		
	}
	
	public boolean isConfigWebProxy(){
		return this.hiveAp.isBranchRouter();
	}

	public boolean isConfigWebProxy(ProxyType type) {
		if(type == ProxyType.barracuda){
			return serviceSettings != null && subNetMap.get(ProxyType.barracuda) != null && !subNetMap.get(ProxyType.barracuda).isEmpty();
		}else{
			return serviceSettings != null && subNetMap.get(ProxyType.websense) != null && !subNetMap.get(ProxyType.websense).isEmpty();
		}
	}
	
	public boolean isConfigHttpHost(ProxyType type){
		String host = getHttpHost(type);
		return host != null && !"".equals(host);
	}

	public String getHttpHost(ProxyType type) {
		if(type == ProxyType.barracuda){
			return serviceSettings.getServiceHost();
		}else{
			return serviceSettings.getWebSenseServiceHost();
		}
	}
	
	public boolean isConfigHttpPort(ProxyType type){
		int port = getHttpPort(type);
		return port > 0;
	}

	public int getHttpPort(ProxyType type) {
		if(type == ProxyType.barracuda){
			return serviceSettings.getServicePort();
		}else{
			return serviceSettings.getPort();
		}
	}
	
	public boolean isConfigHttpsPort(ProxyType type){
		int port = getHttpsPort(type);
		return port > 0;
	}

	public String getHttpsHost(ProxyType type) {
		if(type == ProxyType.barracuda){
			return serviceSettings.getServiceHost();
		}else{
			return serviceSettings.getWebSenseServiceHost();
		}
	}

	public int getHttpsPort(ProxyType type) {
		if(type == ProxyType.barracuda){
			return serviceSettings.getServicePort();
		}else{
			return serviceSettings.getPort();
		}
	}

	public int getSubnetSize(ProxyType type) {
		if(subNetMap.get(type) != null){
			return subNetMap.get(type).size();
		}else{
			return 0;
		}
	}

	public String getSubnetValue(ProxyType type, int index) {
		return subNetMap.get(type).get(index).getSubnet();
	}
	
	public AhAllowBlockValue getSubnetAction(ProxyType type, int index){
		return subNetMap.get(type).get(index).getAction();
	}

	public String getAccountId(ProxyType type) {
		if(type == ProxyType.barracuda){
			return serviceSettings.getAuthorizationKey();
		}else{
			return serviceSettings.getAccountID();
		}
	}

	public String getDefaultUsername(ProxyType type) {
		if(type == ProxyType.barracuda){
			return serviceSettings.getBarracudaDefaultUserName();
		}else if(type == ProxyType.websense){
			return serviceSettings.getWebSenseDefaultUserName();
		}else{
			return null;
		}
	}
	
	public boolean isConfigDefaultDomain(ProxyType type){
		String domainName = getDefaultDomain(type);
		return domainName != null && !"".equals(domainName);
	}
	
	public String getDefaultDomain(ProxyType type) {
		if(type == ProxyType.barracuda){
			return serviceSettings.getWindowsDomain();
		}else{
			return serviceSettings.getDefaultDomain();
		}
	}
	
	public boolean isEnableWebProxy(ProxyType type){
		return true;
	}

	public boolean isConfigHttpsHost(ProxyType type) {
		String host = this.getHttpsHost(type);
		return host != null && !"".equals(host);
	}

	public boolean isConfigAccountId(ProxyType type) {
		String accountId = this.getAccountId(type);
		return accountId != null && !"".equals(accountId);
	}

	public boolean isConfigDefaultUsername(ProxyType type) {
		String userName = this.getDefaultUsername(type);
		return userName != null && !"".equals(userName);
	}
	
	public boolean isConfigAccountKey(ProxyType type){
		String key = getAccountKey(type);
		return key != null && !"".equals(key);
	}
	
	public String getAccountKey(ProxyType type){
		if(type == ProxyType.barracuda){
			return null;
		}else{
			return serviceSettings.getSecurityKey();
		}
	}
	
	public int getWhitelistSize(ProxyType type){
		if(type == ProxyType.websense){
			if(serviceSettings.getWebsenseWhitelist() == null || serviceSettings.getWebsenseWhitelist().getItems() == null){
				return 0;
			}else{
				return serviceSettings.getWebsenseWhitelist().getItems().size();
			}
		}else if(type == ProxyType.barracuda){
			if(serviceSettings.getBarracudaWhitelist() == null || serviceSettings.getBarracudaWhitelist().getItems() == null){
				return 0;
			}else{
				return serviceSettings.getBarracudaWhitelist().getItems().size();
			}
		}else{
			return 0;
		}
	}
	
	public String getWhitelistName(ProxyType type, int index){
		DomainObject list = null;
		if(type == ProxyType.websense){
			list = serviceSettings.getWebsenseWhitelist();
		}else{
			list = serviceSettings.getBarracudaWhitelist();
		}
		return list.getItems().get(index).getDomainName();
	}
	
	public static class SubAction{
		
		private String subnet;
		private AhAllowBlockValue action;
		
		public String getSubnet() {
			return subnet;
		}
		public void setSubnet(String subnet) {
			this.subnet = subnet;
		}
		public AhAllowBlockValue getAction() {
			return action;
		}
		public void setAction(AhAllowBlockValue action) {
			this.action = action;
		}
		
	}

	@Override
	public boolean isEnableOpenDNS() {
		if(serviceSettings != null){
			return serviceSettings.isEnableOpenDNS() && hiveAp.isBR200();
		}
		return false;		
	}

	@Override
	public String getOpenDNSDID(Long userProfileID) {
		List<OpenDNSMapping> mappings = MgrUtil.getQueryEntity().executeQuery(OpenDNSMapping.class, null, new FilterParams("userProfileId=:s1 and openDNSAccount.id=:s2", new Object[]{userProfileID, activeAccount.getId()}),hiveAp.getOwner().getId(), new WebSecurityQueryBo());
		if(!mappings.isEmpty()){
			OpenDNSDevice device = mappings.get(0).getOpenDNSDevice();
			if(device != null){
				return device.getDeviceId();
			}
		}
		
		return null;
	}

	@Override
	public String getOpenDNSServer1() {
		if(activeAccount != null){
			return activeAccount.getDnsServer1();
		}
		return null;	
	}

	@Override
	public String getOpenDNSServer2() {
		if(activeAccount != null){
			return activeAccount.getDnsServer2();
		}
		return null;	
	}
	
	static class WebSecurityQueryBo implements QueryBo {

		@Override
		public Collection<HmBo> load(HmBo bo) {
			if(bo instanceof OpenDNSMapping){
				OpenDNSMapping mapping = (OpenDNSMapping)bo;
				OpenDNSDevice device = mapping.getOpenDNSDevice();
				if(device != null){
					device.getId();
				}
			}
			return null;
		}
		
	}

}
