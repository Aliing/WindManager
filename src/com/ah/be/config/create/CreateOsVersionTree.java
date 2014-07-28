package com.ah.be.config.create;

import com.ah.be.config.create.common.GenerateXMLDebug;

import com.ah.be.config.create.source.OsVersionInt;
import com.ah.xml.be.config.AhNameActValueQuoteProhibited;

public class CreateOsVersionTree {
	
	private OsVersionInt osVersionImpl;
	private GenerateXMLDebug oDebug;
	
	private AhNameActValueQuoteProhibited osVersionObj;
	
	public CreateOsVersionTree(OsVersionInt osVersionImpl, GenerateXMLDebug oDebug){
		this.osVersionImpl = osVersionImpl;
		this.oDebug = oDebug;
	}
	
	public AhNameActValueQuoteProhibited getOsVersionObj(){
		return this.osVersionObj;
	}
	
	public void generate(){
		osVersionObj = new AhNameActValueQuoteProhibited();
		generateOsObjectLevel_1();
		
	}
	
	private void generateOsObjectLevel_1(){
				
		/**element: <os-version>.<option55> */
		osVersionObj = CLICommonFunc.createAhNameActValueQuoteProhibited(osVersionImpl.getOsVersionName(),
				CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault());
		
	}
}
