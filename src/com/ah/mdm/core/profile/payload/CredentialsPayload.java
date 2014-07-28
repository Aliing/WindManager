package com.ah.mdm.core.profile.payload;

import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;

import org.bouncycastle.util.encoders.Base64;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.CredentialsProfileInfo;
import com.ah.mdm.core.profile.entity.ProfilePayloadType;
import com.ah.mdm.core.profile.utils.CertificateParser;

public class CredentialsPayload extends ProfilePayload
{
	private static Logger	logger	= LoggerFactory.getLogger(CredentialsPayload.class);

	public CredentialsPayload(AbstractProfileInfo model)
	{
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		super.fillPayloadContent(parentNode);
		try
		{
			CredentialsProfileInfo m = (CredentialsProfileInfo) model;
			if (m.getPassword() != null)
			{
				addElement(parentNode, "Password", m.getPassword());
			}
			addElement(parentNode, "PayloadCertificateFileName", m.getCertificateFileName());
			String content = new String(m.getCertificateContent(), "ISO-8859-1");
			content = CertificateParser.removeCertStartAndEndStr(content);
			addElement(parentNode, "PayloadContent", content.getBytes("ISO-8859-1"));
		} catch (Exception e)
		{
			logger.error("Exception occured: ", e);
		}
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		CredentialsProfileInfo m = (CredentialsProfileInfo) super.parse(dictElement);
		String s = getValue(dictElement, "PayloadCertificateFileName", false);
		m.setCertificateFileName(s == null ? m.getCertificateFileName() : s);

		String password = getValue(dictElement, "Password", false);
		m.setPassword(password);

		s = getValue(dictElement, "PayloadContent", false);
		try
		{
			m.setCertificateContent(s == null ? m.getCertificateContent() : s.getBytes("ISO-8859-1"));
			if (s != null)
			{
				s = CertificateParser.removeCertStartAndEndStr(s);
				m.setCertificateContent(s.getBytes("ISO-8859-1"));
				X509Certificate certificate = null;
				if (null != m.getPassword() && !"".equals(m.getPassword()))
				{
					ByteArrayInputStream bufferIO = new ByteArrayInputStream(Base64.decode(s));
					certificate = CertificateParser.ananysisP12(bufferIO, m.getPassword().trim().toCharArray());
					m.setType(ProfilePayloadType.PAYLOAD_TYPE_CREDENTIALS_P12);
					m.setUsedBySelect(true);
				} else
				{
					ByteArrayInputStream bufferIO = new ByteArrayInputStream(Base64.decode(m.getCertificateContent()));
					certificate = CertificateParser.analysisCert(bufferIO);
					m.setType(CertificateParser.isCA(certificate) ? ProfilePayloadType.PAYLOAD_TYPE_CREDENTIALS_CRT_CA
							: ProfilePayloadType.PAYLOAD_TYPE_CREDENTIALS_CRT_ENTITY);
					m.setUsedBySelect(false);
				}
				m.setIssuer(CertificateParser.getIssuer(certificate));
				m.setNotAfter(CertificateParser.getNotAfter(certificate));
				m.setNotBefore(CertificateParser.getNotBefore(certificate));
			}
		} catch (Exception e)
		{
			logger.error("Exception occured: ", e);
		}
		return m;
	}

}
