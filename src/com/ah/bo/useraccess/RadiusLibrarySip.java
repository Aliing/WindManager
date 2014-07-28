/**
 *@filename		RadiusLibrarySip.java
 *@version
 *@author		Fiona
 *@createtime	2010-10-13 PM 03:15:43
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.useraccess;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "RADIUS_LIBRARY_SIP")
@org.hibernate.annotations.Table(appliesTo = "RADIUS_LIBRARY_SIP", indexes = {
		@Index(name = "RADIUS_LIBRARY_SIP_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class RadiusLibrarySip implements HmBo
{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String policyName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "DEFAULT_USER_GROUP_ID", nullable = false)
	private LocalUserGroup defUserGroup;
	
	private short defAction = RadiusLibrarySipRule.SIP_RULE_ACTION_PERMIT;
	
	private String defMessage;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "SIP_POLICY_RULE", joinColumns = @JoinColumn(name = "RADIUS_LIBRARY_SIP_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<RadiusLibrarySipRule> rules = new ArrayList<RadiusLibrarySipRule>();

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getLabel() {
		return policyName;
	}
	
	@Override
	public RadiusLibrarySip clone() {
		try {
			return (RadiusLibrarySip) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public LocalUserGroup getDefUserGroup()
	{
		return defUserGroup;
	}

	public void setDefUserGroup(LocalUserGroup defUserGroup)
	{
		this.defUserGroup = defUserGroup;
	}

	public List<RadiusLibrarySipRule> getRules()
	{
		return rules;
	}

	public void setRules(List<RadiusLibrarySipRule> rules)
	{
		this.rules = rules;
	}
	
	@Transient
	public String getValue() {
		return policyName;
	}

	public short getDefAction()
	{
		return defAction;
	}

	public void setDefAction(short defAction)
	{
		this.defAction = defAction;
	}

	public String getDefMessage()
	{
		return defMessage;
	}

	public void setDefMessage(String defMessage)
	{
		this.defMessage = defMessage;
	}
	
	@Transient
	private boolean parentIframeOpenFlg;
	@Transient
	private String parentDomID = "";
	@Transient
	private String contentShowType = "subdrawer";
	
	public String getContentShowType() {
		return contentShowType;
	}

	public void setContentShowType(String contentShowType) {
		this.contentShowType = contentShowType;
	}
	
	public boolean isParentIframeOpenFlg() {
		return parentIframeOpenFlg;
	}

	public void setParentIframeOpenFlg(boolean parentIframeOpenFlg) {
		this.parentIframeOpenFlg = parentIframeOpenFlg;
	}

	public String getParentDomID() {
		return parentDomID;
	}

	public void setParentDomID(String parentDomID) {
		this.parentDomID = parentDomID;
	}

}