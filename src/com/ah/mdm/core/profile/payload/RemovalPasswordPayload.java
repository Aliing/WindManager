package com.ah.mdm.core.profile.payload;

import org.dom4j.Element;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.RemovalPasscodeProfileInfo;


public class RemovalPasswordPayload extends ProfilePayload {

	public RemovalPasswordPayload(AbstractProfileInfo model) {
		super(model);
	}


	@Override
	protected void fillPayloadContent(Element parentNode) {
		super.fillPayloadContent(parentNode);
		RemovalPasscodeProfileInfo m = (RemovalPasscodeProfileInfo)model;
		addElement(parentNode, REMOVAL_PASSWORD, m.getRemovalPassword());
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement) {
		RemovalPasscodeProfileInfo m = (RemovalPasscodeProfileInfo)super.parse(dictElement);
		String s = getValue(dictElement, "RemovalPassword", false);
		m.setRemovalPassword(s==null?m.getRemovalPassword():s);
		return m;
	}
}
