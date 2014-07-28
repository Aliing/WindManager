package com.ah.mdm.core.profile.payload;

import org.dom4j.Element;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.CalendarSubscriptionProfileInfo;



public class CalendarSubscriptionPayload extends ProfilePayload {

	public CalendarSubscriptionPayload(AbstractProfileInfo model) {
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode) {
		super.fillPayloadContent(parentNode);
		CalendarSubscriptionProfileInfo m = (CalendarSubscriptionProfileInfo) model;
		addElement(parentNode, SUB_CAL_ACCOUNT_DESCRIPTION, m.getAccountDescription());
		addElement(parentNode, SUB_CAL_ACCOUNT_HOST_NAME, m.getAccountHostName());
		addElement(parentNode, SUB_CAL_ACCOUNT_USERNAME, m.getAccountUsername());
		addElement(parentNode, SUB_CAL_ACCOUNT_PASSWORD, m.getAccountPassword());
		addElement(parentNode, SUB_CAL_ACCOUNT_USE_SSL, m.isAccountUseSSL());
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement) {
		CalendarSubscriptionProfileInfo m = (CalendarSubscriptionProfileInfo)super.parse(dictElement);
		String desc = ((Element)dictElement.selectNodes("key[text()=\"SubCalAccountDescription\"]/following::string[1]").get(0)).getText();
		m.setAccountDescription(desc);
		String hostname = ((Element)dictElement.selectNodes("key[text()=\"SubCalAccountHostName\"]/following::string[1]").get(0)).getText();
		m.setAccountHostName(hostname);
		String password = ((Element)dictElement.selectNodes("key[text()=\"SubCalAccountPassword\"]/following::string[1]").get(0)).getText();
		m.setAccountPassword(password);
		String useSSL = ((Element)dictElement.selectNodes("key[text()=\"SubCalAccountUseSSL\"]/following::*[1]").get(0)).getName();
		m.setAccountUseSSL(Boolean.valueOf(useSSL));
		String userName = ((Element)dictElement.selectNodes("key[text()=\"SubCalAccountUsername\"]/following::*[1]").get(0)).getText();
		m.setAccountUsername(userName);
		return m;
	}
}
