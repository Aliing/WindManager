package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class DnsSpecificSettings implements Serializable {

	private static final long serialVersionUID = 7818415077220299368L;

	private String domainName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "specificDNS")
	private IpAddress dnsServer;

	/**
	 * @return the domainName
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * @param domainName
	 *            the domainName to set
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	/**
	 * @return the dnsServer
	 */
	public IpAddress getDnsServer() {
		return dnsServer;
	}

	/**
	 * @param dnsServer 
	 *		the dnsServer to set
	 */
	public void setDnsServer(IpAddress dnsServer) {
		this.dnsServer = dnsServer;
	}


}
