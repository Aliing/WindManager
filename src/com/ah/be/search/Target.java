/**
 * @filename			Target.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import java.sql.Timestamp;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "TARGET")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TARGET_TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("T")
public class Target implements HmBo {

	private static final long	serialVersionUID	= 1L;

	@Id
	@GeneratedValue
	private Long id;

	private String action;
	
	private String feature;
	
	private int type;
	
	/*
	 * userName and userDomainId are used to differentiate the ownership
	 * of found items in database table. 
	 * 
	 */
	private String userName;
	
	private Long userDomainId;
	
	private Long boDomainId;
	
	private String urlParams;
	
	@Version
	private Timestamp version;

	public Target() {
		super();
	}

	public Target(String action, 
			String feature, 
			int type, 
			String urlParams,
			String userName,
			Long userDomainId,
			Long boDomainId) {
		this.action = action;
		this.feature = feature;
		this.type = type;
		this.urlParams = urlParams;
		this.userName = userName;
		this.userDomainId = userDomainId;
		this.boDomainId = boDomainId;
	}

	/**
	 * getter of action
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * setter of action
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * getter of feature
	 * @return the feature
	 */
	public String getFeature() {
		return feature;
	}

	/**
	 * setter of feature
	 * @param feature the feature to set
	 */
	public void setFeature(String feature) {
		this.feature = feature;
	}

	/**
	 * getter of type
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * setter of type
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * getter of urlParams
	 * @return the urlParams
	 */
	public String getUrlParams() {
		return urlParams;
	}

	/**
	 * setter of urlParams
	 * @param urlParams the urlParams to set
	 */
	public void setUrlParams(String urlParams) {
		this.urlParams = urlParams;
	}
	
	public String toString() {
		StringBuilder buffer = new StringBuilder("[Target]");
		buffer.append(" action: ").append(this.getAction()).append(";");
		buffer.append(" feature: ").append(this.getFeature()).append(";");
		buffer.append(" type: ").append(this.getType()).append(";");
		
		return buffer.toString();
	}
	
	@Override
	public int hashCode() {
        return new HashCodeBuilder().append(this.action).append(this.feature)
                .append(this.type).append(this.boDomainId)
                .append(this.userName).append(this.userDomainId).hashCode();
	}
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof Target) {
	        Target other = (Target) obj;
            return new EqualsBuilder().append(this.action, other.action)
                    .append(this.feature, other.feature)
                    .append(this.type, other.type)
                    .append(this.boDomainId, other.boDomainId)
                    .append(this.userName, other.userName)
                    .append(this.userDomainId, other.userDomainId).isEquals();
	    } else {
	        return false;
	    }
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Timestamp getVersion() {
		return this.version;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setSelected(boolean selected) {
		
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
		
	}
	
	/**
	 * getter of domainId
	 * @return the domainId
	 */
	public Long getUserDomainId() {
		return userDomainId;
	}

	/**
	 * setter of domainId
	 * @param domainId the domainId to set
	 */
	public void setUserDomainId(Long domainId) {
		this.userDomainId = domainId;
	}
	
	/**
	 * getter of boDomainId
	 * @return the boDomainId
	 */
	public Long getBoDomainId() {
		return boDomainId;
	}

	/**
	 * setter of boDomainId
	 * @param boDomainId the boDomainId to set
	 */
	public void setBoDomainId(Long boDomainId) {
		this.boDomainId = boDomainId;
	}

	/**
	 * getter of userName
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * setter of userName
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
		
}