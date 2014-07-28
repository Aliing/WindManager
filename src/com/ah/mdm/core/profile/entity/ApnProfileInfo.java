package com.ah.mdm.core.profile.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

//import org.springframework.util.AutoPopulatingList;

@Entity
@Table(name = "t_profile_apns")
public class ApnProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long				apnsId;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = ProfileApn.class)
	@JoinColumn(name = "apnsId")
	private List<ProfileApn>	apns	= new ArrayList<ProfileApn>();

	public ApnProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_APN);
	}

	public long getApnsId()
	{
		return apnsId;
	}

	public void setApnsId(long apnsId)
	{
		this.apnsId = apnsId;
	}

	public List<ProfileApn> getApns()
	{
		return apns;
	}

	public void setApns(List<ProfileApn> apns)
	{
		this.apns = apns;
	}

}
