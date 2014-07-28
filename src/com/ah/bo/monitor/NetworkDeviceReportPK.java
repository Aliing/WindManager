package com.ah.bo.monitor;


import java.io.Serializable;

import javax.persistence.Embeddable;


@Embeddable
@SuppressWarnings("serial")
public class NetworkDeviceReportPK implements Serializable {
	private long mac ;
	
	private short port ;
	
	public long getMac() {
		return mac;
	}

	public void setMac(long mAC) {
		mac = mAC;
	}

	public short getPort() {
		return port;
	}

	public void setPort(short port) {
		this.port = port;
	}

	public int hashCode() 
	{
		return (PRIME + Long.valueOf( mac).hashCode()) * PRIME + port;
	}
static public final int PRIME = 31;

	public boolean equals(Object obj) 
	{
		if( this == obj )
			return true;
		if( ! (obj instanceof NetworkDeviceReportPK) )
			return false;
		final NetworkDeviceReportPK other = (NetworkDeviceReportPK)obj;
		return mac == other.mac
			&& port == other.port;
	}
}