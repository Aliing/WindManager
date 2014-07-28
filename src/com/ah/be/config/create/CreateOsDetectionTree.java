package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.OsDetectionInt;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.xml.be.config.OsDetectionMethod;
import com.ah.xml.be.config.OsDetectionObj;

public class CreateOsDetectionTree {
	
	private OsDetectionInt osDetectionImpl;
	private GenerateXMLDebug oDebug;
	private List<Object> aaaChildList_0 = new ArrayList<Object>();
	private OsDetectionObj osDetectionObj;

	public CreateOsDetectionTree(OsDetectionInt osDetectionImpl, GenerateXMLDebug oDebug){
		this.osDetectionImpl = osDetectionImpl;
		this.oDebug = oDebug;
	}
	
	public OsDetectionObj getOsDetectionObj(){
		return this.osDetectionObj;
	}
	
	public void generate(){
		if(osDetectionImpl.isConfigOsDetection()){
			osDetectionObj = new OsDetectionObj();
			generateOsObjectLevel_1();
		}
	}
	
	private void generateOsObjectLevel_1(){
		/** element: <os-detection>.<enable> */
		osDetectionObj.setEnable(CLICommonFunc.getAhOnlyAct(osDetectionImpl.isEnableOsDetection()));
		
		/** element: <os-detection>.<method> */
		if(osDetectionImpl.isEnableOsDetection()) {
			OsDetectionMethod osMethod = new OsDetectionMethod();
			aaaChildList_0.add(osMethod);
			osDetectionObj.setMethod(osMethod);
		}
		
		generateOsObjectLevel_2();
	}
	
	private void generateOsObjectLevel_2(){
		
		for(Object childObj : aaaChildList_0){
			if(childObj instanceof OsDetectionMethod){
				OsDetectionMethod osMethod = (OsDetectionMethod)childObj;
				/** element: <os-detection>.<user-agent|dhcp-option55> */
				if(osDetectionImpl.isConfigOsDetectionMethod(MgmtServiceOption.OS_DETECTION_METHOD_DHCP)){
					osMethod.setUserAgent(CLICommonFunc.getAhOnlyAct(!osDetectionImpl.isEnableOsDetection()));
					osMethod.setDhcpOption55(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}else if(osDetectionImpl.isConfigOsDetectionMethod(MgmtServiceOption.OS_DETECTION_METHOD_HTTP)){
					osMethod.setUserAgent(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
					osMethod.setDhcpOption55(CLICommonFunc.getAhOnlyAct(!osDetectionImpl.isEnableOsDetection()));
				}else if(osDetectionImpl.isConfigOsDetectionMethod(MgmtServiceOption.OS_DETECTION_METHOD_BOTH)){
					osMethod.setUserAgent(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
					osMethod.setDhcpOption55(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
			}
		}
		aaaChildList_0.clear();
	}
}
