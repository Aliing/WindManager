package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Range;

import com.ah.bo.useraccess.UserProfile;

@Embeddable
public class ConfigTemplateQos implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "USER_PROFILE_ID")
	private UserProfile userProfile;

	@Range(min = 0, max = 54000)
	private int policingRate = 54000;
	
	@Range(min = 0, max = 2000000)
	private int policingRate11n = 1000000;
	
	@Range(min = 0, max = 2000000)
	private int policingRate11ac = 1000000;
	
	@Range(min = 0, max = 1000)
	private int schedulingWeight = 10;

	private float weightPercent;

	private int radioMode;

	public int getRadioMode() {
		return radioMode;
	}

	public void setRadioMode(int radioMode) {
		this.radioMode = radioMode;
	}

	public int getPolicingRate() {
		return policingRate;
	}

	public void setPolicingRate(int policingRate) {
		this.policingRate = policingRate;
	}

	public int getSchedulingWeight() {
		return schedulingWeight;
	}

	public void setSchedulingWeight(int schedulingWeight) {
		this.schedulingWeight = schedulingWeight;
	}

	public float getWeightPercent() {
		return weightPercent;
	}

	public void setWeightPercent(float weightPercent) {
		this.weightPercent = weightPercent;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	@Transient
	public String getKey() {
		return userProfile.getId() + "|" + radioMode;
	}

	public int getPolicingRate11n() {
		return policingRate11n;
	}

	public void setPolicingRate11n(int policingRate11n) {
		this.policingRate11n = policingRate11n;
	}
	
	public int getPolicingRate11ac() {
		return policingRate11ac;
	}

	public void setPolicingRate11ac(int policingRate11ac) {
		this.policingRate11ac = policingRate11ac;
	}

}