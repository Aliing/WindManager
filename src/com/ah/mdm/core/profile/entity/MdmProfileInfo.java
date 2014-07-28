package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_mdm")
public class MdmProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	mdmId;
	private int		accessRights;
	private String	checkInURL;
	private boolean	checkOutWhenRemoved;
	private String	identityCertificateUUID;
	private String	serverURL;
	private boolean	signMessage;
	private String	topic;
	private boolean	useDevelopmentAPNS;

	public MdmProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_MDM);
	}

	public int getAccessRights()
	{
		return accessRights;
	}

	public void setAccessRights(int accessRights)
	{
		this.accessRights = accessRights;
	}

	public String getCheckInURL()
	{
		return checkInURL;
	}

	public void setCheckInURL(String checkInURL)
	{
		this.checkInURL = checkInURL;
	}

	public boolean isCheckOutWhenRemoved()
	{
		return checkOutWhenRemoved;
	}

	public void setCheckOutWhenRemoved(boolean checkOutWhenRemoved)
	{
		this.checkOutWhenRemoved = checkOutWhenRemoved;
	}

	public String getIdentityCertificateUUID()
	{
		return identityCertificateUUID;
	}

	public void setIdentityCertificateUUID(String identityCertificateUUID)
	{
		this.identityCertificateUUID = identityCertificateUUID;
	}

	public String getServerURL()
	{
		return serverURL;
	}

	public void setServerURL(String serverURL)
	{
		this.serverURL = serverURL;
	}

	public boolean isSignMessage()
	{
		return signMessage;
	}

	public void setSignMessage(boolean signMessage)
	{
		this.signMessage = signMessage;
	}

	public String getTopic()
	{
		return topic;
	}

	public void setTopic(String topic)
	{
		this.topic = topic;
	}

	public boolean isUseDevelopmentAPNS()
	{
		return useDevelopmentAPNS;
	}

	public void setUseDevelopmentAPNS(boolean useDevelopmentAPNS)
	{
		this.useDevelopmentAPNS = useDevelopmentAPNS;
	}

	public long getMdmId()
	{
		return mdmId;
	}

	public void setMdmId(long mdmId)
	{
		this.mdmId = mdmId;
	}

}
