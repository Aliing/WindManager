package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceTime;

@Embeddable
public class DeviceCVGDepend implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CVG_MGT0_NETWORK_ID")
	private VpnNetwork mgtNetwork;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CVG_MGT0_VLAN_ID")
	private Vlan mgtVlan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CVG_DNS_ID")
	private MgmtServiceDns dnsForCVG;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CVG_NTP_ID")
	private MgmtServiceTime ntpForCVG;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CVG_SYSLOG_ID")
	private MgmtServiceSyslog mgmtServiceSyslog;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CVG_SNMP_ID")
	private MgmtServiceSnmp mgmtServiceSnmp;
	
	public VpnNetwork getMgtNetwork() {
		return mgtNetwork;
	}

	public void setMgtNetwork(VpnNetwork mgtNetwork) {
		this.mgtNetwork = mgtNetwork;
	}

	public MgmtServiceDns getDnsForCVG() {
		return dnsForCVG;
	}

	public void setDnsForCVG(MgmtServiceDns dnsForCVG) {
		this.dnsForCVG = dnsForCVG;
	}

	public MgmtServiceTime getNtpForCVG() {
		return ntpForCVG;
	}

	public void setNtpForCVG(MgmtServiceTime ntpForCVG) {
		this.ntpForCVG = ntpForCVG;
	}

	public Vlan getMgtVlan() {
		return mgtVlan;
	}

	public void setMgtVlan(Vlan mgtVlan) {
		this.mgtVlan = mgtVlan;
	}

	public MgmtServiceSyslog getMgmtServiceSyslog() {
		return mgmtServiceSyslog;
	}

	public void setMgmtServiceSyslog(MgmtServiceSyslog mgmtServiceSyslog) {
		this.mgmtServiceSyslog = mgmtServiceSyslog;
	}

	public MgmtServiceSnmp getMgmtServiceSnmp() {
		return mgmtServiceSnmp;
	}

	public void setMgmtServiceSnmp(MgmtServiceSnmp mgmtServiceSnmp) {
		this.mgmtServiceSnmp = mgmtServiceSnmp;
	}

}
