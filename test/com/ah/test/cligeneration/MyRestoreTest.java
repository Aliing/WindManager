package com.ah.test.cligeneration;

import com.ah.be.admin.restoredb.RestoreRoutingPolicy;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.VpnService;

public class MyRestoreTest {
	
	private void initData() throws Exception {
		

//		HmDomain owner = new HmDomain();
//		owner.setDomainName("vhm1");
//		QueryUtil.createBo(owner);
//		System.out.println("owner id:" + owner.getId());
//		
//		VpnService vpnService = new VpnService();
//		vpnService.setOwner(owner);
//		vpnService.setProfileName("vpnservice1");
//		vpnService.setUpgradeFlag(true);
//		vpnService.setIpsecVpnType(VpnService.IPSEC_VPN_LAYER_3);
//		QueryUtil.createBo(vpnService);
//		System.out.println("vpnservice id:" + vpnService.getId());
		
		
		HmDomain owner = QueryUtil.findBoById(HmDomain.class, 1L);
		VpnService vpnService = QueryUtil.findBoByAttribute(VpnService.class, "profileName", "vpnservice1");
		ConfigTemplate template = new ConfigTemplate();
		template.setOwner(owner);
		template.setConfigName("configtemplate1");
		template.setVpnService(vpnService);
		QueryUtil.createBo(template);
		System.out.println("template id:" + template.getId());
		
		
	}
	
	public void execute() {
		RestoreRoutingPolicy.RestoreRoutingPolicyByVpnService();
	}

	public static void main(String[] args) throws Exception {
		MyRestoreTest test = new MyRestoreTest();
		//test.initData();
		test.execute();

	}

}
