package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.bo.HmBo;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.wlan.Cwp;
import com.ah.util.EnumConstUtil;

@Entity
@Table(name = "HM_LOGIN_AUTHENTICATION")
public class HmLoginAuthentication implements HmBo
{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private short hmAdminAuth = EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL;

	private int authType = Cwp.AUTH_METHOD_PAP;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RADIUS_SERVICE_ASSIGN_ID")
	private RadiusAssignment radiusAssignment;

	public short getHmAdminAuth()
	{
		return hmAdminAuth;
	}

	public void setHmAdminAuth(short hmAdminAuth)
	{
		this.hmAdminAuth = hmAdminAuth;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Version
	private Timestamp version;

	@Override
	public Timestamp getVersion() {
		return version;
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
	public String getLabel()
	{
		return "HmLoginAuthentication";
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public RadiusAssignment getRadiusAssignment()
	{
		return radiusAssignment;
	}

	public void setRadiusAssignment(RadiusAssignment radiusAssignment)
	{
		this.radiusAssignment = radiusAssignment;
	}

	@Override
	public void setId(Long id)
	{
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public int getAuthType() {
		return authType;
	}

	public void setAuthType(int authType) {
		this.authType = authType;
	}

}