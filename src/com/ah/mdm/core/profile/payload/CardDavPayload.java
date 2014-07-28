package com.ah.mdm.core.profile.payload;

import org.dom4j.Element;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.CardDavProfileInfo;



public class CardDavPayload extends ProfilePayload {

	public CardDavPayload(AbstractProfileInfo model) {
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode) {
		super.fillPayloadContent(parentNode);
		CardDavProfileInfo m = (CardDavProfileInfo) model;
		addElement(parentNode, "CardDAVAccountDescription", m.getAccountDescription());
		addElement(parentNode, "CardDAVHostName", m.getHostName());
		addElement(parentNode, "CardDAVUsername", m.getUsername());
		addElement(parentNode, "CardDAVPassword", m.getPassword());
		addElement(parentNode, "CardDAVUseSSL", m.isUseSSL());
		addElement(parentNode, "CardDAVPort", m.getPort());
		addElement(parentNode, "CardDAVPrincipalURL", m.getPrincipalURL());
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement) {
		CardDavProfileInfo m = (CardDavProfileInfo) super.parse(dictElement);
		String desc = getValue(dictElement, "CardDAVAccountDescription", false);
		m.setAccountDescription(desc==null?m.getAccountDescription():desc);
		
		String hostname = getValue(dictElement, "CardDAVHostName", false);
		m.setHostName(hostname==null?m.getHostName():hostname);
		
		String password = getValue(dictElement, "CardDAVPassword", false);
		m.setPassword(password==null?m.getPassword():password);
		
		String port = getValue(dictElement, "CardDAVPort", false);
		m.setPort(port==null?m.getPort():Integer.valueOf(port));
		
		String url = getValue(dictElement, "CardDAVPrincipalURL", false);
		m.setPrincipalURL(url==null?m.getPrincipalURL():url);
		
		String useSSL = getValue(dictElement, "CardDAVUseSSL", true);
		m.setUseSSL(useSSL==null?m.isUseSSL():Boolean.valueOf(useSSL));
		
		String userName = getValue(dictElement, "CardDAVUsername", false);
		m.setUsername(userName==null?m.getUsername():userName);

		return m;
	}
}
