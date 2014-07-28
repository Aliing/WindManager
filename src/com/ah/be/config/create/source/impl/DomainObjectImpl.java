package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.DomainObjectInt;
import com.ah.bo.network.DomainObject;

public class DomainObjectImpl implements DomainObjectInt {

	private DomainObject domainObj;
	
	public DomainObjectImpl(DomainObject domainObj){
		this.domainObj = domainObj;
	}
	
	public String getDomainObjectName(){
		return domainObj.getObjName();
	}
	
	public int getDomainSize(){
		if(domainObj.getItems() == null){
			return 0;
		}else{
			return domainObj.getItems().size();
		}
	}
	
	public String getDomainObjName(int index){
		return domainObj.getItems().get(index).getDomainName();
	}
}
