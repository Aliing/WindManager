package com.ah.bo.mgmt.impl;

/*
 * @author Chris Scheers
 */

import com.ah.bo.mgmt.HiveProfilesMgmt;
import com.ah.bo.mobility.HiveProfile;
//import com.ah.util.Tracer;
import com.ah.xml.hiveprofile.XmlHiveProfile;

public final class HiveProfilesMgmtImpl implements HiveProfilesMgmt {

//	private static final Tracer log = new Tracer(HiveProfilesMgmtImpl.class.getSimpleName());

	/*
	 * Singleton, NO class vars !
	 */

	private HiveProfilesMgmtImpl() {
	}

	private static HiveProfilesMgmt instance;

	public synchronized static HiveProfilesMgmt getInstance() {
		if (instance == null) {
			instance = new HiveProfilesMgmtImpl();
		}

		return instance;
	}

	public XmlHiveProfile marshal(HiveProfile hiveProfile) {
		XmlHiveProfile xmlHiveProfile = new XmlHiveProfile();
		xmlHiveProfile.setHiveName(hiveProfile.getHiveName());
		// xmlHiveProfile.setNativeVlan(hiveProfile.getNativeVlan());
		xmlHiveProfile.setFragThreshold(hiveProfile.getFragThreshold());
		xmlHiveProfile.setRtsThreshold(hiveProfile.getRtsThreshold());
		return xmlHiveProfile;
	}

}