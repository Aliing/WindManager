package com.ah.be.config.create;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.DomainObjectInt;
import com.ah.xml.be.config.DomainObjectObj;

public class CreateDomainObjectTree {
	
	private DomainObjectInt domainImpl;
	private GenerateXMLDebug oDebug;
	
	private DomainObjectObj domainObj;

	public CreateDomainObjectTree(DomainObjectInt domainImpl, GenerateXMLDebug oDebug){
		this.domainImpl = domainImpl;
		this.oDebug = oDebug;
	}
	
	public void generate(){
		domainObj = new DomainObjectObj();
		
		generateDomainObjectLevel_1();
	}
	
	public DomainObjectObj getDomainObjectObj(){
		return this.domainObj;
	}
	
	private void generateDomainObjectLevel_1(){
		
		/** attribute: operation */
		domainObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		domainObj.setName(domainImpl.getDomainObjectName());
		
		/** element: <domain-object>.<domain> */
		for(int index=0; index<domainImpl.getDomainSize(); index++){
			domainObj.getDomain().add(CLICommonFunc.createAhNameActValue(
					domainImpl.getDomainObjName(index), CLICommonFunc.getYesDefault()));
		}
	}
}
