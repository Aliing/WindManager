package com.ah.bo;

import java.io.Serializable;

import com.ah.bo.admin.HmDomain;

/*
 * @author Chris Scheers
 */

public interface HmBoBase extends Serializable, Cloneable {
	public static final short DEFAULT_STRING_LENGTH = 32;
	
	public static final short DEFAULT_SPNSOR_LENGTH = 128;

	public static final short DEFAULT_DESCRIPTION_LENGTH = 64;
	
	public static final short DEFAULT_LENGTH_255 = 255;

	public static final short MAC_ADDRESS_LENGTH = 20;

	public static final short IP_ADDRESS_LENGTH = 15;

	public static final short IP_ADDRESS_NETMASK_LENGTH = 18;
	
	public static final short IP_ADDRESS_WEBPAGE_URL = 256;

	// For access control
	public HmDomain getOwner();

	public void setOwner(HmDomain owner);

	// Label used in error messages
	public String getLabel();
}
