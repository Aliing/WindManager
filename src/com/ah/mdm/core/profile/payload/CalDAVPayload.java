package com.ah.mdm.core.profile.payload;

import org.dom4j.Element;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.CalDavProfileInfo;

public class CalDAVPayload extends ProfilePayload
{

	public CalDAVPayload(AbstractProfileInfo model)
	{
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		super.fillPayloadContent(parentNode);
		CalDavProfileInfo m = (CalDavProfileInfo) model;
		addElement(parentNode, CALDAV_ACCOUNT_DESCRIPTION, m.getAccountDescription());
		addElement(parentNode, CALDAV_HOST_NAME, m.getHostName());
		addElement(parentNode, CALDAV_USERNAME, m.getUsername());
		addElement(parentNode, CALDAV_PASSWORD, m.getPassword());
		addElement(parentNode, CALDAV_USE_SSL, m.isUseSSL());
		addElement(parentNode, CALDAV_PORT, m.getPort());
		addElement(parentNode, CALDAV_PRINCIPAL_URL, m.getPrincipalURL());
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		CalDavProfileInfo m = (CalDavProfileInfo) super.parse(dictElement);
		String desc = getValue(dictElement, "CalDAVAccountDescription", false);
		m.setAccountDescription(desc == null ? m.getAccountDescription() : desc);

		String hostname = getValue(dictElement, "CalDAVHostName", false);
		m.setHostName(hostname == null ? m.getHostName() : hostname);

		String password = getValue(dictElement, "CalDAVPassword", false);
		m.setPassword(password == null ? m.getPassword() : password);

		String port = getValue(dictElement, "CalDAVPort", false);
		m.setPort(port == null ? m.getPort() : Integer.valueOf(port));

		String url = getValue(dictElement, "CalDAVPrincipalURL", false);
		m.setPrincipalURL(url == null ? m.getPrincipalURL() : url);

		String useSSL = getValue(dictElement, "CalDAVUseSSL", true);
		m.setUseSSL(useSSL == null ? m.isUseSSL() : Boolean.valueOf(useSSL));

		String userName = getValue(dictElement, "CalDAVUsername", false);
		m.setUsername(userName == null ? m.getUsername() : userName);
		return m;
	}

}
