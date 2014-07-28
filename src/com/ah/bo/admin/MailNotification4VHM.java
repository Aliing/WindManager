package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.bo.HmBo;

@Entity
@Table(name = "MAIL_NOTIFICATION_VHM")
public class MailNotification4VHM implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long	id;

	@Column(length = 512)
	private String	mailTo;

	public String getMailTo()
	{
		return mailTo;
	}

	public void setMailTo(String mailTo)
	{
		this.mailTo = mailTo;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	@Override
	public String getLabel()
	{
		return "mail_notification_" + owner.getDomainName();
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain	owner;

	@Override
	public HmDomain getOwner()
	{
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner)
	{
		this.owner = owner;
	}

	@Version
	private Timestamp	version;

	@Override
	public Timestamp getVersion()
	{
		return version;
	}

	@Transient
	private boolean	selected;

	@Override
	public boolean isSelected()
	{
		return selected;
	}

	@Override
	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}

	@Transient
	private String[] toMailArray;

	private String[] getToMailArray()
	{
		if (toMailArray == null)
		{
			if (mailTo.length() > 0 && mailTo.charAt(0) == ';')
			{
				mailTo = mailTo.substring(1);
			}

			toMailArray = mailTo.split(";");
		}

		return toMailArray;
	}

	public String getToEmail1()
	{
		String[] mailArray = getToMailArray();
		return mailArray.length > 0 ? mailArray[0] : "";
	}

	public String getToEmail2()
	{
		String[] mailArray = getToMailArray();
		return mailArray.length > 1 ? mailArray[1] : "";
	}

	public String getToEmail3()
	{
		String[] mailArray = getToMailArray();
		return mailArray.length > 2 ? mailArray[2] : "";
	}

	public String getToEmail4()
	{
		String[] mailArray = getToMailArray();
		return mailArray.length > 3 ? mailArray[3] : "";
	}

	public String getToEmail5()
	{
		String[] mailArray = getToMailArray();
		return mailArray.length > 4 ? mailArray[4] : "";
	}

	@Override
	public void setId(Long id)
	{
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

}