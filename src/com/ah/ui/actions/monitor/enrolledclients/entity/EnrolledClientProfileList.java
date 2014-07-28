package com.ah.ui.actions.monitor.enrolledclients.entity;


import java.util.List;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("content")
public class EnrolledClientProfileList
{
	@XStreamAlias("ProfileList")
	private List<EnrolledClientProfileItem>	profileList;

	public List<EnrolledClientProfileItem> getProfileList() {
		return profileList;
	}

	public void setProfileList(List<EnrolledClientProfileItem> profileList) {
		this.profileList = profileList;
	}




}
