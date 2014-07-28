package com.ah.be.ls.stat;

import com.ah.bo.HmBo;

public class StatConfig implements Cloneable {

	private int featureId;
	private String featureName;
	private String boClassName;
	private Class<? extends HmBo> boClass;
	private String searchRule;
	private String searchType;

	public int getFeatureId() {
		return featureId;
	}

	public void setFeatureId(int featureId) {
		this.featureId = featureId;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getBoClassName() {
		return boClassName;
	}

	@SuppressWarnings("unchecked")
	public void setBoClassName(String boClassName)
			throws ClassNotFoundException {
		this.boClassName = boClassName;
		this.boClass = (Class<? extends HmBo>) Class.forName(this.boClassName);
	}

	public Class<? extends HmBo> getBoClass() {
		return boClass;
	}

	public String getSearchRule() {
		return searchRule;
	}

	public void setSearchRule(String searchRule) {
		this.searchRule = searchRule;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	@Override
	protected StatConfig clone() throws CloneNotSupportedException {
		return (StatConfig)super.clone();
	}
}