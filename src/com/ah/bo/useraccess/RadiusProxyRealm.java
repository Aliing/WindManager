/**
 *@filename		RadiusProxyRealm.java
 *@version
 *@author		Fiona
 *@createtime	2010-5-20 AM 11:19:35
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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.HmBo;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Embeddable
public class RadiusProxyRealm implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	
	public final static String DEFAULT_REALM_NAME = "Default";
	
	public final static String NULL_REALM_NAME = "Null";

	//actually it is the realm name
	@Column(length = HmBo.DEFAULT_STRING_LENGTH, nullable = false)
	private String serverName;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RADIUS_SERVER_ID", nullable = true)
	private RadiusAssignment radiusServer;
	
	private boolean strip = true;
	
	// for IDM
	private boolean useIDM;
	private int tlsPort;
	
	@Transient
	private boolean idmAuthProxy;

	public String getServerName()
	{
		return serverName;
	}

	public void setServerName(String serverName)
	{
		this.serverName = serverName;
	}
	
	public RadiusAssignment getRadiusServer()
	{
		return radiusServer;
	}

	public void setRadiusServer(RadiusAssignment radiusServer)
	{
		this.radiusServer = radiusServer;
	}

	public boolean isStrip()
	{
		return strip;
	}

	public void setStrip(boolean strip)
	{
		this.strip = strip;
	}

    public boolean isUseIDM() {
        return useIDM;
    }

    public int getTlsPort() {
        return tlsPort;
    }

    public void setUseIDM(boolean useIDM) {
        this.useIDM = useIDM;
    }

    public void setTlsPort(int tlsPort) {
        this.tlsPort = tlsPort;
    }
    
    @Transient
    public boolean isProxy4RadSec() {
        return this.useIDM;
    }
    
    @Transient
    public boolean isProxy4RadSecAuth() {
        return this.useIDM && this.radiusServer != null;
    }
    
    @Transient
    public boolean isIdmAuthProxy() {
		return idmAuthProxy;
	}

    @Transient
	public void setIdmAuthProxy(boolean idmAuthProxy) {
		this.idmAuthProxy = idmAuthProxy;
	}
}
