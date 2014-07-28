package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Embeddable;


@Embeddable
public class RoutingProfilePolicyRule implements Serializable, Cloneable {

	private static final long serialVersionUID          	  = 1L;
	public static final int MATCHMAP_SOURCE_ANY               = 0;
	public static final int MATCHMAP_SOURCE_IPRANGE   		  = 1;
	public static final int MATCHMAP_SOURCE_NETWORK   		  = 2;
	public static final int MATCHMAP_SOURCE_INTERFACE         = 3;
	public static final int MATCHMAP_SOURCE_USERPROFILE       = 4;
	public static final int MATCHMAP_DESTINTION_ANY           = 0;
	public static final int MATCHMAP_DESTINTION_IPRANGE       = 1;
	public static final int MATCHMAP_DESTINTION_NETWORK       = 2;
	public static final int MATCHMAP_DESTINTION_HOSTNAME      = 3;
	public static final int MATCHMAP_DESTINTION_PRIVATE       = 4;

	public static final String USER_PROFILES_ANY_GUEST="Any Guest";
    public static final String DEVICE_TYPE_DROP_VALUE = "-1";
    public static final String DEVICE_TYPE_CORPORATE_NETWORK_VPN_VALUE = "-2";
    public static final String DEVICE_TYPE_PRIMARY_WAN_VALUE = "-10";
    public static final String DEVICE_TYPE_BACKUP_WAN_1_VALUE = "-11";
    public static final String DEVICE_TYPE_BACKUP_WAN_2_VALUE = "-12";
    public static final String DEVICE_TYPE_BLANK_VALUE = "";

//	private String sourcename;
	private int sourcetype;
	private String sourcevalue;
//	private String destinationname;
	private int destinationtype;
	private String destinationvalue;
	private String out1;
	private String out2;
	private String out3;
	private String out4;
	
	

	public String getOut4() {
		return out4;
	}
	public void setOut4(String out4) {
		this.out4 = out4;
	}

	private int priority;

//	public String getSourcename() {
//		return sourcename;
//	}
//	public void setSourcename(String sourcename) {
//		this.sourcename = sourcename;
//	}
	
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
//	public String getDestinationname() {
//		return destinationname;
//	}
//	public void setDestinationname(String destinationname) {
//		this.destinationname = destinationname;
//	}
	
	
	public String getSourcevalue() {
		return sourcevalue;
	}
	public void setSourcevalue(String sourcevalue) {
		this.sourcevalue = sourcevalue;
	}
	
	public int getSourcetype() {
		return sourcetype;
	}
	public void setSourcetype(int sourcetype) {
		this.sourcetype = sourcetype;
	}
	public int getDestinationtype() {
		return destinationtype;
	}
	public void setDestinationtype(int destinationtype) {
		this.destinationtype = destinationtype;
	}
	public String getDestinationvalue() {
		return destinationvalue;
	}
	public void setDestinationvalue(String destinationvalue) {
		this.destinationvalue = destinationvalue;
	}
	public String getOut1() {
		return out1;
	}
	public void setOut1(String out1) {
		this.out1 = out1;
	}
	public String getOut2() {
		return out2;
	}
	public void setOut2(String out2) {
		this.out2 = out2;
	}
	public String getOut3() {
		return out3;
	}
	public void setOut3(String out3) {
		this.out3 = out3;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	@Override
	public RoutingProfilePolicyRule clone() {
		try {
			return (RoutingProfilePolicyRule) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	

	
}
