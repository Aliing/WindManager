package com.ah.mdm.core.profile.payload;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.bouncycastle.util.encoders.Base64;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.VpnProfileInfo;

public class VPNPayload extends ProfilePayload
{
	private static Logger		logger			= LoggerFactory.getLogger(VPNPayload.class);
	public static final String	VPNTYPE_L2TP	= "L2TP";
	public static final String	VPNTYPE_PPTP	= "PPTP";
	public static final String	VPNTYPE_IPSec	= "IPSec";

	public VPNPayload(AbstractProfileInfo model)
	{
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		try
		{
			super.fillPayloadContent(parentNode);
			VpnProfileInfo m = (VpnProfileInfo) model;
			addElement(parentNode, "UserDefinedName", m.getUserDefinedName());
			// IPV4 Node
			parentNode.addElement(KEY).setText("IPv4");
			Element dict = parentNode.addElement(DICT);
			addElement(dict, "OverridePrimary", m.isOverridePrimary() ? 1 : 0);
			// VPN Type
			addElement(parentNode, "VPNType", m.getVpnType());
			// EAP
			// parentNode.addElement(KEY).setText("EAP");
			// parentNode.addElement(DICT);
			// Proxies
			parentNode.addElement(KEY).setText("Proxies");
			Element proxyDict = parentNode.addElement(DICT);
			if (m.getProxiesType().equals("Manual"))
			{
				addElement(proxyDict, "HTTPEnable", 1);
				addElement(proxyDict, "HTTPProxy", m.getHttpProxy());
				addElement(proxyDict, "HTTPPort", m.getHttpPort());
				addElement(proxyDict, "HTTPProxyUsername", m.getHttpProxyUsername());
				addElement(proxyDict, "HTTPProxyPassword", m.getHttpProxyPassword());
				// https
				addElement(proxyDict, "HTTPSEnable", 1);
				addElement(proxyDict, "HTTPSPort", m.getHttpPort());
				addElement(proxyDict, "HTTPSProxy", m.getHttpProxy());
			} else if (m.getProxiesType().equals("Auto"))
			{
				addElement(proxyDict, "ProxyAutoConfigEnable", 1);
				addElement(proxyDict, "ProxyAutoConfigURLString", m.getProxyAutoConfigURLString());

				if (m.getProxyAutoConfigURLString() != null)
				{
					proxyDict.addElement(KEY).setText("ProxyAutoConfigURLString");
					proxyDict.addElement(STRING).setText(m.getProxyAutoConfigURLString());
				}
			}

			if (m.getVpnType().equals(VPNTYPE_L2TP))
			{
				parentNode.addElement(KEY).setText("IPSec");
				Element ipSecElement = parentNode.addElement(DICT);
				addElement(ipSecElement, "AuthenticationMethod", m.getAuthenticationMethod());
				addElement(ipSecElement, "SharedSecret", m.getSharedSecret().getBytes("UTF-8"));

				parentNode.addElement(KEY).setText("PPP");
				Element pppElement = parentNode.addElement(DICT);
				addElement(pppElement, "AuthName", m.getAuthName());
				addElement(pppElement, "CommRemoteAddress", m.getCommRemoteAddress());
				if ("Password".equals(m.getAuthType()))
				{
					addElement(pppElement, "AuthPassword", m.getAuthPassword());
				} else
				{
					pppElement.addElement("key").setText("AuthEAPPlugins");
					Element arrElement = pppElement.addElement("array");
					arrElement.addElement("string").setText("EAP-RSA");
					pppElement.addElement("key").setText("AuthProtocol");
					arrElement = pppElement.addElement("array");
					arrElement.addElement("string").setText("EAP");
					addElement(pppElement, "TokenCard", m.isTokenCard());
				}

			} else if (m.getVpnType().equals(VPNTYPE_PPTP))
			{
				parentNode.addElement(KEY).setText("PPP");
				Element pppElement = parentNode.addElement(DICT);

				addElement(pppElement, "AuthName", m.getAuthName());
				addElement(pppElement, "CommRemoteAddress", m.getCommRemoteAddress());
				if ("Password".equals(m.getAuthType()))
				{
					addElement(pppElement, "AuthPassword", m.getAuthPassword());
				} else
				{
					pppElement.addElement("key").setText("AuthEAPPlugins");
					Element arrElement = pppElement.addElement("array");
					arrElement.addElement("string").setText("EAP-RSA");
					pppElement.addElement("key").setText("AuthProtocol");
					arrElement = pppElement.addElement("array");
					arrElement.addElement("string").setText("EAP");
					addElement(pppElement, "TokenCard", m.isTokenCard());
				}

				if ("none".equals(m.getEncryptionLevel()))
				{
					addElement(pppElement, "CCPEnabled", 0);
					addElement(pppElement, "CCPMPPE128Enabled", 0);
					addElement(pppElement, "CCPMPPE40Enabled", 0);
				} else if ("automatic".equals(m.getEncryptionLevel()))
				{
					addElement(pppElement, "CCPEnabled", 1);
					addElement(pppElement, "CCPMPPE128Enabled", 1);
					addElement(pppElement, "CCPMPPE40Enabled", 1);
				} else
				{
					addElement(pppElement, "CCPEnabled", 1);
					addElement(pppElement, "CCPMPPE128Enabled", 1);
					addElement(pppElement, "CCPMPPE40Enabled", 0);
				}

			} else if (m.getVpnType().equals(VPNTYPE_IPSec))
			{
				parentNode.addElement(KEY).setText("IPSec");
				Element ipSecElement = parentNode.addElement(DICT);
				addElement(ipSecElement, "AuthenticationMethod", m.getAuthenticationMethod());
				addElement(ipSecElement, "RemoteAddress", m.getRemoteAddress());
				addElement(ipSecElement, "XAuthEnabled", m.getxAuthEnabled());
				addElement(ipSecElement, "XAuthName", m.getxAuthName());
				addElement(ipSecElement, "XAuthPassword", m.getxAuthName());

				if (m.getAuthenticationMethod().equals("Certificate"))
				{
					addElement(ipSecElement, "OnDemandEnabled", m.isOnDemandEnable() ? 1 : 0);

					if (!m.getOnDemandMatchDomainsAlwaysHost().equals(""))
					{
						ipSecElement.addElement(KEY).setText("OnDemandMatchDomainsAlways");
						Element arr = ipSecElement.addElement(ARRAY);
						for (String s : m.getOnDemandMatchDomainsAlwaysHost().split(","))
						{
							arr.addElement(STRING).setText(s);
						}
					}
					if (!m.getOnDemandMatchDomainsNeverHost().equals(""))
					{
						ipSecElement.addElement(KEY).setText("OnDemandMatchDomainsNever");
						Element arr = ipSecElement.addElement(ARRAY);
						for (String s : m.getOnDemandMatchDomainsNeverHost().split(","))
						{
							arr.addElement(STRING).setText(s);
						}
					}
					if (!m.getOnDemandMatchDomainsOnRetryHost().equals(""))
					{
						ipSecElement.addElement(KEY).setText("OnDemandMatchDomainsOnRetry");
						Element arr = ipSecElement.addElement(ARRAY);
						for (String s : m.getOnDemandMatchDomainsOnRetryHost().split(","))
						{
							arr.addElement(STRING).setText(s);
						}
					}
					addElement(ipSecElement, "PayloadCertificateUUID", m.getPayloadCertificateUUID());
					addElement(ipSecElement, "PromptForVPNPIN", m.isPromptForVPNPIN());
				} else
				{
					if (m.getSharedSecret() != null)
					{
						addElement(ipSecElement, "SharedSecret", m.getSharedSecret().getBytes("UTF-8"));
					}
					addElement(ipSecElement, "LocalIdentifier", m.isHybridAuth() ? m.getLocalIdentifier() + "[hybrid]" : m.getLocalIdentifier());
					addElement(ipSecElement, "LocalIdentifierType", "KeyID");

					if (m.isPromptForPassword())
					{
						addElement(ipSecElement, "XAuthPasswordEncryption", "Prompt");
					}
				}
			}
		} catch (UnsupportedEncodingException e)
		{
			logger.error("build VPN payload error", e);
		}
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		VpnProfileInfo m = (VpnProfileInfo) super.parse(dictElement);

		try
		{
			Element overridePrimaryEle = (Element) dictElement.selectNodes("dict/key[text()='OverridePrimary']/following::*[1]").get(0);
			String overridePrimary = overridePrimaryEle.getText();
			m.setOverridePrimary(overridePrimary == null ? m.isOverridePrimary() : "1".equals(overridePrimary));

			String vpnType = getValue(dictElement, "VPNType", false);
			m.setVpnType(vpnType == null ? m.getVpnType() : vpnType);

			String userDefinedName = getValue(dictElement, "UserDefinedName", false);
			m.setUserDefinedName(userDefinedName == null ? m.getUserDefinedName() : userDefinedName);

			// Proxy Setting
			Element proxyDict = getValueElement(dictElement, "Proxies");
			if (proxyDict != null)
			{
				if (getValueElement(proxyDict, "ProxyAutoConfigURLString") != null)
				{
					m.setProxiesType("Auto");
					String proxyAutoConfigURLString = getValue(proxyDict, "ProxyAutoConfigURLString", false);
					m.setProxyAutoConfigURLString(proxyAutoConfigURLString);
				} else if (!proxyDict.elements().isEmpty())
				{
					m.setProxiesType("Manual");
					String httpEnable = getValue(proxyDict, "HTTPEnable", false);
					m.setHttpEnable(httpEnable == null ? m.getHttpEnable() : Integer.valueOf(httpEnable));

					String httpProxy = getValue(proxyDict, "HTTPProxy", false);
					m.setHttpProxy(httpProxy == null ? m.getHttpProxy() : httpProxy);

					String httpPort = getValue(proxyDict, "HTTPPort", false);
					m.setHttpPort(httpPort == null ? m.getHttpPort() : Integer.valueOf(httpPort));

					String username = getValue(proxyDict, "HTTPProxyUsername", false);
					m.setHttpProxyUsername(username == null ? m.getHttpProxyUsername() : username);

					String password = getValue(proxyDict, "HTTPProxyPassword", false);
					m.setHttpProxyPassword(password == null ? m.getHttpProxyPassword() : password);
				}
			}
			Element ipSecDict = getValueElement(dictElement, "IPSec");
			Element pppDict = getValueElement(dictElement, "PPP");

			// Element pppDict = getValueElement(dictElement, "PPP");
			if (pppDict != null)
			{
				String authName = getValue(pppDict, "AuthName", false);
				m.setAuthName(authName);
				String commRemoteAddress = getValue(pppDict, "CommRemoteAddress", false);
				m.setCommRemoteAddress(commRemoteAddress);
				String authPassword = getValue(pppDict, "AuthPassword", false);
				m.setAuthPassword(authPassword);
				if (authPassword == null)
				{
					m.setAuthType("SecurID");
				}
				String tokenCard = getValue(pppDict, "TokenCard", true);
				m.setTokenCard(tokenCard == null ? m.isTokenCard() : Boolean.valueOf(tokenCard));

				String ccpMPPE40Enabled = getValue(pppDict, "CCPMPPE40Enabled", false);
				m.setCcpMPPE40Enabled(ccpMPPE40Enabled == null ? m.isCcpMPPE40Enabled() : "1".equals(ccpMPPE40Enabled));

				String ccpMPPE128Enabled = getValue(pppDict, "CCPMPPE128Enabled", false);
				m.setCcpMPPE128Enabled(ccpMPPE128Enabled == null ? m.isCcpMPPE128Enabled() : "1".equals(ccpMPPE128Enabled));

				String ccpEnabled = getValue(pppDict, "CCPEnabled", false);
				m.setCcpEnabled(ccpEnabled == null ? m.isCcpEnabled() : "1".equals(ccpEnabled));

				if ("0".equals(ccpEnabled) && "0".equals(ccpMPPE40Enabled) && "0".equals(ccpMPPE128Enabled))
				{
					m.setEncryptionLevel("none");
				}
				if ("1".equals(ccpEnabled) && "1".equals(ccpMPPE128Enabled))
				{
					m.setEncryptionLevel("maximumBit");
				}
				if ("1".equals(ccpEnabled) && "1".equals(ccpMPPE40Enabled) && "1".equals(ccpMPPE128Enabled))
				{
					m.setEncryptionLevel("automatic");
				}

				List<?> list = pppDict.selectNodes("key[text()='AuthEAPPlugins']");
				if (list != null && !list.isEmpty())
				{
					m.setAuthType("SecurID");
				}
			}

			// Element ipSecDict = getValueElement(dictElement, "IPSec");
			if (ipSecDict != null)
			{

				String sharedSceret = getValue(ipSecDict, "SharedSecret", false);
				m.setSharedSecret(sharedSceret == null ? m.getSharedSecret() : new String(Base64.decode(sharedSceret), "UTF-8"));

				String remoteAddress = getValue(ipSecDict, "RemoteAddress", false);
				m.setCommRemoteAddress(remoteAddress == null ? m.getCommRemoteAddress() : remoteAddress);

				String xAuthEnabled = getValue(ipSecDict, "XAuthEnabled", false);
				m.setxAuthEnabled(xAuthEnabled == null ? m.getxAuthEnabled() : Integer.valueOf(xAuthEnabled));

				String xAuthName = getValue(ipSecDict, "XAuthName", false);
				m.setxAuthName(xAuthName == null ? m.getAuthName() : xAuthName);

				String xAuthPassword = getValue(ipSecDict, "XAuthPassword", false);
				m.setxAuthPassword(xAuthPassword == null ? m.getAuthPassword() : xAuthPassword);

				String xAuthPasswordEncryption = getValue(ipSecDict, "XAuthPasswordEncryption", false);
				m.setPromptForPassword(xAuthPasswordEncryption == null ? m.isPromptForPassword() : "Prompt".equals(xAuthPasswordEncryption));

				String authenticationMethod = getValue(ipSecDict, "AuthenticationMethod", false);
				m.setAuthenticationMethod(authenticationMethod == null ? m.getAuthenticationMethod() : authenticationMethod);

				String localIdentifier = getValue(ipSecDict, "LocalIdentifier", false);
				if (localIdentifier != null)
				{
					m.setLocalIdentifier(localIdentifier);
					m.setHybridAuth(localIdentifier.contains("[hybrid]"));
				}

				String promptForVPNPIN = getValue(ipSecDict, "PromptForVPNPIN", true);
				m.setPromptForVPNPIN(promptForVPNPIN == null ? m.isPromptForVPNPIN() : Boolean.valueOf(promptForVPNPIN));

				String payloadCertificateUUID = getValue(ipSecDict, "PayloadCertificateUUID", false);
				m.setPayloadCertificateUUID(payloadCertificateUUID == null ? m.getPayloadCertificateUUID() : payloadCertificateUUID);

				String onDemandEnabled = getValue(ipSecDict, "OnDemandEnabled", false);
				m.setOnDemandEnable(onDemandEnabled == null ? m.isOnDemandEnable() : Integer.valueOf(onDemandEnabled) == 1);

				Element e = getValueElement(ipSecDict, "OnDemandMatchDomainsAlways");
				if (e != null)
				{
					String s = "";
					StringBuffer buf = new StringBuffer();
					for (Object o : e.elements())
					{
						buf.append(((Element) o).getText());
						buf.append(",");
					}
					s = buf.toString();
					s = s.substring(0, s.length() - 1);
					m.setOnDemandMatchDomainsAlwaysHost(s);
				}
				e = getValueElement(ipSecDict, "OnDemandMatchDomainsNever");
				if (e != null)
				{
					String s = "";
					StringBuffer buf = new StringBuffer();
					for (Object o : e.elements())
					{
						buf.append(((Element) o).getText());
						buf.append(",");
					}
					s = buf.toString();
					s = s.substring(0, s.length() - 1);
					m.setOnDemandMatchDomainsNeverHost(s);
				}
				e = getValueElement(ipSecDict, "OnDemandMatchDomainsOnRetry");
				if (e != null)
				{
					String s = "";
					StringBuffer buf = new StringBuffer();
					for (Object o : e.elements())
					{
						buf.append(((Element) o).getText());
						buf.append(",");
					}
					s = buf.toString();
					s = s.substring(0, s.length() - 1);
					m.setOnDemandMatchDomainsOnRetryHost(s);
				}
			}
		} catch (Exception e)
		{
			logger.error("parse VPN payload error", e);
		}

		return m;
	}

}
