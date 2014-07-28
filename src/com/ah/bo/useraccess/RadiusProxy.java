/**
 *@filename		RadiusProxy.java
 *@version
 *@author		Fiona
 *@createtime	2010-5-20 AM 11:11:02
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
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "RADIUS_PROXY")
@org.hibernate.annotations.Table(appliesTo = "RADIUS_PROXY", indexes = {
		@Index(name = "RADIUS_PROXY_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class RadiusProxy implements HmBo
{

	private static final long	serialVersionUID	= 1L;

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
	private String proxyName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	public static final short RADIUS_PROXY_FORMAT_NAI = 1;
	
	public static final short RADIUS_PROXY_FORMAT_NT = 2;
	
	public static EnumItem[] ENUM_RADIUS_PROXY_FORMAT = MgrUtil.enumItems(
		"enum.radius.proxy.format.", new int[] { RADIUS_PROXY_FORMAT_NAI,
			RADIUS_PROXY_FORMAT_NT });
	
	private short proxyFormat = RADIUS_PROXY_FORMAT_NAI;
	
	@Range(min=3, max=10)
	private short retryDelay = 5;
	
	@Range(min=1, max=10)
	private short retryCount = 3;
	
	@Range(min=30, max=3600)
	private int deadTime = 300;
	
	private boolean injectOperatorNmAttri;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "RADIUS_PROXY_REALM", joinColumns = @JoinColumn(name = "RADIUS_PROXY_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<RadiusProxyRealm> radiusRealm = new ArrayList<RadiusProxyRealm>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "RADIUS_PROXY_NAS", joinColumns = @JoinColumn(name = "RADIUS_PROXY_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<RadiusHiveapAuth> radiusNas = new ArrayList<RadiusHiveapAuth>();

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
		return proxyName;
	}

	public String getProxyName()
	{
		return proxyName;
	}

	public void setProxyName(String proxyName)
	{
		this.proxyName = proxyName;
	}

	public short getProxyFormat()
	{
		return proxyFormat;
	}

	public void setProxyFormat(short proxyFormat)
	{
		this.proxyFormat = proxyFormat;
	}

	public short getRetryDelay()
	{
		return retryDelay;
	}

	public void setRetryDelay(short retryDelay)
	{
		this.retryDelay = retryDelay;
	}

	public short getRetryCount()
	{
		return retryCount;
	}

	public void setRetryCount(short retryCount)
	{
		this.retryCount = retryCount;
	}

	public int getDeadTime()
	{
		return deadTime;
	}

	public void setDeadTime(int deadTime)
	{
		this.deadTime = deadTime;
	}

	public List<RadiusProxyRealm> getRadiusRealm()
	{
		return radiusRealm;
	}

	public void setRadiusRealm(List<RadiusProxyRealm> radiusRealm)
	{
		this.radiusRealm = radiusRealm;
	}

	public List<RadiusHiveapAuth> getRadiusNas()
	{
		return radiusNas;
	}

	public void setRadiusNas(List<RadiusHiveapAuth> radiusNas)
	{
		this.radiusNas = radiusNas;
	}
	
	@Transient
	public String getFormatStr() {
		return MgrUtil.getEnumString("enum.radius.proxy.format." + proxyFormat);
	}

	@Override
	public RadiusProxy clone() {
		try {
			return (RadiusProxy) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Transient
	private String nasSettingsStyle = "none";
	
	public String getNasSettingsStyle() {
		return nasSettingsStyle;
	}

	public void setNasSettingsStyle(String nasSettingsStyle) {
		this.nasSettingsStyle = nasSettingsStyle;
	}
	
	@Transient
	private String realmSettingsStyle = "none";

	public String getRealmSettingsStyle()
	{
		return realmSettingsStyle;
	}

	public void setRealmSettingsStyle(String realmSettingsStyle)
	{
		this.realmSettingsStyle = realmSettingsStyle;
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

	public boolean isInjectOperatorNmAttri() {
		return injectOperatorNmAttri;
	}

	public void setInjectOperatorNmAttri(boolean injectOperatorNmAttri) {
		this.injectOperatorNmAttri = injectOperatorNmAttri;
	}

    @Transient
	private boolean proxy4Router;
    public boolean isProxy4Router() {
        return proxy4Router;
    }
    public void setProxy4Router(boolean proxy4Router) {
        this.proxy4Router = proxy4Router;
    }

    @Transient
    public boolean isEnabledIDM() {
        if(null == this.radiusRealm) {
            return false;
        }
        for (RadiusProxyRealm realm : this.radiusRealm) {
            if(realm.isUseIDM()) {
                return true;
            }
        }
        return false;
    }
    @Transient
    private boolean enabledIDMSession;
    public boolean isEnabledIDMSession() {
        return enabledIDMSession;
    }
    public void setEnabledIDMSession(boolean enabledIDMSession) {
        this.enabledIDMSession = enabledIDMSession;
    }
    
}