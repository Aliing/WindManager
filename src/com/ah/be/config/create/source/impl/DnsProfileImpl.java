package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.DnsProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceDnsInfo;
import com.ah.util.MgrUtil;

/**
 * 
 * @author zhang
 * 
 */
public class DnsProfileImpl implements DnsProfileInt {

	private HiveAp hiveAp;

	private MgmtServiceDns mgmtServiceDns;
	private List<String> dnsServerIps = new ArrayList<String>();

	public DnsProfileImpl(HiveAp hiveAp) throws CreateXMLException {
		this.hiveAp = hiveAp;

		mgmtServiceDns = hiveAp.getConfigTemplate().getMgmtServiceDns();
		if (mgmtServiceDns != null) {
			for(MgmtServiceDnsInfo dnsInfo : mgmtServiceDns.getDnsInfo()){
				if(dnsInfo == null || dnsInfo.getIpAddress() == null){
					continue;
				}
				String ipAddress = CLICommonFunc.getIpAddress(dnsInfo.getIpAddress(), hiveAp).getIpAddress();
				boolean found = false;
				for(String ipStr : dnsServerIps){
					if(ipAddress.equals(ipStr)){
						found = true;
						break;
					}
				}
				if(!found){
					dnsServerIps.add(ipAddress);
				}
			}
		}
	}
	
	public String getMgmtServiceDnsGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.mgmtServiceDns");
	}
	
	public String getMgmtServiceDnsName(){
		if(mgmtServiceDns != null){
			return mgmtServiceDns.getMgmtName();
		}else{
			return null;
		}
	}

	public String getApVersion() {
		return hiveAp.getSoftVer();
	}

	public boolean isConfigureDns() {
		return mgmtServiceDns != null;
	}
	
	public boolean isConfigDomainName(){
		return mgmtServiceDns.getDomainName() != null && !"".equals(mgmtServiceDns.getDomainName());
	}

	public String getDnsServerDomainName() {
		return mgmtServiceDns.getDomainName();
	}

	public int getDnsServerIpSize() {
		if (dnsServerIps != null) {
			return dnsServerIps.size();
		} else {
			return 0;
		}
	}

	public String getDnsServerIp(int index){
		return dnsServerIps.get(index);
	}
	
	public boolean isConfigureFirst(int index){
		return index == 0;
	}

	public boolean isConfigureSecond(int index) {
		return index == 1;
	}

	public boolean isConfigureThird(int index) {
		return index == 2;
	}

}