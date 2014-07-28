package com.ah.be.admin.hhmoperate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VhmLoginInfoCache {

	private static VhmLoginInfoCache	instance;

	private static final Set<String> vhmLoginInfo = new HashSet<String>();

	public synchronized static VhmLoginInfoCache getInstance() {
		if (instance == null) {
			instance = new VhmLoginInfoCache();
		}

		return instance;
	}

	public List<String> getInfo() {
		List<String> strs = new ArrayList<String>();
		strs.addAll(vhmLoginInfo);

		vhmLoginInfo.clear();

		return strs;
	}

	public void putInfo(String vhmName) {
		vhmLoginInfo.add(vhmName);
	}

}