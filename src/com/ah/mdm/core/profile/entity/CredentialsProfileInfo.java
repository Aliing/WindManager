package com.ah.mdm.core.profile.entity;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_credentials")
public class CredentialsProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	credentialsId;

	private String	password;
	
	private String passwordDisplay;

	private String	certificateFileName;
	@Column(length = 2048)
	private byte[]	certificateContent;
	private String	issuer;
	private Date	notBefore;
	private Date	notAfter;
	private boolean	usedBySelect;			// means this certificate can be
											// used by scep.jsp etc... or not;

	public CredentialsProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_CREDENTIALS_CRT_ENTITY);
	}

	public String getPasswordDisplay() {
		return passwordDisplay;
	}

	public void setPasswordDisplay(String passwordDisplay) {
		this.passwordDisplay = passwordDisplay;
	}

	public boolean isUsedBySelect()
	{
		return usedBySelect;
	}

	public void setUsedBySelect(boolean usedBySelect)
	{
		this.usedBySelect = usedBySelect;
	}

	public String getIssuer()
	{
		return issuer;
	}

	public void setIssuer(String issuer)
	{
		this.issuer = issuer;
	}

	public Date getNotBefore()
	{
		if (notBefore == null)
		{
			return null;
		}
		Calendar date = Calendar.getInstance();
		date.setTime(notBefore);
		return date.getTime();
	}

	public void setNotBefore(Date notBeforeParam)
	{
		if (null != notBeforeParam)
		{
			Calendar date = Calendar.getInstance();
			date.setTime(notBeforeParam);
			this.notBefore = date.getTime();
		}
	}

	public Date getNotAfter()
	{
		if (notAfter == null)
		{
			return null;
		}
		Calendar date = Calendar.getInstance();
		date.setTime(notAfter);
		return date.getTime();
	}

	public void setNotAfter(Date notAfterParam)
	{
		if (null != notAfterParam)
		{
			Calendar date = Calendar.getInstance();
			date.setTime(notAfterParam);
			this.notAfter = date.getTime();
		}
	}

	public String getCertificateFileName()
	{
		return certificateFileName;
	}

	public void setCertificateFileName(String certificateFileName)
	{
		this.certificateFileName = certificateFileName;
	}

	public byte[] getCertificateContent()
	{
		return certificateContent;
	}

	public void setCertificateContent(byte[] certificateContent)
	{
		this.certificateContent = certificateContent;
	}

	public long getCredentialsId()
	{
		return credentialsId;
	}

	public void setCredentialsId(long credentialsId)
	{
		this.credentialsId = credentialsId;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

}