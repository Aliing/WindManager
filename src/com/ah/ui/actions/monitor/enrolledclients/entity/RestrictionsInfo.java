package com.ah.ui.actions.monitor.enrolledclients.entity;


import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@Embeddable
@XStreamAlias("RestrictionsInfo")
public class RestrictionsInfo
{	
	@Transient
	@XStreamAlias("Name")
	public String name;
	@Transient
	@XStreamAlias("Value")
	public String	value;

	public RestrictionsInfo()
	{
		// TODO Auto-generated constructor stub
	}

	public RestrictionsInfo(String name, String value)
	{
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
