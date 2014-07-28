package com.ah.mdm.core.profile.payload;

import java.io.UnsupportedEncodingException;

import org.bouncycastle.util.encoders.Base64;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.WebClipProfileInfo;

public class WebClipPayload extends ProfilePayload
{
	private static Logger	logger	= LoggerFactory.getLogger(WebClipPayload.class);

	public WebClipPayload(AbstractProfileInfo model)
	{
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		super.fillPayloadContent(parentNode);
		WebClipProfileInfo m = (WebClipProfileInfo) model;
		addElement(parentNode, URL, m.getUrl());
		addElement(parentNode, LABEL, m.getLabel());
		if (m.getIcon() != null)
		{
			addElement(parentNode, ICON, m.getIcon());
		}
		addElement(parentNode, IS_REMOVABLE, m.isRemovable());
		addElement(parentNode, FULL_SCREEN, m.isFullScreen());
		addElement(parentNode, PRECOMPOSED, m.isPrecomposed());
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		WebClipProfileInfo m = (WebClipProfileInfo) super.parse(dictElement);
		String fullScreen = getValue(dictElement, "FullScreen", true);
		m.setFullScreen(fullScreen == null ? m.isFullScreen() : Boolean.valueOf(fullScreen));

		String icon = getValue(dictElement, "Icon", false);
		try
		{
			m.setIcon(icon == null ? m.getIcon() : Base64.decode(icon.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e)
		{
			logger.error("Exception occured: ", e);
		}

		String isRemovable = getValue(dictElement, "IsRemovable", true);
		m.setRemovable(isRemovable == null ? m.isRemovable() : Boolean.valueOf(isRemovable));

		String label = getValue(dictElement, "Label", false);
		m.setLabel(label == null ? m.getLabel() : label);

		String url = getValue(dictElement, URL, false);
		m.setUrl(url);

		String precomposed = getValue(dictElement, "Precomposed", true);
		m.setPrecomposed(precomposed == null ? m.isPrecomposed() : Boolean.valueOf(precomposed));
		return m;
	}
}
