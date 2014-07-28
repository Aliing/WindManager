package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.DnsProfileInt;
import com.ah.xml.be.config.*;

/**
 * 
 * @author zhang
 *
 */
public class CreateDnsTree {
	
	private List<Object> dnsChildList_1 = new ArrayList<Object>();
	
	private DnsObj dnsObj;
	private DnsProfileInt dnsProfileImpl;
	
	private GenerateXMLDebug oDebug;
	
	public CreateDnsTree(DnsProfileInt dnsProfileImpl, GenerateXMLDebug oDebug) {
		this.dnsProfileImpl = dnsProfileImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		if(dnsProfileImpl.isConfigureDns()){
			dnsObj = new DnsObj();
			generateDnsLevel_1();
		}
	}
	
	public DnsObj getDnsObj(){
		return this.dnsObj;
	}
	
	private void generateDnsLevel_1() throws Exception{
		/**
		 * <dns>
		 */
		
		/** element: <dns>.<domain-name> */
		oDebug.debug("/configuration/dns", 
				"domain-name", GenerateXMLDebug.CONFIG_ELEMENT,
				dnsProfileImpl.getMgmtServiceDnsGuiName(), dnsProfileImpl.getMgmtServiceDnsName());
		if(dnsProfileImpl.isConfigDomainName()){
			DnsObj.DomainName domainObj = new DnsObj.DomainName();
			dnsChildList_1.add(domainObj);
			dnsObj.setDomainName(domainObj);
		}
		
		/** element: <dns>.<server-ip> */
		oDebug.debug("/configuration/dns", 
				"server-ip", GenerateXMLDebug.CONFIG_ELEMENT,
				dnsProfileImpl.getMgmtServiceDnsGuiName(), dnsProfileImpl.getMgmtServiceDnsName());
		for(int i=0; i<dnsProfileImpl.getDnsServerIpSize(); i++ ){
			if(i<=2){
				dnsObj.getServerIp().add(createServerIp(i));
			}
		}
		generateDnsLevel_2();
	}
	
	private void generateDnsLevel_2() throws Exception {
		/**
		 * <dns>.<domain-name>		DnsObj.DomainName
		 */
		for(Object childObj : dnsChildList_1){
			
			/** element: <dns>.<domain-name> */
			if(childObj instanceof DnsObj.DomainName){
				DnsObj.DomainName domainObj = (DnsObj.DomainName)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/dns", 
						"domain-name", GenerateXMLDebug.SET_VALUE,
						dnsProfileImpl.getMgmtServiceDnsGuiName(), dnsProfileImpl.getMgmtServiceDnsName());
				domainObj.setValue(dnsProfileImpl.getDnsServerDomainName());
				
				/** attribute: operation */
				domainObj.setOperation(
						CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault())
				);
			}
		}
	}
	
	private DnsObj.ServerIp createServerIp(int index) throws CreateXMLException{
		DnsObj.ServerIp serverIpObj = new DnsObj.ServerIp();
		
		/** attribute: value */
		oDebug.debug("/configuration/dns", 
				"server-ip", GenerateXMLDebug.SET_NAME,
				dnsProfileImpl.getMgmtServiceDnsGuiName(), dnsProfileImpl.getMgmtServiceDnsName());
		serverIpObj.setName(dnsProfileImpl.getDnsServerIp(index));
		
		/** attribute: operation */
		serverIpObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <cr> */
		oDebug.debug("/configuration/dns/server-ip[@name='"+serverIpObj.getName()+"']", 
				"cr", GenerateXMLDebug.CONFIG_ELEMENT,
				dnsProfileImpl.getMgmtServiceDnsGuiName(), dnsProfileImpl.getMgmtServiceDnsName());
		if(dnsProfileImpl.isConfigureFirst(index)){
			serverIpObj.setCr("");
		}
		
		/** element: <second> */
		oDebug.debug("/configuration/dns/server-ip[@name='"+serverIpObj.getName()+"']", 
				"second", GenerateXMLDebug.CONFIG_ELEMENT,
				dnsProfileImpl.getMgmtServiceDnsGuiName(), dnsProfileImpl.getMgmtServiceDnsName());
		if(dnsProfileImpl.isConfigureSecond(index)){
			serverIpObj.setSecond("");
		}
		
		/** element: <third> */
		oDebug.debug("/configuration/dns/server-ip[@name='"+serverIpObj.getName()+"']", 
				"third", GenerateXMLDebug.CONFIG_ELEMENT,
				dnsProfileImpl.getMgmtServiceDnsGuiName(), dnsProfileImpl.getMgmtServiceDnsName());
		if(dnsProfileImpl.isConfigureThird(index)){
			serverIpObj.setThird("");
		}
		
		return serverIpObj;
	}
	
}
