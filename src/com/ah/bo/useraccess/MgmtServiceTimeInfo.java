package com.ah.bo.useraccess;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.HmBo;
import com.ah.bo.network.IpAddress;

@Embeddable
public class MgmtServiceTimeInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MGMT_SERVICE_IP_ADDRESS_ID")
	private IpAddress ipAddress;
	
	@Column(length = HmBo.DEFAULT_DESCRIPTION_LENGTH)
	private String timeDescription;

	@Transient
    public String[] getFieldValues(){
    	String[] fieldValues={"MGMT_SERVICE_TIME_ID","MGMT_SERVICE_IP_ADDRESS_ID","type",
    			"timeDescription","serverName"};
    	return fieldValues;
    }
	
	public String getTimeDescription() {
		return timeDescription;
	}

	public void setTimeDescription(String timeDescription) {
		this.timeDescription = timeDescription;
	}

	public IpAddress getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(IpAddress ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	@Transient
	public String getServerName()
	{
		return this.getIpAddress().getAddressName();
	}

}