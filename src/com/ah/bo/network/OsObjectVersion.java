package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.ah.bo.HmBo;

@Embeddable
public class OsObjectVersion implements Serializable, Cloneable {
	private static final long	serialVersionUID	= 1L;
	
	@Column(length = HmBo.DEFAULT_STRING_LENGTH)
	private String	osVersion;

	@Column(length = HmBo.DEFAULT_DESCRIPTION_LENGTH)
	private String	description;
	
	private String option55;
	
	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOption55() {
		return option55;
	}

	public void setOption55(String option55) {
		this.option55 = option55;
	}
}
