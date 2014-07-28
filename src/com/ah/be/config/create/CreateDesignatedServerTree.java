package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.DesignatedServerInt;
import com.ah.xml.be.config.DesignatedServerIdmProxy;
import com.ah.xml.be.config.DesignatedServerObj;

@Deprecated
public class CreateDesignatedServerTree {
	
	private DesignatedServerInt serverImpl;
	private GenerateXMLDebug oDebug;
	
	private DesignatedServerObj dServer;
	
	private List<Object> designatedChildLevel_1 = new ArrayList<Object>();

	public CreateDesignatedServerTree(DesignatedServerInt serverImpl, GenerateXMLDebug oDebug){
		this.serverImpl = serverImpl;
		this.oDebug = oDebug;
	}
	
	public DesignatedServerObj getDesignatedServerObj(){
		return this.dServer;
	}
	
	public void generate() throws Exception {
		
		/** element: <designated-server> */
		dServer = new DesignatedServerObj();
		
		generateDesignatedServer_1();
	}
	
	private void generateDesignatedServer_1() throws Exception {
		
		/** element: <designated-server>.<idm-proxy> */
		DesignatedServerIdmProxy proxy = new DesignatedServerIdmProxy();
		dServer.setIdmProxy(proxy);
		designatedChildLevel_1.add(proxy);
		
//		generateDesignatedServer_2();
	}
	
//	private void generateDesignatedServer_2() throws Exception{
//		/**
//		 * <designated-server>.<idm-proxy>				DesignatedServerIdmProxy
//		 */
//		for(Object obj : designatedChildLevel_1){
//			
//			/** element: <designated-server>.<idm-proxy> */
//			if(obj instanceof DesignatedServerIdmProxy){
//				DesignatedServerIdmProxy proxy = (DesignatedServerIdmProxy)obj;
//				
//				/** element: <designated-server>.<idm-proxy>.<announce> */
//				proxy.setAnnounce(CLICommonFunc.getAhOnlyAct(serverImpl.isEnableIdmProxyAnnounce()));
//				
//				/** element: <designated-server>.<idm-proxy>.<dynamic> */
//				proxy.setDynamic(CLICommonFunc.getAhOnlyAct(serverImpl.isEnableIdmProxyDynamic()));
//			}
//		}
//		designatedChildLevel_1.clear();
//	}
}
