package com.ah.mdm.core.profile.payload;

import java.util.List;

import org.dom4j.Element;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.LdapProfileInfo;
import com.ah.mdm.core.profile.entity.ProfileLdapSearchSetting;

public class LDAPPayload extends ProfilePayload
{

	public LDAPPayload(AbstractProfileInfo model)
	{
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		super.fillPayloadContent(parentNode);
		LdapProfileInfo m = (LdapProfileInfo) model;
		addElement(parentNode, LDAP_ACCOUNT_DESCRIPTION, m.getAccountDescription());
		addElement(parentNode, LDAP_ACCOUNT_NAME, m.getAccountHostName());
		addElement(parentNode, LDAP_ACCOUNT_USE_SSL, m.isAccountUseSSL());
		addElement(parentNode, LDAP_ACCOUNT_USER_NAME, m.getAccountUserName());
		addElement(parentNode, LDAP_ACCOUNT_PASSWORD, m.getAccountPassword());

		List<ProfileLdapSearchSetting> searchSettings = m.getSearchSettings();
		if (searchSettings != null && !searchSettings.isEmpty())
		{
			parentNode.addElement(KEY).setText(LDAP_SEARCH_SETTINGS);
			Element seartchSettingArray = parentNode.addElement(ARRAY);
			for (ProfileLdapSearchSetting searchSetting : searchSettings)
			{
				Element searchSettingDict = seartchSettingArray.addElement(DICT);
				addElement(searchSettingDict, LDAP_SEARCH_SETTINGS_DESCRIPTION, searchSetting.getDescription());
				addElement(searchSettingDict, LDAP_SEARCH_SETTINGS_BASE, searchSetting.getSearchBase());
				addElement(searchSettingDict, LDAP_SEARCH_SETTINGS_SCOPE, searchSetting.getScope());
			}
		}
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		LdapProfileInfo m = (LdapProfileInfo) super.parse(dictElement);
		String desc = getValue(dictElement, "LDAPAccountDescription", false);
		m.setAccountDescription(desc == null ? m.getAccountDescription() : desc);

		String hostName = getValue(dictElement, "LDAPAccountHostName", false);
		m.setAccountHostName(hostName == null ? m.getAccountHostName() : hostName);

		String password = getValue(dictElement, "LDAPAccountPassword", false);
		m.setAccountPassword(password == null ? m.getAccountPassword() : password);

		String useSSL = getValue(dictElement, "LDAPAccountUseSSL", true);
		m.setAccountUseSSL(useSSL == null ? m.isAccountUseSSL() : Boolean.valueOf(useSSL));

		String username = getValue(dictElement, "LDAPAccountUserName", false);
		m.setAccountUserName(username == null ? m.getAccountUserName() : username);

		Element element = getValueElement(dictElement, "ProfileLdapSearchSettings");
		if (element != null)
		{
			for (Object e : element.elements())
			{
				Element eDict = (Element) e;
				String descStr = getValue(eDict, "ProfileLdapSearchSettingDescription", false);
				String searchBaseStr = getValue(eDict, "ProfileLdapSearchSettingScope", false);
				String scopeStr = getValue(eDict, "ProfileLdapSearchSettingSearchBase", false);
				ProfileLdapSearchSetting setting = new ProfileLdapSearchSetting();
				// descStr, searchBaseStr, scopeStr
				setting.setSearchBase(searchBaseStr);
				setting.setDescription(descStr);
				setting.setScope(scopeStr);
				m.getSearchSettings().add(setting);
			}
		}
		return m;
	}
}
