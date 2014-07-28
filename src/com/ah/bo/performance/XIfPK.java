package com.ah.bo.performance;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


@Embeddable
@SuppressWarnings("serial")
public class XIfPK implements Serializable
{

	@Column(length = 64, nullable = false)
	private String	apName;

	@Column(nullable = false)
	private long	statTimeStamp;

	@Column(nullable = false)
	private int		ifIndex;
//	
//	private String timeZone;

	public String getApName()
	{
		return apName;
	}

	public void setApName(String apName)
	{
		this.apName = apName;
	}

//	public String getStatTime()
//	{
//		return AhDateTimeUtil.getSpecifyDateTimeReport(statTimeStamp,TimeZone.getTimeZone(timeZone));
//
//	}

	public long getStatTimeValue()
	{
		return statTimeStamp;
	}

	public void setStatTime(long statTime)
	{
		this.statTimeStamp = statTime;
	}

	public int getIfIndex()
	{
		return ifIndex;
	}

	public void setIfIndex(int ifIndex)
	{
		this.ifIndex = ifIndex;
	}


	/**
	 * mark: let's compare object through equals() and hashCode()
	 */
//	 @Transient
//	 public String getKey()
//	 {
//	 return apName + "|" + statTime + "|" + ifIndex;
//	 }
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof XIfPK))
			return false;

		final XIfPK xifpk = (XIfPK) o;

		return new EqualsBuilder() 
	        .append(apName, xifpk.apName) 
	        .append(statTimeStamp, xifpk.statTimeStamp) 
	        .append(ifIndex, xifpk.ifIndex).isEquals(); 
	}
	
	@Override
	public int hashCode()
	{
		 return new HashCodeBuilder(17,37).append(this.apName).append(this.statTimeStamp)
         	.append(this.ifIndex).toHashCode();

	}
}
