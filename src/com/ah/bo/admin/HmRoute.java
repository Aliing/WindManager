package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Version;

import com.ah.bo.HmBo;

public class HmRoute implements HmBo {

	private static final long serialVersionUID = 1L;

	private Long id;

	@Version
	private Timestamp version;

	private boolean selected;

	private String dest;

	private String mask;

	private String gateway;

	public HmRoute() {

	}

	public HmRoute(Long id, String dest, String mask, String gateway) {
		this.id = id;
		this.dest = dest;
		this.mask = mask;
		this.gateway = gateway;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		return dest + "-" + mask + "-" + gateway;
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	public void setOwner(String owner) {
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getDest() {
		return dest.trim();
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getGateway() {
		return gateway.trim();
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getMask() {
		return mask.trim();
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

}