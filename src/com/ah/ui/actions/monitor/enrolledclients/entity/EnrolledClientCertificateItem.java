package com.ah.ui.actions.monitor.enrolledclients.entity;


import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@Embeddable
@XStreamAlias("Cert")
public class EnrolledClientCertificateItem
{	
	@Transient
	@XStreamAlias("CommonName")
	public String commonName;
	@Transient
	@XStreamAlias("Issued")
	public String issued;
	@Transient
	@XStreamAlias("NotBefore")
	public long notBefore;
	@Transient
	@XStreamAlias("NotAfter")
	public long notAfter;
	@Transient
	@XStreamAlias("IsIdentity")
	public String isIdentity;

	public EnrolledClientCertificateItem()
	{
		super();
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getIssued() {
		return issued;
	}

	public void setIssued(String issued) {
		this.issued = issued;
	}

	public long getNotBefore() {
		return notBefore;
	}

	public void setNotBefore(long notBefore) {
		this.notBefore = notBefore;
	}

	public long getNotAfter() {
		return notAfter;
	}

	public void setNotAfter(long notAfter) {
		this.notAfter = notAfter;
	}

	public String getIsIdentity() {
		return isIdentity;
	}

	public void setIsIdentity(String isIdentity) {
		this.isIdentity = isIdentity;
	}

}
