package com.ah.be.config.create;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.NtpProfileInt;
import com.ah.xml.be.config.NtpObj;
import com.ah.xml.be.config.NtpServer;
import com.ah.xml.be.config.NtpServerRank;

/**
 * 
 * @author zhang
 *
 */
public class CreateNtpTree {

	private NtpProfileInt ntpImpl;
	private NtpObj ntpObj;
	
	private GenerateXMLDebug oDebug;
	
	public CreateNtpTree(NtpProfileInt ntpImpl, GenerateXMLDebug oDebug){
		this.ntpImpl = ntpImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		if(ntpImpl.isConfigureNtp()){
			ntpObj = new NtpObj();
			generateNtpLevel_1();
		}
	}
	
	public NtpObj getNtpObj(){
		return ntpObj;
	}
	
	private void generateNtpLevel_1() throws Exception{
		/**
		 * <ntp>		NtpObj
		 */
		
		/** element: <ntp>.<enable> */
		oDebug.debug("/configuration/ntp", 
				"enable", GenerateXMLDebug.SET_OPERATION,
				ntpImpl.getNtpGuiName(), ntpImpl.getNtpName());
		ntpObj.setEnable(
				CLICommonFunc.getAhOnlyAct(ntpImpl.isEnableNtpServer())
		);
		
		if(ntpImpl.isEnableNtpServer()){
			
			/** element: <interval> */
			oDebug.debug("/configuration/ntp", 
					"interval", GenerateXMLDebug.SET_VALUE,
					ntpImpl.getNtpGuiName(), ntpImpl.getNtpName());
			Object[][] intervalParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, ntpImpl.getIntervalValue()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			ntpObj.setInterval(
					(NtpObj.Interval)CLICommonFunc.createObjectWithName(NtpObj.Interval.class, intervalParm)
			);
			
			/** element: <server> */
			for(int i=0; i<ntpImpl.getNtpServerSize(); i++){
				
//				oDebug.debug("/configuration/ntp", 
//						"server", GenerateXMLDebug.SET_NAME,
//						ntpImpl.getNtpGuiName(), ntpImpl.getNtpName());
				ntpObj.getServer().add(createNtpServer(i));
			}
		}
		
	}
	
	private NtpServer createNtpServer(int index) throws CreateXMLException{
		NtpServer oNtpServer = new NtpServer();
		
		/** attribute: name */
		oDebug.debug("/configuration/ntp", 
				"server", GenerateXMLDebug.SET_NAME,
				ntpImpl.getNtpGuiName(), ntpImpl.getNtpName());
		oNtpServer.setName(ntpImpl.getNtpServerAddress(index));
		
		/** attribute: operation */
		oNtpServer.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <ntp>.<server>.<cr> */
		oDebug.debug("/configuration/ntp/server[@name='"+oNtpServer.getName()+"']", 
				"cr", GenerateXMLDebug.CONFIG_ELEMENT,
				ntpImpl.getNtpGuiName(), ntpImpl.getNtpName());
		if(ntpImpl.isNtpServerFirst(index)){
			oNtpServer.setCr(this.createNtpServerRank(index));
		}
		
		/** element: <ntp>.<server>.<second> */
		oDebug.debug("/configuration/ntp/server[@name='"+oNtpServer.getName()+"']", 
				"second", GenerateXMLDebug.CONFIG_ELEMENT,
				ntpImpl.getNtpGuiName(), ntpImpl.getNtpName());
		if(ntpImpl.isNtpServerSecond(index)){
			oNtpServer.setSecond(this.createNtpServerRank(index));
		}
		
		/** element: <ntp>.<server>.<third> */
		oDebug.debug("/configuration/ntp/server[@name='"+oNtpServer.getName()+"']", 
				"third", GenerateXMLDebug.CONFIG_ELEMENT,
				ntpImpl.getNtpGuiName(), ntpImpl.getNtpName());
		if(ntpImpl.isNtpServerThird(index)){
			oNtpServer.setThird(this.createNtpServerRank(index));
		}
		
		/** element: <ntp>.<server>.<fourth> */
		oDebug.debug("/configuration/ntp/server[@name='"+oNtpServer.getName()+"']", 
				"fourth", GenerateXMLDebug.CONFIG_ELEMENT,
				ntpImpl.getNtpGuiName(), ntpImpl.getNtpName());
		if(ntpImpl.isNtpServerFourth(index)){
			oNtpServer.setFourth(this.createNtpServerRank(index));
		}
		
		return oNtpServer;
	}
	
	private NtpServerRank createNtpServerRank(int index) throws CreateXMLException{
		NtpServerRank ntpRank = new NtpServerRank();
		
		/** attribute: operation */
		ntpRank.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <cr> */
		ntpRank.setCr("");
		
		/** element: <via-vpn-tunnel> */
		if(ntpImpl.isEnableVpnTunnel(ntpImpl.getNtpServerAddress(index))){
			ntpRank.setViaVpnTunnel(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		return ntpRank;
	}
}
