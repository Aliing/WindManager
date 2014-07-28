package com.ah.be.config.create;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.MacObjectInt;
import com.ah.xml.be.config.MacObjectObj;

public class CreateMacObjectTree {
	
	private MacObjectInt macObjectImpl;
	private GenerateXMLDebug oDebug;
	
	private MacObjectObj macObject;

	public CreateMacObjectTree(MacObjectInt macObjectImpl, GenerateXMLDebug oDebug){
		this.macObjectImpl = macObjectImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws CreateXMLException{
		macObject = new MacObjectObj();
		generateMacObjectLevel_1();
	}
	
	public MacObjectObj getMacObjectObj(){
		return this.macObject;
	}
	
	private void generateMacObjectLevel_1() throws CreateXMLException{
		
		/** attribute: operation */
		macObject.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		macObject.setName(macObjectImpl.getMacObjectName());
		
		/** element: <mac-range> */
		for(int index=0; index<macObjectImpl.getMacObjectSize(); index++){
			macObject.getMacRange().add(CLICommonFunc.createAhNameActValueQuoteProhibited(
					macObjectImpl.getMacRange(index), true, true));
		}
	}
}
