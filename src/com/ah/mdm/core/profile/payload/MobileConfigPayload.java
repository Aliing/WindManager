package com.ah.mdm.core.profile.payload;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.ApnProfileInfo;
import com.ah.mdm.core.profile.entity.CalDavProfileInfo;
import com.ah.mdm.core.profile.entity.CalendarSubscriptionProfileInfo;
import com.ah.mdm.core.profile.entity.CardDavProfileInfo;
import com.ah.mdm.core.profile.entity.ConfigurationProfileInfo;
import com.ah.mdm.core.profile.entity.CredentialsProfileInfo;
import com.ah.mdm.core.profile.entity.EmailProfileInfo;
import com.ah.mdm.core.profile.entity.ExchangeProfileInfo;
import com.ah.mdm.core.profile.entity.LdapProfileInfo;
import com.ah.mdm.core.profile.entity.MdmProfileInfo;
import com.ah.mdm.core.profile.entity.PasscodeProfileInfo;
import com.ah.mdm.core.profile.entity.ProfilePayloadType;
import com.ah.mdm.core.profile.entity.RemovalPasscodeProfileInfo;
import com.ah.mdm.core.profile.entity.RestrictionsProfileInfo;
import com.ah.mdm.core.profile.entity.ScepProfileInfo;
import com.ah.mdm.core.profile.entity.VpnProfileInfo;
import com.ah.mdm.core.profile.entity.WebClipProfileInfo;
import com.ah.mdm.core.profile.entity.WifiProfileInfo;

public class MobileConfigPayload extends ProfilePayload
{
	private static Logger			logger					= LoggerFactory.getLogger(MobileConfigPayload.class);
	// private Map<Class<?>, Class<?>> model2PayloadMap = new HashMap<Class<?>,
	// Class<?>>();

	private Map<String, Class<?>>	payloadTypeMap			= new HashMap<String, Class<?>>();
	private Map<String, Class<?>>	payloadType2ModelMap	= new HashMap<String, Class<?>>();

	public MobileConfigPayload(AbstractProfileInfo model)
	{
		super(model);

		// model2PayloadMap.put(ApnProfileInfo.class, APNPayload.class);
		// model2PayloadMap.put(CalDavProfileInfo.class, CalDAVPayload.class);
		// model2PayloadMap.put(CalendarSubscriptionProfileInfo.class,
		// CalendarSubscriptionPayload.class);
		// model2PayloadMap.put(CardDavProfileInfo.class, CardDavPayload.class);
		// model2PayloadMap.put(CredentialsProfileInfo.class,
		// CredentialsPayload.class);
		// model2PayloadMap.put(EmailProfileInfo.class, EmailPayload.class);
		// model2PayloadMap.put(LdapProfileInfo.class, LDAPPayload.class);
		//
		// model2PayloadMap.put(PasscodeProfileInfo.class,
		// PasscodePolicyPayload.class);
		// model2PayloadMap.put(RemovalPasscodeProfileInfo.class,
		// RemovalPasswordPayload.class);
		// model2PayloadMap.put(RestrictionsProfileInfo.class,
		// RestrictionsPayload.class);
		// model2PayloadMap.put(ScepProfileInfo.class, SCEPPlayload.class);
		// model2PayloadMap.put(VpnProfileInfo.class, VPNPayload.class);
		// model2PayloadMap.put(WebClipProfileInfo.class, WebClipPayload.class);
		// model2PayloadMap.put(WifiProfileInfo.class, WiFiPayload.class);
		// model2PayloadMap.put(ExchangeProfileInfo.class,
		// ExchangePayload.class);
		// model2PayloadMap.put(MdmProfileInfo.class, MDMPayload.class);

		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_APN, APNPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_CAL_DAV, CalDAVPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_SUB_CAL, CalendarSubscriptionPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_CARD_DAV, CardDavPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_CREDENTIALS_CRT_ENTITY, CredentialsPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_CREDENTIALS_P12, CredentialsPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_CREDENTIALS_CRT_CA, CredentialsPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_EMAIL, EmailPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_LDAP, LDAPPayload.class);

		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_PASSWORD_POLICY, PasscodePolicyPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_REMOVAL_PASSWORD, RemovalPasswordPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_RESTRICTIONS, RestrictionsPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_SCEP, SCEPPlayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_VPN, VPNPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_WEP_CLIP, WebClipPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_WIFI, WiFiPayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_EXCHANGE_IOS, ExchangePayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_EXCHANGE_OSX, ExchangePayload.class);
		payloadTypeMap.put(ProfilePayloadType.PAYLOAD_TYPE_MDM, MDMPayload.class);

		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_APN, ApnProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_CAL_DAV, CalDavProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_SUB_CAL, CalendarSubscriptionProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_CARD_DAV, CardDavProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_CREDENTIALS_CRT_ENTITY, CredentialsProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_CREDENTIALS_P12, CredentialsProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_CREDENTIALS_CRT_CA, CredentialsProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_EMAIL, EmailProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_LDAP, LdapProfileInfo.class);

		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_PASSWORD_POLICY, PasscodeProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_REMOVAL_PASSWORD, RemovalPasscodeProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_RESTRICTIONS, RestrictionsProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_SCEP, ScepProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_VPN, VpnProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_WEP_CLIP, WebClipProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_WIFI, WifiProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_EXCHANGE_IOS, ExchangeProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_EXCHANGE_OSX, ExchangeProfileInfo.class);
		payloadType2ModelMap.put(ProfilePayloadType.PAYLOAD_TYPE_MDM, MdmProfileInfo.class);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		try
		{
			super.fillPayloadContent(parentNode);
			ConfigurationProfileInfo m = (ConfigurationProfileInfo) model;

			parentNode.addElement(KEY).setText(HAS_REMOVAL_PASSCODE);
			parentNode.addElement(m.isHasRemovalPasscode() ? TRUE : FALSE);
			parentNode.addElement(KEY).setText(IS_ENCYPTED);
			parentNode.addElement(m.isEncrypted() ? TRUE : FALSE);
			parentNode.addElement(KEY).setText(IS_MANAGED);
			parentNode.addElement(m.isManaged() ? TRUE : FALSE);
			parentNode.addElement(KEY).setText(PAYLOAD_CONTENT);
			Element contentArray = parentNode.addElement(ARRAY);

			List<AbstractProfileInfo> contentList = m.getProfileInfoItems();
			for (AbstractProfileInfo gm : contentList)
			{
				Element profileDict = contentArray.addElement(DICT);
				// Class<?> payloadClazz = model2PayloadMap.get(gm.getClass());
				Class<?> payloadClazz = payloadTypeMap.get(gm.getType());
				Constructor<?> con = payloadClazz.getConstructor(AbstractProfileInfo.class);
				ProfilePayload p = (ProfilePayload) con.newInstance(gm);
				p.fillPayloadContent(profileDict);
			}
			parentNode.addElement(KEY).setText(PAYLOAD_REMOVAL_DISALLOWED);
			parentNode.addElement(m.isRemovalDisallowed() ? TRUE : FALSE);
		} catch (Exception e)
		{
			logger.error("Exception occured: ", e);
		}
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		ConfigurationProfileInfo m = (ConfigurationProfileInfo) super.parse(dictElement);

		String hasRemovalPasscode = getValue(dictElement, "HasRemovalPasscode", true);
		m.setHasRemovalPasscode(hasRemovalPasscode == null ? m.isHasRemovalPasscode() : Boolean.valueOf(hasRemovalPasscode));

		String isEncrypted = getValue(dictElement, "IsEncrypted", true);
		m.setEncrypted(isEncrypted == null ? m.isEncrypted() : Boolean.valueOf(isEncrypted));

		String isManaged = getValue(dictElement, "IsManaged", true);
		m.setManaged(isManaged == null ? m.isManaged() : Boolean.valueOf(isManaged));

		String removalDisallowed = getValue(dictElement, "PayloadRemovalDisallowed", true);
		m.setRemovalDisallowed(removalDisallowed == null ? m.isRemovalDisallowed() : Boolean.valueOf(removalDisallowed));

		// Payload Content
		Element contentElement = getValueElement(dictElement, "PayloadContent");
		if (contentElement != null)
		{
			List<?> payloadList = contentElement.elements();
			for (Object p : payloadList)
			{
				Element pDict = (Element) p;
				String payLoadType = getValue(pDict, "PayloadType", false);
				ProfilePayload payload = getProfilePayloadByType(payLoadType);
				if (payload != null)
				{
					AbstractProfileInfo mm = payload.parse(pDict);// TODO

					m.getProfileInfoItems().add(mm);
				}
			}
		}
		return m;
	}

	protected ProfilePayload getProfilePayloadByType(String payloadType)
	{
		// try
		// {
		// AbstractProfileInfo gm = null;
		// for (Class<?> clazz : model2PayloadMap.keySet())
		// {
		// AbstractProfileInfo m = (AbstractProfileInfo) clazz.newInstance();
		// if (m.getType().equals(payloadType))
		// {
		// gm = m;
		// break;
		// }
		// }
		// if (gm != null)
		// {
		// Class<?> payloadClazz = model2PayloadMap.get(gm.getClass());
		// Constructor<?> con =
		// payloadClazz.getConstructor(AbstractProfileInfo.class);
		// ProfilePayload p = (ProfilePayload) con.newInstance(gm);
		// return p;
		// }
		// } catch (Exception e)
		// {
		// logger.error("Exception occured: ", e);
		// }
		// return null;
		try
		{
			Class<?> payloadClazz = payloadTypeMap.get(payloadType);
			Constructor<?> con = payloadClazz.getConstructor(AbstractProfileInfo.class);
			AbstractProfileInfo m = (AbstractProfileInfo) payloadType2ModelMap.get(payloadType).newInstance();
			ProfilePayload p = (ProfilePayload) con.newInstance(m);
			return p;
		} catch (Exception e)
		{
			logger.error("Exception occured: ", e);
		}
		return null;
	}
}
