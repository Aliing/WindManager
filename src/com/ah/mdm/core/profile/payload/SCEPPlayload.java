package com.ah.mdm.core.profile.payload;

import org.dom4j.Element;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.ScepProfileInfo;

public class SCEPPlayload extends ProfilePayload
{

	public SCEPPlayload(AbstractProfileInfo model)
	{
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		super.fillPayloadContent(parentNode);
		parentNode.addElement(KEY).setText("PayloadContent");
		parentNode = parentNode.addElement(DICT);
		ScepProfileInfo m = (ScepProfileInfo) model;
		addElement(parentNode, "URL", m.getUrl());
		addElement(parentNode, "Name", m.getName());
		addElement(parentNode, "Challenge", m.getChallenge());
		addElement(parentNode, "Key Type", m.getKeyType());
		addElement(parentNode, "Key Usage", m.getKeyUsage());
		addElement(parentNode, "Keysize", m.getKeySize());
		addElement(parentNode, "Retries", m.getRetries());
		addElement(parentNode, "RetryDelay", m.getRetryDelay());

		// Subject
		parentNode.addElement(KEY).setText("Subject");
		Element subjectArrElement = parentNode.addElement(ARRAY);
		if (m.getSubject() != null)
		{
			String[] ss = m.getSubject().split(",");
			for (String s : ss)
			{
				String[] keyValue = s.split("=");
				if (keyValue.length == 2)
				{
					Element e = subjectArrElement.addElement(ARRAY).addElement(ARRAY);
					e.addElement(STRING).setText(keyValue[0]);
					e.addElement(STRING).setText(keyValue[1]);
				}
			}
		}
		if (!m.getSubjectAltNameType().equals("None"))
		{
			// SubjectAltName
			parentNode.addElement(KEY).setText("SubjectAltName");
			Element nameDict = parentNode.addElement(DICT);
			addElement(nameDict, "ntPrincipalName", m.getNtPrincipalName());
			if (m.getSubjectAltNameType().equals("uniformResourceIdentifier"))
			{
				addElement(nameDict, "uniformResourceIdentifier", m.getAltNameValue());
			} else if (m.getSubjectAltNameType().equals("rfc822Name"))
			{
				addElement(nameDict, "rfc822Name", m.getAltNameValue());
			} else
			{
				addElement(nameDict, "dNSName", m.getAltNameValue());
			}
		}

	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		ScepProfileInfo m = (ScepProfileInfo) super.parse(dictElement);
		Element contentElement = getValueElement(dictElement, "PayloadContent");
		if (contentElement != null)
		{
			Element dict = contentElement;
			String CAFingerprint = getValue(dict, "CAFingerprint", false);
			m.setFingerprint(CAFingerprint == null ? m.getFingerprint() : CAFingerprint);

			String challenge = getValue(dict, "Challenge", false);
			m.setChallenge(challenge == null ? m.getChallenge() : challenge);

			String keyType = getValue(dict, "Key Type", false);
			m.setKeyType(keyType == null ? m.getKeyType() : keyType);

			String keyUsage = getValue(dict, "Key Usage", false);
			m.setKeyUsage(keyUsage == null ? m.getKeyUsage() : Integer.valueOf(keyUsage));

			String keysize = getValue(dict, "Keysize", false);
			m.setKeySize(keysize == null ? m.getKeySize() : Integer.valueOf(keysize));

			String name = getValue(dict, "Name", false);
			m.setName(name == null ? m.getName() : name);

			String retries = getValue(dict, "Retries", false);
			m.setRetries(retries == null ? m.getRetries() : Integer.valueOf(retries));

			String retryDelay = getValue(dict, "RetryDelay", false);
			m.setRetryDelay(retryDelay == null ? m.getRetryDelay() : Integer.valueOf(retryDelay));

			String url = getValue(dict, "URL", false);
			m.setUrl(url == null ? m.getUrl() : url);

			String subjectValue = "";
			Element subjectElement = getValueElement(dict, "Subject");
			if (subjectElement != null)
			{
				Element arr = subjectElement;
				for (Object o : arr.elements())
				{
					Element arrayElement = (Element) o;
					Element e = (Element) arrayElement.elements().get(0);
					subjectValue = subjectValue + ((Element) e.elements().get(0)).getText() + "=" + ((Element) e.elements().get(1)).getText() + ",";
				}
			}

			if (subjectValue.length() != 0)
				m.setSubject(subjectValue.substring(0, subjectValue.length() - 1));
			Element subjectAltNameElement = getValueElement(dict, "SubjectAltName");
			if (subjectAltNameElement == null)
			{
				m.setSubjectAltNameType("None");
			} else
			{
				String ntPrincipalName = getValue(subjectAltNameElement, "ntPrincipalName", false);
				m.setNtPrincipalName(ntPrincipalName);
				Element altNameDict = subjectAltNameElement;

				Element rfcElement = getValueElement(altNameDict, "rfc822Name");
				Element uriElement = getValueElement(altNameDict, "uniformResourceIdentifier");
				Element dnsElement = getValueElement(altNameDict, "dNSName");
				if (rfcElement != null)
				{
					m.setSubjectAltNameType("rfc822Name");
					m.setAltNameValue(rfcElement.getText());
				} else if (uriElement != null)
				{
					m.setSubjectAltNameType("uniformResourceIdentifier");
					m.setAltNameValue(uriElement.getText());
				} else if (dnsElement != null)
				{
					m.setSubjectAltNameType("dNSName");
					m.setAltNameValue(dnsElement.getText());
				}
			}
		}
		return m;
	}
}
