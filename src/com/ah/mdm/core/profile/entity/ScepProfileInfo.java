package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_scep")
public class ScepProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	scepId;

	private String	url;
	private String	name;
	// Subject The representation of a X.500 name represented as an array of OID
	// and value.
	private String	subject;						// For Example:
													// O=Company,CN=Foo
	// None,rfc822Name,dNSName,uniformResourceIdentifier
	private String	subjectAltNameType	= "None";
	/*
	 * For URI Name Type ,the key is ntPrincipalName and
	 * uniformResourceIdentifier For RFC 822 Name Type , the key is
	 * ntPrincipalName and rfc822Name For DNS Name Type ,the key is
	 * ntPrincipalName and dNSName
	 */
	private String	altNameValue;
	// private String uniformResourceIdentifier; //use for URI Name Type
	// private String rfc822Name; //use for RFC 822 name
	// private String dNSName; //use for DNS Name Type

	private String	ntPrincipalName;
	private String	challenge;
	private int		retries;
	private int		retryDelay;
	private int		keySize				= 1024;
	private String	fingerprint;

	private String	keyType				= "RSA";
	/*
	 * 0 : useAsDigitalSign and useForKeyEnciperment are all false; 1 :
	 * useAsDigitalSign is true 4 : useForKeyEnciperment is true 5 :
	 * useAsDigitalSign and useForKeyEnciperment are all true
	 */
	private boolean	useAsDigitalSign;
	private boolean	useForKeyEnciperment;

	public long getScepId()
	{
		return scepId;
	}

	public void setScepId(long scepId)
	{
		this.scepId = scepId;
	}

	public ScepProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_SCEP);
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getSubjectAltNameType()
	{
		return subjectAltNameType;
	}

	public void setSubjectAltNameType(String subjectAltNameType)
	{
		this.subjectAltNameType = subjectAltNameType;
	}

	public String getAltNameValue()
	{
		return altNameValue;
	}

	public void setAltNameValue(String altNameValue)
	{
		this.altNameValue = altNameValue;
	}

	public String getNtPrincipalName()
	{
		return ntPrincipalName;
	}

	public void setNtPrincipalName(String ntPrincipalName)
	{
		this.ntPrincipalName = ntPrincipalName;
	}

	public String getChallenge()
	{
		return challenge;
	}

	public void setChallenge(String challenge)
	{
		this.challenge = challenge;
	}

	public int getRetries()
	{
		return retries;
	}

	public void setRetries(int retries)
	{
		this.retries = retries;
	}

	public int getRetryDelay()
	{
		return retryDelay;
	}

	public void setRetryDelay(int retryDelay)
	{
		this.retryDelay = retryDelay;
	}

	public int getKeySize()
	{
		return keySize;
	}

	public void setKeySize(int keySize)
	{
		this.keySize = keySize;
	}

	public String getFingerprint()
	{
		return fingerprint;
	}

	public void setFingerprint(String fingerprint)
	{
		this.fingerprint = fingerprint;
	}

	public String getKeyType()
	{
		return keyType;
	}

	public void setKeyType(String keyType)
	{
		this.keyType = keyType;
	}

	public boolean isUseAsDigitalSign()
	{
		return useAsDigitalSign;
	}

	public void setUseAsDigitalSign(boolean useAsDigitalSign)
	{
		this.useAsDigitalSign = useAsDigitalSign;
	}

	public boolean isUseForKeyEnciperment()
	{
		return useForKeyEnciperment;
	}

	public void setUseForKeyEnciperment(boolean useForKeyEnciperment)
	{
		this.useForKeyEnciperment = useForKeyEnciperment;
	}

	public int getKeyUsage()
	{
		if (useAsDigitalSign)
		{
			if (useForKeyEnciperment)
			{
				return 5;
			} else
			{
				return 1;
			}
		} else
		{
			if (useForKeyEnciperment)
			{
				return 4;
			} else
			{
				return 0;
			}
		}
	}

	public void setKeyUsage(int keyUsage)
	{
		switch (keyUsage)
		{
		case 0:
			useAsDigitalSign = false;
			useForKeyEnciperment = false;
			break;
		case 1:
			useAsDigitalSign = true;
			useForKeyEnciperment = false;
			break;
		case 4:
			useAsDigitalSign = false;
			useForKeyEnciperment = true;
			break;
		case 5:
			useAsDigitalSign = true;
			useForKeyEnciperment = true;
			break;
		default:
			useAsDigitalSign = false;
			useForKeyEnciperment = false;
		}
	}
}
