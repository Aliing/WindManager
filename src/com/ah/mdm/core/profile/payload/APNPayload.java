package com.ah.mdm.core.profile.payload;

import java.util.List;

import org.dom4j.Element;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.ApnProfileInfo;
import com.ah.mdm.core.profile.entity.ProfileApn;

public class APNPayload extends ProfilePayload
{

	public APNPayload(AbstractProfileInfo model)
	{
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		super.fillPayloadContent(parentNode);
		ApnProfileInfo m = (ApnProfileInfo) model;
		List<ProfileApn> apns = m.getApns();
		if (apns != null && !apns.isEmpty())
		{
			parentNode.addElement(KEY).setText(PAYLOAD_CONTENT);
			Element contentArray = parentNode.addElement(ARRAY);
			Element contentDict = contentArray.addElement(DICT);
			contentDict.addElement(KEY).setText(DEFAULTS_DATA);
			Element defaultsDict = contentDict.addElement(DICT);
			defaultsDict.addElement(KEY).setText("apns");
			Element apnArray = defaultsDict.addElement(ARRAY);
			for (ProfileApn apn : apns)
			{
				Element apnDict = apnArray.addElement(DICT);
				addElement(apnDict, APN, apn.getApn(), "");
				addElement(apnDict, USERNAME, apn.getUsername(), "");
				addElement(apnDict, PASSWORD, apn.getPassword(), "");
				if (apn.getProxy() != null)
				{
					addElement(apnDict, PROXY, apn.getProxy(), "");
					addElement(apnDict, PROXY_PORT, apn.getProxyPort());
				}
			}
			contentDict.addElement(KEY).setText(DEFAULTS_DOMAIN_NAME);
			contentDict.addElement(STRING).setText("com.apple.managedCarrier");
		}
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		ApnProfileInfo m = (ApnProfileInfo) super.parse(dictElement);
		List list = dictElement.selectNodes("key[text()=\"PayloadContent\"]/following::array[1]");
		if (!list.isEmpty())
		{
			Element arr = (Element) list.get(0);
			Element apnDict = (Element) arr.selectNodes("dict/dict/array/dict").get(0);
			List l = apnDict.selectNodes("key[text()=\"apn\"]/following::*[1]");
			String apn = l.isEmpty() ? "" : ((Element) l.get(0)).getText();

			l = apnDict.selectNodes("key[text()=\"username\"]/following::*[1]");
			String username = l.isEmpty() ? "" : ((Element) l.get(0)).getText();

			l = apnDict.selectNodes("key[text()=\"password\"]/following::*[1]");
			String password = l.isEmpty() ? "" : ((Element) l.get(0)).getText();

			l = apnDict.selectNodes("key[text()=\"proxy\"]/following::*[1]");
			String proxy = l.isEmpty() ? "" : ((Element) l.get(0)).getText();

			l = apnDict.selectNodes("key[text()=\"proxyPort\"]/following::*[1]");
			String proxyPort = l.isEmpty() ? "0" : ((Element) l.get(0)).getText();
			ProfileApn a = new ProfileApn();

			a.setApn(apn);
			a.setUsername(username);
			a.setPassword(password);
			a.setProxy(proxy);
			a.setProxyPort(Integer.valueOf(proxyPort));
			m.getApns().add(a);
		}
		return m;
	}

}
