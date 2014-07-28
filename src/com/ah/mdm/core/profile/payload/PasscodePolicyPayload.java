package com.ah.mdm.core.profile.payload;

import org.dom4j.Element;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.PasscodeProfileInfo;


public class PasscodePolicyPayload extends ProfilePayload
{

	public PasscodePolicyPayload(AbstractProfileInfo model)
	{
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		super.fillPayloadContent(parentNode);
		PasscodeProfileInfo m = (PasscodeProfileInfo) model;
		addElement(parentNode, ALLOW_SIMPLE, m.isAllowSimple());
		addElement(parentNode, FORCE_PIN, m.isForcePIN());
		if (m.getMaxFailedAttempts() != -1)
		{
			addElement(parentNode, MAX_FAILED_ATTEMPTS, m.getMaxFailedAttempts());
		}
		if (m.getMaxInactivity() != -1)
		{
			addElement(parentNode, MAX_INACTIVITY, m.getMaxInactivity());
		}
		if (m.getMaxPINAgeInDays() != 0)
		{
			addElement(parentNode, MAX_PIN_AGE_IN_DAYS, m.getMaxPINAgeInDays());
		}
		if (m.getMinComplexChars() != -1)
		{
			addElement(parentNode, MIN_COMPLEX_CHARS, m.getMinComplexChars());
		}
		if (m.getMinLength() != -1)
		{
			addElement(parentNode, MIN_LENGTH, m.getMinLength());
		}
		addElement(parentNode, REQUIRE_ALPHANUMERIC, m.isRequireAlphanumeric());
		if (m.getPinHistory() != 0)
		{
			addElement(parentNode, PIN_HISTORY, m.getPinHistory());
		}
		if (m.getMaxGracePeriod() != -1)
		{
			addElement(parentNode, MAX_GRACE_PERIOD, m.getMaxGracePeriod());
		}
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		PasscodeProfileInfo m = (PasscodeProfileInfo) super.parse(dictElement);
		String allowSimple = getValue(dictElement, "allowSimple", true);
		m.setAllowSimple(allowSimple == null ? m.isAllowSimple() : Boolean.valueOf(allowSimple));

		String forcePIN = getValue(dictElement, "forcePIN", true);
		m.setForcePIN(forcePIN == null ? m.isForcePIN() : Boolean.valueOf(forcePIN));

		String maxFailedAttempts = getValue(dictElement, "maxFailedAttempts", false);
		m.setMaxFailedAttempts(maxFailedAttempts == null ? m.getMaxFailedAttempts() : Integer.valueOf(maxFailedAttempts));

		String maxGracePeriod = getValue(dictElement, "maxGracePeriod", false);
		m.setMaxGracePeriod(maxGracePeriod == null ? m.getMaxGracePeriod() : Integer.valueOf(maxGracePeriod));

		String maxInactivity = getValue(dictElement, "maxInactivity", false);
		m.setMaxInactivity(maxInactivity == null ? m.getMaxInactivity() : Integer.valueOf(maxInactivity));

		String maxPINAgeInDays = getValue(dictElement, "maxPINAgeInDays", false);
		m.setMaxPINAgeInDays(maxPINAgeInDays == null ? m.getMaxPINAgeInDays() : Integer.valueOf(maxPINAgeInDays));

		String minComplexChars = getValue(dictElement, "minComplexChars", false);
		m.setMinComplexChars(minComplexChars == null ? m.getMinComplexChars() : Integer.valueOf(minComplexChars));

		String minLength = getValue(dictElement, "minLength", false);
		m.setMinLength(minLength == null ? m.getMinLength() : Integer.valueOf(minLength));

		String pinHistory = getValue(dictElement, "pinHistory", false);
		m.setPinHistory(pinHistory == null ? m.getPinHistory() : Integer.valueOf(pinHistory));

		String requireAlphanumeric = getValue(dictElement, "requireAlphanumeric", true);
		m.setRequireAlphanumeric(requireAlphanumeric == null ? m.isRequireAlphanumeric() : Boolean.valueOf(requireAlphanumeric));
		return m;
	}
}
