package com.ah.mdm.core.profile.payload;

import org.dom4j.Element;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.WifiProfileInfo;
import com.ah.mdm.core.profile.utils.IProtocolContants;

public class WiFiPayload extends ProfilePayload
{

	public WiFiPayload(AbstractProfileInfo model)
	{
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		super.fillPayloadContent(parentNode);
		WifiProfileInfo m = (WifiProfileInfo) model;
		addElement(parentNode, "SSID_STR", m.getSsidStr());
		addElement(parentNode, "AutoJoin", m.isAutoJoin());
		addElement(parentNode, "HIDDEN_NETWORK", m.isHiddenNetwork());
		addElement(parentNode, "EncryptionType", m.getEncryptionType());

		if (!m.isEnterpriseUsed())
		{
			addElement(parentNode, "Password", m.getPassword());
		} else
		{
			// Protocol config
			parentNode.addElement(KEY).setText("EAPClientConfiguration");
			Element configDict = parentNode.addElement(DICT);
			if (m.getAcceptEAPTypes() != null && !m.getAcceptEAPTypes().isEmpty())
			{
				configDict.addElement(KEY).setText("AcceptEAPTypes");
				Element acceptEAPArr = configDict.addElement(ARRAY);
				String[] arrs = m.getAcceptEAPTypes().split(",");
				for (String s : arrs)
				{
					acceptEAPArr.addElement(INTEGER).setText(s);
				}
			}
			addElement(configDict, "EAPFASTProvisionPAC", m.iseAPFASTProvisionPAC());
			addElement(configDict, "EAPFASTProvisionPACAnonymously", m.iseAPFASTProvisionPACAnonymously());
			addElement(configDict, "EAPFASTUsePAC", m.iseAPFASTUsePAC());

			if (m.getAcceptEAPTypes() != null && m.getAcceptEAPTypes().contains(IProtocolContants.ACCEPT_EAP_TYPE_TTLS + ""))
			{
				addElement(configDict, "TTLSInnerAuthentication", m.gettTLSInnerAuthentication());
			}
			// Authentication
			if (m.getUsername() != null && !m.getUsername().isEmpty())
			{
				addElement(configDict, "UserName", m.getUsername());
			}
			if (!m.isOneTimeUserPassword())
			{
				if (m.getUserPassword() != null && !m.getUserPassword().isEmpty())
				{
					addElement(parentNode, "UserPassword", m.getUserPassword());
				}
			} else
			{
				addElement(parentNode, "OneTimeUserPassword", m.isOneTimeUserPassword());
			}
			if (m.getPayloadCertificateUUID() != null && !m.getPayloadCertificateUUID().isEmpty())
			{
				addElement(parentNode, "PayloadCertificateUUID", m.getPayloadCertificateUUID());
			}
			if (m.getOuterIdentity() != null && !m.getOuterIdentity().isEmpty())
			{
				addElement(parentNode, "OuterIdentity", m.getOuterIdentity());
			}
			// Trust
			if (m.getPayloadCertificateAnchorUUID() != null && !m.getPayloadCertificateAnchorUUID().isEmpty())
			{
				configDict.addElement(KEY).setText("PayloadCertificateAnchorUUID");
				Element arrElement = configDict.addElement(ARRAY);
				String[] arr = m.getPayloadCertificateAnchorUUID().split(",");
				for (String s : arr)
				{
					arrElement.addElement(STRING).setText(s);
				}
			}
			if (m.gettLSTrustedServerNames() != null && !m.gettLSTrustedServerNames().isEmpty())
			{
				configDict.addElement(KEY).setText("TLSTrustedServerNames");
				Element arrElement = configDict.addElement(ARRAY);
				String[] arr = m.gettLSTrustedServerNames().split(",");
				for (String s : arr)
				{
					arrElement.addElement(STRING).setText(s);
				}
			}
		}
		// Proxy
		addElement(parentNode, "ProxyType", m.getProxyType());
		if (m.getProxyType().equals("Auto"))
		{
			addElement(parentNode, "ProxyPACURL", m.getProxyPACURL());
		} else if (m.getProxyType().equals("Manual"))
		{
			addElement(parentNode, "ProxyServer", m.getProxyServer());
			addElement(parentNode, "ProxyServerPort", m.getProxyServerPort());
			if (m.getProxyPassword() != null && !m.getProxyPassword().isEmpty())
			{
				addElement(parentNode, "ProxyPassword", m.getProxyPassword());
			}
			if (m.getProxyUsername() != null && !m.getProxyUsername().isEmpty())
			{
				addElement(parentNode, "ProxyUsername", m.getProxyUsername());
			}
		}
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		WifiProfileInfo m = (WifiProfileInfo) super.parse(dictElement);
		String ssidStr = getValue(dictElement, "SSID_STR", false);
		m.setSsidStr(ssidStr == null ? m.getSsidStr() : ssidStr);

		String hiddenNetwork = getValue(dictElement, "HIDDEN_NETWORK", true);
		m.setHiddenNetwork(hiddenNetwork == null ? m.isHiddenNetwork() : Boolean.valueOf(hiddenNetwork));

		String autoJoin = getValue(dictElement, "AutoJoin", true);
		m.setAutoJoin(autoJoin == null ? m.isAutoJoin() : Boolean.valueOf(autoJoin));

		String enterpriseType = getValue(dictElement, "EncryptionType", false);
		m.setEncryptionType(enterpriseType == null ? m.getEncryptionType() : enterpriseType);

		String password = getValue(dictElement, "Password", false);
		m.setPassword(password == null ? m.getPassword() : password);

		String proxyType = getValue(dictElement, "ProxyType", false);
		m.setProxyType(proxyType == null ? m.getProxyType() : proxyType);

		String proxyServer = getValue(dictElement, "ProxyServer", false);
		m.setProxyServer(proxyServer == null ? m.getProxyServer() : proxyServer);

		String proxyServerPort = getValue(dictElement, "ProxyServerPort", false);
		m.setProxyServerPort(proxyServerPort == null ? m.getProxyServerPort() : Integer.valueOf(proxyServerPort));

		String proxyPassword = getValue(dictElement, "ProxyPassword", false);
		m.setProxyPassword(proxyPassword == null ? m.getProxyPassword() : proxyPassword);

		String proxyUsername = getValue(dictElement, "ProxyUsername", false);
		m.setProxyUsername(proxyUsername == null ? m.getProxyUsername() : proxyUsername);

		String proxyPACURL = getValue(dictElement, "ProxyPACURL", false);
		m.setProxyPACURL(proxyPACURL == null ? m.getProxyPACURL() : proxyPACURL);

		String payloadCertificateUUID = getValue(dictElement, "PayloadCertificateUUID", false);
		m.setPayloadCertificateUUID(payloadCertificateUUID == null ? m.getPayloadCertificateUUID() : payloadCertificateUUID);

		Element eapClientConfigElement = getValueElement(dictElement, "EAPClientConfiguration");
		if (eapClientConfigElement != null)
		{
			m.setEnterpriseUsed(true);
			Element acceptTypesElement = getValueElement(eapClientConfigElement, "AcceptEAPTypes");
			if (acceptTypesElement != null)
			{
				String acceptTypesValue = "";
				for (Object o : acceptTypesElement.elements())
				{
					acceptTypesValue = acceptTypesValue + ((Element) o).getText() + ",";
				}
				acceptTypesValue = acceptTypesValue.substring(0, acceptTypesValue.length() - 1);
				m.setAcceptEAPTypes(acceptTypesValue);
			}
			String v = getValue(eapClientConfigElement, "EAPFASTProvisionPAC", true);
			m.seteAPFASTProvisionPAC(v == null ? m.iseAPFASTProvisionPAC() : Boolean.valueOf(v));

			v = getValue(eapClientConfigElement, "EAPFASTProvisionPACAnonymously", true);
			m.seteAPFASTProvisionPACAnonymously(v == null ? m.iseAPFASTProvisionPACAnonymously() : Boolean.valueOf(v));

			v = getValue(eapClientConfigElement, "EAPFASTUsePAC", true);
			m.seteAPFASTUsePAC(v == null ? m.iseAPFASTUsePAC() : Boolean.valueOf(v));

			v = getValue(eapClientConfigElement, "TTLSInnerAuthentication", false);
			m.settTLSInnerAuthentication(v == null ? m.gettTLSInnerAuthentication() : v);

			v = getValue(eapClientConfigElement, "UserName", false);
			m.setUsername(v == null ? m.getUsername() : v);

			v = getValue(eapClientConfigElement, "UserPassword", false);
			m.setUserPassword(v == null ? m.getUserPassword() : v);

			v = getValue(eapClientConfigElement, "OuterIdentity", false);
			m.setOuterIdentity(v == null ? m.getOuterIdentity() : v);

			Element uuIDsElement = getValueElement(eapClientConfigElement, "PayloadCertificateAnchorUUID");
			if (uuIDsElement != null)
			{
				String uuidsStr = "";
				for (Object o : uuIDsElement.elements())
				{
					uuidsStr = uuidsStr + ((Element) o).getText() + ",";
				}
				uuidsStr = uuidsStr.substring(0, uuidsStr.length() - 1);
				m.setPayloadCertificateAnchorUUID(uuidsStr);
			}

			Element trustNamesElement = getValueElement(eapClientConfigElement, "TLSTrustedServerNames");
			if (trustNamesElement != null)
			{
				String value = "";
				for (Object o : trustNamesElement.elements())
				{
					value = value + ((Element) o).getText() + ",";
				}
				value = value.substring(0, value.length() - 1);
				m.settLSTrustedServerNames(value);
			}
		}
		return m;
	}
}
