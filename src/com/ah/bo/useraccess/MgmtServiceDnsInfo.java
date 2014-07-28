package com.ah.bo.useraccess;

import java.io.Serializable;

import com.ah.bo.HmBo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.network.IpAddress;

@Embeddable
public class MgmtServiceDnsInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MGMT_SERVICE_IP_ADDRESS_ID", nullable = true)
	private IpAddress ipAddress;
	@Column(length = HmBo.DEFAULT_STRING_LENGTH)
	private String serverName;
	@Column(length = HmBo.DEFAULT_DESCRIPTION_LENGTH)
	private String dnsDescription;
	private short severity;
	
	@Transient
	public String[] getFieldValues(){
	    String[] fieldValues={"MGMT_SERVICE_DNS_ID","MGMT_SERVICE_IP_ADDRESS_ID",
	    		"serverName","dnsDescription","severity","position"};
	    return fieldValues;
	}
	
	// used for restore order
	@Transient
	private int position;
	
	public short getSeverity() {
		return severity;
	}
	public void setSeverity(short severity) {
		this.severity = severity;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public IpAddress getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(IpAddress ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getDnsDescription() {
		return dnsDescription;
	}
	public void setDnsDescription(String dnsDescription) {
		this.dnsDescription = dnsDescription;
	}
	public int getPosition()
	{
		return position;
	}
	public void setPosition(int position)
	{
		this.position = position;
	}

}