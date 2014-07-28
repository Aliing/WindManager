/**
 *@filename		ActiveDirectoryDomain.java
 *@version
 *@author		Fiona
 *@createtime	2008-8-11 PM 05:03:28
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.useraccess;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Embeddable
public class ActiveDirectoryDomain implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	@Column(length = 64)
	private String domain = "";
	
	@Column(length = 64)
	private String server = "";

	@Column(length = 64)
	private String fullName = "";
	
	// remove this field from 3.4r3
	//@Column(length = 256)
	//private String basedN = "";
	
	@Column(length = 256)
	private String bindDnName = "";
	
	@Column(length = 64)
	private String bindDnPass = "";
	
	private boolean defaultFlag = false;
	
	//--> for AD tree browser
	@Transient
	private Long serverId;
	@Transient
	private int domainId;
	@Transient
	private String basedN = "";
	@Transient
	private String optGroupLabel = "";
	//<-- for AD tree browser
	
	public String getDomain()
	{
		return domain;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public String getBindDnName()
	{
		return bindDnName;
	}

	public void setBindDnName(String bindDnName)
	{
		this.bindDnName = bindDnName;
	}

	public String getBindDnPass()
	{
		return bindDnPass;
	}

	public void setBindDnPass(String bindDnPass)
	{
		this.bindDnPass = bindDnPass;
	}

	public boolean isDefaultFlag()
	{
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag)
	{
		this.defaultFlag = defaultFlag;
	}
	
	@Transient
	public String getStringPassword()
	{
		StringBuffer strBuf = new StringBuffer();
		for(int i = 0; i < bindDnPass.length(); i ++) {
			strBuf.append("*");
		}
		return strBuf.toString();
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		this.server = server;
	}

	public Long getServerId() {
		return serverId;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	public int getDomainId() {
		return domainId;
	}

	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}

	public String getBasedN() {
		return basedN;
	}

	public void setBasedN(String basedN) {
		this.basedN = basedN;
	}

	public String getOptGroupLabel() {
		return optGroupLabel;
	}

	public void setOptGroupLabel(String optGroupLabel) {
		this.optGroupLabel = optGroupLabel;
	}

	public String getBindDnWithFullName()
	{
		if (bindDnName != null && bindDnName.indexOf("@") == -1) {
			bindDnName += "@";
			bindDnName += fullName;
		}
		return bindDnName;
	}

}
