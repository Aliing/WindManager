package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.AlgProfileInt;
import com.ah.xml.be.config.AlgDns;
import com.ah.xml.be.config.AlgFtp;
import com.ah.xml.be.config.AlgHttp;
import com.ah.xml.be.config.AlgObj;
import com.ah.xml.be.config.AlgSip;
import com.ah.xml.be.config.AlgTftp;

/**
 * @author zhang
 * @version 2007-12-19 10:35:38
 */

public class CreateAlgTree {
	
	private AlgProfileInt algImpl;
	private AlgObj algObj;
	
	private List<Object> algChildList_1 = new ArrayList<Object>();
	
	private GenerateXMLDebug oDebug;

	public CreateAlgTree(AlgProfileInt algImpl, GenerateXMLDebug oDebug) throws Exception {
		this.algImpl = algImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		oDebug.debug("/configuration", 
				"alg", GenerateXMLDebug.CONFIG_ELEMENT,
				algImpl.getWlanGuiName(), algImpl.getWlanName());
		if(algImpl.isConfigAlg()){
			algObj = new AlgObj();
			generateAlgLevel_1();
		}
	}
	
	public AlgObj getAlgObj(){
		return this.algObj;
	}
	
	private void generateAlgLevel_1() throws Exception {
		/**
		 * <alg>		AlgObj
		 */
		
		/** attribute: updateTime */
		algObj.setUpdateTime(algImpl.getUpdateTime());
		
		/** element: <alg>.<ftp> */
		AlgFtp algFtpObj = new AlgFtp();
		algChildList_1.add(algFtpObj);
		algObj.setFtp(algFtpObj);
		
		/** element: <alg>.<tftp> */
		AlgTftp algTftpObj = new AlgTftp();
		algChildList_1.add(algTftpObj);
		algObj.setTftp(algTftpObj);
		
		/** element: <alg>.<sip> */
		AlgSip algSipObj = new AlgSip();
		algChildList_1.add(algSipObj);
		algObj.setSip(algSipObj);
		
//		remove alg dns for 3.2r2
		/** element: <alg>.<dns> */
		AlgDns algDnsObj =new AlgDns();
		algChildList_1.add(algDnsObj);
		algObj.setDns(algDnsObj);
		
		/** element: <alg>.<http> */
		AlgHttp http = new AlgHttp();
		algChildList_1.add(http);
		algObj.setHttp(http);
		
		generateAlgLevel_2();
	
	}
	
	private void generateAlgLevel_2() throws Exception{
		/**
		 * <alg>.<ftp>				AlgFtp
		 * <alg>.<tftp>				AlgTftp
		 * <alg>.<sip>				AlgSip
		 * <alg>.<dns>				AlgDns
		 * <alg>.<http>				AlgHttp
		 */
		for(Object childObj : algChildList_1){
			
			/** element: <alg>.<ftp> */
			if(childObj instanceof AlgFtp){
				AlgFtp algFtpObj = (AlgFtp)childObj;
				
				/** element: <alg>.<ftp>.<enable> */
				oDebug.debug("/configuration/alg/ftp", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						algImpl.getAlgGuiName(), algImpl.getAlgName());
				algFtpObj.setEnable(CLICommonFunc.getAhOnlyAct(algImpl.isAlgEnable(AlgProfileInt.AlgType.ftp)));
				
				
				if(algImpl.isAlgEnable(AlgProfileInt.AlgType.ftp)){
					
					/** element: <alg>.<ftp>.<inactive-data-timeout> */
					oDebug.debug("/configuration/alg/ftp", 
							"inactive-data-timeout", GenerateXMLDebug.SET_VALUE,
							algImpl.getAlgGuiName(), algImpl.getAlgName());
					Object[][] inactiveParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, algImpl.getInactiveDataTimeout(AlgProfileInt.AlgType.ftp)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					algFtpObj.setInactiveDataTimeout(
							(AlgFtp.InactiveDataTimeout)CLICommonFunc.createObjectWithName(
									AlgFtp.InactiveDataTimeout.class, inactiveParm)
					);
					
					/** element: <alg>.<ftp>.<max-duration> */
					oDebug.debug("/configuration/alg/ftp", 
							"max-duration", GenerateXMLDebug.SET_VALUE,
							algImpl.getAlgGuiName(), algImpl.getAlgName());
					Object[][] maxDurationParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, algImpl.getMaxDuration(AlgProfileInt.AlgType.ftp)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					algFtpObj.setMaxDuration(
							(AlgFtp.MaxDuration)CLICommonFunc.createObjectWithName(
									AlgFtp.MaxDuration.class, maxDurationParm)
					);
					
					/** element: <alg>.<ftp>.<qos> */
					oDebug.debug("/configuration/alg/ftp", 
							"qos", GenerateXMLDebug.SET_VALUE,
							algImpl.getAlgGuiName(), algImpl.getAlgName());
					Object[][] qosParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, algImpl.getQos(AlgProfileInt.AlgType.ftp)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					algFtpObj.setQos(
							(AlgFtp.Qos)CLICommonFunc.createObjectWithName(AlgFtp.Qos.class, qosParm)
					);
				}
			}
			
			/** element: <alg>.<tftp> */
			if(childObj instanceof AlgTftp){
				AlgTftp algTftpObj = (AlgTftp)childObj;
				
				/** element: <alg>.<tftp>.<enable> */
				oDebug.debug("/configuration/alg/tftp", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						algImpl.getAlgGuiName(), algImpl.getAlgName());
				algTftpObj.setEnable(CLICommonFunc.getAhOnlyAct(algImpl.isAlgEnable(AlgProfileInt.AlgType.tftp)));
				
				if(algImpl.isAlgEnable(AlgProfileInt.AlgType.tftp)){
					
					/** element: <alg>.<tftp>.<inactive-data-timeout> */
					oDebug.debug("/configuration/alg/tftp", 
							"inactive-data-timeout", GenerateXMLDebug.SET_VALUE,
							algImpl.getAlgGuiName(), algImpl.getAlgName());
					Object[][] inactiveParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, algImpl.getInactiveDataTimeout(AlgProfileInt.AlgType.tftp)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					algTftpObj.setInactiveDataTimeout(
							(AlgTftp.InactiveDataTimeout)CLICommonFunc.createObjectWithName(
									AlgTftp.InactiveDataTimeout.class, inactiveParm)
					);
					
					/** element: <alg>.<tftp>.<max-duration> */
					oDebug.debug("/configuration/alg/tftp", 
							"max-duration", GenerateXMLDebug.SET_VALUE,
							algImpl.getAlgGuiName(), algImpl.getAlgName());
					Object[][] maxDurationParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, algImpl.getMaxDuration(AlgProfileInt.AlgType.tftp)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					algTftpObj.setMaxDuration(
							(AlgTftp.MaxDuration)CLICommonFunc.createObjectWithName(
									AlgTftp.MaxDuration.class, maxDurationParm)
					);
					
					/** element: <alg>.<tftp>.<qos> */
					oDebug.debug("/configuration/alg/tftp", 
							"qos", GenerateXMLDebug.SET_VALUE,
							algImpl.getAlgGuiName(), algImpl.getAlgName());
					Object[][] qosParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, algImpl.getQos(AlgProfileInt.AlgType.tftp)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					algTftpObj.setQos(
							(AlgTftp.Qos)CLICommonFunc.createObjectWithName(AlgTftp.Qos.class, qosParm)
					);
				}

			}
			
			/** element: <alg>.<sip> */
			if(childObj instanceof AlgSip){
				AlgSip algSipObj = (AlgSip)childObj;
				
				/** element: <alg>.<sip>.<enable> */
				oDebug.debug("/configuration/alg/sip", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						algImpl.getAlgGuiName(), algImpl.getAlgName());
				algSipObj.setEnable(CLICommonFunc.getAhOnlyAct(algImpl.isAlgEnable(AlgProfileInt.AlgType.sip)));
				
				if(algImpl.isAlgEnable(AlgProfileInt.AlgType.sip)){

					/** element: <alg>.<sip>.<inactive-data-timeout> */
					oDebug.debug("/configuration/alg/sip", 
							"inactive-data-timeout", GenerateXMLDebug.SET_VALUE,
							algImpl.getAlgGuiName(), algImpl.getAlgName());
					Object[][] inactiveParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, algImpl.getInactiveDataTimeout(AlgProfileInt.AlgType.sip)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					algSipObj.setInactiveDataTimeout(
							(AlgSip.InactiveDataTimeout)CLICommonFunc.createObjectWithName(
									AlgSip.InactiveDataTimeout.class, inactiveParm)
					);
					
					/** element: <alg>.<sip>.<max-duration> */
					oDebug.debug("/configuration/alg/sip", 
							"max-duration", GenerateXMLDebug.SET_VALUE,
							algImpl.getAlgGuiName(), algImpl.getAlgName());
					Object[][] maxDurationParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, algImpl.getMaxDuration(AlgProfileInt.AlgType.sip)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					algSipObj.setMaxDuration(
							(AlgSip.MaxDuration)CLICommonFunc.createObjectWithName(
									AlgSip.MaxDuration.class, maxDurationParm)
					);
					
					/** element: <alg>.<sip>.<qos> */
					oDebug.debug("/configuration/alg/sip", 
							"qos", GenerateXMLDebug.SET_VALUE,
							algImpl.getAlgGuiName(), algImpl.getAlgName());
					Object[][] qosParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, algImpl.getQos(AlgProfileInt.AlgType.sip)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					algSipObj.setQos(
							(AlgSip.Qos)CLICommonFunc.createObjectWithName(AlgSip.Qos.class, qosParm)
					);
				}
				
			}
			
			/** element: <alg>.<dns> */
			if(childObj instanceof AlgDns){
				AlgDns dnsObj = (AlgDns)childObj;
				
				/** element: <alg>.<dns>.<enable> */
				oDebug.debug("/configuration/alg/dns", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						algImpl.getAlgGuiName(), algImpl.getAlgName());
				dnsObj.setEnable(CLICommonFunc.getAhOnlyAct(algImpl.isAlgEnable(AlgProfileInt.AlgType.dns)));
				
				
				oDebug.debug("/configuration/alg/dns", 
						"qos", GenerateXMLDebug.CONFIG_ELEMENT,
						algImpl.getAlgGuiName(), algImpl.getAlgName());
				if(algImpl.isAlgEnable(AlgProfileInt.AlgType.dns)){
					
					/** element: <alg>.<dns>.<qos> */
					oDebug.debug("/configuration/alg/dns",
							"qos", GenerateXMLDebug.SET_VALUE,
							algImpl.getAlgGuiName(), algImpl.getAlgName());
					Object[][] qosParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, algImpl.getQos(AlgProfileInt.AlgType.dns)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					dnsObj.setQos(
							(AlgDns.Qos)CLICommonFunc.createObjectWithName(AlgDns.Qos.class, qosParm)
					);
				}
			}
			
			/** element: <alg>.<http> */
			if(childObj instanceof AlgHttp){
				AlgHttp http = (AlgHttp)childObj;
				
				/** element: <alg>.<http>.<enable> */
				http.setEnable(CLICommonFunc.getAhOnlyAct(algImpl.isAlgEnable(AlgProfileInt.AlgType.http)));
			}
		}
		algChildList_1.clear();
	}
}
