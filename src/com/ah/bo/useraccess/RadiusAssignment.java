/**
 *@filename		RadiusAssignment.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-27 PM 05:07:06
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
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "RADIUS_SERVICE_ASSIGN")
@org.hibernate.annotations.Table(appliesTo = "RADIUS_SERVICE_ASSIGN", indexes = {
		@Index(name = "RADIUS_SERVICE_ASSIGN_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class RadiusAssignment implements HmBo {

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
	private String radiusName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	@Range(min = 60)
	private int retryInterval = 600;

	@Range(min = 10)
	private int updateInterval = 20;
	
	private boolean enableExtensionRadius;
	
	private boolean injectOperatorNmAttri;
	
	public static final short RADIUS_MACAUTHDELIMITER_COLON = 1;

	public static final short RADIUS_MACAUTHDELIMITER_DASH = 2;
	
	public static final short RADIUS_MACAUTHDELIMITER_DOT = 3;

	public static EnumItem[] ENUM_RADIUS_MACAUTH_DELIMITER = MgrUtil.enumItems(
			"enum.macAuthDelimiter.", new int[] { RADIUS_MACAUTHDELIMITER_COLON,
				RADIUS_MACAUTHDELIMITER_DASH, RADIUS_MACAUTHDELIMITER_DOT });
	
	public static final short RADIUS_MACAUTHSTYLE_NO = 1;

	public static final short RADIUS_MACAUTHSTYLE_TWO = 2;
	
	public static final short RADIUS_MACAUTHSTYLE_FIVE = 3;

	public static EnumItem[] ENUM_RADIUS_MACAUTH_STYLE = MgrUtil.enumItems(
			"enum.macAuthStyle.", new int[] { RADIUS_MACAUTHSTYLE_NO,
				RADIUS_MACAUTHSTYLE_TWO, RADIUS_MACAUTHSTYLE_FIVE });
	
	public static final short RADIUS_MACAUTHCASE_LOWER = 1;

	public static final short RADIUS_MACAUTHCASE_UPPER = 2;
	
	public static EnumItem[] ENUM_RADIUS_MACAUTH_CASE = MgrUtil.enumItems(
		"enum.macAuthCase.", new int[] { RADIUS_MACAUTHCASE_LOWER,
			RADIUS_MACAUTHCASE_UPPER });

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "RADIUS_SERVICE", joinColumns = @JoinColumn(name = "ASSIGNMENT_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<RadiusServer> services = new ArrayList<RadiusServer>();
	
	private boolean enableDHCP4RadiusServer;

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
	
	@Transient
	private String optionalStyle = "none";
	
	@Transient
	private String advanceStyle = "none";

	@Override
	public String getLabel() {
		return radiusName;
	}

	public String getRadiusName() {
		return radiusName;
	}

	public String getRadiusNameSubstr() {
		if (radiusName==null) {
			return "";
		}
		if (radiusName.length()> BaseAction.DISPLAY_LENGTH_IN_GUI_OK) {
			return radiusName.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI-1) + "...";
		}
		
		return radiusName;
	}
	
	public void setRadiusName(String radiusName) {
		this.radiusName = radiusName;
	}

	public int getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

	public int getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}

	public List<RadiusServer> getServices() {
		return services;
	}

	public void setServices(List<RadiusServer> services) {
		this.services = services;
	}

	public boolean getEnableExtensionRadius() {
		return enableExtensionRadius;
	}

	public void setEnableExtensionRadius(boolean enableExtensionRadius) {
		this.enableExtensionRadius = enableExtensionRadius;
	}

	public String getOptionalStyle()
	{
		return optionalStyle;
	}

	public void setOptionalStyle(String optionalStyle)
	{
		this.optionalStyle = optionalStyle;
	}

	public String getAdvanceStyle()
	{
		return advanceStyle;
	}

	public void setAdvanceStyle(String advanceStyle)
	{
		this.advanceStyle = advanceStyle;
	}
	
	@Override
	public RadiusAssignment clone() {
		try {
			return (RadiusAssignment) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
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

    public boolean isEnableDHCP4RadiusServer() {
        return enableDHCP4RadiusServer;
    }

    public void setEnableDHCP4RadiusServer(boolean enableDHCP4RadiusServer) {
        this.enableDHCP4RadiusServer = enableDHCP4RadiusServer;
    }

}