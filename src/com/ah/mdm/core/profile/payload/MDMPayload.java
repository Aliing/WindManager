package com.ah.mdm.core.profile.payload;

import org.dom4j.Element;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.MdmProfileInfo;

public class MDMPayload extends ProfilePayload
{

	public MDMPayload(AbstractProfileInfo model)
	{
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		super.fillPayloadContent(parentNode);
		MdmProfileInfo m = (MdmProfileInfo) model;
		addElement(parentNode, ACCESS_RIGHTS, m.getAccessRights());
		addElement(parentNode, CHECK_IN_URL, m.getCheckInURL());
		addElement(parentNode, CHECK_OUT_WHEN_REMOVED, m.isCheckOutWhenRemoved());
		addElement(parentNode, IDENTITY_CERTIFICATE_UUID, m.getIdentityCertificateUUID());
		addElement(parentNode, SERVER_URL, m.getServerURL());
		addElement(parentNode, SIGN_MESSAGE, m.isSignMessage());
		addElement(parentNode, TOPIC, m.getTopic());
		addElement(parentNode, USE_DEVELOPMENT_APNS, m.isUseDevelopmentAPNS());
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		MdmProfileInfo m = (MdmProfileInfo) super.parse(dictElement);
		String s = getValue(dictElement, "AccessRights", false);
		m.setAccessRights(s == null ? m.getAccessRights() : Integer.valueOf(s));

		s = getValue(dictElement, "CheckInURL", false);
		m.setCheckInURL(s == null ? m.getCheckInURL() : s);

		s = getValue(dictElement, "CheckOutWhenRemoved", true);
		m.setCheckOutWhenRemoved(s == null ? m.isCheckOutWhenRemoved() : Boolean.valueOf(s));

		s = getValue(dictElement, "IdentityCertificateUUID", false);
		m.setIdentityCertificateUUID(s == null ? m.getIdentityCertificateUUID() : s);

		s = getValue(dictElement, "ServerURL", false);
		m.setServerURL(s == null ? m.getServerURL() : s);

		s = getValue(dictElement, "SignMessage", true);
		m.setSignMessage(s == null ? m.isSignMessage() : Boolean.valueOf(s));

		s = getValue(dictElement, "Topic", false);
		m.setTopic(s == null ? m.getTopic() : s);

		s = getValue(dictElement, "UseDevelopmentAPNS", true);
		m.setUseDevelopmentAPNS(s == null ? m.isUseDevelopmentAPNS() : Boolean.valueOf(s));
		return m;
	}
}
