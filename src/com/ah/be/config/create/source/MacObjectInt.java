package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;

public interface MacObjectInt {

	public String getMacObjectName();
	
	public int getMacObjectSize();
	
	public String getMacRange(int index) throws CreateXMLException;
}
