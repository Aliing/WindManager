package com.ah.ui.actions.monitor.enrolledclients.entity;


import java.util.List;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("content")
public class RestrictionsListResponse
{
	@XStreamAlias("RestrictionsInfoList")
	private List<RestrictionsInfo>	restrictionList;

	public List<RestrictionsInfo> getRestrictionList()
	{
		return restrictionList;
	}

	public void setRestrictionList(List<RestrictionsInfo> restrictionList)
	{
		this.restrictionList = restrictionList;
	}
}
