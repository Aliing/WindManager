package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.OsVersionInt;
import com.ah.bo.network.OsObjectVersion;

public class OsVersionImpl implements OsVersionInt {
	
	private OsObjectVersion osVersion;
	
	public OsVersionImpl(OsObjectVersion osVersion) {
		this.osVersion = osVersion;
	}

	public String getOsVersionName() {
		String name = osVersion.getOsVersion() + OPTION55 + osVersion.getOption55();
		return name;
	}
	
}
