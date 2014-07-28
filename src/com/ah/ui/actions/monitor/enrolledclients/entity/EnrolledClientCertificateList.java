package com.ah.ui.actions.monitor.enrolledclients.entity;


import java.util.List;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("content")
public class EnrolledClientCertificateList
{
	@XStreamAlias("CertList")
	private List<EnrolledClientCertificateItem>	certificateList;

	public List<EnrolledClientCertificateItem> getCertificateList() {
		return certificateList;
	}

	public void setCertificateList(
			List<EnrolledClientCertificateItem> certificateList) {
		this.certificateList = certificateList;
	}




}
