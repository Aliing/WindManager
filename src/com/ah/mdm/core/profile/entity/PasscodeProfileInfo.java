package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_passcode")
public class PasscodeProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	passcodeId;

	//private boolean	allowSimple			= true;
	private boolean	allowSimple			;
	private boolean	forcePIN			= false;
	private int		maxFailedAttempts	= 11;
	private int		maxInactivity;
	private int		maxPINAgeInDays;
	private int		minComplexChars		= 0;
	private int		minLength			= 0;
	//private boolean	requireAlphanumeric	= false;
	private boolean	requireAlphanumeric;
	private int		pinHistory;
	private int		maxGracePeriod		= 0;

	public PasscodeProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_PASSWORD_POLICY);
	}

	public boolean isAllowSimple()
	{
		return allowSimple;
	}

	public void setAllowSimple(boolean allowSimple)
	{
		this.allowSimple = allowSimple;
	}

	public boolean isForcePIN()
	{
		return forcePIN;
	}

	public void setForcePIN(boolean forcePIN)
	{
		this.forcePIN = forcePIN;
	}

	public int getMaxFailedAttempts()
	{
		return maxFailedAttempts;
	}

	public void setMaxFailedAttempts(int maxFailedAttempts)
	{
		this.maxFailedAttempts = maxFailedAttempts;
	}

	public int getMaxInactivity()
	{
		return maxInactivity;
	}

	public void setMaxInactivity(int maxInactivity)
	{
		this.maxInactivity = maxInactivity;
	}

	public int getMaxPINAgeInDays()
	{
		return maxPINAgeInDays;
	}

	public void setMaxPINAgeInDays(int maxPINAgeInDays)
	{
		this.maxPINAgeInDays = maxPINAgeInDays;
	}

	public int getMinComplexChars()
	{
		return minComplexChars;
	}

	public void setMinComplexChars(int minComplexChars)
	{
		this.minComplexChars = minComplexChars;
	}

	public int getMinLength()
	{
		return minLength;
	}

	public void setMinLength(int minLength)
	{
		this.minLength = minLength;
	}

	public boolean isRequireAlphanumeric()
	{
		return requireAlphanumeric;
	}

	public void setRequireAlphanumeric(boolean requireAlphanumeric)
	{
		this.requireAlphanumeric = requireAlphanumeric;
	}

	public int getPinHistory()
	{
		return pinHistory;
	}

	public void setPinHistory(int pinHistory)
	{
		this.pinHistory = pinHistory;
	}

	public int getMaxGracePeriod()
	{
		return maxGracePeriod;
	}

	public void setMaxGracePeriod(int maxGracePeriod)
	{
		this.maxGracePeriod = maxGracePeriod;
	}

	public long getPasscodeId()
	{
		return passcodeId;
	}

	public void setPasscodeId(long passcodeId)
	{
		this.passcodeId = passcodeId;
	}
}
