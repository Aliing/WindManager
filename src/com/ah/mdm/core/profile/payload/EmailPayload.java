package com.ah.mdm.core.profile.payload;
import org.dom4j.Element;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.EmailProfileInfo;

public class EmailPayload extends ProfilePayload
{

	public EmailPayload(AbstractProfileInfo model)
	{
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		super.fillPayloadContent(parentNode);
		EmailProfileInfo m = (EmailProfileInfo) model;
		addElement(parentNode, EMAIL_ACCOUNT_DESCRIPTION, m.getAccountDescription());
		addElement(parentNode, EMAIL_ACCOUNT_NAME, m.getAccountName());
		addElement(parentNode, EMAIL_ACCOUNT_TYPE, m.getAccountType());
		addElement(parentNode, EMAIL_ADDRESS, m.getAddress());
		if (m.getIncomingMailServerIMAPPathPrefix() != null)
		{
			addElement(parentNode, INCOMING_MAIL_SERVER_IMAPPATHPREFIX, m.getIncomingMailServerIMAPPathPrefix());
		}
		addElement(parentNode, INCOMING_MAIL_SERVER_AUTHENTICATION, m.getIncomingMailServerAuthentication());
		addElement(parentNode, INCOMING_MAIL_SERVER_HOST_NAME, m.getIncomingMailServerHostName());
		addElement(parentNode, INCOMING_MAIL_SERVER_PORT_NUMBER, m.getIncomingMailServerPortNumber());
		addElement(parentNode, INCOMING_MAIL_SERVER_USE_SSL, m.isIncomingMailServerUseSSL());
		addElement(parentNode, INCOMING_MAIL_SERVER_USERNAME, m.getIncomingMailServerUsername());
		if (m.getIncomingPassword() != null)
		{
			addElement(parentNode, INCOMING_PASSWORD, m.getIncomingPassword());
		}
		if (m.getOutgoingPassword() != null)
		{
			addElement(parentNode, OUTGOING_PASSWORD, m.getOutgoingPassword());
		}
		addElement(parentNode, OUTGOING_PASSWORD_SAME_AS_INCOMING_PASSWORD, m.isOutgoingPasswordSameAsIncomingPassword());
		addElement(parentNode, OUTGOING_MAIL_SERVER_AUTHENTICATION, m.getOutgoingMailServerAuthentication());
		addElement(parentNode, OUTGOING_MAIL_SERVER_HOST_NAME, m.getOutgoingMailServerHostName());
		addElement(parentNode, OUTGOING_MAIL_SERVER_PORT_NUMBER, m.getOutgoingMailServerPortNumber());
		addElement(parentNode, OUTGOING_MAIL_SERVER_USE_SSL, m.isOutgoingMailServerUseSSL());
		addElement(parentNode, OUTGOING_MAIL_SERVER_USERNAME, m.getOutgoingMailServerUsername());
		addElement(parentNode, PREVENT_MOVE, m.isPreventMove());
		addElement(parentNode, PREVENT_APP_SHEET, m.isPreventAppSheet());
		addElement(parentNode, SMIME_ENABLED, m.isSmimeEnabled());
		addElement(parentNode, "disableMailRecentsSyncing", m.isDisableMailRecentsSyncing());

		if (m.isSmimeEnabled())
		{
			addElement(parentNode, SMIME_SIGNING_CERTIFICATE_UUID, m.getSmimeSigningCertificateUUID());
			addElement(parentNode, SMIME_ENCRYPTION_CERTIFICATE_UUID, m.getSmimeEncryptionCertificateUUID());
		}
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		EmailProfileInfo m = (EmailProfileInfo) super.parse(dictElement);
		String desc = getValue(dictElement, "EmailAccountDescription", false);
		m.setAccountDescription(desc == null ? m.getAccountDescription() : desc);

		String name = getValue(dictElement, "EmailAccountName", false);
		m.setAccountName(name == null ? m.getAccountName() : name);

		String accountType = getValue(dictElement, "EmailAccountType", false);
		m.setAccountType(accountType == null ? m.getAccountType() : accountType);

		String emailAddress = getValue(dictElement, "EmailAddress", false);
		m.setAddress(emailAddress == null ? m.getAddress() : emailAddress);

		String incomingMailServerAuthentication = getValue(dictElement, "IncomingMailServerAuthentication", false);
		m.setIncomingMailServerAuthentication(incomingMailServerAuthentication == null ? m.getIncomingMailServerAuthentication()
				: incomingMailServerAuthentication);

		String incomingMailServerHostName = getValue(dictElement, "IncomingMailServerHostName", false);
		m.setIncomingMailServerHostName(incomingMailServerHostName == null ? m.getIncomingMailServerHostName() : incomingMailServerHostName);

		String incomingMailServerIMAPPathPrefix = getValue(dictElement, "IncomingMailServerIMAPPathPrefix", false);
		m.setIncomingMailServerIMAPPathPrefix(incomingMailServerIMAPPathPrefix == null ? m.getIncomingMailServerIMAPPathPrefix()
				: incomingMailServerIMAPPathPrefix);

		String incomingMailServerPortNumber = getValue(dictElement, "IncomingMailServerPortNumber", false);
		m.setIncomingMailServerPortNumber(incomingMailServerPortNumber == null ? m.getIncomingMailServerPortNumber() : Integer
				.valueOf(incomingMailServerPortNumber));

		String useSSL = getValue(dictElement, "IncomingMailServerUseSSL", true);
		m.setIncomingMailServerUseSSL(useSSL == null ? m.isIncomingMailServerUseSSL() : Boolean.valueOf(useSSL));

		String username = getValue(dictElement, "IncomingMailServerUsername", false);
		m.setIncomingMailServerUsername(username == null ? m.getIncomingMailServerUsername() : username);

		String password = getValue(dictElement, "IncomingPassword", false);
		m.setIncomingPassword(password == null ? m.getIncomingPassword() : password);

		String outgoingMailServerAuthentication = getValue(dictElement, "OutgoingMailServerAuthentication", false);
		m.setOutgoingMailServerAuthentication(outgoingMailServerAuthentication == null ? m.getOutgoingMailServerAuthentication()
				: outgoingMailServerAuthentication);

		String outgoingMailServerHostName = getValue(dictElement, "OutgoingMailServerHostName", false);
		m.setOutgoingMailServerHostName(outgoingMailServerHostName == null ? m.getOutgoingMailServerHostName() : outgoingMailServerHostName);

		String outgoingMailServerPortNumber = getValue(dictElement, "OutgoingMailServerPortNumber", false);
		m.setOutgoingMailServerPortNumber(outgoingMailServerPortNumber == null ? m.getOutgoingMailServerPortNumber() : Integer
				.valueOf(outgoingMailServerPortNumber));

		String outgoingMailServerUseSSL = getValue(dictElement, "OutgoingMailServerUseSSL", true);
		m.setOutgoingMailServerUseSSL(outgoingMailServerUseSSL == null ? m.isOutgoingMailServerUseSSL() : Boolean.valueOf(outgoingMailServerUseSSL));

		String outgoingMailServerUsername = getValue(dictElement, "OutgoingMailServerUsername", false);
		m.setOutgoingMailServerUsername(outgoingMailServerUsername == null ? m.getOutgoingMailServerUsername() : outgoingMailServerUsername);

		String outGoingpassword = getValue(dictElement, "OutgoingPassword", false);
		m.setOutgoingPassword(outGoingpassword == null ? m.getOutgoingPassword() : outGoingpassword);

		String outgoingPasswordSameAsIncomingPassword = getValue(dictElement, "OutgoingPasswordSameAsIncomingPassword", true);
		if (outgoingPasswordSameAsIncomingPassword != null && Boolean.valueOf(outgoingPasswordSameAsIncomingPassword))
		{
			m.setOutgoingPasswordSameAsIncomingPassword(true);
			m.setOutgoingPassword(null);
		} else
		{
			m.setOutgoingPasswordSameAsIncomingPassword(false);
		}

		String preventAppSheet = getValue(dictElement, "PreventAppSheet", true);
		m.setPreventAppSheet(preventAppSheet == null ? m.isPreventAppSheet() : Boolean.valueOf(preventAppSheet));

		String preventMove = getValue(dictElement, "PreventMove", true);
		m.setPreventMove(preventMove == null ? m.isPreventMove() : Boolean.valueOf(preventMove));

		String sMIMEEnabled = getValue(dictElement, "SMIMEEnabled", true);
		m.setSmimeEnabled(sMIMEEnabled == null ? m.isSmimeEnabled() : Boolean.valueOf(sMIMEEnabled));

		String encrypCertUUID = getValue(dictElement, "SMIMEEncryptionCertificateUUID", false);
		m.setSmimeEncryptionCertificateUUID(encrypCertUUID == null ? m.getSmimeEncryptionCertificateUUID() : encrypCertUUID);

		String signCertUUID = getValue(dictElement, "SMIMESigningCertificateUUID", false);
		m.setSmimeSigningCertificateUUID(signCertUUID == null ? m.getSmimeSigningCertificateUUID() : signCertUUID);
		return m;
	}
}
