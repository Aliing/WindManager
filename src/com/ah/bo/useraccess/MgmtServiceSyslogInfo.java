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
import com.ah.util.MgrUtil;

@Embeddable
public class MgmtServiceSyslogInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MGMT_SERVICE_IP_ADDRESS_ID", nullable = true)
	private IpAddress ipAddress;

	@Column(length = HmBo.DEFAULT_DESCRIPTION_LENGTH)
	private String syslogDescription;
	private short severity;

	@Transient
    public String[] getFieldValues(){
    	String[] fieldValues={"MGMT_SERVICE_SYSLOG_ID","MGMT_SERVICE_IP_ADDRESS_ID","type","syslogDescription",
    			"severity","serverName"};
    	return fieldValues;
    }
	
	public short getSeverity() {
		return severity;
	}
	
	public void setSeverity(short severity) {
		this.severity = severity;
	}
	
	public IpAddress getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(IpAddress ipAddress) {
		this.ipAddress = ipAddress;
	}
	public enum EnumSeverity{
		Emergency,Alert,Gritical,Error,Warning,Notification,Info,Debug;
		public String getKey(){
			return name();
		}
		public String getValue() {
			return MgrUtil.getUserMessage(name());
		}
	}

	public enum EnumFacility{
		Auth,Authpriv,Security,User,Local0,Local1,Local2,Local3,Local4,Local5,Local6,Local7;
		public String getKey(){
			return name();
		}
		public String getValue() {
			return MgrUtil.getUserMessage(name());
		}
	}
	public String getSyslogDescription() {
		return syslogDescription;
	}

	public void setSyslogDescription(String syslogDescription) {
		this.syslogDescription = syslogDescription;
	}
	
	@Transient
	public String getServerName() {
		return this.getIpAddress().getAddressName();
	}

}