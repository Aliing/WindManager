package com.ah.mdm.core.profile.payload;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.utils.Payload;

public class ProfilePayload extends Payload
{
	private static Logger			logger	= LoggerFactory.getLogger(ProfilePayload.class);
	protected AbstractProfileInfo	model;

	public ProfilePayload(AbstractProfileInfo model)
	{
		super();
		this.model = model;
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		addElement(parentNode, PAYLOAD_DESCRIPTION, model.getDescription(), "");
		addElement(parentNode, PAYLOAD_DISPLAYNAME, model.getDisplayName(), "");
		addElement(parentNode, PAYLOAD_IDENTIFIER, model.getIdentifier(), "");
		addElement(parentNode, PAYLOAD_ORGANIZATION, model.getOrganization(), "");
		addElement(parentNode, PAYLOAD_TYPE, model.getType(), "");
		addElement(parentNode, PAYLOAD_UUID, model.getUuid(), "");
		addElement(parentNode, PAYLOAD_VERSION, model.getVersion(), "");
	}

	public AbstractProfileInfo parse(Element dictElement)
	{
		String str = getValue(dictElement, "PayloadDescription", false);
		if (str != null)
			model.setDescription(str);
		str = getValue(dictElement, "PayloadDisplayName", false);
		if (str != null)
			model.setDisplayName(str);
		str = getValue(dictElement, "PayloadIdentifier", false);
		if (str != null)
			model.setIdentifier(str);
		str = getValue(dictElement, "PayloadOrganization", false);
		if (str != null)
			model.setOrganization(str);
		str = getValue(dictElement, "PayloadType", false);
		if (str != null)
			model.setType(str);
		str = getValue(dictElement, "PayloadVersion", false);
		if (str != null)
			model.setVersion(Integer.valueOf(str));
		return model;
	}

	protected String getValue(Element e, String key, boolean isBoolean)
	{
		String str = "key[text()=\"" + key + "\"]/following::*[1]";
		List list = e.selectNodes(str);
		if (list.isEmpty())
			return null;
		if (isBoolean)
		{
			return ((Element) list.get(0)).getName();
		} else
		{
			return ((Element) list.get(0)).getText();
		}
	}

	protected Element getValueElement(Element e, String key)
	{
		String str = "key[text()=\"" + key + "\"]/following::*[1]";
		List list = e.selectNodes(str);
		if (list.isEmpty())
			return null;
		return (Element) list.get(0);
	}

	/**
	 * 
	 * @param parentElement
	 * @param key
	 * @param value
	 */
	protected void addElement(Element parentElement, String key, Object value)
	{
		addElement(parentElement, key, value, null);
	}

	/**
	 * 
	 * @param parentElement
	 * @param key
	 * @param value
	 * @param defaultValue
	 *            when the value is null,if the defaultVlaue is null,did't add
	 *            this element else add the element with this value
	 */
	protected void addElement(Element parentElement, String key, Object value, String defaultValue)
	{
		if (value != null)
		{
			Class<?> clazz = value.getClass();
			parentElement.addElement(KEY).setText(key);
			if (clazz == String.class)
			{
				parentElement.addElement(STRING).setText(value.toString());
			} else if (clazz == Integer.class || clazz == int.class)
			{
				parentElement.addElement(INTEGER).setText(value.toString());
			} else if (clazz == Boolean.class || clazz == boolean.class)
			{
				parentElement.addElement(value.toString());
			} else if (clazz == Double.class || clazz == double.class)
			{
				parentElement.addElement(REAL).setText(value.toString());
			} else if (clazz == byte[].class)
			{
				parentElement.addElement(DATA).setText(Base64.encodeBase64String((byte[]) value));
			}
		}
	}

}