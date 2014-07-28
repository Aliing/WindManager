package com.ah.mdm.core.profile.payload;

import org.dom4j.Element;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.ExchangeProfileInfo;

public class ExchangePayload extends ProfilePayload
{

	public ExchangePayload(AbstractProfileInfo model)
	{
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		super.fillPayloadContent(parentNode);
		ExchangeProfileInfo m = (ExchangeProfileInfo) model;
		addElement(parentNode, "EmailAddress", m.getEmailAddress());
		addElement(parentNode, "Host", m.getHost());
		addElement(parentNode, "MailNumberOfPastDaysToSync", m.getMailNumberOfPastDaysToSync());
		addElement(parentNode, "SSL", m.isSsl());
		if (m.getUserName() != null && !m.getUserName().isEmpty())
		{
			addElement(parentNode, "UserName", m.getUserName());
		}
		if (m.getPassword() != null && !m.getPassword().isEmpty())
		{
			addElement(parentNode, "Password", m.getPassword());
		}
		// addElement(parentNode, "Certificate", m.getCertificate());
		// addElement(parentNode, "CertificateName", m.getCertificateName());
		// addElement(parentNode, "CertificatePassword",
		// m.getCertificatePassword());
		addElement(parentNode, "PreventMove", m.isPreventMove());
		addElement(parentNode, "PreventAppSheet", m.isPreventAppSheet());
		if (m.getPayloadCertificateUUID() != null && !m.getPayloadCertificateUUID().isEmpty())
		{
			addElement(parentNode, "PayloadCertificateUUID", m.getPayloadCertificateUUID());
		}
		addElement(parentNode, "SMIMEEnabled", m.isSmimeEnabled());
		if (m.isSmimeEnabled())
		{
			addElement(parentNode, "SMIMESigningCertificateUUID", m.getSmimeSigningCertificateUUID());
			addElement(parentNode, "SMIMEEncryptionCertificateUUID", m.getSmimeEncryptionCertificateUUID());
		}
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		ExchangeProfileInfo m = (ExchangeProfileInfo) super.parse(dictElement);
		String emailAddress = getValue(dictElement, "EmailAddress", false);
		m.setEmailAddress(emailAddress == null ? m.getEmailAddress() : emailAddress);

		String host = getValue(dictElement, "Host", false);
		m.setHost(host == null ? m.getHost() : host);

		String mailNumberOfPastDaysToSync = getValue(dictElement, "MailNumberOfPastDaysToSync", false);
		m.setMailNumberOfPastDaysToSync(mailNumberOfPastDaysToSync == null ? m.getMailNumberOfPastDaysToSync() : Integer
				.valueOf(mailNumberOfPastDaysToSync));

		String password = getValue(dictElement, "Password", false);
		m.setPassword(password == null ? m.getPassword() : password);

		String payloadCertificateUUID = getValue(dictElement, "PayloadCertificateUUID", false);
		m.setPayloadCertificateUUID(payloadCertificateUUID == null ? m.getPayloadCertificateUUID() : payloadCertificateUUID);

		String preventAppSheet = getValue(dictElement, "PreventAppSheet", true);
		m.setPreventAppSheet(preventAppSheet == null ? m.isPreventAppSheet() : Boolean.valueOf(preventAppSheet));

		String preventMove = getValue(dictElement, "PreventMove", true);
		m.setPreventMove(preventMove == null ? m.isPreventMove() : Boolean.valueOf(preventMove));

		String sMIMEEnabled = getValue(dictElement, "SMIMEEnabled", true);
		m.setSmimeEnabled(sMIMEEnabled == null ? m.isSmimeEnabled() : Boolean.valueOf(sMIMEEnabled));

		String ssl = getValue(dictElement, "SSL", true);
		m.setSsl(ssl == null ? m.isSsl() : Boolean.valueOf(ssl));

		String username = getValue(dictElement, "UserName", false);
		m.setUserName(username == null ? m.getUserName() : username);

		String signUUID = getValue(dictElement, "SMIMESigningCertificateUUID", false);
		m.setSmimeSigningCertificateUUID(signUUID == null ? m.getSmimeSigningCertificateUUID() : signUUID);

		String encryUUID = getValue(dictElement, "SMIMEEncryptionCertificateUUID", false);
		m.setSmimeEncryptionCertificateUUID(encryUUID == null ? m.getSmimeEncryptionCertificateUUID() : encryUUID);

		// String cert = getValue(dictElement, "Certificate", false);
		// try
		// {
		// m.setCertificate(cert == null ? m.getCertificate() :
		// cert.getBytes("UTF-8"));
		// } catch (UnsupportedEncodingException e)
		// {
		// e.printStackTrace();
		// }
		//
		// String certName = getValue(dictElement, "CertificateName", false);
		// m.setCertificateName(certName == null ? m.getCertificateName() :
		// certName);
		//
		// String certPassword = getValue(dictElement, "CertificatePassword",
		// false);
		// m.setCertificatePassword(certPassword == null ?
		// m.getCertificatePassword() : certPassword);
		return m;
	}
}
