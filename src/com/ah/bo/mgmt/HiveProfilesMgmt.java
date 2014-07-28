package com.ah.bo.mgmt;

/*
 * @author Chris Scheers
 */
import com.ah.bo.mobility.HiveProfile;
import com.ah.xml.hiveprofile.XmlHiveProfile;

public interface HiveProfilesMgmt {
	public XmlHiveProfile marshal(HiveProfile hiveProfile);
}
